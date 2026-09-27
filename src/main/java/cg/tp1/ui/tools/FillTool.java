package cg.tp1.ui.tools;

import cg.tp1.model.Point;
import cg.tp1.ui.DrawingEditor;
import cg.tp1.ui.FillSettings;

/**
 * Um clique define a semente do preenchimento, com as opções do painel "Preenchimento".
 */
public class FillTool implements Tool {

    private final DrawingEditor editor;
    private final FillSettings settings;
    private String lastResult = "";

    public FillTool(DrawingEditor editor, FillSettings settings) {
        this.editor = editor;
        this.settings = settings;
    }

    @Override
    public String name() {
        return "Preencher";
    }

    @Override
    public String hint() {
        return "Clique dentro da região a preencher (" + settings.algorithm()
                + ", conectividade " + settings.connectivity() + "). " + lastResult;
    }

    @Override
    public void mousePressed(Point point, boolean primary) {
        if (primary) {
            int painted = editor.fill(point, settings);
            lastResult = painted == 0
                    ? "Nenhum pixel pintado (semente sobre a fronteira ou já preenchida)."
                    : painted + " pixel(s) pintado(s).";
        }
    }

    @Override
    public void reset() {
        lastResult = "";
    }
}
