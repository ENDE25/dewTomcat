<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("key") == null) {
        response.sendRedirect("login_alumno.html");
        return;
    }

    String acronimo = (String) request.getAttribute("acronimo");
    String nombre = (String) request.getAttribute("nombre");
    String curso = (String) request.getAttribute("curso");
    String creditos = (String) request.getAttribute("creditos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Notas Online – Detalle Asignatura</title>
  <!-- Bootstrap 5 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="style/nolStyles.css" rel="stylesheet">
</head>

<body class="d-flex flex-column min-vh-100">

  <!-- Encabezado -->
  <header class="py-3">
    <div class="container text-center">
      <h1 class="display-4">Notas Online</h1>
      <p class="lead">Detalle de tu asignatura</p>
    </div>
  </header>

  <!-- Contenido principal -->
  <main class="container flex-grow-1 my-5">
    <div class="row justify-content-center gy-4">

      <div class="col-md-6">
        <div class="card h-100 text-center shadow">
          <div class="card-body">
            <h5 class="card-title">Asignatura: <%= acronimo %></h5>
            <p class="card-text"><strong>Nombre:</strong> <%= nombre %></p>
            <p class="card-text"><strong>Curso:</strong> <%= curso %></p>
            <p class="card-text"><strong>Créditos:</strong> <%= creditos %></p>
            <a href="index.jsp" class="btn btn-primary mt-3">Volver al Dashboard</a>
          </div>
        </div>
      </div>

    </div>
  </main>

  <!-- Pie de página con info de grupo -->
  <footer class="py-3 mt-auto">
    <div class="container text-center">
      <small class="text-muted">
        Grupo G1: Ethan Arroyo, Feran Catalán, Pablo Rodríguez, Sergi Beneyto, Carles Hervás
      </small>
    </div>
  </footer>

  <!-- Bootstrap 5 JS Bundle -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>