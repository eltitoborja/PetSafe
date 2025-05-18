package com.example.petsafeapp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Controlador principal para la plantilla de pantallas de la aplicación.
 * Gestiona el cambio entre diferentes vistas (paneles) dentro de un contenedor principal,
 * así como la visualización y ocultación de un menú lateral y la flecha de retroceso.
 * Mantiene información sobre el usuario actual y el gestor de strings.
 */
public class PlantillaPantallasController {
 /**
* Etiqueta en la parte superior de la pantalla que muestra el título de la vista actual.
*/
@javafx.fxml.FXML
private Label tituloPantallaLabel;
/**
* Panel principal donde se cargan las diferentes vistas (paneles) de la aplicación.
  */
  @javafx.fxml.FXML
  private Pane panelContenido;
  /**
  * El panel raíz de la escena, utilizado para animaciones o interacciones a nivel global.
  */
  @javafx.fxml.FXML
  private Pane panelRoot;
  /**
  * Panel de fondo para el menú lateral, utilizado para oscurecer el contenido principal.
  */
  @javafx.fxml.FXML
  private Pane fondoMenuPane;
  /**
  * Panel que contiene los elementos del menú lateral.
  */
  @javafx.fxml.FXML
  private Pane menuPane;
  /**
  * ImageView de la flecha de retroceso, visible u oculta según la pantalla.
  */
  @javafx.fxml.FXML
  private ImageView flechaAtras;
  /**
  * Botón en el menú lateral que muestra el nombre del usuario y permite ir al perfil.
  */
  @javafx.fxml.FXML
  private Button nombreUsuarioMenuButton;
  /**
  * ImageView en el menú lateral que muestra la foto de perfil del usuario.
  */
  @javafx.fxml.FXML
  private ImageView fotoPerfilUsuarioMenuImageView;

  /**
  * Gestor de strings utilizado para almacenar y recuperar información de estado, como la vista anterior.
  */
  private GestorStrings gestorStrings = new GestorStrings();
  /**
  * Objeto Usuario que representa al usuario actualmente logueado.
  */
  private Usuario usuario;
  /**
  * Almacena la última dirección buscada en el mapa para mantener el estado al regresar.
  */
  private String direccionBusqueda = null;


  /**
  * Establece el usuario actual, actualiza el nombre y la foto en el menú lateral,
  * oculta el menú inicialmente y carga la vista del mapa localizador como pantalla predeterminada.
  * @param usuario El objeto Usuario a establecer como usuario actual.
  * @throws IOException Si ocurre un error al cargar la vista del mapa localizador.
  */
  public void setUsuario(Usuario usuario) throws IOException {
    // TODO LO RELACIONADO CON EL USUARIO
    this.usuario = usuario;
    nombreUsuarioMenuButton.setText(this.usuario.getNombre());

    Image imagen = new Image(this.usuario.getFoto().toURI().toString());
    fotoPerfilUsuarioMenuImageView.setImage(imagen);

    // oculto el menú
    menuPane.setTranslateX(254);

    // carga "MapaLocalizador" en el panelContenido como predeterminado
    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("mapaLocalizador-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    MapaLocalizadorController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    controller.setUsuario(this.usuario);
    controller.setPlantillaController(this);
    controller.loadMapMarkers(null);

    this.setTituloPantalla("Mapa localizador");
    this.panelContenido.getChildren().setAll(pane);
    mostrarFlecha(false);
  }

  /**
  * Maneja el evento de clic en la flecha de retroceso.
  * Carga la vista almacenada en el gestor de strings como la vista anterior
  * y configura el controlador correspondiente.
  * @param event El evento de clic.
  * @throws IOException Si ocurre un error al cargar la vista FXML.
  */
  @javafx.fxml.FXML
  public void onFlechaAtrasClick(Event event) throws IOException {
    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource(gestorStrings.getVistaFlechaAtras()));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador y pasa los datos
    Object controller = loader.getController();
    switch(gestorStrings.getVistaFlechaAtras()) {
      case "mapaLocalizador-view.fxml":
        ((MapaLocalizadorController) controller).setGestorStrings(gestorStrings);
        ((MapaLocalizadorController) controller).setFlechaAtras(flechaAtras);
        ((MapaLocalizadorController) controller).setTituloPantallaLabel(tituloPantallaLabel);
        ((MapaLocalizadorController) controller).setUsuario(this.usuario);
        ((MapaLocalizadorController) controller).setPlantillaController(this);
        ((MapaLocalizadorController) controller).loadMapMarkers(direccionBusqueda); // Recarga el mapa con la última búsqueda si existe
        setTituloPantalla("Mapa localizador");
        break;
      case "agenda-view.fxml":
        ((AgendaController) controller).setGestorStrings(gestorStrings);
        ((AgendaController) controller).setFlechaAtras(flechaAtras);
        ((AgendaController) controller).setTituloPantallaLabel(tituloPantallaLabel);
        ((AgendaController) controller).setUsuario(this.usuario);
        setTituloPantalla("Agenda");
        break;
      case "listaVeterinario-view.fxml":
        ((ListaVeterinarioController) controller).setGestorStrings(gestorStrings);
        ((ListaVeterinarioController) controller).setFlechaAtras(flechaAtras);
        ((ListaVeterinarioController) controller).setTituloPantallaLabel(tituloPantallaLabel);
        //((ListaVeterinarioController) controller).setUsuario(this.usuario); // Comentado en el código original
        setTituloPantalla("Veterinarios");
        break;
      case "listaLocales-view.fxml":
        ((ListaLocalesController) controller).setGestorStrings(gestorStrings);
        ((ListaLocalesController) controller).setFlechaAtras(flechaAtras);
        ((ListaLocalesController) controller).setTituloPantallaLabel(tituloPantallaLabel);
        //((ListaVeterinarioController) controller).setUsuario(this.usuario); // Comentado en el código original
        setTituloPantalla("Locales PetFriendly");
        break;
      case "listaProtectoras-view.fxml":
        ((ListaProtectorasController) controller).setGestorStrings(gestorStrings);
        ((ListaProtectorasController) controller).setFlechaAtras(flechaAtras);
        ((ListaProtectorasController) controller).setTituloPantallaLabel(tituloPantallaLabel);
        //((ListaVeterinarioController) controller).setUsuario(this.usuario); // Comentado en el código original
        setTituloPantalla("Protectoras");
        break;
    }
    mostrarFlecha(false); // La flecha de atrás se oculta al volver a una pantalla principal

    // this.setTituloPantalla("Mapa localizador"); // Comentado en el código original
    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el icono de menú (tres rayas).
  * Muestra el menú lateral.
  * @param event El evento de clic.
  */
  @javafx.fxml.FXML
  public void onMenuTresRayasClick(Event event) {
    mostrarMenu(true);
  }

  /**
  * Maneja el evento de clic en el botón "Agenda" del menú lateral.
  * Cambia la vista al panel de Agenda, oculta el menú y la flecha de retroceso,
  * y configura el controlador de Agenda.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML de Agenda.
  */
  @javafx.fxml.FXML
  public void onAgendaMenuButtonClick(ActionEvent actionEvent) throws IOException {
    this.setTituloPantalla("Agenda");
    this.mostrarMenu(false);
    this.mostrarFlecha(false); // La flecha de atrás se oculta al ir a una pantalla principal

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("agenda-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    AgendaController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    gestorStrings.setVistaFlechaAtras("agenda-view.fxml"); // Establece la vista actual como destino de la flecha (no se usa al ir *a* agenda)
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    controller.setUsuario(usuario);

    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el botón "Mapa" del menú lateral.
  * Reinicia la dirección de búsqueda del mapa y carga la vista del mapa localizador.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML del mapa.
  */
  @javafx.fxml.FXML
  public void onMapaMenuButtonClick(ActionEvent actionEvent) throws IOException {
    direccionBusqueda = null; // Limpia la búsqueda anterior al volver al mapa desde el menú
    loadMapView();
  }

  /**
  * Maneja el evento de clic en el fondo del menú.
  * Oculta el menú lateral.
  * @param event El evento de clic.
  */
  @javafx.fxml.FXML
  public void cerrarMenu(Event event) {
    mostrarMenu(false);
  }

  /**
  * Maneja el evento de clic en el botón "Veterinarios" del menú lateral.
  * Cambia la vista al panel de Lista de Veterinarios, oculta el menú,
  * y configura el controlador correspondiente.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML de la lista de veterinarios.
  */
  @javafx.fxml.FXML
  public void onVeterinariosMenuButtonClick(ActionEvent actionEvent) throws IOException {
    this.setTituloPantalla("Veterinarios");
    this.mostrarMenu(false);

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("listaVeterinario-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    ListaVeterinarioController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    gestorStrings.setVistaFlechaAtras("listaVeterinario-view.fxml"); // Establece la vista actual como destino de la flecha
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    //((ListaVeterinarioController) controller).setUsuario(this.usuario); // Comentado en el código original


    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el botón "Locales PetFriendly" del menú lateral.
  * Cambia la vista al panel de Lista de Locales, oculta el menú,
  * y configura el controlador correspondiente.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML de la lista de locales.
  */
  @javafx.fxml.FXML
  public void onLocalesPetFriendlyMenuButtonClick(ActionEvent actionEvent) throws IOException {
    this.setTituloPantalla("Locales PetFriendly");
    this.mostrarMenu(false);

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("listaLocales-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    ListaLocalesController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    gestorStrings.setVistaFlechaAtras("listaLocales-view.fxml"); // Establece la vista actual como destino de la flecha
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);

    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el botón "Protectoras" del menú lateral.
  * Cambia la vista al panel de Lista de Protectoras, oculta el menú,
  * y configura el controlador correspondiente.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML de la lista de protectoras.
  */
  @javafx.fxml.FXML
  public void onProtectorasMenuButtonClick(ActionEvent actionEvent) throws IOException {
    this.setTituloPantalla("Protectoras");
    this.mostrarMenu(false);

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("listaProtectoras-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    ListaProtectorasController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    gestorStrings.setVistaFlechaAtras("listaProtectoras-view.fxml"); // Establece la vista actual como destino de la flecha
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);

    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Controla la visibilidad del menú lateral mediante una animación de traslación.
  * También controla la visibilidad del panel de fondo del menú.
  * @param mostrar Booleano que indica si el menú debe mostrarse (true) u ocultarse (false).
  */
  public void mostrarMenu(boolean mostrar) {
    TranslateTransition transicion = new TranslateTransition();
    transicion.setDuration(Duration.seconds(0.5));
    transicion.setNode(menuPane);

    if(mostrar) {
      fondoMenuPane.setVisible(true);
      // el 254 es la anchura del menú, por lo que se ocultaría
      transicion.setToX(transicion.getNode().getLayoutX() - 163); // Mueve el menú hacia la izquierda para mostrarlo
    } else {
      fondoMenuPane.setVisible(false);
      // el 254 es la anchura del menú, por lo que se ocultaría
      transicion.setToX(transicion.getNode().getLayoutX() + 163); // Mueve el menú hacia la derecha para ocultarlo
    }

    transicion.play();
  }

  /**
  * Establece el texto de la etiqueta del título de la pantalla.
  * @param titulo El String que se establecerá como título.
  */
  public void setTituloPantalla(String titulo) {
    tituloPantallaLabel.setText(titulo);
  }

  /**
  * Controla la visibilidad de la flecha de retroceso.
  * @param mostrar Booleano que indica si la flecha debe mostrarse (true) u ocultarse (false).
  */
  public void mostrarFlecha(boolean mostrar) {
    flechaAtras.setVisible(mostrar);
  }

  /**
  * Almacena una dirección de búsqueda para el mapa y recarga la vista del mapa.
  * Utilizado para centrar el mapa en una dirección específica después de una búsqueda.
  * @param direccion La dirección en la que se debe centrar el mapa.
  * @throws IOException Si ocurre un error al cargar la vista del mapa.
  */
  public void mapSearch(String direccion) throws IOException {
    direccionBusqueda = direccion;
    loadMapView();
  }

  /**
  * Carga la vista del mapa localizador en el panel de contenido.
  * Configura el título, oculta la flecha y el menú, y pasa los datos necesarios
  * al controlador del mapa, incluyendo la última dirección buscada si existe.
  * @throws IOException Si ocurre un error al cargar la vista FXML del mapa.
  */
  private void loadMapView() throws IOException {
    this.setTituloPantalla("Mapa localizador");
    this.mostrarFlecha(false);
    this.mostrarMenu(false);

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("mapaLocalizador-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    MapaLocalizadorController controller = loader.getController();

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    controller.setUsuario(this.usuario);
    controller.setPlantillaController(this);
    controller.loadMapMarkers(direccionBusqueda); // Carga los marcadores y centra el mapa en la dirección de búsqueda si existe

    this.panelContenido.getChildren().setAll(pane);
  }

  /**
  * Maneja el evento de clic en el botón con el nombre de usuario en el menú lateral.
  * Cambia la vista al panel del perfil de usuario, oculta el menú, muestra la flecha de retroceso,
  * y configura el controlador del perfil.
  * @param actionEvent El evento de acción.
  * @throws IOException Si ocurre un error al cargar la vista FXML del perfil.
  */
  @javafx.fxml.FXML
  public void onNombreUsuarioMenuButtonClick(ActionEvent actionEvent) throws IOException {
    mostrarMenu(false);
    mostrarFlecha(true);
    gestorStrings.setVistaFlechaAtras("mapaLocalizador-view.fxml"); // Establece el mapa como la vista a la que volver
    flechaAtras.setVisible(true);
    tituloPantallaLabel.setText("Perfil");

    // CAMBIO DE PANTALLA
    FXMLLoader loader = new FXMLLoader(getClass().getResource("perfilUsuario-view.fxml"));

    // 2. Carga el Pane
    Pane pane = loader.load();

    // 3. Obtén el controlador
    PerfilUsuarioController controller = loader.getController();
    controller.setImgMenuLateral(fotoPerfilUsuarioMenuImageView); // Pasa la ImageView del menú lateral para actualizar la foto si cambia

    // 4. Pasa los datos (ejemplo: pasar un usuario)
    controller.setGestorStrings(gestorStrings);
    controller.setFlechaAtras(flechaAtras);
    controller.setTituloPantallaLabel(tituloPantallaLabel);
    controller.setUsuario(this.usuario);

    this.panelContenido.getChildren().setAll(pane);
  }
}
