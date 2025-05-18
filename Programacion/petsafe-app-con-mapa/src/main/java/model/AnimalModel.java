package model;

import com.example.petsafeapp.Animal;
import com.example.petsafeapp.Situacion;
import com.example.petsafeapp.TipoAnimal;
import javafx.scene.image.Image; // No se usa directamente en esta versión, pero se mantiene el import

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * Clase modelo para gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * de la entidad {@link Animal} en la base de datos.
 * Esta versión de la clase maneja las imágenes de los animales como objetos {@link File}
 * y las almacena y recupera como datos binarios (BLOB) en la base de datos.
 * Extiende {@link DBUtil} para utilizar sus funcionalidades de conexión a la base de datos.
 */
public class AnimalModel extends DBUtil {

    /**
     * Crea un nuevo registro de animal en la base de datos, incluyendo su imagen como un BLOB.
     * La imagen se obtiene del objeto {@link Animal} a través de {@code animal.getFoto()}.
     *
     * @param animal El objeto {@link Animal} que contiene la información a insertar.
     *               Se espera que el objeto {@code animal} tenga establecidos sus atributos
     *               de situación, descripción, tipo, fecha y la foto (como un objeto {@link File}).
     * @return {@code true} si el animal fue creado exitosamente en la base de datos,
     *         {@code false} en caso contrario o si ocurre un error.
     * @throws FileNotFoundException si el archivo de imagen especificado en {@code animal.getFoto()} no se encuentra.
     */
    public boolean createAnimal(Animal animal) throws FileNotFoundException {
        boolean res = false;

        Situacion situacion = animal.getSituacion();
        int idSituacion = situacion.getId();
        String descripcion = animal.getDescripción();
        TipoAnimal tipo = animal.getTipo();
        int idTipo = tipo.getId();
        Date fecha = Date.valueOf(animal.getDate());
        File imagen = animal.getFoto(); // Obtiene la imagen como File desde el objeto Animal
        FileInputStream fis = new FileInputStream(imagen);

        try {
            String sql = "INSERT INTO animal (situacion, descripcion, tipo, fecha, imagen) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idSituacion);
            ps.setString(2, descripcion);
            ps.setInt(3, idTipo);
            ps.setDate(4, fecha);
            ps.setBinaryStream(5, fis, (int) imagen.length()); // Almacena el archivo como BLOB

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
     * Lee todos los registros de animales de la base de datos.
     * Para cada animal, recupera su imagen (almacenada como BLOB) y la guarda en un archivo temporal.
     * El objeto {@link Animal} devuelto contendrá una referencia a este archivo temporal.
     *
     * @return Un {@link ArrayList} de objetos {@link Animal} que representan todos los
     *         animales encontrados. Retorna una lista vacía si no hay animales o
     *         {@code null} si ocurre un error durante la consulta.
     * @throws FileNotFoundException si ocurre un error al crear el archivo temporal o al escribir en él.
     * @throws IOException si ocurre un error de entrada/salida al leer el flujo de la imagen
     *         o al escribir en el archivo temporal.
     */
    public ArrayList<Animal> readAnimales() throws FileNotFoundException, IOException {
        ArrayList<Animal> animales = new ArrayList<Animal>();

        try {
            String sql = "SELECT\n" +
                    "    a.id AS animal_id,  -- Alias para evitar ambigüedad si se usa en frameworks\n" +
                    "    a.descripcion,\n" +
                    "    a.tipo AS tipo_id, -- El ID del tipo\n" +
                    "    t.nombre AS tipo_nombre, -- El nombre del tipo\n" +
                    "    a.situacion AS situacion_id, -- El ID de la situación\n" +
                    "    s.nombre AS situacion_nombre, -- El nombre de la situación\n" +
                    "    a.fecha,\n" +
                    "    a.imagen\n" + 
                    "FROM\n" +
                    "    animal a -- Alias 'a' para la tabla animal\n" +
                    "INNER JOIN\n" +
                    "    Tipo t ON a.tipo = t.id -- Unir con Tipo donde los IDs coincidan\n" +
                    "INNER JOIN\n" +
                    "    Situacion s ON a.situacion = s.id -- Unir con Situacion donde los IDs coincidan";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("animal_id");
                String descripcion = rs.getString("descripcion");
                int idTipo = rs.getInt("tipo_id");
                String nombreTipo = rs.getString("tipo_nombre");
                TipoAnimal tipoAnimal = new TipoAnimal(idTipo,nombreTipo);
                int idSituacion = rs.getInt("situacion_id");
                String nombreSituacion = rs.getString("situacion_nombre");
                Situacion situacion = new Situacion(idSituacion, nombreSituacion);
                Date fechaDate = rs.getDate("fecha");
                LocalDate fecha = fechaDate.toLocalDate();

                InputStream is = rs.getBinaryStream("imagen"); // Lee el BLOB como InputStream
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

                Animal animal = new Animal(id,tempFile,fecha,tipoAnimal,descripcion,situacion);
                animales.add(animal);
            }

            return animales;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
    }

    /**
     * Actualiza un registro de animal existente en la base de datos, incluyendo su imagen.
     * La imagen se obtiene del objeto {@link Animal} ({@code animal.getFoto()}) y se actualiza
     * como un BLOB en la base de datos.
     * <p>
     * Nota: Este método podría lanzar una {@link FileNotFoundException} si el archivo
     * obtenido de {@code animal.getFoto()} no existe, aunque no está declarado explícitamente.
     * </p>
     *
     * @param animal El objeto {@link Animal} con los datos actualizados.
     *               Debe contener el ID del animal a modificar y los nuevos valores
     *               para descripción, tipo, situación, fecha y la nueva foto (como {@link File}).
     * @return El número de filas afectadas por la operación de actualización.
     *         Típicamente será 1 si la actualización fue exitosa, o 0 si el animal
     *         no fue encontrado o si ocurre un error.
     */
    public int updateAnimal(Animal animal) { 
        int res = 0;
        int idTipo = animal.getTipo().getId();
        int idSituacion = animal.getSituacion().getId();
        Date fecha = Date.valueOf(animal.getDate());
        FileInputStream fis = null;

        try {
            String sql = "UPDATE animal\n" +
                    "SET\n" +
                    "    descripcion = ?,   -- 1st placeholder\n" +
                    "    tipo = ?,          -- 2nd placeholder\n" +
                    "    situacion = ?,     -- 3rd placeholder\n" +
                    "    fecha = ?,         -- 4th placeholder\n" +
                    "    imagen = ?         -- 5th placeholder (para el BLOB de la imagen)\n" +
                    "WHERE\n" +
                    "    id = ?;            -- 6th placeholder (for the ID)";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            fis = new FileInputStream(animal.getFoto()); 

            ps.setString(1, animal.getDescripción());
            ps.setInt(2, idTipo);
            ps.setInt(3, idSituacion);
            ps.setDate(4, fecha);
            ps.setBinaryStream(5, fis, (int) animal.getFoto().length()); // Actualiza el BLOB
            ps.setInt(6, animal.getId());

            res = ps.executeUpdate();
        } catch (SQLException | FileNotFoundException e) { // Captura FileNotFoundException también
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
     * Elimina un registro de animal de la base de datos utilizando el ID
     * contenido en el objeto {@link Animal} proporcionado.
     * Este método es una sobrecarga que delega la lógica al método
     * {@link #deleteAnimal(int)}.
     *
     * @param animal El objeto {@link Animal} cuyo ID se utilizará para identificar
     *               el registro a eliminar.
     * @return {@code true} si el animal fue eliminado exitosamente,
     *         {@code false} en caso contrario o si ocurre un error.
     * @see #deleteAnimal(int)
     */
    public boolean deleteAnimal(Animal animal) {
        return this.deleteAnimal(animal.getId());
    }

    /**
     * Elimina un registro de animal de la base de datos utilizando su ID.
     *
     * @param idAnimal El ID del animal que se desea eliminar.
     * @return {@code true} si el animal fue eliminado exitosamente (al menos una fila afectada),
     *         {@code false} si el animal no fue encontrado (0 filas afectadas) o si ocurre un error.
     */
    public boolean deleteAnimal(int idAnimal) {
        boolean res = false;

        try {
            String sql = "DELETE FROM animal WHERE id = ?";
            PreparedStatement ps = this.getConexion().prepareStatement(sql);

            ps.setInt(1, idAnimal);

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
     * Obtiene un animal de la base de datos basándose en su nombre y tipo de animal.
     * La imagen del animal se recupera como un BLOB y se guarda en un archivo temporal.
     * <p>
     * <strong>Advertencia de Seguridad:</strong> La construcción de la consulta SQL mediante
     * concatenación de cadenas con los parámetros {@code nombre} y {@code tipo.getId()}
     * es vulnerable a inyección SQL. Se recomienda utilizar {@link PreparedStatement}
     * con placeholders (?) para estos parámetros. El código se documenta tal cual está.
     * </p>
     * <p>
     * Nota: La consulta asume que la tabla {@code animal} tiene una columna {@code nombre}.
     * </p>
     *
     * @param nombre El nombre del animal a buscar.
     * @param tipo El {@link TipoAnimal} del animal a buscar.
     * @return Un objeto {@link Animal} si se encuentra un animal que coincida con los criterios,
     *         o {@code null} si no se encuentra o si ocurre un error. El objeto {@link Animal}
     *         devuelto contendrá una referencia al archivo temporal de su imagen.
     * @throws IOException Si ocurre un error durante la creación o escritura del archivo temporal.
     */
    public Animal getAnimalConNombreYTipo(String nombre, TipoAnimal tipo) throws IOException {
        Animal animal = null;

        String sql = "SELECT\n" +
                "    a.id AS animal_id,\n" +
                "    a.descripcion,\n" +
                "    a.tipo AS tipo_id,\n" +
                "    t.nombre AS tipo_nombre,\n" +
                "    a.situacion AS situacion_id,\n" +
                "    s.nombre AS situacion_nombre,\n" +
                "    a.fecha,\n" +
                "    a.imagen\n" + // Se asume que esta columna 'imagen' contiene el BLOB
                "FROM\n" +
                "    animal a -- Alias 'a' para la tabla animal\n" +
                "INNER JOIN\n" +
                "    Tipo t ON a.tipo = t.id -- Unir con Tipo donde los IDs coincidan\n" +
                "INNER JOIN\n" +
                "    Situacion s ON a.situacion = s.id -- Unir con Situacion donde los IDs coincidan\n" +
                " WHERE a.descripcion = ? AND a.tipo = ? ";
        
        try (PreparedStatement ps = this.getConexion().prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, tipo.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { 
                    int id = rs.getInt("animal_id");
                    String descripcion = rs.getString("descripcion");
                    int idTipo = rs.getInt("tipo_id");
                    String nombreTipo = rs.getString("tipo_nombre");
                    TipoAnimal tipoAnimal = new TipoAnimal(idTipo,nombreTipo);
                    int idSituacion = rs.getInt("situacion_id");
                    String nombreSituacion = rs.getString("situacion_nombre");
                    Situacion situacion = new Situacion(idSituacion, nombreSituacion);
                    Date fechaDate = rs.getDate("fecha");
                    LocalDate fecha = fechaDate.toLocalDate();

                    InputStream is = rs.getBinaryStream("imagen");
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
                    animal = new Animal(id,tempFile,fecha,tipoAnimal,descripcion,situacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerramos conexión
            this.cerrarConexion();
        }
        return animal; // Devolver el animal construido o null
    }

//    /**
//     * Obtiene la imagen de un animal (como un objeto {@link javafx.scene.image.Image})
//     * desde la base de datos utilizando el ID del animal.
//     * La imagen se recupera de una columna BLOB llamada 'imagenBlob'.
//     * <p>
//     * Nota: Este método está actualmente comentado en el código fuente.
//     * </p>
//     *
//     * @param idAnimal El ID del animal cuya imagen se desea obtener.
//     * @return Un objeto {@link javafx.scene.image.Image} si se encuentra la imagen y se puede cargar,
//     *         o {@code null} si no se encuentra el animal, no tiene imagen,
//     *         o si ocurre un error durante la consulta o carga de la imagen.
//     */
//    public Image getAnimalImageFromId(int idAnimal) {
//        Image img = null;
//
//        try {
//            String sql = "SELECT imagenBlob FROM animal WHERE id = ?";
//            PreparedStatement ps = this.getConexion().prepareStatement(sql);
//            ps.setInt(1, idAnimal);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                InputStream is = rs.getBinaryStream("imagenBlob");
//                if (is != null) {
//                    img = new Image(is); // Crea un objeto Image de JavaFX
//                    is.close(); // Es importante cerrar el InputStream
//                }
//            }
//
//            return img;
//        } catch (SQLException | IOException e) { // IOException por is.close()
//            e.printStackTrace();
//            return null;
//        } finally {
//            // Cerramos conexión
//            this.cerrarConexion();
//        }
//    }
}
