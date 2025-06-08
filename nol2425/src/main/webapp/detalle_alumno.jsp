<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%
    String nombre = (String) request.getAttribute("nombre");
    String apellidos = (String) request.getAttribute("apellidos");
    String dni = (String) request.getAttribute("dni");
    List<String> asignaturas = (List<String>) request.getAttribute("asignaturas");
%>


    <h4><strong><%= apellidos %>, <%= nombre %></strong> (<%= dni %>)</h4>

    <div class="row mt-3">
        <div class="col-md-3 text-center">
            <img src="img/<%= dni %>.png" alt="Foto de <%= nombre %>" class="img-fluid rounded">
        </div>
        <div class="col-md-9">
            <p><strong>[Matriculad@ en:</strong>
            	<% for (int i = 0; i < asignaturas.size(); i++) { %>
                    <%= asignaturas.get(i) %><%= (i < asignaturas.size() - 1) ? ", " : "" %>
                <% } %>
            <strong>]</strong>
            </p>
            <p>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. 
                Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, 
                ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim.
            </p>
            <%
    			String nota = (String) request.getAttribute("nota");
    			String acronimo = (String) request.getAttribute("acronimo");
			%>

				<div class="mt-3 d-flex align-items-center gap-2">
  					<strong>Nota:</strong>
  					<span id="nota-valor-<%= dni %>-<%= acronimo %>" class="me-3"><%= nota != null ? nota : "Sin calificar" %></span>

  					<button class="btn btn-sm btn-primary"
          			onclick="cambiarNota('<%= dni %>', '<%= acronimo %>')">Cambiar nota</button>

  					<input type="number" min="0" max="10" step="0.1"
         			id="input-nota-<%= dni %>-<%= acronimo %>"
         			class="form-control form-control-sm"
         			style="width: 80px;">
				</div>
        </div>
    </div>

