package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.*;
import java.util.*;

@WebServlet("/GenerarPDFServlet")
public class GenerarPDFServlet extends HttpServlet {
    @Override
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
            "curl", "-X", "GET", url,
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
                System.err.println("curl falló con código: " + exitCode);
                
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        System.err.println("stderr: " + errorLine);
                    }
                }

                response.sendError(500, "Error ejecutando curl");
                return;
            }
            System.out.println("Comando curl: " + String.join(" ", command));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Excepción al ejecutar curl");
            return;
        }

        // Procesar JSON plano
        String json = resultado.toString().trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }

        String[] items = json.split("\\},\\{");

        Map<String, String> asignaturasNotas = new LinkedHashMap<>();

        for (String item : items) {
            item = item.replace("{", "").replace("}", "");
            String[] partes = item.split(",");

            String asignatura = "", nota = "";

            for (String parte : partes) {
                String[] keyValue = parte.split(":");
                if (keyValue.length == 2) {
                    String clave = keyValue[0].replaceAll("\"", "").trim();
                    String valor = keyValue[1].replaceAll("\"", "").trim();

                    if (clave.equals("asignatura")) {
                        asignatura = valor;
                    } else if (clave.equals("nota")) {
                        nota = valor;
                    }
                }
            }

            if (!asignatura.isEmpty()) {
                asignaturasNotas.put(asignatura, nota);
            }
        }

        // Generar PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"informe_notas.pdf\"");

        try (OutputStream out = response.getOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Informe Académico del Alumno " + dni).setBold().setFontSize(16));
            document.add(new Paragraph("\n"));

            for (Map.Entry<String, String> entry : asignaturasNotas.entrySet()) {
                document.add(new Paragraph("Asignatura: " + entry.getKey()));
                document.add(new Paragraph("Nota: " + (entry.getValue().isEmpty() ? "Sin calificar" : entry.getValue())));
                document.add(new Paragraph("-----------------------------"));
            }

            document.close();
        }
    }
}
