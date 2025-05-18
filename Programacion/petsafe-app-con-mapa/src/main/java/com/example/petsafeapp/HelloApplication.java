/**
 * Clase principal de la aplicación PetSafe que inicia la interfaz gráfica de usuario.
 * <p>
 * Esta clase extiende {@link javafx.application.Application} y sirve como punto de entrada
 * para la aplicación JavaFX. Configura la ventana principal, carga la interfaz de inicio de sesión
 * y define los elementos visuales básicos como el título y el icono de la aplicación.
 * </p>
 * 
 * @see javafx.application.Application
 * @see javafx.stage.Stage
 */
package com.example.petsafeapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    
    /**
     * Método principal que inicia la aplicación JavaFX.
     * 
     * @param args Argumentos de línea de comandos (no utilizados en esta aplicación).
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Configura y muestra la ventana principal de la aplicación.
     * <p>
     * Carga la interfaz gráfica desde el archivo FXML 'inicioSesion-view.fxml',
     * establece el título de la ventana, añade un icono a la aplicación,
     * y fija las dimensiones iniciales de la ventana a 411x700 píxeles.
     * </p>
     * 
     * @param stage Escenario principal proporcionado por JavaFX.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     * 
     * @see javafx.scene.Scene
     * @see javafx.fxml.FXMLLoader
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("inicioSesion-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 411, 700);
        stage.setTitle("PetSafe");

        // Carga el icono de la aplicación desde los recursos
        Image logo = new Image(getClass().getResource("/images/logo-app.png").toExternalForm());
        stage.getIcons().add(logo);

        stage.setScene(scene);
        stage.show();
    }
}
