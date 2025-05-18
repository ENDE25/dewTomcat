package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dni = request.getParameter("dni");
        String password = request.getParameter("password");

        // Aquí deberías validar las credenciales con el sistema de datos
        if (isValidUser (dni, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("userDNI", dni);
            // Redirigir según el rol
            if (isAlumno(dni)) {
                response.sendRedirect("alumno_dashboard.html");
            } else {
                response.sendRedirect("profesor_dashboard.html");
            }
        } else {
            response.sendRedirect("index.html?error=invalid");
        }
    }

    private boolean isValidUser (String dni, String password) {
        // Implementar la lógica de validación de usuario
        return true; // Cambiar según la lógica real
    }

    private boolean isAlumno(String dni) {
        // Implementar la lógica para determinar si es alumno
        return true; // Cambiar según la lógica real
    }
}
