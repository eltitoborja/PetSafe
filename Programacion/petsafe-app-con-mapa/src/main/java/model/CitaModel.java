package model;

import com.example.petsafeapp.Cita;
import com.example.petsafeapp.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Clase {@code CitaModel} que gestiona las interacciones con la base de datos
 * para la entidad {@link Cita}.
 * Proporciona métodos para crear, leer, actualizar y eliminar registros de citas.
 * Esta clase extiende {@link DBUtil} para utilizar sus funcionalidades de conexión
 * a la base de datos.
 */
public class CitaModel extends DBUtil {

    /**
     * Crea una nueva cita en la base de datos.
     *
     * @param cita El objeto {@link Cita} que contiene la información de la cita a registrar.
     *             Se espera que este objeto proporcione el ID del usuario, fecha, hora,
     *             nombre del animal y el motivo de la cita.
     * @return {@code true} si la cita se creó correctamente en la base de datos (al menos una fila afectada),
     *         {@code false} en caso contrario o si ocurre una {@link SQLException}.
     *         En caso de error, se imprime la traza de la excepción. La conexión
     *         a la base de datos se cierra siempre en el bloque {@code finally}.
     */
    public boolean createCita(Cita cita) {
        boolean res = false;

        int idUsuario = cita.getIdUsuario();
        Date fecha = Date.valueOf(cita.getFecha());
        LocalTime hora = cita.getHora();
        String nombreAnimal = cita.getNombreAnimal();
        String motivo = cita.getMotivo();

        try {
            String sql = "INSERT INTO Citas (id_usuario, fecha, hora, Nombre_Animal, motivo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idUsuario);
            ps.setDate(2, fecha);
            ps.setTime(3, Time.valueOf(hora));
            ps.setString(4, nombreAnimal);
            ps.setString(5, motivo);

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
     * Recupera todas las citas existentes en la base de datos.
     *
     * @return Un {@link ArrayList} de objetos {@link Cita} que representa todas las citas encontradas.
     *         Si ocurre una {@link SQLException} durante la consulta, se imprime la traza
     *         de la excepción y se retorna {@code null}. Si no hay citas, se retorna una lista vacía.
     *         La conexión a la base de datos se cierra siempre en el bloque {@code finally}
     *         (después de que el {@code ArrayList} es devuelto o {@code null} en caso de error).
     */
    public ArrayList<Cita> readCitas() {
        ArrayList<Cita> citas = new ArrayList<Cita>();

        try {
            String sql = "SELECT Id, Id_Usuario, Fecha, Hora, Nombre_Animal, Motivo FROM Citas;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idCita = rs.getInt("Id");
                int idUsuario = rs.getInt("Id_Usuario");
                Date fechaDate = rs.getDate("Fecha");
                LocalDate fecha = fechaDate.toLocalDate();
                LocalTime hora = rs.getTime("Hora").toLocalTime();
                String nombreAnimal = rs.getString("Nombre_Animal");
                String motivo = rs.getString("motivo");

                Cita c = new Cita(idCita, fecha, hora, nombreAnimal, motivo, idUsuario);
                citas.add(c);
            }
            return citas;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Recupera todas las citas asociadas a un {@link Usuario} específico.
     * Este método delega la obtención de citas al método {@link #readCitas(int)},
     * utilizando el ID del usuario proporcionado.
     *
     * @param usuario El objeto {@link Usuario} para el cual se quieren obtener las citas.
     * @return Un {@link ArrayList} de objetos {@link Cita} asociadas al usuario.
     *         El resultado depende de la ejecución de {@link #readCitas(int)}.
     */
    public ArrayList<Cita> readCitas(Usuario usuario) {
        return this.readCitas(usuario.getId());
    }

    /**
     * Recupera todas las citas asociadas a un ID de usuario específico.
     *
     * @param idUsuarioIntroducido El ID del usuario cuyas citas se desean obtener.
     * @return Un {@link ArrayList} de objetos {@link Cita} que pertenecen al usuario especificado.
     *         Si ocurre una {@link SQLException}, se imprime la traza y se retorna {@code null}.
     *         Si el usuario no tiene citas, se retorna una lista vacía.
     *         La conexión a la base de datos se cierra siempre en el bloque {@code finally}.
     */
    public ArrayList<Cita> readCitas(int idUsuarioIntroducido) {
        ArrayList<Cita> citas = new ArrayList<Cita>();

        try {
            String sql = "SELECT Id, Id_Usuario, Fecha, Hora, Nombre_Animal, Motivo FROM Citas WHERE Id_Usuario = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idUsuarioIntroducido);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idCita = rs.getInt("Id");
                int idUsuario = rs.getInt("Id_Usuario");
                Date fechaDate = rs.getDate("Fecha");
                LocalDate fecha = fechaDate.toLocalDate();
                LocalTime hora = rs.getTime("Hora").toLocalTime();
                String nombreAnimal = rs.getString("Nombre_Animal");
                String motivo = rs.getString("motivo");

                Cita c = new Cita(idCita, fecha, hora, nombreAnimal, motivo, idUsuario);
                citas.add(c);
            }
            return citas;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Recupera todas las citas de un {@link Usuario} específico para una fecha determinada.
     *
     * @param usuario El objeto {@link Usuario} para el cual se buscan las citas.
     * @param fechaIntroducida La {@link LocalDate} específica para la cual se filtrarán las citas.
     * @return Un {@link ArrayList} de objetos {@link Cita} que pertenecen al usuario y fecha especificados.
     *         Si ocurre una {@link SQLException}, se imprime la traza y se retorna {@code null}.
     *         Si no hay citas para ese usuario en esa fecha, se retorna una lista vacía.
     *         La conexión a la base de datos se cierra siempre en el bloque {@code finally}.
     */
    public ArrayList<Cita> readCitasDia(Usuario usuario, LocalDate fechaIntroducida) {
        ArrayList<Cita> citas = new ArrayList<Cita>();

        try {
            String sql = "SELECT Id, Id_Usuario, Fecha, Hora, Nombre_Animal, Motivo FROM Citas WHERE Id_Usuario = ? AND Fecha = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, usuario.getId());
            ps.setDate(2, Date.valueOf(fechaIntroducida));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idCita = rs.getInt("Id");
                int idUsuario = rs.getInt("Id_Usuario");
                Date fechaDate = rs.getDate("Fecha");
                LocalDate fecha = fechaDate.toLocalDate();
                LocalTime hora = rs.getTime("Hora").toLocalTime();
                String nombreAnimal = rs.getString("Nombre_Animal");
                String motivo = rs.getString("motivo");

                Cita c = new Cita(idCita, fecha, hora, nombreAnimal, motivo, idUsuario);
                citas.add(c);
            }
            return citas;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza la información de una cita existente en la base de datos.
     * La cita a actualizar se identifica por su ID, el cual se espera que esté
     * implícito en la consulta SQL (mediante el placeholder "?") pero no se establece
     * explícitamente un valor para el ID en este método con `ps.setInt()`.
     * La consulta SQL es "UPDATE CITAS SET Fecha =?, Hora =?, Nombre_Animal =?, Motivo =? WHERE Id =?;".
     *
     * @param cita El objeto {@link Cita} que contiene los nuevos datos (fecha, hora, nombre del animal, motivo)
     *             y se asume que también contiene el ID de la cita a modificar para que la cláusula WHERE Id = ? funcione.
     * @return El número de filas afectadas por la operación de actualización.
     *         Si ocurre una {@link SQLException}, se imprime la traza y se devuelve el valor
     *         inicial de {@code res} (que es 0). La conexión se cierra siempre en el bloque {@code finally}.
     */
    public int updateCita(Cita cita) {
        int res = 0;

        try {
            String sql = "UPDATE Citas SET Fecha =?, Hora =?, Nombre_Animal =?, Motivo =? WHERE Id =?;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ps.setDate(1, Date.valueOf(cita.getFecha()));
            ps.setTime(2, Time.valueOf(cita.getHora()));
            ps.setString(3, cita.getNombreAnimal());
            ps.setString(4, cita.getMotivo());
            ps.setInt(5, cita.getIdCita());

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
     * Elimina una cita de la base de datos.
     * La cita a eliminar se identifica por el ID obtenido del objeto {@link Cita} proporcionado.
     *
     * @param cita El objeto {@link Cita} que se desea eliminar. Se utiliza {@code cita.getIdCita()}
     *             para identificar el registro a borrar.
     * @return {@code true} si la cita se eliminó correctamente (al menos una fila afectada),
     *         {@code false} si la cita no se encontró, no se eliminó, o si ocurrió una {@link SQLException}.
     *         En caso de error SQL, se imprime la traza. La conexión se cierra siempre
     *         en el bloque {@code finally}.
     */
    public boolean deleteCita(Cita cita) {
       boolean res = false;

       try {

           String sql = "DELETE FROM Citas WHERE Id = ?";
           PreparedStatement ps = this.getConexion().prepareStatement(sql);

           ps.setInt(1, cita.getIdCita());

           int filasAfectadas = ps.executeUpdate();
           if (filasAfectadas > 0) {
               res = true;
           }
       } catch (SQLException e) {
           e.printStackTrace();
       } finally {
           this.cerrarConexion();
           return res;
       }
    }
}


