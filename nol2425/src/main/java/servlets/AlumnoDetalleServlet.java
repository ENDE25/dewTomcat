package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        String acronimo = request.getParameter("acronimo");
        request.setAttribute("acronimo", acronimo);

        if (dniAlumno == null || dniAlumno.isEmpty()) {
            response.sendError(400, "Falta el DNI del alumno.");
            return;
        }

        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");

        // --- PRIMERA PETICIÓN: Datos del alumno ---
        String alumnoJson = ejecutarCurl("http://localhost:9090/CentroEducativo/alumnos/" + dniAlumno + "?key=" + key, cookie);
        if (alumnoJson == null) {
            response.sendError(500, "Error obteniendo datos del alumno");
            return;
        }

        // --- SEGUNDA PETICIÓN: Asignaturas del alumno ---
        String asignaturasJson = ejecutarCurl("http://localhost:9090/CentroEducativo/alumnos/" + dniAlumno + "/asignaturas?key=" + key, cookie);

        List<String> asignaturas = new ArrayList<>();
        String[] objetos = new String[0];  // <- Declaramos aquí

        if (asignaturasJson != null && asignaturasJson.startsWith("[")) {
            asignaturasJson = asignaturasJson.substring(1, asignaturasJson.length() - 1); // quitar [ ]
            objetos = asignaturasJson.split("\\},\\{");

            for (String obj : objetos) {
                obj = obj.replace("{", "").replace("}", ""); // limpiar
                String[] campos = obj.split(",");
                for (String campo : campos) {
                    if (campo.contains("\"asignatura\"")) {
                        String valor = campo.split(":")[1].replaceAll("\"", "").trim();
                        asignaturas.add(valor);
                    }
                }
            }
        }

        request.setAttribute("dni", dniAlumno);
        request.setAttribute("nombre", extraerCampo(alumnoJson, "nombre"));
        request.setAttribute("apellidos", extraerCampo(alumnoJson, "apellidos"));
        request.setAttribute("asignaturas", asignaturas);

        // --- Buscar nota del alumno para la asignatura ---
        String nota = "No disponible";
        if (acronimo != null && !acronimo.isEmpty()) {
            for (String obj : objetos) {
                obj = obj.replace("{", "").replace("}", "");
                String[] campos = obj.split(",");
                String asignaturaEncontrada = null;
                String notaEncontrada = null;

                for (String campo : campos) {
                    if (campo.contains("\"asignatura\"")) {
                        asignaturaEncontrada = campo.split(":")[1].replaceAll("\"", "").trim();
                    }
                    if (campo.contains("\"nota\"")) {
                        notaEncontrada = campo.split(":")[1].replaceAll("\"", "").trim();
                    }
                }

                if (asignaturaEncontrada != null && asignaturaEncontrada.equals(acronimo)) {
                    nota = notaEncontrada != null ? notaEncontrada : "No disponible";
                    break;
                }
            }
        }

        request.setAttribute("nota", nota);
        request.getRequestDispatcher("detalle_alumno.jsp").forward(request, response);
    }

    private String ejecutarCurl(String url, String cookie) {
        String[] command = {
            "curl", "-X", "GET", url,
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
            int exitCode = process.waitFor();
            if (exitCode != 0) return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return resultado.toString();
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
