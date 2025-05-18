package com.example.petsafeapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.NegocioModel; // Aunque no se usa directamente, se mantiene por si es relevante en el proyecto.
import model.ProtectoraModel;
import model.TipoNegocioModel; // Aunque no se usa directamente, se mantiene por si es relevante en el proyecto.


import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors; // Aunque no se usa directamente, se mantiene por si es relevante en el proyecto.

/**
 * Controlador para la vista que muestra una lista de protectoras de animales.
 * Permite visualizar las protectoras y seleccionar una para ver sus detalles.
 */
public class ListaProtectorasController {

    /**
     * Columna de la tabla que muestra la información de contacto de la protectora (e.g., email).
     */
    @javafx.fxml.FXML
    private TableColumn contactoColumn;
    /**
     * Columna de la tabla que muestra el nombre de la protectora.
     */
    @javafx.fxml.FXML
    private TableColumn nombreColumn;
    /**
     * Tabla (TableView) que muestra la lista de protectoras.
     */
    @javafx.fxml.FXML
    private TableView listaTabla;
    /**
     * Columna de la tabla que muestra la dirección de la protectora.
     */
    @javafx.fxml.FXML
    private TableColumn direccionColumn;

    /**
     * Etiqueta utilizada para mostrar el título de la pantalla actual.
     * Esta instancia es proporcionada externamente.
     */
    private Label tituloPantallaLabel;
    /**
     * Gestor de cadenas de texto, probablemente utilizado para la internacionalización
     * o para centralizar los textos de la aplicación. Esta instancia es proporcionada externamente.
     */
    private GestorStrings gestorStrings;
    /**
     * ImageView que representa una flecha para retroceder, utilizada para la navegación.
     * Esta instancia es proporcionada externamente.
     */
    private ImageView flechaAtras;


    /**
     * Columna de la tabla que muestra el ID único de la protectora.
     */
    @javafx.fxml.FXML
    private TableColumn idColumn;

    /**
     * Almacena la instancia de la protectora ({@link Protectora}) que ha sido seleccionada
     * por el usuario en la {@link #listaTabla}. Se inicializa con un nuevo objeto Protectora.
     */
    Protectora p = new Protectora();
    /**
     * Botón que, al ser presionado, muestra los detalles de la protectora seleccionada en la tabla.
     * Se habilita cuando se selecciona una protectora.
     */
    @javafx.fxml.FXML
    private Button verButton;
    /**
     * Panel principal que contiene la tabla de protectoras y donde se cargarán otras vistas,
     * como la de detalles de la protectora.
     */
    @javafx.fxml.FXML
    private Pane protectorasTabla;

    /**
     * Método de inicialización invocado por JavaFX después de que todos los elementos
     * FXML han sido inyectados. Configura las columnas de la tabla {@link #listaTabla}
     * para que muestren las propiedades correspondientes de los objetos {@link Protectora}.
     * Carga todas las protectoras desde el {@link ProtectoraModel} y las añade a la tabla.
     * Imprime el ID de la instancia {@link #p} recién inicializada (antes de cualquier selección).
     * Captura y maneja excepciones que puedan ocurrir durante este proceso.
     */
    public void initialize() {
        try {

            // Configurar columnas con PropertyValueFactory
            // IMPORTANTE: Asegúrate de que estos nombres coincidan EXACTAMENTE con los getters de la clase Negocio // Este comentario parece ser un vestigio de otra clase, se refiere a Protectora aquí.
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreProtectora"));
            direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
            contactoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idProtectora"));
            System.out.println(p.getIdProtectora());

            ProtectoraModel modelo = new ProtectoraModel();
            ArrayList<Protectora> prt = modelo.readProtectoras();



            // Filtrar veterinarios // Este comentario parece ser un vestigio; el bucle añade todas las protectoras.
            for (Protectora p : prt) {

                listaTabla.getItems().add(p);
            }

        } catch (Exception e) {
            System.out.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Establece la etiqueta que se utilizará para mostrar el título de la pantalla.
     * @param tituloPantallaLabel La {@link Label} a configurar como título.
     */
    public void setTituloPantallaLabel(Label tituloPantallaLabel) {
        this.tituloPantallaLabel = tituloPantallaLabel;
    }

    /**
     * Establece el gestor de cadenas de texto para este controlador.
     * @param gestorStrings El {@link GestorStrings} a utilizar.
     */
    public void setGestorStrings(GestorStrings gestorStrings) {
        this.gestorStrings = gestorStrings;
    }

    /**
     * Establece el {@link ImageView} que se utilizará como botón de "flecha atrás"
     * para la navegación.
     * @param flechaAtras El {@link ImageView} de la flecha.
     */
    public void setFlechaAtras(ImageView flechaAtras) {
        this.flechaAtras = flechaAtras;
        //flechaAtras.setVisible(true);
    }


    /**
     * Manejador de eventos para cuando se hace clic en una fila de la tabla {@link #listaTabla}.
     * Obtiene la protectora seleccionada, la almacena en la variable {@link #p},
     * y si una protectora es seleccionada (no nula), habilita el botón {@link #verButton}.
     * @param event El evento de selección de la tabla.
     */
    @javafx.fxml.FXML
    public void onClickTabla(Event event) {
        this.p = (Protectora) this.listaTabla.getSelectionModel().getSelectedItem();
        if(this.p !=null) {
            verButton.setDisable(false);
        }

    }

    /**
     * Manejador de eventos para el clic en el botón "Ver" ({@link #verButton}).
     * Carga la vista FXML "Protectora.fxml" y su controlador {@link ProtectoraController}.
     * Llama al método {@code cargarProtectora} del {@link ProtectoraController} con el ID
     * de la protectora seleccionada ({@link #p}).
     * Reemplaza el contenido del panel {@link #protectorasTabla} con la nueva vista cargada.
     * Si la flecha de atrás ({@link #flechaAtras}) está configurada, la hace visible.
     * @param actionEvent El evento de acción generado por el clic en el botón.
     * @throws IOException Si ocurre un error durante la carga del archivo FXML "Protectora.fxml".
     */
    @javafx.fxml.FXML
    public void onVerButtonClick(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("Protectora.fxml"));
        Pane pane = (Pane)loader.load();
        ProtectoraController pc = (ProtectoraController) loader.getController();
        pc.cargarProtectora(this.p.getIdProtectora());
        loader.setController(pc);
        this.protectorasTabla.getChildren().setAll(new Node[]{pane});
        if(this.flechaAtras != null) this.flechaAtras.setVisible(true);
    }
}
