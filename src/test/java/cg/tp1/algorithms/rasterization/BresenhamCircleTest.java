package cg.tp1.algorithms.rasterization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cg.tp1.raster.Pixel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BresenhamCircleTest {

    private final BresenhamCircle circle = new BresenhamCircle();

    @Test
    void matchesHandCalculatedOctantForRadius5() {
        // d = 3 − 2·5 = −7
        // (0,5) d<0  → d = −7 + 4·0 + 6 = −1
        // (1,5) d<0  → d = −1 + 4·1 + 6 = 9
        // (2,5) d>=0 → d = 9 + 4·(2−5) + 10 = 7, y = 4
        // (3,4) d>=0 → d = 7 + 4·(3−4) + 10 = 13, y = 3 → x = 4 > y: fim
        Set<Pixel> pixels = new HashSet<>(circle.rasterize(0, 0, 5));
        for (Pixel p : List.of(new Pixel(0, 5), new Pixel(1, 5), new Pixel(2, 5), new Pixel(3, 4))) {
            assertTrue(pixels.contains(p), "Faltou " + p);
        }
    }

    @Test
    void pixelsAreCloseToTheIdealCircle() {
        int radius = 20;
        for (Pixel p : circle.rasterize(0, 0, radius)) {
            double distance = Math.hypot(p.x(), p.y());
            assertTrue(Math.abs(distance - radius) < 1.0, p + " está a " + distance + " do centro");
        }
    }

    @Test
    void resultIsSymmetricInAllEightOctants() {
        Set<Pixel> pixels = new HashSet<>(circle.rasterize(0, 0, 17));
        for (Pixel p : pixels) {
            assertTrue(pixels.contains(new Pixel(-p.x(), p.y())));
            assertTrue(pixels.contains(new Pixel(p.x(), -p.y())));
            assertTrue(pixels.contains(new Pixel(p.y(), p.x())));
        }
    }

    @Test
    void centerIsApplied() {
        Set<Pixel> pixels = new HashSet<>(circle.rasterize(10, -4, 3));
        assertTrue(pixels.contains(new Pixel(10, -1)));  // topo
        assertTrue(pixels.contains(new Pixel(13, -4)));  // direita
        assertTrue(pixels.contains(new Pixel(10, -7)));  // base
        assertTrue(pixels.contains(new Pixel(7, -4)));   // esquerda
    }

    @Test
    void zeroRadiusIsASinglePixel() {
        assertEquals(Set.of(new Pixel(2, 2)), new HashSet<>(circle.rasterize(2, 2, 0)));
    }
}
