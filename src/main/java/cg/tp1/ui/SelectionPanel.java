package cg.tp1.ui;

import cg.tp1.model.Shape;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Painel "Seleção": mostra quais objetos estão selecionados, para que fique
 * claro sobre o que as próximas operações (transformação, recorte) vão agir.
 */
public class SelectionPanel extends TitledPane {

    private final DrawingEditor editor;
    private final Label countLabel = new Label();
    private final ListView<String> list = new ListView<>();

    public SelectionPanel(DrawingEditor editor) {
        this.editor = editor;

        list.setPrefHeight(110);
        list.setFocusTraversable(false);
        list.setPlaceholder(new Label("Nenhum objeto selecionado"));

        Button selectAll = new Button("Selecionar tudo");
        selectAll.setOnAction(e -> editor.selectAll());
        Button clear = new Button("Limpar seleção");
        clear.setOnAction(e -> editor.clearSelection());
        selectAll.setMaxWidth(Double.MAX_VALUE);
        clear.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(selectAll, Priority.ALWAYS);
        HBox.setHgrow(clear, Priority.ALWAYS);

        setText("Seleção");
        setContent(new VBox(6, countLabel, list, new HBox(6, selectAll, clear)));
        setCollapsible(false);

        editor.addChangeListener(this::refresh);
        refresh();
    }

    private void refresh() {
        var selection = editor.model().selection();
        countLabel.setText(selection.size() + " de " + editor.model().shapes().size() + " objeto(s)");
        list.getItems().setAll(selection.stream().map(Shape::toString).toList());
    }
}
