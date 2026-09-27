package cg.tp1.model;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Ponto isolado desenhado na Área de Desenho (ocupa um pixel).
 */
public final class PointShape implements Shape {

    private Point position;
    private final int color;

    public PointShape(Point position, int color) {
        this.position = position;
        this.color = color;
    }

    public Point position() {
        return position;
    }

    @Override
    public List<Point> vertices() {
        return List.of(position);
    }

    @Override
    public void transform(UnaryOperator<Point> mapping) {
        position = mapping.apply(position);
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return "Ponto " + position;
    }
}
