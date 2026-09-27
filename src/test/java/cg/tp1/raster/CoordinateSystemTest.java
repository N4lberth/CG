package cg.tp1.raster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CoordinateSystemTest {

    private final CoordinateSystem cs = new CoordinateSystem(200, 150);

    @Test
    void originIsAtCenterOfMatrix() {
        assertEquals(100, cs.toColumn(0));
        assertEquals(75, cs.toRow(0));
    }

    @Test
    void yAxisIsInverted() {
        // subir no cartesiano = diminuir a linha da matriz
        assertEquals(cs.toRow(0) - 10, cs.toRow(10));
    }

    @Test
    void conversionIsReversible() {
        for (int x = cs.minX(); x <= cs.maxX(); x++) {
            assertEquals(x, cs.toCartesianX(cs.toColumn(x)));
        }
        for (int y = cs.minY(); y <= cs.maxY(); y++) {
            assertEquals(y, cs.toCartesianY(cs.toRow(y)));
        }
    }

    @Test
    void limitsMatchMatrixBorders() {
        assertTrue(cs.contains(cs.minX(), cs.minY()));
        assertTrue(cs.contains(cs.maxX(), cs.maxY()));
        assertFalse(cs.contains(cs.maxX() + 1, 0));
        assertFalse(cs.contains(0, cs.maxY() + 1));
    }

    @Test
    void pixelBufferWritesInCartesianCoordinates() {
        PixelBuffer buffer = new PixelBuffer(cs, PixelBuffer.WHITE);
        buffer.set(0, 0, 0xFF000000);
        assertEquals(0xFF000000, buffer.getByIndex(75, 100));
        buffer.set(1000, 1000, 0xFF000000); // fora da área: ignorado, sem exceção
    }
}
