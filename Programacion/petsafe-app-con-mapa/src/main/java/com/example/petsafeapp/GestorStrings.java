/**
 * Clase utilitaria para gestionar cadenas de texto compartidas entre controladores.
 * <p>
 * Proporciona un mecanismo centralizado para almacenar y recuperar valores de strings
 * que necesitan ser accesibles por múltiples controladores en la aplicación, facilitando
 * la comunicación entre componentes de la interfaz de usuario.
 * </p>
 */
package com.example.petsafeapp;

public class GestorStrings {

    private String vistaFlechaAtras;

    /**
     * Constructor por defecto sin parámetros.
     * <p>
     * Crea una instancia del gestor con valores iniciales nulos/vacíos.
     * </p>
     */
    public GestorStrings() {
        // Constructor
    }

    /**
     * Obtiene la referencia de vista almacenada para navegación hacia atrás.
     * 
     * @return Ruta o identificador de la vista anterior almacenada
     */
    public String getVistaFlechaAtras() {
        return vistaFlechaAtras;
    }

    /**
     * Establece la referencia de vista para navegación hacia atrás.
     * 
     * @param vistaFlechaAtras Ruta o identificador de la vista anterior a almacenar
     */
    public void setVistaFlechaAtras(String vistaFlechaAtras) {
        this.vistaFlechaAtras = vistaFlechaAtras;
    }
}
