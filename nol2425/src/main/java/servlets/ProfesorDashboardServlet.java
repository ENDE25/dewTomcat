package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profesor_dashboard")
public class ProfesorDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String dni = (String) session.getAttribute("userDNI");

        // Aquí deberías cargar las asignaturas del profesor desde el sistema de datos
        // Enviar datos a la página de dashboard
        request.setAttribute("nombre", "Profesor"); // Cambiar según la lógica real
        request.getRequestDispatcher("profesor_dashboard.html").forward(request, response);
    }
}
