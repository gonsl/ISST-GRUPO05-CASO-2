document.addEventListener('DOMContentLoaded', () => {
  const contenedor = document.getElementById('contenedor-libros');

  fetch('/catalogo/resumenes')
    .then(response => {
      if (!response.ok) {
        throw new Error('Error al cargar los resúmenes');
      }
      return response.json();
    })
    .then(resumenes => {
      resumenes.forEach(resumen => {
        const card = document.createElement('div');
        card.classList.add('resumen-card');

        card.innerHTML = `
          <h3>${resumen.titulo}</h3>
          <p><strong>Descripción:</strong> ${resumen.descripcion}</p>
          <p><strong>Prime:</strong> ${resumen.prime ? 'Sí' : 'No'}</p>
          <button onclick="verResumen(${resumen.id})">Ver resumen completo</button>
        `;

        contenedor.appendChild(card);
      });
    })
    .catch(error => {
      contenedor.innerHTML = '<p>Error al cargar los resúmenes.</p>';
      console.error(error);
    });
});

function verResumen(id) {
  window.location.href = `/frontend/pages/resumen.html?id=${id}`;
}
