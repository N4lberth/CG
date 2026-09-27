package cg.tp1.model;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Segmento de reta entre dois pontos. Guarda o algoritmo de rasterização
 * escolhido no momento da criação, o que permite comparar DDA e Bresenham
 * lado a lado na mesma cena.
 */
public final class Line implements Shape {

    private Point start;
    private Point end;
    private final int color;
    private final LineAlgorithm algorithm;

    public Line(Point start, Point end, int color, LineAlgorithm algorithm) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.algorithm = algorithm;
    }

    public Point start() {
        return start;
    }

    public Point end() {
        return end;
    }

    public LineAlgorithm algorithm() {
        return algorithm;
    }

    @Override
    public List<Point> vertices() {
        return List.of(start, end);
    }

    @Override
    public void transform(UnaryOperator<Point> mapping) {
        start = mapping.apply(start);
        end = mapping.apply(end);
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return "Reta (" + algorithm + ") " + start + " → " + end;
    }
}
