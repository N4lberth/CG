package cg.tp1.algorithms.rasterization;

import cg.tp1.raster.Pixel;
import java.util.ArrayList;
import java.util.List;

/**
 * DDA (Digital Differential Analyzer).
 *
 * Ideia: andar sempre 1 pixel no eixo de maior variação e somar um incremento
 * fracionário no outro eixo. Usa aritmética de ponto flutuante e arredonda
 * a posição a cada passo.
 */
public final class DDALine implements LineRasterizer {

    @Override
    public List<Pixel> rasterize(int x1, int y1, int x2, int y2) {
        List<Pixel> pixels = new ArrayList<>();

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Número de passos = maior variação entre os eixos
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) { // extremos iguais: a reta é um único pixel
            pixels.add(new Pixel(x1, y1));
            return pixels;
        }

        // Incremento por passo: um deles vale ±1, o outro é fracionário (|inc| <= 1)
        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;
        pixels.add(new Pixel((int) Math.round(x), (int) Math.round(y)));

        for (int k = 1; k <= steps; k++) {
            x += xIncrement;
            y += yIncrement;
            pixels.add(new Pixel((int) Math.round(x), (int) Math.round(y)));
        }
        return pixels;
    }
}
