package cg.tp1.algorithms.clipping;

import cg.tp1.model.Point;
import cg.tp1.model.Rectangle;
import java.util.Optional;

/**
 * Recorte de retas pela equação paramétrica (Liang-Barsky).
 *
 * A reta é escrita como P(u) = P1 + u·(P2 − P1), com 0 ≤ u ≤ 1:
 *   x(u) = x1 + u·dx,   y(u) = y1 + u·dy.
 *
 * Estar dentro da janela equivale a 4 desigualdades da forma u·p[k] ≤ q[k]:
 *   k = 0 (esquerda):  p = −dx,  q = x1 − xmin
 *   k = 1 (direita):   p =  dx,  q = xmax − x1
 *   k = 2 (inferior):  p = −dy,  q = y1 − ymin
 *   k = 3 (superior):  p =  dy,  q = ymax − y1
 *
 * Para cada borda:
 *  - p = 0 → reta paralela à borda; se q < 0 está fora → rejeita;
 *  - p < 0 → a reta ENTRA pela borda em u = q/p → u1 = max(u1, q/p);
 *  - p > 0 → a reta SAI pela borda em u = q/p   → u2 = min(u2, q/p).
 * Se u1 > u2, não há parte visível. Senão, o trecho visível vai de P(u1) a P(u2).
 */
public final class LiangBarsky implements LineClipper {

    @Override
    public Optional<Segment> clip(Point p1, Point p2, Rectangle w) {
        double x1 = p1.x();
        double y1 = p1.y();
        double dx = p2.x() - x1;
        double dy = p2.y() - y1;

        double[] p = {-dx, dx, -dy, dy};
        double[] q = {x1 - w.minX(), w.maxX() - x1, y1 - w.minY(), w.maxY() - y1};

        double u1 = 0.0; // maior parâmetro de entrada
        double u2 = 1.0; // menor parâmetro de saída

        for (int k = 0; k < 4; k++) {
            if (p[k] == 0) {
                if (q[k] < 0) {
                    return Optional.empty(); // paralela à borda e do lado de fora
                }
            } else {
                double u = q[k] / p[k];
                if (p[k] < 0) {
                    u1 = Math.max(u1, u); // entrando
                } else {
                    u2 = Math.min(u2, u); // saindo
                }
                if (u1 > u2) {
                    return Optional.empty(); // sai antes de entrar: fora da janela
                }
            }
        }

        Point start = new Point(x1 + u1 * dx, y1 + u1 * dy);
        Point end = new Point(x1 + u2 * dx, y1 + u2 * dy);
        return Optional.of(new Segment(start, end));
    }
}
