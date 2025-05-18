package model;

import com.example.petsafeapp.Usuario;
import javafx.scene.image.Image; // Import mantenido aunque el método que lo usa está comentado

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link Usuario} en la base de datos.
 * Esta versión maneja la imagen del logo del usuario ({@code logoImagen}) como un BLOB,
 * interactuando con objetos {@link File} para la entrada/salida de estas imágenes.
 * También incluye métodos para autenticar usuarios y verificar la existencia de emails.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class UsuarioModel extends DBUtil{

    /**
     * Crea un nuevo registro de usuario en la base de datos, incluyendo su imagen de logo como un BLOB.
     * La imagen se obtiene del objeto {@link Usuario} a través de {@code usuario.getFoto()}.
     *
     * @param usuario El objeto {@link Usuario} que contiene la información a insertar:
     *                nombre, contraseña, email, número de teléfono y la foto (como un objeto {@link File}).
     * @return {@code true} si el usuario fue creado exitosamente, {@code false} en caso contrario.
     * @throws FileNotFoundException si el archivo de imagen especificado en {@code usuario.getFoto()} no se encuentra.
     */
    public boolean createUsuario(Usuario usuario) throws FileNotFoundException {
        boolean res = false;

        String nombre = usuario.getNombre();
        String contrasenya = usuario.getContrasena();
        String email = usuario.getEmail();
        String numTel = usuario.getNumTel();

        FileInputStream fis = new FileInputStream(usuario.getFoto()); // Obtiene el stream del File
        try {
            String sql = "INSERT INTO Usuario (nombreUser, contraseña, email, " +
                    "telefonoContacto, logoImagen) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, nombre);
            ps.setString(2,contrasenya);
            ps.setString(3,email);
            ps.setString(4,numTel);
            ps.setBinaryStream(5, fis, (int) usuario.getFoto().length()); // Guarda el BLOB

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
     * Lee todos los registros de usuarios de la base de datos.
     * Para cada usuario, recupera su imagen de logo (almacenada como BLOB) y la guarda en un archivo temporal.
     * El objeto {@link Usuario} devuelto contendrá una referencia a este archivo temporal.
     *
     * @return Un {@link ArrayList} de objetos {@link Usuario}. Cada objeto {@link Usuario}
     *         contiene todos sus datos y la imagen del logo como un {@link File} temporal.
     *         Retorna una lista vacía si no hay usuarios o {@code null} si ocurre una {@link SQLException}.
     * @throws FileNotFoundException si ocurre un error al crear el archivo temporal o al escribir en él.
     * @throws IOException si ocurre un error de entrada/salida al leer el flujo de la imagen
     *         o al escribir en el archivo temporal.
     */
    public ArrayList<Usuario> readUsuarios() throws FileNotFoundException, IOException {
        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

        try {
            String sql = "SELECT id, nombreUser, contraseña, email, telefonoContacto, logoImagen FROM Usuario;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombreUser");
                String contrasenya = rs.getString("contraseña");
                String email = rs.getString("email");
                String numTel = rs.getString("telefonoContacto");
                InputStream is = rs.getBinaryStream("logoImagen"); // Lee el BLOB

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
                        is.close();
                    }
                }
                // El constructor de Usuario debe aceptar File para la imagen
                Usuario usuario = new Usuario(id,tempFile,numTel,email,contrasenya,nombre);
                usuarios.add(usuario);
            }

            return usuarios;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de usuario existente en la base de datos, incluyendo su imagen de logo.
     * La imagen se obtiene del objeto {@link Usuario} ({@code usuario.getFoto()}) y se actualiza
     * como un BLOB en la base de datos.
     *
     * @param usuario El objeto {@link Usuario} con los datos actualizados.
     *                Debe contener el ID del usuario a modificar y los nuevos valores
     *                para nombre, contraseña, email, número de teléfono y la nueva foto (como {@link File}).
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si el usuario
     *         no fue encontrado o si ocurre un error.
     */
    public int updateUsuario(Usuario usuario) { // Podría lanzar FileNotFoundException
        int res = 0;
        FileInputStream fis = null;

        try {
            String sql = "UPDATE Usuario SET nombreUser = ?, contraseña = ?, email = ?, " +
                    "telefonoContacto = ?, logoImagen = ? WHERE id = ?;";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            fis = new FileInputStream(usuario.getFoto()); // Puede lanzar FileNotFoundException

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getNumTel());
            ps.setBinaryStream(5, fis, (int) usuario.getFoto().length()); // Actualiza el BLOB
            ps.setInt(6,usuario.getId());

            res = ps.executeUpdate();
        } catch (SQLException | FileNotFoundException e) { // Capturar también FileNotFoundException
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Cerramos conexión
            this.cerrarConexion();
            return res;
        }
    }

    /**
     * Elimina un registro de usuario de la base de datos utilizando el ID
     * contenido en el objeto {@link Usuario} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteUsuario(int)}.
     *
     * @param usuario El objeto {@link Usuario} cuyo ID se utilizará para identificar
     *                el registro a eliminar.
     * @return {@code true} si el usuario fue eliminado exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteUsuario(int)
     */
    public boolean deleteUsuario(Usuario usuario) {
        return this.deleteUsuario(usuario.getId());
    }

    /**
     * Elimina un registro de usuario de la base de datos utilizando su ID.
     *
     * @param idUsuario El ID del usuario que se desea eliminar.
     * @return {@code true} si el usuario fue eliminado exitosamente (al menos una fila afectada),
     *         {@code false} si el usuario no fue encontrado (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteUsuario(int idUsuario) {
        boolean res = false;

        try {
            String sql = "DELETE FROM Usuario WHERE id = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idUsuario);

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

    /**
     * Obtiene un usuario de la base de datos basándose en su email y contraseña.
     * La imagen de logo del usuario se recupera como un BLOB y se guarda en un archivo temporal.
     * Este método se utiliza comúnmente para la autenticación de usuarios.
     *
     * @param emailIntroducido El email del usuario a buscar.
     * @param contrasenaIntroducida La contraseña del usuario a verificar.
     * @return Un objeto {@link Usuario} si se encuentra un usuario con las credenciales proporcionadas,
     *         con su imagen de logo cargada en un {@link File} temporal.
     *         Retorna {@code null} si no se encuentra ningún usuario que coincida,
     *         o si ocurre un error.
     */
    public Usuario getUsuarioConCredenciales(String emailIntroducido, String contrasenaIntroducida) { // Podría lanzar IOException
        Usuario res = null;

        try {
            String sql = "SELECT * FROM Usuario WHERE email = ? AND contraseña = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, emailIntroducido);
            ps.setString(2, contrasenaIntroducida);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) { 
                int id = rs.getInt("id");
                String nombre = rs.getString("nombreUser");
                String contrasenya = rs.getString("contraseña");
                String email = rs.getString("email");
                String numTel = rs.getString("telefonoContacto");
                InputStream is = rs.getBinaryStream("logoImagen");

                File tempFile = null;
                if (is != null) {
                    tempFile = File.createTempFile("imagen_", ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) { // Captura IOException del manejo de archivos
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                           try { is.close(); } catch (IOException e) { e.printStackTrace(); }
                        }
                    }
                }
                res = new Usuario(id, tempFile, numTel, email, contrasenya, nombre);
            }

        } catch (SQLException | IOException e) { // IOException por File.createTempFile
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
        }
        return res; // Devuelve el usuario encontrado o null
    }

    /**
     * Verifica si un email ya existe en la base de datos de usuarios.
     *
     * @param emailIntroducido El email a verificar.
     * @return {@code true} si el email ya existe en la base de datos, {@code false} en caso contrario
     *         o si ocurre un error.
     */
    public boolean existeEmail(String emailIntroducido) {
        boolean res = false;

        try {
            String sql = "SELECT * FROM Usuario WHERE email = ? LIMIT 1"; // LIMIT 1 es una buena optimización
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setString(1, emailIntroducido);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) // Si rs.next() es true, significa que se encontró al menos una fila
                res = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.cerrarConexion();
        }
        return res;
    }

//    /**
//     * Obtiene la imagen de un usuario (como un objeto {@link javafx.scene.image.Image})
//     * desde la base de datos utilizando el ID del usuario.
//     * La imagen se recupera de una columna BLOB que podría llamarse 'logoImagen' o 'imagenBlob'.
//     *
//     * @param idUsuario El ID del usuario cuya imagen se desea obtener.
//     * @return Un objeto {@link javafx.scene.image.Image} si se encuentra la imagen y se puede cargar,
//     *         o {@code null} si no se encuentra el usuario, no tiene imagen,
//     *         o si ocurre un error.
//     */
//    public Image getUsuarioImageFromId(int idUsuario) {
//        Image img = null;
//
//        try {
//            String sql = "SELECT imagenBlob FROM usuario WHERE id = ?"; // Podría ser 'logoImagen'
//            PreparedStatement ps = this.getConexion().prepareStatement(sql);
//            ps.setInt(1, idUsuario);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                InputStream is = rs.getBinaryStream("imagenBlob"); // Ajustar nombre de columna si es necesario
//                if (is != null) {
//                    img = new Image(is);
//                    is.close();
//                }
//            }
//
//            return img;
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            // Cerramos conexión
//            this.cerrarConexion();
//        }
//    }
}
