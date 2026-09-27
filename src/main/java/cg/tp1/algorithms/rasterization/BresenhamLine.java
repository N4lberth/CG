package cg.tp1.algorithms.rasterization;

import cg.tp1.raster.Pixel;
import java.util.ArrayList;
import java.util.List;

/**
 * Bresenham para retas.
 *
 * Ideia: andar 1 pixel por vez no eixo de maior variação e decidir, usando
 * apenas aritmética INTEIRA, se o outro eixo também avança. A decisão usa o
 * parâmetro p, que mede de que lado da reta ideal está o ponto médio entre os
 * dois pixels candidatos.
 *
 * Funciona em todos os octantes:
 *  - incX / incY (±1) tratam retas que vão para a esquerda ou para baixo;
 *  - os dois casos (|dx| > |dy| e |dy| >= |dx|) tratam inclinações menores
 *    e maiores que 1, trocando o papel dos eixos.
 */
public final class BresenhamLine implements LineRasterizer {

    @Override
    public List<Pixel> rasterize(int x1, int y1, int x2, int y2) {
        List<Pixel> pixels = new ArrayList<>();

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Direção do passo em cada eixo
        int incX = dx >= 0 ? 1 : -1;
        int incY = dy >= 0 ? 1 : -1;
        dx = Math.abs(dx);
        dy = Math.abs(dy);

        int x = x1;
        int y = y1;
        pixels.add(new Pixel(x, y));

        if (dx > dy) {
            // Caso 1: |inclinação| < 1 -> x avança sempre, y às vezes
            int p = 2 * dy - dx;
            int c1 = 2 * dy;            // incremento de p quando y NÃO muda
            int c2 = 2 * (dy - dx);     // incremento de p quando y muda
            for (int i = 0; i < dx; i++) {
                x += incX;
                if (p < 0) {
                    p += c1;
                } else {
                    y += incY;
                    p += c2;
                }
                pixels.add(new Pixel(x, y));
            }
        } else {
            // Caso 2: |inclinação| >= 1 -> y avança sempre, x às vezes
            int p = 2 * dx - dy;
            int c1 = 2 * dx;
            int c2 = 2 * (dx - dy);
            for (int i = 0; i < dy; i++) {
                y += incY;
                if (p < 0) {
                    p += c1;
                } else {
                    x += incX;
                    p += c2;
                }
                pixels.add(new Pixel(x, y));
            }
        }
        return pixels;
    }
}
