package com.example.petsafeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.CitaModel;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la vista de la agenda. Gestiona la visualización, creación,
 * modificación y eliminación de citas para un usuario.
 */
public class AgendaController {

    /**
     * Panel principal que contiene los elementos de la interfaz de la agenda.
     */
    @javafx.fxml.FXML
    private Pane panelContenido;
    /**
     * Selector de fecha para elegir el día a visualizar o para el cual se creará una cita.
     */
    @javafx.fxml.FXML
    private DatePicker fechaDatePicker;
    /**
     * Columna de la tabla que muestra la fecha de la cita.
     */
    @javafx.fxml.FXML
    private TableColumn fechaTableColumn;
    /**
     * Columna de la tabla que muestra el nombre del animal asociado a la cita.
     */
    @javafx.fxml.FXML
    private TableColumn animalTableColumn;
    /**
     * Columna de la tabla que muestra la hora de la cita.
     */
    @javafx.fxml.FXML
    private TableColumn horaTableColumn;
    /**
     * Columna de la tabla que muestra el motivo de la cita.
     */
    @javafx.fxml.FXML
    private TableColumn motivoTableColumn;
    /**
     * Tabla que muestra la lista de citas para el día seleccionado.
     */
    @javafx.fxml.FXML
    private TableView citasTableView;

    /**
     * Etiqueta para mostrar el título de la pantalla actual.
     */
    private Label tituloPantallaLabel;
    /**
     * Objeto para gestionar cadenas de texto, posiblemente para internacionalización.
     */
    private GestorStrings gestorStrings;
    /**
     * ImageView para el icono de la flecha "atrás", para navegación.
     */
    private ImageView flechaAtras;
    /**
     * Objeto Usuario que representa al usuario actualmente logueado.
     */
    private Usuario usuario;

    /**
     * Lista de fechas que deben ser resaltadas en el DatePicker (e.g., días con citas).
     */
    private List<LocalDate> fechasAResaltar = new ArrayList<LocalDate>();
    /**
     * Lista observable de objetos Cita, utilizada para poblar el TableView.
     */
    private ObservableList<Cita> citas;

    /**
     * Establece la etiqueta del título de la pantalla.
     * @param tituloPantallaLabel La etiqueta donde se mostrará el título.
     */
    public void setTituloPantallaLabel(Label tituloPantallaLabel) {
        this.tituloPantallaLabel = tituloPantallaLabel;
    }

    /**
     * Establece el gestor de cadenas.
     * @param gestorStrings El objeto GestorStrings a utilizar.
     */
    public void setGestorStrings(GestorStrings gestorStrings) {
        this.gestorStrings = gestorStrings;
    }

    /**
     * Establece el ImageView para la flecha de "atrás".
     * @param flechaAtras El ImageView de la flecha.
     */
    public void setFlechaAtras(ImageView flechaAtras) {
        this.flechaAtras = flechaAtras;
        //flechaAtras.setVisible(true); // Código original comentado
    }

    /**
     * Establece el usuario actual y carga sus citas.
     * @param usuario El usuario para el cual se mostrará la agenda.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.setCitas(this.usuario);
    }

    /**
     * Configura las citas para el usuario especificado.
     * Inicializa la lista de citas y configura el DatePicker para resaltar los días
     * que tienen citas programadas. También establece un listener para cuando
     * cambia la fecha seleccionada en el DatePicker.
     * @param usuario El usuario para el cual se cargarán las citas.
     */
    public void setCitas(Usuario usuario) {
        citas = FXCollections.observableArrayList();
        fechasAResaltar = new ArrayList<LocalDate>();

        // fechas resaltadas
        CitaModel cm = new CitaModel();
        if(cm.readCitas(usuario) != null) {
            for (Cita c : cm.readCitas(usuario))
                fechasAResaltar.add(c.getFecha());

            Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);

                            // Si la celda no está vacía y la fecha está en nuestra lista
                            if (!empty && item != null && fechasAResaltar.contains(item)) {
                                // Aplica un estilo de fondo
                                setStyle("-fx-background-color: #F4842B; -fx-text-fill: #fff;");
                            } else {
                                // Asegúrate de resetear el estilo para las fechas que no deben estar coloreadas
                                setStyle(null);
                                getStyleClass().remove("mi-clase-resaltada");
                            }

                        }
                    };
                }
            };

            // Asigna la CellFactory al DatePicker
            fechaDatePicker.setDayCellFactory(dayCellFactory);
        }

        // cuando cambia la fecha en la agenda
        fechaDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setCitasDia(this.usuario, newValue);
        });
    }

    /**
     * Carga y muestra las citas para un usuario y una fecha específicos en el TableView.
     * Si no hay citas para ese día, limpia la tabla.
     * @param usuario El usuario para el cual se buscan las citas.
     * @param fecha La fecha específica para la cual se cargarán las citas.
     */
    public void setCitasDia(Usuario usuario, LocalDate fecha) {
        citas = FXCollections.observableArrayList();

        CitaModel cm = new CitaModel();
        ArrayList<Cita> citasDia = cm.readCitasDia(usuario, fecha);

        for(Cita c : citasDia) citas.add(c);

        if(!citasDia.isEmpty())
            citasTableView.setItems(citas);
        else
            citasTableView.getItems().clear();
    }

    /**
     * Método de inicialización llamado automáticamente después de que los campos FXML han sido inyectados.
     * Configura las fábricas de celdas (cell value factories) para las columnas del TableView,
     * enlazando las propiedades de los objetos Cita con las columnas correspondientes.
     */
    public void initialize() {
        // columnas TableView
        this.fechaTableColumn.setCellValueFactory(new PropertyValueFactory("fecha"));
        this.horaTableColumn.setCellValueFactory(new PropertyValueFactory("hora"));
        this.animalTableColumn.setCellValueFactory(new PropertyValueFactory("nombreAnimal"));
        this.motivoTableColumn.setCellValueFactory(new PropertyValueFactory("motivo"));
    }

    /**
     * Maneja el evento de clic en el botón "Modificar".
     * Abre una nueva ventana para modificar la cita seleccionada en el TableView.
     * Muestra un mensaje de error si no se ha seleccionado ninguna cita.
     * @param actionEvent El evento de acción que disparó este método.
     * @throws IOException Si ocurre un error al cargar el archivo FXML para la ventana de modificación.
     */
    @javafx.fxml.FXML
    public void onModificarButtonClick(ActionEvent actionEvent) throws IOException {
        Cita citaSeleccionada = (Cita) this.citasTableView.getSelectionModel().getSelectedItem();

        if(citaSeleccionada != null) {
            // SI HAY CITA SELECCIONADA
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("modificarCita-view.fxml"));
            Stage nuevaVentana = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 411, 469);

            // obtengo el controlador de addCita y le paso los datos necesarios
            ModificarCitaController controller = fxmlLoader.getController();
            controller.setDatos(usuario, citasTableView, fechaDatePicker, citaSeleccionada);

            nuevaVentana.setTitle("Modificar cita");

            Image logo = new Image(getClass().getResource("/images/logo-app.png").toExternalForm());
            nuevaVentana.getIcons().add(logo);

            nuevaVentana.setScene(scene);
            nuevaVentana.show();
        } else {
            // SI NO HAY CITA SELECCIONADA
            Alert alertaError = new Alert(Alert.AlertType.ERROR);

            alertaError.setTitle("ERROR");
            alertaError.setHeaderText("Selecciona una cita");
            alertaError.setContentText("Debes seleccionar una cita para poder modificarla.");

            alertaError.showAndWait();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Ver".
     * Muestra un diálogo de información con los detalles de la cita seleccionada en el TableView.
     * Muestra un mensaje de error si no se ha seleccionado ninguna cita.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void onVerButtonClick(ActionEvent actionEvent) {
        Cita citaSeleccionada = (Cita) this.citasTableView.getSelectionModel().getSelectedItem();

        if(citaSeleccionada != null) {
            // SI HAY CITA SELECCIONADA
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaFormateada = citaSeleccionada.getFecha().format(formatoFecha);

            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
            String horaFormateada = citaSeleccionada.getHora().format(formatoHora);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);

            alerta.setTitle("Información de la cita");
            alerta.setHeaderText("Cita");
            alerta.setContentText("- Fecha:\t\t\t" + fechaFormateada + "\n- Hora:\t\t\t" + horaFormateada + "\n- Nombre del animal:\t" + citaSeleccionada.getNombreAnimal() + "\n- Motivo de la cita:\n" + citaSeleccionada.getMotivo());

            alerta.showAndWait();
        } else {
            // SI NO HAY CITA SELECCIONADA
            Alert alertaError = new Alert(Alert.AlertType.ERROR);

            alertaError.setTitle("ERROR");
            alertaError.setHeaderText("Selecciona una cita");
            alertaError.setContentText("Debes seleccionar una cita para poder visualizarla.");

            alertaError.showAndWait();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Eliminar".
     * Pide confirmación al usuario y, si se confirma, elimina la cita seleccionada del TableView y de la base de datos.
     * Actualiza la vista de citas después de la eliminación.
     * Muestra un mensaje de error si no se ha seleccionado ninguna cita.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void onEliminarButtonClick(ActionEvent actionEvent) {
        Cita citaSeleccionada = (Cita) this.citasTableView.getSelectionModel().getSelectedItem();

        if(citaSeleccionada != null) {
            // SI HAY CITA SELECCIONADA
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaFormateada = citaSeleccionada.getFecha().format(formatoFecha);

            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
            String horaFormateada = citaSeleccionada.getHora().format(formatoHora);

            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);

            alerta.setTitle("Confirmar eliminación");
            alerta.setHeaderText("Eliminar cita");
            alerta.setContentText("¿Eliminar cita del día " + fechaFormateada + " a las " + horaFormateada + "?");

            alerta.showAndWait();

            if(alerta.getResult() == ButtonType.OK) {
                CitaModel cm = new CitaModel();
                cm.deleteCita(citaSeleccionada);

                this.setCitas(this.usuario);
                this.setCitasDia(this.usuario, fechaDatePicker.getValue());
            }
        } else {
            // SI NO HAY CITA SELECCIONADA
            Alert alertaError = new Alert(Alert.AlertType.ERROR);

            alertaError.setTitle("ERROR");
            alertaError.setHeaderText("Selecciona una cita");
            alertaError.setContentText("Debes seleccionar una cita para poder eliminarla.");

            alertaError.showAndWait();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Nueva Cita".
     * Abre una nueva ventana para que el usuario pueda añadir una nueva cita.
     * Pasa el usuario actual y referencias a los componentes de la agenda al controlador de la nueva ventana.
     * @param actionEvent El evento de acción que disparó este método.
     * @throws IOException Si ocurre un error al cargar el archivo FXML para la ventana de añadir cita.
     */
    @javafx.fxml.FXML
    public void onNuevaCitaButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addCita-view.fxml"));
        Stage nuevaVentana = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 411, 469);

        // obtengo el controlador de addCita y le paso los datos necesarios
        AddCitaController controller = fxmlLoader.getController();
        controller.setDatos(this.usuario, this.citasTableView, this.fechaDatePicker);

        nuevaVentana.setTitle("Añadir nueva cita");

        Image logo = new Image(getClass().getResource("/images/logo-app.png").toExternalForm());
        nuevaVentana.getIcons().add(logo);

        nuevaVentana.setScene(scene);
        nuevaVentana.show();
    }
}
