package librerias;

import java.util.List;

/**
 * HtmlRes genera contenido HTML con estilos actualizados para tablas y textos,
 * manteniendo la misma funcionalidad de generación.
 */
public class HtmlRes {
    private static final String HTML_OPEN = "<!DOCTYPE html><html lang=\"es\">";
    private static final String HTML_CLOSE = "</html>";
    private static final String HEAD_STYLE =
            "<head>"
                    + "  <meta charset=\"UTF-8\">"
                    + "  <style>"
                    + "    body { font-family: Arial, sans-serif; margin: 0; padding: 1em; background: #f9f9f9; }"
                    + "    .report-table {"
                    + "      border-collapse: collapse;"
                    + "      width: 90%;"
                    + "      margin: 1em auto;"
                    + "      box-shadow: 0 2px 5px rgba(0,0,0,0.1);"
                    + "      background: #fff;"
                    + "    }"
                    + "    .report-table th, .report-table td {"
                    + "      border: 1px solid #ddd;"
                    + "      padding: 0.75em 1em;"
                    + "      text-align: center;"
                    + "    }"
                    + "    .report-table th {"
                    + "      background-color: #4CAF50;"
                    + "      color: white;"
                    + "      font-weight: normal;"
                    + "    }"
                    + "    .report-table tr:nth-child(even) {"
                    + "      background-color: #f2f2f2;"
                    + "    }"
                    + "    .report-caption {"
                    + "      caption-side: top;"
                    + "      text-align: center;"
                    + "      font-size: 1.5em;"
                    + "      margin-bottom: 0.5em;"
                    + "      color: #333;"
                    + "    }"
                    + "    .text-block {"
                    + "      width: 90%;"
                    + "      margin: 1em auto;"
                    + "      text-align: center;"
                    + "      color: #333;"
                    + "    }"
                    + "    .text-block h2 { font-size: 1.8em; margin: 0.5em 0; }"
                    + "    .text-block h3 { font-size: 1.2em; margin: 0.3em 0; }"
                    + "  </style>"
                    + "</head>";

    /**
     * Genera una tabla con título, encabezados y datos.
     */
    public static String generateTable(String title, String[] headers, List<String[]> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"report-table\">");
        sb.append("<caption class=\"report-caption\">").append(title).append("</caption>");
        sb.append("<thead><tr>");
        for (String header : headers) {
            sb.append("<th>").append(header).append("</th>");
        }
        sb.append("</tr></thead>");
        sb.append("<tbody>");
        for (String[] row : data) {
            sb.append("<tr>");
            for (String cell : row) {
                sb.append("<td>").append(cell).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return wrapHtml(sb.toString());
    }

    /**
     * Genera bloques de texto centrado (por ejemplo mensajes de éxito o error).
     */
    public static String generateText(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"text-block\">");
        if (args.length > 0) {
            sb.append("<h2>").append(args[0]).append("</h2>");
            for (int i = 1; i < args.length; i++) {
                sb.append("<h3>").append(args[i]).append("</h3>");
            }
        }
        sb.append("</div>");
        return wrapHtml(sb.toString());
    }

    /**
     * Genera una tabla simple de dos columnas (cabecera-dato).
     */
    public static String generateTableForSimpleData(String title, String[] headers, String[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"report-table\">");
        sb.append("<caption class=\"report-caption\">").append(title).append("</caption>");
        sb.append("<tbody>");
        for (int i = 0; i < headers.length; i++) {
            sb.append("<tr>");
            sb.append("<th>").append(headers[i]).append("</th>");
            sb.append("<td>").append(data[i]).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return wrapHtml(sb.toString());
    }

    /**
     * Envuelve el contenido dado dentro de la estructura HTML y HEAD con estilos.
     */
    private static String wrapHtml(String bodyContent) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_OPEN)
                .append(HEAD_STYLE)
                .append("<body>")
                .append(bodyContent)
                .append("</body>")
                .append(HTML_CLOSE);
        return sb.toString();
    }
}
