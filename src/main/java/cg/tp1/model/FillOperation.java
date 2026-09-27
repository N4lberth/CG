package cg.tp1.model;

/**
 * Um preenchimento feito pelo usuário, guardado para ser refeito sempre que
 * a cena é redesenhada (ex.: depois de uma transformação).
 *
 * Se a semente foi clicada dentro de um polígono ou circunferência, esse
 * objeto é o "dono" do preenchimento: quando ele é transformado, a semente é
 * transformada junto, e o preenchimento acompanha o objeto.
 */
public final class FillOperation {

    private Point seed;
    private final Shape owner; // pode ser null (semente fora de qualquer objeto fechado)
    private final FillAlgorithm algorithm;
    private final Connectivity connectivity;
    private final int fillColor;
    private final int boundaryColor; // usada apenas pelo Boundary-Fill

    public FillOperation(Point seed, Shape owner, FillAlgorithm algorithm, Connectivity connectivity,
                         int fillColor, int boundaryColor) {
        this.seed = seed;
        this.owner = owner;
        this.algorithm = algorithm;
        this.connectivity = connectivity;
        this.fillColor = fillColor;
        this.boundaryColor = boundaryColor;
    }

    public Point seed() {
        return seed;
    }

    public void moveSeed(Point newSeed) {
        this.seed = newSeed;
    }

    public Shape owner() {
        return owner;
    }

    public FillAlgorithm algorithm() {
        return algorithm;
    }

    public Connectivity connectivity() {
        return connectivity;
    }

    public int fillColor() {
        return fillColor;
    }

    public int boundaryColor() {
        return boundaryColor;
    }
}
