package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;

@WebServlet("/ActualizarNotaServlet")
public class ActualizarNotaServlet extends HttpServlet {

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");

        // Leer cuerpo del JSON
        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        // Extraer campos manualmente (simple)
        String dni = extraerCampo(json.toString(), "dni");
        String acronimo = extraerCampo(json.toString(), "acronimo");
        String nota = extraerCampo(json.toString(), "nota");

        if (dni == null || acronimo == null || nota == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos incompletos");
            return;
        }

        String url = "http://localhost:9090/CentroEducativo/alumnos/" + dni + "/asignaturas/" + acronimo + "?key=" + key;

        String[] command = {
            "curl", "-X", "PUT", url,
            "-H", "accept: text/plain",
            "-H", "Cookie: " + cookie,
            "-H", "Content-Type: application/json",
            "-d", nota
        };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error actualizando nota");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Nota actualizada correctamente.");
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno");
        }
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

        return null;
    }
}

