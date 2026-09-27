package cg.tp1.raster;

import java.util.Arrays;

/**
 * A matriz de pixels (Área de Desenho) exigida pelo enunciado.
 *
 * Cada célula guarda uma cor no formato ARGB (0xAARRGGBB). Os algoritmos leem
 * e escrevem pixels usando coordenadas cartesianas; a conversão para
 * [linha][coluna] é feita pelo {@link CoordinateSystem}.
 *
 * Esta classe não conhece o JavaFX: a interface apenas copia a matriz para a tela.
 */
public final class PixelBuffer {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int TRANSPARENT = 0x00000000;

    private final CoordinateSystem coordinates;
    private final int[][] pixels; // pixels[linha][coluna]
    private final int background;

    public PixelBuffer(CoordinateSystem coordinates, int background) {
        this.coordinates = coordinates;
        this.background = background;
        this.pixels = new int[coordinates.rows()][coordinates.columns()];
        clear();
    }

    /** Pinta toda a matriz com a cor de fundo. */
    public void clear() {
        for (int[] row : pixels) {
            Arrays.fill(row, background);
        }
    }

    /** Altera a cor do pixel (x, y). Pontos fora da área são ignorados. */
    public void set(int x, int y, int color) {
        if (coordinates.contains(x, y)) {
            pixels[coordinates.toRow(y)][coordinates.toColumn(x)] = color;
        }
    }

    public void set(Pixel pixel, int color) {
        set(pixel.x(), pixel.y(), color);
    }

    /** Cor do pixel (x, y). Deve ser consultado apenas dentro da área ({@link #contains}). */
    public int get(int x, int y) {
        return pixels[coordinates.toRow(y)][coordinates.toColumn(x)];
    }

    public boolean contains(int x, int y) {
        return coordinates.contains(x, y);
    }

    /** Leitura direta por linha/coluna, usada somente para copiar a matriz para a tela. */
    public int getByIndex(int row, int column) {
        return pixels[row][column];
    }

    public int background() {
        return background;
    }

    public CoordinateSystem coordinates() {
        return coordinates;
    }
}
