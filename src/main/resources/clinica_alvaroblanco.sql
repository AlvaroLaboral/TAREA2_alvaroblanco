DROP DATABASE IF EXISTS clinica_alvaroblanco;
CREATE DATABASE clinica_alvaroblanco CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE clinica_alvaroblanco;

CREATE TABLE paciente (
    idPaciente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fechaNacimiento DATE,
    telefono VARCHAR(15),
    email VARCHAR(100),
    direccion VARCHAR(150),
    nifNie VARCHAR(9) NOT NULL UNIQUE,
    usuario VARCHAR(30) UNIQUE,
    password VARCHAR(30),
    numHistoria BIGINT NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE historial_alergia (
    numHistoria BIGINT NOT NULL,
    idAlergia BIGINT NOT NULL,
    PRIMARY KEY (numHistoria, idAlergia),
    FOREIGN KEY (numHistoria) REFERENCES paciente(numHistoria) ON DELETE CASCADE
);

CREATE TABLE tratamiento (
    idTratamiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    idPaciente BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fechaInicio DATE NOT NULL,
    consentimiento BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (idPaciente) REFERENCES paciente(idPaciente)
);

CREATE TABLE empleado (
    idEmpleado BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombreYapellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(100),
    nifNie VARCHAR(9) NOT NULL UNIQUE,
    direccion VARCHAR(150),
    usuario VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    perfil ENUM('SECRETARIADO','ENFERMERIA','CIRUGIA') NOT NULL
);

CREATE TABLE secretario (
    idEmpleado BIGINT PRIMARY KEY,
    aniosExperiencia INT NOT NULL DEFAULT 0,
    FOREIGN KEY (idEmpleado) REFERENCES empleado(idEmpleado) ON DELETE CASCADE
);

CREATE TABLE enfermero (
    idEmpleado BIGINT PRIMARY KEY,
    categoria ENUM('A','B','C') NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES empleado(idEmpleado) ON DELETE CASCADE
);

CREATE TABLE cirujano (
    idEmpleado BIGINT PRIMARY KEY,
    FOREIGN KEY (idEmpleado) REFERENCES empleado(idEmpleado) ON DELETE CASCADE
);

CREATE TABLE especialidad (
    idEspecialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE cirujano_especialidad (
    idEmpleado BIGINT NOT NULL,
    idEspecialidad BIGINT NOT NULL,
    PRIMARY KEY (idEmpleado, idEspecialidad),
    FOREIGN KEY (idEmpleado) REFERENCES cirujano(idEmpleado) ON DELETE CASCADE,
    FOREIGN KEY (idEspecialidad) REFERENCES especialidad(idEspecialidad)
);

CREATE TABLE cita (
    idCita BIGINT AUTO_INCREMENT PRIMARY KEY,
    fechaHora DATETIME NOT NULL,
    anotacion TEXT NOT NULL,
    idTratamiento BIGINT NOT NULL,
    idEmpleado BIGINT NOT NULL,
    tipo ENUM('REVISION','INTERVENCION') NOT NULL,
    FOREIGN KEY (idTratamiento) REFERENCES tratamiento(idTratamiento),
    FOREIGN KEY (idEmpleado) REFERENCES enfermero(idEmpleado)
);

CREATE TABLE intervencion (
    idCita BIGINT PRIMARY KEY,
    duracionMin INT NOT NULL CHECK (duracionMin > 0),
    FOREIGN KEY (idCita) REFERENCES cita(idCita) ON DELETE CASCADE
);

CREATE TABLE intervencion_cirujano (
    idCita BIGINT NOT NULL,
    idEmpleado BIGINT NOT NULL,
    PRIMARY KEY (idCita, idEmpleado),
    FOREIGN KEY (idCita) REFERENCES intervencion(idCita) ON DELETE CASCADE,
    FOREIGN KEY (idEmpleado) REFERENCES cirujano(idEmpleado)
);

INSERT INTO especialidad (nombre) VALUES
('Odontología'), ('Fisioterapia'), ('Ginecología'), ('Cirugía plástica'),
('Digestivo'), ('Cirugía pediátrica'), ('Radiología'), ('Geriatría');

INSERT INTO empleado (nombreYapellidos, telefono, email, nifNie, direccion, usuario, password, perfil) VALUES
('Camila Sánchez Ruiz', '600111222', 'camila@clinica.es', '12345678Z', 'C/ Uría 1, Oviedo', 'camila', 'cam1las', 'SECRETARIADO'),
('Patricia González Díaz', '600333444', 'patri@clinica.es', '87654321X', 'C/ Corrida 5, Gijón', 'patrigc', 'patri', 'ENFERMERIA'),
('Alberto García López', '600555666', 'alberto@clinica.es', '11223344B', 'Av. Galicia 10, Oviedo', 'alberto', 'alberto', 'CIRUGIA');

INSERT INTO secretario (idEmpleado, aniosExperiencia) VALUES (1, 5);
INSERT INTO enfermero (idEmpleado, categoria) VALUES (2, 'A');
INSERT INTO cirujano (idEmpleado) VALUES (3);
INSERT INTO cirujano_especialidad (idEmpleado, idEspecialidad) VALUES (3, 1), (3, 4);


    

        