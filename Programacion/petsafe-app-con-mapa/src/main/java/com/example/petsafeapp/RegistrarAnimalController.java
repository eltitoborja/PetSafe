/**
 * Controlador para la vista de registro de animales en la aplicación PetSafeApp.
 * Gestiona la lógica de negocio para registrar animales perdidos/encontrados,
 * incluyendo validación de formularios, selección de imágenes y geocodificación
 * de direcciones para integración con mapas.
 */
package com.example.petsafeapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Callback;
import model.AnimalModel;
import model.ReporteModel;
import model.SituacionModel;
import model.TipoAnimalModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Clase controladora que implementa la funcionalidad de registro de animales.
 * Maneja la interacción entre:
 * - Formulario de registro (campos de texto, combos, calendario)
 * - Selección de imágenes desde el sistema de archivos
 * - Validación de datos e integración con modelos de base de datos
 * - Comunicación con servicios externos de geocodificación
 */
public class RegistrarAnimalController {
    /**
     * ComboBox para seleccionar la situación del animal (perdido/encontrado).
     * @see SituacionModel Modelo de datos para las posibles situaciones
     */
    @javafx.fxml.FXML
    private ComboBox situacionComboBox;

    /**
     * Botón para seleccionar la imagen del animal desde el sistema de archivos.
     */
    @javafx.fxml.FXML
    private Button imagenesButton;

    /**
     * Campo de texto para ingresar el número de contacto del reportante.
     */
    @javafx.fxml.FXML
    private TextField contactoTextField;

    /**
     * Área de texto para descripción detallada del animal.
     * Incluye características físicas y contexto relevante.
     */
    @javafx.fxml.FXML
    private TextArea descripcionTextArea;

    /**
     * ComboBox para seleccionar el tipo de animal (perro, gato, etc.).
     * @see TipoAnimalModel Modelo de datos para los tipos de animales
     */
    @javafx.fxml.FXML
    private ComboBox tipoComboBox;

    /**
     * Selector de fecha para registrar cuándo se vio al animal.
     * Se restringe a fechas no posteriores a la actual.
     */
    @javafx.fxml.FXML
    private DatePicker fechaDatePicker;

    /**
     * Panel contenedor principal para navegación dinámica entre vistas.
     */
    @javafx.fxml.FXML
    private Pane panelContenido;

    /**
     * Archivo de imagen seleccionado para el animal.
     * Almacena temporalmente la imagen antes de la persistencia.
     */
    private File imgFile;

    /**
     * Etiqueta de título de la pantalla actual.
     */
    private Label tituloPantallaLabel;

    /**
     * Gestor de cadenas localizadas para soporte multilingüe.
     */
    private GestorStrings gestorStrings;

    /**
     * Botón de navegación para regresar a la vista anterior.
     */
    private ImageView flechaAtras;

    /**
     * Número de la dirección donde se encontró/vio el animal.
     */
    @javafx.fxml.FXML
    private TextField numeroDireccionTextField;

    /**
     * Calle de la dirección donde se encontró/vio el animal.
     */
    @javafx.fxml.FXML
    private TextField calleDireccionTextField;

    /**
     * Ciudad de la dirección donde se encontró/vio el animal.
     */
    @javafx.fxml.FXML
    private TextField ciudadDireccionTextField;

    /**
     * Usuario actual autenticado que realiza el reporte.
     */
    private Usuario usuario;

    /**
     * Establece la etiqueta de título de la pantalla.
     * @param tituloPantallaLabel Referencia a la etiqueta de título
     */
    public void setTituloPantallaLabel(Label tituloPantallaLabel) {
        this.tituloPantallaLabel = tituloPantallaLabel;
    }

    /**
     * Establece el gestor de cadenas localizadas.
     * @param gestorStrings Instancia del gestor de internacionalización
     */
    public void setGestorStrings(GestorStrings gestorStrings) {
        this.gestorStrings = gestorStrings;
    }

    /**
     * Establece el botón de navegación atrás y configura su visibilidad inicial.
     * @param flechaAtras Referencia al ImageView de la flecha de navegación
     */
    public void setFlechaAtras(ImageView flechaAtras) {
        this.flechaAtras = flechaAtras;
    }

    /**
     * Establece el usuario actual para mantener el contexto de sesión.
     * @param usuario Usuario autenticado en la aplicación
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Inicializa los componentes de la interfaz al cargar la vista.
     * Configura:
     * - Fecha por defecto como la fecha actual
     * - Carga de opciones en los ComboBox
     * - Restricciones de fechas futuras en el DatePicker
     */
    public void initialize() {
        // Establecer fecha actual por defecto
        LocalDate actual = LocalDate.now();
        fechaDatePicker.setValue(actual);
        
        // Cargar opciones de situaciones y tipos de animales
        SituacionModel sm = new SituacionModel();
        for(Situacion s : sm.readSituaciones()) {
            situacionComboBox.getItems().add(s);
        }
        
        TipoAnimalModel tam = new TipoAnimalModel();
        for(TipoAnimal ta : tam.readTipoAnimales()) {
            tipoComboBox.getItems().add(ta);
        }

        // Configurar restricción de fechas futuras
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isAfter(LocalDate.now())) {
                            setDisable(true);
                        }
                    }
                };
            }
        };
        fechaDatePicker.setDayCellFactory(dayCellFactory);
    }

    /**
     * Manejador de evento para seleccionar una imagen del sistema.
     * Abre un diálogo de selección de archivos y actualiza el botón con el nombre del archivo.
     * 
     * @param actionEvent Evento de acción disparado
     */
    @javafx.fxml.FXML
    public void onImagenesButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona la imagen");
        
        // Configurar filtros de archivos
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Todas las imágenes", "*.jpg", "*.png"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        // Mostrar diálogo y obtener archivo seleccionado
        Stage stage = (Stage) panelContenido.getScene().getWindow();
        imgFile = fileChooser.showOpenDialog(stage);
        
        // Actualizar texto del botón con el nombre del archivo
        if (imgFile != null) {
            Image image = new Image("file:" + imgFile.getAbsolutePath());
            imagenesButton.setText(imgFile.getName());
        } else {
            imagenesButton.setText("Seleccionar imagen");
        }
    }

    /**
     * Manejador de evento para registrar un nuevo animal.
     * Valida todos los campos, crea el registro y navega de vuelta al mapa.
     * 
     * @param actionEvent Evento de acción disparado
     * @throws IOException Si ocurre un error al cargar la vista del mapa
     */
    @javafx.fxml.FXML
    public void onAddAnimalButtonClick(ActionEvent actionEvent) throws IOException {
        Alert alertaError = new Alert(Alert.AlertType.ERROR);
        alertaError.setTitle("ERROR");
        alertaError.setHeaderText("Faltan algunos campos");
        
        try {
            // Obtener valores de los campos
            Situacion situacion = (Situacion) situacionComboBox.getValue();
            String descripcion = descripcionTextArea.getText();
            String calleDireccion = calleDireccionTextField.getText();
            String numDireccion = numeroDireccionTextField.getText();
            String ciudadDireccion = ciudadDireccionTextField.getText();
            String direccion = calleDireccion + ", " + numDireccion + ", " + ciudadDireccion;
            TipoAnimal tipo = (TipoAnimal) tipoComboBox.getValue();
            LocalDate fecha = fechaDatePicker.getValue();
            
            // Validar número de contacto
            int contacto;
            try {
                contacto = Integer.valueOf(contactoTextField.getText());
            } catch (NumberFormatException e) {
                alertaError.setHeaderText("Número de contacto inválido");
                alertaError.setContentText("- No has introducido un número de contacto válido.");
                alertaError.showAndWait();
                return;
            }

            // Construir mensaje de errores
            StringBuilder sb = new StringBuilder("Errores:\n");
            if (situacion == null) {
                sb.append("- No se ha seleccionado ninguna situación.\n");
            }
            if(calleDireccion.isEmpty()) {
                sb.append("- No se ha introducido ninguna calle en la dirección.\n");
            }
            if(numDireccion.isEmpty()) {
                sb.append("- No se ha introducido ningún número en la dirección.\n");
            }
            if(ciudadDireccion.isEmpty()) {
                sb.append("- No se ha introducido ninguna ciudad en la dirección.\n");
            }
            if (descripcion.isEmpty()) {
                sb.append("- No se ha introducido ninguna descripción.\n");
            }
            if (tipo == null) {
                sb.append("- No se ha seleccionado ningún tipo de animal.\n");
            }
            if (imgFile == null) {
                sb.append("- No se ha seleccionado ninguna imagen.\n");
            }
            if (fecha == null) {
                sb.append("- No se ha introducido ninguna fecha.\n");
            }
            if (fecha != null && fecha.isAfter(LocalDate.now())) {
                sb.append("- Se ha introducido una fecha en el futuro.\n");
            }
            if (contactoTextField.getText().isEmpty()) {
                sb.append("- No se ha introducido ningún número de contacto.\n");
            }

            // Mostrar errores si existen
            if (sb.toString().contains("-")) {
                alertaError.setContentText(sb.toString());
                alertaError.showAndWait();
                return;
            }

            // Geocodificar dirección
            double[] coord = obtenerCoordenadasDesdeDireccion(direccion);
            if (coord == null) {
                sb.append("- No has introducido una dirección correcta.\n");
                alertaError.setContentText(sb.toString());
                alertaError.showAndWait();
                return;
            }

            // Guardar datos en base de datos
            AnimalModel am = new AnimalModel();
            Animal animal = new Animal(imgFile, fecha, tipo, descripcion, situacion);
            am.createAnimal(animal);
            
            ReporteModel rm = new ReporteModel();
            Animal animalCreado = am.getAnimalConNombreYTipo(descripcion, tipo);
            Reporte reporte = new Reporte(direccion, animalCreado, this.usuario);
            rm.createReporte(reporte);

            // Mostrar confirmación y navegar al mapa
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Animal añadido");
            alerta.setHeaderText("Animal añadido correctamente");
            alerta.setContentText("Se ha añadido correctamente al animal.");
            alerta.showAndWait();

            // Navegar a la vista del mapa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mapaLocalizador-view.fxml"));
            Pane pane = loader.load();
            
            MapaLocalizadorController controller = loader.getController();
            controller.setGestorStrings(this.gestorStrings);
            controller.setFlechaAtras(this.flechaAtras);
            controller.setTituloPantallaLabel(this.tituloPantallaLabel);
            controller.setUsuario(this.usuario);
            controller.loadMapMarkers(reporte.getUbicacion());

            tituloPantallaLabel.setText("Mapa localizador");
            flechaAtras.setVisible(false);
            this.panelContenido.getChildren().setAll(pane);

        } catch (Exception e) {
            alertaError.setHeaderText("Error en el registro");
            alertaError.setContentText("Ocurrió un error al procesar el registro: " + e.getMessage());
            alertaError.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Obtiene coordenadas geográficas desde una dirección usando OpenStreetMap Nominatim.
     * 
     * @param direccion Dirección textual a geocodificar
     * @return Array con [latitud, longitud] o null si falla
     */
    private double[] obtenerCoordenadasDesdeDireccion(String direccion) {
        try {
            String query = URLEncoder.encode(direccion, "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestProperty("User-Agent", "PetSafe");

            Scanner scanner = new Scanner(conn.getInputStream());
            String json = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONArray array = new JSONArray(json);
            if (array.isEmpty()) return null;

            JSONObject obj = array.getJSONObject(0);
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");

            return new double[]{lat, lon};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
