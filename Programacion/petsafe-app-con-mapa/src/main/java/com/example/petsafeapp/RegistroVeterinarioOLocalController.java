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
import model.NegocioModel;
import model.TipoNegocioModel;
import model.UsuarioModel;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controlador para la vista de registro de un negocio, que puede ser un veterinario o un local pet-friendly.
 * Gestiona el formulario para que un usuario registre una nueva cuenta de tipo negocio,
 * permitiendo seleccionar el tipo de negocio, validar los datos, interactuar con la base de datos
 * para crear el usuario y el negocio asociado, y navegar a la pantalla de inicio de sesión
 * tras un registro exitoso.
 */
public class RegistroVeterinarioOLocalController {
  /**
  * Panel raíz de la vista de registro de negocio.
  * Se utiliza para cambiar la escena principal de la aplicación.
  */
  @javafx.fxml.FXML
  private Pane panelRoot;
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
  * Campo de texto para el correo electrónico del negocio.
  */
  @javafx.fxml.FXML
  private TextField emailTextField;
  /**
  * Campo de contraseña para la contraseña de la cuenta.
  */
  @javafx.fxml.FXML
  private PasswordField contraseñaPasswordField;
  /**
  * ComboBox para seleccionar el tipo de negocio (Veterinario o Local).
  */
  @javafx.fxml.FXML
  private ComboBox tipoNegocioComboBox;
  /**
  * Campo de texto para la dirección del negocio.
  */
  @javafx.fxml.FXML
  private TextField direccionTextField;
  /**
  * Campo de texto para el número de teléfono de contacto del negocio.
  */
  @javafx.fxml.FXML
  private TextField telefonoTextField;
  /**
  * Campo de texto para la descripción del negocio.
  */
  @javafx.fxml.FXML
  private TextField descripcionTextField;
  /**
  * ImageView que muestra una vista previa de la foto seleccionada para el negocio.
  */
  @javafx.fxml.FXML
  private ImageView fotoImageView;
  /**
  * Campo de texto para el nombre del negocio.
  */
  @javafx.fxml.FXML
  private TextField nombreTextField;

  /**
  * Archivo de la imagen seleccionada para la foto del negocio.
  */
  private File imgFile;


  /**
  * Método de inicialización del controlador. Se ejecuta después de que los elementos FXML han sido cargados.
  * Carga los tipos de negocio disponibles desde el modelo y los añade al ComboBox.
  */
  public void initialize() {


    TipoNegocioModel tnm = new TipoNegocioModel();
    for(TipoNegocio tn : tnm.readTipoNegocio()) tipoNegocioComboBox.getItems().add(tn);


  }

  /**
  * Maneja el evento de clic en el botón "Registrar Veterinario/Local".
  * Realiza la validación de los campos del formulario, verifica la existencia del email,
  * crea un nuevo usuario genérico y un nuevo negocio (veterinario o local) asociado en la base de datos
  * si los datos son válidos, muestra alertas de error o éxito y navega a la pantalla de inicio de sesión.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista de inicio de sesión.
  * @throws SQLException Si ocurre un error relacionado con la base de datos.
  * @throws PacketTooBigException Si el tamaño de los datos excede el límite del paquete MySQL.
  * @throws NumberFormatException Si el número de teléfono no es un entero válido.
  */
  @javafx.fxml.FXML
  public void onRegistrarVeterinarioOLocalButtonClick(ActionEvent actionEvent) throws IOException, SQLException, PacketTooBigException {
    Alert alertaError = new Alert(Alert.AlertType.ERROR);

    alertaError.setTitle("ERROR");
    alertaError.setHeaderText("Faltan algunos campos");

    try {
      String nombre = nombreTextField.getText();
      String descripcion = descripcionTextField.getText();
      String email = emailTextField.getText();
      String direccion = direccionTextField.getText();
      String telefono = telefonoTextField.getText();
      String contrasena = contraseñaPasswordField.getText();
      String confirmarContrasena = confirmarContraseñaPasswordField.getText();
      TipoNegocio tipoNegocio = (TipoNegocio) tipoNegocioComboBox.getValue();
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
      if (descripcion.length() <= 0) {
        sb.append("- No has introducido la descripción.\n");
      }
      if (email.length() <= 0) {
        sb.append("- No has introducido el email.\n");
      } else if(!correoValido) {
        sb.append("- El correo es inválido.\n");
      } else if(um.existeEmail(email)) {
        sb.append("- La cuenta con ese email ya existe.\n");
      }
      if (direccion.length() <= 0) {
        sb.append("- No has introducido la dirección.\n");
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
      if(tipoNegocio == null) {
        sb.append("- Debes seleccionar el tipo de negocio.\n");
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

        // CREAR NEGOCIO EN LA BASE DE DATOS
        NegocioModel nm = new NegocioModel();

        // Obtiene el usuario recién creado para obtener su ID
        Usuario usuarioCreado = um.getUsuarioConCredenciales(email, contrasena);
        // Crea el objeto Negocio asociado al usuario
        Negocio negocio = new Negocio(imgFile, telefono, email, contrasena, nombre, descripcion, direccion, imgFile, tipoNegocio, 0, usuarioCreado.getId()); // El primer 'imgFile' y el último 'imgFile' pueden ser redundantes dependiendo de cómo se gestione la foto. El 'nombre' también parece que debería ser nombreNegocio.

        nm.createNegocio(negocio); // Crea el negocio en su tabla

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
  * Maneja el evento de clic en el botón para seleccionar la foto del negocio.
  * Abre un selector de archivos para que el usuario elija una imagen para el negocio.
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
