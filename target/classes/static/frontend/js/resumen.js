document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const resumenId = params.get('id');
  
    if (!resumenId) {
      document.getElementById('resumen-container').innerHTML = "<p>ID de resumen no proporcionado.</p>";
      return;
    }
  
    fetch(`/catalogo/resumenes/${resumenId}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('No se pudo cargar el resumen.');
        }
        return response.json();
      })
      .then(resumen => {
        document.getElementById('titulo').textContent = resumen.titulo;
        document.getElementById('descripcion').textContent = resumen.descripcion;
        document.getElementById('contenido').textContent = resumen.contenido;
        document.getElementById('autor').textContent = `Autor: ${resumen.autor.nombre}`;
      })
      .catch(error => {
        document.getElementById('resumen-container').innerHTML = "<p>Error al cargar el resumen.</p>";
        console.error(error);
      });
  });
  