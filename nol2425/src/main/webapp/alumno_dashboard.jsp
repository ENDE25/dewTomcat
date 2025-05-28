<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("key") == null) {
        response.sendRedirect("login_alumno.html");
        return;
    }
    String dni = (String) sesion.getAttribute("dni");
    String nombreAlumno = (String) sesion.getAttribute("nombreAlumno");
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard Alumno - Notas Online</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="style/nolStyles.css" rel="stylesheet">
</head>

<body class="d-flex flex-column min-vh-100">

  <!-- Encabezado -->
  <header class="py-3">
    <div class="container text-center">
      <h1 class="display-4">Dashboard Alumno</h1>
      <p class="lead">Consulta tus calificaciones y asignaturas</p>
    </div>
  </header>

  <!-- Contenido principal -->
  <main class="container flex-grow-1 my-5">
    <div class="row justify-content-center">
      <div class="col-md-10">
        <div class="card shadow">
          <!-- Contenedor de pesta�as -->
          <div class="card-header p-0">
            <ul class="nav nav-tabs" id="asignaturas-tabs" role="tablist">
              <!-- Las pesta�as se generar�n din�micamente con JavaScript -->
            </ul>
          </div>
          
          <div class="card-body">
            <h5 class="card-title">Bienvenido, <%= nombreAlumno != null ? nombreAlumno : dni %></h5>
            
            <!-- Contenido de las pesta�as -->
            <div class="tab-content" id="asignaturas-content">
              <!-- El contenido se generar� din�micamente con JavaScript -->
            </div>

            <div class="mt-3">
              <a href="login_alumno.html" class="btn btn-secondary">Cerrar Sesi�n</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>

  <!-- Pie de p�gina -->
  <footer class="py-3 mt-auto">
    <div class="container text-center">
      <small class="text-muted">Grupo G1: Ethan Arroyo, Feran Catal�n, Pablo Rodr�guez, Sergi Beneyto, Carles Herv�s</small>
    </div>
  </footer>
<<<<<<< HEAD
=======
 <script>
document.addEventListener("DOMContentLoaded", () => {
  fetch("AlumnoDashboardServlet")
    .then(res => res.json())
    .then(data => {
      const contenedor = document.getElementById("lista-asignaturas");
>>>>>>> branch 'main' of https://github.com/ENDE25/dewTomcat.git

<<<<<<< HEAD
=======
      data.asignaturas.forEach(asig => {
        console.log("Asignatura recibida:", asig);

        const div = document.createElement("div");
        div.className = "d-flex justify-content-between align-items-center mb-2";

        const boton = document.createElement("a");
        boton.href = "asignaturaDetalleServlet?acronimo=" + encodeURIComponent(asig.acronimo);
        boton.className = "btn btn-outline-primary flex-grow-1 me-2 text-start";
        boton.textContent = asig.nombre;

        const badge = document.createElement("span");
        badge.className = "badge bg-primary align-self-center";
        badge.textContent = asig.nota || "Sin calificar";

        div.appendChild(boton);
        div.appendChild(badge);
        contenedor.appendChild(div);
      });
    })
    .catch(err => {
      console.error("Error cargando asignaturas:", err);
    });
});
</script>
>>>>>>> branch 'main' of https://github.com/ENDE25/dewTomcat.git
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
  <script type="text/javascript" src="js/alumno_dashboard.js"></script>
</body>
<<<<<<< HEAD
</html>
=======
</html>

>>>>>>> branch 'main' of https://github.com/ENDE25/dewTomcat.git
