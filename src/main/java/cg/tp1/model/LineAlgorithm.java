package cg.tp1.model;

/**
 * Algoritmo usado para rasterizar uma reta (ou as arestas de um polígono).
 */
public enum LineAlgorithm {
    DDA("DDA"),
    BRESENHAM("Bresenham");

    private final String label;

    LineAlgorithm(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
