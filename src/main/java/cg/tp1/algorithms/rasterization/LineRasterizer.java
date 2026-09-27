package cg.tp1.algorithms.rasterization;

import cg.tp1.raster.Pixel;
import java.util.List;

/**
 * Contrato comum dos algoritmos de rasterização de retas:
 * recebe os extremos inteiros e devolve os pixels que formam a reta.
 */
public interface LineRasterizer {

    List<Pixel> rasterize(int x1, int y1, int x2, int y2);
}
