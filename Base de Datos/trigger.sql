USE PetSafe;

-- 1. Eliminar triggers existentes (para evitar conflicto)
DROP TRIGGER IF EXISTS Animal_Despues_Insertar;
DROP TRIGGER IF EXISTS Animal_Despues_Actualizar;
DROP TRIGGER IF EXISTS Animal_Antes_Borrar;

-- 2. Tabla Auditoría para datos en texto
DROP TABLE IF EXISTS AuditoriaAnimal;

CREATE TABLE AuditoriaAnimal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    animal_id INT,
    operacion VARCHAR(10) NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    datos_antiguos TEXT,
    datos_nuevos TEXT,
    FOREIGN KEY (animal_id) REFERENCES animal(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 3. Tabla para backup de imagen (BLOB)
DROP TABLE IF EXISTS Animal_Imagen_Backup;

CREATE TABLE Animal_Imagen_Backup (
    id INT AUTO_INCREMENT PRIMARY KEY,
    animal_id INT,
    imagen LONGBLOB,
    fecha_copia DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (animal_id) REFERENCES animal(id)
);

-- 4. Triggers para auditoría textual (sin imagen)

DELIMITER $$

CREATE TRIGGER Animal_Despues_Insertar
AFTER INSERT ON animal
FOR EACH ROW
BEGIN
    INSERT INTO AuditoriaAnimal (animal_id, operacion, datos_nuevos)
    VALUES (
        NEW.id, 
        'INSERT', 
        CONCAT(
            'descripcion: ', NEW.descripcion, ', ',
            'tipo: ', NEW.tipo, ', ',
            'situacion: ', NEW.situacion, ', ',
            'fecha: ', NEW.fecha
        )
    );
    
    -- Backup imagen al insertar
    INSERT INTO Animal_Imagen_Backup (animal_id, imagen)
    VALUES (NEW.id, NEW.imagen);
END;
$$

CREATE TRIGGER Animal_Despues_Actualizar
AFTER UPDATE ON animal
FOR EACH ROW
BEGIN
    INSERT INTO AuditoriaAnimal (animal_id, operacion, datos_antiguos, datos_nuevos)
    VALUES (
        NEW.id, 
        'UPDATE',
        CONCAT(
            'descripcion: ', OLD.descripcion, ', ',
            'tipo: ', OLD.tipo, ', ',
            'situacion: ', OLD.situacion, ', ',
            'fecha: ', OLD.fecha
        ),
        CONCAT(
            'descripcion: ', NEW.descripcion, ', ',
            'tipo: ', NEW.tipo, ', ',
            'situacion: ', NEW.situacion, ', ',
            'fecha: ', NEW.fecha
        )
    );
    
    -- Backup imagen al actualizar
    INSERT INTO Animal_Imagen_Backup (animal_id, imagen)
    VALUES (NEW.id, NEW.imagen);
END;
$$

CREATE TRIGGER Animal_Antes_Borrar
BEFORE DELETE ON animal
FOR EACH ROW
BEGIN
    INSERT INTO AuditoriaAnimal (animal_id, operacion, datos_antiguos)
    VALUES (
        OLD.id, 
        'DELETE', 
        CONCAT(
            'descripcion: ', OLD.descripcion, ', ',
            'tipo: ', OLD.tipo, ', ',
            'situacion: ', OLD.situacion, ', ',
            'fecha: ', OLD.fecha
        )
    );
    
    -- Backup imagen antes de borrar
    INSERT INTO Animal_Imagen_Backup (animal_id, imagen)
    VALUES (OLD.id, OLD.imagen);
END;
$$

DELIMITER ;
