package cg.tp1.algorithms.rasterization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cg.tp1.raster.Pixel;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testes comuns a DDA e Bresenham. Os mesmos casos rodam para os dois algoritmos.
 */
class LineRasterizerTest {

    static Stream<Arguments> rasterizers() {
        return Stream.of(
                Arguments.of("DDA", new DDALine()),
                Arguments.of("Bresenham", new BresenhamLine()));
    }

    /** Extremos cobrindo os 8 octantes + retas horizontais, verticais e diagonais. */
    private static final int[][] SEGMENTS = {
            {0, 0, 8, 3}, {0, 0, 3, 8}, {0, 0, -3, 8}, {0, 0, -8, 3},
            {0, 0, -8, -3}, {0, 0, -3, -8}, {0, 0, 3, -8}, {0, 0, 8, -3},
            {-5, 2, 10, 2}, {4, -6, 4, 9}, {0, 0, 7, 7}, {0, 0, -7, 7},
    };

    @ParameterizedTest(name = "{0}")
    @MethodSource("rasterizers")
    void startsAndEndsAtTheEndpoints(String name, LineRasterizer rasterizer) {
        for (int[] s : SEGMENTS) {
            List<Pixel> pixels = rasterizer.rasterize(s[0], s[1], s[2], s[3]);
            assertEquals(new Pixel(s[0], s[1]), pixels.get(0));
            assertEquals(new Pixel(s[2], s[3]), pixels.get(pixels.size() - 1));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rasterizers")
    void producesOnePixelPerStepOfTheLongestAxis(String name, LineRasterizer rasterizer) {
        for (int[] s : SEGMENTS) {
            int steps = Math.max(Math.abs(s[2] - s[0]), Math.abs(s[3] - s[1]));
            assertEquals(steps + 1, rasterizer.rasterize(s[0], s[1], s[2], s[3]).size());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rasterizers")
    void consecutivePixelsAreNeighbours(String name, LineRasterizer rasterizer) {
        for (int[] s : SEGMENTS) {
            List<Pixel> pixels = rasterizer.rasterize(s[0], s[1], s[2], s[3]);
            for (int i = 1; i < pixels.size(); i++) {
                Pixel a = pixels.get(i - 1);
                Pixel b = pixels.get(i);
                assertTrue(Math.abs(a.x() - b.x()) <= 1 && Math.abs(a.y() - b.y()) <= 1,
                        "Buraco entre " + a + " e " + b);
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rasterizers")
    void degenerateLineIsASinglePixel(String name, LineRasterizer rasterizer) {
        assertEquals(List.of(new Pixel(3, 4)), rasterizer.rasterize(3, 4, 3, 4));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rasterizers")
    void matchesHandCalculatedExample(String name, LineRasterizer rasterizer) {
        // (0,0) -> (8,3): y ideal = 3x/8 -> 0; 0,375; 0,75; 1,125; 1,5; 1,875; 2,25; 2,625; 3
        List<Pixel> expected = List.of(
                new Pixel(0, 0), new Pixel(1, 0), new Pixel(2, 1), new Pixel(3, 1), new Pixel(4, 2),
                new Pixel(5, 2), new Pixel(6, 2), new Pixel(7, 3), new Pixel(8, 3));
        assertEquals(expected, rasterizer.rasterize(0, 0, 8, 3));
    }
}
