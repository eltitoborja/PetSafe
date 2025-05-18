/**
 * Representa una persona en la aplicación PetSafeApp, extendiendo la funcionalidad de un usuario
 * con atributos específicos como nombre, apellidos y fecha de nacimiento.
 * Esta clase encapsula los datos personales de un individuo que interactúa con el sistema,
 * complementando la información básica definida en la clase base {@link Usuario}.
 */
package com.example.petsafeapp;

import java.io.File;
import java.time.LocalDate;

/**
 * Clase que modela los datos personales de un usuario en el sistema.
 * Almacena información adicional a la básica definida en {@link Usuario}, 
 * enfocándose en detalles personales específicos de una persona física.
 */
public class Persona extends Usuario {

    /**
     * Identificador único de la persona en el sistema.
     * Este valor es diferente del ID del usuario base (heredado) y se utiliza
     * para identificar entidades de tipo persona específica en contextos especializados.
     */
    private int id;

    /**
     * Nombre propio de la persona.
     * Debe contener solo caracteres alfabéticos y espacios, con un máximo de 50 caracteres.
     */
    private String nombre;

    /**
     * Apellidos completos de la persona.
     * Sigue las mismas restricciones de formato que el nombre.
     */
    private String apellidos;

    /**
     * Fecha de nacimiento de la persona en formato ISO 8601 (YYYY-MM-DD).
     * Se utiliza para cálculos de edad y verificaciones legales en funcionalidades futuras.
     */
    private LocalDate fechaNac;

    /**
     * Obtiene el identificador único de la persona.
     * @return El ID específico de la persona (diferente del ID de usuario base)
     */
    public int getIdPersona() {
        return id;
    }

    /**
     * Establece un nuevo identificador único para la persona.
     * @param id El valor numérico a asignar (debe ser >= 0)
     */
    public void setIdPersona(int id) {
        this.id = id;
    }

    /**
     * Recupera el nombre propio de la persona.
     * @return Cadena con el nombre (máximo 50 caracteres)
     */
    public String getNombrePersona() {
        return nombre;
    }

    /**
     * Asigna un nuevo nombre a la persona.
     * @param nombre Texto alfabético con el nombre (máximo 50 caracteres)
     */
    public void setNombrePersona(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos completos de la persona.
     * @return Cadena con los apellidos (máximo 100 caracteres)
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece nuevos apellidos para la persona.
     * @param apellidos Texto alfabético con los apellidos (máximo 100 caracteres)
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Recupera la fecha de nacimiento de la persona.
     * @return Objeto LocalDate con la fecha en formato ISO 8601
     */
    public LocalDate getFechaNac() {
        return fechaNac;
    }

    /**
     * Asigna una nueva fecha de nacimiento.
     * @param fechaNac Nueva fecha a establecer (no puede ser nula)
     */
    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    /**
     * Constructor completo para crear una persona con todos los atributos.
     * Incluye parámetros para la clase base {@link Usuario} y atributos específicos.
     * 
     * @param idUsuario ID para la clase base Usuario (debe ser >= 1)
     * @param imagen Imagen de perfil del usuario
     * @param numTel Número de teléfono de contacto
     * @param email Dirección de correo electrónico
     * @param contrasena Contraseña cifrada del usuario
     * @param nombreUsuario Nombre de usuario para autenticación
     * @param idPersona ID específico para esta persona (diferente del ID base)
     * @param fechaNac Fecha de nacimiento de la persona
     * @param apellidos Apellidos completos
     * @param nombreReal Nombre propio de la persona
     */
    public Persona(int idUsuario, File imagen, String numTel, String email, String contrasena, String nombreUsuario, int idPersona, LocalDate fechaNac, String apellidos, String nombreReal) {
        super(idUsuario, imagen, numTel, email, contrasena, nombreUsuario);
        this.id = idPersona;
        this.fechaNac = fechaNac;
        this.apellidos = apellidos;
        this.nombre = nombreReal;
    }

    /**
     * Constructor básico para crear una persona sin especificar ID.
     * Utilizado para nuevas instancias antes de la persistencia.
     * 
     * @param idUsuario ID para la clase base Usuario (debe ser >= 1)
     * @param imagen Imagen de perfil del usuario
     * @param numTel Número de teléfono de contacto
     * @param email Dirección de correo electrónico
     * @param contrasena Contraseña cifrada del usuario
     * @param nombreUsuario Nombre de usuario para autenticación
     * @param fechaNac Fecha de nacimiento de la persona
     * @param apellidos Apellidos completos
     * @param nombreReal Nombre propio de la persona
     */
    public Persona(int idUsuario, File imagen, String numTel, String email, String contrasena, String nombreUsuario, LocalDate fechaNac, String apellidos, String nombreReal) {
        super(idUsuario, imagen, numTel, email, contrasena, nombreUsuario);
        this.fechaNac = fechaNac;
        this.apellidos = apellidos;
        this.nombre = nombreReal;
    }

    /**
     * Retorna una representación en cadena de la persona.
     * Sobrescribe el método toString() para mostrar únicamente el nombre.
     * 
     * @return Cadena con el nombre de la persona
     */
    @Override
    public String toString() {
        return this.getNombrePersona();
    }
}
