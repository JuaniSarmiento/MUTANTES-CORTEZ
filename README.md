# DNA Mutant Analyzer

Sistema avanzado de análisis genético para identificación de mutantes basado en patrones de ADN. Proyecto desarrollado como parte del desafío técnico de MercadoLibre.

## 🧬 Descripción del Proyecto

Este sistema permite detectar mutantes analizando secuencias de ADN. Un individuo se clasifica como mutante cuando su secuencia contiene **más de una cadena** de cuatro bases nitrogenadas idénticas (A, T, C, G) en cualquier dirección: horizontal, vertical o diagonal.

### Ejemplos de Análisis

**Caso 1: Mutante Detectado (Diagonal + Horizontal)**
```
ATGCGA
CAGTGC
TTATGT
AGAAGG
CCCCTA
TCACTG
```
Resultado: 2 secuencias encontradas (diagonal AAAA + horizontal CCCC)

**Caso 2: Humano Normal**
```
ATGCGA
CAGTGC
TTATTT
AGACGG
GCGTCA
TCACTG
```
Resultado: 1 secuencia encontrada - No califica como mutante

## 🛠️ Stack Tecnológico

- **Java 17 LTS**
- **Spring Boot 3.2.0** (Framework principal)
- **Spring Data JPA** + Hibernate ORM
- **H2 Database 2.x** (almacenamiento en disco)
- **Lombok 1.18.x** (reducción de código boilerplate)
- **SpringDoc OpenAPI 3** (documentación interactiva)
- **JUnit 5 + Mockito** (suite de testing)
- **Jacoco** (análisis de cobertura)

## 🏛️ Arquitectura del Sistema

Diseño por capas con clara separación de responsabilidades:

```
├── domain/              # Capa de dominio
│   ├── detector/       # Lógica central de detección
│   ├── entity/         # Modelos de datos
│   └── repository/     # Acceso a datos
├── application/         # Capa de aplicación
│   ├── dto/            # Objetos de transferencia
│   ├── service/        # Servicios de negocio
│   └── validation/     # Validadores personalizados
└── infrastructure/      # Capa de infraestructura
    ├── controller/     # APIs REST
    └── exception/      # Manejo global de errores
```

## ⚡ Características de Rendimiento

### 1. Motor de Análisis Optimizado

- **Arrays nativos**: Uso de `char[][]` para máxima velocidad de acceso
- **Terminación anticipada**: Finaliza al detectar más de una secuencia
- **Expansión de bucles**: Verificación directa sin iteraciones internas
- **Complejidad temporal**: O(N²) en peor caso, O(N) promedio para mutantes

### 2. Gestión de Datos Eficiente

- **Identificación única**: Hash SHA-256 como clave primaria
- **Evitar duplicados**: Sistema de caché automático
- **Consultas rápidas**: Índices optimizados para estadísticas O(1)

### 3. Validación Robusta

- Anotación `@ValidDna` personalizada
- Verificación de matriz cuadrada NxN
- Solo bases válidas: A, T, C, G

## 🔌 API Endpoints

### POST /mutant

Evalúa una secuencia de ADN y determina su clasificación.

**Solicitud:**
```json
{
  "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

**Respuestas:**
- `200 OK` - Mutante detectado
- `403 FORBIDDEN` - Humano normal
- `400 BAD REQUEST` - Formato inválido

### GET /stats

Obtiene métricas de análisis realizados.

**Respuesta:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

## 🛠️ Configuración y Ejecución

### Prerequisitos

- Java Development Kit (JDK) 17+
- Maven Wrapper (incluido en el proyecto)

### Inicio del Proyecto

**En Windows:**
```powershell
# Ejecutar pruebas
.\mvnw clean test

# Iniciar aplicación
.\mvnw spring-boot:run

# Acceso:
# - Documentación API: http://localhost:8080/swagger-ui.html
# - Consola H2: http://localhost:8080/h2-console
```

**En Linux/Mac:**
```bash
# Ejecutar pruebas
./mvnw clean test

# Iniciar aplicación
./mvnw spring-boot:run
```

**Scripts de inicio rápido (Windows):**
```powershell
.\start.ps1   # PowerShell
.\start.bat   # CMD
```

### Tareas de VS Code

El proyecto incluye tareas configuradas. Abre Command Palette (`Ctrl+Shift+P`) y busca:

- **Run App** - Inicia la aplicación en modo background
- **Run Tests** - Ejecuta la suite de tests
- **Clean Install** - Build completo con tests
- **Generate Coverage Report** - Genera reporte Jacoco
- **Open Swagger UI** - Abre Swagger en el navegador
- **Open H2 Console** - Abre consola H2 en el navegador

### Suite de Pruebas

```powershell
# Ejecutar todas las pruebas
.\mvnw test

# Generar reporte de cobertura (Jacoco)
.\mvnw clean install
.\mvnw jacoco:report

# Reporte disponible en: target\site\jacoco\index.html
```

## 📊 Cobertura de Pruebas

Suite completa de tests unitarios e integración con cobertura superior al 80%:

- **MutantDetectorTest**: 20+ casos del motor de detección
- **MutantServiceTest**: Pruebas de persistencia y caché
- **StatsServiceTest**: Validación de cálculos estadísticos
- **MutantControllerTest**: Tests de endpoints REST
- **DnaValidatorTest**: Validación de formatos de entrada

## 📖 Documentación API

La documentación interactiva está disponible con Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## 🗄️ Almacenamiento de Datos

### Configuración H2

Base de datos embebida con persistencia en disco:

- **Ubicación**: `~/mutant_dna_records.mv.db`
- **Modo**: Persistente con servidor TCP habilitado
- **DDL**: Auto-actualización del esquema

### Acceso a la Consola

**Consola Web H2:**
```
URL: http://localhost:8080/h2-console

Conexión:
  JDBC URL: jdbc:h2:file:~/mutant_dna_records
  Usuario: sa
  Contraseña: (vacío)
```

**Cliente Externo:**
Puedes conectarte con DBeaver, IntelliJ u otra herramienta:
```
JDBC URL: jdbc:h2:tcp://localhost/~/mutant_dna_records
Driver: org.h2.Driver
Usuario: sa
```

### Esquema

```sql
CREATE TABLE dna_records (
    dna_hash VARCHAR(64) PRIMARY KEY,
    is_mutant BOOLEAN NOT NULL,
    sequence_size INTEGER NOT NULL,
    analyzed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_is_mutant ON dna_records(is_mutant);
```

## 💁 Ejemplos de Uso

### Analizar ADN

```powershell
curl -X POST http://localhost:8080/mutant `
  -H "Content-Type: application/json" `
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

### Consultar Estadísticas

```powershell
curl http://localhost:8080/stats
```

## 🔍 Detalles del Algoritmo

El algoritmo implementa las siguientes optimizaciones críticas:

1. **Búsqueda direccional**: Solo explora 4 direcciones (→, ↓, ↘, ↙) en lugar de 8
2. **Validación temprana**: Verifica espacios disponibles antes de buscar
3. **Loop unrolling**: Compara las 4 posiciones en una sola expresión booleana
4. **No recursión**: Evita overhead del stack

```java
// Ejemplo de verificación optimizada
private boolean checkSequence(char[][] matrix, int row, int col, 
                               int rowDelta, int colDelta, char expected) {
    return matrix[row + rowDelta][col + colDelta] == expected &&
           matrix[row + 2*rowDelta][col + 2*colDelta] == expected &&
           matrix[row + 3*rowDelta][col + 3*colDelta] == expected;
}
```

## ✅ Cumplimiento de Requisitos

✔️ **Algoritmo Optimizado**: Arrays nativos, terminación anticipada, O(N²)
✔️ **Persistencia Robusta**: Hash SHA-256, sin duplicación de análisis
✔️ **APIs RESTful**: POST /mutant (200/403), GET /stats
✔️ **Validación Completa**: Matrices NxN, bases válidas A/T/C/G
✔️ **Testing Exhaustivo**: Cobertura >80% con JUnit 5 + Mockito
✔️ **Documentación**: OpenAPI/Swagger totalmente integrado
✔️ **Arquitectura Limpia**: Capas bien definidas y mantenibles

## 👨‍💻 Desarrollador

Proyecto realizado para el desafío técnico de MercadoLibre

---

**DNA Mutant Analyzer** - Sistema de Análisis Genético © 2025
