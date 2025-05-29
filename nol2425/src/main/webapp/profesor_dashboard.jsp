<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("key") == null) {
        response.sendRedirect("login_profesor.html");
        return;
    }
    String dni = (String) sesion.getAttribute("dni");
    String nombreProfesor = (String) sesion.getAttribute("nombreAlumno");
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard Profesor - Notas Online</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="style/nolStyles.css" rel="stylesheet">
</head>

<body class="d-flex flex-column min-vh-100">

  <!-- Encabezado -->
  <header class="py-3">
    <div class="container text-center">
      <h1 class="display-4">Dashboard Profesor</h1>
      <p class="lead">Consulta asignaturas</p>
    </div>
  </header>

  <!-- Contenido principal -->
  <main class="container flex-grow-1 my-5">
    <div class="row justify-content-center">
      <div class="col-md-10">
        <div class="card shadow">
          <div class="card-header p-0">
            <ul class="nav nav-tabs" id="asignaturas-tabs" role="tablist">
              <!-- Pestañas generadas por JS -->
            </ul>
          </div>
          <div class="card-body">
            <h5 class="card-title">Bienvenido, <%= nombreProfesor != null ? nombreProfesor : dni %></h5>
            <div class="tab-content" id="asignaturas-content">
              <!-- Contenido generado por JS -->
            </div>
            <div class="mt-3">
              <a href="login_profesor.html" class="btn btn-secondary">Cerrar Sesión</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>

  <!-- Pie de página -->
  <footer class="py-3 mt-auto">
    <div class="container text-center">
      <small class="text-muted">Grupo G1: Ethan Arroyo, Feran Catalán, Pablo Rodríguez, Sergi Beneyto, Carles Hervás</small>
    </div>
  </footer>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
  <script>
  document.addEventListener("DOMContentLoaded", () => {
	  fetch("ProfesorDashboardServlet")
	    .then(res => res.json())
	    .then(data => {
	      const tabContainer = document.getElementById("asignaturas-tabs");
	      const contentContainer = document.getElementById("asignaturas-content");

	      // Limpiar contenedores
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

	      data.forEach(alumno => {
	        tabla += `
	          <tr>
	            <td>${alumno.alumno}</td>
	            <td><button class="btn btn-info btn-sm" onclick="verDetalleAlumno('${alumno.alumno}')">Ver Detalles</button></td>
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

  </script>
</body>
</html>
