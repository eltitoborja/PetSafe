package com.example.petsafeapp;

import java.io.File;

/**
 * Representa un usuario genérico de la aplicación.
 * Contiene información básica común a todos los tipos de usuarios (personas, negocios, protectoras).
 */
public class Usuario {

  /**
  * El identificador único del usuario.
  */
  private int id;
  /**
  * El nombre del usuario o entidad.
  */
  private String nombre;
  /**
  * La contraseña de la cuenta del usuario.
  */
  private String contrasena;
  /**
  * El correo electrónico del usuario, utilizado también para iniciar sesión.
  */
  private String email;
  /**
  * El número de teléfono de contacto del usuario.
  */
  private String numTel;
  /**
  * El archivo de imagen asociado al perfil del usuario.
  */
  private File foto;

  /**
  * Constructor por defecto para la clase Usuario.
  */
  public Usuario() {

  }

  /**
  * Obtiene el ID del usuario.
  * @return El ID del usuario.
  */
  public int getId() {
    return id;
  }

  /**
  * Establece el ID del usuario.
  * @param id El ID a establecer.
  */
  public void setId(int id) {
    this.id = id;
  }

  /**
  * Obtiene el archivo de la foto de perfil del usuario.
  * @return El objeto File que representa la foto.
  */
  public File getFoto() {
    return foto;
  }

  /**
  * Establece el archivo de la foto de perfil del usuario.
  * @param imagen El objeto File a establecer como foto.
  */
  public void setFoto(File imagen) {
    this.foto = imagen;
  }

  /**
  * Obtiene el número de teléfono del usuario.
  * @return El número de teléfono como String.
  */
  public String getNumTel() {
    return numTel;
  }

  /**
  * Establece el número de teléfono del usuario.
  * @param numTel El número de teléfono a establecer.
  */
  public void setNumTel(String numTel) {
    this.numTel = numTel;
  }

  /**
  * Obtiene el correo electrónico del usuario.
  * @return El correo electrónico como String.
  */
  public String getEmail() {
    return email;
  }

  /**
  * Establece el correo electrónico del usuario.
  * @param email El correo electrónico a establecer.
  */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
  * Obtiene la contraseña del usuario.
  * @return La contraseña como String.
  */
  public String getContrasena() {
    return contrasena;
  }

  /**
  * Establece la contraseña del usuario.
  * @param contrasena La contraseña a establecer.
  */
  public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
  }

  /**
  * Obtiene el nombre del usuario.
  * @return El nombre del usuario como String.
  */
  public String getNombre() {
    return nombre;
  }

  /**
  * Establece el nombre del usuario.
  * @param nombre El nombre a establecer.
  */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
  * Constructor completo para la clase Usuario.
  * @param id El identificador único del usuario.
  * @param imagen El archivo de la foto de perfil.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombre El nombre del usuario.
  */
  public Usuario(int id, File imagen, String numTel, String email, String contrasena, String nombre) {
    this.id = id;
    this.foto = imagen;
    this.numTel = numTel;
    this.email = email;
    this.contrasena = contrasena;
    this.nombre = nombre;
  }

  /**
  * Constructor para crear un objeto Usuario sin especificar el ID (asumiendo que se generará automáticamente).
  * @param imagen El archivo de la foto de perfil.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param contrasena La contraseña del usuario.
  * @param nombre El nombre del usuario.
  */
  public Usuario(File imagen, String numTel, String email, String contrasena, String nombre) {
    this.foto = imagen;
    this.numTel = numTel;
    this.email = email;
    this.contrasena = contrasena;
    this.nombre = nombre;
  }

  /**
  * Constructor para crear un objeto Usuario sin especificar la contraseña.
  * @param id El identificador único del usuario.
  * @param imagen El archivo de la foto de perfil.
  * @param numTel El número de teléfono del usuario.
  * @param email El correo electrónico del usuario.
  * @param nombre El nombre del usuario.
  */
  public Usuario(int id, File imagen, String numTel, String email, String nombre) {
    this.id = id;
    this.foto = imagen;
    this.numTel = numTel;
    this.email = email;
    this.nombre = nombre;
  }

  /**
  * Devuelve el nombre del usuario como representación String del objeto.
  * @return El nombre del usuario.
  */
  @Override
  public String toString() {
    return this.getNombre();
  }

}
