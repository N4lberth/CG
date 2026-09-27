package cg.tp1.ui;

import cg.tp1.model.Connectivity;
import cg.tp1.model.FillAlgorithm;

/**
 * Opções de preenchimento escolhidas no painel e usadas pela ferramenta Preencher.
 */
public class FillSettings {

    private FillAlgorithm algorithm = FillAlgorithm.FLOOD_FILL;
    private Connectivity connectivity = Connectivity.FOUR;
    private int fillColor = 0xFF42A5F5;
    private int boundaryColor = 0xFF000000;

    public FillAlgorithm algorithm() {
        return algorithm;
    }

    public void setAlgorithm(FillAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Connectivity connectivity() {
        return connectivity;
    }

    public void setConnectivity(Connectivity connectivity) {
        this.connectivity = connectivity;
    }

    public int fillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int boundaryColor() {
        return boundaryColor;
    }

    public void setBoundaryColor(int boundaryColor) {
        this.boundaryColor = boundaryColor;
    }
}
