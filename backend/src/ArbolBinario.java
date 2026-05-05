package backend.src;
import java.util.LinkedList;
import java.util.Queue;

public class ArbolBinario {
    private Nodo raiz;

    public ArbolBinario() { this.raiz = null; }

    public boolean estaVacio() { return raiz == null; }

    public void agregar(int dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacio()) raiz = nuevo;
        else agregarRecursivo(raiz, nuevo);
    }

    private void agregarRecursivo(Nodo actual, Nodo nuevo) {
        if (nuevo.dato < actual.dato) {
            if (actual.izquierdo == null) actual.izquierdo = nuevo;
            else agregarRecursivo(actual.izquierdo, nuevo);
        } else if (nuevo.dato > actual.dato) {
            if (actual.derecho == null) actual.derecho = nuevo;
            else agregarRecursivo(actual.derecho, nuevo);
        }
    }

    public String inorden() {
        StringBuilder r = new StringBuilder();
        inordenRecursivo(raiz, r);
        return r.toString().trim();
    }

    private void inordenRecursivo(Nodo actual, StringBuilder r) {
        if (actual != null) {
            inordenRecursivo(actual.izquierdo, r);
            r.append(actual.dato).append(" ");
            inordenRecursivo(actual.derecho, r);
        }
    }

    public String preorden() {
        StringBuilder r = new StringBuilder();
        preordenRecursivo(raiz, r);
        return r.toString().trim();
    }

    private void preordenRecursivo(Nodo actual, StringBuilder r) {
        if (actual != null) {
            r.append(actual.dato).append(" ");
            preordenRecursivo(actual.izquierdo, r);
            preordenRecursivo(actual.derecho, r);
        }
    }

    public String postorden() {
        StringBuilder r = new StringBuilder();
        postordenRecursivo(raiz, r);
        return r.toString().trim();
    }

    private void postordenRecursivo(Nodo actual, StringBuilder r) {
        if (actual != null) {
            postordenRecursivo(actual.izquierdo, r);
            postordenRecursivo(actual.derecho, r);
            r.append(actual.dato).append(" ");
        }
    }

    public boolean existeDato(int dato) {
        if (estaVacio()) return false;
        return buscarRecursivo(raiz, dato);
    }

    private boolean buscarRecursivo(Nodo actual, int dato) {
        if (actual == null) return false;
        if (dato == actual.dato) return true;
        else if (dato < actual.dato) return buscarRecursivo(actual.izquierdo, dato);
        else return buscarRecursivo(actual.derecho, dato);
    }

    public int obtenerPeso() {
        if (estaVacio()) return 0;
        return contarNodos(raiz);
    }

    private int contarNodos(Nodo actual) {
        if (actual == null) return 0;
        return 1 + contarNodos(actual.izquierdo) + contarNodos(actual.derecho);
    }

    public int obtenerAltura() {
        if (estaVacio()) return 0;
        return calcularAltura(raiz);
    }

    private int calcularAltura(Nodo actual) {
        if (actual == null) return 0;
        return 1 + Math.max(calcularAltura(actual.izquierdo),
                            calcularAltura(actual.derecho));
    }

    public int obtenerNivel(int dato) {
        if (estaVacio()) return -1;
        return buscarNivel(raiz, dato, 1);
    }

    private int buscarNivel(Nodo actual, int dato, int nivelActual) {
        if (actual == null) return -1;
        if (dato == actual.dato) return nivelActual;
        else if (dato < actual.dato) return buscarNivel(actual.izquierdo, dato, nivelActual + 1);
        else return buscarNivel(actual.derecho, dato, nivelActual + 1);
    }

    public int contarHojas() {
        if (estaVacio()) return 0;
        return contarHojasRecursivo(raiz);
    }

    private int contarHojasRecursivo(Nodo actual) {
        if (actual == null) return 0;
        if (actual.izquierdo == null && actual.derecho == null) return 1;
        return contarHojasRecursivo(actual.izquierdo) +
               contarHojasRecursivo(actual.derecho);
    }

    public int obtenerMenor() {
        if (estaVacio()) return -1;
        return buscarMenor(raiz);
    }

    private int buscarMenor(Nodo actual) {
        if (actual.izquierdo == null) return actual.dato;
        return buscarMenor(actual.izquierdo);
    }

    public String imprimirAmplitud() {
        if (estaVacio()) return "El árbol está vacío";
        StringBuilder r = new StringBuilder();
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            r.append(actual.dato).append(" ");
            if (actual.izquierdo != null) cola.add(actual.izquierdo);
            if (actual.derecho != null)   cola.add(actual.derecho);
        }
        return r.toString().trim();
    }

    public void eliminarDato(int dato) {
        if (estaVacio()) return;
        raiz = eliminarRecursivo(raiz, dato);
    }

    private Nodo eliminarRecursivo(Nodo actual, int dato) {
        if (actual == null) return null;
        if (dato < actual.dato) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, dato);
        } else if (dato > actual.dato) {
            actual.derecho = eliminarRecursivo(actual.derecho, dato);
        } else {
            if (actual.izquierdo == null && actual.derecho == null) return null;
            if (actual.izquierdo == null) return actual.derecho;
            if (actual.derecho == null)   return actual.izquierdo;
            int sucesor = buscarMenor(actual.derecho);
            actual.dato = sucesor;
            actual.derecho = eliminarRecursivo(actual.derecho, sucesor);
        }
        return actual;
    }

    public int obtenerNodoMayor() {
        if (estaVacio()) return -1;
        return buscarMayor(raiz);
    }

    private int buscarMayor(Nodo actual) {
        if (actual.derecho == null) return actual.dato;
        return buscarMayor(actual.derecho);
    }

    public Nodo obtenerNodoMenor() {
        if (estaVacio()) return null;
        return buscarNodoMenor(raiz);
    }

    private Nodo buscarNodoMenor(Nodo actual) {
        if (actual.izquierdo == null) return actual;
        return buscarNodoMenor(actual.izquierdo);
    }

    public void borrarArbol() { raiz = null; }

    // ── Método especial para enviar el árbol al frontend ──
    public String arbolAJson(Nodo nodo) {
        if (nodo == null) return "null";
        return "{\"dato\":" + nodo.dato +
               ",\"izquierdo\":" + arbolAJson(nodo.izquierdo) +
               ",\"derecho\":" + arbolAJson(nodo.derecho) + "}";
    }

    public Nodo getRaiz() { return raiz; }
}