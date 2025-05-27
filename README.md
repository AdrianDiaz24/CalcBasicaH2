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
   var salida = false
   do {
      try {
         ui.mostrarMenu(arrayOf("--- MENU ---", "Realizar operacion", "Mostrar operaciones anteriores", "Eliminar operacion del historial", "Borrar historial", "Salir"))
         val eleccionMenu = ui.pedirNumeroConLimites(1, 5)
         when (eleccionMenu) {
            1 -> {
               ui.mostrar("--- Antiguas operaciones ---")
               operacionService.getAllWithLimit().forEach { ui.mostrar(it.toString()) }
               ui.limpiarPantalla(2)
               ui.mostrar("--- CALCULADORA ---")
               val (numero1, operador, numero2) = ui.pedirInfo()
               val resultado = realizarCalculo(numero1, operador, numero2)
               ui.mostrar("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
               operacionService.add(Operacion(num1 = numero1, operador = operador, num2 = numero2, resultado = resultado.redondear(2)))
            }
            2 -> {
               ui.mostrar("--- OPERACIONES ANTERIORES ---")
               operacionService.getAll().forEach { ui.mostrar(it.toString()) }
            }
            3 -> {
               ui.mostrar("--- ELIMINAR OPERACION ---")
               ui.mostrar("Introduzca la ID de la operacion a eliminar:")
               val id = ui.pedirNumeroConLimites(1, operacionService.getAll().size)
               operacionService.delete(id)
            }
            4 -> {
               operacionService.inicializarTabla()
               ui.mostrar("--- Historial Borrado ---\n")
            }
            5 -> salida = true
         }

      } catch (e: NumberFormatException) {
         ui.mostrarError(e.message ?: "Se ha producido un error!")
      }
   } while (!salida)
   ui.limpiarPantalla()
}
````

El método `iniciar()` es el que comienza el bucle del funcionamiento hasta que se desee salir:

1. **Inicio del bucle de la calculadora:**
   - El método utiliza un bucle `do-while` que permite al usuario realizar varias operaciones mientras no desee salir.

2. **Mostrar el menú:**
   - Se llama al método ``mostrarMenu()`` de la clase ``Consola()`` que recibe un ``Array<String>`` con cada línea del menú y la muestra cada una debajo de la anterior.

3. **Elección de la opción**
   - Se llama al método `pedirNumeroConLimites()` de la clase ``Consola()``, al cual se le pasa el valor mínimo posible y el máximo posible, y devuelve el número elegido por el usuario.


4. **Posibles elecciones y su explicación**
   1. Realizar operación: 
      1. Se llama al método ``mostrar()`` de la ``Consola()`` que muestra un mensaje informando que se te van a mostrar las anteriores operaciones.
      2. Se llama al método ``getAllWithLimit()`` del `OperacionService`, que te devuelve un ``List`` con las últimas 5 operaciones, y las muestra por pantalla.
      3. Desde la ``Consola()`` se llama al método ``limpiarPantalla()``, que de forma predeterminada muestra en pantalla 20 saltos de línea (en este caso son 2), y se muestra a continuación con el método ``mostrar()`` un mensaje que informa que empieza la calculadora.
      4. Con el método ``pedirInfo()`` te piden que introduzcas tanto el num1 como el num2 y el operador, y te lo devuelve en un ``Triple<Double, Operador, Double>``. 

      5. Ahora se llama a la función ``realizarCalculo()``, que esta sí se encuentra en la propia clase de la ``Calculadora()``, al que se le pasan los valores obtenidos anteriormente, y con un ``when()`` y el operador se realiza una operación u otra, siendo devuelto el resultado.
      6. Se vuelve a llamar al ``mostrar()``, que te muestra por pantalla el ``num1`` seguido del 1.º símbolo del ``Operador()``, el ``num2`` y el ``resultado``, siendo este último redondeado con una función de extensión del ``Double`` hecha a nivel superior.
      7. Por último, desde ``operacionService()`` se llama al método ``add()``, al que se le da una data class ``Operacion()`` con los datos que se han mostrado por pantalla, y se suben a la base de datos.

   2. Mostrar operaciones anteriores
      1. Se muestra con el método ``mostrar()`` un mensaje informando que se van a mostrar todas las operaciones realizadas. 
      2. Desde la clase ``OperacionService()`` se llama al método ``getAll()``, que devuelve una ``List<Operacion>``, y muestra por pantalla todas ellas.
   3. Eliminar una operación de la base de datos
      1. Se muestra en pantalla un mensaje que informa que esta parte es para borrar una operación y otro para pedirle al usuario que ingrese una ID.
      2. Desde la ``Consola()`` se vuelve a llamar al ``pedirNumeroConLimites()``, con el 1 como mínimo posible y el máximo se calcula con el ``.size`` de la lista que te devuelve el ``getAll()`` del ``OperacionService()``.
      3. Desde ``OperacionService()``, se llama al método ``delete()``, y se le pasa la ID, eliminando de la base de datos esa operación.
   4.  Borrar todo el historial de operaciones
         - Esta función con lo de borrar historial lo que hace en realidad es borrar la tabla y volver a crearla
       1. Llama al método ``inicializarTabla()`` de ``OperacionService()``, el cual borra y crea nuevamente la tabla.
       2. Si la operación anterior no ha dado problemas, se muestra en pantalla un mensaje informando que se ha borrado el historial correctamente.

Este método se encarga de gestionar todo el flujo operativo de la calculadora, integrando las funcionalidades de la lógica principal, la interacción con el usuario y la persistencia de datos en la base de datos.

Tambien se refactoriza el metodo `pedirNumero()` para que sea mas facil entenderlo y se refactoriza levemente el metodo `pedirInfo()`

### Posibles mejoras a implementar

1. Implementar un menú para, además de realizar las operaciones, poder mostrar el registro completo, buscar solo por ID, borrar por ID y borrar todo el historial.
2. Añadir un metodo ``getAllWithLimit()`` para usar cuando vayamos a calcular una operacion salgan antes solo las ultimas 3-5 operaciones realizadas, ya que sino cuando llevemos 50 sera una lista enorme, la idea es que la consulta sea ``SELECT * FROM Operaciones ORDER ID DESC LIMIT 5``
3. Cuando la base de datos esté llena de operaciones, tal vez se quiera borrar el historial. Para ello, se podría crear un método ``inicializarBD()``, que use un ``DROP TABLE IF EXISTS`` y un ``CREATE TABLE`` para eliminar y crear la misma tabla, pero vacía.
