package cg.tp1.ui.tools;

import cg.tp1.model.Point;
import cg.tp1.model.Shape;
import java.util.List;

/**
 * Ferramenta ativa na Área de Desenho (padrão Strategy): cada ferramenta
 * decide o que fazer com os eventos do mouse. O editor apenas repassa os
 * eventos, já convertidos para coordenadas cartesianas.
 */
public interface Tool {

    /** Nome exibido no botão da barra de ferramentas. */
    String name();

    /** Instrução mostrada na barra de status enquanto a ferramenta está ativa. */
    String hint();

    /** Clique do mouse. {@code primary} = botão esquerdo; caso contrário, direito. */
    void mousePressed(Point point, boolean primary);

    default void mouseMoved(Point point) {
    }

    /** Por padrão, arrastar se comporta como mover (a pré-visualização acompanha o mouse). */
    default void mouseDragged(Point point) {
        mouseMoved(point);
    }

    default void mouseReleased(Point point) {
    }

    /** Descarta qualquer operação em andamento (ex.: ao trocar de ferramenta). */
    default void reset() {
    }

    /** Figuras temporárias exibidas enquanto a operação não termina. */
    default List<Shape> preview() {
        return List.of();
    }
}
