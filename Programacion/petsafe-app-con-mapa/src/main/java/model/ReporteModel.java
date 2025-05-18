package model;

import com.example.petsafeapp.*; 

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link Reporte} en la base de datos.
 * Un reporte vincula un {@link Animal} con un {@link Usuario} y una ubicación.
 * Esta versión de la clase, en su método de lectura, maneja las imágenes de los animales
 * ({@code animal_imagen}) y los logos de los usuarios ({@code usuario_logo}) como datos
 * binarios (BLOB) que se convierten a objetos {@link File} temporales.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class ReporteModel extends DBUtil{

    /**
     * Crea un nuevo registro de reporte en la base de datos.
     * <p>
     * <strong>Nota:</strong> Este método actualmente inserta los IDs del animal y del usuario,
     * y la ubicación, pero no maneja la escritura de ninguna imagen como BLOB.
     * El método {@code readReportes}, en contraste, sí procesa imágenes BLOB.
     * </p>
     *
     * @param reporte El objeto {@link Reporte} que contiene la información a insertar.
     *                Se espera que el objeto {@code reporte} tenga asociados un objeto
     *                {@link Animal} y un objeto {@link Usuario} (de los cuales se obtendrán sus IDs),
     *                así como una ubicación.
     * @return {@code true} si el reporte fue creado exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     */
    public boolean createReporte(Reporte reporte) {
        boolean res = false;

        int idAnimal = reporte.getAnimal().getId();
        int idUsuario = reporte.getUsuario().getId();
        String ubicacion = reporte.getUbicacion();

        try {
            String sql = "INSERT INTO reporte (animal, Usuario, ubicacion) VALUES (?, ?, ?)"; // Columnas FK: animal, Usuario
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idAnimal);
            ps.setInt(2, idUsuario);
            ps.setString(3,ubicacion);

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
     * Lee todos los registros de reportes de la base de datos.
     * Realiza JOINs con las tablas {@code Animal}, {@code Usuario}, {@code Tipo} (TipoAnimal),
     * y {@code Situacion} para obtener la información completa.
     * <p>
     * La imagen del animal ({@code animal_imagen}) y el logo del usuario ({@code usuario_logo})
     * se recuperan como BLOBs desde la base de datos y se guardan en archivos temporales.
     * Los objetos {@link Animal} y {@link Usuario} dentro del {@link Reporte} devuelto
     * contendrán referencias a estos archivos temporales.
     * </p>
     * <p>
     * Este método captura {@link FileNotFoundException} y {@link IOException} que pueden
     * ocurrir durante el manejo de los archivos de imagen y las relanza como {@link RuntimeException}.
     * </p>
     * <p>
     * Nota: En el código original, al obtener la ubicación del reporte del ResultSet,
     * se utiliza {@code rs.getString("ubicacion")} que coincide con el alias en la consulta SQL.
     * </p>
     *
     * @return Un {@link ArrayList} de objetos {@link Reporte}. Cada objeto {@link Reporte}
     *         contiene los objetos {@link Animal} y {@link Usuario} asociados,
     *         con sus respectivas imágenes cargadas en archivos temporales.
     *         Retorna una lista vacía si no hay reportes o {@code null} si ocurre una {@link SQLException}.
     * @throws RuntimeException si ocurre una {@link FileNotFoundException} o {@link IOException}
     *                          durante el procesamiento de las imágenes.
     */
    public ArrayList<Reporte> readReportes() {
        ArrayList<Reporte> reportes = new ArrayList<Reporte>();

        try {
            String sql = "SELECT " +
                    "    r.id AS reporte_id, r.ubicacion, r.animal, r.Usuario, " + // Datos del Reporte + FKs
                    "    a.id AS animal_id, a.descripcion AS animal_descripcion, a.fecha AS animal_fecha, " + // Datos del Animal
                    "    a.imagen AS animal_imagen, " + // Columna BLOB para imagen de animal
                    "    a.tipo AS tipo_id, a.situacion AS situacion_id, " + // FKs en Animal
                    "    t.nombre AS tipo_nombre, " + // Nombre del Tipo
                    "    s.nombre AS situacion_nombre, " + // Nombre de la Situación
                    "    u.id AS usuario_id, u.nombreUser AS usuario_nombreUser, u.email AS usuario_email, " + // Datos del Usuario
                    "    u.telefonoContacto AS usuario_telefono, u.logoImagen AS usuario_logo " + // Columna BLOB para logo de usuario
                    "FROM " +
                    "    reporte r " + // Tabla base
                    "INNER JOIN animal a ON r.animal = a.id " + // JOIN con Animal (usando columna 'animal' de Reporte)
                    "INNER JOIN Usuario u ON r.Usuario = u.id " + // JOIN con Usuario (usando columna 'Usuario' de Reporte)
                    "INNER JOIN Tipo t ON a.tipo = t.id " + // JOIN con Tipo (a través de Animal)
                    "INNER JOIN Situacion s ON a.situacion = s.id " + // JOIN con Situación (a través de Animal)
                    "ORDER BY r.id ASC"; // Ordenación;
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idReporte = rs.getInt("reporte_id");
                String ubicacion = rs.getString("ubicacion");
                int idAnimalFK = rs.getInt("animal"); // FK de la tabla Reporte a Animal
                int idUsuarioFK = rs.getInt("Usuario"); // FK de la tabla Reporte a Usuario

                // Datos del Animal
                int idAnimal = rs.getInt("animal_id");
                String descripcionAnimal = rs.getString("animal_descripcion");
                Date fechaDate = rs.getDate("animal_fecha");
                LocalDate fechaAnimal = fechaDate.toLocalDate();

                InputStream isAnimalImg = rs.getBinaryStream("animal_imagen");
                File imagenAnimalFile = null;
                if (isAnimalImg != null) {
                    imagenAnimalFile = File.createTempFile("animal_img_", ".jpg"); 
                    try (FileOutputStream fosAnimalImg = new FileOutputStream(imagenAnimalFile)) {
                        byte[] bufferAnimal = new byte[1024];
                        int bytesReadAnimal;
                        while ((bytesReadAnimal = isAnimalImg.read(bufferAnimal)) != -1) {
                            fosAnimalImg.write(bufferAnimal, 0, bytesReadAnimal);
                        }
                    } finally {
                        isAnimalImg.close();
                    }
                }

                int idTipoAnimal = rs.getInt("tipo_id");
                int idSituacion = rs.getInt("situacion_id");
                String nombreTipo = rs.getString("tipo_nombre");
                String nombreSituacion = rs.getString("situacion_nombre");

                // Datos del Usuario
                int idUsuario = rs.getInt("usuario_id");
                String nombreUsuario = rs.getString("usuario_nombreUser");
                String email = rs.getString("usuario_email");
                String telefono = rs.getString("usuario_telefono");

                InputStream isUsuarioLogo = rs.getBinaryStream("usuario_logo");
                File logoUsuarioFile = null;
                if (isUsuarioLogo != null) {
                    logoUsuarioFile = File.createTempFile("usuario_logo_", ".jpg"); 
                    try (FileOutputStream fosUsuarioLogo = new FileOutputStream(logoUsuarioFile)) {
                        byte[] bufferUsuario = new byte[1024];
                        int bytesReadUsuario;
                        while ((bytesReadUsuario = isUsuarioLogo.read(bufferUsuario)) != -1) {
                            fosUsuarioLogo.write(bufferUsuario, 0, bytesReadUsuario);
                        }
                    } finally {
                        isUsuarioLogo.close();
                    }
                }

                TipoAnimal tipoAnimal = new TipoAnimal(idTipoAnimal,nombreTipo);
                Situacion situacion = new Situacion(idSituacion,nombreSituacion);
                Animal animal = new Animal(idAnimal,imagenAnimalFile,fechaAnimal,tipoAnimal,descripcionAnimal,situacion);

                Usuario usuario = new Usuario(idUsuario,logoUsuarioFile,telefono,email,nombreUsuario);

                Reporte reporte = new Reporte(idReporte,ubicacion,animal,usuario);
                reportes.add(reporte);
            }

            return reportes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) { // Captura y relanza según el código original
            throw new RuntimeException(e);
        } catch (IOException e) { // Captura y relanza según el código original
            throw new RuntimeException(e);
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de reporte existente en la base de datos.
     * Modifica el animal asociado, el usuario asociado y la ubicación del reporte.
     * <p>
     * <strong>Nota:</strong> Este método actualiza los IDs de referencia del animal y usuario,
     * y la cadena de ubicación, pero no maneja la actualización de imágenes BLOB
     * asociadas al animal o al usuario.
     * </p>
     *
     * @param reporte El objeto {@link Reporte} con los datos actualizados.
     *                Debe contener el ID del reporte a modificar, y los nuevos IDs
     *                para el animal y usuario asociados, y la nueva ubicación.
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si el reporte
     *         no fue encontrado o si ocurre un error.
     */
    public int updateReporte(Reporte reporte) {
        int res = 0;

        try {
            String sql = "UPDATE reporte\n" +
                    "SET\n" +
                    "    animal = ?,    -- 1st placeholder (Nuevo ID de Animal)\n" +
                    "    Usuario = ?,   -- 2nd placeholder (Nuevo ID de Usuario)\n" +
                    "    ubicacion = ?    -- 3rd placeholder (Nueva Ubicación)\n" +
                    "WHERE\n" +
                    "    id = ?;          -- 4th placeholder (ID del Reporte a actualizar)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, reporte.getAnimal().getId());
            ps.setInt(2, reporte.getUsuario().getId());
            ps.setString(3,reporte.getUbicacion());
            ps.setInt(4,reporte.getId());

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
     * Elimina un registro de reporte de la base de datos utilizando el ID
     * contenido en el objeto {@link Reporte} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteReporte(int)}.
     *
     * @param reporte El objeto {@link Reporte} cuyo ID se utilizará para identificar
     *               el registro a eliminar.
     * @return {@code true} si el reporte fue eliminado exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteReporte(int)
     */
    public boolean deleteReporte(Reporte reporte) {
        return this.deleteReporte(reporte.getId());
    }

    /**
     * Elimina un registro de reporte de la base de datos utilizando su ID.
     *
     * @param idReporte El ID del reporte que se desea eliminar.
     * @return {@code true} si el reporte fue eliminado exitosamente (al menos una fila afectada),
     *         {@code false} si el reporte no fue encontrado (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteReporte(int idReporte) {
        boolean res = false;

        try {
            String sql = "DELETE FROM reporte WHERE id = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idReporte);

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
