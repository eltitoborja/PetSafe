/**
 * Representa un animal en la aplicación PetSafeApp, con información sobre su situación,
 * tipo, descripción, fecha de registro y fotografía asociada.
 */
package com.example.petsafeapp;

import java.io.File;
import java.time.LocalDate;

/**
 * Clase que modela la información de un animal en la aplicación.
 * Contiene atributos básicos para identificar y describir animales reportados como 
 * perdidos o encontrados, junto con su imagen y metadatos relacionados.
 */
public class Animal {

    /**
     * Identificador único del animal en el sistema.
     */
    private int id;

    /**
     * Estado actual del animal (perdido, encontrado, etc.).
     * @see Situacion Enumeración que define los posibles estados
     */
    private Situacion situacion;

    /**
     * Descripción detallada del animal, incluyendo características físicas o contexto relevante.
     */
    private String descripción;

    /**
     * Categoría taxonómica del animal (perro, gato, etc.).
     * @see TipoAnimal Enumeración con tipos soportados
     */
    private TipoAnimal tipo;

    /**
     * Fecha de registro de la situación actual del animal (sin componente horario).
     */
    private LocalDate date;

    /**
     * Archivo de imagen que representa al animal.
     */
    private File foto;

    /**
     * Obtiene el identificador único del animal.
     * @return El ID asignado
     */
    public int getId() {
        return id;
    }

    /**
     * Establece un nuevo identificador único para el animal.
     * @param id El valor numérico a asignar
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el estado actual del animal.
     * @return Objeto de tipo Situacion
     */
    public Situacion getSituacion() {
        return situacion;
    }

    /**
     * Actualiza el estado del animal.
     * @param situacion El nuevo estado a aplicar
     */
    public void setSituacion(Situacion situacion) {
        this.situacion = situacion;
    }

    /**
     * Recupera la descripción del animal.
     * @return Cadena con información descriptiva
     */
    public String getDescripción() {
        return descripción;
    }

    /**
     * Asigna una nueva descripción al animal.
     * @param descripción Texto descriptivo a almacenar
     */
    public void setDescripción(String descripción) {
        this.descripción = descripción;
    }

    /**
     * Obtiene la categoría taxonómica del animal.
     * @return Objeto TipoAnimal que clasifica al animal
     */
    public TipoAnimal getTipo() {
        return tipo;
    }

    /**
     * Establece la categoría taxonómica del animal.
     * @param tipo La nueva clasificación a asignar
     */
    public void setTipo(TipoAnimal tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la fecha de registro de la situación actual.
     * @return Fecha en formato LocalDate
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Establece la fecha de registro de la situación.
     * @param date Nueva fecha a asignar
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Recupera la imagen asociada al animal.
     * @return Objeto File que apunta a la ubicación de la imagen
     */
    public File getFoto() {
        return foto;
    }

    /**
     * Asigna una nueva imagen al animal.
     * @param imagen Archivo de imagen a almacenar
     */
    public void setFoto(File imagen) {
        this.foto = imagen;
    }

    /**
     * Constructor completo para crear un animal con todos los atributos.
     * @param id Identificador único
     * @param imagen Archivo de imagen
     * @param date Fecha de registro
     * @param tipo Categoría taxonómica
     * @param descripción Información descriptiva
     * @param situacion Estado actual
     */
    public Animal(int id, File imagen, LocalDate date, TipoAnimal tipo, String descripción, Situacion situacion) {
        this.id = id;
        this.foto = imagen;
        this.date = date;
        this.tipo = tipo;
        this.descripción = descripción;
        this.situacion = situacion;
    }

    /**
     * Constructor básico para crear un animal sin especificar ID.
     * @param imagen Archivo de imagen
     * @param date Fecha de registro
     * @param tipo Categoría taxonómica
     * @param descripción Información descriptiva
     * @param situacion Estado actual
     */
    public Animal(File imagen, LocalDate date, TipoAnimal tipo, String descripción, Situacion situacion) {
        this.foto = imagen;
        this.date = date;
        this.tipo = tipo;
        this.descripción = descripción;
        this.situacion = situacion;
    }
}
