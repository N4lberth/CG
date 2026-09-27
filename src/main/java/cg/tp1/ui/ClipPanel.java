package cg.tp1.ui;

import cg.tp1.algorithms.clipping.ClipAlgorithm;
import cg.tp1.model.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Painel "Recorte": escolha do algoritmo e execução sobre a janela definida
 * com a ferramenta "Janela de recorte".
 */
public class ClipPanel extends TitledPane {

    private final DrawingEditor editor;
    private final Label windowLabel = new Label();
    private final Label resultLabel = new Label();

    public ClipPanel(DrawingEditor editor) {
        this.editor = editor;

        ComboBox<ClipAlgorithm> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll(ClipAlgorithm.values());
        algorithmBox.setValue(ClipAlgorithm.COHEN_SUTHERLAND);
        algorithmBox.setMaxWidth(Double.MAX_VALUE);

        Button clipButton = new Button("Recortar");
        clipButton.setOnAction(e -> clip(algorithmBox.getValue()));
        Button removeWindow = new Button("Remover janela");
        removeWindow.setOnAction(e -> editor.setClipWindow(null));
        clipButton.setMaxWidth(Double.MAX_VALUE);
        removeWindow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(clipButton, Priority.ALWAYS);
        HBox.setHgrow(removeWindow, Priority.ALWAYS);

        Label note = new Label("Atua nos objetos selecionados ou, sem seleção, em todos. "
                + "Polígonos são recortados aresta por aresta e viram retas. "
                + "Circunferências não são recortadas.");
        note.getStyleClass().add("note");
        note.setWrapText(true);

        resultLabel.getStyleClass().add("note");
        resultLabel.setWrapText(true);

        setText("Recorte");
        setContent(new VBox(6, windowLabel, algorithmBox, new HBox(6, clipButton, removeWindow), note, resultLabel));
        setCollapsible(false);

        editor.addChangeListener(this::refresh);
        refresh();
    }

    private void clip(ClipAlgorithm algorithm) {
        if (editor.model().clipWindow() == null) {
            Controls.warn("Nenhuma janela de recorte",
                    "Use a ferramenta \"Janela de recorte\" e arraste um retângulo na Área de Desenho.");
            return;
        }
        resultLabel.setText(editor.clip(algorithm));
    }

    private void refresh() {
        Rectangle window = editor.model().clipWindow();
        windowLabel.setText(window == null
                ? "Janela: não definida"
                : String.format("Janela: x ∈ [%.0f, %.0f], y ∈ [%.0f, %.0f]",
                        window.minX(), window.maxX(), window.minY(), window.maxY()));
    }
}
