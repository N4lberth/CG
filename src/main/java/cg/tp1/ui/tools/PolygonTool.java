package cg.tp1.ui.tools;

import cg.tp1.model.Line;
import cg.tp1.model.Point;
import cg.tp1.model.Polygon;
import cg.tp1.model.Shape;
import cg.tp1.ui.DrawingEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * Cada clique adiciona um vértice. O polígono é fechado com o botão direito
 * ou clicando perto do primeiro vértice (mínimo de 3 vértices).
 */
public class PolygonTool implements Tool {

    /** Distância (em pixels) do 1º vértice que conta como "clicar nele". */
    private static final double CLOSE_DISTANCE = 2.0;

    private final DrawingEditor editor;
    private final List<Point> vertices = new ArrayList<>();
    private Point mouse;

    public PolygonTool(DrawingEditor editor) {
        this.editor = editor;
    }

    @Override
    public String name() {
        return "Polígono";
    }

    @Override
    public String hint() {
        if (vertices.isEmpty()) {
            return "Clique para adicionar o primeiro vértice.";
        }
        return "Clique para adicionar vértices. Feche com o botão direito ou clicando no 1º vértice ("
                + vertices.size() + " até agora).";
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        boolean canClose = vertices.size() >= 3;
        if (!primary) {
            if (canClose) {
                close();
            } else {
                reset(); // botão direito com menos de 3 vértices cancela
            }
        } else if (canClose && point.distanceTo(vertices.get(0)) <= CLOSE_DISTANCE) {
            close();
        } else {
            vertices.add(point);
            mouse = point;
        }
    }

    @Override
    public void mouseMoved(Point point) {
        mouse = point;
    }

    @Override
    public void reset() {
        vertices.clear();
        mouse = null;
    }

    /** Arestas já definidas + aresta "elástica" até o mouse. */
    @Override
    public List<Shape> preview() {
        List<Shape> edges = new ArrayList<>();
        for (int i = 0; i + 1 < vertices.size(); i++) {
            edges.add(newEdge(vertices.get(i), vertices.get(i + 1)));
        }
        if (!vertices.isEmpty() && mouse != null) {
            edges.add(newEdge(vertices.get(vertices.size() - 1), mouse));
        }
        return edges;
    }

    private void close() {
        editor.addShape(new Polygon(vertices, editor.currentColor(), editor.currentLineAlgorithm()));
        reset();
    }

    private Line newEdge(Point a, Point b) {
        return new Line(a, b, editor.currentColor(), editor.currentLineAlgorithm());
    }
}
