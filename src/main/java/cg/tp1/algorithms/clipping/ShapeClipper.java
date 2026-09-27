package cg.tp1.algorithms.clipping;

import cg.tp1.model.Circle;
import cg.tp1.model.Line;
import cg.tp1.model.Point;
import cg.tp1.model.PointShape;
import cg.tp1.model.Polygon;
import cg.tp1.model.Rectangle;
import cg.tp1.model.Shape;
import java.util.ArrayList;
import java.util.List;

/**
 * Aplica um algoritmo de recorte de retas às figuras do modelo.
 *
 * Cohen-Sutherland e Liang-Barsky recortam SEGMENTOS, por isso:
 *  - Reta: é recortada diretamente;
 *  - Polígono: cada aresta é recortada como um segmento independente, e o
 *    resultado passa a ser um conjunto de retas (não é mais um polígono fechado);
 *  - Ponto: permanece se estiver dentro da janela;
 *  - Circunferência: não é recortada (fora do escopo desses algoritmos).
 */
public final class ShapeClipper {

    private final LineClipper clipper;

    public ShapeClipper(LineClipper clipper) {
        this.clipper = clipper;
    }

    /**
     * @return as figuras que substituem {@code shape} após o recorte
     *         (lista vazia = a figura estava inteiramente fora da janela)
     */
    public List<Shape> clip(Shape shape, Rectangle window) {
        return switch (shape) {
            case Line line -> clipLine(line, window);
            case Polygon polygon -> clipPolygon(polygon, window);
            case PointShape point -> window.contains(point.position()) ? List.of(point) : List.of();
            case Circle circle -> List.of(circle);
        };
    }

    private List<Shape> clipLine(Line line, Rectangle window) {
        return clipper.clip(line.start(), line.end(), window)
                .<List<Shape>>map(s -> List.of(new Line(s.start(), s.end(), line.color(), line.algorithm())))
                .orElse(List.of());
    }

    private List<Shape> clipPolygon(Polygon polygon, Rectangle window) {
        List<Shape> edges = new ArrayList<>();
        List<Point> vertices = polygon.vertices();
        for (int i = 0; i < vertices.size(); i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % vertices.size());
            clipper.clip(a, b, window).ifPresent(s ->
                    edges.add(new Line(s.start(), s.end(), polygon.color(), polygon.algorithm())));
        }
        return edges;
    }
}
