package cg.tp1.ui.tools;

import cg.tp1.model.Line;
import cg.tp1.model.Point;
import cg.tp1.model.Shape;
import cg.tp1.ui.DrawingEditor;
import java.util.List;

/**
 * 1º clique: ponto inicial. 2º clique: ponto final.
 * Entre os cliques, mostra a pré-visualização até a posição do mouse.
 */
public class LineTool implements Tool {

    private final DrawingEditor editor;
    private Point start;
    private Point mouse;

    public LineTool(DrawingEditor editor) {
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Reta";
    }

    @Override
    public String hint() {
        return start == null
                ? "Clique no ponto inicial da reta."
                : "Clique no ponto final (botão direito cancela).";
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        if (!primary) {
            reset();
        } else if (start == null) {
            start = point;
            mouse = point;
        } else {
            editor.addShape(newLine(start, point));
            reset();
        }
    }

    @Override
    public void mouseMoved(Point point) {
        mouse = point;
    }

    @Override
    public void reset() {
        start = null;
        mouse = null;
    }

    @Override
    public List<Shape> preview() {
        return start == null ? List.of() : List.of(newLine(start, mouse));
    }

    private Line newLine(Point a, Point b) {
        return new Line(a, b, editor.currentColor(), editor.currentLineAlgorithm());
    }
}
