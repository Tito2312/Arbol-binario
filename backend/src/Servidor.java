package backend.src;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Servidor {
    private static ArbolBinario arbol = new ArbolBinario();

    public static void iniciar(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Maneja OPTIONS para CORS
        server.createContext("/", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
                exchange.sendResponseHeaders(204, -1);
                exchange.getResponseBody().close();
            } else {
                new ArchivoHandler().handle(exchange);
            }
        });

        // Endpoints de la API
        server.createContext("/api/estaVacio",       exchange -> responder(exchange, String.valueOf(arbol.estaVacio())));
        server.createContext("/api/agregar",          new AgregarHandler());
        server.createContext("/api/inorden",          exchange -> responder(exchange, arbol.inorden()));
        server.createContext("/api/preorden",         exchange -> responder(exchange, arbol.preorden()));
        server.createContext("/api/postorden",        exchange -> responder(exchange, arbol.postorden()));
        server.createContext("/api/existeDato",       new ExisteHandler());
        server.createContext("/api/obtenerPeso",      exchange -> responder(exchange, String.valueOf(arbol.obtenerPeso())));
        server.createContext("/api/obtenerAltura",    exchange -> responder(exchange, String.valueOf(arbol.obtenerAltura())));
        server.createContext("/api/obtenerNivel",     new NivelHandler());
        server.createContext("/api/contarHojas",      exchange -> responder(exchange, String.valueOf(arbol.contarHojas())));
        server.createContext("/api/obtenerMenor",     exchange -> responder(exchange, String.valueOf(arbol.obtenerMenor())));
        server.createContext("/api/imprimirAmplitud", exchange -> responder(exchange, arbol.imprimirAmplitud()));
        server.createContext("/api/eliminar",         new EliminarHandler());
        server.createContext("/api/obtenerMayor",     exchange -> responder(exchange, String.valueOf(arbol.obtenerNodoMayor())));
        server.createContext("/api/obtenerNodoMenor", exchange -> responder(exchange, String.valueOf(arbol.obtenerMenor())));
        server.createContext("/api/borrarArbol",      exchange -> { arbol.borrarArbol(); responder(exchange, "Árbol borrado"); });
        server.createContext("/api/arbol",            exchange -> responder(exchange, arbol.arbolAJson(arbol.getRaiz())));

        server.start();
        System.out.println("✅ Servidor corriendo en puerto " + port);
    }

    // ── Handlers con parámetros ───────────────────────────
    static class AgregarHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int dato = Integer.parseInt(query.split("=")[1]);
            arbol.agregar(dato);
            responder(exchange, "Dato " + dato + " agregado correctamente");
        }
    }

    static class ExisteHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int dato = Integer.parseInt(query.split("=")[1]);
            responder(exchange, String.valueOf(arbol.existeDato(dato)));
        }
    }

    static class NivelHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int dato = Integer.parseInt(query.split("=")[1]);
            responder(exchange, String.valueOf(arbol.obtenerNivel(dato)));
        }
    }

    static class EliminarHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int dato = Integer.parseInt(query.split("=")[1]);
            arbol.eliminarDato(dato);
            responder(exchange, "Dato " + dato + " eliminado correctamente");
        }
    }

    // ── Sirve archivos estáticos ──────────────────────────
    static class ArchivoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            File archivo = new File("web" + path);
            if (archivo.exists()) {
                byte[] bytes = Files.readAllBytes(Paths.get(archivo.getPath()));
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } else {
                byte[] bytes = "404 Not Found".getBytes("UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
            exchange.getResponseBody().close();
        }
    }

    // ── Respuesta genérica con CORS ───────────────────────
    static void responder(HttpExchange exchange, String respuesta) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        byte[] bytes = respuesta.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}