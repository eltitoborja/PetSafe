/**
 * Representa una cita en la aplicación PetSafeApp, con información sobre su identificación,
 * fecha, hora, animal asociado, motivo y usuario responsable.
 */
package com.example.petsafeapp;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase que modela los datos de una cita relacionada con un animal en la aplicación.
 * Almacena información clave para la gestión de citas veterinarias, seguimientos
 * o notificaciones asociadas a animales registrados.
 */
public class Cita {
    /**
     * Identificador único de la cita en el sistema.
     */
    private int id;

    /**
     * Fecha programada para la cita (en formato ISO 8601: YYYY-MM-DD).
     */
    private LocalDate fecha;

    /**
     * Hora programada para la cita (en formato HH:MM).
     */
    private LocalTime hora;

    /**
     * Nombre del animal asociado a esta cita.
     */
    private String nombreAnimal;

    /**
     * Descripción del propósito o motivo de la cita.
     */
    private String motivo;

    /**
     * Identificador del usuario que creó o gestiona esta cita.
     */
    private int idUsuario;

    /**
     * Obtiene el identificador único de la cita.
     * @return El ID de la cita (entero positivo)
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario asociado a la cita.
     * @param idUsuario Nuevo valor de identificación de usuario
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el identificador único de la cita.
     * @return El ID de la cita (entero positivo)
     */
    public int getIdCita() {
        return id;
    }

    /**
     * Establece el identificador único de la cita.
     * @param id Nuevo valor de identificación para la cita
     */
    public void setIdCita(int id) {
        this.id = id;
    }

    /**
     * Recupera la fecha programada para la cita.
     * @return Objeto LocalDate con la fecha de la cita
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Asigna una nueva fecha para la cita.
     * @param fecha Nueva fecha a establecer
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la hora programada para la cita.
     * @return Objeto LocalTime con la hora de la cita
     */
    public LocalTime getHora() {
        return hora;
    }

    /**
     * Establece una nueva hora para la cita.
     * @param hora Nueva hora a asignar
     */
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /**
     * Recupera el nombre del animal asociado a la cita.
     * @return Cadena con el nombre del animal
     */
    public String getNombreAnimal() {
        return nombreAnimal;
    }

    /**
     * Asigna el nombre del animal relacionado con la cita.
     * @param nombreAnimal Nuevo nombre a registrar
     */
    public void setNombreAnimal(String nombreAnimal) {
        this.nombreAnimal = nombreAnimal;
    }

    /**
     * Obtiene el motivo o propósito de la cita.
     * @return Cadena con la descripción del motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Establece el motivo de la cita.
     * @param motivo Nuevo motivo a asignar
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Constructor completo para crear una cita con todos los atributos.
     * @param id Identificador único de la cita
     * @param fecha Fecha programada para la cita
     * @param hora Hora programada para la cita
     * @param nombreAnimal Nombre del animal asociado
     * @param motivo Descripción del propósito de la cita
     * @param idUsuario Identificador del usuario responsable
     */
    public Cita(int id, LocalDate fecha, LocalTime hora, String nombreAnimal, String motivo, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.nombreAnimal = nombreAnimal;
        this.motivo = motivo;
        this.idUsuario = idUsuario;
    }

    /**
     * Constructor básico para crear una cita sin identificador asignado.
     * @param fecha Fecha programada para la cita
     * @param hora Hora programada para la cita
     * @param nombreAnimal Nombre del animal asociado
     * @param motivo Descripción del propósito de la cita
     * @param idUsuario Identificador del usuario responsable
     */
    public Cita(LocalDate fecha, LocalTime hora, String nombreAnimal, String motivo, int idUsuario) {
        this.fecha = fecha;
        this.hora = hora;
        this.nombreAnimal = nombreAnimal;
        this.motivo = motivo;
        this.idUsuario = idUsuario;
    }
}
