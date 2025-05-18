package com.example.petsafeapp;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.NegocioModel;

/**
 * Controlador para la vista que muestra los detalles de un veterinario (un tipo específico de negocio).
 * Carga y visualiza la información de un negocio de tipo veterinario, incluyendo su nombre, logo,
 * contacto (email, teléfono) y descripción.
 */
public class VeterinarioController {

  /**
  * Etiqueta que muestra el número de teléfono del veterinario.
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
  * Etiqueta que muestra la dirección del veterinario.
  */
  @javafx.fxml.FXML
  private Label direccionLabel;
  /**
  * ImageView que muestra el logo o foto del veterinario.
  */
  @javafx.fxml.FXML
  private ImageView logoImageView;
  /**
  * Etiqueta que muestra la descripción del veterinario.
  */
  @javafx.fxml.FXML
  private Label descipcionLabel;
  /**
  * Etiqueta que muestra el correo electrónico del veterinario.
  */
  @javafx.fxml.FXML
  private Label emailLabel;
  /**
  * ImageView para el icono asociado a la dirección.
  */
  @javafx.fxml.FXML
  private ImageView direccionImageView;
  /**
  * Panel principal que contiene los elementos de la vista del veterinario.
  */
  @javafx.fxml.FXML
  private Pane panelVeterinario;
  /**
  * Etiqueta que muestra el nombre del veterinario.
  */
  @javafx.fxml.FXML
  private Label nombreLabel;


  /**
  * Carga los datos de un negocio de tipo veterinario específico utilizando su ID
  * y actualiza los elementos de la interfaz gráfica para mostrar su información.
  * @param id El ID del negocio (veterinario) cuyos datos se van a cargar.
  */
  public void cargarVeterinario(int id){
    System.out.println("ID --> " + id);
    NegocioModel nm = new NegocioModel();
    Negocio n = nm.getNegocioById(id); // Asume que getNegocioById puede devolver un Negocio de tipo Veterinario
    Image imagen = new Image(n.getFoto().toURI().toString());
    logoImageView.setImage(imagen);
    nombreLabel.setText(n.getNombreNegocio());
    Image imagencorreo = new Image(getClass().getResource("/images/carta.png").toExternalForm());
    correoImageView.setImage(imagencorreo);
    emailLabel.setText(n.getEmail());
    telefonoLabel.setText(n.getNumTel());
    Image imagentelf = new Image(getClass().getResource("/images/contacto.png").toExternalForm());
    telefonoImageView.setImage(imagentelf);
    Image imagendireccion = new Image(getClass().getResource("/images/ubicacion.png").toExternalForm());
    direccionImageView.setImage(imagendireccion);
    direccionLabel.setText(n.getDireccion());
    descipcionLabel.setText(n.getDescripcion());


  }
}
