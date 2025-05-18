package model;

import com.example.petsafeapp.Persona;

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link Persona} en la base de datos.
 * Esta versión de la clase maneja la lectura de la imagen del logo del usuario asociado
 * ({@code logoImagen}) como un BLOB y la convierte a un {@link File} temporal.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 * Se asume que la entidad {@link Persona} está relacionada con una entidad {@link com.example.petsafeapp.Usuario}.
 */
public class PersonaModel extends DBUtil {

    /**
     * Crea un nuevo registro de persona en la base de datos.
     * <p>
     * El ID del usuario asociado se obtiene de {@code persona.getId()}.
     * Si la fecha de nacimiento es nula, se inserta un valor NULL en la base de datos.
     * <strong>Nota:</strong> Este método actualmente no maneja la escritura de la imagen
     * del logo como BLOB, a diferencia del método {@code readPersonas} que sí lee
     * la imagen como BLOB.
     * </p>
     *
     * @param persona El objeto {@link Persona} que contiene la información a insertar.
     *                Se espera que el objeto {@code persona} tenga establecidos sus atributos
     *                de fecha de nacimiento, nombre, apellidos y el ID del usuario asociado.
     * @return {@code true} si la persona fue creada exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     */
    public boolean createPersona(Persona persona) {
        boolean res = false;
        String sql = "INSERT INTO Persona (fechaNacimiento, nombre, apellidos, Usuario_id) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            if (persona.getFechaNac() != null) {
                ps.setDate(1, Date.valueOf(persona.getFechaNac()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            ps.setString(2, persona.getNombre());
            ps.setString(3, persona.getApellidos());
            ps.setInt(4, persona.getId());


            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                res = true;
            }

        } catch (SQLException e) {
            System.err.println("Error al crear Persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
        }
        return res;
    }

    /**
     * Lee todos los registros de personas de la base de datos, incluyendo
     * información relacionada de la tabla {@code Usuario}.
     * La imagen del logo del usuario ({@code logoImagen}) se recupera como un BLOB
     * y se guarda en un archivo temporal. El objeto {@link Persona} devuelto
     * contendrá una referencia a este archivo temporal.
     * <p>
     * La consulta realiza un JOIN implícito (estilo antiguo con coma) entre las tablas
     * {@code Persona} y {@code Usuario}. Sería recomendable usar un JOIN explícito
     * con una cláusula ON para mayor claridad.
     * (Nota: Esta observación es solo para JavaDoc, el código no se modifica).
     * </p>
     * <p>
     * Este método captura {@link FileNotFoundException} y {@link IOException} que pueden
     * ocurrir durante el manejo del archivo de imagen y las relanza como {@link RuntimeException}.
     * </p>
     *
     * @return Un {@link ArrayList} de objetos {@link Persona} que representan todas las
     *         personas encontradas. Cada objeto {@link Persona} se construye con datos
     *         de ambas tablas y con la imagen del logo como un {@link File} temporal.
     *         Retorna una lista vacía si no hay personas o {@code null} si ocurre una {@link SQLException}.
     * @throws RuntimeException si ocurre una {@link FileNotFoundException} o {@link IOException}
     *                          durante el procesamiento de la imagen.
     */
    public ArrayList<Persona> readPersonas() {
        ArrayList<Persona> personas = new ArrayList<>();
        String sql = "SELECT p.idPersona, p.Usuario_id, p.fechaNacimiento, p.nombre, p.apellidos, " +
                "u.nombreUser, u.contraseña, u.email, u.telefonoContacto, u.logoImagen FROM Persona p, Usuario u;";

        try {
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idPersona"); // Este es el id de la tabla Persona
                int idUsuario = rs.getInt("Usuario_id"); // Este es el FK a Usuario
                LocalDate fecha = null;
                Date fechaSql = rs.getDate("fechaNacimiento");
                if (fechaSql != null) {
                    fecha = fechaSql.toLocalDate();
                }
                String nombre = rs.getString("nombre"); // Nombre de la Persona
                String apellidos = rs.getString("apellidos");
                String nombreUser = rs.getString("nombreUser"); // Nombre de Usuario
                String contraseña = rs.getString("contraseña");
                String email = rs.getString("email");
                String telefono = rs.getString("telefonoContacto");

                InputStream is = rs.getBinaryStream("logoImagen"); // Lee el BLOB del logo
                File tempFile = null;
                if (is != null) {
                    tempFile = File.createTempFile("imagen_", ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        is.close(); // Cierra el InputStream después de usarlo
                    }
                }
                Persona persona = new Persona(idUsuario, tempFile, telefono, email, contraseña, nombreUser, id, fecha, apellidos, nombre);
                personas.add(persona);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer Personas: " + e.getMessage());
            e.printStackTrace();
            return null; // Retornar null para indicar un error SQL
        } catch (FileNotFoundException e) {
            // Captura y relanza como RuntimeException según el código original
            throw new RuntimeException(e);
        } catch (IOException e) {
            // Captura y relanza como RuntimeException según el código original
            throw new RuntimeException(e);
        } finally {
            this.cerrarConexion();
        }
        return personas;
    }

    /**
     * Actualiza un registro de persona existente en la base de datos.
     * La identificación de la persona a actualizar se basa en el ID contenido
     * en el objeto {@code persona} proporcionado, que se asume es el {@code idPersona}.
     * <p>
     * Si la fecha de nacimiento es nula, se actualiza a NULL en la base de datos.
     * <strong>Nota:</strong> Este método actualmente no maneja la actualización de la imagen
     * del logo como BLOB.
     * </p>
     *
     * @param persona El objeto {@link Persona} con los datos actualizados.
     *                Debe contener el ID de la persona ({@code idPersona}) a modificar y
     *                los nuevos valores para fecha de nacimiento, nombre y apellidos.
     *                Se asume que {@code persona.getId()} devuelve el {@code idPersona}.
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si la persona
     *         no fue encontrada o si ocurre un error.
     */
    public int updatePersona(Persona persona) {
        int res = 0;
        String sql = "UPDATE Persona SET fechaNacimiento = ?, nombre = ?, apellidos = ? WHERE idPersona = ?";

        try {
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            if (persona.getFechaNac() != null) {
                ps.setDate(1, Date.valueOf(persona.getFechaNac()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            ps.setString(2, persona.getNombre());
            ps.setString(3, persona.getApellidos());
            ps.setInt(4, persona.getIdPersona());

            res = ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar Persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
        }
        return res;
    }

    /**
     * Elimina un registro de persona de la base de datos utilizando el ID
     * contenido en el objeto {@link Persona} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deletePersona(int)}. Se asume que {@code persona.getId()}
     * devuelve el {@code idPersona}.
     *
     * @param persona El objeto {@link Persona} cuyo ID ({@code idPersona}) se utilizará
     *                para identificar el registro a eliminar.
     * @return {@code true} si la persona fue eliminada exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deletePersona(int)
     */
    public boolean deletePersona(Persona persona) {
        return this.deletePersona(persona.getId());
    }

    /**
     * Elimina un registro de persona de la base de datos utilizando su ID.
     *
     * @param idPersona El ID ({@code idPersona}) de la persona que se desea eliminar.
     * @return {@code true} si la persona fue eliminada exitosamente (al menos una fila afectada),
     *         {@code false} si la persona no fue encontrada (0 filas afectadas) o si ocurre un error.
     */
    public boolean deletePersona(int idPersona) {
        boolean res = false;
        String sql = "DELETE FROM Persona WHERE idPersona = ?";

        try {
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ps.setInt(1, idPersona);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                res = true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar Persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
        }
        return res;
    }

    /**
     * Obtén a una persona de la base de datos utilizando la ID de usuario.
     *
     * @param idUsuarioIntroducido El ID ({@code idUsuario}) del usuario.
     * @return {@code Persona} si se encontró a la persona exitosamente,
     *         {@code null} si la persona no fue encontrada o si ocurre un error.
     */
    public Persona getPersonaFromIdUsuario(int idUsuarioIntroducido) {
        Persona persona = null;
        String sql = "SELECT p.idPersona, p.Usuario_id, p.fechaNacimiento, p.nombre, p.apellidos, " +
                "u.nombreUser, u.contraseña, u.email, u.telefonoContacto, u.logoImagen FROM Persona p, Usuario u WHERE p.Usuario_id = ?;";

        try {
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idUsuarioIntroducido);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idPersona"); // Este es el id de la tabla Persona
                int idUsuario = rs.getInt("Usuario_id"); // Este es el FK a Usuario
                LocalDate fecha = null;
                Date fechaSql = rs.getDate("fechaNacimiento");
                if (fechaSql != null) {
                    fecha = fechaSql.toLocalDate();
                }
                String nombre = rs.getString("nombre"); // Nombre de la Persona
                String apellidos = rs.getString("apellidos");
                String nombreUser = rs.getString("nombreUser"); // Nombre de Usuario
                String contraseña = rs.getString("contraseña");
                String email = rs.getString("email");
                String telefono = rs.getString("telefonoContacto");

                InputStream is = rs.getBinaryStream("logoImagen"); // Lee el BLOB del logo
                File tempFile = null;
                if (is != null) {
                    tempFile = File.createTempFile("imagen_", ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        is.close(); // Cierra el InputStream después de usarlo
                    }
                }

                persona = new Persona(idUsuario, tempFile, telefono, email, contraseña, nombreUser, id, fecha, apellidos, nombre);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer Personas: " + e.getMessage());
            e.printStackTrace();
            return null; // Retornar null para indicar un error SQL
        } catch (FileNotFoundException e) {
            // Captura y relanza como RuntimeException según el código original
            throw new RuntimeException(e);
        } catch (IOException e) {
            // Captura y relanza como RuntimeException según el código original
            throw new RuntimeException(e);
        } finally {
            this.cerrarConexion();
        }
        return persona;
    }
}