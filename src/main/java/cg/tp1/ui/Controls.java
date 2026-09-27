package cg.tp1.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;

/**
 * Fábrica de controles com o comportamento padrão da aplicação.
 */
public final class Controls {

    private Controls() {
    }

    /**
     * Spinner numérico que pode ser ajustado só com o mouse: setas
     * (clique) e roda do mouse. Digitar continua possível, mas não é necessário.
     */
    public static Spinner<Double> doubleSpinner(double min, double max, double initial, double step) {
        Spinner<Double> spinner = new Spinner<>(new DoubleSpinnerValueFactory(min, max, initial, step));
        spinner.setEditable(true);
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                spinner.increment();
            } else if (event.getDeltaY() < 0) {
                spinner.decrement();
            }
        });
        spinner.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                commitText(spinner);
            }
        });
        return spinner;
    }

    /** Valor atual do spinner, considerando também um texto digitado e ainda não confirmado. */
    public static double valueOf(Spinner<Double> spinner) {
        commitText(spinner);
        return spinner.getValue();
    }

    /** Converte o texto digitado em valor; se for inválido, restaura o valor anterior. */
    private static void commitText(Spinner<Double> spinner) {
        SpinnerValueFactory<Double> factory = spinner.getValueFactory();
        try {
            Double typed = factory.getConverter().fromString(spinner.getEditor().getText());
            if (typed != null) {
                factory.setValue(typed); // a fábrica limita o valor ao intervalo [min, max]
            }
        } catch (RuntimeException invalidText) {
            // texto inválido: mantém o valor anterior
        }
        spinner.getEditor().setText(factory.getConverter().toString(factory.getValue()));
    }

    public static void warn(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle("Atenção");
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
