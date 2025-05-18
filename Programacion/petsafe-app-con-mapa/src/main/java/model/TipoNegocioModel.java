package model;

import com.example.petsafeapp.TipoNegocio; 

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link TipoNegocio} en la base de datos.
 * Un tipo de negocio define la categoría o clase de un establecimiento o servicio
 * (e.g., "Veterinaria", "Tienda de mascotas", "Peluquería canina").
 * La tabla en la base de datos se asume que es 'tiponegocio'.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class TipoNegocioModel extends DBUtil{

    /**
     * Crea un nuevo registro de tipo de negocio en la base de datos.
     *
     * @param tipoNegocio El objeto {@link TipoNegocio} que contiene el nombre del tipo de negocio a insertar.
     *                    Se espera que el ID del tipo de negocio se genere automáticamente por la base de datos
     *                    y no se utilice en la inserción.
     * @return {@code true} si el tipo de negocio fue creado exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     */
    public boolean createTipoNegocio(TipoNegocio tipoNegocio) {
        boolean res = false;

        String nombre = tipoNegocio.getNombre();

        try {
            String sql = "INSERT INTO TipoNegocio (nombre) VALUES (?)"; // 'tiponegocio' es el nombre de la tabla
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
     * Lee todos los registros de tipos de negocio de la base de datos.
     *
     * @return Un {@link ArrayList} de objetos {@link TipoNegocio} que representan todos los
     *         tipos de negocio encontrados. Cada objeto {@link TipoNegocio} contendrá su ID y nombre.
     *         Retorna una lista vacía si no hay tipos de negocio o {@code null} si ocurre un error
     *         durante la consulta.
     */
    public ArrayList<TipoNegocio> readTipoNegocio() {
        ArrayList<TipoNegocio> tipoNegocios = new ArrayList<TipoNegocio>();

        try {
            String sql = "SELECT id, nombre FROM TipoNegocio;"; // 'tiponegocio' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                TipoNegocio tipoNegocio = new TipoNegocio(id, nombre);
                tipoNegocios.add(tipoNegocio);
            }

            return tipoNegocios;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de tipo de negocio existente en la base de datos.
     * La identificación del tipo de negocio a actualizar se basa en el ID contenido
     * en el objeto {@code tipoNegocio} proporcionado.
     *
     * @param tipoNegocio El objeto {@link TipoNegocio} con los datos actualizados.
     *                    Debe contener el ID del tipo de negocio a modificar y el nuevo nombre.
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si el tipo de negocio
     *         no fue encontrado o si ocurre un error.
     */
    public int updateTipoNegocio(TipoNegocio tipoNegocio) {
        int res = 0;

        try {
            String sql = "UPDATE TipoNegocio SET nombre = ? WHERE id = ?;"; // 'tiponegocio' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, tipoNegocio.getNombre());
            ps.setInt(2,tipoNegocio.getId());

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
     * Elimina un registro de tipo de negocio de la base de datos utilizando el ID
     * contenido en el objeto {@link TipoNegocio} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteTipoNegocio(int)}.
     *
     * @param tipoNegocio El objeto {@link TipoNegocio} cuyo ID se utilizará para identificar
     *                    el registro a eliminar.
     * @return {@code true} si el tipo de negocio fue eliminado exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteTipoNegocio(int)
     */
    public boolean deleteTipoNegocio(TipoNegocio tipoNegocio) {
        return this.deleteTipoNegocio(tipoNegocio.getId());
    }

    /**
     * Elimina un registro de tipo de negocio de la base de datos utilizando su ID.
     *
     * @param idTipoNegocio El ID del tipo de negocio que se desea eliminar.
     * @return {@code true} si el tipo de negocio fue eliminado exitosamente (al menos una fila afectada),
     *         {@code false} si el tipo de negocio no fue encontrado (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteTipoNegocio(int idTipoNegocio) {
        boolean res = false;

        try {
            String sql = "DELETE FROM TipoNegocio WHERE id = ?"; // 'tiponegocio' es el nombre de la tabla
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idTipoNegocio);

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
