package model;

import com.example.petsafeapp.TipoAnimal; 

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link TipoAnimal} en la base de datos.
 * Un tipo de animal define la especie o categoría del animal (e.g., "Perro", "Gato", "Ave").
 * La tabla en la base de datos se asume que es 'tipo'.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class TipoAnimalModel extends DBUtil {

    /**
     * Crea un nuevo registro de tipo de animal en la base de datos.
     *
     * @param tipoAnimal El objeto {@link TipoAnimal} que contiene el nombre del tipo de animal a insertar.
     *                   Se espera que el ID del tipo de animal se genere automáticamente por la base de datos
     *                   y no se utilice en la inserción.
     * @return {@code true} si el tipo de animal fue creado exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     */
    public boolean createTipoAnimal(TipoAnimal tipoAnimal) {
        boolean res = false;

        String nombre = tipoAnimal.getNombre();

        try {
            String sql = "INSERT INTO Tipo (nombre) VALUES (?)"; // 'tipo' es el nombre de la tabla
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
     * Lee todos los registros de tipos de animales de la base de datos.
     *
     * @return Un {@link ArrayList} de objetos {@link TipoAnimal} que representan todos los
     *         tipos de animales encontrados. Cada objeto {@link TipoAnimal} contendrá su ID y nombre.
     *         Retorna una lista vacía si no hay tipos de animales o {@code null} si ocurre un error
     *         durante la consulta.
     */
    public ArrayList<TipoAnimal> readTipoAnimales() {
        ArrayList<TipoAnimal> tipoAnimales = new ArrayList<TipoAnimal>();

        try {
            String sql = "SELECT id, nombre FROM Tipo;"; // 'tipo' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                TipoAnimal tipo = new TipoAnimal(id,nombre);
                tipoAnimales.add(tipo);
            }

            return tipoAnimales;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de tipo de animal existente en la base de datos.
     * La identificación del tipo de animal a actualizar se basa en el ID contenido
     * en el objeto {@code tipoAnimal} proporcionado.
     *
     * @param tipoAnimal El objeto {@link TipoAnimal} con los datos actualizados.
     *                   Debe contener el ID del tipo de animal a modificar y el nuevo nombre.
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si el tipo de animal
     *         no fue encontrado o si ocurre un error.
     */
    public int updateTipoAnimal(TipoAnimal tipoAnimal) {
        int res = 0;

        try {
            String sql = "UPDATE Tipo SET nombre = ? WHERE id = ?;"; // 'tipo' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, tipoAnimal.getNombre());
            ps.setInt(2, tipoAnimal.getId());

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
     * Elimina un registro de tipo de animal de la base de datos utilizando el ID
     * contenido en el objeto {@link TipoAnimal} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteTipoAnimal(int)}.
     *
     * @param tipoAnimal El objeto {@link TipoAnimal} cuyo ID se utilizará para identificar
     *                   el registro a eliminar.
     * @return {@code true} si el tipo de animal fue eliminado exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteTipoAnimal(int)
     */
    public boolean deleteTipoAnimal(TipoAnimal tipoAnimal) {
        return this.deleteTipoAnimal(tipoAnimal.getId());
    }

    /**
     * Elimina un registro de tipo de animal de la base de datos utilizando su ID.
     *
     * @param idTipoAnimal El ID del tipo de animal que se desea eliminar.
     * @return {@code true} si el tipo de animal fue eliminado exitosamente (al menos una fila afectada),
     *         {@code false} si el tipo de animal no fue encontrado (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteTipoAnimal(int idTipoAnimal) {
        boolean res = false;

        try {
            String sql = "DELETE FROM Tipo WHERE id = ?"; // 'tipo' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idTipoAnimal);

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
