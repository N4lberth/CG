package cg.tp1.ui.tools;

import cg.tp1.model.Circle;
import cg.tp1.model.Point;
import cg.tp1.model.Shape;
import cg.tp1.ui.DrawingEditor;
import java.util.List;

/**
 * 1º clique: centro. 2º clique: um ponto da circunferência
 * (o raio é a distância entre os dois pontos).
 */
public class CircleTool implements Tool {

    private final DrawingEditor editor;
    private Point center;
    private Point mouse;

    public CircleTool(DrawingEditor editor) {
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Circunferência";
    }

    @Override
    public String hint() {
        if (center == null) {
            return "Clique no centro da circunferência.";
        }
        return String.format("Clique para definir o raio (atual: %.1f). Botão direito cancela.",
                mouse == null ? 0.0 : center.distanceTo(mouse));
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        if (!primary) {
            reset();
        } else if (center == null) {
            center = point;
            mouse = point;
        } else {
            editor.addShape(newCircle(point));
            reset();
        }
    }

    @Override
    public void mouseMoved(Point point) {
        mouse = point;
    }

    @Override
    public void reset() {
        center = null;
        mouse = null;
    }

    @Override
    public List<Shape> preview() {
        return center == null ? List.of() : List.of(newCircle(mouse));
    }

    private Circle newCircle(Point pointOnBorder) {
        return new Circle(center, center.distanceTo(pointOnBorder), editor.currentColor());
    }
}
