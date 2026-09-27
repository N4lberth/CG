package cg.tp1.algorithms.transformation;

import cg.tp1.model.Point;

/**
 * Matriz 3×3 para transformações 2D em coordenadas homogêneas.
 *
 * Um ponto (x, y) é tratado como o vetor coluna [x, y, 1]. Com a terceira
 * coordenada, a translação também vira uma multiplicação de matrizes, e
 * várias transformações podem ser combinadas numa única matriz.
 *
 * Imutável: as operações devolvem uma nova matriz.
 */
public final class Matrix3 {

    private final double[][] m;

    public Matrix3(double[][] values) {
        if (values.length != 3 || values[0].length != 3 || values[1].length != 3 || values[2].length != 3) {
            throw new IllegalArgumentException("A matriz deve ser 3×3");
        }
        this.m = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(values[i], 0, m[i], 0, 3);
        }
    }

    public static Matrix3 identity() {
        return new Matrix3(new double[][] {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}});
    }

    /**
     * Produto this × other.
     * Aplicar o resultado a um ponto equivale a aplicar primeiro {@code other}
     * e depois {@code this}.
     */
    public Matrix3 multiply(Matrix3 other) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += m[i][k] * other.m[k][j];
                }
            }
        }
        return new Matrix3(result);
    }

    /**
     * Aplica a matriz ao ponto:
     * <pre>
     * | x' |   | m00 m01 m02 |   | x |
     * | y' | = | m10 m11 m12 | · | y |
     * | 1  |   |  0   0   1  |   | 1 |
     * </pre>
     */
    public Point apply(Point p) {
        double x = m[0][0] * p.x() + m[0][1] * p.y() + m[0][2];
        double y = m[1][0] * p.x() + m[1][1] * p.y() + m[1][2];
        return new Point(x, y);
    }

    public double get(int row, int column) {
        return m[row][column];
    }

    /** Matriz formatada em 3 linhas (exibida na interface após cada transformação). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : m) {
            sb.append(String.format("| %7.2f %7.2f %7.2f |%n", clean(row[0]), clean(row[1]), clean(row[2])));
        }
        return sb.toString().stripTrailing();
    }

    /** Evita exibir "-0,00" por causa de erros de arredondamento. */
    private static double clean(double value) {
        return Math.abs(value) < 1e-9 ? 0.0 : value;
    }
}
