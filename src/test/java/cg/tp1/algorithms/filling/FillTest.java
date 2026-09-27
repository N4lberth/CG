package cg.tp1.algorithms.filling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cg.tp1.model.Connectivity;
import cg.tp1.model.FillAlgorithm;
import cg.tp1.model.FillOperation;
import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Point;
import cg.tp1.model.Polygon;
import cg.tp1.model.Shape;
import cg.tp1.raster.CoordinateSystem;
import cg.tp1.raster.PixelBuffer;
import cg.tp1.render.SceneRenderer;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FillTest {

    private static final int WHITE = PixelBuffer.WHITE;
    private static final int BLACK = 0xFF000000;
    private static final int RED = 0xFFFF0000;
    private static final int BLUE = 0xFF0000FF;

    /** Quadrado de (0,0) a (10,10): interior de 9 × 9 = 81 pixels. */
    private static final int SQUARE_INTERIOR = 81;

    private final SceneRenderer renderer = new SceneRenderer();
    private PixelBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new PixelBuffer(new CoordinateSystem(40, 40), WHITE); // x, y ∈ [−20, 19]
    }

    private static Polygon square() {
        return new Polygon(List.of(new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 10)),
                BLACK, LineAlgorithm.BRESENHAM);
    }

    /** Losango: arestas diagonais, 8-conectadas (pixels encostam só pela quina). */
    private static Polygon diamond() {
        return new Polygon(List.of(new Point(0, 5), new Point(5, 10), new Point(10, 5), new Point(5, 0)),
                BLACK, LineAlgorithm.BRESENHAM);
    }

    private void draw(Shape shape) {
        renderer.draw(shape, buffer);
    }

    @Test
    void boundaryFillPaintsTheInteriorOfTheSquare() {
        draw(square());
        assertEquals(SQUARE_INTERIOR, BoundaryFill.fill(buffer, 5, 5, BLUE, BLACK, Connectivity.FOUR));
        assertEquals(BLUE, buffer.get(1, 1));
        assertEquals(BLACK, buffer.get(0, 0));   // fronteira intacta
        assertEquals(WHITE, buffer.get(-5, -5)); // lado de fora intacto
    }

    @Test
    void floodFillPaintsTheInteriorOfTheSquare() {
        draw(square());
        assertEquals(SQUARE_INTERIOR, FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.FOUR));
        assertEquals(WHITE, buffer.get(-5, -5));
    }

    @Test
    void eightConnectivityLeaksThroughDiagonalEdges() {
        draw(diamond());
        FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.FOUR);
        assertEquals(WHITE, buffer.get(-15, -15), "4-conectividade não atravessa a diagonal");

        setUp();
        draw(diamond());
        FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.EIGHT);
        assertEquals(BLUE, buffer.get(-15, -15), "8-conectividade vaza pela diagonal");
    }

    @Test
    void boundaryFillAlsoLeaksWithEightConnectivity() {
        draw(diamond());
        BoundaryFill.fill(buffer, 5, 5, BLUE, BLACK, Connectivity.FOUR);
        assertEquals(WHITE, buffer.get(-15, -15));

        setUp();
        draw(diamond());
        BoundaryFill.fill(buffer, 5, 5, BLUE, BLACK, Connectivity.EIGHT);
        assertEquals(BLUE, buffer.get(-15, -15));
    }

    @Test
    void boundaryFillPaintsOverOtherColorsButFloodFillStopsAtThem() {
        draw(square());
        buffer.set(3, 3, RED); // "obstáculo" de outra cor dentro do quadrado
        assertEquals(SQUARE_INTERIOR, BoundaryFill.fill(buffer, 5, 5, BLUE, BLACK, Connectivity.FOUR));
        assertEquals(BLUE, buffer.get(3, 3)); // só a cor da fronteira limita

        setUp();
        draw(square());
        buffer.set(3, 3, RED);
        assertEquals(SQUARE_INTERIOR - 1, FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.FOUR));
        assertEquals(RED, buffer.get(3, 3)); // qualquer cor diferente da cor alvo é barreira
    }

    @Test
    void boundaryFillIgnoresBordersOfAnotherColor() {
        draw(square()); // borda preta
        // Informando vermelho como fronteira, a borda preta não segura o preenchimento
        BoundaryFill.fill(buffer, 5, 5, BLUE, RED, Connectivity.FOUR);
        assertEquals(BLUE, buffer.get(-15, -15));
    }

    @Test
    void nothingIsPaintedWhenThereIsNothingToDo() {
        draw(square());
        assertEquals(0, BoundaryFill.fill(buffer, 0, 0, BLUE, BLACK, Connectivity.FOUR)); // semente na fronteira
        FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.FOUR);
        assertEquals(0, FloodFill.fill(buffer, 5, 5, BLUE, Connectivity.FOUR)); // já preenchido
    }

    @Test
    void fillingTheWholeAreaDoesNotOverflowTheStack() {
        assertEquals(40 * 40, FloodFill.fill(buffer, 0, 0, BLUE, Connectivity.EIGHT));
    }

    @Test
    void rendererReplaysStoredFills() {
        Polygon square = square();
        FillOperation fill = new FillOperation(new Point(5, 5), square, FillAlgorithm.BOUNDARY_FILL,
                Connectivity.FOUR, BLUE, BLACK);
        renderer.render(List.of(square), List.of(fill), buffer);
        assertEquals(BLUE, buffer.get(5, 5));
        assertEquals(WHITE, buffer.get(-5, -5));
    }

    @Test
    void pointInPolygon() {
        Polygon square = square();
        assertTrue(square.contains(new Point(5, 5)));
        assertFalse(square.contains(new Point(15, 5)));

        // Polígono côncavo em "U": o ponto no vão do U está fora
        Polygon u = new Polygon(List.of(
                new Point(0, 0), new Point(9, 0), new Point(9, 9), new Point(6, 9),
                new Point(6, 3), new Point(3, 3), new Point(3, 9), new Point(0, 9)),
                BLACK, LineAlgorithm.DDA);
        assertTrue(u.contains(new Point(1, 5)));
        assertFalse(u.contains(new Point(4.5, 6)));
    }
}
