package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;

public class AsignaturaDetalleServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("dni") == null ||
            session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendRedirect("login_alumno.html?error=session");
            return;
        }

        String acronimo = request.getParameter("acronimo");
        if (acronimo == null || acronimo.isEmpty()) {
            response.sendError(400, "Falta el acrónimo.");
            return;
        }

        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");

        String url = "http://localhost:9090/CentroEducativo/asignaturas/" + acronimo + "?key=" + key;

        String[] command = {
            "curl",
            "-X", "GET",
            url,
            "-H", "accept: application/json",
            "-H", "Cookie: " + cookie
        };

        StringBuilder json = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }

            process.waitFor();

        } catch (Exception e) {
            response.sendError(500, "Error al obtener detalles de la asignatura.");
            return;
        }
        
        String urlProfesor = "http://localhost:9090/CentroEducativo/asignaturas/" + acronimo + "/profesores?key=" + key;

        String[] comandoProfesor = {
            "curl",
            "-X", "GET",
            urlProfesor,
            "-H", "accept: application/json",
            "-H", "Cookie: " + cookie
        };

        StringBuilder jsonProfesor = new StringBuilder();

        try {
            ProcessBuilder pb2 = new ProcessBuilder(comandoProfesor);
            Process proceso2 = pb2.start();

            try (BufferedReader reader2 = new BufferedReader(new InputStreamReader(proceso2.getInputStream()))) {
                String line;
                while ((line = reader2.readLine()) != null) {
                    jsonProfesor.append(line);
                }
            }

            proceso2.waitFor();

        } catch (Exception e) {
            jsonProfesor.append("");
        }
        
        String respuestaProfesor = jsonProfesor.toString();
        String nombreProfe = extraerCampo(respuestaProfesor, "nombre");
        String apellidosProfe = extraerCampo(respuestaProfesor, "apellidos");
        String profesor = (!nombreProfe.equals("No disponible") && !apellidosProfe.equals("No disponible"))
            ? nombreProfe + " " + apellidosProfe
            : "Profesor no disponible";

        // Simulación simple: parseo manual de campos
        String respuesta = json.toString();
        request.setAttribute("acronimo", acronimo);
        request.setAttribute("nombre", extraerCampo(respuesta, "nombre"));
        request.setAttribute("curso", extraerCampo(respuesta, "curso"));
        request.setAttribute("creditos", extraerCampo(respuesta, "creditos"));
        request.setAttribute("profesor", profesor);

        request.getRequestDispatcher("detalle_asignatura.jsp").forward(request, response);
    }

    private String extraerCampo(String json, String campo) {
        String patronTexto = "\"" + campo + "\":\"";
        String patronNumero = "\"" + campo + "\":";

        int inicio = json.indexOf(patronTexto);
        if (inicio != -1) {
            inicio += patronTexto.length();
            int fin = json.indexOf("\"", inicio);
            return json.substring(inicio, fin);
        }

        inicio = json.indexOf(patronNumero);
        if (inicio != -1) {
            inicio += patronNumero.length();
            int fin = json.indexOf(",", inicio);
            if (fin == -1) fin = json.indexOf("}", inicio);
            return json.substring(inicio, fin).trim();
        }

        return "No disponible";
    }
}
