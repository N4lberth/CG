package cg.tp1.ui.tools;

import cg.tp1.model.Rectangle;
import cg.tp1.ui.DrawingEditor;

/**
 * Seleção por região retangular: são selecionados os objetos inteiramente
 * dentro do retângulo arrastado. Botão direito limpa a seleção.
 */
public class SelectTool extends RectangleDragTool {

    private final DrawingEditor editor;

    public SelectTool(DrawingEditor editor) {
        super(0xFF1E88E5);
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Selecionar";
    }

    @Override
    public String hint() {
        return "Arraste um retângulo para selecionar os objetos inteiramente dentro dele. "
                + editor.model().selection().size() + " selecionado(s).";
    }

    @Override
    protected void rectangleDefined(Rectangle rectangle) {
        editor.selectInside(rectangle);
    }

    @Override
    protected void secondaryClick() {
        editor.clearSelection();
    }
}
