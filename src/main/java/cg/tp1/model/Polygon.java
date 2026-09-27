package cg.tp1.model;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Polígono fechado: a última aresta liga o último vértice ao primeiro.
 * As arestas são rasterizadas com o algoritmo de reta escolhido.
 */
public final class Polygon implements Shape {

    private List<Point> vertices;
    private final int color;
    private final LineAlgorithm algorithm;

    public Polygon(List<Point> vertices, int color, LineAlgorithm algorithm) {
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Um polígono precisa de pelo menos 3 vértices");
        }
        this.vertices = List.copyOf(vertices);
        this.color = color;
        this.algorithm = algorithm;
    }

    public LineAlgorithm algorithm() {
        return algorithm;
    }

    @Override
    public List<Point> vertices() {
        return vertices;
    }

    @Override
    public void transform(UnaryOperator<Point> mapping) {
        vertices = vertices.stream().map(mapping).toList();
    }

    /**
     * Teste de ponto em polígono por lançamento de raio (regra par-ímpar):
     * traça-se uma semirreta horizontal a partir de p, para a direita, e
     * contam-se as arestas cruzadas. Número ímpar = dentro.
     */
    @Override
    public boolean contains(Point p) {
        boolean inside = false;
        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            Point a = vertices.get(i);
            Point b = vertices.get(j);
            boolean crossesHorizontal = (a.y() > p.y()) != (b.y() > p.y());
            if (crossesHorizontal) {
                double xAtY = a.x() + (p.y() - a.y()) * (b.x() - a.x()) / (b.y() - a.y());
                if (p.x() < xAtY) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return "Polígono (" + vertices.size() + " vértices, " + algorithm + ")";
    }
}
