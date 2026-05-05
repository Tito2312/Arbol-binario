package backend.src;
public class Nodo {
    int dato;
    Nodo izquierdo;
    Nodo derecho;

    public Nodo(int dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
    }
}