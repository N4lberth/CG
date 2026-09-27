package cg.tp1.algorithms.rasterization;

import cg.tp1.raster.Pixel;
import java.util.ArrayList;
import java.util.List;

/**
 * Bresenham para circunferências (parâmetro de decisão inicial d = 3 − 2r).
 *
 * Ideia: calcular apenas o 2º octante (de x = 0 até x = y, partindo do topo
 * (0, r)) e obter os outros 7 octantes por simetria. A cada passo x aumenta 1
 * e o parâmetro d decide se y se mantém ou diminui 1.
 */
public final class BresenhamCircle {

    public List<Pixel> rasterize(int xc, int yc, int radius) {
        List<Pixel> pixels = new ArrayList<>();

        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        while (x <= y) {
            addSymmetricPoints(pixels, xc, yc, x, y);
            if (d < 0) {
                // pixel de cima (y mantém)
                d = d + 4 * x + 6;
            } else {
                // pixel diagonal (y diminui)
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
        return pixels;
    }

    /** Simetria de 8 pontos: (±x, ±y) e (±y, ±x) deslocados para o centro. */
    private void addSymmetricPoints(List<Pixel> pixels, int xc, int yc, int x, int y) {
        pixels.add(new Pixel(xc + x, yc + y));
        pixels.add(new Pixel(xc - x, yc + y));
        pixels.add(new Pixel(xc + x, yc - y));
        pixels.add(new Pixel(xc - x, yc - y));
        pixels.add(new Pixel(xc + y, yc + x));
        pixels.add(new Pixel(xc - y, yc + x));
        pixels.add(new Pixel(xc + y, yc - x));
        pixels.add(new Pixel(xc - y, yc - x));
    }
}
