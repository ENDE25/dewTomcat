package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AlumnoDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendRedirect("login_alumno.html?error=session");
            return;
        }

        String dni = (String) session.getAttribute("dni");
        
        if (session == null || !"rolalu".equals(session.getAttribute("rol"))) {
            response.sendRedirect("login_alumno.html?error=rol");
            return;
        }
        
        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");
        
        String url = "http://localhost:9090/CentroEducativo/alumnos/" + dni + "/asignaturas?key=" + key;

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
                    resultado.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                response.sendError(500, "Error ejecutando curl");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Excepción al ejecutar curl");
            return;
        }

        // Parse manual JSON plano: [{"asignatura":"DCU","nota":"8.5"},...]
        String json = resultado.toString().trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1); // sin corchetes
        }

        String[] items = json.split("\\},\\{");

        Map<String, String> asignaturasNotas = new HashMap<>();

        for (String item : items) {
            item = item.replace("{", "").replace("}", "");

            String[] partes = item.split(",");
            String asignatura = "";
            String nota = "";

            for (String parte : partes) {
                String[] keyValue = parte.split(":");
                if (keyValue.length == 2) {
                    String clave = keyValue[0].replaceAll("\"", "").trim();
                    String valor = keyValue[1].replaceAll("\"", "").trim();

                    if (clave.equals("asignatura")) {
                        asignatura = valor;
                    } else if (clave.equals("nota")) {
                        nota = valor;
                    }
                }
            }

            if (!asignatura.isEmpty()) {
                asignaturasNotas.put(asignatura, nota);
            }
        }

        // Devolver JSON a partir del Map
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.print("{\"asignaturas\":[");
        boolean primero = true;
        for (Map.Entry<String, String> entry : asignaturasNotas.entrySet()) {
            if (!primero) out.print(",");
            primero = false;
            out.print("{\"nombre\":\"" + entry.getKey() + "\",\"nota\":\"" + entry.getValue() + "\"}");
        }
        out.print("]}");
    }
}
