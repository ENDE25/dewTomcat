document.addEventListener("DOMContentLoaded", () => {
  fetch("AlumnoDashboardServlet")   //petición al servlet
    .then(res => res.json())        //lo meto en un json
    .then(data => {                 //esto se ejecuta una vez metida la respuesta en el json
      const tabContainer = document.getElementById("asignaturas-tabs");     //lista ul para las asignaturas
      const contentContainer = document.getElementById("asignaturas-content");    //contenedor para cada una
      
      // Limpiar contenedores
      tabContainer.innerHTML = '';
      contentContainer.innerHTML = '';
      
      // Crear pestañas y contenido
      data.asignaturas.forEach((asig, index) => {      //para cada asignatura ...
        // Crear pestaña (solo con el acrónimo)
        const tabItem = document.createElement("li");     //crear un item de la lista de pestañas por asignatura
        tabItem.className = "nav-item";
        tabItem.role = "presentation";
        
        const tabButton = document.createElement("button");   //botón para entrar en esa asignatura
        tabButton.className = "nav-link" + (index === 0 ? " active" : "");
        tabButton.id = `tab-${asig.nombre}`;
        tabButton.setAttribute("data-bs-toggle", "tab");
        tabButton.setAttribute("data-bs-target", `#content-${asig.nombre}`);
        tabButton.type = "button";
        tabButton.role = "tab";
        tabButton.textContent = asig.nombre; // Solo el acrónimo aquí
        
        tabItem.appendChild(tabButton);       //meter el botón en la pestaña
        tabContainer.appendChild(tabItem);    //meter la pestaña en la lista de pestañas
        
        // Contenido
        const tabPane = document.createElement("div");
        tabPane.className = "tab-pane fade" + (index === 0 ? " show active" : "");
        tabPane.id = `content-${asig.nombre}`;
        tabPane.role = "tabpanel";
        tabPane.setAttribute("aria-labelledby", `tab-${asig.nombre}`);
		tabPane.innerHTML = `
		  <div class="alert alert-info">
		    <div class="d-flex justify-content-between align-items-center">
		      <h4 class="mb-0">${asig.nombre}</h4>
		      <button class="btn btn-sm btn-outline-primary" type="button"
		              data-bs-toggle="collapse"
		              data-bs-target="#detalles-${asig.nombre}"
		              aria-expanded="false"
		              aria-controls="detalles-${asig.nombre}"
		              onclick="cargarDetalles('${asig.nombre}')">
		        Detalles
		      </button>
		    </div>
		    <p class="mt-2">Nota actual: <strong>${asig.nota || 'Sin calificar'}</strong></p>
		    <div class="collapse mt-2" id="detalles-${asig.nombre}">
		      <div class="card card-body" id="contenido-detalles-${asig.nombre}">
		        <div class="text-muted">Cargando...</div>
		      </div>
		    </div>
		  </div>
		`;

        
        contentContainer.appendChild(tabPane);
      });
    })
    .catch(err => {    //por si aca
      console.error("Error cargando asignaturas:", err);
      document.getElementById("asignaturas-content").innerHTML = `
        <div class="alert alert-danger">
          Error al cargar las asignaturas. Inténtelo de nuevo más tarde.
        </div>
      `;
    });
});
function cargarDetalles(acronimo) {
  const contenedor = document.getElementById(`contenido-detalles-${acronimo}`);

  // Si ya hay contenido cargado, no vuelvas a hacer la petición
  if (contenedor.dataset.cargado === "true") return;

  fetch(`AsignaturaDetalleServlet?acronimo=${encodeURIComponent(acronimo)}`)
    .then(res => {
      if (!res.ok) throw new Error("Error cargando detalles");
      return res.text(); // El JSP devuelve HTML, no JSON
    })
    .then(html => {
      contenedor.innerHTML = html;
      contenedor.dataset.cargado = "true"; // Marcar como cargado
    })
    .catch(err => {
      contenedor.innerHTML = `<div class="text-danger">Error al cargar los detalles.</div>`;
    });
}
