package com.example.petsafeapp;

/**
 * Representa la situación o estado de un animal (ej. Perdido, Encontrado, En Adopción).
 */
public class Situacion {

  /**
  * El identificador único de la situación.
  */
  private int id;
  /**
  * El nombre descriptivo de la situación.
  */
  private String nombre;

  /**
  * Obtiene el ID de la situación.
  * @return El ID de la situación.
  */
  public int getId() {
    return id;
  }

  /**
  * Establece el ID de la situación.
  * @param id El ID a establecer.
  */
  public void setId(int id) {
    this.id = id;
  }

  /**
  * Obtiene el nombre de la situación.
  * @return El nombre de la situación.
  */
  public String getNombre() {
    return nombre;
  }

  /**
  * Establece el nombre de la situación.
  * @param nombre El nombre a establecer.
  */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
  * Constructor para la clase Situacion.
  * @param id El identificador único de la situación.
  * @param nombre El nombre de la situación.
  */
  public Situacion(int id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  /**
  * Devuelve el nombre de la situación como representación String del objeto.
  * @return El nombre de la situación.
  */
  @Override
  public String toString() {
    return this.getNombre();
  }
}
