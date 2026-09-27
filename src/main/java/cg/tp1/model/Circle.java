package cg.tp1.model;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Circunferência definida por centro e raio, rasterizada por Bresenham.
 */
public final class Circle implements Shape {

    private Point center;
    private double radius;
    private final int color;

    public Circle(Point center, double radius, int color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    public Point center() {
        return center;
    }

    public double radius() {
        return radius;
    }

    @Override
    public List<Point> vertices() {
        return List.of(center);
    }

    /** O centro não basta: a caixa vai de (centro − raio) até (centro + raio). */
    @Override
    public Rectangle boundingBox() {
        return new Rectangle(center.x() - radius, center.y() - radius,
                center.x() + radius, center.y() + radius);
    }

    /**
     * Transforma o centro e um ponto da borda; o novo raio é a distância entre
     * eles. Vale para translação, rotação, reflexão e escala uniforme
     * (sx = sy). Escala não uniforme geraria uma elipse e é bloqueada na interface.
     */
    @Override
    public void transform(UnaryOperator<Point> mapping) {
        Point border = new Point(center.x() + radius, center.y());
        center = mapping.apply(center);
        radius = center.distanceTo(mapping.apply(border));
    }

    @Override
    public boolean contains(Point p) {
        return center.distanceTo(p) < radius;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public String toString() {
        return String.format("Circunferência centro %s raio %.1f", center, radius);
    }
}
