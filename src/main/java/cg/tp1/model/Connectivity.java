package cg.tp1.model;

/**
 * Vizinhança usada pelos algoritmos de preenchimento.
 *
 * <pre>
 *  Conectividade 4        Conectividade 8
 *       . N .                N  N  N
 *       N P N                N  P  N
 *       . N .                N  N  N
 * </pre>
 */
public enum Connectivity {
    FOUR("4", new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}),
    EIGHT("8", new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}});

    private final String label;
    private final int[][] offsets;

    Connectivity(String label, int[][] offsets) {
        this.label = label;
        this.offsets = offsets;
    }

    /** Deslocamentos (dx, dy) de cada vizinho em relação ao pixel atual. */
    public int[][] offsets() {
        return offsets;
    }

    @Override
    public String toString() {
        return label;
    }
}
