CREATE DATABASE IF NOT EXISTS PetSafe;
USE PetSafe;

CREATE TABLE Tipo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100)
);

INSERT INTO Tipo (nombre) VALUES
('Perro'),
('Gato'),
('Ave'),
('Otro');

CREATE TABLE Situacion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100)
);

INSERT INTO Situacion (nombre) VALUES
('Encontrado'),
('Perdido'),
('En Adopción');

CREATE TABLE IF NOT EXISTS animal (
    id INT PRIMARY KEY AUTO_INCREMENT,
    descripcion VARCHAR (100),
    tipo INT,
    situacion INT,
    fecha DATE,
    imagen VARCHAR(255),
    FOREIGN KEY (tipo) REFERENCES Tipo(id),
    FOREIGN KEY (situacion) REFERENCES Situacion(id)
    );

    INSERT INTO animal (descripcion, tipo, situacion, fecha, imagen) VALUES
('Golden Retriever joven y amigable', 1, 1, '2025-05-05', 'golden_encontrado.jpg'),
('Gato siamés con ojos azules', 2, 2, '2025-05-01', 'siames_perdido.png'),
('Canario amarillo, canta muy bien', 3, 1, '2025-04-28', 'canario_encontrado.jpeg'),
('Cachorro mestizo, busca hogar', 1, 3, '2025-05-03', 'mestizo_adopcion.gif'),
('Gata persa, muy cariñosa', 2, 3, '2025-04-20', 'persa_adopcion.webp');

CREATE TABLE Usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombreUser VARCHAR(100),
    contraseña VARCHAR(100),
    email VARCHAR(100),
    telefonoContacto VARCHAR(20),
    logoImagen VARCHAR(255)
);

INSERT INTO Usuario (nombreUser, contraseña, email, telefonoContacto, logoImagen) VALUES
('Usuario123', 'pass123', 'usuario1@email.com', '612345678', 'usuario1_logo.png'),
('AmanteDeAnimales', 'animales456', 'amante@miemail.net', '698765432', 'amante_logo.jpg'),
('FundacionPeluditos', 'protectora2023', 'info@peluditos.org', '911234567', 'peluditos_logo.svg');

CREATE TABLE Reporte (
    id INT PRIMARY KEY AUTO_INCREMENT,
    animal INT,
    Usuario INT,
    FOREIGN KEY (animal) REFERENCES animal(id),
    FOREIGN KEY (Usuario) REFERENCES Usuario(id),
    ubicacion VARCHAR (255)
);

INSERT INTO Reporte (animal, Usuario, ubicacion) VALUES
(1, 1, 'Parque Central, cerca de la fuente'),
(2, 2, 'Calle Mayor, esquina con Luna'),
(3, 1, 'Jardín de una casa en la urbanización'),
(4, 3, 'Refugio de animales "Patitas Felices"'),
(5, 2, 'Clínica veterinaria "San Roque"');

CREATE TABLE Persona (
    idPersona INT PRIMARY KEY AUTO_INCREMENT,
    fechaNacimiento DATE,
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    Usuario_id INT,  -- Cambiado a Usuario_id
    FOREIGN KEY (Usuario_id) REFERENCES Usuario(id) -- Referencia al id de Usuario
);


INSERT INTO Persona (fechaNacimiento, nombre, apellidos, Usuario_id) VALUES
('1990-03-15', 'Ana', 'García López', 1),
('1985-11-20', 'Carlos', 'Pérez Ruiz', 2),
('2000-07-01', 'Sofía', 'Martínez Sanz', 3);

CREATE TABLE TipoNegocio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100)
);

INSERT INTO TipoNegocio (nombre) VALUES
('Veterinaria'),
('Tienda de Mascotas'),
('Peluquería Canina');

CREATE TABLE Negocio (
    idNegocio INT PRIMARY KEY AUTO_INCREMENT,
    nombreNegocio VARCHAR  (100),
    direccion VARCHAR (255),
    descripcion VARCHAR (255),
    fotos VARCHAR (255),
    puntuacion DOUBLE,
    tipoNegocio_id INT,
    Usuario_id INT, -- Añadido Usuario_id
    FOREIGN KEY (tipoNegocio_id) REFERENCES TipoNegocio(id),
    FOREIGN KEY (Usuario_id) REFERENCES Usuario(id)
);

INSERT INTO Negocio (nombreNegocio, direccion, descripcion, fotos, tipoNegocio_id, Usuario_id) VALUES
('Clínica Veterinaria Mascotas Felices', 'Av. de la Constitución, 15', 'Servicios veterinarios completos para todo tipo de animales.', 'clinica_fotos.jpg', 1, 1),
('Mundo Animal Tienda', 'C/ del Sol, 22', 'Alimentos, accesorios y juguetes para tus mascotas.', 'tienda_fotos.png', 2, 2),
('Estilo Canino', 'Plaza Mayor, 5', 'Servicios profesionales de peluquería y estética canina.', 'peluqueria_fotos.jpeg', 3, 3);

CREATE TABLE Protectoras (
    idProtectora INT PRIMARY KEY AUTO_INCREMENT,
    nombreProtectora VARCHAR (255),
    direccion VARCHAR (255),
    descripcion VARCHAR (255),
    fotos VARCHAR (255),
    Usuario_id INT, -- Añadido Usuario_id
    FOREIGN KEY (Usuario_id) REFERENCES Usuario(id)
);

INSERT INTO Protectoras (nombreProtectora, direccion, descripcion, fotos, Usuario_id) VALUES
('Huellas de Esperanza', 'Camino Viejo, Km 3', 'Refugio dedicado al rescate y adopción de animales abandonados.', 'huellas_fotos.webp', 1),
('Patitas Unidas', 'Polígono Industrial, Nave 10', 'Asociación sin ánimo de lucro que busca hogar para animales necesitados.', 'patitas_fotos.gif', 3);

CREATE USER 'borja'@'%' IDENTIFIED BY 'borja';
CREATE USER 'andrea'@'%' IDENTIFIED BY 'andrea';
CREATE USER 'raul'@'%' IDENTIFIED BY 'raul';
CREATE USER 'cristina'@'%' IDENTIFIED BY 'cristina';
CREATE USER 'edgar'@'%' IDENTIFIED BY 'edgar';

GRANT ALL PRIVILEGES ON PetSafe.* TO 'borja'@'%';
GRANT ALL PRIVILEGES ON PetSafe.* TO 'andrea'@'%';
GRANT ALL PRIVILEGES ON PetSafe.* TO 'raul'@'%';
GRANT ALL PRIVILEGES ON PetSafe.* TO 'cristina'@'%';
GRANT ALL PRIVILEGES ON PetSafe.* TO 'edgar'@'%';

FLUSH PRIVILEGES;