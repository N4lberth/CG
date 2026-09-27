package cg.tp1.ui;

import cg.tp1.algorithms.clipping.ClipAlgorithm;
import cg.tp1.algorithms.clipping.ShapeClipper;
import cg.tp1.algorithms.transformation.Matrix3;
import cg.tp1.model.DrawingModel;
import cg.tp1.model.FillOperation;
import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Polygon;
import cg.tp1.model.Point;
import cg.tp1.model.Rectangle;
import cg.tp1.model.Shape;
import cg.tp1.raster.Pixel;
import cg.tp1.raster.PixelBuffer;
import cg.tp1.render.SceneRenderer;
import cg.tp1.ui.tools.Tool;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Coordena o modelo, as matrizes de pixels e a ferramenta ativa.
 *
 * Fluxo de um clique:
 * mouse (pixels da tela) → DrawingCanvas.toCartesian → Tool ativa
 * → (se criou objeto) DrawingModel → SceneRenderer → PixelBuffer → tela.
 *
 * Existem duas matrizes:
 *  - scene:   os objetos definitivos (é nela que os algoritmos atuam);
 *  - overlay: figuras temporárias (pré-visualização e destaque da seleção),
 *    com fundo transparente. Fica "por cima" da cena só na exibição e nunca
 *    altera a cena.
 */
public class DrawingEditor {

    private static final int SELECTION_COLOR = 0xFFFF6D00;
    private static final int VERTEX_MARKER_COLOR = 0xFFBF360C;
    private static final int CLIP_WINDOW_COLOR = 0xFF2E7D32;

    private final DrawingModel model = new DrawingModel();
    private final SceneRenderer renderer = new SceneRenderer();
    private final PixelBuffer scene;
    private final PixelBuffer overlay;
    private final DrawingCanvas canvas;

    private final ReadOnlyStringWrapper coordinatesText = new ReadOnlyStringWrapper("");
    private final ReadOnlyStringWrapper hintText = new ReadOnlyStringWrapper("");
    private final List<Runnable> changeListeners = new ArrayList<>();

    private Tool activeTool;
    private int currentColor = 0xFF000000;
    private LineAlgorithm currentLineAlgorithm = LineAlgorithm.DDA;

    public DrawingEditor(PixelBuffer scene, PixelBuffer overlay, DrawingCanvas canvas) {
        this.scene = scene;
        this.overlay = overlay;
        this.canvas = canvas;
        registerMouseHandlers();
    }

    // ---------- operações usadas pelas ferramentas e pela interface ----------

    public void addShape(Shape shape) {
        model.add(shape);
        redraw();
    }

    public void clear() {
        if (activeTool != null) {
            activeTool.reset();
        }
        model.clear();
        redraw();
    }

    /** Recalcula a matriz inteira a partir do modelo e avisa os painéis. */
    public void redraw() {
        renderer.render(model.shapes(), model.fills(), scene);
        refreshOverlay();
        changeListeners.forEach(Runnable::run);
    }

    /** Redesenha o destaque da seleção e as figuras temporárias da ferramenta. */
    private void refreshOverlay() {
        overlay.clear();
        Rectangle window = model.clipWindow();
        if (window != null) {
            renderer.draw(new Polygon(window.corners(), CLIP_WINDOW_COLOR, LineAlgorithm.BRESENHAM), overlay);
        }
        for (Shape shape : model.selection()) {
            renderer.draw(shape, overlay, SELECTION_COLOR);
            shape.vertices().forEach(this::drawVertexMarker);
        }
        if (activeTool != null) {
            activeTool.preview().forEach(shape -> renderer.draw(shape, overlay));
            hintText.set(activeTool.hint());
        }
        canvas.render();
    }

    /** Pequeno quadrado 3×3 marcando um vértice selecionado (apenas visual). */
    private void drawVertexMarker(Point vertex) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                overlay.set(vertex.roundedX() + dx, vertex.roundedY() + dy, VERTEX_MARKER_COLOR);
            }
        }
    }

    // ---------- seleção ----------

    public void selectInside(Rectangle region) {
        model.selectInside(region);
        redraw();
    }

    public void selectAll() {
        model.selectAll();
        redraw();
    }

    public void clearSelection() {
        model.clearSelection();
        redraw();
    }

    // ---------- transformações ----------

    /**
     * Aplica a matriz a todos os objetos selecionados e redesenha a cena.
     * Como o modelo guarda coordenadas reais, nada se perde por arredondamento.
     */
    public void transformSelection(Matrix3 matrix) {
        for (Shape shape : model.selection()) {
            shape.transform(matrix::apply);
        }
        // O preenchimento acompanha o objeto: a semente sofre a mesma transformação
        for (FillOperation fill : model.fills()) {
            if (fill.owner() != null && model.isSelected(fill.owner())) {
                fill.moveSeed(matrix.apply(fill.seed()));
            }
        }
        redraw();
    }

    // ---------- preenchimento ----------

    /**
     * Preenche a partir da semente clicada. O preenchimento é guardado no
     * modelo (vinculado ao objeto fechado que contém a semente, se houver)
     * para ser refeito nos próximos redesenhos.
     *
     * @return quantidade de pixels pintados
     */
    public int fill(Point seed, FillSettings settings) {
        FillOperation fill = new FillOperation(
                seed,
                model.topmostShapeContaining(seed),
                settings.algorithm(),
                settings.connectivity(),
                settings.fillColor(),
                settings.boundaryColor());
        model.addFill(fill);

        // A matriz já contém a cena atual: basta executar o novo preenchimento sobre ela
        int painted = renderer.applyFill(fill, scene);
        refreshOverlay();
        changeListeners.forEach(Runnable::run);
        return painted;
    }

    public void clearFills() {
        model.clearFills();
        redraw();
    }

    // ---------- recorte ----------

    public void setClipWindow(Rectangle window) {
        model.setClipWindow(window);
        redraw();
    }

    /**
     * Recorta os objetos selecionados (ou todos, se nada estiver selecionado)
     * pela janela de recorte atual. A janela precisa estar definida.
     *
     * @return resumo do que aconteceu, exibido na interface
     */
    public String clip(ClipAlgorithm algorithm) {
        Rectangle window = model.clipWindow();
        ShapeClipper shapeClipper = new ShapeClipper(algorithm.clipper());
        List<Shape> targets = new ArrayList<>(
                model.selection().isEmpty() ? model.shapes() : model.selection());

        int removed = 0;
        for (Shape shape : targets) {
            List<Shape> result = shapeClipper.clip(shape, window);
            if (result.isEmpty()) {
                removed++;
            }
            model.replace(shape, result);
        }
        model.clearSelection();
        redraw();
        return String.format("%s: %d objeto(s) processado(s), %d removido(s) por estarem fora da janela.",
                algorithm, targets.size(), removed);
    }

    /** Registra uma ação executada sempre que o modelo ou a seleção mudam. */
    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    public void setActiveTool(Tool tool) {
        if (activeTool != null) {
            activeTool.reset();
        }
        activeTool = tool;
        refreshOverlay();
    }

    public int currentColor() {
        return currentColor;
    }

    public void setCurrentColor(int argb) {
        this.currentColor = argb;
    }

    public LineAlgorithm currentLineAlgorithm() {
        return currentLineAlgorithm;
    }

    public void setCurrentLineAlgorithm(LineAlgorithm algorithm) {
        this.currentLineAlgorithm = algorithm;
    }

    public DrawingModel model() {
        return model;
    }

    public ReadOnlyStringProperty coordinatesTextProperty() {
        return coordinatesText.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty hintTextProperty() {
        return hintText.getReadOnlyProperty();
    }

    // ---------- eventos do mouse ----------

    private void registerMouseHandlers() {
        canvas.setOnMousePressed(event -> handle(event, (tool, point) ->
                tool.mousePressed(point, event.getButton() == MouseButton.PRIMARY)));
        canvas.setOnMouseMoved(event -> handle(event, Tool::mouseMoved));
        canvas.setOnMouseDragged(event -> handle(event, Tool::mouseDragged));
        canvas.setOnMouseReleased(event -> handle(event, Tool::mouseReleased));
    }

    /** Converte a posição do mouse, repassa à ferramenta e atualiza a tela. */
    private void handle(MouseEvent event, BiConsumer<Tool, Point> action) {
        Pixel pixel = canvas.toCartesian(event.getX(), event.getY());
        if (pixel == null || activeTool == null) {
            return; // fora da Área de Desenho
        }
        updateCoordinates(pixel);
        action.accept(activeTool, new Point(pixel.x(), pixel.y()));
        refreshOverlay();
    }

    private void updateCoordinates(Pixel pixel) {
        coordinatesText.set(String.format("x = %d, y = %d  |  matriz [linha %d, coluna %d]",
                pixel.x(), pixel.y(),
                scene.coordinates().toRow(pixel.y()),
                scene.coordinates().toColumn(pixel.x())));
    }
}
