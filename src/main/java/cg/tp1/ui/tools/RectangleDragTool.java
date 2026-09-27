package cg.tp1.ui.tools;

import cg.tp1.model.LineAlgorithm;
import cg.tp1.model.Point;
import cg.tp1.model.Polygon;
import cg.tp1.model.Rectangle;
import cg.tp1.model.Shape;
import java.util.List;

/**
 * Base das ferramentas que definem um retângulo arrastando o mouse
 * (seleção e janela de recorte): pressionar, arrastar e soltar.
 */
public abstract class RectangleDragTool implements Tool {

    private final int previewColor;
    private Point anchor;   // canto onde o botão foi pressionado
    private Point current;  // canto oposto (posição atual do mouse)

    protected RectangleDragTool(int previewColor) {
        this.previewColor = previewColor;
    }

    /** Chamado ao soltar o botão, com o retângulo definido pelo usuário. */
    protected abstract void rectangleDefined(Rectangle rectangle);

    /** Chamado ao clicar com o botão direito. */
    protected void secondaryClick() {
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        if (primary) {
            anchor = point;
            current = point;
        } else {
            reset();
            secondaryClick();
        }
    }

    @Override
    public void mouseDragged(Point point) {
        if (anchor != null) {
            current = point;
        }
    }

    @Override
    public void mouseReleased(Point point) {
        if (anchor == null) {
            return;
        }
        Rectangle rectangle = Rectangle.fromCorners(anchor, point);
        reset();
        rectangleDefined(rectangle);
    }

    @Override
    public void reset() {
        anchor = null;
        current = null;
    }

    /** O retângulo é desenhado como um polígono de 4 vértices (arestas por Bresenham). */
    @Override
    public List<Shape> preview() {
        if (anchor == null) {
            return List.of();
        }
        List<Point> corners = Rectangle.fromCorners(anchor, current).corners();
        return List.of(new Polygon(corners, previewColor, LineAlgorithm.BRESENHAM));
    }
}
