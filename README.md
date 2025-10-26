# SIGCLC — Sistema de Gestión de un Club de Lectura Comunitario

[![Java](https://img.shields.io/badge/Java-17-007396)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.4.11-6DB33F)](https://spring.io/projects/spring-boot)
[![MongoDB Atlas](https://img.shields.io/badge/MongoDB-Atlas-47A248)](https://www.mongodb.com/atlas/database)
[![License](https://img.shields.io/badge/license-internal-orange)]()

---

## 📌 Descripción

**SIGCLC** es un **backend** para gestionar un **club de lectura**. Incluye:

- Gestión de **usuarios** (roles: lector, moderador, administrador).
- Administración de **libros** (pendiente / en lectura / leído).
- **Reuniones** (presencial/virtual) con asistentes y adjuntos.
- **Reseñas** con calificación y comentarios de utilidad.
- **Retos** de lectura con progreso por participante.
- **Foros** y comentarios por publicación.
- **Cumplimiento estricto del MER/JSON** provisto (nombres, tipos y enums no se modifican).

> 🔒 En esta fase **no usamos Docker, CORS ni CI**. Trabajamos con **MongoDB Atlas** compartido.

---

## 🛠 Tecnologías

- Java 17  
- Spring Boot 3.4.11  
- Spring Web • Spring Data MongoDB • Validation • Lombok  
- (Opcional) springdoc-openapi para documentación (Swagger)

---

## ⚙ Requisitos

- JDK 17  
- Maven 3.9+  
- Git  
- Cuenta de **MongoDB Atlas** (o usar la URI compartida del equipo)  
- Postman o un cliente HTTP similar

---

## 🚀 Instalación (desarrollo local)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/<ORG_O_USUARIO>/sigclc-api.git
   cd sigclc-api
