
const hoursElement = document.getElementById("hours");
const minutesElement = document.getElementById("minutes");
const secondsElement = document.getElementById("seconds");
const dateElement = document.getElementById("current-date");
const connectionDot = document.getElementById("connection-dot");
const connectionText = document.getElementById("connection-text");
const installButton = document.getElementById("install-button");

/**
 * Agrega un cero a la izquierda cuando el número es menor a 10.
 * Ejemplo: 5 -> "05"
 * @param {number} value
 * @returns {string}
 */
function agregarCeroInicial(value) {
  return String(value).padStart(2, "0");
}

function actualizarReloj() {
  const ahora = new Date();

  hoursElement.textContent = agregarCeroInicial(ahora.getHours());
  minutesElement.textContent = agregarCeroInicial(ahora.getMinutes());
  secondsElement.textContent = agregarCeroInicial(ahora.getSeconds());
}

function actualizarFecha() {
  const ahora = new Date();
  const opciones = {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  };
  const fechaTexto = ahora.toLocaleDateString("es-MX", opciones);

  dateElement.textContent =
    fechaTexto.charAt(0).toUpperCase() + fechaTexto.slice(1);
}

function actualizarFechaYHora() {
  actualizarReloj();
  actualizarFecha();
}

actualizarFechaYHora();

setInterval(actualizarFechaYHora, 1000);

function actualizarEstadoConexion() {
  const estaConectado = navigator.onLine;

  connectionText.textContent = estaConectado ? "Conectado" : "Sin conexión";
  connectionDot.classList.toggle("offline", !estaConectado);
}

window.addEventListener("online", actualizarEstadoConexion);
window.addEventListener("offline", actualizarEstadoConexion);
actualizarEstadoConexion();

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker
      .register("service-worker.js")
      .then((registro) => {
        console.log("Service Worker registrado correctamente:", registro.scope);
      })
      .catch((error) => {
        console.error("Error al registrar el Service Worker:", error);
      });
  });
}

let eventoDeInstalacionDiferido = null;

window.addEventListener("beforeinstallprompt", (evento) => {
  // Evita que el navegador muestre su propio banner automático
  evento.preventDefault();

  eventoDeInstalacionDiferido = evento;
  installButton.hidden = false;
});

installButton.addEventListener("click", async () => {
  if (!eventoDeInstalacionDiferido) return;

  eventoDeInstalacionDiferido.prompt();
  await eventoDeInstalacionDiferido.userChoice;

  eventoDeInstalacionDiferido = null;
  installButton.hidden = true;
});

window.addEventListener("appinstalled", () => {
  installButton.hidden = true;
});
