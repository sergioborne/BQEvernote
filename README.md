# BQEvernote
Evernote Sync test projecto for BQ


For testing, is needed to add CONSUMER_KEY and CONSUMER_SECRET in MyApplication class

We have replaced FindNotesTask with FindNotesService to be able to get FindNotes result when device configuration changes during request.


### Requisitos

Desarrollar una aplicación Android que sea cliente de Evernote cumpliendo los siguientes puntos:

**1. Exista una pantalla inicial de login, donde el usuario pueda introducir sus credenciales para tener acceso a su cuenta Evernote. (https://evernote.com/)**

Uso de la API de Evernote para Android que incluye una pantalla de login para autenticarte. Comprobación del login para poder usar la aplicacióm.

**2. Una vez introducidos los credenciales, se mostrarán en pantalla todas las notas creadas por el usuario.**

Creación de una AsyncTask para obtener la lista de las notas en segundo plano. Sustitución de esta por un Servicio para desacoplarlo de la Activity y poder lidiar con los cambios de orientación, etc... Se ha usado un recycler view y un adapter para mostrar los resultados.

**3. Dicha pantalla tendrá un menú desplegable con dos opciones, una de ellas ordenará la lista por el título de la nota y la otra por fecha de creación o modificación.**

Uso del método de la API de Evernote que permite ordenar las busquedas por titulo, fecha, etc... Al igual que en el caso anterior, en este punto se aprovecha también para sincronizar de nuevo las notas por si hay algún cambio.

**4. Al hacer tap sobre una nota, se accederá al contenido de la misma. (No es necesario que las notas sean editables)**

Creación de una nueva vista desde la que mostrar los detalles de la nota seleccionada. Para obtener la información se vuelve a usar otro método de la API de Evernote que te devuelve el contenido de la nota en HTML para despues parsearlo y pasarselo a un TextView.
En este caso y viendo que la ejecución era bastante rapida se ha optado por usar una AsyncTask, aunque se puede hacer igual que en el caso de listar las notas y pasar a usar un Servicio (incluso el mismo que el anterior pero con un Intent diferente).
Debido a que la clase RecyclerView no dispone de un evento OnItemClick, se ha tenido que implementar un OnTouchListener junto con un Gesture Detector para suplirlo. Se podría haber optado por añadir el evento directamente en la vista que infla el adapter, pero creo mas adecuado este enfoque.

**5. Existirá un botón para “añadir nota” que permitirá crear una nota (con título y cuerpo) y posteriormente guardarla.**

Aprovechamos el diseño de las apps de Android para añadir esta funcionalidad en el FAB. Se ha implementado un sencillo dialogo con dos campos de texto para introducir título y contenido. Después se llama a otro método de la API y se sincroniza la nueva nota.

**6.  Al crear una nota, se podrá elegir entre crearla mediante el teclado o bien escribir sobre la pantalla; donde un OCR convertirá la escritura en tipografía de computadora..**

La idea para esta parte es añadir otro dialogo o actividad en la parte de crear una nota que permita la introduccion de texto escrito a mano, como si de un teclado se tratase, pero integrado en la app.
Sabiendo que google saco el año pasado un teclado con reconocimiento de escritura a mano, el primer paso ha sido buscar si habian liberado alguna API al respecto. Parece ser que no.
Otra alternativa sería la búsqueda de alguna librería en la que basar el funcionamiento. Se han encontrado algunas, pero las que son open source no parecen funcionar correctamente (ademas que son muy antiguas, pero se podrian refactorizar), y las que parecen funcionar bien son de pago. La que mejor pinta tiene y que usan grandes aplicaciones como Khan Academy, es MyScript (https://dev.myscript.com/).

