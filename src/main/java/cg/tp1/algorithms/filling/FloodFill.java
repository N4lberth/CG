package cg.tp1.algorithms.filling;

import cg.tp1.model.Connectivity;
import cg.tp1.raster.Pixel;
import cg.tp1.raster.PixelBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Flood-Fill (preenchimento por inundação).
 *
 * A cor ALVO é a cor da semente no momento do clique. O algoritmo substitui
 * por fillColor todo pixel alcançável que tenha exatamente a cor alvo. Qualquer
 * outra cor (de qualquer objeto) funciona como barreira.
 *
 * Diferença para o Boundary-Fill: aqui não se informa a cor da fronteira; a
 * região é definida pela cor que ela JÁ tem.
 *
 * Versão recursiva vista em aula:
 * <pre>
 * floodFill(x, y):
 *     se getPixel(x, y) == alvo:
 *         setPixel(x, y, preenchimento)
 *         para cada vizinho (4 ou 8): floodFill(vizinho)
 * </pre>
 * Implementado com PILHA explícita (mesma lógica, sem estourar a pilha do Java).
 */
public final class FloodFill {

    private FloodFill() {
    }

    /** @return quantidade de pixels pintados */
    public static int fill(PixelBuffer buffer, int seedX, int seedY,
                           int fillColor, Connectivity connectivity) {
        if (!buffer.contains(seedX, seedY)) {
            return 0;
        }
        int targetColor = buffer.get(seedX, seedY);
        if (targetColor == fillColor) {
            return 0; // já tem a cor desejada; sem esta checagem o laço nunca terminaria
        }

        int painted = 0;
        Deque<Pixel> stack = new ArrayDeque<>();
        stack.push(new Pixel(seedX, seedY));

        while (!stack.isEmpty()) {
            Pixel p = stack.pop();
            if (!buffer.contains(p.x(), p.y()) || buffer.get(p.x(), p.y()) != targetColor) {
                continue; // fora da área ou cor diferente da cor alvo
            }

            buffer.set(p.x(), p.y(), fillColor);
            painted++;

            for (int[] offset : connectivity.offsets()) {
                stack.push(new Pixel(p.x() + offset[0], p.y() + offset[1]));
            }
        }
        return painted;
    }
}
