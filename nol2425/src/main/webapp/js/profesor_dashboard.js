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
		document.getElementById("btn-media").addEventListener("click", calcularMedia);
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
	        const detalleId = `detalle-${alumnoObj.alumno}-${acronimo}`;
	        const contenidoDetalleId = `contenido-detalle-${alumnoObj.alumno}-${acronimo}`;
	        tabla += `
	          <tr>
	            <td>${alumnoObj.alumno}</td>
	            <td>
	              <button class="btn btn-primary btn-sm" type="button" data-bs-toggle="collapse"
	                data-bs-target="#${detalleId}" aria-expanded="false" aria-controls="${detalleId}"
	                onclick="verDetalleAlumno('${alumnoObj.alumno}', '${acronimo}', this)">Mostrar Detalles</button>
	            </td>
	          </tr>
	          <tr>
	            <td colspan="2">
	              <div class="collapse" id="${detalleId}">
	                <div class="card card-body" id="${contenidoDetalleId}">
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
	
	function verDetalleAlumno(dni, acronimo, boton) {
	  const contenedor = document.getElementById(`contenido-detalle-${dni}-${acronimo}`);
	  const collapseDiv = document.getElementById(`detalle-${dni}-${acronimo}`);

	  if (!contenedor.dataset.cargado) {
		fetch(`AlumnoDetalleServlet?dni=${encodeURIComponent(dni)}&acronimo=${encodeURIComponent(acronimo)}`)
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

	  collapseDiv.addEventListener('shown.bs.collapse', () => {
	    boton.textContent = 'Ocultar Detalles';
	  });
	  collapseDiv.addEventListener('hidden.bs.collapse', () => {
	    boton.textContent = 'Mostrar Detalles';
	  });
	}

	function calcularMedia() {
	  const activeTab = document.querySelector('.nav-link.active');
	  if (!activeTab) return;

	  const acronimo = activeTab.textContent;
	  const containerId = `alumnos-${acronimo}`;
	  const mediaId = `media-${acronimo}`;

	  // Evitar duplicados: si ya existe, lo eliminamos
	  const viejo = document.getElementById(mediaId);
	  if (viejo) viejo.remove();

	  fetch("AsignaturaAlumnosServlet?acronimo=" + encodeURIComponent(acronimo))
	    .then(res => res.json())
	    .then(data => {
	      const notas = data.map(a => parseFloat(a.nota)).filter(n => !isNaN(n));
	      const contenedor = document.getElementById(containerId);

	      const div = document.createElement("div");
	      div.className = "alert alert-info mt-3";
	      div.id = mediaId;

	      if (notas.length === 0) {
	        div.textContent = "No hay notas disponibles para esta asignatura.";
	      } else {
	        const media = notas.reduce((acc, n) => acc + n, 0) / notas.length;
	        div.textContent = `Media de la asignatura ${acronimo}: ${media.toFixed(2)}`;
	      }

	      contenedor.appendChild(div);
	    })
	    .catch(err => {
	      console.error("Error calculando media:", err);
	      const contenedor = document.getElementById(containerId);
	      const div = document.createElement("div");
	      div.className = "alert alert-danger mt-3";
	      div.textContent = "Error al calcular la media.";
	      contenedor.appendChild(div);
	    });
	}

	function cambiarNota(dni, acronimo) {
	  const input = document.getElementById(`input-nota-${dni}-${acronimo}`);
	  const nuevaNota = input.value.trim();

	  if (nuevaNota === "" || isNaN(nuevaNota) || nuevaNota < 0 || nuevaNota > 10) {
	    alert("Introduce una nota válida entre 0 y 10.");
	    return;
	  }

	  fetch("ActualizarNotaServlet", {
	    method: "PUT",
	    headers: { "Content-Type": "application/json" },
	    body: JSON.stringify({ dni, acronimo, nota: nuevaNota })
	  })
	  .then(res => {
	    if (!res.ok) throw new Error("Error en la actualización");
	    return res.text();
	  })
	  .then(msg => {
	    document.getElementById(`nota-valor-${dni}-${acronimo}`).textContent = nuevaNota;
	    input.value = ""; // Vaciar el input tras actualizar
	    alert("Nota actualizada correctamente");
	  })
	  .catch(err => {
	    console.error(err);
	    alert("No se pudo actualizar la nota.");
	  });
	}
