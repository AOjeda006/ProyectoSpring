# Gestión Académica API

> API REST para la gestión académica de alumnos, profesores, cursos y matrículas, construida con **Spring Boot 4 + Java 17** sobre **MySQL**, con **autenticación JWT y roles**, migraciones con **Flyway**, documentación **OpenAPI/Swagger** y una arquitectura en capas limpia.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.6-E94E1B)](https://mapstruct.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)](https://springdoc.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## Tabla de contenidos

1. [Descripción](#descripción)
2. [Características](#características)
3. [Stack tecnológico](#stack-tecnológico)
4. [Arquitectura](#arquitectura)
5. [Estructura del proyecto](#estructura-del-proyecto)
6. [Modelo de datos](#modelo-de-datos)
7. [API REST](#api-rest)
8. [Seguridad y autenticación](#seguridad-y-autenticación)
9. [Flujo de una operación de extremo a extremo](#flujo-de-una-operación-de-extremo-a-extremo)
10. [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
11. [Pruebas](#pruebas)
12. [Decisiones técnicas destacadas](#decisiones-técnicas-destacadas)
13. [Autor](#autor)

---

## Descripción

**Gestión Académica API** es un servicio REST que cubre el ciclo de gestión de un
centro educativo: dar de alta **profesores**, crear **cursos** asignados a un
profesor, registrar **alumnos** y **matricularlos** en cursos, controlando el
estado de cada matrícula (activa, finalizada, cancelada) y su calificación final.

El proyecto se ha construido como pieza de portfolio para demostrar el desarrollo
**backend end-to-end** de una API de gestión real: un dominio relacional rico, una
capa de servicio con reglas de negocio, seguridad basada en **JWT y roles**,
persistencia versionada con **Flyway**, documentación interactiva con **Swagger** y
una **pirámide de tests** (unitarios, de capa web, de integración y con
Testcontainers).

Lo más destacable del diseño es la **separación estricta de responsabilidades por
capas** y el desacoplamiento entre el modelo de persistencia y el contrato público:
las entidades JPA nunca se exponen al cliente, sino que se traducen a **DTOs de
respuesta inmutables** mediante *mappers* declarativos. El servidor es la **fuente
de verdad**: valida cada entrada, aplica las reglas de negocio y autoriza cada
operación según el rol del usuario.

---

## Características

### Gestión del dominio académico

- **CRUD de profesores, cursos, alumnos y matrículas**, con validación de entrada y
  manejo de errores homogéneo.
- **Matrícula como entidad intermedia** entre alumno y curso (no una simple relación
  *N:M*), con sus propios datos: fecha, **estado** (`ACTIVA` / `FINALIZADA` /
  `CANCELADA`) y **nota** final.
- **Reglas de negocio en la capa de servicio**: un alumno no puede matricularse dos
  veces en el mismo curso, la fecha y el estado inicial los asigna el servidor, la
  nota se valida en el rango 0–10, etc.
- **Asociación curso → profesor** y agregación de matrículas por alumno y por curso.

### Búsqueda y paginación

- **Filtrado dinámico** en los listados mediante **Spring Data Specifications**:
  varios filtros opcionales **combinables** en una sola consulta (por nombre, email,
  especialidad, rango de créditos, estado, rango de nota, etc.).
- **Paginación y ordenación** en todos los listados (`page`, `size`, `sort`).

### Seguridad

- **Autenticación con JWT** (access token de vida corta + refresh token de vida
  larga) y usuarios persistidos en base de datos con contraseñas **BCrypt**.
- **Autorización por roles** (`ADMIN`, `PROFESOR`, `ALUMNO`) a nivel de método con
  `@PreAuthorize`.
- **Registro público** (rol ALUMNO), **login**, **refresh** y alta de usuarios con
  rol concreto reservada al administrador.

### Calidad y operación

- **Migraciones de esquema versionadas** con Flyway (con Hibernate en modo
  `validate` como red de seguridad).
- **Documentación interactiva** OpenAPI 3 / Swagger UI, con botón *Authorize* para
  probar los endpoints protegidos.
- **Errores estandarizados** con `ProblemDetail` (RFC 7807).
- **Observabilidad** con Spring Boot Actuator (`health`, `info`, `metrics`).
- **Contenedorización** con Docker multi-stage y orquestación local con Docker
  Compose.

---

## Stack tecnológico

| Categoría           | Tecnología                                                          |
| ------------------- | ------------------------------------------------------------------- |
| Lenguaje            | **Java 17**                                                         |
| Framework           | **Spring Boot 4.0** (Web MVC)                                       |
| Persistencia        | **Spring Data JPA** (Hibernate) sobre **MySQL 8**                   |
| Migraciones         | **Flyway** (`flyway-mysql`)                                         |
| Seguridad           | **Spring Security** + **JWT** (jjwt) + **BCrypt**                   |
| Validación          | **Bean Validation** (Jakarta Validation)                           |
| Mapeo entidad ↔ DTO | **MapStruct 1.6**                                                   |
| Boilerplate         | **Lombok**                                                          |
| Documentación       | **springdoc-openapi** (Swagger UI)                                  |
| Observabilidad      | **Spring Boot Actuator**                                            |
| Pruebas             | **JUnit 5** · **Mockito** · **MockMvc** · **H2** · **Testcontainers** |
| Build               | **Maven** (con *wrapper*)                                           |
| Contenedores        | **Docker** · **Docker Compose**                                    |

---

## Arquitectura

Arquitectura **en capas** con dependencias dirigidas hacia el dominio. Cada petición
atraviesa controlador → servicio → repositorio, y el resultado vuelve traducido a
DTOs; las entidades JPA nunca cruzan la frontera HTTP.

```
                    ┌───────────────────────────────────────────────┐
   HTTP / JSON      │  uicontrollers  (REST Controllers + Swagger)   │
   ───────────────► │  · valida entrada (@Valid)                     │
                    │  · GlobalExceptionHandler → ProblemDetail      │
                    └───────────────────────┬───────────────────────┘
                                            │ DTOs
                    ┌───────────────────────▼───────────────────────┐
                    │  service  (reglas de negocio)                  │
                    │  · orquesta repositorios y mappers             │
                    │  · lanza excepciones de dominio                │
                    └───────────────────────┬───────────────────────┘
                                            │ entidades
                    ┌───────────────────────▼───────────────────────┐
                    │  data  (repositorios JPA + Specifications)     │
                    │  · Spring Data JPA / Hibernate                 │
                    └───────────────────────┬───────────────────────┘
                                            │ SQL
                    ┌───────────────────────▼───────────────────────┐
                    │  MySQL  (esquema gestionado por Flyway)        │
                    └───────────────────────────────────────────────┘

   security  (filtro JWT, UserDetailsService, @PreAuthorize)  ── transversal
   domain    (entities · dto · mappers · exceptions · enums)  ── núcleo compartido
```

- **`uicontrollers`** — controladores REST finos: reciben la petición, validan el
  cuerpo, delegan en el servicio y devuelven DTOs. Un `@ControllerAdvice` global
  traduce las excepciones a respuestas `ProblemDetail`.
- **`service`** — lógica de aplicación: orquesta repositorios y *mappers*, aplica las
  reglas de negocio y lanza excepciones de dominio (`ResourceNotFoundException`,
  `DuplicateResourceException`…).
- **`data`** — repositorios `JpaRepository` + `JpaSpecificationExecutor` y las
  **Specifications** que construyen las consultas dinámicas.
- **`domain`** — el núcleo: entidades JPA, DTOs de entrada/salida/filtro, *mappers*
  MapStruct, excepciones y *enums*.
- **`security`** — configuración de Spring Security, filtro de autenticación JWT,
  `UserDetailsService` sobre la base de datos y los manejadores de 401/403.

---

## Estructura del proyecto

```
ProyectoSpring/
├── src/main/java/com/example/proyectospring/
│   ├── ProyectoSpringApplication.java
│   ├── config/                      # DataInitializer (admin inicial), OpenApiConfig
│   ├── data/
│   │   ├── repositories/            # AlumnoRepository, CursoRepository, ...
│   │   └── specifications/          # filtros dinámicos (Criteria API)
│   ├── domain/
│   │   ├── entities/                # Alumno, Profesor, Curso, Matricula, Usuario + enums
│   │   ├── dto/                     # DTOs de entrada (AlumnoDTO, CursoDTO, ...)
│   │   │   ├── response/            # DTOs de salida (records inmutables)
│   │   │   ├── filter/              # objetos de filtro para las búsquedas
│   │   │   └── auth/                # Login/Register/Refresh/AuthResponse...
│   │   ├── mappers/                 # mappers MapStruct entidad → DTO
│   │   ├── exceptions/              # excepciones de dominio
│   │   └── security/                # SecurityConfig, JwtService, filtro JWT, handlers
│   ├── service/                     # lógica de negocio por agregado
│   └── uicontrollers/               # controladores REST + GlobalExceptionHandler
│
├── src/main/resources/
│   ├── application.properties       # config común (secretos externalizados)
│   ├── application-dev.properties   # perfil de desarrollo
│   ├── application-prod.properties  # perfil de producción
│   └── db/migration/                # V1__init_schema.sql (Flyway)
│
├── src/test/java/...                # tests unitarios, de capa web e integración
├── src/test/resources/
│   └── application-test.properties  # perfil de test (H2 en memoria)
│
├── Dockerfile                       # build multi-stage (JDK → JRE)
├── docker-compose.yml               # API + MySQL
├── .env.example                     # plantilla de variables de entorno
└── pom.xml
```

---

## Modelo de datos

Cinco tablas de dominio más la tabla de usuarios de seguridad. La relación
alumno ↔ curso se modela mediante la **entidad intermedia `matriculas`**, que añade
datos propios a cada inscripción:

```
┌──────────────────┐        ┌──────────────────┐        ┌──────────────────┐
│    profesores    │ 1    * │      cursos      │ 1    * │    matriculas    │
├──────────────────┤────────├──────────────────┤────────├──────────────────┤
│ id (PK)          │        │ id (PK)          │        │ id (PK)          │
│ nombre           │        │ nombre           │        │ alumno_id (FK)   │
│ email (U)        │        │ descripcion      │        │ curso_id  (FK)   │
│ especialidad     │        │ creditos         │        │ fecha_matricula  │
│ fecha_contrat.   │        │ profesor_id (FK) │        │ estado (enum)    │
└──────────────────┘        └──────────────────┘        │ nota             │
                                                         └────────┬─────────┘
┌──────────────────┐                                              │ *
│     usuarios     │        ┌──────────────────┐                  │ 1
├──────────────────┤        │     alumnos      │ 1                ▼
│ id (PK)          │        ├──────────────────┤──────────────────┘
│ username (U)     │        │ id (PK)          │
│ password (BCrypt)│        │ nombre           │   UNIQUE (alumno_id, curso_id)
│ rol (enum)       │        │ email (U)        │
│ habilitado       │        │ fecha_registro   │
└──────────────────┘        └──────────────────┘
```

**Decisiones de modelado relevantes:**

- **Matrícula como entidad intermedia**: alumno ↔ curso no es un `@ManyToMany` puro,
  sino la entidad `Matricula` con fecha, estado y nota propios, lo que permite
  modelar el ciclo de vida de cada inscripción.
- **Unicidad garantizada por la base de datos**: índices únicos en `email`
  (alumnos y profesores), `username` (usuarios) y en el par
  `(alumno_id, curso_id)` de `matriculas` — la integridad la asegura la BD, no el
  código de aplicación.
- **Enums persistidos como texto**: `estado` y `rol` se guardan como cadena
  (vía `ENUM` de MySQL), de modo que el dato es legible y estable frente a
  reordenaciones del enum en Java.
- **Esquema versionado con Flyway**: la estructura vive en migraciones
  (`db/migration`), y Hibernate arranca en modo `validate` para garantizar que las
  entidades y el esquema están sincronizados.
- **Asociaciones `LAZY`** entre entidades, con traducción a DTOs *resumen* en la capa
  web para no arrastrar grafos completos ni exponer las entidades.

---

## API REST

Base: `/api/v1`. Documentación interactiva en `/swagger-ui.html` y especificación en
`/v3/api-docs`. Todas las rutas (salvo las de autenticación) requieren un
*access token* JWT en la cabecera `Authorization: Bearer <token>`.

### Autenticación — `/api/v1/auth`

| Método | Ruta              | Acción                                            | Acceso |
| ------ | ----------------- | ------------------------------------------------- | ------ |
| `POST` | `/register`       | Registra un usuario (rol ALUMNO) y emite tokens   | Público |
| `POST` | `/login`          | Valida credenciales y emite access + refresh      | Público |
| `POST` | `/refresh`        | Renueva el access token con un refresh token      | Público |
| `POST` | `/usuarios`       | Crea un usuario con rol concreto                  | ADMIN  |

### Alumnos — `/api/v1/alumnos`

| Método   | Ruta            | Acción                                                 | Acceso        |
| -------- | --------------- | ------------------------------------------------------ | ------------- |
| `GET`    | `/`             | Lista/filtra (`nombre`, `email`, rango de fecha)       | Autenticado   |
| `GET`    | `/{id}`         | Obtiene un alumno por id                               | Autenticado   |
| `POST`   | `/`             | Crea un alumno                                         | ADMIN         |
| `PUT`    | `/{id}`         | Actualiza un alumno                                    | ADMIN         |
| `DELETE` | `/{id}`         | Elimina un alumno                                      | ADMIN         |

### Profesores — `/api/v1/profesores`

| Método   | Ruta            | Acción                                          | Acceso      |
| -------- | --------------- | ----------------------------------------------- | ----------- |
| `GET`    | `/`             | Lista/filtra (`nombre`, `especialidad`)         | Autenticado |
| `GET`    | `/{id}`         | Obtiene un profesor por id                      | Autenticado |
| `POST`   | `/`             | Crea un profesor                                | ADMIN       |
| `PUT`    | `/{id}`         | Actualiza un profesor                           | ADMIN       |
| `DELETE` | `/{id}`         | Elimina un profesor                             | ADMIN       |

### Cursos — `/api/v1/cursos`

| Método   | Ruta            | Acción                                                          | Acceso              |
| -------- | --------------- | -------------------------------------------------------------- | ------------------- |
| `GET`    | `/`             | Lista/filtra (`nombre`, `profesorId`, `creditosMin/Max`)       | Autenticado         |
| `GET`    | `/{id}`         | Obtiene un curso (con resumen de su profesor)                  | Autenticado         |
| `POST`   | `/`             | Crea un curso                                                  | ADMIN / PROFESOR    |
| `PUT`    | `/{id}`         | Actualiza un curso                                             | ADMIN / PROFESOR    |
| `DELETE` | `/{id}`         | Elimina un curso                                               | ADMIN               |

### Matrículas — `/api/v1/matriculas`

| Método   | Ruta            | Acción                                                                | Acceso           |
| -------- | --------------- | --------------------------------------------------------------------- | ---------------- |
| `GET`    | `/`             | Lista/filtra (`alumnoId`, `cursoId`, `estado`, `notaMin/Max`)         | Autenticado      |
| `GET`    | `/{id}`         | Obtiene una matrícula (con resúmenes de alumno y curso)               | Autenticado      |
| `POST`   | `/`             | Matricula un alumno en un curso (estado inicial ACTIVA)               | ADMIN / PROFESOR |
| `PUT`    | `/{id}`         | Actualiza estado y nota de la matrícula                               | ADMIN / PROFESOR |
| `DELETE` | `/{id}`         | Elimina una matrícula                                                 | ADMIN            |

### Observabilidad — `/actuator`

| Método | Ruta                 | Acción                       | Acceso      |
| ------ | -------------------- | ---------------------------- | ----------- |
| `GET`  | `/actuator/health`   | Estado de la aplicación      | Público     |
| `GET`  | `/actuator/info`     | Metadatos del build          | Público     |
| `GET`  | `/actuator/metrics`  | Métricas                     | Autenticado |

---

## Seguridad y autenticación

El acceso se controla con **JSON Web Tokens**. El `login` devuelve un **access
token** (vida corta) y un **refresh token** (vida larga); el cliente envía el access
token como `Bearer` en cada petición. Un filtro (`JwtAuthenticationFilter`) lo valida
y carga el usuario desde la base de datos, dejando la sesión autenticada en el
contexto de Spring Security (sin estado de servidor).

La autorización es **por rol y a nivel de método** (`@PreAuthorize`):

| Operación                                              | ADMIN | PROFESOR | ALUMNO |
| ------------------------------------------------------ | :---: | :------: | :----: |
| Lecturas (GET de cualquier recurso)                    |  Sí   |   Sí     |  Sí    |
| Crear / editar **cursos** y **matrículas**             |  Sí   |   Sí     |  No    |
| Eliminar cursos / matrículas                           |  Sí   |   No     |  No    |
| Crear / editar / eliminar **alumnos** y **profesores** |  Sí   |   No     |  No    |
| Crear usuarios con rol concreto                        |  Sí   |   No     |  No    |

Los intentos sin token reciben **401** y los que carecen del rol necesario **403**,
ambos en formato `ProblemDetail`. Al arrancar, la aplicación **siembra un usuario
administrador** inicial (configurable por variables de entorno).

> **Credenciales de demostración:** usuario `admin`, contraseña `admin1234`
> (definidas por `ADMIN_USERNAME` / `ADMIN_PASSWORD`, pensadas solo para uso local).

---

## Flujo de una operación de extremo a extremo

Matricular un alumno en un curso recorre todas las capas y muestra el desacoplamiento
entidad ↔ DTO:

```
 POST /api/v1/matriculas   { alumnoId, cursoId }   (Authorization: Bearer <JWT>)
   │
   ├─ JwtAuthenticationFilter  → valida el token y autentica al usuario
   │
   ├─ MatriculaController.crear(dto)        (@PreAuthorize ADMIN/PROFESOR, @Valid)
   │     └─ MatriculaService.create(dto)
   │           ├─ carga Alumno y Curso (404 si no existen)
   │           ├─ comprueba que no exista ya la matrícula (409 si duplicada)
   │           ├─ fija fecha = hoy y estado = ACTIVA
   │           └─ MatriculaRepository.save(...)  ─► INSERT en MySQL
   │
   └─ MatriculaMapper.toResponse(entidad)  → MatriculaResponse (alumno y curso
                                              como resúmenes, sin exponer la entidad)
          │
          ▼
   201 Created + Location: /api/v1/matriculas/{id} + cuerpo MatriculaResponse
```

Si el cuerpo no es válido (p. ej. falta `alumnoId`), la petición se corta en la
validación y el `GlobalExceptionHandler` devuelve **400** con el detalle por campo.

---

## Cómo ejecutar el proyecto

### Opción A — Docker Compose (recomendada)

Levanta la API y una base de datos MySQL con un solo comando (no necesitas Java ni
MySQL instalados):

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Opción B — Ejecución local

**Requisitos:** JDK 17, Maven (incluido el *wrapper*) y un MySQL accesible.

1. Crea la base de datos y configura las variables de entorno a partir de la
   plantilla (los secretos **no** se versionan):

   ```bash
   cp .env.example .env   # y edita los valores (DB_*, JWT_SECRET, ADMIN_*)
   ```

   Las variables se pueden exportar al entorno o pasarse a la aplicación. Valores
   mínimos:

   ```env
   DB_URL=jdbc:mysql://localhost:3306/alumnos_db
   DB_USERNAME=alumnos_user
   DB_PASSWORD=tu_password
   JWT_SECRET=una-clave-de-al-menos-32-bytes
   ```

2. Arranca la aplicación (Flyway crea/migra el esquema automáticamente):

   ```bash
   ./mvnw spring-boot:run
   ```

La aplicación queda disponible en `http://localhost:8080`.

> El esquema lo gestiona **Flyway**. Sobre una base de datos ya existente, Flyway hace
> *baseline* y adopta el esquema sin destruir datos; sobre una base de datos vacía,
> aplica la migración inicial `V1__init_schema.sql`.

---

## Pruebas

```bash
./mvnw test
```

La estrategia es **piramidal** y no depende de infraestructura externa (la suite usa
**H2 en memoria**):

- **Unitarios (JUnit 5 + Mockito):** lógica de los servicios (CRUD, reglas de
  negocio, excepciones) y del `JwtService` (emisión y validación de tokens).
- **Capa web (`@WebMvcTest` + MockMvc):** controladores aislados — códigos de estado,
  validación de entrada y autorización por roles con `@WithMockUser`.
- **Integración full-stack (`@SpringBootTest` + MockMvc + H2):** la cadena completa
  controlador → seguridad JWT → servicio → repositorio, con login real y verificación
  de 401/403/201 de extremo a extremo.
- **Testcontainers (MySQL):** un test de integración contra un **MySQL real** en
  Docker, que se **omite automáticamente** si Docker no está disponible (de modo que
  el build no falla en máquinas sin Docker y se ejecuta cuando Docker está presente).

Toda la suite se compila y ejecuta en local con `./mvnw test`; no requiere
infraestructura externa ni configuración adicional.

---

## Decisiones técnicas destacadas

- **Las entidades nunca se exponen**: el contrato público se define con **DTOs de
  respuesta inmutables** (`record`) y el mapeo entidad → DTO se delega en **MapStruct**
  (generado en tiempo de compilación, sin reflexión). Las asociaciones se proyectan
  como DTOs *resumen* para no arrastrar grafos completos.
- **Búsqueda dinámica con Specifications**: en lugar de multiplicar métodos de
  repositorio, los listados aceptan **filtros opcionales combinables** que se traducen
  a predicados de la Criteria API y se componen con `AND`.
- **Seguridad sin estado con JWT y roles**: autenticación por *access* + *refresh
  token*, usuarios en base de datos con **BCrypt** y autorización a nivel de método con
  `@PreAuthorize`. Los errores de seguridad se devuelven como `ProblemDetail` (401/403).
- **Errores como contrato**: un `@ControllerAdvice` centraliza la traducción de
  excepciones de dominio a respuestas **RFC 7807** (`ProblemDetail`), de forma que el
  cliente recibe siempre un formato de error coherente.
- **Esquema versionado con Flyway + `validate`**: la base de datos se gobierna con
  migraciones y Hibernate arranca en modo `validate`, garantizando que entidades y
  esquema no divergen; `baseline-on-migrate` permite adoptar Flyway sobre una BD
  existente sin riesgo.
- **Configuración externalizada por perfiles**: `dev` y `prod` separan el
  comportamiento, y **todos los secretos** (BD, JWT, admin) se inyectan por variables
  de entorno; el repositorio solo contiene *placeholders*.
- **Pirámide de tests realista**: unitarios rápidos con mocks, *slices* de web,
  integración full-stack sobre H2 y un test con **Testcontainers** que se autodesactiva
  sin Docker — cobertura amplia sin acoplar el build a infraestructura externa.
- **Listo para desplegar**: imagen **Docker multi-stage** (compila con JDK, ejecuta con
  JRE y usuario no *root*) y orquestación con **Docker Compose** (API + MySQL).

---

## Autor

**Andrés Ojeda Rodríguez**
[andresojedarodriguez@gmail.com](mailto:andresojedarodriguez@gmail.com)


---

## Licencia

Este proyecto está licenciado bajo la **PolyForm Noncommercial License 1.0.0**.
Puedes ver, ejecutar, estudiar y modificar el código con fines **no comerciales**
(estudio personal, educación, evaluación), pero **cualquier uso comercial requiere
permiso escrito del autor**. Consulta el archivo [LICENSE.md](LICENSE.md) para los
términos completos.

© 2026 Andrés Ojeda Rodríguez. Todos los derechos no concedidos expresamente quedan reservados.
