package cg.tp1.model;

/**
 * Algoritmo de preenchimento de regiões.
 */
public enum FillAlgorithm {
    BOUNDARY_FILL("Boundary-Fill"),
    FLOOD_FILL("Flood-Fill");

    private final String label;

    FillAlgorithm(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
