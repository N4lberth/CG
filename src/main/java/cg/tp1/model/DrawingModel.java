package cg.tp1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Armazena todos os objetos da cena, na ordem em que foram desenhados,
 * o subconjunto atualmente selecionado e a janela de recorte.
 * É a "fonte da verdade": a matriz de pixels é sempre recalculada a partir daqui.
 */
public class DrawingModel {

    private final List<Shape> shapes = new ArrayList<>();

    /**
     * Objetos selecionados. As figuras não sobrescrevem equals(), então o
     * conjunto compara por identidade: duas retas iguais continuam sendo objetos distintos.
     */
    private final Set<Shape> selection = new LinkedHashSet<>();

    /** Preenchimentos, refeitos em ordem após desenhar os objetos. */
    private final List<FillOperation> fills = new ArrayList<>();

    /** Janela de recorte definida pelo usuário ({@code null} = nenhuma). */
    private Rectangle clipWindow;

    public void add(Shape shape) {
        shapes.add(shape);
    }

    public void clear() {
        shapes.clear();
        selection.clear();
        fills.clear();
        clipWindow = null;
    }

    /**
     * Substitui um objeto por outros, na mesma posição da ordem de desenho
     * (usado no recorte: uma reta vira a reta recortada; um polígono vira suas arestas).
     */
    public void replace(Shape original, List<? extends Shape> replacements) {
        int index = shapes.indexOf(original);
        if (index < 0) {
            return;
        }
        shapes.remove(index);
        shapes.addAll(index, replacements);
        selection.remove(original);
        if (!replacements.contains(original)) {
            // O objeto deixou de existir (ex.: polígono recortado virou retas abertas):
            // o preenchimento dele "vazaria" pela abertura, então é descartado.
            fills.removeIf(fill -> fill.owner() == original);
        }
    }

    /** Objeto fechado mais recente (o de cima) que contém o ponto, ou null. */
    public Shape topmostShapeContaining(Point p) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(p)) {
                return shapes.get(i);
            }
        }
        return null;
    }

    // ---------- preenchimentos ----------

    public void addFill(FillOperation fill) {
        fills.add(fill);
    }

    public void clearFills() {
        fills.clear();
    }

    public List<FillOperation> fills() {
        return Collections.unmodifiableList(fills);
    }

    public List<Shape> shapes() {
        return Collections.unmodifiableList(shapes);
    }

    // ---------- seleção ----------

    /**
     * Seleção por região retangular: substitui a seleção atual pelos objetos
     * que estão INTEIRAMENTE dentro da região (caixa envolvente contida nela).
     *
     * @return quantidade de objetos selecionados
     */
    public int selectInside(Rectangle region) {
        selection.clear();
        for (Shape shape : shapes) {
            if (region.contains(shape.boundingBox())) {
                selection.add(shape);
            }
        }
        return selection.size();
    }

    public void selectAll() {
        selection.clear();
        selection.addAll(shapes);
    }

    public void clearSelection() {
        selection.clear();
    }

    public boolean isSelected(Shape shape) {
        return selection.contains(shape);
    }

    public Set<Shape> selection() {
        return Collections.unmodifiableSet(selection);
    }

    // ---------- janela de recorte ----------

    public Rectangle clipWindow() {
        return clipWindow;
    }

    public void setClipWindow(Rectangle clipWindow) {
        this.clipWindow = clipWindow;
    }

    /** Caixa envolvente de TODOS os objetos selecionados juntos (seleção não pode estar vazia). */
    public Rectangle selectionBounds() {
        List<Point> corners = new ArrayList<>();
        for (Shape shape : selection) {
            corners.addAll(shape.boundingBox().corners());
        }
        return Rectangle.enclosing(corners);
    }
}
