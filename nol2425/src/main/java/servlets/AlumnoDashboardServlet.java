package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;

@WebServlet("/AlumnoDashboardServlet")
public class AlumnoDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("dni") == null || session.getAttribute("key") == null || session.getAttribute("cookieCentro") == null) {
            response.sendRedirect("login_alumno.html?error=session");
            return;
        }

        String dni = (String) session.getAttribute("dni");
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

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<h2>Asignaturas del alumno:</h2>");
        out.println("<pre>" + resultado.toString() + "</pre>");
    }
}


