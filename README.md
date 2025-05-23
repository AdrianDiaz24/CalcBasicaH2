## Explicacion de la calculadora basica con logs en H2

### Resumen

Este proyecto ha tomado como base la calculadora que se hizo en el tema anterior con logs basados en archivos .txt, una vez con la base pasada he eliminado a priori todas la clases y funciones configuradas para esos logs y he añadido al build.gradle.kts las dependecias necesarias para usar h2 y el pool de conexiones con hikari, se ha dividio la carpeta data con otras 2 carpetas dao y db, en la primera se ha realizado la interfaz para los logs con DAO y la clase que gestiona los logs y en la ultima se ha añadido el Dataobject que contiene la funcion para conseguir un DataSource, a continuacion se ha creado la DataClass Operaciones en el model que sera quien almacene la informacion de las misma para cuando se obtenga el registro como para subir nuevas operaciones, y por ultimo se ha modificado en app la Clase Calculadora para implementar que se suban las nuevas operaciones cada vez que se haga una nueva, ademas de cambiar algunas cosas de coldigo que se explicara mas adelanteb

### Explicacion paso a paso

1. Implementar las dependencias necesarias para utilizar H2 y Hikari, esto se hace de la siguiente forma

````kts
    implementation("com.h2database:h2:2.2.224") // 2.2.224 hace referencia a la version utilizada
    implementation("com.zaxxer:HikariCP:5.1.0") // 5.1.0 hace referencia a la version utilizada
````

2. Crear el DataObject que se encargara de crear el Pool de conexiones y entregar los DataSource a la funciones que la necesiten