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
  <script type="text/javascript" src="js/profesor_dashboard.js"></script>
</body>
</html>
