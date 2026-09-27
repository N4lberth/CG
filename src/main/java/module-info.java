/**
 * Módulo da aplicação. O JavaFX só é usado pela camada de interface (pacote ui);
 * os algoritmos e o modelo não dependem dele.
 */
module cg.tp1 {
    requires javafx.controls;

    exports cg.tp1;
}
