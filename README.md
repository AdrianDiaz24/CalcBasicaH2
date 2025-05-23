## Explicacion de la calculadora basica con logs en H2

### Resumen

Este proyecto ha tomado como base la calculadora que se hizo en el tema anterior con logs basados en archivos .txt. Una vez con la base pasada, he eliminado a priori todas las clases y funciones configuradas para esos logs, y he añadido al build.gradle.kts las dependencias necesarias para usar H2 y el pool de conexiones con Hikari. Se ha dividido la carpeta data con otras 2 carpetas: dao y db. En la primera se ha realizado la interfaz para los logs con DAO y la clase que gestiona los logs, y en la última se ha añadido el DataObject que contiene la función para conseguir un DataSource. A continuación, se ha creado la DataClass Operaciones en el model, que será quien almacene la información de las mismas, tanto para cuando se obtenga el registro como para subir nuevas operaciones. Por último, se ha modificado en app la clase Calculadora para implementar que se suban las nuevas operaciones cada vez que se haga una nueva, además de cambiar algunas cosas de código que se explicarán más adelante.

### Explicacion paso a paso

1. Implementar las dependencias necesarias para utilizar H2 y Hikari. Esto se hace de la siguiente forma:

````kts
    implementation("com.h2database:h2:2.2.224") // 2.2.224 hace referencia a la version utilizada
    implementation("com.zaxxer:HikariCP:5.1.0") // 5.1.0 hace referencia a la version utilizada
````

2. Crear el DataObject que se encargará de crear el pool de conexiones y entregar los DataSource a las funciones que lo necesiten.

````kotlin
object Dataobject {
    fun getDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:./Logs/Operaciones;AUTO_SERVER=TRUE"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
        }
        return HikariDataSource(config)
    }
}
````
Se usa un Object ya que es un patrón Singleton, una clase que no necesita ser instanciada para usarla y es accesible por todo el programa de esta forma cualquier clase de tipo DAO que se encarge de la gestionar la informacion que se pretenda añadir a la base de datos puede pedir ahi una DAtaSource para usarla.

3. Crear la data class Operacion que se encargara tanto de almacenar los datos que se subiran a la base de datos como lo que se recogen de esta para ser mostrados

````kotlin
data class Operacion(val id: Int = 0, val num1: Double, val operador: Operadores?, val num2: Double, val resultado: Double) {

    override fun toString(): String {
        return "ID $id: $num1 ${operador?.simbolos?.getOrNull(0) ?: "?"} $num2 = $resultado"
    }
}
````
Como se puede ver la data class contiene los datos que se almacenan num1, operador, num2 y resultado, pero tambien `ìd: Int = 0` sirve para guardar el valor que tiene la columna `id` en la base de datos que es autoincremental de forma que cuando se muestre todo este enumerado pero que cuando yo realize la operacion no me haga falta especifircar la ID manualmente

Tambien se sobrescribe la funcion ``toString`` para cada vez que haga un print salga ya con el formato deseado

4. Creacion de la interfaz para los logs de las operaciones

