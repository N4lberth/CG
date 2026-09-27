package cg.tp1.algorithms.clipping;

import cg.tp1.model.Point;
import cg.tp1.model.Rectangle;
import java.util.Optional;

/**
 * Recorte de retas por regiões codificadas (Cohen-Sutherland).
 *
 * O plano é dividido em 9 regiões em torno da janela, e cada extremo recebe
 * um código de 4 bits:
 * <pre>
 *            LEFT       RIGHT
 *   TOP     1001 | 1000 | 1010
 *          ------+------+------
 *           0001 | 0000 | 0010      0000 = dentro da janela
 *          ------+------+------
 *   BOTTOM  0101 | 0100 | 0110
 * </pre>
 * Bits: TOP = 8, BOTTOM = 4, RIGHT = 2, LEFT = 1.
 *
 * Repete até decidir:
 *  - c1 | c2 == 0 → os dois extremos dentro: aceita (aceitação trivial);
 *  - c1 & c2 != 0 → os dois do mesmo lado de fora: rejeita (rejeição trivial);
 *  - senão → move um extremo que está fora para a interseção com a borda
 *    correspondente e recalcula o código.
 */
public final class CohenSutherland implements LineClipper {

    static final int INSIDE = 0; // 0000
    static final int LEFT = 1;   // 0001
    static final int RIGHT = 2;  // 0010
    static final int BOTTOM = 4; // 0100
    static final int TOP = 8;    // 1000

    /** Código de região de um ponto em relação à janela. */
    static int regionCode(double x, double y, Rectangle w) {
        int code = INSIDE;
        if (x < w.minX()) {
            code |= LEFT;
        } else if (x > w.maxX()) {
            code |= RIGHT;
        }
        if (y < w.minY()) {
            code |= BOTTOM;
        } else if (y > w.maxY()) {
            code |= TOP;
        }
        return code;
    }

    @Override
    public Optional<Segment> clip(Point p1, Point p2, Rectangle w) {
        double x1 = p1.x();
        double y1 = p1.y();
        double x2 = p2.x();
        double y2 = p2.y();
        int code1 = regionCode(x1, y1, w);
        int code2 = regionCode(x2, y2, w);

        while (true) {
            if ((code1 | code2) == 0) {
                // Aceitação trivial: os dois extremos estão dentro
                return Optional.of(new Segment(new Point(x1, y1), new Point(x2, y2)));
            }
            if ((code1 & code2) != 0) {
                // Rejeição trivial: os dois estão do mesmo lado, fora da janela
                return Optional.empty();
            }

            // Escolhe um extremo que está fora da janela
            int outside = code1 != 0 ? code1 : code2;
            double x;
            double y;

            // Interseção com a borda indicada pelo bit, usando a equação da reta
            if ((outside & TOP) != 0) {
                x = x1 + (x2 - x1) * (w.maxY() - y1) / (y2 - y1);
                y = w.maxY();
            } else if ((outside & BOTTOM) != 0) {
                x = x1 + (x2 - x1) * (w.minY() - y1) / (y2 - y1);
                y = w.minY();
            } else if ((outside & RIGHT) != 0) {
                y = y1 + (y2 - y1) * (w.maxX() - x1) / (x2 - x1);
                x = w.maxX();
            } else { // LEFT
                y = y1 + (y2 - y1) * (w.minX() - x1) / (x2 - x1);
                x = w.minX();
            }

            // Substitui o extremo de fora pela interseção e recalcula o código
            if (outside == code1) {
                x1 = x;
                y1 = y;
                code1 = regionCode(x1, y1, w);
            } else {
                x2 = x;
                y2 = y;
                code2 = regionCode(x2, y2, w);
            }
        }
    }
}
