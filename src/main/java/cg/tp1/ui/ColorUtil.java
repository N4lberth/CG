package cg.tp1.ui;

import javafx.scene.paint.Color;

/**
 * Converte a cor do JavaFX (usada nos controles) para o inteiro ARGB
 * armazenado na matriz de pixels.
 */
public final class ColorUtil {

    private ColorUtil() {
    }

    public static int toArgb(Color color) {
        int a = (int) Math.round(color.getOpacity() * 255);
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
