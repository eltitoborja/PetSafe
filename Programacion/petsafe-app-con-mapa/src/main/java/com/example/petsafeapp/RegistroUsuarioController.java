package com.example.petsafeapp;

import com.mysql.cj.jdbc.exceptions.PacketTooBigException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.PersonaModel;
import model.UsuarioModel;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controlador para la vista de registro de usuario (persona física).
 * Gestiona el formulario para que un usuario se registre en la aplicación como persona física,
 * incluyendo la validación de datos, la selección de foto, la interacción con la base de datos
 * para crear el usuario y la persona asociada, y la navegación a la pantalla de inicio de sesión
 * tras un registro exitoso.
 */
public class RegistroUsuarioController {
  /**
  * Panel raíz de la vista de registro de usuario.
  * Se utiliza para cambiar la escena principal de la aplicación.
  */
  @javafx.fxml.FXML
  private Pane panelRoot;
  /**
  * Campo de texto para el correo electrónico del usuario.
  */
  @javafx.fxml.FXML
  private TextField emailTextField;
  /**
  * Campo de contraseña para la contraseña del usuario.
  */
  @javafx.fxml.FXML
  private PasswordField contraseñaPasswordField;
  /**
  * Campo para confirmar la contraseña ingresada.
  */
  @javafx.fxml.FXML
  private PasswordField confirmarContraseñaPasswordField;
  /**
  * CheckBox para aceptar los términos y condiciones.
  */
  @javafx.fxml.FXML
  private CheckBox terminosYCondicionesCheckBox;
  /**
  * Campo de texto para el nombre del usuario.
  */
  @javafx.fxml.FXML
  private TextField nombreTextField;
  /**
  * Botón para iniciar el proceso de registro de usuario.
  */
  @javafx.fxml.FXML
  private Button registrarUsuarioButton;
  /**
  * Campo de texto para el número de teléfono del usuario.
  */
  @javafx.fxml.FXML
  private TextField telefonoTextField;
  /**
  * DatePicker para seleccionar la fecha de nacimiento del usuario.
  */
  @javafx.fxml.FXML
  private DatePicker fechaNacimientoDatePicker;
  /**
  * Campo de texto para los apellidos del usuario.
  */
  @javafx.fxml.FXML
  private TextField apellidosTextField;
  /**
  * ImageView que muestra una vista previa de la foto seleccionada para el perfil.
  */
  @javafx.fxml.FXML
  private ImageView fotoImageView;

  /**
  * Archivo de la imagen seleccionada para la foto de perfil.
  */
  private File imgFile;


  /**
  * Método de inicialización del controlador. Se ejecuta después de que los elementos FXML han sido cargados.
  * Configura la CellFactory del DatePicker para deshabilitar fechas futuras.
  */
  public void initialize() {
    Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
      @Override
      public DateCell call(final DatePicker datePicker) {
        return new DateCell() {
          @Override
          public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);

            // Opcional: Deshabilitar fechas futuras
            if (item.isAfter(LocalDate.now())) {
              setDisable(true);
              //setStyle("-fx-background-color: #ffc0cb;"); // Fondo rosa para días pasados deshabilitados
            }
          }
        };
      }
    };

    // Asigna la CellFactory al DatePicker
    fechaNacimientoDatePicker.setDayCellFactory(dayCellFactory);
  }

  /**
  * Maneja el evento de clic en el botón "Registrar Usuario".
  * Realiza la validación de los campos del formulario, verifica la existencia del email,
  * crea un nuevo usuario genérico y una nueva persona asociada en la base de datos si los datos son válidos,
  * muestra alertas de error o éxito y navega a la pantalla de inicio de sesión.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista de inicio de sesión.
  * @throws SQLException Si ocurre un error relacionado con la base de datos.
  * @throws PacketTooBigException Si el tamaño de los datos excede el límite del paquete MySQL.
  * @throws NumberFormatException Si el número de teléfono no es un entero válido.
  */
  @javafx.fxml.FXML
  public void onRegistrarUsuarioButtonClick(ActionEvent actionEvent) throws IOException, SQLException, PacketTooBigException {
    Alert alertaError = new Alert(Alert.AlertType.ERROR);

    alertaError.setTitle("ERROR");
    alertaError.setHeaderText("Faltan algunos campos");

    try {
      String nombre = nombreTextField.getText();
      String apellidos = apellidosTextField.getText();
      String email = emailTextField.getText();
      LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();
      String telefono = telefonoTextField.getText();
      String contrasena = contraseñaPasswordField.getText();
      String confirmarContrasena = confirmarContraseñaPasswordField.getText();
      boolean aceptarTerminosYCondiciones = terminosYCondicionesCheckBox.isSelected();

      String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
      boolean correoValido = email.matches(regex);

      UsuarioModel um = new UsuarioModel();

      // CONTROL DE ERRORES

      StringBuilder sb = new StringBuilder("Errores:\n");
      if(imgFile == null) {
        sb.append("- No has seleccionado ninguna foto.\n");
      }
      if (nombre.length() <= 0) {
        sb.append("- No has introducido el nombre.\n");
      }
      if (apellidos.length() <= 0) {
        sb.append("- No has introducido los apellidos.\n");
      }
      if (email.length() <= 0) {
        sb.append("- No has introducido el email.\n");
      } else if(!correoValido) {
        sb.append("- El correo es inválido.\n");
      } else if(um.existeEmail(email)) {
        sb.append("- La cuenta con ese email ya existe.\n");
      }
      if (fechaNacimiento == null) {
        sb.append("- No has introducido la fecha de nacimiento.\n");
      } else if(fechaNacimiento.isAfter(LocalDate.now())) {
        sb.append("- Has introducido una fecha de nacimiento en el futuro.\n");
      }
      if (telefono.length() <= 0) {
        sb.append("- No has introducido el teléfono de contacto.\n");
      }
      if (contrasena.length() <= 0) {
        sb.append("- No has introducido la contraseña.\n");
      } else if (contrasena.length() < 6) {
        sb.append("- La contraseña debe tener al menos 6 carácteres.\n");
      }
      if (!contrasena.equals(confirmarContrasena)) {
        sb.append("- No coinciden las contraseñas.\n");
      }
      if(!aceptarTerminosYCondiciones) {
        sb.append("- Debes aceptar los términos y condiciones.");
      }

      alertaError.setContentText(sb.toString());

      Integer.valueOf(telefono); // Intenta convertir a entero para validar que es un número

      if (sb.indexOf("-") >= 0) {
        // SI HAY ERROR
        alertaError.showAndWait();
      } else {
        // SI NO HAY ERROR
        // Crea el usuario genérico
        Usuario usuario = new Usuario(imgFile, telefono, email, contrasena, nombre);
        um.createUsuario(usuario);

        // Obtiene el usuario recién creado para obtener su ID
        Usuario usuarioEncontrado = um.getUsuarioConCredenciales(email, contrasena);

        PersonaModel pm = new PersonaModel();
        // Crea el objeto Persona asociado al usuario
        Persona persona = new Persona(usuarioEncontrado.getId(), imgFile, telefono, email, contrasena, nombre, fechaNacimiento, apellidos, nombre); // El último 'nombre' parece un error, debería ser nombreUsuario o similar

        pm.createPersona(persona); // Crea la persona en su tabla

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Cuenta creada");
        alerta.setHeaderText("Cuenta creada correctamente");
        alerta.setContentText("Se ha creado correctamente la cuenta.");

        alerta.showAndWait();

        // CAMBIO DE PANTALLA a inicio de sesión
        FXMLLoader loader = new FXMLLoader(getClass().getResource("inicioSesion-view.fxml"));

        // 2. Carga el Pane
        Pane pane = loader.load();

        // 3. Obtén el controlador
        InicioSesionController controller = loader.getController();

        // 4. Pasa los datos (ejemplo: pasar un usuario)
        // ...

        this.panelRoot.getChildren().setAll(pane);
      }
    } catch(NumberFormatException e) {
      // Captura la excepción si el teléfono no es un número
      alertaError.setHeaderText("Formato incorrecto");
      alertaError.setContentText("- No se ha introducido un número de teléfono correcto.");

      alertaError.showAndWait();
    }
  }

  /**
  * Maneja el evento de clic en la flecha de retroceso.
  * Cambia la vista al panel de inicio de sesión.
  * @param event El evento de clic.
  * @throws IOException Si ocurre un error al cargar la vista FXML de inicio de sesión.
  */
  @javafx.fxml.FXML
  public void onFlechaAtrasClick(Event event) throws IOException {
    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("inicioSesion-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    InicioSesionController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    // controller.setDatos(this.usuario); // Comentado en el código original

    this.panelRoot.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el botón para seleccionar la foto de perfil.
  * Abre un selector de archivos para que el usuario elija una imagen.
  * Si se selecciona una imagen, la muestra en la ImageView. Si se cancela,
  * muestra una imagen predeterminada.
  * @param actionEvent El evento de acción.
  */
  @javafx.fxml.FXML
  public void onSeleccionarFotoButtonClick(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Selecciona la imagen");

    // Agregar filtros para facilitar la busqueda
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Todas las imágenes", "*.jpg", "*.png"),
        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        new FileChooser.ExtensionFilter("PNG", "*.png")
    );

    // Obtener la imagen seleccionada
    Stage stage = (Stage) panelRoot.getScene().getWindow();
    imgFile = fileChooser.showOpenDialog(stage);

    // Compruebo si ha seleccionado alguna image
    if (imgFile != null) {
      Image image = new Image("file:" + imgFile.getAbsolutePath());
      fotoImageView.setImage(image);
    } else {
      // Si no selecciona ninguna imagen, muestra una por defecto
      Image image = new Image(getClass().getResource("/images/nuevo-archivo.png").toExternalForm());
      fotoImageView.setImage(image);
    }
  }
}
