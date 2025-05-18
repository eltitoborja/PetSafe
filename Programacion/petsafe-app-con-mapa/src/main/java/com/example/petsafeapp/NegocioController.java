package com.example.petsafeapp;

/**
 * Controlador para la visualización de información de negocios en la aplicación PetSafe.
 * Gestiona la interfaz que muestra los detalles de un negocio, como su nombre, logo,
 * información de contacto y dirección.
 */
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.NegocioModel;

public class NegocioController {
    /**
     * Label para mostrar el número de teléfono del negocio.
     */
    @javafx.fxml.FXML
    private Label telefonoLabel;
    
    /**
     * ImageView para mostrar el icono del teléfono.
     */
    @javafx.fxml.FXML
    private ImageView telefonoImageView;
    
    /**
     * ImageView para mostrar el icono del correo electrónico.
     */
    @javafx.fxml.FXML
    private ImageView correoImageView;
    
    /**
     * Label para mostrar la dirección del negocio.
     */
    @javafx.fxml.FXML
    private Label direccionLabel;
    
    /**
     * ImageView para mostrar el logo del negocio.
     */
    @javafx.fxml.FXML
    private ImageView logoImageView;
    
    /**
     * Label para mostrar la descripción del negocio.
     */
    @javafx.fxml.FXML
    private Label descipcionLabel;
    
    /**
     * Label para mostrar el correo electrónico del negocio.
     */
    @javafx.fxml.FXML
    private Label emailLabel;
    
    /**
     * ImageView para mostrar el icono de la dirección.
     */
    @javafx.fxml.FXML
    private ImageView direccionImageView;
    
    /**
     * Panel principal que contiene todos los elementos de la interfaz del negocio.
     */
    @javafx.fxml.FXML
    private Pane panelNegocio;
    
    /**
     * Label para mostrar el nombre del negocio.
     */
    @javafx.fxml.FXML
    private Label nombreLabel;
    
    /**
     * Carga y muestra la información de un negocio específico en la interfaz.
     * Obtiene los datos del negocio a partir de su ID y configura los elementos
     * visuales correspondientes con la información recuperada.
     * 
     * @param id Identificador único del negocio a cargar
     */
    public void cargarNegocio(int id){
        NegocioModel nm = new NegocioModel();
        Negocio n = nm.getNegocioById(id);
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
