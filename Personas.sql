-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS Personas;

-- Seleccionar la base de datos
USE Personas;

CREATE TABLE IF NOT EXISTS Personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(15) NOT NULL,
    vehiculo VARCHAR(50) NOT NULL
);

DROP DATABASE IF EXISTS Personas;

SELECT * FROM PERSONAS;