package cg.tp1.algorithms.clipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cg.tp1.model.Circle;
import cg.tp1.model.Line;
import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Point;
import cg.tp1.model.PointShape;
import cg.tp1.model.Polygon;
import cg.tp1.model.Rectangle;
import cg.tp1.model.Shape;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShapeClipperTest {

    private static final int BLACK = 0xFF000000;
    private static final Rectangle WINDOW = new Rectangle(0, 0, 10, 10);
    private final ShapeClipper clipper = new ShapeClipper(new CohenSutherland());

    @Test
    void polygonCrossingTheWindowBecomesLines() {
        // Quadrado de (5,5) a (15,15): só as arestas inferior e esquerda entram na janela
        Polygon square = new Polygon(List.of(
                new Point(5, 5), new Point(15, 5), new Point(15, 15), new Point(5, 15)),
                BLACK, LineAlgorithm.BRESENHAM);

        List<Shape> result = clipper.clip(square, WINDOW);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s instanceof Line));
        Line bottom = (Line) result.get(0);
        assertEquals(new Point(5, 5), bottom.start());
        assertEquals(new Point(10, 5), bottom.end());
        assertEquals(LineAlgorithm.BRESENHAM, bottom.algorithm()); // mantém cor e algoritmo
        assertEquals(BLACK, bottom.color());
    }

    @Test
    void polygonOutsideDisappears() {
        Polygon far = new Polygon(List.of(new Point(20, 20), new Point(30, 20), new Point(25, 30)),
                BLACK, LineAlgorithm.DDA);
        assertTrue(clipper.clip(far, WINDOW).isEmpty());
    }

    @Test
    void pointsAreKeptOnlyInside() {
        PointShape inside = new PointShape(new Point(3, 3), BLACK);
        assertEquals(List.of(inside), clipper.clip(inside, WINDOW));
        assertTrue(clipper.clip(new PointShape(new Point(-3, 3), BLACK), WINDOW).isEmpty());
    }

    @Test
    void circlesAreNotClipped() {
        Circle circle = new Circle(new Point(0, 0), 50, BLACK);
        assertSame(circle, clipper.clip(circle, WINDOW).get(0));
    }
}
