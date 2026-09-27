package cg.tp1.algorithms.clipping;

/**
 * Algoritmos de recorte disponíveis na interface.
 */
public enum ClipAlgorithm {
    COHEN_SUTHERLAND("Cohen-Sutherland", new CohenSutherland()),
    LIANG_BARSKY("Liang-Barsky", new LiangBarsky());

    private final String label;
    private final LineClipper clipper;

    ClipAlgorithm(String label, LineClipper clipper) {
        this.label = label;
        this.clipper = clipper;
    }

    public LineClipper clipper() {
        return clipper;
    }

    @Override
    public String toString() {
        return label;
    }
}
