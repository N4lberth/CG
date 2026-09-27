package cg.tp1.ui;

import cg.tp1.raster.CoordinateSystem;
import cg.tp1.raster.Pixel;
import cg.tp1.raster.PixelBuffer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;

/**
 * Componente visual da Área de Desenho.
 *
 * Não desenha nenhuma geometria: apenas copia a {@link PixelBuffer} para a tela,
 * ampliando cada pixel lógico para um quadrado de {@code pixelSize × pixelSize}
 * pixels do monitor. Grade e eixos são guias visuais e NÃO fazem parte da matriz
 * (assim não interferem nos algoritmos de preenchimento).
 *
 * Ordem de exibição de cada célula: overlay (se não for transparente) →
 * cena → eixos → fundo.
 */
public class DrawingCanvas extends Canvas {

    private static final int AXIS_COLOR = 0xFFB0BEC5;
    private static final int GRID_COLOR = 0xFFE3E7EA;

    private final PixelBuffer buffer;
    private final PixelBuffer overlay;
    private int pixelSize;
    private boolean showGrid = true;
    private boolean showAxes = true;

    public DrawingCanvas(PixelBuffer buffer, PixelBuffer overlay, int pixelSize) {
        this.buffer = buffer;
        this.overlay = overlay;
        setPixelSize(pixelSize);
    }

    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
        CoordinateSystem cs = buffer.coordinates();
        setWidth(cs.columns() * pixelSize);
        setHeight(cs.rows() * pixelSize);
        render();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        render();
    }

    public void setShowAxes(boolean showAxes) {
        this.showAxes = showAxes;
        render();
    }

    /**
     * Copia a matriz de pixels para a tela.
     * Cada célula [linha][coluna] vira um bloco de pixelSize × pixelSize.
     */
    public void render() {
        CoordinateSystem cs = buffer.coordinates();
        int screenWidth = cs.columns() * pixelSize;
        int screenHeight = cs.rows() * pixelSize;
        int[] screen = new int[screenWidth * screenHeight];
        boolean drawGrid = showGrid && pixelSize >= 4; // com pixels pequenos a grade só polui

        for (int row = 0; row < cs.rows(); row++) {
            for (int column = 0; column < cs.columns(); column++) {
                int color = cellColor(row, column);
                fillBlock(screen, screenWidth, row, column, color, drawGrid);
            }
        }

        getGraphicsContext2D().getPixelWriter().setPixels(
                0, 0, screenWidth, screenHeight,
                PixelFormat.getIntArgbInstance(), screen, 0, screenWidth);
    }

    /** Cor exibida para a célula: overlay, matriz da cena ou, se for fundo, a cor do eixo. */
    private int cellColor(int row, int column) {
        int overlayColor = overlay.getByIndex(row, column);
        if ((overlayColor >>> 24) != 0) { // canal alfa diferente de zero = não transparente
            return overlayColor;
        }
        int color = buffer.getByIndex(row, column);
        if (showAxes && color == buffer.background()) {
            CoordinateSystem cs = buffer.coordinates();
            if (cs.toCartesianX(column) == 0 || cs.toCartesianY(row) == 0) {
                return AXIS_COLOR;
            }
        }
        return color;
    }

    private void fillBlock(int[] screen, int screenWidth, int row, int column, int color, boolean drawGrid) {
        int startX = column * pixelSize;
        int startY = row * pixelSize;
        for (int dy = 0; dy < pixelSize; dy++) {
            int offset = (startY + dy) * screenWidth + startX;
            for (int dx = 0; dx < pixelSize; dx++) {
                boolean border = drawGrid && (dx == pixelSize - 1 || dy == pixelSize - 1);
                screen[offset + dx] = border && color == buffer.background() ? GRID_COLOR : color;
            }
        }
    }

    /**
     * Converte a posição do mouse (em pixels do monitor) para coordenadas
     * cartesianas da matriz. Retorna {@code null} se estiver fora da área.
     */
    public Pixel toCartesian(double mouseX, double mouseY) {
        CoordinateSystem cs = buffer.coordinates();
        int column = (int) Math.floor(mouseX / pixelSize);
        int row = (int) Math.floor(mouseY / pixelSize);
        if (column < 0 || column >= cs.columns() || row < 0 || row >= cs.rows()) {
            return null;
        }
        return new Pixel(cs.toCartesianX(column), cs.toCartesianY(row));
    }
}
