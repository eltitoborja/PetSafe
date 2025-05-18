package com.example.petsafeapp;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.NegocioModel;
import model.PersonaModel;
import model.ProtectoraModel;
import model.UsuarioModel;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Controlador para la vista del perfil de usuario.
 * Permite visualizar y editar la información del usuario actual,
 * incluyendo nombre, email, contraseña, teléfono y foto de perfil.
 */
public class PerfilUsuarioController {
  /**
  * Panel principal que contiene la vista del perfil.
  * Se utiliza para acceder a la ventana principal (Stage).
  */
  @javafx.fxml.FXML
  private Pane panelContenido;
  /**
  * ImageView que muestra la foto de perfil del usuario.
  */
  @javafx.fxml.FXML
  private ImageView fotoPerfilImageView;
  /**
  * Campo de texto para el correo electrónico del usuario.
  */
  @javafx.fxml.FXML
  private TextField emailTextField;
  /**
  * Campo de contraseña para la contraseña del usuario.
  */
  @javafx.fxml.FXML
  private PasswordField contrasenaPasswordField;
  /**
  * Campo de texto para el nombre del usuario.
  */
  @javafx.fxml.FXML
  private TextField nombreTextField;
  /**
  * Botón para seleccionar una nueva foto de perfil.
  */
  @javafx.fxml.FXML
  private Button fotoPerfilButton;
  /**
  * Campo de texto para el número de teléfono del usuario.
  */
  @javafx.fxml.FXML
  private TextField telefonoTextField;

  /**
  * Etiqueta que muestra el título de la pantalla actual.
  */
  private Label tituloPantallaLabel;
  /**
  * Gestor de strings para manejar la localización o el estado de la vista.
  */
  private GestorStrings gestorStrings;
  /**
  * Imagen de flecha utilizada para regresar a la pantalla anterior.
  */
  private ImageView flechaAtras;
  /**
  * Objeto Usuario que representa al usuario actualmente logueado.
  */
  private Usuario usuario;
  /**
  * Archivo de la nueva imagen seleccionada para el perfil.
  */
  private File imgFile;
  /**
  * Archivo de la imagen original del perfil antes de cualquier cambio.
  */
  private File imgOriginal;
  /**
  * ImageView que muestra la imagen de perfil en el menú lateral (posiblemente).
  */
  private ImageView imgPerfil;


  /**
  * Establece la ImageView para la imagen de perfil en el menú lateral.
  * @param img La ImageView a establecer.
  */
  public void setImgMenuLateral(ImageView img) {
    this.imgPerfil = img;
  }
  /**
  * Establece la etiqueta del título de la pantalla.
  * @param tituloPantallaLabel La etiqueta Label a establecer como título.
  */
  public void setTituloPantallaLabel(Label tituloPantallaLabel) {
    this.tituloPantallaLabel = tituloPantallaLabel;
  }

  /**
  * Establece el gestor de strings utilizado por el controlador.
  * @param gestorStrings El GestorStrings a establecer.
  */
  public void setGestorStrings(GestorStrings gestorStrings) {
    this.gestorStrings = gestorStrings;
  }

  /**
  * Establece la imagen de flecha de retroceso y la hace visible (comentado).
  * @param flechaAtras La ImageView de la flecha de retroceso.
  */
  public void setFlechaAtras(ImageView flechaAtras) {
    this.flechaAtras = flechaAtras;
    // flechaAtras.setVisible(true);
  }

  /**
  * Establece el usuario actual y carga sus datos en los campos de la interfaz.
  * También carga la foto de perfil en la ImageView y guarda la foto original.
  * @param usuario El objeto Usuario a establecer y mostrar.
  */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;

    System.out.println(this.usuario.getFoto().toURI().toString());
    Image imagen = new Image(this.usuario.getFoto().toURI().toString());
    fotoPerfilImageView.setImage(imagen);

    imgOriginal = usuario.getFoto();

    nombreTextField.setText(usuario.getNombre());
    emailTextField.setText(usuario.getEmail());
    contrasenaPasswordField.setText(usuario.getContrasena());
    telefonoTextField.setText(usuario.getNumTel());
  }

  /**
  * Maneja el evento de clic en el botón "Editar Perfil".
  * Valida los campos de entrada, actualiza la información del usuario en la base de datos
  * (tanto en la tabla de usuario genérico como en tablas específicas como Persona, Negocio o Protectora),
  * y muestra mensajes de éxito o error.
  * @param actionEvent El evento de acción.
  */
  @javafx.fxml.FXML
  public void onEditarPerfilButtonClick(ActionEvent actionEvent) {
    Alert alertaError = new Alert(Alert.AlertType.ERROR);

    alertaError.setTitle("ERROR");
    alertaError.setHeaderText("Faltan algunos campos");

    try {
      String nombre = nombreTextField.getText();
      String email = emailTextField.getText();
      String contrasena = contrasenaPasswordField.getText();
      String telefono = telefonoTextField.getText();

      String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
      boolean correoValido = email.matches(regex);

      UsuarioModel um = new UsuarioModel();

      StringBuilder sb = new StringBuilder("Existen los siguientes errores:\n");

      if (nombre.length() <= 0) {
        sb.append("- No has introducido el nombre.");
      }
      if (email.length() <= 0) {
        sb.append("- No has introducido el email.");
      } else if(!correoValido) {
        sb.append("- No has introducido un correo válido.");
      } else if(um.existeEmail(email) && !email.equals(this.usuario.getEmail())) {
        sb.append("- La cuenta con ese email ya existe.\n");
      }
      if (contrasena.length() <= 0) {
        sb.append("- No has introducido la contraseña.");
      } else if(contrasena.length() < 6) {
        sb.append("- La contraseña debe tener 6 carácteres como mínimo.");
      }
      if (telefono.length() <= 0) {
        sb.append("- No has introducido el teléfono de contacto.");
      }

      alertaError.setContentText(sb.toString());

      Integer.valueOf(telefono); // Intenta convertir a entero para validar que es un número

      if (sb.indexOf("-") >= 0) {
        // SI HAY ERROR
        alertaError.showAndWait();
      } else {
        // SI NO HAY ERROR
        this.usuario.setNombre(nombre);
        this.usuario.setEmail(email);
        this.usuario.setContrasena(contrasena);
        this.usuario.setNumTel(telefono);
        if(imgFile != null)
          this.usuario.setFoto(imgFile);

        um.updateUsuario(this.usuario);
        imgOriginal = imgFile; // La nueva imagen seleccionada se convierte en la original tras guardar

        // si es una persona
        PersonaModel pm = new PersonaModel();
        Persona persona = pm.getPersonaFromIdUsuario(this.usuario.getId());

        if(persona != null) {
          persona.setNombrePersona(nombre);

          pm.updatePersona(persona);
        }

        // si es un negocio
        NegocioModel nm = new NegocioModel();
        Negocio negocio = nm.getNegocioFromIdUsuario(this.usuario.getId());

        if(negocio != null) {
          negocio.setNombreNegocio(nombre); // POSIBLE ERROR: Esto parece que debería actualizar el nombre del negocio, no el nombre del usuario asociado.

          nm.updateNegocio(negocio);
        }

        // si es una protectora
        ProtectoraModel ptm = new ProtectoraModel();
        Protectora protectora = ptm.getProtectoraFromIdUsuario(this.usuario.getId());

        if(protectora != null) {
          protectora.setNombreProtectora(nombre); // POSIBLE ERROR: Esto parece que debería actualizar el nombre de la protectora, no el nombre del usuario asociado.

          ptm.updateProtectora(protectora);
        }

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Cuenta modificada");
        alerta.setHeaderText("Cuenta modificada correctamente");
        alerta.setContentText("Se ha modificado correctamente la cuenta.");

        alerta.showAndWait();

        if(this.imgFile != null) {
          Image imagen = new Image(imgFile.getAbsolutePath());
          this.imgPerfil.setImage(imagen); // Actualiza la imagen en el menú lateral si se cambió la foto
        }
      }
    } catch(NumberFormatException e) {
      alertaError.setHeaderText("Formato incorrecto");
      alertaError.setContentText("- No se ha introducido un número de teléfono correcto.");

      alertaError.showAndWait();
    } catch (FileNotFoundException e) {
      alertaError.setHeaderText("Archivo no encontrado");
      alertaError.setContentText("- No se ha encontrado el archivo seleccionado.");

      alertaError.showAndWait();
    }
  }

  /**
  * Maneja el evento de clic en el botón para seleccionar la foto de perfil.
  * Abre un selector de archivos para que el usuario elija una imagen.
  * Si se selecciona una imagen, la muestra en la ImageView y actualiza el texto del botón.
  * Si se cancela la selección, restaura la imagen y texto originales.
  * @param actionEvent El evento de acción.
  */
  @javafx.fxml.FXML
  public void onFotoPerfilButtonClick(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Selecciona la imagen");

    // Agregar filtros para facilitar la busqueda
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Todas las imágenes", "*.jpg", "*.png"),
        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        new FileChooser.ExtensionFilter("PNG", "*.png")
    );

    // Obtener la imagen seleccionada
    Stage stage = (Stage) panelContenido.getScene().getWindow();
    imgFile = fileChooser.showOpenDialog(stage);

    // Compruebo si ha seleccionado alguna image
    if (imgFile != null) {
      Image image = new Image("file:" + imgFile.getAbsolutePath());
      fotoPerfilImageView.setImage(image);
      fotoPerfilButton.setText(imgFile.getName());
    } else {
      Image image = new Image(imgOriginal.toURI().toString());
      fotoPerfilImageView.setImage(image);
      fotoPerfilButton.setText("Selecciona una imagen");
    }
  }
}
