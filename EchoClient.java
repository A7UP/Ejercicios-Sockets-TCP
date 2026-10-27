JSMS - Java Socket Messaging System
Compilar y ejecutar (cada carpeta es independiente; todas usan el puerto 8080, ejecutar de una en una):

Ejercicio 1:  cd ejercicio1 && javac *.java && java EchoServer      (otra terminal: java EchoClient)
Ejercicio 2:  cd ejercicio2 && javac *.java && java MultiClientServer   (prueba: varios "nc localhost 8080")
Ejercicios 3-5 (sistema completo):
              cd ejercicio3_4_5 && javac *.java && java JSMSServer
              (otras terminales: java ChatClient)
Protocolo: CONNECT <user> | JOIN <sala> | MSG <texto> | QUIT   (cada mensaje termina en \n)
