package filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
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

        String usuario = (httpRequest.getRemoteUser() != null) ? httpRequest.getRemoteUser() : "anonimo";
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
