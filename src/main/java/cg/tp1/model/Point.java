package cg.tp1.model;

/**
 * Ponto (vértice) em coordenadas cartesianas.
 *
 * As coordenadas são {@code double} para que transformações sucessivas
 * (ex.: várias rotações) não acumulem erro de arredondamento. O arredondamento
 * para pixel só acontece no momento da rasterização.
 */
public record Point(double x, double y) {

    public int roundedX() {
        return (int) Math.round(x);
    }

    public int roundedY() {
        return (int) Math.round(y);
    }

    public double distanceTo(Point other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}
