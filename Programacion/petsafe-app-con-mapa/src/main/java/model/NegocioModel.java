package model;

import com.example.petsafeapp.*;
import javafx.scene.image.Image; // Esta importación no parece usarse en el código visible
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase modelo para interactuar con la tabla 'negocio' en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar objetos Negocio.
 * Hereda funcionalidades de conexión a base de datos de DBUtil.
 */
public class NegocioModel extends DBUtil{
  /**
  * Inserta un nuevo registro de negocio en la base de datos.
  * @param negocio El objeto Negocio a insertar.
  * @return true si la inserción fue exitosa, false en caso contrario.
  * @throws FileNotFoundException Si el archivo de la foto no se encuentra.
  */
  public boolean createNegocio(Negocio negocio) throws FileNotFoundException {
    boolean res = false;

    FileInputStream fis = new FileInputStream(negocio.getFoto());

    String nombreNegocio = negocio.getNombreNegocio();
    String direccion = negocio.getDireccion();
    String descripcion = negocio.getDescripcion();
    Double puntuacion = negocio.getPuntuacion();
    int tipoNegocio_id = negocio.getTipo().getId();
    int usuario_id = negocio.getId(); // Asume que Negocio hereda el ID de Usuario

    try {
      String sql = "INSERT INTO Negocio (nombreNegocio, direccion, descripcion, fotos, puntuacion, tipoNegocio_id, Usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setString(1, nombreNegocio);
      ps.setString(2, direccion);
      ps.setString(3, descripcion);
      ps.setBinaryStream(4, fis, (int) negocio.getFoto().length());
      ps.setDouble(5, puntuacion);
      ps.setInt(6, tipoNegocio_id);
      ps.setInt(7, usuario_id);

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
  * Recupera todos los registros de negocios de la base de datos.
  * Construye objetos Negocio a partir de los datos recuperados, incluyendo la imagen.
  * @return Una lista de objetos Negocio. Devuelve null si ocurre un error.
  */
  public ArrayList<Negocio> readNegocios() {
    ArrayList<Negocio> negocios = new ArrayList<Negocio>();

    try {
      String sql = "SELECT " +
          "  n.idNegocio AS negocio_id, " +
          "  n.nombreNegocio AS negocio_nombre, " +
          "  n.direccion AS negocio_direccion, " +
          "  n.descripcion AS negocio_descripcion, " +
          "  n.fotos AS negocio_fotos, " +
          "  n.puntuacion AS negocio_puntuacion, " +
          "  tn.id AS tipoNegocio_id, " +
          "  tn.nombre AS tipoNegocio_nombre, " +
          "  u.id AS usuario_id, " +
          "  u.nombreUser AS usuario_nombreUser, " + // Nombre del usuario asociado
          "  u.email AS usuario_email, " +
          "  u.telefonoContacto AS usuario_telefono, " +
          "  u.logoImagen AS usuario_logo " + // Logo del usuario asociado
          "FROM " +
          "  Negocio n " +
          "INNER JOIN TipoNegocio tn ON n.tipoNegocio_id = tn.id " +
          "INNER JOIN Usuario u ON n.Usuario_id = u.id " +
          "ORDER BY n.idNegocio ASC";

      PreparedStatement ps = this.getConexion().prepareStatement(sql);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        // Datos de Usuario (heredados por Negocio)
        int idUsuario = rs.getInt("usuario_id");

        // Se lee la foto del negocio desde el campo 'negocio_fotos'
        InputStream logoUsuario = rs.getBinaryStream("negocio_fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = logoUsuario.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        logoUsuario.close();

        String telefono = rs.getString("usuario_telefono");
        String email = rs.getString("usuario_email");
        String contrasena = ""; // si no está en la consulta, pon un valor por defecto o agrégalo al SELECT
        String nombreUsuario = rs.getString("usuario_nombreUser"); // Nombre del usuario

        // Datos de Negocio
        int idNegocio = rs.getInt("negocio_id");
        String nombreNegocio = rs.getString("negocio_nombre");
        String direccion = rs.getString("negocio_direccion");
        String descripcion = rs.getString("negocio_descripcion");

        // Se lee la foto del usuario desde el campo 'usuario_logo'
        InputStream fotos = rs.getBinaryStream("usuario_logo");

        // Se guarda la imagen en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile2);

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = fotos.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        fotos.close();

        double puntuacion = rs.getDouble("negocio_puntuacion");

        // Datos de TipoNegocio
        int idTipoNegocio = rs.getInt("tipoNegocio_id");
        String nombreTipoNegocio = rs.getString("tipoNegocio_nombre");
        TipoNegocio tipoNegocio = new TipoNegocio(idTipoNegocio, nombreTipoNegocio);

        // Crear objeto Negocio (que extiende Usuario)
        // Nota: El constructor de Negocio parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto del negocio.
                // Nota: El constructor de Negocio recibe 'nombre' para el nombre del negocio y 'nombreUsuario' para el nombre del super.
        Negocio negocio = new Negocio(
            idUsuario,       // id de Usuario (super)
            tempFile,      // imagen (super) -> Esta imagen es la de 'negocio_fotos'
            telefono,       // numTel (super)
            email,         // email (super)
            contrasena,      // contraseña (super) – ¡ojo! asegúrate de incluirla si es necesaria
            nombreUsuario,     // nombre (super) -> Este es el nombre del usuario
            idNegocio,       // id1 (id específico del negocio)
            nombreNegocio,     // nombre del negocio
            descripcion,      // descripción
            direccion,       // dirección
            tempFile2,         // fotos -> Esta imagen es la de 'usuario_logo'
            tipoNegocio,      // TipoNegocio
            puntuacion       // puntuación
        );

        negocios.add(negocio);
      }

      return negocios;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e); // Envuelve FileNotFoundException en RuntimeException
    } catch (IOException e) {
      throw new RuntimeException(e); // Envuelve IOException en RuntimeException
    } finally {
      // Cerramos conexión
      this.cerrarConexion();
    }
  }

  /**
  * Recupera un registro de negocio de la base de datos por su ID específico de negocio.
  * Construye un objeto Negocio a partir de los datos recuperados, incluyendo la imagen.
  * @param negocioId El ID específico del negocio a buscar.
  * @return El objeto Negocio si se encuentra, o null si no se encuentra o si ocurre un error.
  */
  public Negocio getNegocioById(int negocioId) {
    Negocio negocio = new Negocio(); // Inicializa a null
    try {
      String sql = "SELECT " +
          "  n.idNegocio AS negocio_id, " +
          "  n.nombreNegocio AS negocio_nombre, " +
          "  n.direccion AS negocio_direccion, " +
          "  n.descripcion AS negocio_descripcion, " +
          "  n.fotos AS negocio_fotos, " +
          "  n.puntuacion AS negocio_puntuacion, " +
          "  tn.id AS tipoNegocio_id, " +
          "  tn.nombre AS tipoNegocio_nombre, " +
          "  u.id AS usuario_id, " +
          "  u.nombreUser AS usuario_nombreUser, " + // Nombre del usuario asociado
          "  u.email AS usuario_email, " +
          "  u.telefonoContacto AS usuario_telefono, " +
          "  u.logoImagen AS usuario_logo " + // Logo del usuario asociado
          "FROM " +
          "  Negocio n " +
          "INNER JOIN TipoNegocio tn ON n.tipoNegocio_id = tn.id " +
          "INNER JOIN Usuario u ON n.Usuario_id = u.id " +
          " WHERE n.idNegocio = ?" +
          " ORDER BY n.idNegocio ASC";
      PreparedStatement ps = this.getConexion().prepareStatement(sql);
      ps.setInt(1, negocioId);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        // Datos de Usuario (heredados por Negocio)
        int idUsuario = rs.getInt("usuario_id");

        // Se lee la foto del negocio desde el campo 'negocio_fotos'
        InputStream logoUsuario = rs.getBinaryStream("negocio_fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = logoUsuario.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        logoUsuario.close();

        String telefono = rs.getString("usuario_telefono");
        String email = rs.getString("usuario_email");
        String contrasena = ""; // si no está en la consulta, pon un valor por defecto o agrégalo al SELECT
        String nombreUsuario = rs.getString("usuario_nombreUser"); // Nombre del usuario

        // Datos de Negocio
        int idNegocio = rs.getInt("negocio_id");
        String nombreNegocio = rs.getString("negocio_nombre");
        String direccion = rs.getString("negocio_direccion");
        String descripcion = rs.getString("negocio_descripcion");

        // Se lee la foto del usuario desde el campo 'usuario_logo'
        InputStream fotos = rs.getBinaryStream("usuario_logo");

        // Se guarda la imagen en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile2);

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = fotos.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        fotos.close();

        double puntuacion = rs.getDouble("negocio_puntuacion");

        // Datos de TipoNegocio
        int idTipoNegocio = rs.getInt("tipoNegocio_id");
        String nombreTipoNegocio = rs.getString("tipoNegocio_nombre");
        TipoNegocio tipoNegocio = new TipoNegocio(idTipoNegocio, nombreTipoNegocio);

        // Crear objeto Negocio (que extiende Usuario)
        // Nota: El constructor de Negocio parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto del negocio.
                // Nota: El constructor de Negocio recibe 'nombre' para el nombre del negocio y 'nombreUsuario' para el nombre del super.
        negocio = new Negocio(
            idUsuario,       // id de Usuario (super)
            tempFile,      // imagen (super) -> Esta imagen es la de 'negocio_fotos'
            telefono,       // numTel (super)
            email,         // email (super)
            contrasena,      // contraseña (super) – ¡ojo! asegúrate de incluirla si es necesaria
            nombreUsuario,     // nombre (super) -> Este es el nombre del usuario
            idNegocio,       // id1 (id específico del negocio)
            nombreNegocio,     // nombre del negocio
            descripcion,      // descripción
            direccion,       // dirección
            tempFile2,         // fotos -> Esta imagen es la de 'usuario_logo'
            tipoNegocio,      // TipoNegocio
            puntuacion       // puntuación
        );
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return negocio;
  }

  /**
  * Actualiza un registro de negocio existente en la base de datos.
  * @param negocio El objeto Negocio con los datos actualizados.
  * @return El número de filas afectadas por la operación de actualización.
  * @throws FileNotFoundException Si el archivo de la foto no se encuentra.
  */
  public int updateNegocio(Negocio negocio) throws FileNotFoundException {
    int res = 0;

    FileInputStream fis = new FileInputStream(negocio.getFoto());

    try {
      String sql = "UPDATE Negocio SET " +
          "nombreNegocio = ?, " +   // 1
          "direccion = ?, " +     // 2
          "descripcion = ?, " +    // 3
          "fotos = ?, " +       // 4
          "puntuacion = ?, " +    // 5
          "tipoNegocio_id = ?, " +  // 6
          "Usuario_id = ? " +     // 7
          "WHERE idNegocio = ?";       // 8

      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setString(1, negocio.getNombre()); // POSIBLE ERROR: Parece que debería usar getNombreNegocio() en lugar de getNombre()
      ps.setString(2, negocio.getDireccion());
      ps.setString(3, negocio.getDescripcion());
      ps.setBinaryStream(4,fis, (int) negocio.getFoto().length());
      ps.setDouble(5, negocio.getPuntuacion());
      ps.setInt(6, negocio.getTipo().getId());
      ps.setInt(7, negocio.getId());    // este es el ID del usuario heredado
      ps.setInt(8, negocio.getIdNegocio());    // este es el ID del negocio (clave primaria de la tabla)

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
  * Elimina un registro de negocio de la base de datos utilizando un objeto Negocio.
  * Delega la operación al método deleteNegocio(int idNegocio).
  * @param negocio El objeto Negocio a eliminar.
  * @return true si la eliminación fue exitosa, false en caso contrario.
  */
  public boolean deleteNegocio(Negocio negocio) {
    return this.deleteNegocio(negocio.getIdNegocio());
  }

  /**
  * Elimina un registro de negocio de la base de datos utilizando su ID específico de negocio.
  * @param idNegocio El ID específico del negocio a eliminar.
  * @return true si la eliminación fue exitosa, false en caso contrario.
  */
  public boolean deleteNegocio(int idNegocio) {
    boolean res = false;

    try {
      String sql = "DELETE FROM Negocio WHERE id = ?"; // POSIBLE ERROR: El campo de la clave primaria debería ser idNegocio, no id.
      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setInt(1, idNegocio);

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
  * Recupera un registro de negocio de la base de datos por el ID del usuario asociado.
  * Esto es útil si un usuario genérico solo tiene un negocio asociado.
  * @param idUsuarioIntroducido El ID del usuario genérico asociado al negocio.
  * @return El objeto Negocio asociado al usuario, o null si no se encuentra o si ocurre un error.
  */
  public Negocio getNegocioFromIdUsuario(int idUsuarioIntroducido) {
    Negocio negocioRes = null;

    try {
      String sql = "SELECT " +
          "  n.idNegocio AS negocio_id, " +
          "  n.nombreNegocio AS negocio_nombre, " +
          "  n.direccion AS negocio_direccion, " +
          "  n.descripcion AS negocio_descripcion, " +
          "  n.fotos AS negocio_fotos, " +
          "  n.puntuacion AS negocio_puntuacion, " +
          "  tn.id AS tipoNegocio_id, " +
          "  tn.nombre AS tipoNegocio_nombre, " +
          "  u.id AS usuario_id, " +
          "  u.nombreUser AS usuario_nombreUser, " + // Nombre del usuario asociado
          "  u.email AS usuario_email, " +
          "  u.telefonoContacto AS usuario_telefono, " +
          "  u.logoImagen AS usuario_logo " + // Logo del usuario asociado
          "FROM " +
          "  Negocio n " +
          "INNER JOIN TipoNegocio tn ON n.tipoNegocio_id = tn.id " +
          "INNER JOIN Usuario u ON n.Usuario_id = u.id " +
          "WHERE n.Usuario_id = ? " +
          "ORDER BY n.idNegocio ASC";

      PreparedStatement ps = this.getConexion().prepareStatement(sql);

      ps.setInt(1, idUsuarioIntroducido);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) { // Debería ser un 'if' si solo se espera un resultado por usuario
        // Datos de Usuario (heredados por Negocio)
        int idUsuario = rs.getInt("usuario_id");

        // Se lee la foto del negocio desde el campo 'negocio_fotos'
        InputStream logoUsuario = rs.getBinaryStream("negocio_fotos");

        // Se guarda la imagen en un archivo temporal
        File tempFile = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = logoUsuario.read(buffer)) != -1) {
          fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        logoUsuario.close();

        String telefono = rs.getString("usuario_telefono");
        String email = rs.getString("usuario_email");
        String contrasena = ""; // si no está en la consulta, pon un valor por defecto o agrégalo al SELECT
        String nombreUsuario = rs.getString("usuario_nombreUser"); // Nombre del usuario

        // Datos de Negocio
        int idNegocio = rs.getInt("negocio_id");
        String nombreNegocio = rs.getString("negocio_nombre");
        String direccion = rs.getString("negocio_direccion");
        String descripcion = rs.getString("negocio_descripcion");

        // Se lee la foto del usuario desde el campo 'usuario_logo'
        InputStream fotos = rs.getBinaryStream("usuario_logo");

        // Se guarda la imagen en otro archivo temporal
        File tempFile2 = File.createTempFile("imagen_", ".png"); // Usa .png si corresponde
        FileOutputStream fos2 = new FileOutputStream(tempFile2);

        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = fotos.read(buffer2)) != -1) {
          fos2.write(buffer2, 0, bytesRead2);
        }

        fos2.close();
        fotos.close();

        double puntuacion = rs.getDouble("negocio_puntuacion");

        // Datos de TipoNegocio
        int idTipoNegocio = rs.getInt("tipoNegocio_id");
        String nombreTipoNegocio = rs.getString("tipoNegocio_nombre");
        TipoNegocio tipoNegocio = new TipoNegocio(idTipoNegocio, nombreTipoNegocio);

        // Crear objeto Negocio (que extiende Usuario)
        // Nota: El constructor de Negocio parece recibir dos File para la foto. Se usa tempFile para el super y tempFile2 para la foto del negocio.
                // Nota: El constructor de Negocio recibe 'nombre' para el nombre del negocio y 'nombreUsuario' para el nombre del super.
        negocioRes = new Negocio( // Se asigna al resultado
            idUsuario,       // id de Usuario (super)
            tempFile,      // imagen (super) -> Esta imagen es la de 'negocio_fotos'
            telefono,       // numTel (super)
            email,         // email (super)
            contrasena,      // contraseña (super) – ¡ojo! asegúrate de incluirla si es necesaria
            nombreUsuario,     // nombre (super) -> Este es el nombre del usuario
            idNegocio,       // id1 (id específico del negocio)
            nombreNegocio,     // nombre del negocio
            descripcion,      // descripción
            direccion,       // dirección
            tempFile2,         // fotos -> Esta imagen es la de 'usuario_logo'
            tipoNegocio,      // TipoNegocio
            puntuacion       // puntuación
        );
      }

      return negocioRes;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e); // Envuelve FileNotFoundException en RuntimeException
    } catch (IOException e) {
      throw new RuntimeException(e); // Envuelve IOException en RuntimeException
    } finally {
      // Cerramos conexión
      this.cerrarConexion();
    }
  }

//  public Image getNegocioImageFromId(int idNegocio) {
//    Image img = null;
//
//    try {
//      String sql = "SELECT imagenBlob FROM negocio WHERE idNegocio = ?";
//      PreparedStatement ps = this.getConexion().prepareStatement(sql);
//      ps.setInt(1, idNegocio);
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
