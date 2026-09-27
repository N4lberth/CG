package cg.tp1.ui;

import cg.tp1.algorithms.transformation.Matrix3;
import cg.tp1.algorithms.transformation.Transformations;
import cg.tp1.model.Circle;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Painel "Transformações": o usuário escolhe o tipo, informa os fatores
 * e aplica sobre os objetos selecionados.
 */
public class TransformPanel extends TitledPane {

    private enum Kind {
        TRANSLATION("Translação"),
        ROTATION("Rotação"),
        SCALE("Escala"),
        REFLECTION_X("Reflexão em X"),
        REFLECTION_Y("Reflexão em Y"),
        REFLECTION_XY("Reflexão em XY");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final DrawingEditor editor;

    private final ComboBox<Kind> kindBox = new ComboBox<>();
    private final VBox parameters = new VBox(6);

    // Fatores informados pelo usuário (valores iniciais são só sugestões)
    private final Spinner<Double> dx = Controls.doubleSpinner(-400, 400, 10, 1);
    private final Spinner<Double> dy = Controls.doubleSpinner(-400, 400, 10, 1);
    private final Spinner<Double> angle = Controls.doubleSpinner(-360, 360, 45, 1);
    private final Slider angleSlider = new Slider(-360, 360, 45);
    private final Spinner<Double> sx = Controls.doubleSpinner(0.1, 10, 1.5, 0.1);
    private final Spinner<Double> sy = Controls.doubleSpinner(0.1, 10, 1.5, 0.1);

    private final RadioButton pivotCenter = new RadioButton("Centro da seleção");
    private final RadioButton pivotOrigin = new RadioButton("Origem (0, 0)");
    private final Label matrixLabel = new Label();

    public TransformPanel(DrawingEditor editor) {
        this.editor = editor;

        kindBox.getItems().addAll(Kind.values());
        kindBox.setValue(Kind.TRANSLATION);
        kindBox.setMaxWidth(Double.MAX_VALUE);
        kindBox.setOnAction(e -> showParameters());

        configureAngleSlider();

        ToggleGroup pivotGroup = new ToggleGroup();
        pivotCenter.setToggleGroup(pivotGroup);
        pivotOrigin.setToggleGroup(pivotGroup);
        pivotCenter.setSelected(true);

        Button apply = new Button("Aplicar");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(e -> applyTransformation());

        matrixLabel.getStyleClass().add("matrix");

        VBox content = new VBox(8,
                kindBox,
                parameters,
                new Label("Referência (rotação, escala e reflexão):"),
                pivotCenter, pivotOrigin,
                apply,
                matrixLabel);

        setText("Transformações");
        setContent(content);
        setCollapsible(false);
        showParameters();
    }

    /** Ângulo pode ser ajustado pelo slider ou pelo spinner; os dois ficam sincronizados. */
    private void configureAngleSlider() {
        angleSlider.setShowTickMarks(true);
        angleSlider.setMajorTickUnit(90);
        angleSlider.setMinorTickCount(1);
        angleSlider.valueProperty().addListener((obs, old, value) ->
                angle.getValueFactory().setValue((double) Math.round(value.doubleValue())));
        angle.valueProperty().addListener((obs, old, value) -> angleSlider.setValue(value));
    }

    /** Mostra apenas os campos do tipo de transformação escolhido. */
    private void showParameters() {
        Kind kind = kindBox.getValue();
        Node fields = switch (kind) {
            case TRANSLATION -> grid("dx", dx, "dy", dy);
            case ROTATION -> new VBox(4, grid("Ângulo (°)", angle), angleSlider);
            case SCALE -> grid("sx", sx, "sy", sy);
            case REFLECTION_X -> note("Inverte o sinal de y: (x, y) → (x, −y).");
            case REFLECTION_Y -> note("Inverte o sinal de x: (x, y) → (−x, y).");
            case REFLECTION_XY -> note("Inverte os dois sinais: (x, y) → (−x, −y).");
        };
        parameters.getChildren().setAll(fields);

        boolean usesPivot = kind != Kind.TRANSLATION; // translação não depende de referência
        pivotCenter.setDisable(!usesPivot);
        pivotOrigin.setDisable(!usesPivot);
    }

    private void applyTransformation() {
        if (editor.model().selection().isEmpty()) {
            Controls.warn("Nenhum objeto selecionado",
                    "Use a ferramenta Selecionar e arraste um retângulo sobre os objetos a transformar.");
            return;
        }

        Kind kind = kindBox.getValue();
        if (kind == Kind.SCALE && !canScaleSelection()) {
            return;
        }

        Matrix3 matrix = buildMatrix(kind);
        if (kind != Kind.TRANSLATION && pivotCenter.isSelected()) {
            matrix = Transformations.aroundPivot(matrix, editor.model().selectionBounds().center());
        }

        editor.transformSelection(matrix);
        matrixLabel.setText("Matriz aplicada:\n" + matrix);
    }

    private Matrix3 buildMatrix(Kind kind) {
        return switch (kind) {
            case TRANSLATION -> Transformations.translation(Controls.valueOf(dx), Controls.valueOf(dy));
            case ROTATION -> Transformations.rotation(Controls.valueOf(angle));
            case SCALE -> Transformations.scale(Controls.valueOf(sx), Controls.valueOf(sy));
            case REFLECTION_X -> Transformations.reflectionX();
            case REFLECTION_Y -> Transformations.reflectionY();
            case REFLECTION_XY -> Transformations.reflectionXY();
        };
    }

    /** Circunferência só aceita escala uniforme: com sx ≠ sy ela viraria uma elipse. */
    private boolean canScaleSelection() {
        boolean uniform = Controls.valueOf(sx) == Controls.valueOf(sy);
        boolean hasCircle = editor.model().selection().stream().anyMatch(s -> s instanceof Circle);
        if (!uniform && hasCircle) {
            Controls.warn("Escala não uniforme em circunferência",
                    "Com sx ≠ sy a circunferência viraria uma elipse, que o algoritmo de Bresenham "
                            + "para circunferências não desenha. Use sx = sy ou retire as circunferências da seleção.");
            return false;
        }
        return true;
    }

    private static GridPane grid(String label, Node field) {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(4);
        addRow(grid, label, field);
        return grid;
    }

    private static GridPane grid(String label1, Node field1, String label2, Node field2) {
        GridPane grid = grid(label1, field1);
        addRow(grid, label2, field2);
        return grid;
    }

    private static void addRow(GridPane grid, String label, Node field) {
        GridPane.setHgrow(field, Priority.ALWAYS);
        grid.addRow(grid.getRowCount(), new Label(label), field);
    }

    private static Label note(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("note");
        label.setWrapText(true);
        return label;
    }
}
