package cg.tp1.algorithms.filling;

import cg.tp1.model.Connectivity;
import cg.tp1.raster.Pixel;
import cg.tp1.raster.PixelBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Boundary-Fill (preenchimento por fronteira).
 *
 * A partir da semente, pinta todo pixel alcançável que NÃO tenha a cor da
 * fronteira nem a cor de preenchimento. A região é delimitada por uma cor
 * específica (a fronteira); o que houver dentro dela, de qualquer cor, é pintado.
 *
 * Versão recursiva vista em aula:
 * <pre>
 * boundaryFill(x, y):
 *     cor = getPixel(x, y)
 *     se cor != fronteira e cor != preenchimento:
 *         setPixel(x, y, preenchimento)
 *         para cada vizinho (4 ou 8): boundaryFill(vizinho)
 * </pre>
 * Aqui a recursão é substituída por uma PILHA explícita: a lógica é a mesma,
 * mas não estoura a pilha de chamadas do Java (StackOverflowError) em regiões grandes.
 */
public final class BoundaryFill {

    private BoundaryFill() {
    }

    /** @return quantidade de pixels pintados */
    public static int fill(PixelBuffer buffer, int seedX, int seedY,
                           int fillColor, int boundaryColor, Connectivity connectivity) {
        int painted = 0;
        Deque<Pixel> stack = new ArrayDeque<>();
        stack.push(new Pixel(seedX, seedY));

        while (!stack.isEmpty()) {
            Pixel p = stack.pop();
            if (!buffer.contains(p.x(), p.y())) {
                continue; // saiu da Área de Desenho
            }
            int color = buffer.get(p.x(), p.y());
            if (color == boundaryColor || color == fillColor) {
                continue; // chegou à fronteira ou já foi pintado
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
