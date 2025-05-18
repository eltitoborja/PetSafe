package com.example.petsafeapp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.UsuarioModel;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio de sesión de la aplicación PetSafe.
 * Maneja la autenticación de usuarios y la navegación a pantallas relacionadas.
 */
public class InicioSesionController {

    @FXML
    private Label registrarseAhoraLabel;
    @FXML
    private Button iniciarSesionButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label olvidoSuContraseñaLabel;
    @FXML
    private PasswordField contraseñaPasswordField;
    @FXML
    private Pane panelRoot;
    @FXML
    private VBox registrarseAhoraPopUp;
    @FXML
    private Pane registrarseAhoraPopUpFondo;

    private Usuario usuario;

    /**
     * Maneja el evento de clic en el enlace "Olvidó su contraseña".
     * Navega a la pantalla de recuperación de contraseña.
     *
     * @param event El evento de acción que desencadenó este método
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    public void onOlvidoSuContraseñaLabelClick(Event event) throws IOException {
        /*
        // CAMBIO DE PANTALLA
        FXMLLoader loader = new FXMLLoader(getClass().getResource("plantillaPantallas-view.fxml"));

        // 2. Carga el Pane
        Pane pane = loader.load();

        // 3. Obtén el controlador
        PlantillaPantallasController controller = loader.getController();

        // 4. Pasa los datos (ejemplo: pasar un usuario)
        // controller.setDatos(this.usuario);

        this.panelRoot.getChildren().setAll(pane);
        */
    }

    /**
     * Maneja el evento de clic en el botón de inicio de sesión.
     * Valida las credenciales y navega a la pantalla principal si son correctas.
     *
     * @param actionEvent El evento de acción que desencadenó este método
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    public void onIniciarSesionButtonClick(ActionEvent actionEvent) throws IOException {
        String email = emailTextField.getText();
        String contrasena = contraseñaPasswordField.getText();
        boolean inicioSesionValido = false;

        // POR AQUÍ SE VERIFICA SI EXISTE LA CUENTA Y SE CAMBIA EL VALOR DE LA VARIABLE "inicioSesionValido"
        UsuarioModel um = new UsuarioModel();
        Usuario encontrado = um.getUsuarioConCredenciales(email, contrasena);

        if(encontrado != null)
            inicioSesionValido = true;

        if(inicioSesionValido) {
            this.usuario = encontrado;

            // CAMBIO DE PANTALLA
            // la plantilla para las pantallas, donde se carga el mapa localizador por defecto
            FXMLLoader loader = new FXMLLoader(getClass().getResource("plantillaPantallas-view.fxml"));

            // 2. Carga el Pane
            Pane pane = loader.load();

            // 3. Obtén el controlador
            PlantillaPantallasController controller = loader.getController();

            // 4. Pasa los datos (ejemplo: pasar un usuario)
            controller.setUsuario(this.usuario);

            this.panelRoot.getChildren().setAll(pane);
        } else {
            Alert alertaError = new Alert(Alert.AlertType.ERROR);

            alertaError.setTitle("Credenciales inválidas");
            alertaError.setHeaderText("CREDENCIALES INVÁLIDAS");
            alertaError.setContentText("No se encontró ninguna cuenta con ese correo y contraseña.");

            alertaError.showAndWait();
        }
    }

    /**
     * Maneja el evento de clic en el enlace "Registrarse ahora".
     * Muestra el popup de opciones de registro.
     *
     * @param event El evento que desencadenó este método
     */
    @FXML
    public void onRegistrarseAhoraLabelClick(Event event) {
        // mostrar el popup de "Registrarse como"
        popUpRegistrarseComo();
    }

    /**
     * Muestra u oculta el popup de opciones de registro.
     * Alterna la visibilidad del popup y su fondo.
     */
    @FXML
    public void popUpRegistrarseComo() {
        /*
        El ! es para invertir el boolean:
        - true -->  false
        - false --> true
         */
        boolean nuevoEstado = !registrarseAhoraPopUpFondo.isVisible();

        registrarseAhoraPopUpFondo.setVisible(nuevoEstado);
        registrarseAhoraPopUp.setVisible(nuevoEstado);

        registrarseAhoraPopUpFondo.setManaged(nuevoEstado);
        registrarseAhoraPopUp.setManaged(nuevoEstado);
    }

    /**
     * Maneja el evento de clic en el botón "Registrarse como Usuario".
     * Navega a la pantalla de registro de usuario.
     *
     * @param actionEvent El evento de acción que desencadenó este método
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    public void onRegistrarseComoUsuarioButtonClick(ActionEvent actionEvent) throws IOException {
        // CAMBIO DE PANTALLA
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registroUsuario-view.fxml"));

        // 2. Carga el Pane
        Pane pane = loader.load();

        // 3. Obtén el controlador
        RegistroUsuarioController controller = loader.getController();

        // 4. Pasa los datos (ejemplo: pasar un usuario)
        // controller.setDatos(this.usuario);

        this.panelRoot.getChildren().setAll(pane);
    }

    /**
     * Maneja el evento de clic en el botón "Registrarse como Protectora".
     * Navega a la pantalla de registro de protectoras.
     *
     * @param actionEvent El evento de acción que desencadenó este método
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    public void onRegistrarseComoProtectoraButtonClick(ActionEvent actionEvent) throws IOException {
        // CAMBIO DE PANTALLA
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registroProtectora-view.fxml"));

        // 2. Carga el Pane
        Pane pane = loader.load();

        // 3. Obtén el controlador
        RegistroProtectoraController controller = loader.getController();

        // 4. Pasa los datos (ejemplo: pasar un usuario)
        // controller.setDatos(this.usuario);

        this.panelRoot.getChildren().setAll(pane);
    }

    /**
     * Maneja el evento de clic en el botón "Registrarse como Veterinario o Local".
     * Navega a la pantalla de registro para veterinarios o locales.
     *
     * @param actionEvent El evento de acción que desencadenó este método
     * @throws IOException Si ocurre un error al cargar la vista
     */
    @FXML
    public void onRegistrarseComoVeterinarioOLocalButtonClick(ActionEvent actionEvent) throws IOException {
        // CAMBIO DE PANTALLA
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registroVeterinarioOLocal-view.fxml"));

        // 2. Carga el Pane
        Pane pane = loader.load();

        // 3. Obtén el controlador
        RegistroVeterinarioOLocalController controller = loader.getController();

        // 4. Pasa los datos (ejemplo: pasar un usuario)
        // controller.setDatos(this.usuario);

        this.panelRoot.getChildren().setAll(pane);
    }
}
