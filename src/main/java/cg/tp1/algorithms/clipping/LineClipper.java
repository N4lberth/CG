package cg.tp1.algorithms.clipping;

import cg.tp1.model.Point;
import cg.tp1.model.Rectangle;
import java.util.Optional;

/**
 * Contrato comum dos algoritmos de recorte de retas.
 *
 * @return a parte do segmento dentro da janela, ou vazio se o segmento
 *         estiver inteiramente fora dela
 */
public interface LineClipper {

    Optional<Segment> clip(Point p1, Point p2, Rectangle window);
}
