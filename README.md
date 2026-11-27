# TAREA 2 - Acceso a Datos (2º DAM)

Aplicación de consola en Java para la gestión de una clínica. Los datos se guardan en una base de datos MySQL mediante JDBC, salvo las alergias y los medicamentos, que se guardan en ficheros binarios.

El script `src/main/resources/clinica_alvaroblanco.sql` crea la base de datos y también el usuario `clinica`, que es con el que se conecta la aplicación.

## Usuarios de prueba

| Usuario | Contraseña | Perfil |
|---|---|---|
| admin | admin | Administrador |
| camila | cam1las | Secretariado |
| patrigc | patri | Enfermería |
| alberto | alberto | Cirugía |

## Arquitectura

El código está dividido en capas:

- `modelo`: entidades del sistema.
- `dao`: acceso a datos (base de datos con JDBC y ficheros binarios). Incluye la conexión única a la base de datos (patrón Singleton) y una excepción propia, `AccesoDatosException`.
- `servicio`: lógica de los casos de uso, validaciones, sesión y permisos.
- `vista`: menús y entrada y salida de datos por consola.

Añadí una clase llamada `Color.java` para personalizar los colores de la consola. No aporta mucho, pero así es más vistoso.

Las excepciones de acceso a datos se tratan con una excepción propia, `AccesoDatosException.java`. Los DAO capturan la `SQLException` (o la `IOException` en el caso de las alergias) y lanzan esta excepción, guardando la original como causa. Así la capa de servicio no depende de JDBC ni sabe si los datos vienen de la base de datos o de ficheros, y solo la vista muestra mensajes por pantalla.

## Casos de uso implementados

- CU1: Login/Logout y mantenimiento de sesión.
- CU2: Alta de empleados (con transacción).
- CU3: Gestión de alergias y medicamentos en ficheros binarios.
- CU4A: Crear y modificar pacientes, con su historial y alergias.
- CU4B: Buscar pacientes por nombre, teléfono, email y/o NIF/NIE.
- CU6: Ver la ficha del cirujano.

## Mejoras previstas

- Los comentarios y el Javadoc los añadiré en la siguiente parte del proyecto.
- Hay ciertas cosas en la base de datos que cambiaré en la siguiente parte del proyecto, como separar en campos distintos el nombre y los apellidos.
- Unificar las validaciones repetidas (teléfono, email, NIF/NIE) en una clase común.
