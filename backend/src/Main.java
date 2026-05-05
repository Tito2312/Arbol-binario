package backend.src;

public class Main {
    public static void main(String[] args) throws Exception {
        
        String puerto = System.getenv("PORT");
        int port = (puerto != null) ? Integer.parseInt(puerto) : 8080;

        Servidor.iniciar(port);
        System.out.println("Servidor corriendo en puerto " + port);
    }
}