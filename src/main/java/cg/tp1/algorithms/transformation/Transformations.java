package cg.tp1.algorithms.transformation;

import cg.tp1.model.Point;

/**
 * Matrizes das transformações geométricas 2D (coordenadas homogêneas).
 * Os fatores sempre vêm da interface; nenhum valor é fixo.
 */
public final class Transformations {

    private Transformations() {
    }

    /** Translação: x' = x + dx, y' = y + dy. */
    public static Matrix3 translation(double dx, double dy) {
        return new Matrix3(new double[][] {
                {1, 0, dx},
                {0, 1, dy},
                {0, 0, 1}});
    }

    /**
     * Rotação em torno da origem, ângulo em graus.
     * Ângulo positivo = sentido anti-horário (eixo Y para cima).
     * x' = x·cosθ − y·senθ,  y' = x·senθ + y·cosθ.
     */
    public static Matrix3 rotation(double degrees) {
        double theta = Math.toRadians(degrees);
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Matrix3(new double[][] {
                {cos, -sin, 0},
                {sin, cos, 0},
                {0, 0, 1}});
    }

    /** Escala em relação à origem: x' = x·sx, y' = y·sy. */
    public static Matrix3 scale(double sx, double sy) {
        return new Matrix3(new double[][] {
                {sx, 0, 0},
                {0, sy, 0},
                {0, 0, 1}});
    }

    /** Reflexão em relação ao eixo X: (x, y) → (x, −y). */
    public static Matrix3 reflectionX() {
        return scale(1, -1);
    }

    /** Reflexão em relação ao eixo Y: (x, y) → (−x, y). */
    public static Matrix3 reflectionY() {
        return scale(-1, 1);
    }

    /** Reflexão em relação aos dois eixos (à origem): (x, y) → (−x, −y). */
    public static Matrix3 reflectionXY() {
        return scale(-1, -1);
    }

    /**
     * Aplica {@code transform} tomando {@code pivot} como referência, em vez da origem.
     * Composição (lida da direita para a esquerda):
     * 1) T(−pivot): leva o pivô para a origem;
     * 2) transform: rotação, escala ou reflexão;
     * 3) T(+pivot): devolve o pivô ao lugar.
     */
    public static Matrix3 aroundPivot(Matrix3 transform, Point pivot) {
        return translation(pivot.x(), pivot.y())
                .multiply(transform)
                .multiply(translation(-pivot.x(), -pivot.y()));
    }
}
