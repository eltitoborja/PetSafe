/**
 * Controlador para la interfaz de añadir citas en la aplicación PetSafe.
 * <p>
 * Gestiona la interacción del usuario con los elementos de la interfaz gráfica,
 * valida los datos de entrada y coordina la creación de nuevas citas mediante
 * el modelo {@link model.CitaModel}. También maneja la actualización de vistas
 * relacionadas y el resaltado de fechas con citas existentes.
 * </p>
 */
package com.example.petsafeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.CitaModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AddCitaController {
    @javafx.fxml.FXML
    private Pane panelRoot;
    @javafx.fxml.FXML
    private DatePicker fechaDatePicker;
    @javafx.fxml.FXML
    private TextArea motivoCitaTextArea;
    @javafx.fxml.FXML
    private TextField nombreAnimalTextField;
    @javafx.fxml.FXML
    private ComboBox horaHoraComboBox;
    @javafx.fxml.FXML
    private ComboBox horaMinutoComboBox;

    private Usuario usuario;
    private TableView citasTableView;
    private DatePicker fechaDatePickerAgenda;
    private final List<LocalDate> fechasAResaltar = new ArrayList<LocalDate>();
    private ObservableList<Cita> citas;

    /**
     * Inicializa los datos necesarios para el funcionamiento del controlador.
     * 
     * @param usuario Usuario actual de la aplicación
     * @param citasTableView Tabla donde se muestran las citas
     * @param fechaDatePickerAgenda Selector de fecha de la agenda principal
     */
    public void setDatos(Usuario usuario, TableView citasTableView, DatePicker fechaDatePickerAgenda) {
        this.usuario = usuario;
        this.citasTableView = citasTableView;
        this.fechaDatePickerAgenda = fechaDatePickerAgenda;
        this.setFecha(this.fechaDatePickerAgenda.getValue());
    }

    /**
     * Establece la fecha inicial en el selector de fechas.
     * 
     * @param localDate Fecha a establecer (si no es nula)
     */
    public void setFecha(LocalDate localDate) {
        if(localDate != null) this.fechaDatePicker.setValue(localDate);
    }

    /**
     * Inicializa los componentes de la interfaz gráfica.
     * <p>
     * Configura los valores disponibles en los ComboBox para selección de hora y minuto,
     * proporcionando un rango completo de valores (0-23 horas y 0-59 minutos).
     * </p>
     */
    public void initialize() {
        // Configuración de opciones para horas
        for(int i = 0; i <= 23; i++) horaHoraComboBox.getItems().add(i);

        // Configuración de opciones para minutos
        for(int i = 0; i <= 59; i++) horaMinutoComboBox.getItems().add(i);
    }

    /**
     * Maneja el evento de clic en el botón "Añadir Cita".
     * <p>
     * Realiza validación de campos obligatorios, muestra errores mediante {@link Alert}
     * y, si la validación es exitosa, crea una nueva cita usando {@link model.CitaModel}.
     * Actualiza las vistas relacionadas y cierra la ventana actual.
     * </p>
     * 
     * @param actionEvent Evento de acción generado por el botón
     */
    @javafx.fxml.FXML
    public void onAddCitaButtonClick(ActionEvent actionEvent) {
        Alert alertaError = new Alert(Alert.AlertType.ERROR);
        alertaError.setTitle("ERROR");
        alertaError.setHeaderText("Faltan algunos campos");

        // Recogida de datos
        LocalDate fecha = fechaDatePicker.getValue();
        String nombreAnimal = nombreAnimalTextField.getText();
        String motivoCita = motivoCitaTextArea.getText();

        // Validación de campos
        StringBuilder sb = new StringBuilder("Errores:\n");
        if (fecha == null) sb.append("- No se ha seleccionado ninguna fecha.\n");
        if (horaHoraComboBox.getValue() == null) sb.append("- No se ha seleccionado ninguna hora.\n");
        if (horaMinutoComboBox.getValue() == null) sb.append("- No se ha seleccionado ningún minuto.\n");
        if (nombreAnimal.isEmpty()) sb.append("- No se ha introducido ningún nombre de animal.\n");
        if (motivoCita.isEmpty()) sb.append("- No se ha introducido ningún motivo de cita.\n");

        alertaError.setContentText(sb.toString());

        if (sb.indexOf("-") >= 0) {
            alertaError.showAndWait();
        } else {
            // Creación de la cita
            LocalTime horaCompleta = LocalTime.of(
                (Integer) horaHoraComboBox.getValue(),
                (Integer) horaMinutoComboBox.getValue()
            );

            CitaModel cm = new CitaModel();
            Cita cita = new Cita(fecha, horaCompleta, nombreAnimal, motivoCita, this.usuario.getId());
            cm.createCita(cita);

            // Actualización de vistas
            fechaDatePickerAgenda.setValue(fecha);
            this.setCitas(this.usuario);
            this.setCitasDia(this.usuario, fecha);

            // Confirmación y cierre
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Cita añadida");
            alerta.setHeaderText("Cita añadida correctamente");
            alerta.showAndWait();

            ((Stage) panelRoot.getScene().getWindow()).close();
        }
    }

    /**
     * Configura la lista de citas y el resaltado de fechas en el calendario.
     * <p>
     * Aplica una CellFactory personalizada al DatePicker para resaltar las fechas
     * que contienen citas existentes usando un color específico (#F4842B).
     * </p>
     * 
     * @param usuario Usuario actual para filtrar las citas
     */
    public void setCitas(Usuario usuario) {
        citas = FXCollections.observableArrayList();
        CitaModel cm = new CitaModel();

        if(cm.readCitas(usuario) != null) {
            // Resaltado de fechas con citas
            fechasAResaltar.clear();
            cm.readCitas(usuario).forEach(c -> fechasAResaltar.add(c.getFecha()));

            Callback<DatePicker, DateCell> dayCellFactory = datePicker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && fechasAResaltar.contains(item)) {
                        setStyle("-fx-background-color: #F4842B; -fx-text-fill: #fff;");
                    } else {
                        setStyle(null);
                    }
                }
            };

            fechaDatePickerAgenda.setDayCellFactory(dayCellFactory);
        }

        // Listener para cambios de fecha
        fechaDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> 
            this.setCitasDia(this.usuario, newVal)
        );
    }

    /**
     * Actualiza la tabla de citas con las citas del día seleccionado.
     * 
     * @param usuario Usuario actual para filtrar las citas
     * @param fecha Fecha específica para cargar las citas
     */
    public void setCitasDia(Usuario usuario, LocalDate fecha) {
        citas = FXCollections.observableArrayList();
        CitaModel cm = new CitaModel();
        ArrayList<Cita> citasDia = cm.readCitasDia(usuario, fecha);

        citasDia.forEach(citas::add);
        citasTableView.setItems(citas.isEmpty() ? FXCollections.emptyObservableList() : citas);
    }
}
