package model;

import com.example.petsafeapp.Protectora;
import javafx.scene.image.Image; // Esta importación no parece usarse en el código visible
import model.DBUtil;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para interactuar con la tabla 'Protectoras' en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar objetos Protectora.
 * Hereda funcionalidades de conexión a base de datos de DBUtil.
 */
public class ProtectoraModel extends DBUtil {
  /**
  * Inserta un nuevo registro de protectora en la base de datos.
  * @param protectora El objeto Protectora a insertar.
  * @return true si la inserción fue exitosa, false en caso contrario.
  * @throws FileNotFoundException Si el archivo de la foto no se encuentra.
  */
  public boolean createProtectora(Protectora protectora) throws FileNotFoundException {
    boolean res = false;
    String sql = "INSERT INTO protectoras (nombreProtectora, direccion, descripcion, fotos, Usuario_id) " +
        "VALUES (?, ?, ?, ?, ?)";
    FileInputStream fis = new FileInputStream(protectora.getFoto()); // Usa getFoto() heredado de Usuario

    try {
      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setString(1, protectora.getNombre()); // POSIBLE ERROR: Parece que debería usar getNombreProtectora() en lugar de getNombre()
      ps.setString(2, protectora.getDireccion());
      ps.setString(3, protectora.getDescripcion());
      ps.setBinaryStream(4,fis, (int) protectora.getFoto().length()); // Usa getFoto() heredado de Usuario
      ps.setInt(5, protectora.getId()); // Usa getId() heredado de Usuario

      int filasAfectadas = ps.executeUpdate();
      if (filasAfectadas > 0) {
        res = true;
      }

    } catch (SQLException e) {
      System.err.println("Error al crear Protectora: " + e.getMessage());
      e.printStackTrace();
    } finally {
      this.cerrarConexion();
    }
    return res;
  }

  /**
  * Recupera todos los registros de protectoras de la base de datos, incluyendo información del usuario asociado.
  * Construye objetos Protectora a partir de los datos recuperados, guardando las imágenes en archivos temporales.
  * @return Una lista de objetos Protectora. Devuelve null si ocurre un error.
  */
  public ArrayList<Protectora> readProtectoras() {
    ArrayList<Protectora> protectoras = new ArrayList<Protectora>();
    // Consulta SQL para seleccionar datos de Protectora y su Usuario asociado
    String sql = "SELECT p.idProtectora, p.nombreProtectora, p.descripcion, p.fotos,p.direccion, " +
        "p.Usuario_id, u.nombreUser, u.contraseña, u.email, u.telefonoContacto, u.logoImagen FROM protectoras p INNER join Usuario u ON p.Usuario_id = u.id;";

    try {
      PreparedStatement ps = this.getConexion().prepareStatement(sql);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        // Datos de Protectora
        int id = rs.getInt("idProtectora");
        int idUsuario = rs.getInt("Usuario_id"); // ID del usuario asociado
        String direccion = rs.getString("direccion");

        // Se lee la foto de la protectora desde el campo 'fotos'
        InputStream is = rs.getBinaryStream("fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".jpg"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        is.close();

        String nombre = rs.getString("nombreProtectora"); // Nombre de la protectora
        String descripcion = rs.getString("descripcion");
        // Datos del Usuario asociado
        String nombreUser = rs.getString("nombreUser");
        String contraseña = rs.getString("contraseña");
        String email = rs.getString("email");
        String telefono = rs.getString("telefonoContacto");

        // Se lee la foto del usuario asociado desde el campo 'logoImagen'
        InputStream is2 = rs.getBinaryStream("logoImagen");

        // Se guarda la imagen del usuario en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".jpg"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile); // POSIBLE ERROR: Esto podría sobrescribir tempFile en lugar de usar tempFile2

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = is2.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        is2.close();

        // Crea el objeto Protectora (que extiende Usuario)
        // Nota: El constructor de Protectora parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto de la protectora.
                // Nota: El constructor de Protectora recibe 'nombreUser' para el nombre del super y 'nombre' para el nombre de la protectora.
        Protectora protectora = new Protectora(idUsuario,tempFile, telefono, email, contraseña, nombreUser, id, direccion, descripcion, tempFile2, nombre);
        protectoras.add(protectora);
      }
    } catch (SQLException | IOException e) {
      System.err.println("Error al leer Protectoras: " + e.getMessage());
      e.printStackTrace();
      return null; // Retornar null para indicar un error
    } finally {
      this.cerrarConexion();
    }
    return protectoras;
  }

    /**
    * Recupera un registro de protectora de la base de datos por su ID específico de protectora.
    * Construye un objeto Protectora a partir de los datos recuperados, guardando las imágenes en archivos temporales.
    * @param protectoraid El ID específico de la protectora a buscar.
    * @return El objeto Protectora si se encuentra, o null si no se encuentra o si ocurre un error.
    */
    public Protectora getProtectoraById(int protectoraid) {
      Protectora protectora = new Protectora(); // Inicializa a null
    try {
      // Consulta SQL para seleccionar datos de una Protectora y su Usuario asociado por el ID de la protectora
      String sql = "SELECT p.idProtectora, p.nombreProtectora, p.descripcion, p.fotos, p.direccion, p.Usuario_id, u.nombreUser, u.contraseña, u.email, u.telefonoContacto, u.logoImagen FROM protectoras p INNER JOIN Usuario u ON p.Usuario_id = u.id WHERE p.idProtectora = ?;";
      PreparedStatement ps = this.getConexion().prepareStatement(sql);
      ps.setInt(1, protectoraid);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) { // Si encuentra un resultado
        // Datos de Protectora
        int id = rs.getInt("idProtectora");
        int idUsuario = rs.getInt("Usuario_id"); // ID del usuario asociado
        String direccion = rs.getString("direccion");

        // Se lee la foto de la protectora desde el campo 'fotos'
        InputStream is = rs.getBinaryStream("fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        is.close();

        String nombre = rs.getString("nombreProtectora"); // Nombre de la protectora
        String descripcion = rs.getString("descripcion");
        // Datos del Usuario asociado
        String nombreUser = rs.getString("nombreUser");
        String contraseña = rs.getString("contraseña");
        String email = rs.getString("email");
        String telefono = rs.getString("telefonoContacto");

        // Se lee la foto del usuario asociado desde el campo 'logoImagen'
        InputStream is2 = rs.getBinaryStream("logoImagen");

        // Se guarda la imagen del usuario en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".jpg"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile); // POSIBLE ERROR: Esto podría sobrescribir tempFile en lugar de usar tempFile2

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = is2.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        is2.close();

        // Crea el objeto Protectora (que extiende Usuario)
        // Nota: El constructor de Protectora parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto de la protectora.
                // Nota: El constructor de Protectora recibe 'nombreUser' para el nombre del super y 'nombre' para el nombre de la protectora.
        protectora = new Protectora(idUsuario,tempFile, telefono, email, contraseña, nombreUser, id, direccion, descripcion, tempFile2, nombre);
      }
    } catch (SQLException | IOException e) {
      System.err.println("Error al leer Protectoras: " + e.getMessage());
      e.printStackTrace();
      return null; // Retornar null para indicar un error
    } finally {
      this.cerrarConexion();
    }
    return protectora;
  }

  /**
  * Actualiza un registro de protectora existente en la base de datos.
  * @param protectora El objeto Protectora con los datos actualizados.
  * @return El número de filas afectadas por la operación de actualización.
  * @throws FileNotFoundException Si el archivo de la foto no se encuentra.
  */
  public int updateProtectora(Protectora protectora) throws FileNotFoundException {
    int res = 0;
    String sql = "UPDATE protectoras SET nombreProtectora = ?, direccion = ?, descripcion = ?, fotos = ?, Usuario_id = ? " +
        "WHERE idProtectora = ?";
    FileInputStream fis = new FileInputStream(protectora.getFoto()); // Usa getFoto() heredado de Usuario
    try {
      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setString(1, protectora.getNombre()); // POSIBLE ERROR: Parece que debería usar getNombreProtectora() en lugar de getNombre()
      ps.setString(2, protectora.getDireccion());
      ps.setString(3, protectora.getDescripcion());
      ps.setBinaryStream(4,fis, (int) protectora.getFoto().length()); // Usa getFoto() heredado de Usuario
      ps.setInt(5, protectora.getId()); // Usa getId() heredado de Usuario
      ps.setInt(6, protectora.getIdProtectora()); // Usa el ID específico de la protectora

      res = ps.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error al actualizar Protectora: " + e.getMessage());
      e.printStackTrace();
    } finally {
      this.cerrarConexion();
    }
    return res;
  }

  /**
  * Elimina un registro de protectora de la base de datos utilizando un objeto Protectora.
  * Delega la operación al método deleteProtectora(int idProtectora).
  * @param protectora El objeto Protectora a eliminar.
  * @return true si la eliminación fue exitosa, false en caso contrario.
  */
  public boolean deleteProtectora(Protectora protectora) {
    return this.deleteProtectora(protectora.getIdProtectora());
  }

  /**
  * Elimina un registro de protectora de la base de datos utilizando su ID específico de protectora.
  * @param idProtectora El ID específico de la protectora a eliminar.
  * @return true si la eliminación fue exitosa, false en caso contrario.
  */
  public boolean deleteProtectora(int idProtectora) {
    boolean res = false;
    String sql = "DELETE FROM protectoras WHERE idProtectora = ?";

    try {
      PreparedStatement ps = this.getConexion().prepareStatement(sql);
      ps.setInt(1, idProtectora);

      int filasAfectadas = ps.executeUpdate();
      if (filasAfectadas > 0) {
        res = true;
      }

    } catch (SQLException e) {
      System.err.println("Error al eliminar Protectoras: " + e.getMessage());
      e.printStackTrace();
    } finally {
      this.cerrarConexion();
    }
    return res;
  }

  /**
  * Recupera un registro de protectora de la base de datos por el ID del usuario asociado.
  * Esto es útil si un usuario genérico solo tiene una protectora asociada.
  * Construye un objeto Protectora a partir de los datos recuperados, guardando las imágenes en archivos temporales.
  * @param idUsuarioIntroducido El ID del usuario genérico asociado a la protectora.
  * @return El objeto Protectora asociado al usuario, o null si no se encuentra o si ocurre un error.
  */
  public Protectora getProtectoraFromIdUsuario(int idUsuarioIntroducido) {
    Protectora protectoraRes = null;
    // Consulta SQL (parece que falta la cláusula WHERE para filtrar por Usuario_id)
    String sql = "SELECT p.idProtectora, p.nombreProtectora, p.descripcion, p.fotos, p.direccion, p.Usuario_id, " +
        "u.nombreUser, u.contraseña, u.email, u.telefonoContacto, u.logoImagen FROM protectoras p, Usuario u;";

    try {
            // POSIBLE ERROR: Falta establecer el parámetro idUsuarioIntroducido en el PreparedStatement
      PreparedStatement ps = this.getConexion().prepareStatement(sql);
//      ps.setInt(?, idUsuarioIntroducido); // Línea que podría faltar para el filtro

      ResultSet rs = ps.executeQuery();

      while (rs.next()) { // Debería ser un 'if' si solo se espera un resultado por usuario
        // Datos de Protectora
        int id = rs.getInt("idProtectora");
        int idUsuario = rs.getInt("Usuario_id"); // ID del usuario asociado
        String direccion = rs.getString("direccion");

        // Se lee la foto de la protectora desde el campo 'fotos'
        InputStream is = rs.getBinaryStream("fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".jpg"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        is.close();

        String nombre = rs.getString("nombreProtectora"); // Nombre de la protectora
        String descripcion = rs.getString("descripcion");
        // Datos del Usuario asociado
        String nombreUser = rs.getString("nombreUser");
        String contraseña = rs.getString("contraseña");
        String email = rs.getString("email");
        String telefono = rs.getString("telefonoContacto");

        // Se lee la foto del usuario asociado desde el campo 'logoImagen'
        InputStream is2 = rs.getBinaryStream("logoImagen");

        // Se guarda la imagen del usuario en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".jpg"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile); // POSIBLE ERROR: Esto podría sobrescribir tempFile en lugar de usar tempFile2

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = is2.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        is2.close();

        // Crea el objeto Protectora (que extiende Usuario)
        // Nota: El constructor de Protectora parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto de la protectora.
                // Nota: El constructor de Protectora recibe 'nombreUser' para el nombre del super y 'nombre' para el nombre de la protectora.
        protectoraRes = new Protectora(idUsuario,tempFile, telefono, email, contraseña, nombreUser, id, direccion, descripcion, tempFile2, nombre); // Asigna al resultado
      }
    } catch (SQLException | IOException e) {
      System.err.println("Error al leer Protectoras: " + e.getMessage());
      e.printStackTrace();
      return null; // Retornar null para indicar un error
    } finally {
      this.cerrarConexion();
    }
    return protectoraRes;
  }

//  /**
//  * Recupera la imagen de la protectora desde la base de datos por su ID.
//  * Este método está comentado en el código original y no se utiliza.
//  * @param idProtectora El ID de la protectora.
//  * @return Un objeto Image, o null si no se encuentra o hay un error.
//  */
//  public Image getProtectoraImageFromId(int idProtectora) {
//    Image img = null;
//
//    try {
//      String sql = "SELECT imagenBlob FROM protectora WHERE idProtectora = ?";
//      PreparedStatement ps = this.getConexion().prepareStatement(sql);
//      ps.setInt(1, idProtectora);
//      ResultSet rs = ps.executeQuery();
//
//      if (rs.next()) {
//        InputStream is = rs.getBinaryStream("imagenBlob");
//        img = new Image(is);
//      }
//
//      return img;
//    } catch (SQLException e) {
//      e.printStackTrace();
//      return null;
//    } finally {
//      // Cerramos conexión
//      this.cerrarConexion();
//    }
//  }
}
