package cg.tp1.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SelectionTest {

    private static final int BLACK = 0xFF000000;

    private final DrawingModel model = new DrawingModel();

    /** Região de seleção de (−10, −10) até (10, 10). */
    private final Rectangle region = Rectangle.fromCorners(new Point(-10, -10), new Point(10, 10));

    @Test
    void selectsOnlyObjectsEntirelyInside() {
        Line inside = new Line(new Point(-5, 0), new Point(5, 5), BLACK, LineAlgorithm.DDA);
        Line crossing = new Line(new Point(0, 0), new Point(20, 0), BLACK, LineAlgorithm.DDA);
        model.add(inside);
        model.add(crossing);

        assertEquals(1, model.selectInside(region));
        assertTrue(model.isSelected(inside));
        assertFalse(model.isSelected(crossing));
    }

    @Test
    void circleUsesItsBoundingBoxNotOnlyTheCenter() {
        Circle small = new Circle(new Point(0, 0), 5, BLACK);
        Circle big = new Circle(new Point(0, 0), 15, BLACK); // centro dentro, borda fora
        model.add(small);
        model.add(big);

        model.selectInside(region);
        assertTrue(model.isSelected(small));
        assertFalse(model.isSelected(big));
    }

    @Test
    void polygonNeedsAllVerticesInside() {
        Polygon inside = new Polygon(List.of(new Point(0, 0), new Point(5, 0), new Point(0, 5)), BLACK,
                LineAlgorithm.BRESENHAM);
        Polygon partial = new Polygon(List.of(new Point(0, 0), new Point(50, 0), new Point(0, 5)), BLACK,
                LineAlgorithm.BRESENHAM);
        model.add(inside);
        model.add(partial);

        model.selectInside(region);
        assertEquals(1, model.selection().size());
        assertTrue(model.isSelected(inside));
    }

    @Test
    void rectangleCornersCanBeGivenInAnyOrder() {
        Rectangle r = Rectangle.fromCorners(new Point(10, -3), new Point(-2, 7));
        assertEquals(new Rectangle(-2, -3, 10, 7), r);
    }

    @Test
    void newSelectionReplacesThePreviousOne() {
        PointShape a = new PointShape(new Point(0, 0), BLACK);
        PointShape b = new PointShape(new Point(30, 30), BLACK);
        model.add(a);
        model.add(b);

        model.selectInside(region);
        model.selectInside(Rectangle.fromCorners(new Point(25, 25), new Point(35, 35)));
        assertFalse(model.isSelected(a));
        assertTrue(model.isSelected(b));
    }

    @Test
    void identicalShapesAreDistinctObjects() {
        Line l1 = new Line(new Point(0, 0), new Point(1, 1), BLACK, LineAlgorithm.DDA);
        Line l2 = new Line(new Point(0, 0), new Point(1, 1), BLACK, LineAlgorithm.DDA);
        model.add(l1);
        model.add(l2);
        assertEquals(2, model.selectInside(region));
    }
}
