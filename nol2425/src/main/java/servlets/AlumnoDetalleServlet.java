package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;

public class AlumnoDetalleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("dni") == null ||
            session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendRedirect("login_profesor.html?error=session");
            return;
        }

        String dniAlumno = request.getParameter("dni");
        if (dniAlumno == null || dniAlumno.isEmpty()) {
            response.sendError(400, "Falta el DNI del alumno.");
            return;
        }

        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");

        String url = "http://localhost:9090/CentroEducativo/alumnos/" + dniAlumno + "?key=" + key;

        String[] command = {
            "curl",
            "-X", "GET",
            url,
            "-H", "accept: application/json",
            "-H", "Cookie: " + cookie
        };

        StringBuilder resultado = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    resultado.append(line);
                }
            }

            process.waitFor();

        } catch (Exception e) {
            response.sendError(500, "Error al obtener detalles del alumno.");
            return;
        }

        String json = resultado.toString();
        request.setAttribute("dni", dniAlumno);
        request.setAttribute("nombre", extraerCampo(json, "nombre"));
        request.setAttribute("apellidos", extraerCampo(json, "apellidos"));
        request.setAttribute("correo", extraerCampo(json, "correo"));

        request.getRequestDispatcher("detalle_alumno.jsp").forward(request, response);
    }

    private String extraerCampo(String json, String campo) {
        String patronTexto = "\"" + campo + "\":\"";
        int inicio = json.indexOf(patronTexto);
        if (inicio != -1) {
            inicio += patronTexto.length();
            int fin = json.indexOf("\"", inicio);
            return json.substring(inicio, fin);
        }
        return "No disponible";
    }
}
