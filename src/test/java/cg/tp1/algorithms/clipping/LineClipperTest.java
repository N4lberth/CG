package cg.tp1.algorithms.clipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cg.tp1.model.Point;
import cg.tp1.model.Rectangle;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Os mesmos casos rodam para Cohen-Sutherland e Liang-Barsky.
 * Janela usada: x ∈ [0, 10], y ∈ [0, 10].
 */
class LineClipperTest {

    private static final double EPS = 1e-9;
    private static final Rectangle WINDOW = new Rectangle(0, 0, 10, 10);

    static Stream<Arguments> clippers() {
        return Stream.of(
                Arguments.of("Cohen-Sutherland", new CohenSutherland()),
                Arguments.of("Liang-Barsky", new LiangBarsky()));
    }

    private static Optional<Segment> clip(LineClipper c, double x1, double y1, double x2, double y2) {
        return c.clip(new Point(x1, y1), new Point(x2, y2), WINDOW);
    }

    private static void assertSegment(double x1, double y1, double x2, double y2, Optional<Segment> actual) {
        assertTrue(actual.isPresent(), "Esperava um segmento visível");
        Segment s = actual.get();
        assertEquals(x1, s.start().x(), EPS);
        assertEquals(y1, s.start().y(), EPS);
        assertEquals(x2, s.end().x(), EPS);
        assertEquals(y2, s.end().y(), EPS);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void segmentInsideIsUnchanged(String name, LineClipper c) {
        assertSegment(2, 2, 8, 5, clip(c, 2, 2, 8, 5));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void segmentEntirelyOnOneSideIsRejected(String name, LineClipper c) {
        assertTrue(clip(c, -5, -5, -1, 20).isEmpty()); // tudo à esquerda
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void crossingOneBorder(String name, LineClipper c) {
        assertSegment(5, 5, 10, 5, clip(c, 5, 5, 15, 5));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void crossingTwoBorders(String name, LineClipper c) {
        assertSegment(0, 5, 10, 5, clip(c, -5, 5, 15, 5));
        assertSegment(0, 0, 10, 10, clip(c, -5, -5, 15, 15));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void outsideButNotTriviallyRejected(String name, LineClipper c) {
        // (−3, 8) está à ESQUERDA e (3, 14) está ACIMA: códigos 0001 e 1000, AND = 0.
        // A reta y = x + 11 passa acima do canto (0, 10): nenhuma parte visível.
        assertTrue(clip(c, -3, 8, 3, 14).isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void verticalAndHorizontalLines(String name, LineClipper c) {
        assertSegment(5, 0, 5, 10, clip(c, 5, -5, 5, 15));
        assertTrue(clip(c, 12, 0, 12, 10).isEmpty());   // paralela à borda direita, fora
        assertTrue(clip(c, 0, -1, 10, -1).isEmpty());   // paralela à borda inferior, fora
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("clippers")
    void directionIsPreserved(String name, LineClipper c) {
        assertSegment(10, 5, 5, 5, clip(c, 15, 5, 5, 5));
    }

    @Test
    void regionCodes() {
        assertEquals(CohenSutherland.INSIDE, CohenSutherland.regionCode(5, 5, WINDOW));
        assertEquals(CohenSutherland.TOP | CohenSutherland.LEFT, CohenSutherland.regionCode(-1, 11, WINDOW));
        assertEquals(CohenSutherland.BOTTOM | CohenSutherland.RIGHT, CohenSutherland.regionCode(11, -1, WINDOW));
        assertEquals(CohenSutherland.INSIDE, CohenSutherland.regionCode(10, 0, WINDOW)); // borda conta como dentro
    }

    @Test
    void bothAlgorithmsAgreeOnRandomSegments() {
        Random random = new Random(42);
        LineClipper cs = new CohenSutherland();
        LineClipper lb = new LiangBarsky();
        for (int i = 0; i < 1000; i++) {
            Point a = new Point(random.nextDouble() * 40 - 15, random.nextDouble() * 40 - 15);
            Point b = new Point(random.nextDouble() * 40 - 15, random.nextDouble() * 40 - 15);
            Optional<Segment> r1 = cs.clip(a, b, WINDOW);
            Optional<Segment> r2 = lb.clip(a, b, WINDOW);
            assertEquals(r1.isPresent(), r2.isPresent(), "Divergência em " + a + " → " + b);
            if (r1.isPresent()) {
                Segment s = r2.get();
                assertSegment(s.start().x(), s.start().y(), s.end().x(), s.end().y(), r1);
            }
        }
    }
}
