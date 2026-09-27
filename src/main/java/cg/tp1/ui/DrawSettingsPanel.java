package cg.tp1.ui;

import cg.tp1.model.LineAlgorithm;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Painel "Desenho": cor e algoritmo de rasterização usados nos próximos objetos.
 */
public class DrawSettingsPanel extends TitledPane {

    public DrawSettingsPanel(DrawingEditor editor) {
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setMaxWidth(Double.MAX_VALUE);
        colorPicker.setOnAction(e -> editor.setCurrentColor(ColorUtil.toArgb(colorPicker.getValue())));
        editor.setCurrentColor(ColorUtil.toArgb(colorPicker.getValue()));

        ComboBox<LineAlgorithm> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll(LineAlgorithm.values());
        algorithmBox.setValue(editor.currentLineAlgorithm());
        algorithmBox.setMaxWidth(Double.MAX_VALUE);
        algorithmBox.setOnAction(e -> editor.setCurrentLineAlgorithm(algorithmBox.getValue()));

        Label circleNote = new Label("Circunferências usam sempre Bresenham.");
        circleNote.getStyleClass().add("note");
        circleNote.setWrapText(true);

        VBox content = new VBox(6,
                new Label("Cor"), colorPicker,
                new Label("Algoritmo de reta"), algorithmBox,
                circleNote);

        setText("Desenho");
        setContent(content);
        setCollapsible(false);
    }
}
