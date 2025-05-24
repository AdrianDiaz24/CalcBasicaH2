## Explicacion de la calculadora basica con logs en H2

### Resumen

Este proyecto toma como base la calculadora desarrollada en el tema anterior con logs basados en archivos .txt. A partir de esta base, se han eliminado inicialmente todas las clases y funciones configuradas para esos logs, y se han añadido al archivo build.gradle.kts las dependencias necesarias para usar H2 y el pool de conexiones con Hikari. La carpeta `data` se ha dividido en otras dos carpetas: `dao` y `db`. En la primera, se ha creado la interfaz para los logs con DAO y la clase que gestiona los logs; en la segunda, se ha añadido el `DataObject`, que contiene la función para obtener un `DataSource`. Posteriormente, se ha creado la `data class` Operacion en el modelo, que será la encargada de almacenar la información de las operaciones, tanto para obtener los registros como para subir nuevas operaciones. Finalmente, en la clase `Calculadora` de la carpeta `app`, se ha implementado la funcionalidad para subir las nuevas operaciones con cada cálculo realizado, además de modificar algunos aspectos del código que se explicarán más adelante.

### Explicación paso a paso

1. Implementar las dependencias necesarias para usar H2 y Hikari. Esto se realiza de la siguiente forma:

````kts
    implementation("com.h2database:h2:2.2.224") // 2.2.224 hace referencia a la version utilizada
    implementation("com.zaxxer:HikariCP:5.1.0") // 5.1.0 hace referencia a la version utilizada
````

2. Crear el `DataObject`, el cual será responsable de configurar el pool de conexiones y entregar el `DataSource` a las funciones que lo necesiten.

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
Se utiliza un `object` ya que es un patrón Singleton, una clase que no necesita ser instanciada para ser utilizada y que es accesible por todo el programa. De esta forma, cualquier clase de tipo DAO que gestione la información a añadir en la base de datos puede solicitar un `DataSource` en este objeto para usarlo.

3. Crear la `data class` Operacion, que se encargará tanto de almacenar los datos que se subirán a la base de datos como de contener la información obtenida de esta para ser mostrada.

````kotlin
data class Operacion(val id: Int = 0, val num1: Double, val operador: Operadores?, val num2: Double, val resultado: Double) {
    override fun toString(): String {
        return "ID $id: $num1 ${operador?.simbolos?.getOrNull(0) ?: "?"} $num2 = $resultado"
    }
}
````
Como se observa, la `data class` contiene los datos que se almacenan: `num1`, `operador`, `num2` y `resultado`. Además, el campo `id: Int = 0` permite guardar el valor de la columna `id` en la base de datos, que es autoincremental, logrando así que, al mostrar el contenido, las operaciones estén numeradas y no sea necesario especificar manualmente el ID al realizar una operación.

También se sobrescribe la función `toString` para que, al realizar un `print`, el contenido se muestre automáticamente en el formato deseado.

4. Creacion de la interfaz para los logs de las operaciones

