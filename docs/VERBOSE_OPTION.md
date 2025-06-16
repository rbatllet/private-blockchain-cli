# Opción --verbose

## Descripción General

La opción `--verbose` proporciona información detallada durante la ejecución de los comandos, mostrando pasos intermedios, decisiones y procesos internos que normalmente están ocultos. Esta opción es útil para depuración, resolución de problemas y para entender mejor el funcionamiento interno de la aplicación.

## Implementación

### Características Principales

- **Opción Global**: Disponible para todos los comandos como una opción global
- **Opción Local**: También disponible como opción específica para ciertos comandos
- **Salida Detallada**: Muestra información detallada sobre procesos internos
- **Prefijo Visual**: Todas las salidas verbose están prefijadas con "📡 Verbose:" para fácil identificación

### Uso

```bash
# Uso como opción global
java -jar blockchain-cli.jar --verbose [comando] [opciones]

# Uso como opción específica de comando
java -jar blockchain-cli.jar add-block "Datos de prueba" --key-file keys/private.pem --verbose
```

### Información Mostrada

Cuando se utiliza la opción `--verbose`, se muestra información adicional como:

- Pasos de inicialización y configuración
- Detalles de carga y validación de archivos de clave
- Detección automática de formato de archivo
- Procesos de autorización de claves
- Tiempos de ejecución de operaciones
- Consultas SQL (si están habilitadas)
- Resultados de validaciones internas

## Testing

### Tests Unitarios

- **`AddBlockCommandVerboseTest.java`**: Test específico para la opción `--verbose` en el comando `add-block`
  - Verifica que la salida verbose se muestra correctamente
  - Comprueba que los mensajes verbose contienen la información esperada
  - Valida el comportamiento con diferentes combinaciones de opciones

### Tests Funcionales

Los tests funcionales para la opción `--verbose` están integrados en los scripts de prueba existentes:

- **`test_key_file_functionality.sh`**: Incluye pruebas con la opción `--verbose` habilitada

## Ejemplos de Uso

### Ejemplo Básico

```bash
java -jar blockchain-cli.jar --verbose status
```

Salida:
```
📡 Verbose: Inicializando conexión a la base de datos
📡 Verbose: Conexión establecida correctamente
📡 Verbose: Cargando configuración de la blockchain
📡 Verbose: Verificando estado de la cadena
Estado: Activa
Bloques: 42
Último bloque: 2025-06-14T15:30:22
```

### Ejemplo con --key-file

```bash
java -jar blockchain-cli.jar add-block "Test data" --key-file keys/private.pem --verbose
```

Salida:
```
📡 Verbose: Intentando cargar clave privada desde archivo: keys/private.pem
📡 Verbose: Formato de clave detectado: PEM PKCS#8
📡 Verbose: Clave RSA privada cargada correctamente
📡 Verbose: Clave pública derivada de clave privada
📡 Verbose: Auto-autorizando clave con nombre: KeyFile-private.pem-1686841234567
✅ Clave privada cargada correctamente desde archivo
✅ Bloque añadido correctamente!
```

## Integración con Otras Características

La opción `--verbose` se integra perfectamente con otras características:

- **Opción --key-file**: Muestra detalles sobre la carga y procesamiento de archivos de clave
- **Validación de Blockchain**: Proporciona información detallada sobre el proceso de validación
- **Operaciones de Rollback**: Muestra cada paso del proceso de rollback

## Recomendaciones de Uso

- Utilice `--verbose` durante la depuración de problemas
- Combine con redirección de salida para guardar logs detallados: `java -jar blockchain-cli.jar --verbose status > verbose_log.txt 2>&1`
- Para entornos de producción, utilice solo cuando sea necesario para evitar logs excesivos
