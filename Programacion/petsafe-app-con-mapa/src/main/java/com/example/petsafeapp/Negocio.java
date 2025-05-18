package com.example.petsafeapp;

import java.io.File;

/**
 * Representa un negocio (como una tienda de mascotas o un veterinario)
 * que extiende la clase Usuario.
 */
public class Negocio extends Usuario{

  /**
  * El identificador único del negocio.
  */
  private int idNegocio;
  /**
  * El nombre del negocio.
  */
  private String nombreNegocio;
  /**
  * Una descripción del negocio.
  */
  private String descripcion;
  /**
  * La dirección física del negocio.
  */
  private String direccion;
  /**
  * La foto o imagen asociada al negocio.
  */
  private File foto;
  /**
  * El tipo de negocio (ej. Veterinario, Tienda).
  */
  private TipoNegocio tipo;
  /**
  * La puntuación media del negocio.
  */
  private double puntuacion;

  /**
  * Constructor por defecto para la clase Negocio.
  */
  public Negocio() {

  }

  /**
  * Obtiene la foto del negocio.
  * @return El objeto File que representa la foto.
  */
  public File getFoto() {
    return foto;
  }

  /**
  * Establece la foto del negocio.
  * @param foto El objeto File a establecer como foto.
  */
  public void setFoto(File foto) {
    this.foto = foto;
  }

  /**
  * Obtiene el ID del negocio.
  * @return El ID del negocio.
  */
  public int getIdNegocio() {
    return idNegocio;
  }

  /**
  * Establece el ID del negocio.
  * @param id El ID a establecer.
  */
  public void setIdNegocio(int id) {
    this.idNegocio = idNegocio;
  }

  /**
  * Obtiene el tipo de negocio.
  * @return El objeto TipoNegocio.
  */
  public TipoNegocio getTipo() {
    return tipo;
  }

  /**
  * Establece el tipo de negocio.
  * @param tipo El objeto TipoNegocio a establecer.
  */
  public void setTipo(TipoNegocio tipo) {
    this.tipo = tipo;
  }

  /**
  * Obtiene la dirección del negocio.
  * @return La dirección del negocio como String.
  */
  public String getDireccion() {
    return direccion;
  }

  /**
  * Establece la dirección del negocio.
  * @param direccion La dirección a establecer.
  */
  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  /**
  * Obtiene la descripción del negocio.
  * @return La descripción del negocio como String.
  */
  public String getDescripcion() {
    return descripcion;
  }

  /**
  * Establece la descripción del negocio.
  * @param descripcion La descripción a establecer.
  */
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  /**
  * Obtiene el nombre del negocio.
  * @return El nombre del negocio como String.
  */
  public String getNombreNegocio() {
    return nombreNegocio;
  }

  /**
  * Establece el nombre del negocio.
  * @param nombre El nombre a establecer.
  */
  public void setNombreNegocio(String nombre) {
    this.nombreNegocio = nombreNegocio;
  }

  /**
  * Obtiene la puntuación del negocio.
  * @return La puntuación del negocio.
  */
  public double getPuntuacion() {
    return puntuacion;
  }

  /**
  * Establece la puntuación del negocio.
  * @param puntuacion La puntuación a establecer.
  */
  public void setPuntuacion(double puntuacion) {
    this.puntuacion = puntuacion;
  }

  /**
  * Constructor completo para la clase Negocio.
  * Hereda información de Usuario y añade detalles específicos de Negocio.
  * @param id El ID del usuario.
  * @param imagen La imagen de perfil del usuario.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombre El nombre del usuario (o propietario del negocio).
  * @param idNegocio El ID del negocio.
  * @param nombreNegocio El nombre del negocio.
  * @param descripcion La descripción del negocio.
  * @param direccion La dirección del negocio.
  * @param foto La foto o imagen del negocio.
  * @param tipo El tipo de negocio.
  * @param puntuacion La puntuación del negocio.
  */
  public Negocio(int id, File imagen, String numTel, String email, String contrasena, String nombre, int idNegocio, String nombreNegocio, String descripcion, String direccion, File foto, TipoNegocio tipo, double puntuacion) {
    super(id, imagen, numTel, email, contrasena, nombre);
    this.idNegocio = idNegocio;
    this.nombreNegocio = nombre; // POSIBLE ERROR: Esto parece que debería ser nombreNegocio = nombreNegocio;
    this.descripcion = descripcion;
    this.direccion = direccion;
    this.foto = foto;
    this.tipo = tipo;
    this.puntuacion = puntuacion;
  }

  /**
  * Constructor utilizado para crear un objeto Negocio sin especificar el idNegocio inicialmente.
  * Hereda información de Usuario y añade detalles específicos de Negocio.
  * @param imagen La imagen de perfil del usuario.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombre El nombre del usuario (o propietario del negocio).
  * @param descripcion La descripción del negocio.
  * @param direccion La dirección del negocio.
  * @param foto La foto o imagen del negocio.
  * @param tipo El tipo de negocio.
  * @param puntuacion La puntuación del negocio.
  * @param idUsuario El ID del usuario.
  */
  public Negocio(File imagen, String numTel, String email, String contrasena, String nombre, String descripcion, String direccion, File foto, TipoNegocio tipo, double puntuacion, int idUsuario) {
    super(idUsuario, imagen, numTel, email, contrasena, nombre);
    this.nombreNegocio = nombre; // POSIBLE ERROR: Esto parece que debería ser nombreNegocio = nombre;
    this.descripcion = descripcion;
    this.direccion = direccion;
    this.foto = foto;
    this.tipo = tipo;
    this.puntuacion = puntuacion;
  }

  /**
  * Devuelve el nombre del negocio como representación String del objeto.
  * @return El nombre del negocio.
  */
  @Override
  public String toString() {
    return this.getNombre(); // Este método devuelve el nombre del Usuario, no el nombre del Negocio (nombreNegocio)
  }
}
