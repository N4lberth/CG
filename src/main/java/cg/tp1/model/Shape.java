package cg.tp1.model;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Objeto geométrico da cena.
 *
 * A interface é {@code sealed}: só as quatro figuras abaixo podem implementá-la.
 * Assim o compilador garante que o renderizador (e, futuramente, a seleção e o
 * recorte) tratem todos os tipos de figura.
 */
public sealed interface Shape permits PointShape, Line, Polygon, Circle {

    /** Pontos que definem a figura. */
    List<Point> vertices();

    /** Cor de desenho no formato ARGB. */
    int color();

    /**
     * Caixa envolvente: menor retângulo que contém a figura.
     * Para retas, pontos e polígonos basta envolver os vértices;
     * a circunferência sobrescreve este método.
     */
    default Rectangle boundingBox() {
        return Rectangle.enclosing(vertices());
    }

    /**
     * Aplica uma transformação geométrica a todos os pontos da figura.
     * O modelo recebe apenas uma função Ponto → Ponto; quem monta a matriz
     * é o pacote algorithms.transformation.
     */
    void transform(UnaryOperator<Point> mapping);

    /**
     * Indica se o ponto está no INTERIOR da figura. Só figuras fechadas
     * (polígono e circunferência) têm interior; usado para descobrir a qual
     * objeto pertence a semente de um preenchimento.
     */
    default boolean contains(Point p) {
        return false;
    }
}
