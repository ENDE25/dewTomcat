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
            <h4>${asig.nombre}</h4>
            <p>Nota actual: <strong>${asig.nota || 'Sin calificar'}</strong></p>
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