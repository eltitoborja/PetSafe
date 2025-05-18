package com.example.petsafeapp;

/**
 * Controlador para la modificación de citas veterinarias en la aplicación PetSafe.
 * Permite al usuario editar los detalles de una cita existente, como la fecha, hora,
 * nombre del animal y motivo de la cita.
 */
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

public class ModificarCitaController {
    @javafx.fxml.FXML
    private Pane panelRoot;
    @javafx.fxml.FXML
    private ComboBox horaHoraComboBox;
    @javafx.fxml.FXML
    private DatePicker fechaDatePicker;
    @javafx.fxml.FXML
    private TextArea motivoCitaTextArea;
    @javafx.fxml.FXML
    private ComboBox horaMinutoComboBox;
    @javafx.fxml.FXML
    private TextField nombreAnimalTextField;

    private Usuario usuario;
    private TableView citasTableView;
    private DatePicker fechaDatePickerAgenda;

    private final List<LocalDate> fechasAResaltar = new ArrayList<LocalDate>();
    private ObservableList<Cita> citas;
    private Cita citaSeleccionada;

    /**
     * Establece los datos iniciales para la modificación de la cita.
     * Configura los campos del formulario con los valores actuales de la cita seleccionada.
     * 
     * @param usuario Usuario que está modificando la cita
     * @param citasTableView Tabla de citas donde se mostrarán las citas
     * @param fechaDatePickerAgenda Selector de fecha de la agenda principal
     * @param citaSeleccionada Cita que se va a modificar
     */
    public void setDatos(Usuario usuario, TableView citasTableView, DatePicker fechaDatePickerAgenda, Cita citaSeleccionada) {
        this.usuario = usuario;
        this.citasTableView = citasTableView;
        this.fechaDatePickerAgenda = fechaDatePickerAgenda;
        this.citaSeleccionada = citaSeleccionada;

        this.setFecha(this.citaSeleccionada.getFecha());
        int hora = this.citaSeleccionada.getHora().getHour();
        int minuto = this.citaSeleccionada.getHora().getMinute();
        String nombreAnimal = this.citaSeleccionada.getNombreAnimal();
        String motivoCita = this.citaSeleccionada.getMotivo();

        horaHoraComboBox.setValue(hora);
        horaMinutoComboBox.setValue(minuto);
        nombreAnimalTextField.setText(nombreAnimal);
        motivoCitaTextArea.setText(motivoCita);
    }

    /**
     * Establece la fecha en el selector de fechas.
     * 
     * @param localDate Fecha a establecer en el selector
     */
    public void setFecha(LocalDate localDate) {
        if(localDate != null) this.fechaDatePicker.setValue(localDate);
    }

    /**
     * Método de inicialización del controlador.
     * Configura las opciones disponibles en los ComboBox para seleccionar hora y minuto.
     * Este método se llama automáticamente después de cargar el archivo FXML.
     */
    public void initialize() {
        // pongo las posibles opciones en los ComboBox
        // HORA
        for(int i = 0; i <= 23; i++) horaHoraComboBox.getItems().add(i);

        // MINUTO
        for(int i = 0; i <= 59; i++) horaMinutoComboBox.getItems().add(i);
    }

    /**
     * Configura las citas para un usuario específico.
     * Resalta las fechas en el calendario que tienen citas programadas.
     * 
     * @param usuario Usuario cuyas citas se van a configurar
     */
    public void setCitas(Usuario usuario) {
        citas = FXCollections.observableArrayList();

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
                                // Aplica un estilo de fondo (por ejemplo, amarillo)
                                setStyle("-fx-background-color: #F4842B; -fx-text-fill: #fff;"); // Color amarillo
                            } else {
                                // Asegúrate de resetear el estilo para las fechas que no deben estar coloreadas
                                setStyle(null);
                                // También puedes remover clases de estilo si usas CSS externo
                                getStyleClass().remove("mi-clase-resaltada");
                            }

                            // Opcional: Deshabilitar fechas pasadas
                            // if (item.isBefore(LocalDate.now())) {
                            //     setDisable(true);
                            //     setStyle("-fx-background-color: #ffc0cb;"); // Fondo rosa para días pasados deshabilitados
                            // }
                        }
                    };
                }
            };

            // Asigna la CellFactory al DatePicker
            fechaDatePickerAgenda.setDayCellFactory(dayCellFactory);
        }

        // cuando cambia la fecha en la agenda
        fechaDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.setCitasDia(this.usuario, newValue);
        });
    }

    /**
     * Configura las citas para un día específico.
     * Actualiza la tabla de citas con las citas correspondientes a la fecha seleccionada.
     * 
     * @param usuario Usuario cuyas citas se van a mostrar
     * @param fecha Fecha específica para mostrar las citas
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
     * Maneja el evento de clic en el botón para modificar una cita.
     * Valida los datos ingresados, actualiza la cita en la base de datos y cierra la ventana.
     * 
     * @param actionEvent Evento que desencadena la acción
     */
    @javafx.fxml.FXML
    public void onModificarCitaButtonClick(ActionEvent actionEvent) {

        Alert alertaError = new Alert(Alert.AlertType.ERROR);

        alertaError.setTitle("ERROR");
        alertaError.setHeaderText("Faltan algunos campos");

        LocalDate fecha = fechaDatePicker.getValue();
        String nombreAnimal = nombreAnimalTextField.getText();
        String motivoCita = motivoCitaTextArea.getText();

        // CONTROL DE ERRORES

        StringBuilder sb = new StringBuilder("Errores:\n");
        if (fecha == null) {
            sb.append("- No se ha seleccionado ninguna fecha.\n");
        }
        if (horaHoraComboBox.getValue() == null) {
            sb.append("- No se ha seleccionado ninguna hora.\n");
        }
        if (horaMinutoComboBox.getValue() == null) {
            sb.append("- No se ha seleccionado ningún minuto.\n");
        }
        if (nombreAnimal.length() <= 0) {
            sb.append("- No se ha introducido ningún nombre de animal.\n");
        }
        if (motivoCita.length() <= 0) {
            sb.append("- No se ha introducido ningún motivo de cita.\n");
        }

        alertaError.setContentText(sb.toString());

        if (sb.indexOf("-") >= 0) {
            // SI HAY ERROR
            alertaError.showAndWait();
        } else {
            int hora = (Integer) horaHoraComboBox.getValue();
            int minuto = (Integer) horaMinutoComboBox.getValue();
            LocalTime horaCompleta = LocalTime.of(hora, minuto);

            CitaModel cm = new CitaModel();

            citaSeleccionada.setFecha(fecha);
            citaSeleccionada.setHora(horaCompleta);
            citaSeleccionada.setNombreAnimal(nombreAnimal);
            citaSeleccionada.setMotivo(motivoCita);

            cm.updateCita(citaSeleccionada);

            fechaDatePickerAgenda.setValue(fechaDatePicker.getValue());
            this.setCitas(this.usuario);
            this.setCitasDia(this.usuario, this.fechaDatePicker.getValue());

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);

            alerta.setTitle("Cita modificada");
            alerta.setHeaderText("Cita modificada correctamente");
            alerta.setContentText("Tu cita se ha modificado correctamente.");

            alerta.showAndWait();

            Stage stage = (Stage) panelRoot.getScene().getWindow();
            stage.close();
        }
    }
}
