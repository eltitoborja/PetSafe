package com.example.petsafeapp;

/**
 * Representa el tipo de un negocio (ej. Veterinario, Tienda de mascotas).
 */
public class TipoNegocio {

  /**
  * El identificador único del tipo de negocio.
  */
  private int id;
  /**
  * El nombre descriptivo del tipo de negocio.
  */
  private String nombre;

  /**
  * Obtiene el ID del tipo de negocio.
  * @return El ID del tipo de negocio.
  */
  public int getId() {
    return id;
  }

  /**
  * Establece el ID del tipo de negocio.
  * @param id El ID a establecer.
  */
  public void setId(int id) {
    this.id = id;
  }

  /**
  * Obtiene el nombre del tipo de negocio.
  * @return El nombre del tipo de negocio.
  */
  public String getNombre() {
    return nombre;
  }

  /**
  * Establece el nombre del tipo de negocio.
  * @param nombre El nombre a establecer.
  */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
  * Constructor para la clase TipoNegocio.
  * @param id El identificador único del tipo de negocio.
  * @param nombre El nombre del tipo de negocio.
  */
  public TipoNegocio(int id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  /**
  * Devuelve el nombre del tipo de negocio como representación String del objeto.
  * @return El nombre del tipo de negocio.
  */
  @Override
  public String toString() {
    return this.getNombre();
  }
}
