package cg.tp1.ui.tools;

import cg.tp1.model.Point;
import cg.tp1.model.PointShape;
import cg.tp1.ui.DrawingEditor;

/** Um clique cria um ponto. */
public class PointTool implements Tool {

    private final DrawingEditor editor;

    public PointTool(DrawingEditor editor) {
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Ponto";
    }

    @Override
    public String hint() {
        return "Clique para criar um ponto.";
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        if (primary) {
            editor.addShape(new PointShape(point, editor.currentColor()));
        }
    }
}
