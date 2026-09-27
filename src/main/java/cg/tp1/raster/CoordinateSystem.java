package cg.tp1.raster;

/**
 * Conversão entre os dois sistemas de coordenadas usados na aplicação:
 *
 * <pre>
 *  Cartesiano (algoritmos)            Matriz de pixels (tela)
 *
 *          y                          (0,0) ───────► coluna
 *          ▲                            │
 *          │                            │
 *   ───────┼───────► x                  ▼
 *          │ (0,0) no centro           linha
 * </pre>
 *
 * No sistema cartesiano o eixo Y cresce para cima e a origem fica no centro
 * da área de desenho. Na matriz, a linha 0 é a de cima e as linhas crescem
 * para baixo. Toda a inversão do eixo Y acontece somente nesta classe.
 */
public final class CoordinateSystem {

    private final int columns;
    private final int rows;
    private final int originColumn;
    private final int originRow;

    public CoordinateSystem(int columns, int rows) {
        if (columns <= 0 || rows <= 0) {
            throw new IllegalArgumentException("Dimensões da matriz devem ser positivas");
        }
        this.columns = columns;
        this.rows = rows;
        this.originColumn = columns / 2;
        this.originRow = rows / 2;
    }

    // ---------- cartesiano -> matriz ----------

    public int toColumn(int x) {
        return x + originColumn;
    }

    public int toRow(int y) {
        return originRow - y; // inverte o eixo Y
    }

    // ---------- matriz -> cartesiano ----------

    public int toCartesianX(int column) {
        return column - originColumn;
    }

    public int toCartesianY(int row) {
        return originRow - row; // inverte o eixo Y
    }

    /** Indica se o ponto cartesiano (x, y) cai dentro da matriz. */
    public boolean contains(int x, int y) {
        int column = toColumn(x);
        int row = toRow(y);
        return column >= 0 && column < columns && row >= 0 && row < rows;
    }

    // ---------- limites da área em coordenadas cartesianas ----------

    public int minX() {
        return toCartesianX(0);
    }

    public int maxX() {
        return toCartesianX(columns - 1);
    }

    public int minY() {
        return toCartesianY(rows - 1);
    }

    public int maxY() {
        return toCartesianY(0);
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }
}
