package cg.tp1.ui.tools;

import cg.tp1.model.Rectangle;
import cg.tp1.ui.DrawingEditor;

/**
 * Define a janela de recorte arrastando um retângulo.
 * Botão direito remove a janela.
 */
public class ClipWindowTool extends RectangleDragTool {

    private final DrawingEditor editor;

    public ClipWindowTool(DrawingEditor editor) {
        super(0xFF2E7D32);
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Janela de recorte";
    }

    @Override
    public String hint() {
        Rectangle window = editor.model().clipWindow();
        if (window == null) {
            return "Arraste para definir a janela de recorte.";
        }
        return String.format("Janela: x ∈ [%.0f, %.0f], y ∈ [%.0f, %.0f]. Arraste para redefinir; "
                + "botão direito remove.", window.minX(), window.maxX(), window.minY(), window.maxY());
    }

    @Override
    protected void rectangleDefined(Rectangle rectangle) {
        boolean hasArea = rectangle.maxX() > rectangle.minX() && rectangle.maxY() > rectangle.minY();
        if (hasArea) { // um simples clique (sem arrastar) não define janela
            editor.setClipWindow(rectangle);
        }
    }

    @Override
    protected void secondaryClick() {
        editor.setClipWindow(null);
    }
}
