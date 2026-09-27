package cg.tp1.algorithms.transformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cg.tp1.model.Circle;
import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Point;
import cg.tp1.model.Polygon;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransformationsTest {

    private static final double EPS = 1e-9;

    private static void assertPoint(double x, double y, Point actual) {
        assertEquals(x, actual.x(), EPS, "x de " + actual);
        assertEquals(y, actual.y(), EPS, "y de " + actual);
    }

    @Test
    void translationAddsOffsets() {
        assertPoint(7, -1, Transformations.translation(5, -3).apply(new Point(2, 2)));
    }

    @Test
    void rotationOf90DegreesIsCounterClockwise() {
        // (1, 0) gira para (0, 1): sentido anti-horário com Y para cima
        assertPoint(0, 1, Transformations.rotation(90).apply(new Point(1, 0)));
        assertPoint(-1, 0, Transformations.rotation(90).apply(new Point(0, 1)));
    }

    @Test
    void scaleMultipliesCoordinates() {
        assertPoint(6, -2, Transformations.scale(2, 0.5).apply(new Point(3, -4)));
    }

    @Test
    void reflections() {
        Point p = new Point(3, 4);
        assertPoint(3, -4, Transformations.reflectionX().apply(p));
        assertPoint(-3, 4, Transformations.reflectionY().apply(p));
        assertPoint(-3, -4, Transformations.reflectionXY().apply(p));
    }

    @Test
    void reflectionXYEqualsRotationOf180() {
        Point p = new Point(3, 4);
        Point reflected = Transformations.reflectionXY().apply(p);
        Point rotated = Transformations.rotation(180).apply(p);
        assertPoint(reflected.x(), reflected.y(), rotated);
    }

    @Test
    void pivotStaysFixed() {
        Point pivot = new Point(5, 5);
        Matrix3 m = Transformations.aroundPivot(Transformations.rotation(37), pivot);
        assertPoint(5, 5, m.apply(pivot));
    }

    @Test
    void rotationAroundPivot() {
        // 180° em torno de (5, 5): (6, 5) vai para (4, 5)
        Matrix3 m = Transformations.aroundPivot(Transformations.rotation(180), new Point(5, 5));
        assertPoint(4, 5, m.apply(new Point(6, 5)));
    }

    @Test
    void compositionOrderMatters() {
        // T·R (gira e depois translada) é diferente de R·T (translada e depois gira)
        Matrix3 t = Transformations.translation(10, 0);
        Matrix3 r = Transformations.rotation(90);
        Point p = new Point(1, 0);
        assertPoint(10, 1, t.multiply(r).apply(p));
        assertPoint(0, 11, r.multiply(t).apply(p));
    }

    @Test
    void fourRotationsOf90ReturnToStartWithoutDrift() {
        Polygon triangle = new Polygon(List.of(new Point(0, 0), new Point(10, 0), new Point(0, 7)),
                0xFF000000, LineAlgorithm.DDA);
        Matrix3 r = Transformations.aroundPivot(Transformations.rotation(90), new Point(3, 2));
        for (int i = 0; i < 4; i++) {
            triangle.transform(r::apply);
        }
        assertPoint(10, 0, triangle.vertices().get(1));
        assertPoint(0, 7, triangle.vertices().get(2));
    }

    @Test
    void circleRadiusFollowsUniformScaleAndIsKeptByRotation() {
        Circle circle = new Circle(new Point(2, 0), 5, 0xFF000000);

        circle.transform(Transformations.scale(2, 2)::apply);
        assertPoint(4, 0, circle.center());
        assertEquals(10, circle.radius(), EPS);

        circle.transform(Transformations.rotation(90)::apply);
        assertPoint(0, 4, circle.center());
        assertEquals(10, circle.radius(), EPS);
    }
}
