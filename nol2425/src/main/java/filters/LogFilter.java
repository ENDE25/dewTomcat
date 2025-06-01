package filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.time.LocalDateTime;

public class LogFilter implements Filter {
    private String logPath;

    @Override
    public void init(FilterConfig config) throws ServletException {
        logPath = config.getServletContext().getInitParameter("logFilePath");
        if (logPath == null || logPath.isEmpty()) {
            throw new ServletException("No se ha definido 'logFilePath' en web.xml");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        String usuario = "anonimo";
        String tipoUsuario = "desconocido";

        if (session != null && session.getAttribute("dni") != null
                && session.getAttribute("key") != null
                && session.getAttribute("cookieCentro") != null) {

            String dni = (String) session.getAttribute("dni");
            String key = (String) session.getAttribute("key");
            String cookie = (String) session.getAttribute("cookieCentro");

            // Determinar si es profesor o alumno por la ruta de acceso
            String requestURI = httpRequest.getRequestURI();
            boolean esProfesor = requestURI.contains("profesor");

            try {
                String url;
                if (esProfesor) {
                    url = "http://localhost:9090/CentroEducativo/profesores/" + dni + "?key=" + key;
                    tipoUsuario = "profesor";
                } else {
                    url = "http://localhost:9090/CentroEducativo/alumnos/" + dni + "?key=" + key;
                    tipoUsuario = "alumno";
                }

                String[] command = {
                    "curl",
                    "-X", "GET",
                    url,
                    "-H", "accept: application/json",
                    "-H", "Cookie: " + cookie
                };

                StringBuilder jsonResult = new StringBuilder();
                ProcessBuilder pb = new ProcessBuilder(command);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResult.append(line);
                    }
                }

                process.waitFor();
                String json = jsonResult.toString();

                // Extraer nombre (funciona para ambos JSONs)
                int nombreIndex = json.indexOf("\"nombre\":\"");
                if (nombreIndex != -1) {
                    int start = nombreIndex + 10;
                    int end = json.indexOf("\"", start);
                    if (end > start) {
                        String nombre = json.substring(start, end);
                        usuario = nombre;
                       
                        // Guardar en sesión con atributo específico
                        if (esProfesor) {
                            session.setAttribute("nombreProfesor", nombre);
                        } else {
                            session.setAttribute("nombreAlumno", nombre);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Log (ahora incluye tipo de usuario)
        String ip = request.getRemoteAddr();
        String uri = httpRequest.getRequestURI();
        String metodo = httpRequest.getMethod();
        String log = String.format("%s [%s] %s %s %s %s%n",
                LocalDateTime.now(), tipoUsuario, usuario, ip, uri, metodo);

        synchronized (this) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true))) {
                writer.write(log);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}