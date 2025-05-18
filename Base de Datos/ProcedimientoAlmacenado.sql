USE PetSafe;

DELIMITER $$
DROP PROCEDURE IF EXISTS puntuacionNegocio $$
CREATE PROCEDURE puntuacionNegocio (IN p_idNegocio INT, IN p_nuevaPuntuacion DOUBLE)
BEGIN
UPDATE Negocio
SET puntuacion = p_nuevaPuntuacion
WHERE idNegocio = p_idNegocio;
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS animalesSituacion $$
CREATE PROCEDURE animalesSituacion(IN p_situacion INT)
BEGIN
SELECT a.id, a.descripcion, t.nombre AS tipo, s.nombre AS situacion, a.fecha, a.imagen
FROM animal a
JOIN Tipo t ON a.tipo = t.id
JOIN Situacion s ON a.situacion = s.id
WHERE a.situacion = p_situacion; 
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS animalesPorSituacion $$
CREATE PROCEDURE animalesPorSituacion(IN p_situacion INT)
BEGIN
SELECT a.id, a.descripcion, t.nombre AS tipo, s.nombre AS situacion, a.fecha, a.imagen
FROM animal a
JOIN Tipo t ON a.tipo = t.id
JOIN Situacion s ON a.situacion = s.id
WHERE a.situacion = p_situacion; 
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS registroUsuario $$
CREATE PROCEDURE registroUsuario(IN p_nombreUser VARCHAR(255), IN p_contraseña VARCHAR(255),
IN p_email VARCHAR(255),
IN p_telefonoContacto VARCHAR(255),
IN p_logoImagen VARCHAR(255))
BEGIN
INSERT INTO Usuario (nombreUser, contraseña, email, telefonoContacto, logoImagen)
SELECT p_nombreUser, p_contraseña, p_email, p_telefonoContacto, p_logoImagen;
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS registroAnimal $$
CREATE PROCEDURE registroAnimal(IN p_situacion INT, IN p_descripcion VARCHAR(255),
IN p_tipo INT,
IN p_imagen VARCHAR(255),
IN p_fecha DATE,
IN p_telefono VARCHAR(255))
BEGIN
DECLARE v_usuario_id INT;

SELECT id INTO v_usuario_id FROM Usuario WHERE telefonoContacto = p_telefono LIMIT 1;
INSERT INTO animal (descripcion, tipo, situacion, fecha, imagen) 
SELECT p_descripcion, p_tipo, p_situacion, p_fecha, p_imagen;

SET @animal_id = LAST_INSERT_ID();

INSERT INTO Reporte (animal, Usuario, ubicacion)
SELECT @animal_id, v_usuario_id, 'Ubicación no especificada';
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS registroEmpresa $$
CREATE PROCEDURE registroEmpresa ( IN p_nombre VARCHAR (255), IN p_tipo VARCHAR (255),
IN p_cif VARCHAR (255), 
IN contraseña VARCHAR (255),
IN p_email VARCHAR(255),
IN p_direccion VARCHAR(255),
IN p_descripcion VARCHAR(255),
IN p_fotos VARCHAR(255),
IN p_tipoNegocio_id INT
)
BEGIN
DECLARE v_usuario_id INT;

INSERT INTO Usuario (nombreUser, contraseña, email, telefonoContacto, logoImagen)
SELECT p_cif, p_contraseña, p_email, '', '';  -- No usamos VALUES, sino un SELECT
        
SET v_usuario_id = LAST_INSERT_ID();

IF p_tipo = 'Negocio' THEN
INSERT INTO Negocio (nombreNegocio, direccion, descripcion, fotos, puntuacion, tipoNegocio_id, Usuario_id)
SELECT p_nombre, p_direccion, p_descripcion, p_fotos, 0.0, p_tipoNegocio_id, v_usuario_id;

ELSEIF p_tipo = 'Protectora' THEN
INSERT INTO Protectoras (nombreProtectora, direccion, descripcion, fotos, Usuario_id)
SELECT p_nombre, p_direccion, p_descripcion, p_fotos, v_usuario_id;
ELSE
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Error: El tipo debe ser "Negocio" o "Protectora".';
END IF;

END$$

DELIMITER ;

SHOW PROCEDURE STATUS WHERE Db = 'PetSafe';
