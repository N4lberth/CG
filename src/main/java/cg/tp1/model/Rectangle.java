package cg.tp1.model;

import java.util.List;

/**
 * Retângulo alinhado aos eixos, em coordenadas cartesianas.
 * Usado como região de seleção e como caixa envolvente (bounding box) dos objetos.
 */
public record Rectangle(double minX, double minY, double maxX, double maxY) {

    /** Cria o retângulo a partir de dois cantos opostos quaisquer (ordem não importa). */
    public static Rectangle fromCorners(Point a, Point b) {
        return new Rectangle(
                Math.min(a.x(), b.x()), Math.min(a.y(), b.y()),
                Math.max(a.x(), b.x()), Math.max(a.y(), b.y()));
    }

    /** Menor retângulo que contém todos os pontos. */
    public static Rectangle enclosing(List<Point> points) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Point p : points) {
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        return new Rectangle(minX, minY, maxX, maxY);
    }

    public boolean contains(Point p) {
        return p.x() >= minX && p.x() <= maxX && p.y() >= minY && p.y() <= maxY;
    }

    /** Verdadeiro se {@code other} está inteiramente dentro deste retângulo. */
    public boolean contains(Rectangle other) {
        return other.minX >= minX && other.maxX <= maxX
                && other.minY >= minY && other.maxY <= maxY;
    }

    /** Cantos no sentido anti-horário, começando pelo inferior esquerdo. */
    public List<Point> corners() {
        return List.of(
                new Point(minX, minY), new Point(maxX, minY),
                new Point(maxX, maxY), new Point(minX, maxY));
    }

    public Point center() {
        return new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }
}
