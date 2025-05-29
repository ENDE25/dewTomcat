package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;

public class AsignaturaAlumnosServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        String acronimo = request.getParameter("acronimo");
        if (acronimo == null || acronimo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acrónimo no proporcionado");
            return;
        }
        
        String key = (String) session.getAttribute("key");
        String cookie = (String) session.getAttribute("cookieCentro");
      
        String url = "http://localhost:9090/CentroEducativo/asignaturas/" + acronimo + "/alumnos?key=" + key;

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

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error ejecutando curl");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excepción al ejecutar curl");
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(resultado.toString());
    }
}
