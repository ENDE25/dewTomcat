package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.*;

public class ProfesorLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dni = request.getParameter("dni");
        String password = request.getParameter("password");
        String jsonInput = String.format("{\"dni\":\"%s\", \"password\":\"%s\"}", dni, password);

        // Paso 1: Hacer login
        URL url = new URL("http://localhost:9090/CentroEducativo/login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonInput.getBytes("UTF-8"));
        }

        int status = con.getResponseCode();
        if (status != 200) {
            response.sendRedirect("login_profesor.html?error=true");
            return;
        }

        // Leer key
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String key = sb.toString().trim().replace("\"", "");
        if (key.equals("-1")) {
            response.sendRedirect("login_profesor.html?error=true");
            return;
        }

        // Leer cookie
        String setCookie = con.getHeaderField("Set-Cookie");
        String cookie = null;
        if (setCookie != null) {
            cookie = setCookie.split(";", 2)[0];
        }
        con.disconnect();

        // Verificar si es profesor usando método separado
        if (!esProfesor(dni, key, cookie)) {
            response.sendRedirect("login_profesor.html?error=rol");
            return;
        }

        // Guardar en sesión
        HttpSession session = request.getSession();
        session.setAttribute("dni", dni);
        session.setAttribute("password", password);
        session.setAttribute("key", key);
        session.setAttribute("cookieCentro", cookie);
        session.setAttribute("rol", "rolpro");
        session.setAttribute("nombreProfesor", dni); // Puedes ajustar esto si tienes el nombre real

        response.sendRedirect("profesor_dashboard.jsp");
    }

    private boolean esProfesor(String dni, String key, String cookie) {
        String url = "http://localhost:9090/CentroEducativo/profesores?key=" + key;
        String[] curlCommand = {
            "curl",
            "-X", "GET",
            url,
            "-H", "accept: application/json",
            "-H", "Cookie: " + cookie
        };

        StringBuilder profesoresJson = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(curlCommand);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    profesoresJson.append(line);
                }
            }

            process.waitFor();

            return profesoresJson.toString().contains("\"dni\":\"" + dni + "\"");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
