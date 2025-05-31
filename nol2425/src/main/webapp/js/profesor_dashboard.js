document.addEventListener("DOMContentLoaded", () => {
	  fetch("ProfesorDashboardServlet")
	    .then(res => res.json())
	    .then(data => {
	      const tabContainer = document.getElementById("asignaturas-tabs");
	      const contentContainer = document.getElementById("asignaturas-content");

	      // Limpiar contenedoores
	      tabContainer.innerHTML = '';
	      contentContainer.innerHTML = '';

	      data.asignaturas.forEach((asig, index) => {
	        const acronimo = asig.acronimo;

	        // Crear pestaña
	        const tabItem = document.createElement("li");
	        tabItem.className = "nav-item";
	        tabItem.role = "presentation";

	        const tabButton = document.createElement("button");
	        tabButton.className = "nav-link" + (index === 0 ? " active" : "");
	        tabButton.id = `tab-${acronimo}`;
	        tabButton.setAttribute("data-bs-toggle", "tab");
	        tabButton.setAttribute("data-bs-target", `#content-${acronimo}`);
	        tabButton.type = "button";
	        tabButton.role = "tab";
	        tabButton.textContent = acronimo;

	        tabItem.appendChild(tabButton);
	        tabContainer.appendChild(tabItem);

	        // Crear contenido de la pestaña
	        const tabPane = document.createElement("div");
	        tabPane.className = "tab-pane fade" + (index === 0 ? " show active" : "");
	        tabPane.id = `content-${acronimo}`;
	        tabPane.role = "tabpanel";
	        tabPane.setAttribute("aria-labelledby", `tab-${acronimo}`);
	        tabPane.innerHTML = `<div id="alumnos-${acronimo}"></div>`;

	        contentContainer.appendChild(tabPane);
	      });

	      // Agregar evento para cargar alumnos al activar una pestaña
	      const tabButtons = document.querySelectorAll('#asignaturas-tabs button');
	      tabButtons.forEach(button => {
	        button.addEventListener('shown.bs.tab', event => {
	          const acronimo = event.target.textContent;
	          const containerId = `alumnos-${acronimo}`;
	          cargarAlumnos(acronimo, containerId);
	        });
	      });

	      // Cargar alumnos de la primera pestaña por defecto
	      if (data.asignaturas.length > 0) {
	        const firstAcronimo = data.asignaturas[0].acronimo;
	        const firstContainerId = `alumnos-${firstAcronimo}`;
	        cargarAlumnos(firstAcronimo, firstContainerId);
	      }
	    })
	    .catch(err => {
	      console.error("Error cargando asignaturas:", err);
	      document.getElementById("asignaturas-content").innerHTML = `
	        <div class="alert alert-danger">
	          Error al cargar las asignaturas. Inténtelo de nuevo más tarde.
	        </div>
	      `;
	    });
	});

	function cargarAlumnos(acronimo, containerId) {
	  fetch("AsignaturaAlumnosServlet?acronimo=" + encodeURIComponent(acronimo))
	    .then(res => res.json())
	    .then(data => {
	      const container = document.getElementById(containerId);

	      let tabla = `
	        <table class="table table-striped mt-3">
	          <thead>
	            <tr><th>DNI</th><th>Acciones</th></tr>
	          </thead>
	          <tbody>
	      `;

	      data.forEach(alumnoObj => {
			tabla += `
			  <tr>
			    <td>${alumnoObj.alumno}</td>
			    <td>
			      <button class="btn btn-info btn-sm" type="button" data-bs-toggle="collapse"
			        data-bs-target="#detalle-${alumnoObj.alumno}"
			        onclick="verDetalleAlumno('${alumnoObj.alumno}')">Ver Detalles</button>
			    </td>
			  </tr>
			  <tr>
			    <td colspan="2">
			      <div class="collapse" id="detalle-${alumnoObj.alumno}">
			        <div class="card card-body" id="contenido-detalle-${alumnoObj.alumno}">
			          <div class="text-muted">Cargando...</div>
			        </div>
			      </div>
			    </td>
			  </tr>
			`;


	      });

	      tabla += "</tbody></table>";
	      container.innerHTML = tabla;
	    })
	    .catch(err => {
	      console.error("Error cargando alumnos:", err);
	      const container = document.getElementById(containerId);
	      container.innerHTML = `
	        <div class="alert alert-danger mt-3">
	          Error al cargar los alumnos. Inténtelo de nuevo más tarde.
	        </div>
	      `;
	    });
	}
	
	function verDetalleAlumno(dni) {
	  const contenedor = document.getElementById(`contenido-detalle-${dni}`);
	  if (contenedor.dataset.cargado === "true") return;

	  fetch(`AlumnoDetalleServlet?dni=${encodeURIComponent(dni)}`)
	    .then(res => {
	      if (!res.ok) throw new Error("Error cargando detalles");
	      return res.text();
	    })
	    .then(html => {
	      contenedor.innerHTML = html;
	      contenedor.dataset.cargado = "true";
	    })
	    .catch(err => {
	      contenedor.innerHTML = `<div class="text-danger">Error al cargar los detalles.</div>`;
	    });
	}


