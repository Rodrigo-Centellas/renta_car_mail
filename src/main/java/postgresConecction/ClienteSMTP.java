package postgresConecction;

import java.io.*;
import java.net.*;

public class ClienteSMTP {

    public static void main(String[] args) {
        String servidor = "mail.tecnoweb.org.bo";
        String user_receptor = "grupo20sa@tecnoweb.org.bo";
        String user_emisor = "evansbalcazar@uagrm.edu.bo";
        String comando;
        int puerto = 25;

        try {
            System.out.println("🔗 Conectando a " + servidor + ":" + puerto + "...");
            Socket socket = new Socket(servidor, puerto);
            socket.setSoTimeout(30000); // Timeout de 30 segundos

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            // Leer saludo inicial del servidor
            String respuestaInicial = entrada.readLine();
            System.out.println("S: " + respuestaInicial);

            if (respuestaInicial == null || !respuestaInicial.startsWith("220")) {
                System.err.println("❌ Error: El servidor no respondió correctamente");
                return;
            }

            // HELO
            comando = "HELO " + "cliente.test" + "\r\n";
            System.out.print("C: " + comando);
            salida.writeBytes(comando);
            salida.flush();

            String respuestaHelo = entrada.readLine();
            System.out.println("S: " + respuestaHelo);

            if (respuestaHelo == null || !respuestaHelo.startsWith("250")) {
                System.err.println("❌ Error en HELO");
                return;
            }

            // MAIL FROM
            comando = "MAIL FROM:<" + user_emisor + ">\r\n";
            System.out.print("C: " + comando);
            salida.writeBytes(comando);
            salida.flush();

            String respuestaFrom = entrada.readLine();
            System.out.println("S: " + respuestaFrom);

            if (respuestaFrom == null || !respuestaFrom.startsWith("250")) {
                System.err.println("❌ Error en MAIL FROM");
                return;
            }

            // RCPT TO
            comando = "RCPT TO:<" + user_receptor + ">\r\n";
            System.out.print("C: " + comando);
            salida.writeBytes(comando);
            salida.flush();

            String respuestaTo = entrada.readLine();
            System.out.println("S: " + respuestaTo);

            if (respuestaTo == null || !respuestaTo.startsWith("250")) {
                System.err.println("❌ Error en RCPT TO");
                return;
            }

            // DATA
            comando = "DATA\r\n";
            System.out.print("C: " + comando);
            salida.writeBytes(comando);
            salida.flush();

            String respuestaData = entrada.readLine();
            System.out.println("S: " + respuestaData);

            if (respuestaData == null || !respuestaData.startsWith("354")) {
                System.err.println("❌ Error en DATA");
                return;
            }

            // MENSAJE COMPLETO
            String mensaje = "From: " + user_emisor + "\r\n" +
                    "To: " + user_receptor + "\r\n" +
                    "Subject: help get()\r\n" +
                    "\r\n" +
                    "Comando de prueba desde SMTP.\r\n" +
                    ".\r\n";

            System.out.print("C: [enviando mensaje...]\n");
            salida.writeBytes(mensaje);
            salida.flush();

            String respuestaMensaje = entrada.readLine();
            System.out.println("S: " + respuestaMensaje);

            // QUIT
            comando = "QUIT\r\n";
            System.out.print("C: " + comando);
            salida.writeBytes(comando);
            salida.flush();

            String respuestaQuit = entrada.readLine();
            System.out.println("S: " + respuestaQuit);

            salida.close();
            entrada.close();
            socket.close();

            System.out.println("✅ Email enviado exitosamente!");

        } catch (UnknownHostException e) {
            System.err.println("❌ No se puede conectar al servidor: " + servidor);
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            System.err.println("❌ Timeout: El servidor no responde");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Error de comunicación con el servidor");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado");
            e.printStackTrace();
        }
    }

    static protected String getMultiline(BufferedReader in) throws IOException {
        String lines = "";
        String line;

        while ((line = in.readLine()) != null) {
            lines += "\n" + line;
            if (line.length() >= 4 && line.charAt(3) == ' ') {
                break;
            }
        }

        if (line == null) {
            throw new IOException("Server cerró conexión inesperadamente");
        }

        return lines;
    }
}