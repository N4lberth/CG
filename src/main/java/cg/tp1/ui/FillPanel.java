package cg.tp1.ui;

import cg.tp1.model.Connectivity;
import cg.tp1.model.FillAlgorithm;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Painel "Preenchimento": algoritmo, conectividade e cores.
 * O preenchimento em si é feito clicando com a ferramenta "Preencher".
 */
public class FillPanel extends TitledPane {

    public FillPanel(DrawingEditor editor, FillSettings settings) {
        ComboBox<FillAlgorithm> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll(FillAlgorithm.values());
        algorithmBox.setValue(settings.algorithm());
        algorithmBox.setMaxWidth(Double.MAX_VALUE);

        ToggleGroup connectivityGroup = new ToggleGroup();
        RadioButton four = new RadioButton("4 vizinhos");
        RadioButton eight = new RadioButton("8 vizinhos");
        four.setToggleGroup(connectivityGroup);
        eight.setToggleGroup(connectivityGroup);
        four.setUserData(Connectivity.FOUR);
        eight.setUserData(Connectivity.EIGHT);
        (settings.connectivity() == Connectivity.FOUR ? four : eight).setSelected(true);
        connectivityGroup.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                settings.setConnectivity((Connectivity) selected.getUserData());
            }
        });

        ColorPicker fillColor = new ColorPicker(Color.web("#42A5F5"));
        fillColor.setMaxWidth(Double.MAX_VALUE);
        fillColor.setOnAction(e -> settings.setFillColor(ColorUtil.toArgb(fillColor.getValue())));
        settings.setFillColor(ColorUtil.toArgb(fillColor.getValue()));

        ColorPicker boundaryColor = new ColorPicker(Color.BLACK);
        boundaryColor.setMaxWidth(Double.MAX_VALUE);
        boundaryColor.setOnAction(e -> settings.setBoundaryColor(ColorUtil.toArgb(boundaryColor.getValue())));
        settings.setBoundaryColor(ColorUtil.toArgb(boundaryColor.getValue()));
        Label boundaryLabel = new Label("Cor da fronteira (Boundary-Fill)");

        // A cor da fronteira só faz sentido no Boundary-Fill
        Runnable updateBoundaryState = () -> {
            boolean boundary = algorithmBox.getValue() == FillAlgorithm.BOUNDARY_FILL;
            boundaryColor.setDisable(!boundary);
            boundaryLabel.setDisable(!boundary);
        };
        algorithmBox.setOnAction(e -> {
            settings.setAlgorithm(algorithmBox.getValue());
            updateBoundaryState.run();
        });
        updateBoundaryState.run();

        Button clearFills = new Button("Remover preenchimentos");
        clearFills.setMaxWidth(Double.MAX_VALUE);
        clearFills.setOnAction(e -> editor.clearFills());

        Label note = new Label("Com 8 vizinhos o preenchimento pode \"vazar\" pelas diagonais de "
                + "bordas desenhadas por DDA/Bresenham, que são 8-conectadas. É o comportamento esperado.");
        note.getStyleClass().add("note");
        note.setWrapText(true);

        setText("Preenchimento");
        setContent(new VBox(6,
                algorithmBox,
                new Label("Conectividade"), new HBox(12, four, eight),
                new Label("Cor de preenchimento"), fillColor,
                boundaryLabel, boundaryColor,
                clearFills,
                note));
        setCollapsible(false);
    }
}
