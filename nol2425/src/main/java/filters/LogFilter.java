package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@WebFilter("/*")
public class LogFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String user = httpRequest.getRemoteUser () != null ? httpRequest.getRemoteUser () : "Guest";
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        LocalDateTime now = LocalDateTime.now();

        // Aquí se puede registrar en un archivo o base de datos
        System.out.println(now + " - " + user + " accedió a " + uri + " con método " + method);

        chain.doFilter(request, response);
    }
}