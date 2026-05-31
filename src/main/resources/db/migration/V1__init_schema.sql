-- =====================================================================
-- V1 - Esquema inicial del dominio académico
-- (alumnos, profesores, cursos, matriculas, usuarios)
-- Generado a partir del DDL de Hibernate (dialecto MySQL) y ajustado
-- con nombres de constraint legibles.
-- =====================================================================

CREATE TABLE alumnos (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    fecha_registro  DATE,
    PRIMARY KEY (id),
    CONSTRAINT uk_alumnos_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE profesores (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(255) NOT NULL,
    email               VARCHAR(255),
    especialidad        VARCHAR(255),
    fecha_contratacion  DATE,
    PRIMARY KEY (id),
    CONSTRAINT uk_profesores_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE cursos (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(255) NOT NULL,
    descripcion  VARCHAR(255),
    creditos     INTEGER,
    profesor_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_cursos_profesor FOREIGN KEY (profesor_id) REFERENCES profesores (id)
) ENGINE=InnoDB;

CREATE TABLE matriculas (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    alumno_id        BIGINT NOT NULL,
    curso_id         BIGINT NOT NULL,
    fecha_matricula  DATE   NOT NULL,
    estado           ENUM ('ACTIVA','CANCELADA','FINALIZADA') NOT NULL,
    nota             FLOAT(53),
    PRIMARY KEY (id),
    CONSTRAINT uk_matricula_alumno_curso UNIQUE (alumno_id, curso_id),
    CONSTRAINT fk_matriculas_alumno FOREIGN KEY (alumno_id) REFERENCES alumnos (id),
    CONSTRAINT fk_matriculas_curso  FOREIGN KEY (curso_id)  REFERENCES cursos (id)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    rol         ENUM ('ADMIN','ALUMNO','PROFESOR') NOT NULL,
    habilitado  BIT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_username UNIQUE (username)
) ENGINE=InnoDB;
