package model;

import com.example.petsafeapp.Situacion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link Situacion} en la base de datos.
 * Una situación describe el estado o condición de un animal (e.g., "Encontrado", "Perdido", "En adopción").
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class SituacionModel extends DBUtil {

    /**
     * Crea un nuevo registro de situación en la base de datos.
     *
     * @param situacion El objeto {@link Situacion} que contiene el nombre de la situación a insertar.
     *                  Se espera que el ID de la situación se genere automáticamente por la base de datos
     *                  y no se utilice en la inserción.
     * @return {@code true} si la situación fue creada exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     */
    public boolean createSituacion(Situacion situacion) {
        boolean res = false;

        String nombre = situacion.getNombre();

        try {
            String sql = "INSERT INTO Situacion (nombre) VALUES (?)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, nombre);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0)
                res = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
            return res;
        }
    }

    /**
     * Lee todos los registros de situaciones de la base de datos.
     *
     * @return Un {@link ArrayList} de objetos {@link Situacion} que representan todas las
     *         situaciones encontradas. Cada objeto {@link Situacion} contendrá su ID y nombre.
     *         Retorna una lista vacía si no hay situaciones o {@code null} si ocurre un error
     *         durante la consulta.
     */
    public ArrayList<Situacion> readSituaciones() {
        ArrayList<Situacion> situaciones = new ArrayList<Situacion>();

        try {
            String sql = "SELECT id, nombre FROM Situacion;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                Situacion situacion = new Situacion(id,nombre);
                situaciones.add(situacion);
            }

            return situaciones;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de situación existente en la base de datos.
     * La identificación de la situación a actualizar se basa en el ID contenido
     * en el objeto {@code situacion} proporcionado.
     *
     * @param situacion El objeto {@link Situacion} con los datos actualizados.
     *                  Debe contener el ID de la situación a modificar y el nuevo nombre.
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si la situación
     *         no fue encontrada o si ocurre un error.
     */
    public int updateSituaciones(Situacion situacion) {
        int res = 0;

        try {
            String sql = "UPDATE Situacion SET nombre = ? WHERE id = ?;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, situacion.getNombre());
            ps.setInt(2, situacion.getId());

            res = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
            return res;
        }
    }

    /**
     * Elimina un registro de situación de la base de datos utilizando el ID
     * contenido en el objeto {@link Situacion} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteSituacion(int)}.
     *
     * @param situacion El objeto {@link Situacion} cuyo ID se utilizará para identificar
     *                  el registro a eliminar.
     * @return {@code true} si la situación fue eliminada exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteSituacion(int)
     */
    public boolean deleteSituacion(Situacion situacion) {
        return this.deleteSituacion(situacion.getId());
    }

    /**
     * Elimina un registro de situación de la base de datos utilizando su ID.
     *
     * @param idSituacion El ID de la situación que se desea eliminar.
     * @return {@code true} si la situación fue eliminada exitosamente (al menos una fila afectada),
     *         {@code false} si la situación no fue encontrada (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteSituacion(int idSituacion) {
        boolean res = false;

        try {
            String sql = "DELETE FROM Situacion WHERE id = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idSituacion);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0)
                res = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
            return res;
        }
    }
}
