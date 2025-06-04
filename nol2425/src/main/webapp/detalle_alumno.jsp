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
            <img src="fotos/<%= dni %>.jpg" alt="Foto de <%= nombre %>" class="img-fluid rounded">
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
        </div>
    </div>

