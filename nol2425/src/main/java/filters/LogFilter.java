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
        ServletContext context = config.getServletContext();
        logPath = context.getInitParameter("logFilePath");
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

        if (session != null &&
            session.getAttribute("dni") != null &&
            session.getAttribute("key") != null &&
            session.getAttribute("cookieCentro") != null) {

            String dni = (String) session.getAttribute("dni");
            String key = (String) session.getAttribute("key");
            String cookie = (String) session.getAttribute("cookieCentro");

            String url = "http://localhost:9090/CentroEducativo/alumnos/" + dni + "?key=" + key;

            String[] command = {
                "curl",
                "-X", "GET",
                url,
                "-H", "accept: application/json",
                "-H", "Cookie: " + cookie
            };

            StringBuilder jsonResult = new StringBuilder();
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResult.append(line);
                    }
                }

                process.waitFor();
                String json = jsonResult.toString();

                int nombreIndex = json.indexOf("\"nombre\":\"");
                if (nombreIndex != -1) {
                    int start = nombreIndex + 10;
                    int end = json.indexOf("\"", start);
                    if (end > start) {
                        String nombre = json.substring(start, end);
                        usuario = nombre;
                        
                        
                        //añadido por pablo
                        session.setAttribute("nombreAlumno", nombre);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String ip = request.getRemoteAddr();
        String uri = httpRequest.getRequestURI();
        String metodo = httpRequest.getMethod();
        String log = String.format("%s %s %s %s %s%n", LocalDateTime.now(), usuario, ip, uri, metodo);

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

