package com.example.petsafeapp;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.NegocioModel;
import model.ProtectoraModel;

/**
 * Controlador para la vista que muestra los detalles de una protectora específica.
 * Carga y visualiza la información de una protectora, incluyendo su nombre, foto,
 * contacto (email, teléfono) y descripción.
 */
public class ProtectoraController {

  /**
  * Etiqueta que muestra el número de teléfono de la protectora.
  */
  @javafx.fxml.FXML
  private Label telefonoLabel;
  /**
  * ImageView para el icono asociado al teléfono.
  */
  @javafx.fxml.FXML
  private ImageView telefonoImageView;
  /**
  * ImageView para el icono asociado al correo electrónico.
  */
  @javafx.fxml.FXML
  private ImageView correoImageView;
  /**
  * Etiqueta que muestra la dirección de la protectora.
  */
  @javafx.fxml.FXML
  private Label direccionLabel;
  /**
  * ImageView que muestra el logo o foto de la protectora.
  */
  @javafx.fxml.FXML
  private ImageView logoImageView;
  /**
  * Etiqueta que muestra la descripción de la protectora.
  */
  @javafx.fxml.FXML
  private Label descipcionLabel;
  /**
  * Etiqueta que muestra el correo electrónico de la protectora.
  */
  @javafx.fxml.FXML
  private Label emailLabel;
  /**
  * ImageView para el icono asociado a la dirección.
  */
  @javafx.fxml.FXML
  private ImageView direccionImageView;
  /**
  * Panel principal que contiene los elementos de la vista de la protectora.
  */
  @javafx.fxml.FXML
  private Pane panelProtectora;
  /**
  * Etiqueta que muestra el nombre de la protectora.
  */
  @javafx.fxml.FXML
  private Label nombreLabel;

  /**
  * Carga los datos de una protectora específica utilizando su ID
  * y actualiza los elementos de la interfaz gráfica para mostrar su información.
  * @param id El ID de la protectora cuyos datos se van a cargar.
  */
  public void cargarProtectora(int id){
    ProtectoraModel pm = new ProtectoraModel();
    Protectora p = pm.getProtectoraById(id);
    Image imagen = new Image(p.getFoto().toURI().toString());
    logoImageView.setImage(imagen);
    nombreLabel.setText(p.getNombreProtectora());
    Image imagencorreo = new Image(getClass().getResource("/images/carta.png").toExternalForm());
    correoImageView.setImage(imagencorreo);
    emailLabel.setText(p.getEmail());
    telefonoLabel.setText(p.getNumTel());
    Image imagentelf = new Image(getClass().getResource("/images/contacto.png").toExternalForm());
    telefonoImageView.setImage(imagentelf);
    Image imagendireccion = new Image(getClass().getResource("/images/ubicacion.png").toExternalForm());
    direccionImageView.setImage(imagendireccion);
    direccionLabel.setText(p.getDireccion());
    descipcionLabel.setText(p.getDescripcion());


  }
}
