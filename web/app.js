const API = 'https://arbol-binario-production.up.railway.app/api';
const canvas = document.getElementById('canvas-arbol');
const ctx = canvas.getContext('2d');

// ── Utilidades ─────────────────────────────────────────
function mostrarResultado(mensaje, tipo = 'info') {
    const el = document.getElementById('resultado');
    el.textContent = mensaje;
    el.className = tipo;
}

async function llamarAPI(endpoint) {
    try {
        const res = await fetch(`${API}/${endpoint}`);
        return await res.text();
    } catch (e) {
        mostrarResultado('Error conectando con el servidor Java', 'error');
        return null;
    }
}

// ── Operaciones ────────────────────────────────────────
async function agregar() {
    const val = document.getElementById('input-agregar').value;
    if (!val) return mostrarResultado('Ingresa un número', 'error');
    const res = await llamarAPI(`agregar?dato=${val}`);
    if (res) {
        mostrarResultado(res, 'exito');
        document.getElementById('input-agregar').value = '';
        actualizarArbol();
    }
}

async function existeDato() {
    const val = document.getElementById('input-buscar').value;
    if (!val) return mostrarResultado('Ingresa un número', 'error');
    const res = await llamarAPI(`existeDato?dato=${val}`);
    if (res === 'true')  mostrarResultado(`✅ El dato ${val} SÍ existe en el árbol`, 'exito');
    if (res === 'false') mostrarResultado(`❌ El dato ${val} NO existe en el árbol`, 'error');
}

async function obtenerNivel() {
    const val = document.getElementById('input-buscar').value;
    if (!val) return mostrarResultado('Ingresa un número', 'error');
    const res = await llamarAPI(`obtenerNivel?dato=${val}`);
    if (res === '-1') mostrarResultado(`❌ El dato ${val} no existe en el árbol`, 'error');
    else mostrarResultado(`📍 El dato ${val} está en el nivel ${res}`, 'info');
}

async function eliminar() {
    const val = document.getElementById('input-eliminar').value;
    if (!val) return mostrarResultado('Ingresa un número', 'error');
    const res = await llamarAPI(`eliminar?dato=${val}`);
    if (res) {
        mostrarResultado(res, 'exito');
        document.getElementById('input-eliminar').value = '';
        actualizarArbol();
    }
}

async function recorrer(tipo) {
    const res = await llamarAPI(tipo);
    if (res === '') mostrarResultado('El árbol está vacío', 'error');
    else mostrarResultado(`${tipo.charAt(0).toUpperCase() + tipo.slice(1)}: ${res}`, 'info');
}

async function amplitud() {
    const res = await llamarAPI('imprimirAmplitud');
    mostrarResultado(`Amplitud: ${res}`, 'info');
}

async function obtenerPeso() {
    const res = await llamarAPI('obtenerPeso');
    mostrarResultado(`⚖️ El árbol tiene ${res} nodo(s) en total`, 'info');
}

async function obtenerAltura() {
    const res = await llamarAPI('obtenerAltura');
    mostrarResultado(`📏 La altura del árbol es: ${res}`, 'info');
}

async function contarHojas() {
    const res = await llamarAPI('contarHojas');
    mostrarResultado(`🍃 El árbol tiene ${res} hoja(s)`, 'info');
}

async function obtenerMenor() {
    const res = await llamarAPI('obtenerMenor');
    if (res === '-1') mostrarResultado('El árbol está vacío', 'error');
    else mostrarResultado(`🔽 El valor menor es: ${res}`, 'info');
}

async function obtenerMayor() {
    const res = await llamarAPI('obtenerMayor');
    if (res === '-1') mostrarResultado('El árbol está vacío', 'error');
    else mostrarResultado(`🔼 El valor mayor es: ${res}`, 'info');
}

async function obtenerNodoMenor() {
    const res = await llamarAPI('obtenerNodoMenor');
    if (res === '-1') mostrarResultado('El árbol está vacío', 'error');
    else mostrarResultado(`🔽 El nodo menor tiene el dato: ${res}`, 'info');
}

async function estaVacio() {
    const res = await llamarAPI('estaVacio');
    if (res === 'true') mostrarResultado('⚠️ El árbol está vacío', 'error');
    else mostrarResultado('✅ El árbol NO está vacío', 'exito');
}

function confirmarBorrar() {
    document.getElementById('modal-overlay').classList.remove('oculto');
}

function cerrarModal() {
    document.getElementById('modal-overlay').classList.add('oculto');
}

async function borrarArbol() {
    cerrarModal();
    await llamarAPI('borrarArbol');
    mostrarResultado('🗑️ El árbol ha sido borrado', 'error');
    actualizarArbol();
}

// ── Dibujar el árbol en el canvas ──────────────────────
async function actualizarArbol() {
    const res = await llamarAPI('arbol');
    if (!res) return;

    const datos = JSON.parse(res);
    const vacio = document.getElementById('arbol-vacio-msg');

    // Ajusta el canvas al contenedor
    const contenedor = canvas.parentElement;
    canvas.width  = contenedor.clientWidth;
    canvas.height = contenedor.clientHeight;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (!datos) {
        vacio.style.display = 'block';
        document.getElementById('estado-arbol').textContent = 'Árbol vacío';
        return;
    }

    vacio.style.display = 'none';
    dibujarNodo(datos, canvas.width / 2, 50, canvas.width / 4);

    // Actualiza el estado del header
    const peso   = await llamarAPI('obtenerPeso');
    const altura = await llamarAPI('obtenerAltura');
    document.getElementById('estado-arbol').textContent =
        `${peso} nodo(s) · Altura ${altura}`;
}

function dibujarNodo(nodo, x, y, separacion) {
    if (!nodo) return;

    const radio = 24;

    // Dibuja líneas a los hijos primero
    if (nodo.izquierdo) {
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x - separacion, y + 70);
        ctx.strokeStyle = '#2a2d3e';
        ctx.lineWidth = 2;
        ctx.stroke();
        dibujarNodo(nodo.izquierdo, x - separacion, y + 70, separacion / 2);
    }

    if (nodo.derecho) {
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x + separacion, y + 70);
        ctx.strokeStyle = '#2a2d3e';
        ctx.lineWidth = 2;
        ctx.stroke();
        dibujarNodo(nodo.derecho, x + separacion, y + 70, separacion / 2);
    }

    // Dibuja el círculo del nodo
    ctx.beginPath();
    ctx.arc(x, y, radio, 0, Math.PI * 2);
    ctx.fillStyle = '#2d9cdb';
    ctx.fill();
    ctx.strokeStyle = '#1a7fb5';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Dibuja el número dentro del nodo
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 13px Segoe UI';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(nodo.dato, x, y);
}

// Dibuja el árbol al cargar la página
actualizarArbol();