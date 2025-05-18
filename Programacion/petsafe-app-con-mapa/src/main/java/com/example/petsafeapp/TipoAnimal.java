package com.example.petsafeapp;

/**
 * Representa el tipo de un animal (ej. Perro, Gato, Ave).
 */
public class TipoAnimal {

  /**
  * El identificador único del tipo de animal.
  */
  private int id;
  /**
  * El nombre descriptivo del tipo de animal.
  */
  private String nombre;

  /**
  * Obtiene el ID del tipo de animal.
  * @return El ID del tipo de animal.
  */
  public int getId() {
    return id;
  }

  /**
  * Establece el ID del tipo de animal.
  * @param id El ID a establecer.
  */
  public void setId(int id) {
    this.id = id;
  }

  /**
  * Obtiene el nombre del tipo de animal.
  * @return El nombre del tipo de animal.
  */
  public String getNombre() {
    return nombre;
  }

  /**
  * Establece el nombre del tipo de animal.
  * @param nombre El nombre a establecer.
  */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
  * Constructor para la clase TipoAnimal.
  * @param id El identificador único del tipo de animal.
  * @param nombre El nombre del tipo de animal.
  */
  public TipoAnimal(int id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  /**
  * Devuelve el nombre del tipo de animal como representación String del objeto.
  * @return El nombre del tipo de animal.
  */
  @Override
  public String toString() {
    return this.getNombre();
  }
}
