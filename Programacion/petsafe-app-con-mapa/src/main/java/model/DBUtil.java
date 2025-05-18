package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos.
 * Proporciona métodos para obtener y cerrar la conexión a una base de datos MySQL
 * localizada en localhost.
 */
public class DBUtil {

    /**
     * Campo que almacena la conexión activa a la base de datos.
     * Es {@code null} si no hay una conexión establecida o si la conexión ha sido cerrada.
     */
    Connection conexion = null;

    /**
     * Establece y devuelve una conexión a la base de datos MySQL especificada.
     * Utiliza una URL de conexión, usuario y contraseña predefinidos.
     * Si ya existe una conexión activa en el campo {@code conexion}, este método la
     * reemplazará por una nueva conexión sin cerrar la anterior, lo cual podría
     * llevar a fugas de recursos si no se gestiona externamente.
     *
     * @return Un objeto {@link Connection} si la conexión se estableció correctamente.
     *         Si ocurre una {@link SQLException} durante el proceso de conexión,
     *         se imprime la traza del error y se retorna {@code null}.
     */
    public Connection getConexion() {

        String cadenaConexion = "jdbc:mysql://proxy052.r3proxy.com:30740/petsafe";
        String usuario = "root";
        String password = "root";

        try {
            // Registra el driver JDBC para MySQL si no está registrado.
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            // Establece una nueva conexión y la asigna al campo 'conexion'.
            this.conexion = DriverManager.getConnection(cadenaConexion, usuario, password);

        } catch (SQLException e) {
            // En caso de error SQL durante la conexión, imprime la traza.
            e.printStackTrace();
        }

        // Devuelve la conexión establecida, que podría ser null si hubo un error.
        return conexion;

    }

    /**
     * Cierra la conexión a la base de datos si está abierta y no es {@code null}.
     * Este método verifica si el campo {@code conexion} no es {@code null} y si la conexión
     * no está ya cerrada antes de intentar cerrarla.
     */
    public void cerrarConexion() {
        try {
            // Verifica si la conexión existe y si está abierta (aunque el método close()
            if(this.conexion != null && !this.conexion.isClosed()) // Añadida verificación isClosed()
                this.conexion.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            // En caso de error al cerrar la conexión, imprime la traza.
            e.printStackTrace();
        }
    }

}
