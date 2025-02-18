CREATE DATABASE IF NOT EXISTS Personas;
USE Personas;

CREATE TABLE Persona (
    id_persona INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL
);

CREATE TABLE Telefono (
    id_telefono INT PRIMARY KEY AUTO_INCREMENT,
    id_persona INT,
    numero VARCHAR(15) NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES Persona(id_persona) ON DELETE CASCADE
);

CREATE TABLE Vehiculo (
    id_vehiculo INT PRIMARY KEY AUTO_INCREMENT,
    id_persona INT,
    marca VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    tipo VARCHAR(50) NOT NULL, 
    FOREIGN KEY (id_persona) REFERENCES Persona(id_persona) ON DELETE CASCADE
);

SELECT * FROM Vehiculo;
SELECT * FROM Telefono;
SELECT * FROM Persona;

