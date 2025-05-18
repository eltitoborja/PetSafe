package com.example.petsafeapp;

import java.io.File;

/**
 * Representa una protectora de animales que extiende la clase Usuario.
 */
public class Protectora extends Usuario {

  /**
  * El identificador único de la protectora.
  */
  private int idProtectora;
  /**
  * La dirección física de la protectora.
  */
  private String direccion;
  /**
  * Una descripción de la protectora.
  */
  private String descripcion;
  /**
  * La foto o imagen asociada a la protectora.
  */
  private File fotoProtectora;
  /**
  * El nombre de la protectora.
  */
  private String nombreProtectora;

  /**
  * Constructor por defecto para la clase Protectora.
  * Llama al constructor por defecto de la superclase Usuario.
  */
  public Protectora() {
    super();
  }


  /**
  * Obtiene el nombre de la protectora.
  * @return El nombre de la protectora como String.
  */
  public String getNombreProtectora() {
    return nombreProtectora;
  }

  /**
  * Establece el nombre de la protectora.
  * @param nombre El nombre a establecer.
  */
  public void setNombreProtectora(String nombre) {
    this.nombreProtectora = nombreProtectora; // POSIBLE ERROR: Esto parece que debería ser this.nombreProtectora = nombre;
  }

  /**
  * Obtiene el ID de la protectora.
  * @return El ID de la protectora.
  */
  public int getIdProtectora() {
    return idProtectora;
  }

  /**
  * Establece el ID de la protectora.
  * @param id El ID a establecer.
  */
  public void setIdProtectora(int id) {
    this.idProtectora = idProtectora; // POSIBLE ERROR: Esto parece que debería ser this.idProtectora = id;
  }

  /**
  * Obtiene la foto de la protectora.
  * @return El objeto File que representa la foto.
  */
  public File getFotoProtectora() {
    return fotoProtectora;
  }

  /**
  * Establece la foto de la protectora.
  * @param fotos El objeto File a establecer como foto.
  */
  public void setFotoProtectora(File fotos) {
    this.fotoProtectora = fotos;
  }

  /**
  * Obtiene la descripción de la protectora.
  * @return La descripción de la protectora como String.
  */
  public String getDescripcion() {
    return descripcion;
  }

  /**
  * Establece la descripción de la protectora.
  * @param descripcion La descripción a establecer.
  */
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  /**
  * Obtiene la dirección de la protectora.
  * @return La dirección de la protectora como String.
  */
  public String getDireccion() {
    return direccion;
  }

  /**
  * Establece la dirección de la protectora.
  * @param direccion La dirección a establecer.
  */
  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  /**
  * Constructor completo para la clase Protectora.
  * Hereda información de Usuario y añade detalles específicos de Protectora.
  * @param idUsuario El ID del usuario.
  * @param imagen La imagen de perfil del usuario.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombreUsuario El nombre del usuario asociado a la protectora.
  * @param idProtectora El ID de la protectora.
  * @param direccion La dirección de la protectora.
  * @param descripcion La descripción de la protectora.
  * @param foto La foto o imagen de la protectora.
  * @param nombre El nombre de la protectora.
  */
  public Protectora(int idUsuario, File imagen, String numTel, String email, String contrasena, String nombreUsuario, int idProtectora, String direccion, String descripcion, File foto, String nombre) {
    super(idUsuario, imagen, numTel, email, contrasena, nombreUsuario);
    this.idProtectora = idProtectora;
    this.direccion = direccion;
    this.descripcion = descripcion;
    this.fotoProtectora = foto;
    this.nombreProtectora = nombre;
  }

  /**
  * Constructor utilizado para crear un objeto Protectora sin especificar el idProtectora inicialmente.
  * Hereda información de Usuario y añade detalles específicos de Protectora.
  * @param idUsuario El ID del usuario.
  * @param imagen La imagen de perfil del usuario.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombreUsuario El nombre del usuario asociado a la protectora.
  * @param direccion La dirección de la protectora.
  * @param descripcion La descripción de la protectora.
  * @param foto La foto o imagen de la protectora.
  * @param nombre El nombre de la protectora.
  */
  public Protectora(int idUsuario, File imagen, String numTel, String email, String contrasena, String nombreUsuario, String direccion, String descripcion, File foto, String nombre) {
    super(idUsuario, imagen, numTel, email, contrasena, nombreUsuario);
    this.direccion = direccion;
    this.descripcion = descripcion;
    this.fotoProtectora = foto;
    this.nombreProtectora = nombre;
  }

  /**
  * Devuelve el nombre del usuario asociado a la protectora como representación String del objeto.
  * @return El nombre del usuario.
  */
  @Override
  public String toString() {
    return this.getNombre(); // Este método devuelve el nombre del Usuario, no el nombre de la Protectora (nombreProtectora)
  }
}
