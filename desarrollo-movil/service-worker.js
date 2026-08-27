
const NOMBRE_CACHE = "reloj-pwa-cache-v1";

// Archivos necesarios para que la app funcione sin conexión
const ARCHIVOS_APP_SHELL = [
  "./",
  "./index.html",
  "./styles.css",
  "./app.js",
  "./manifest.json",
  "./icon-192.png",
  "./icon-512.png",
];

self.addEventListener("install", (evento) => {
  evento.waitUntil(
    caches
      .open(NOMBRE_CACHE)
      .then((cache) => cache.addAll(ARCHIVOS_APP_SHELL))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (evento) => {
  evento.waitUntil(
    caches
      .keys()
      .then((nombresDeCache) =>
        Promise.all(
          nombresDeCache
            .filter((nombre) => nombre !== NOMBRE_CACHE)
            .map((nombre) => caches.delete(nombre))
        )
      )
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (evento) => {
  evento.respondWith(
    caches.match(evento.request).then((respuestaEnCache) => {
      if (respuestaEnCache) {
        return respuestaEnCache;
      }

      return fetch(evento.request).then((respuestaDeRed) => {
       
        if (
          evento.request.method === "GET" &&
          respuestaDeRed &&
          respuestaDeRed.status === 200
        ) {
          const copiaRespuesta = respuestaDeRed.clone();
          caches.open(NOMBRE_CACHE).then((cache) => {
            cache.put(evento.request, copiaRespuesta);
          });
        }
        return respuestaDeRed;
      });
    })
  );
});
