package cg.tp1.render;

import cg.tp1.algorithms.filling.BoundaryFill;
import cg.tp1.algorithms.filling.FloodFill;
import cg.tp1.algorithms.rasterization.BresenhamCircle;
import cg.tp1.algorithms.rasterization.BresenhamLine;
import cg.tp1.algorithms.rasterization.DDALine;
import cg.tp1.algorithms.rasterization.LineRasterizer;
import cg.tp1.model.Circle;
import cg.tp1.model.FillOperation;
import cg.tp1.model.Line;
import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Point;
import cg.tp1.model.PointShape;
import cg.tp1.model.Polygon;
import cg.tp1.model.Shape;
import cg.tp1.raster.Pixel;
import cg.tp1.raster.PixelBuffer;
import java.util.List;

/**
 * Converte os objetos do modelo em pixels na matriz, chamando os algoritmos
 * de rasterização. É a ponte entre o modelo (coordenadas reais) e a
 * {@link PixelBuffer} (pixels inteiros).
 */
public class SceneRenderer {

    private final LineRasterizer dda = new DDALine();
    private final LineRasterizer bresenham = new BresenhamLine();
    private final BresenhamCircle circleRasterizer = new BresenhamCircle();

    /**
     * Limpa a matriz, desenha todos os objetos e, em seguida, refaz os
     * preenchimentos na ordem em que foram feitos.
     */
    public void render(List<Shape> shapes, List<FillOperation> fills, PixelBuffer buffer) {
        buffer.clear();
        for (Shape shape : shapes) {
            draw(shape, buffer);
        }
        for (FillOperation fill : fills) {
            applyFill(fill, buffer);
        }
    }

    /** Executa um preenchimento na matriz. @return quantidade de pixels pintados */
    public int applyFill(FillOperation fill, PixelBuffer buffer) {
        int x = fill.seed().roundedX();
        int y = fill.seed().roundedY();
        return switch (fill.algorithm()) {
            case BOUNDARY_FILL -> BoundaryFill.fill(buffer, x, y,
                    fill.fillColor(), fill.boundaryColor(), fill.connectivity());
            case FLOOD_FILL -> FloodFill.fill(buffer, x, y, fill.fillColor(), fill.connectivity());
        };
    }

    /** Desenha um objeto com a sua própria cor. */
    public void draw(Shape shape, PixelBuffer buffer) {
        draw(shape, buffer, shape.color());
    }

    /** Desenha um objeto com uma cor específica (usado no destaque da seleção). */
    public void draw(Shape shape, PixelBuffer buffer, int color) {
        switch (shape) {
            case PointShape point -> buffer.set(point.position().roundedX(), point.position().roundedY(), color);
            case Line line -> drawLine(line.start(), line.end(), line.algorithm(), color, buffer);
            case Polygon polygon -> drawPolygon(polygon, color, buffer);
            case Circle circle -> drawCircle(circle, color, buffer);
        }
    }

    private void drawPolygon(Polygon polygon, int color, PixelBuffer buffer) {
        List<Point> vertices = polygon.vertices();
        for (int i = 0; i < vertices.size(); i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % vertices.size()); // última aresta fecha no 1º vértice
            drawLine(a, b, polygon.algorithm(), color, buffer);
        }
    }

    private void drawCircle(Circle circle, int color, PixelBuffer buffer) {
        List<Pixel> pixels = circleRasterizer.rasterize(
                circle.center().roundedX(),
                circle.center().roundedY(),
                (int) Math.round(circle.radius()));
        pixels.forEach(pixel -> buffer.set(pixel, color));
    }

    private void drawLine(Point a, Point b, LineAlgorithm algorithm, int color, PixelBuffer buffer) {
        LineRasterizer rasterizer = algorithm == LineAlgorithm.DDA ? dda : bresenham;
        List<Pixel> pixels = rasterizer.rasterize(a.roundedX(), a.roundedY(), b.roundedX(), b.roundedY());
        pixels.forEach(pixel -> buffer.set(pixel, color));
    }
}
