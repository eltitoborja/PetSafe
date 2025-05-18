package com.example.petsafeapp;

import javafx.animation.PauseTransition;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Controlador para la vista del mapa localizador.
 * Gestiona la visualización de un mapa OpenStreetMap dentro de un WebView,
 * la carga de marcadores para reportes de animales perdidos, negocios y protectoras,
 * y la funcionalidad de búsqueda de direcciones en el mapa.
 */
public class MapaLocalizadorController {

  /**
  * Panel principal que contiene la vista actual.
  * Se utiliza para cambiar de pantalla.
  */
  @javafx.fxml.FXML
  private Pane panelContenido;
  /**
  * WebView que muestra el mapa interactivo.
  */
  @javafx.fxml.FXML
  private WebView webViewMapa;
  /**
  * Campo de texto para ingresar la dirección a buscar en el mapa.
  */
  @javafx.fxml.FXML
  private TextField searchDirectionTextField;

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
  * Controlador de la plantilla principal de pantallas, utilizado para delegar acciones como la búsqueda en el mapa.
  */
  private PlantillaPantallasController plantillaController;

  /**
  * Objeto Usuario que representa al usuario actualmente logueado.
  */
  private Usuario usuario;


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
    //flechaAtras.setVisible(true);
  }

  /**
  * Establece el usuario actual.
  * @param usuario El objeto Usuario a establecer.
  */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  /**
  * Método de inicialización del controlador. Se ejecuta después de que los elementos FXML han sido cargados.
  * Actualmente vacío, la lógica de carga del mapa se maneja en {@link #loadMapMarkers(String)}.
  */
  public void initialize() {

  }

  /**
  * Maneja el evento de clic en la imagen "Add".
  * Cambia la vista al formulario de registro de animal.
  * @param event El evento de clic.
  * @throws IOException Si ocurre un error al cargar la vista FXML.
  */
  @javafx.fxml.FXML
  public void onAddImageViewClick(Event event) throws IOException {
    gestorStrings.setVistaFlechaAtras("mapaLocalizador-view.fxml");
    flechaAtras.setVisible(true);
    tituloPantallaLabel.setText("Registrar animal");

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("registrarAnimal-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    RegistrarAnimalController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    controller.setUsuario(this.usuario);

    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Carga el mapa en el WebView y añade marcadores para reportes, locales, protectoras y veterinarios.
  * El mapa se centra inicialmente en una ubicación predeterminada o en la dirección proporcionada.
  * La carga de marcadores ocurre después de que el HTML del mapa se haya cargado completamente.
  * @param direccionInicio La dirección en la que se debe centrar el mapa inicialmente. Puede ser null para usar la ubicación predeterminada.
  */
  public void loadMapMarkers(String direccionInicio) {
    WebEngine engine = webViewMapa.getEngine();
    URL mapaURL = getClass().getResource("/html/mapa_mascotas.html");
    engine.load(mapaURL.toExternalForm());

    // Espera a que se cargue el HTML
    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        // Ejecutar el primer script
        try {
          PauseTransition delayMapa = new PauseTransition(Duration.millis(500));
          delayMapa.setOnFinished(event1 -> {
                if (direccionInicio == null) {
                  engine.executeScript(getScriptMapa());
                } else {
                  engine.executeScript(getScriptMapaRecentrado(direccionInicio));
                }

          // Retraso de 0.01 segundos para que termine de cargar el mapa antes de cargar los marcadores
          PauseTransition delay = new PauseTransition(Duration.millis(500));
          delay.setOnFinished(event -> {
            try {
              engine.executeScript(getScriptReportes());
              engine.executeScript(getScriptLocales());
              engine.executeScript(getScriptProtectoras());
              engine.executeScript(getScriptVeterinarios());
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
          delay.play();
          });
          delayMapa.play();

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
  * Obtiene las coordenadas de latitud y longitud para una dirección dada utilizando el servicio Nominatim de OpenStreetMap.
  * @param direccion La dirección de la cual obtener las coordenadas.
  * @return Un array de doubles donde el primer elemento es la latitud y el segundo es la longitud, o null si no se encuentran coordenadas.
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

  /**
  * Genera el script JavaScript para añadir marcadores de reportes al mapa.
  * Recupera los reportes de animales perdidos o en adopción, obtiene sus coordenadas
  * y crea un array JSON con la información necesaria para el script del mapa.
  * @return El script JavaScript como String.
  * @throws Exception Si ocurre un error al leer los reportes o animales, o al obtener las coordenadas.
  */
  private String getScriptReportes() throws Exception {
    ReporteModel rm = new ReporteModel();
    AnimalModel am = new AnimalModel();
    ArrayList<Reporte> listaReportes = rm.readReportes();
    ArrayList<Animal> listaAnimales = am.readAnimales();
    JSONArray arrayReportes = new JSONArray();
    JSONArray arrayAnimales = new JSONArray();
    for (Reporte r : listaReportes) {
      if (r.getAnimal().getSituacion().getId() != 1) { // Asumiendo que 1 es "Encontrado" o "Adoptado"
        double[] coord = obtenerCoordenadasDesdeDireccion(r.getUbicacion());
        JSONObject obj = new JSONObject();
        obj.put("lat", coord[0]);
        obj.put("lng", coord[1]);
        obj.put("nombre", r.getUsuario().getNombre());//TODO: Comprobar qué nombre obtener
        obj.put("descripcion", r.getAnimal().getDescripción());
        obj.put("enAdopcion", r.getAnimal().getSituacion().getId() == 3); // Asumiendo que 3 es "En Adopción"
        obj.put("tipo", r.getAnimal().getTipo().getId()); // Asumiendo que Tipo tiene un ID relevante
        obj.put("id", r.getId());
        arrayReportes.put(obj);
      }
    }

    for (Animal a : listaAnimales) {
      JSONObject obj = new JSONObject();
      obj.put("id", a.getId());
      obj.put("foto", a.getFoto().toURI().toString());
      arrayAnimales.put(obj);
    }

    return "procesarReportes(" + arrayReportes.toString() + ", " + arrayAnimales + ");";
  }

  /**
  * Genera el script JavaScript para añadir marcadores de locales (negocios que no son veterinarios) al mapa.
  * Recupera los negocios, filtra los que no son veterinarios, obtiene sus coordenadas y
  * crea un array JSON con la información necesaria para el script del mapa.
  * También incluye un array JSON con las fotos de los usuarios.
  * @return El script JavaScript como String.
  * @throws Exception Si ocurre un error al leer los negocios, usuarios o al obtener las coordenadas.
  */
  private String getScriptLocales() throws Exception {
    NegocioModel nm = new NegocioModel();
    ArrayList<Negocio> listaNegocios = nm.readNegocios();

    JSONArray array = new JSONArray();
    for (Negocio n : listaNegocios) {
      if (n.getTipo().getId() != 1) { // Asumiendo que 1 es el ID para Veterinario
        double[] coord = obtenerCoordenadasDesdeDireccion(n.getDireccion());
        JSONObject obj = new JSONObject();
        obj.put("lat", coord[0]);
        obj.put("lng", coord[1]);
        obj.put("nombre", n.getNombreNegocio());
        obj.put("descripcion", n.getDescripcion());
        obj.put("idUsuario", n.getId()); // Asumiendo que el ID del Negocio se usa como idUsuario para la foto
        array.put(obj);
      }
    }

    System.out.println(getUsuariosFotos().toString()); // Imprime las fotos de usuario (para depuración)
    return "procesarLocales(" + array.toString() + ", " + getUsuariosFotos().toString() + ");";
  }

  /**
  * Genera el script JavaScript para añadir marcadores de protectoras al mapa.
  * Recupera las protectoras, obtiene sus coordenadas y crea un array JSON con la información
  * necesaria para el script del mapa. También incluye un array JSON con las fotos de los usuarios.
  * @return El script JavaScript como String.
  * @throws Exception Si ocurre un error al leer las protectoras, usuarios o al obtener las coordenadas.
  */
  private String getScriptProtectoras() throws Exception {
      ProtectoraModel pm = new ProtectoraModel();
      ArrayList<Protectora> listaProtectoras = pm.readProtectoras();

      JSONArray array = new JSONArray();
      for (Protectora p : listaProtectoras) {
        double[] coord = obtenerCoordenadasDesdeDireccion(p.getDireccion());
        JSONObject obj = new JSONObject();
        obj.put("lat", coord[0]);
        obj.put("lng", coord[1]);
        obj.put("nombre", p.getNombreProtectora());
        obj.put("descripcion", p.getDescripcion());
        obj.put("idUsuario", p.getId()); // Asumiendo que el ID de la Protectora se usa como idUsuario para la foto
        array.put(obj);
      }

      return "procesarProtectoras(" + array.toString() + ", " + getUsuariosFotos().toString() + ");";
  }

  /**
  * Genera el script JavaScript para añadir marcadores de veterinarios al mapa.
  * Recupera los negocios, filtra los que son veterinarios, obtiene sus coordenadas y
  * crea un array JSON con la información necesaria para el script del mapa.
  * También incluye un array JSON con las fotos de los usuarios.
  * @return El script JavaScript como String.
  * @throws Exception Si ocurre un error al leer los negocios, usuarios o al obtener las coordenadas.
  */
  private String getScriptVeterinarios() throws Exception {
    NegocioModel nm = new NegocioModel();
    ArrayList<Negocio> listaNegocios = nm.readNegocios();

    JSONArray array = new JSONArray();
    for (Negocio n : listaNegocios) {
      if (n.getTipo().getId() == 1) { // Asumiendo que 1 es el ID para Veterinario
        double[] coord = obtenerCoordenadasDesdeDireccion(n.getDireccion());
        JSONObject obj = new JSONObject();
        obj.put("lat", coord[0]);
        obj.put("lng", coord[1]);
        obj.put("nombre", n.getNombreNegocio());
        obj.put("descripcion", n.getDescripcion());
        obj.put("idUsuario", n.getId()); // Asumiendo que el ID del Negocio se usa como idUsuario para la foto
        array.put(obj);
      }
    }

    return "procesarVeterinarios(" + array.toString() + ", " + getUsuariosFotos().toString() + ");";
  }

  /**
  * Obtiene un array JSON con los IDs de usuario y las URLs de sus fotos.
  * Esto se utiliza para asociar fotos con los marcadores de reportes, locales, protectoras y veterinarios.
  * @return Un JSONArray con objetos JSON que contienen "id" y "foto" para cada usuario.
  * @throws IOException Si ocurre un error al leer los usuarios.
  */
  private JSONArray getUsuariosFotos() throws IOException {
    UsuarioModel um = new UsuarioModel();
    ArrayList<Usuario> listaUsuarios = um.readUsuarios();

    JSONArray array = new JSONArray();
    for (Usuario u : listaUsuarios) {
      JSONObject obj = new JSONObject();
      obj.put("id", u.getId());
      obj.put("foto", u.getFoto().toURI().toString());
      array.put(obj);
    }

    return array;
  }

  /**
  * Genera el script JavaScript para inicializar el mapa centrado en una ubicación predeterminada (Valencia).
  * @return El script JavaScript como String.
  */
  private String getScriptMapa() {
    JSONObject obj = new JSONObject();
    //Coordenadas centradas en valencia
    obj.put("lat", "39.4699");
    obj.put("lon", "-0.3763");
    obj.put("zoom", "10");

    return "procesarMapa(" + obj.toString() + ");";
  }

  /**
  * Genera el script JavaScript para inicializar el mapa centrado en una dirección específica con un zoom alto.
  * Obtiene las coordenadas de la dirección utilizando {@link #obtenerCoordenadasDesdeDireccion(String)}.
  * @param direccion La dirección en la que centrar el mapa.
  * @return El script JavaScript como String.
  */
  private String getScriptMapaRecentrado(String direccion) {
    JSONObject obj = new JSONObject();
    double[] coord = obtenerCoordenadasDesdeDireccion(direccion);

    obj.put("lat", coord[0]);
    obj.put("lon", coord[1]);
    obj.put("zoom", "18");

    return "procesarMapa(" + obj.toString() + ");";
  }

  /**
  * Maneja el evento de clic en la imagen de búsqueda.
  * Obtiene la dirección del campo de texto de búsqueda, verifica si es válida y
  * solicita a la plantilla de pantallas que recargue el mapa centrado en esa dirección.
  * @param event El evento de clic.
  * @throws IOException Si ocurre un error durante el proceso (ej. al obtener coordenadas).
  */
  @javafx.fxml.FXML
  public void onSearchImageClick(Event event) throws IOException {
    //Obtener texto de searchDirectionTextField
    String direccionBusqueda = searchDirectionTextField.getText();
    //Comprobar que la direccion es valida
    if (obtenerCoordenadasDesdeDireccion(direccionBusqueda) != null) {
      //Actualizar el mapa cambiando la direccion de inicio
      plantillaController.mapSearch(direccionBusqueda);
    }
  }

  /**
  * Establece el controlador de la plantilla principal de pantallas.
  * @param controller El controlador de la plantilla a establecer.
  */
  public void setPlantillaController(PlantillaPantallasController controller) {
    plantillaController = controller;
  }
}
