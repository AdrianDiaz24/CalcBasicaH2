## Explicacion de la calculadora basica con logs en H2

### Resumen

Este proyecto toma como base la calculadora desarrollada en el tema anterior con logs basados en archivos .txt. A partir de esta base, se eliminaron inicialmente todas las clases y funciones configuradas para esos logs, y se añadieron al archivo `build.gradle.kts` las dependencias necesarias para usar H2 y el pool de conexiones con Hikari. La carpeta `data` se dividió en otras dos carpetas: `dao` y `db`. En la primera, se creó la interfaz para los logs con DAO y la clase que gestiona los logs; en la segunda, se añadió el `DataObject`, que contiene la función para obtener un `DataSource`. Posteriormente, se creó la `data class` Operacion en el modelo, encargada de almacenar la información de las operaciones, tanto para obtener los registros como para subir nuevas operaciones. Finalmente, en la clase `Calculadora` de la carpeta `app`, se implementó la funcionalidad para subir las nuevas operaciones con cada cálculo realizado, además de modificar algunos aspectos del código que se explicarán más adelante.

### Explicación paso a paso

#### 1. Implementar las dependencias necesarias para usar H2 y Hikari. Esto se realiza de la siguiente forma:

````kts
    implementation("com.h2database:h2:2.2.224") // 2.2.224 hace referencia a la versión utilizada
    implementation("com.zaxxer:HikariCP:5.1.0") // 5.1.0 hace referencia a la versión utilizada
````

#### 2. Crear el `DataObject`, el cual será responsable de configurar el pool de conexiones y entregar el `DataSource` a las funciones que lo necesiten.

````kotlin
object DataObject {
    fun getDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:./logs/operaciones;AUTO_SERVER=TRUE"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
        }
        return HikariDataSource(config)
    }
}
````
Se utiliza un `object`, ya que es un patrón Singleton, una clase que no necesita ser instanciada para ser utilizada y que es accesible en todo el programa. De esta forma, cualquier clase de tipo DAO que gestione la información para añadir a la base de datos puede solicitar un `DataSource` en este objeto para usarlo.

#### 3. Crear la `data class` Operacion, que se encargará tanto de almacenar los datos que se subirán a la base de datos como de contener la información obtenida de esta para ser mostrada.

````kotlin
data class Operacion(val id: Int = 0, val num1: Double, val operador: Operadores?, val num2: Double, val resultado: Double) {
    override fun toString(): String {
        return "ID $id: $num1 ${operador?.simbolos?.getOrNull(0) ?: "?"} $num2 = $resultado"
    }
}
````
Como se observa, la `data class` contiene los datos que se almacenan: `num1`, `operador`, `num2` y `resultado`. Además, el campo `id: Int = 0` permite guardar el valor de la columna `id` en la base de datos, que es autoincremental. Esto permite que, al mostrar el contenido, las operaciones estén numeradas y no sea necesario especificar manualmente el ID al realizar una operación.

También se sobrescribe la función `toString` para que, al realizar un `print`, el contenido se muestre automáticamente en el formato deseado.

#### 4. Creacion de la interfaz para los logs de las operaciones

````kotlin
interface IOperacionDAO {

    fun getAll(): List<Operacion>

    fun getById(id: Int): Operacion?

    fun add(producto: Operacion)

    fun update(producto: Operacion, id: Int)

    fun delete(id: Int)

}
````
 En esta interfaz declaramos solo los métodos que se desarrollarán en la clase que herede de esta interfaz.

#### 5. Creacion de la clase para los logs de las operaciones

````kotlin
class OperacionDAO: IOperacionDAO
````

Como podemos ver, esta clase hereda de la interfaz anterior y es donde se desarrollan los métodos implementados en la misma. Voy a explicar principalmente las funciones `getAll()` y `add()`, que son las más utilizadas en este programa.

````kotlin
override fun getAll(): List<Operacion> {
    val operaciones = mutableListOf<Operacion>()
    Dataobject.getDataSource().connection.use { connection ->
        connection.prepareStatement("SELECT * FROM Operaciones").use { stmt ->
            val resultados = stmt.executeQuery()
            while (resultados.next()) {
                operaciones.add(
                    Operacion(
                        id = resultados.getInt("id"),
                        num1 = resultados.getDouble("num1"),
                        operador = Operadores.getOperador(resultados.getString("operador")),
                        num2 = resultados.getDouble("num2"),
                        resultado = resultados.getDouble("resultado")
                    )
                )
            }
        }
    }
    return operaciones
}
````

El método `getAll()` es responsable de devolver una lista que contiene todas las operaciones almacenadas en la base de datos. El funcionamiento es el siguiente:

1. Utiliza el objeto `Dataobject` para acceder al pool de conexiones. 
2. Llamando al método `getDataSource()` del `DataObject`, obtiene un `DataSource`.
3. Se prepara la consulta SQL `SELECT * FROM Operaciones` a través de un `PreparedStatement`. 
4. Después, se ejecuta el Statement con el método ``executeQuery()``, almacenando lo que devuelve en un `val` llamado resultado.
    - Funciona como si fuera un cursor, que ejecuta una consulta y te devuelve la informacion almacenada para poder ser usada en vez de mostralo solamente
5. Los resultados de la consulta se recorren con un bucle `while`, con el ``resultado.next()``, que pasa a la siguiente fila almacenada y devuelve un `true`, lo cual permite que continúe el bucle hasta que no queden más filas, devolviendo un `false`.
6. Por cada fila obtenida de los resultados:
    - Se crea una instancia de `Operacion` con sus campos (`id`, `num1`, `operador`, `num2`, `resultado`), utilizando los datos de las columnas de la tabla.
    - Esta instancia se agrega a una lista mutable (`operaciones`) que acumula todas las operaciones encontradas en la tabla.
7. Finalmente, se devuelve la lista `operaciones` con todas las instancias creadas.

````kotlin
override fun add(operacion: Operacion) {
        Dataobject.getDataSource().connection.use { connection ->
            connection.prepareStatement("INSERT INTO Operaciones(num1, operador, num2, resultado) VALUES (?, ?, ?, ?)").use { stmt ->
                stmt.setDouble(1, operacion.num1)
                stmt.setString(2, operacion.operador?.simbolos?.firstOrNull())
                stmt.setDouble(3, operacion.num2)
                stmt.setDouble(4, operacion.resultado)
                stmt.executeUpdate()
            }
        }
    }
````

El método `add()` cumple con la funcionalidad de insertar una nueva operación en la base de datos basada en la información guardada en la data class `Operacion`. El funcionamiento es el siguiente: 

1. Obtiene una conexión al pool de conexiones usando el `Dataobject` y su método `getDataSource()`.
2. Crea un `PreparedStatement` configurado con la consulta SQL `INSERT INTO Operaciones(num1, operador, num2, resultado)`.
3. Asigna valores a los parámetros de la consulta usando los métodos del `PreparedStatement` (`setDouble`, `setString`).
    - El campo `num1` se asigna a la posición `1` con el valor de `operacion.num1`.
    - El campo `operador` se asigna a la posición `2` obteniendo el primer símbolo de la lista de símbolos de `operacion.operador`.
    - El campo `num2` se asigna a la posición `3` con el valor de `operacion.num2`.
    - El campo `resultado` se asigna a la posición `4` con el valor de `operacion.resultado`.
4. Ejecuta la consulta con el método `executeUpdate()` para insertar los datos en la base de datos.


#### 6. Refactorizamos la clase `Calculadora` para que deje de usar la logica para logs basado en archivos y lo haga usando la base de datos

````kotlin
fun iniciar() {
        do {
            try {
                ui.mostrar("--- Antiguas operaciones ---")
                operacionDAO.getAll().forEach { println(it) }
                ui.limpiarPantalla(2)
                ui.mostrar("--- CALCULADORA ---")
                val (numero1, operador, numero2) = pedirInfo()
                val resultado = realizarCalculo(numero1, operador, numero2)
                ui.mostrar("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
                operacionDAO.add(Operacion(num1 = numero1, operador = operador, num2 = numero2, resultado = resultado.redondear(2)))
            } catch (e: NumberFormatException) {
                ui.mostrarError(e.message ?: "Se ha producido un error!")
            }
        } while (ui.preguntar())
        ui.limpiarPantalla()
    }
````

El método `iniciar()` es el que comienza el bucle del funcionamiento hasta que se desee salir:

1. **Inicio del bucle de la calculadora:**
   - El método utiliza un bucle `do-while` que permite al usuario realizar varias operaciones mientras no desee salir.

2. **Mostrar las antiguas operaciones:**
   - Antes de iniciar un nuevo cálculo, el sistema muestra por consola todas las operaciones previamente almacenadas en la base de datos.
   - Para esto, llama al método `getAll()` de la clase `OperacionDAO`, recorriendo las operaciones y mostrándolas en consola.

3. **Interfaz del usuario:**
   - Muestra un encabezado indicando el inicio de la calculadora con el método `ui.mostrar()`, y limpia la pantalla entre pasos usando el método `ui.limpiarPantalla()`.

4. **Obtención de datos para la operación:**
   - Llama al método `pedirInfo()`, que devuelve los operandos y el operador ingresados por el usuario. Este valor se desestructura en las variables: `numero1`, `operador`, y `numero2`.

5. **Realización del cálculo:**
   - Utiliza el método `realizarCalculo()` pasando como parámetros los datos ingresados.
   - El resultado obtenido del cálculo es mostrado en pantalla junto con la operación realizada, en un formato claro y legible.

6. **Almacenamiento de la operación:**
   - Crea una nueva instancia de la clase `Operacion` con los operandos, el operador, y el resultado (redondeado a dos decimales usando el método `redondear()`).
   - La nueva operación se almacena en la base de datos utilizando el método `add()` de `OperacionDAO`.
7. **Control de errores:**
   - El método tiene `try-catch` para manejar excepciones del tipo `NumberFormatException`. Si ocurre un error, se muestra un mensaje claro al usuario.

8. **Continuar o salir:**
   - Al final de cada iteración, usa el método `ui.preguntar()` para consultar al usuario si desea realizar otra operación. El bucle continuará mientras la respuesta sea afirmativa.
   - Antes de finalizar el método, se limpia la pantalla con el método `ui.limpiarPantalla()`.

Este método se encarga de gestionar todo el flujo operativo de la calculadora, integrando las funcionalidades de la lógica principal, la interacción con el usuario y la persistencia de datos en la base de datos.

Tambien se refactoriza el metodo `pedirNumero()` para que sea mas facil entenderlo y se refactoriza levemente el metodo `pedirInfo()`

### Posibles mejoras a implementar

1. Implementar un menú para, además de realizar las operaciones, poder mostrar el registro completo, buscar solo por ID, borrar por ID y borrar todo el historial.
2. Añadir un metodo ``getAllWithLimit()`` para usar cuando vayamos a calcular una operacion salgan antes solo las ultimas 3-5 operaciones realizadas, ya que sino cuando llevemos 50 sera una lista enorme, la idea es que la consulta sea ``SELECT * FROM Operaciones ORDER ID ASC LIMIT 5``
3. Cuando la base de datos esté llena de operaciones, tal vez se quiera borrar el historial. Para ello, se podría crear un método ``inicializarBD()``, que use un ``DROP TABLE IF EXISTS`` y un ``CREATE TABLE`` para eliminar y crear la misma tabla, pero vacía.
