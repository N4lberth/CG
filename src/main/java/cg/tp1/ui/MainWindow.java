package cg.tp1.ui;

import cg.tp1.raster.CoordinateSystem;
import cg.tp1.raster.PixelBuffer;
import cg.tp1.ui.tools.CircleTool;
import cg.tp1.ui.tools.ClipWindowTool;
import cg.tp1.ui.tools.FillTool;
import cg.tp1.ui.tools.LineTool;
import cg.tp1.ui.tools.PointTool;
import cg.tp1.ui.tools.PolygonTool;
import cg.tp1.ui.tools.SelectTool;
import cg.tp1.ui.tools.Tool;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Monta o layout da janela principal:
 * barra de ferramentas (topo), Área de Desenho (centro),
 * painel de operações (direita) e barra de status (base).
 */
public class MainWindow {

    /** Resolução lógica da matriz de pixels (colunas × linhas). */
    private static final int COLUMNS = 200;
    private static final int ROWS = 150;
    private static final int DEFAULT_PIXEL_SIZE = 4;

    private final Stage stage;
    private final DrawingCanvas canvas;
    private final DrawingEditor editor;
    private final FillSettings fillSettings = new FillSettings();

    public MainWindow(Stage stage) {
        this.stage = stage;
        CoordinateSystem coordinates = new CoordinateSystem(COLUMNS, ROWS);
        PixelBuffer sceneBuffer = new PixelBuffer(coordinates, PixelBuffer.WHITE);
        PixelBuffer overlay = new PixelBuffer(coordinates, PixelBuffer.TRANSPARENT);
        this.canvas = new DrawingCanvas(sceneBuffer, overlay, DEFAULT_PIXEL_SIZE);
        this.editor = new DrawingEditor(sceneBuffer, overlay, canvas);

        Tool lineTool = new LineTool(editor);
        List<Tool> tools = List.of(
                new SelectTool(editor),
                new PointTool(editor),
                lineTool,
                new PolygonTool(editor),
                new CircleTool(editor),
                new ClipWindowTool(editor),
                new FillTool(editor, fillSettings));

        BorderPane root = new BorderPane();
        root.setTop(createToolBar(tools, lineTool));
        root.setCenter(createDrawingArea());
        root.setRight(createSidePanel());
        root.setBottom(createStatusBar());

        Scene fxScene = new Scene(root);
        fxScene.getStylesheets().add(getClass().getResource("/cg/tp1/style.css").toExternalForm());
        stage.setTitle("CG TP1 — Algoritmos da Unidade 1");
        stage.setScene(fxScene);
    }

    public void show() {
        stage.show();
    }

    /**
     * Barra superior. Usa FlowPane (e não ToolBar) para que os controles quebrem
     * para uma segunda linha em janelas estreitas, em vez de ficarem escondidos.
     */
    private Node createToolBar(List<Tool> tools, Tool initialTool) {
        HBox toolButtons = new HBox(4);
        ToggleButton initialButton = null;

        // Botões de ferramenta: apenas um ativo por vez
        ToggleGroup group = new ToggleGroup();
        for (Tool tool : tools) {
            ToggleButton button = new ToggleButton(tool.name());
            button.setToggleGroup(group);
            button.setUserData(tool);
            toolButtons.getChildren().add(button);
            if (tool == initialTool) {
                initialButton = button;
            }
        }
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                group.selectToggle(oldToggle); // impede ficar sem ferramenta
            } else {
                editor.setActiveTool((Tool) newToggle.getUserData());
            }
        });
        group.selectToggle(initialButton);

        Button clearButton = new Button("Limpar");
        clearButton.setOnAction(e -> editor.clear());

        CheckBox gridCheck = new CheckBox("Grade");
        gridCheck.setSelected(true);
        gridCheck.setOnAction(e -> canvas.setShowGrid(gridCheck.isSelected()));

        CheckBox axesCheck = new CheckBox("Eixos");
        axesCheck.setSelected(true);
        axesCheck.setOnAction(e -> canvas.setShowAxes(axesCheck.isSelected()));

        ComboBox<Integer> zoomBox = new ComboBox<>();
        zoomBox.getItems().addAll(2, 3, 4, 5, 6);
        zoomBox.setValue(DEFAULT_PIXEL_SIZE);
        zoomBox.setOnAction(e -> canvas.setPixelSize(zoomBox.getValue()));

        HBox viewOptions = new HBox(10, gridCheck, axesCheck, new Label("Tamanho do pixel:"), zoomBox);
        viewOptions.setAlignment(Pos.CENTER_LEFT);

        FlowPane bar = new FlowPane(16, 6, toolButtons, clearButton, viewOptions);
        bar.getStyleClass().add("top-bar");
        return bar;
    }

    private ScrollPane createDrawingArea() {
        StackPane holder = new StackPane(canvas);
        holder.getStyleClass().add("drawing-holder");
        ScrollPane scroll = new ScrollPane(holder);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        return scroll;
    }

    private Node createSidePanel() {
        VBox panel = new VBox(10,
                new DrawSettingsPanel(editor),
                new SelectionPanel(editor),
                new TransformPanel(editor),
                new ClipPanel(editor),
                new FillPanel(editor, fillSettings));
        panel.getStyleClass().add("side-panel");

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefWidth(300);
        scroll.setPrefViewportHeight(300); // a altura da janela é ditada pela Área de Desenho; o painel rola
        return scroll;
    }

    private HBox createStatusBar() {
        Label coordinates = new Label();
        coordinates.textProperty().bind(editor.coordinatesTextProperty());
        Label hint = new Label();
        hint.textProperty().bind(editor.hintTextProperty());
        hint.getStyleClass().add("hint");

        HBox status = new HBox(16, hint, spacer(), coordinates);
        status.getStyleClass().add("status-bar");
        status.setAlignment(Pos.CENTER_LEFT);
        return status;
    }

    private static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
