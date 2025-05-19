package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        String jsonInput = String.format("{\"dni\":\"%s\", \"password\":\"%s\"}", dni, password);

        URL url = new URL("http://localhost:9090/CentroEducativo/login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Enviar el JSON
        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonInput.getBytes("UTF-8"));
        }

        int status = con.getResponseCode();
        if (status == 200) {
            // Leer key del cuerpo
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String key = sb.toString().trim().replace("\"", "");
            
            if(key.equals("-1")) {
            	System.out.println("pene");
            	response.sendRedirect("login_alumno.html?error=true");
            	return;
            }
            
            System.out.println(key);            
            // Leer la cookie
            String setCookie = con.getHeaderField("Set-Cookie");
            String cookie = null;
            if (setCookie != null) {
                cookie = setCookie.split(";", 2)[0];
            }

            // Guardar en sesión
            HttpSession session = request.getSession();
            session.setAttribute("dni", dni);
            session.setAttribute("password", password);
            session.setAttribute("key", key);
            session.setAttribute("cookieCentro", cookie);  // Para reutilizarla después

            if (dni.startsWith("1") || dni.startsWith("3")) {
                response.sendRedirect("index.jsp");
            } else {
                response.sendRedirect("profesor/index.jsp");
            }

        } else {
            // Login fallido
            response.sendRedirect("login_alumno.html?error=true");
        }

        con.disconnect();
    }
}