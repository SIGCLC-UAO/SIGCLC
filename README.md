SIGCLC — Sistema de Gestión de un Club de Lectura Comunitario








📌 Descripción

SIGCLC es un backend para gestionar un club de lectura. Permite:

Gestión de usuarios (roles: lector, moderador, administrador).

Registro y administración de libros (pendiente / en lectura / leído).

Reuniones (presencial/virtual) con asistentes y adjuntos.

Reseñas con calificación y comentarios de utilidad.

Retos de lectura con progreso por participante.

Foros y comentarios por publicación.

Cumplimiento estricto del MER/JSON provisto (nombres, tipos y enums no se modifican).

🔒 En esta fase no usamos Docker, CORS ni CI. Trabajamos con MongoDB Atlas compartido.

🛠 Tecnologías

Java 17

Spring Boot 3.4.11

Spring Web • Spring Data MongoDB • Validation • Lombok

(Opcional) springdoc-openapi para documentación (Swagger)

⚙ Requisitos

JDK 17

Maven 3.9+

Git

Cuenta de MongoDB Atlas (o usar la URI compartida del equipo)

Postman o un cliente HTTP similar

🚀 Instalación (desarrollo local)

Clonar el repositorio

git clone https://github.com/<ORG_O_USUARIO>/sigclc-api.git
cd sigclc-api


Configurar la conexión a Atlas

Copia application-example.yml a src/main/resources/application.yml.

Reemplaza la URI con la que te compartió el líder (no subas contraseñas al repo).

spring:
  application:
    name: sigclc-api
  data:
    mongodb:
      uri: mongodb+srv://<USER>:<PASSWORD>@<CLUSTER>.mongodb.net/SIGCLC?retryWrites=true&w=majority
server:
  port: 8080


Compilar y ejecutar

mvn spring-boot:run


Por defecto la API atenderá en http://localhost:8080.

Probar un módulo base (ejemplo)

Listar libros: GET http://localhost:8080/api/libros

Crear libro (JSON válido según MER):

{
  "titulo": "Libro de ejemplo",
  "autor": "Autora Demo",
  "anioPublicacion": 2020,
  "estado": "pendiente",
  "sinopsis": "Texto de sinopsis",
  "genero": "Novela"
}


❗ Si Atlas da error de validación, revisa que el JSON cumpla exactamente el $jsonSchema (campos requeridos y enums).

🗄️ Base de Datos (Atlas) — uso compartido

Organización/Proyecto en Atlas con Cluster M0 (Free).

Usuario de BD de desarrollo (p. ej. sigclc_dev con readWriteAnyDatabase).

Network Access: para desarrollo puede usarse 0.0.0.0/0 (quitar al finalizar el curso).

Base: SIGCLC.

Colecciones con validación JSON Schema:
Usuarios, Libros, Reuniones, Resenias, Retos, ComentarioPost, Foros.

El MER/JSON es inmutable: los nombres, tipos y enums deben respetarse tal cual.

📁 Estructura del Proyecto
sigclc-api/
├── src/main/java/com/sigclc/backend/
│   ├── controller/        # Entrada de la API (endpoints REST)
│   ├── service/           # Reglas de negocio
│   ├── repository/        # Acceso a MongoDB (Spring Data)
│   ├── dto/               # Objetos de transferencia (si aplica)
│   ├── mapper/            # Conversión entidad<->dto (si aplica)
│   ├── model/             # Modelos @Document por colección
│   ├── config/            # Configuración de la app
│   └── exception/         # Manejo de errores (centralizado)
├── src/main/resources/
│   ├── application-example.yml   # plantilla (no subir secretos)
│   └── application.yml           # local (ignorado por .gitignore)
├── pom.xml
└── README.md

🏗 Arquitectura de SIGCLC

Patrón Controller → Service → Repository:

Controller: define los endpoints REST (rutas, entradas/salidas).

Service: concentra la lógica del negocio y validaciones adicionales.

Repository: operaciones con MongoDB (Spring Data).

Modelos: una clase por colección con @Document(collection = "...").

Validación: anotaciones de Bean Validation (ej.: @NotNull, rangos), siempre alineadas al MER.

Recomendación: comenzar por Libros como ejemplo y luego replicar el patrón en el resto de módulos.

🌿 Flujo de Trabajo con Git

Ramas principales:

main: estable (releases / entregas).

develop: integración.

Ramas por funcionalidad:

Formato: feature/<modulo>-<iniciales>

Ejemplos: feature/libros-jm, feature/resenias-nc

Pull Requests siempre hacia develop con revisión de un compañero.

No subir credenciales (usa application-example.yml y tu application.yml local).

Convenciones de commits (Conventional Commits)

feat: nueva funcionalidad

fix: corrección

docs: documentación

test: pruebas

refactor: mejoras internas

Ejemplos:

feat: crear CRUD de Libros

fix: corregir validación de estado en Libros

docs: actualizar instrucciones de conexión a Atlas

📐 Convenciones de Desarrollo

Respetar el MER/JSON: nombres, tipos y enums exactos (no cambiar).

Validar entradas: usa Bean Validation acorde al esquema.

Respuestas claras ante errores (mensaje y causa).

Ejemplos de Postman por módulo (colecciones compartidas).

Código y comentarios en español; nombres de clases y paquetes en inglés/español consistente.

🔎 (Opcional) Documentación automática

Si deseas Swagger (recomendado), agrega al pom.xml:

<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.5.0</version>
</dependency>


Luego abre: http://localhost:8080/swagger-ui.html

🧪 Comprobación rápida

App corre en http://localhost:8080

GET /api/libros responde (vacío o con datos de prueba)

Un POST válido crea un libro según el MER

No hay secretos comprometidos en el repo

📄 Licencia

Uso interno del equipo SIGCLC para fines académicos.

Anexos (referencia rápida del MER)

Usuarios.rol → lector | moderador | administrador

Libros.estado → pendiente | en lectura | leído

En algunas rutas del esquema se usan ObjectId y en otras string: respétalo tal cual.

Adjuntos se guardan como extensiones (ej.: .pdf, .png, etc.), no como archivos.
