-- Eliminar la base de datos si ya existe
DROP DATABASE IF EXISTS ChatServer;

-- Crear la base de datos
CREATE DATABASE ChatServer;

-- Usar la base de datos recién creada
USE ChatServer;

-- Crear tabla USER
CREATE TABLE USER (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    remote_object_address VARCHAR(255)
);

-- Crear tabla FRIENDSHIP
CREATE TABLE FRIENDSHIP (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requesting_user VARCHAR(50) NOT NULL,
    receiving_user VARCHAR(50) NOT NULL,
    pending TINYINT(1) NOT NULL,
    FOREIGN KEY (requesting_user) REFERENCES USER(username) ON DELETE CASCADE,
    FOREIGN KEY (receiving_user) REFERENCES USER(username) ON DELETE CASCADE
);
