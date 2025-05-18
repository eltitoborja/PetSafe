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
import model.NegocioModel;
import model.TipoNegocioModel; // Aunque no se usa directamente, se mantiene por si es relevante en el proyecto.


import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors; // Aunque no se usa directamente, se mantiene por si es relevante en el proyecto.

/**
 * Controlador para la vista que muestra una lista de negocios de tipo veterinario.
 * Permite visualizar los veterinarios y seleccionar uno para ver sus detalles.
 */
public class ListaVeterinarioController {

    /**
     * Columna de la tabla que muestra la información de contacto del veterinario (e.g., email).
     * Se enlaza a la propiedad 'email' del objeto {@link Negocio}.
     */
    @javafx.fxml.FXML
    private TableColumn contactoColumn;
    /**
     * Panel principal que contiene la tabla de veterinarios y donde se cargarán otras vistas,
     * como la de detalles del veterinario.
     */
    @javafx.fxml.FXML
    private Pane veterinarioTabla;
    /**
     * Columna de la tabla que muestra el nombre del veterinario.
     * Se enlaza a la propiedad 'nombreNegocio' del objeto {@link Negocio}.
     */
    @javafx.fxml.FXML
    private TableColumn nombreColumn;
    /**
     * Tabla (TableView) que muestra la lista de veterinarios.
     * Los ítems de esta tabla son objetos de tipo {@link Negocio}.
     */
    @javafx.fxml.FXML
    private TableView listaTabla;
    /**
     * Columna de la tabla que muestra la dirección del veterinario.
     * Se enlaza a la propiedad 'direccion' del objeto {@link Negocio}.
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
     * Columna de la tabla que muestra el ID único del negocio veterinario.
     * Se enlaza a la propiedad 'idNegocio' del objeto {@link Negocio}.
     */
    @javafx.fxml.FXML
    private TableColumn idColumn;
    /**
     * Botón que, al ser presionado, muestra los detalles del veterinario seleccionado en la tabla.
     * Se habilita cuando se selecciona un veterinario.
     */
    @javafx.fxml.FXML
    private Button verButton;

    /**
     * Almacena la instancia del negocio ({@link Negocio}) que representa al veterinario
     * seleccionado por el usuario en la {@link #listaTabla}.
     */
    Negocio n ;

    /**
     * Método de inicialización invocado por JavaFX después de que todos los elementos
     * FXML han sido inyectados. Configura las columnas de la tabla {@link #listaTabla}
     * para que muestren las propiedades correspondientes de los objetos {@link Negocio}.
     * Carga todos los negocios desde el {@link NegocioModel}, filtra aquellos cuyo tipo
     * de negocio tiene ID 1 (identificándolos como veterinarios) y los añade a la tabla.
     * Captura y maneja excepciones que puedan ocurrir durante este proceso.
     */
    public void initialize() {
        try {

            // Configurar columnas con PropertyValueFactory
            // IMPORTANTE: Asegúrate de que estos nombres coincidan EXACTAMENTE con los getters de la clase Negocio
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreNegocio"));
            direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
            contactoColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idNegocio"));
            System.out.println(idColumn);

            NegocioModel modelo = new NegocioModel();
            ArrayList<Negocio> todosNegocios = modelo.readNegocios();

            // Crear lista observable para los veterinarios // Esta lista no se usa directamente para poblar la tabla aquí.
            ObservableList<Negocio> veterinarios = FXCollections.observableArrayList();

            // Filtrar veterinarios
            for (Negocio negocio : todosNegocios) {
                if (negocio.getTipo() != null && negocio.getTipo().getId() == 1) {
                    listaTabla.getItems().add(negocio);
                }
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
        //flechaAtras.setVisible(false);
    }

    /**
     * Manejador de eventos para cuando se hace clic en una fila de la tabla {@link #listaTabla}.
     * Obtiene el negocio (veterinario) seleccionado, lo almacena en la variable {@link #n},
     * y si un veterinario es seleccionado (no nulo), habilita el botón {@link #verButton}.
     * @param event El evento de selección de la tabla.
     */
    @javafx.fxml.FXML
    public void onClickTabla(Event event) {
        this.n = (Negocio) this.listaTabla.getSelectionModel().getSelectedItem();
        if(this.n !=null) {
            verButton.setDisable(false);
        }

    }

    /**
     * Manejador de eventos para el clic en el botón "Ver" ({@link #verButton}).
     * Carga la vista FXML "Veterinario.fxml" y su controlador {@link VeterinarioController}.
     * Llama al método {@code cargarVeterinario} del {@link VeterinarioController} con el ID
     * del negocio (veterinario) seleccionado ({@link #n}).
     * Reemplaza el contenido del panel {@link #veterinarioTabla} con la nueva vista cargada.
     * Si la flecha de atrás ({@link #flechaAtras}) está configurada, la hace visible.
     * @param actionEvent El evento de acción generado por el clic en el botón.
     * @throws IOException Si ocurre un error durante la carga del archivo FXML "Veterinario.fxml".
     */
    @javafx.fxml.FXML
    public void onVerButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("Veterinario.fxml"));
        Pane pane = (Pane)loader.load();
        VeterinarioController nc = (VeterinarioController)loader.getController();
        nc.cargarVeterinario(this.n.getIdNegocio());
        loader.setController(nc);
        this.veterinarioTabla.getChildren().setAll(new Node[]{pane});
        if(this.flechaAtras != null) this.flechaAtras.setVisible(true);
    }
}
