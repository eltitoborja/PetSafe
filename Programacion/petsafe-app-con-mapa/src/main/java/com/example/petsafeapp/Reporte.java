package com.example.petsafeapp;

/**
 * Representa un reporte de un animal, asociado a una ubicación, un animal específico y el usuario que lo reportó.
 * Puede ser utilizado para reportar animales perdidos, encontrados o en adopción.
 */
public class Reporte {

  /**
  * El identificador único del reporte.
  */
  private int id;
  /**
  * La ubicación geográfica asociada al reporte (ej. dirección donde se vio al animal).
  */
  private String ubicacion;
  /**
  * El objeto Animal asociado a este reporte.
  */
  private Animal animal;
  /**
  * El objeto Usuario que creó el reporte.
  */
  private Usuario usuario;

  /**
  * Obtiene el ID del reporte.
  * @return El ID del reporte.
  */
  public int getId() {
    return id;
  }

  /**
  * Establece el ID del reporte.
  * @param id El ID a establecer.
  */
  public void setId(int id) {
    this.id = id;
  }

  /**
  * Obtiene el usuario que creó el reporte.
  * @return El objeto Usuario que creó el reporte.
  */
  public Usuario getUsuario() {
    return usuario;
  }

  /**
  * Establece el usuario que creó el reporte.
  * @param usuario El objeto Usuario a establecer.
  */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  /**
  * Obtiene el animal asociado al reporte.
  * @return El objeto Animal asociado al reporte.
  */
  public Animal getAnimal() {
    return animal;
  }

  /**
  * Establece el animal asociado al reporte.
  * @param animal El objeto Animal a establecer.
  */
  public void setAnimal(Animal animal) {
    this.animal = animal;
  }

  /**
  * Obtiene la ubicación asociada al reporte.
  * @return La ubicación como String.
  */
  public String getUbicacion() {
    return ubicacion;
  }

  /**
  * Establece la ubicación asociada al reporte.
  * @param ubicacion La ubicación a establecer.
  */
  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }

  /**
  * Constructor completo para la clase Reporte.
  * @param id El identificador único del reporte.
  * @param ubicacion La ubicación del reporte.
  * @param animal El animal asociado al reporte.
  * @param usuario El usuario que creó el reporte.
  */
  public Reporte(int id, String ubicacion, Animal animal, Usuario usuario) {
    this.id = id;
    this.ubicacion = ubicacion;
    this.animal = animal;
    this.usuario = usuario;
  }

  /**
  * Constructor para la clase Reporte sin especificar el ID (asumiendo que se generará automáticamente).
  * @param ubicacion La ubicación del reporte.
  * @param animal El animal asociado al reporte.
  * @param usuario El usuario que creó el reporte.
  */
  public Reporte(String ubicacion, Animal animal, Usuario usuario) {
    this.ubicacion = ubicacion;
    this.animal = animal;
    this.usuario = usuario;
  }
}
