//package postgresConecction;
//
//import interfaces.IEmailListener;
//import java.net.*;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.security.sasl.AuthenticationException;
//import librerias.Email;
//
//public class EmailReceipt implements Runnable {
//
//    static final String HOST = "mail.tecnoweb.org.bo";
//    private final static int PORT_POP = 110;
//    private final static String USER = "grupo20sa";
//    private final static String PASSWORD = "grup020grup020*";
//
//    private Socket socket;
//    private BufferedReader input;
//    private DataOutputStream output;
//
//    private IEmailListener emailListener;
//
//    public IEmailListener getEmailListener() {
//        return emailListener;
//    }
//
//    public void setEmailListener(IEmailListener emailListener) {
//        this.emailListener = emailListener;
//    }
//
//    public EmailReceipt(Socket socket, BufferedReader input, DataOutputStream output) {
//        this.socket = null;
//        this.input = null;
//        this.output = null;
//    }
//
//    public EmailReceipt() {
//    }
//
//    @Override
//    public void run() {
//        System.out.println("🔗 [LOG] Iniciando conexión a servidor POP3...");
//        System.out.println("📍 [LOG] HOST: " + HOST + ", PUERTO: " + PORT_POP);
//        System.out.println("👤 [LOG] USUARIO: " + USER);
//
//        while (true) {
//            try {
//                System.out.println("\n⏳ [LOG] Intentando conectar socket...");
//                this.socket = new Socket(HOST, PORT_POP);
//                System.out.println("✅ [LOG] Socket conectado exitosamente");
//
//                List<Email> emails = null;
//
//                // Establecer streams
//                input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
//                output = new DataOutputStream(socket.getOutputStream());
//                System.out.println("🔧 [LOG] Streams de entrada y salida configurados");
//
//                System.out.println("_________ Conexion establecida __________");
//
//                // Autenticación
//                System.out.println("🔐 [LOG] Iniciando proceso de autenticación...");
//                authUser(USER, PASSWORD);
//                System.out.println("✅ [LOG] Autenticación exitosa");
//
//                // Obtener cantidad de emails
//                System.out.println("📊 [LOG] Obteniendo cantidad de emails...");
//                int cant = this.getEmailCount();
//                System.out.println("📧 [LOG] Emails encontrados: " + cant);
//
//                if (cant > 0) {
//                    System.out.println("📥 [LOG] Descargando " + cant + " emails...");
//                    emails = this.getEmails(cant);
//                    System.out.println("✅ [LOG] Emails descargados exitosamente");
//
//                    // Log detallado de cada email
//                    for (int i = 0; i < emails.size(); i++) {
//                        Email email = emails.get(i);
//                        if (email != null) {
//                            System.out.println("📨 [LOG] Email " + (i+1) + ":");
//                            System.out.println("    FROM: " + email.getFrom());
//                            System.out.println("    SUBJECT: " + email.getSubject());
//                        } else {
//                            System.out.println("⚠️ [LOG] Email " + (i+1) + " es NULL");
//                        }
//                    }
//
//                    System.out.println("🗑️ [LOG] Eliminando emails del servidor...");
//                    this.deleteEmails(cant);
//                    System.out.println("✅ [LOG] Emails eliminados del servidor");
//                } else {
//                    System.out.println("📭 [LOG] No hay emails nuevos");
//                }
//
//                // Cerrar conexión POP3
//                System.out.println("👋 [LOG] Enviando comando QUIT...");
//                output.writeBytes("QUIT \r\n");
//                String quitResponse = input.readLine();
//                System.out.println("📤 [LOG] Respuesta QUIT: " + quitResponse);
//
//                input.close();
//                output.close();
//                socket.close();
//                System.out.println("__________ Conexion cerrada ___________");
//
//                // Procesar emails si los hay
//                if (cant > 0 && emails != null) {
//                    System.out.println("🔄 [LOG] Enviando " + emails.size() + " emails al listener...");
//                    this.emailListener.onReceiptEmail(emails);
//                    System.out.println("✅ [LOG] Emails enviados al listener para procesamiento");
//                }
//
//            } catch (AuthenticationException e) {
//                System.err.println("❌ [ERROR] Fallo de autenticación: " + e.getMessage());
//                System.err.println("🔍 [DEBUG] Verificar usuario: " + USER);
//                System.err.println("🔍 [DEBUG] Verificar contraseña (longitud): " + PASSWORD.length() + " caracteres");
//            } catch (IOException e) {
//                System.err.println("❌ [ERROR] Error de E/S: " + e.getMessage());
//                System.err.println("🔍 [DEBUG] Tipo de error: " + e.getClass().getSimpleName());
//                if (e.getMessage().contains("Connection refused")) {
//                    System.err.println("🔍 [DEBUG] El servidor rechazó la conexión. Verificar HOST y PUERTO");
//                } else if (e.getMessage().contains("timeout")) {
//                    System.err.println("🔍 [DEBUG] Timeout de conexión. El servidor puede estar lento");
//                }
//            } catch (Exception e) {
//                System.err.println("❌ [ERROR] Error inesperado: " + e.getMessage());
//                e.printStackTrace();
//            }
//
//            System.out.println("💤 [LOG] Desconectado del servidor. Esperando 5 segundos...");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException ex) {
//                System.err.println("❌ [ERROR] Interrupción del hilo: " + ex.getMessage());
//                Logger.getLogger(EmailReceipt.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//    private void deleteEmails(int emails) throws IOException {
//        System.out.println("🗑️ [LOG] Iniciando eliminación de " + emails + " emails...");
//        for (int i = 1; i <= emails; i++) {
//            System.out.println("🗑️ [LOG] Eliminando email " + i + "...");
//            output.writeBytes("DELE " + i + "\r\n");
//            // No leemos respuesta para DELE, algunos servidores no la envían inmediatamente
//        }
//        System.out.println("✅ [LOG] Comandos DELE enviados para todos los emails");
//    }
//
//    private void authUser(String email, String password) throws IOException {
//        if (socket != null && input != null && output != null) {
//            // Leer mensaje de bienvenida
//            System.out.println("👋 [LOG] Leyendo mensaje de bienvenida...");
//            String welcome = input.readLine();
//            System.out.println("📨 [LOG] Servidor dice: " + welcome);
//
//            // Enviar USER
//            System.out.println("👤 [LOG] Enviando comando USER...");
//            output.writeBytes("USER " + email + "\r\n");
//            String userResponse = input.readLine();
//            System.out.println("📨 [LOG] Respuesta USER: " + userResponse);
//
//            // Enviar PASS
//            System.out.println("🔑 [LOG] Enviando comando PASS...");
//            output.writeBytes("PASS " + password + "\r\n");
//            String passResponse = input.readLine();
//            System.out.println("📨 [LOG] Respuesta PASS: " + passResponse);
//
//            if (passResponse.contains("-ERR")) {
//                System.err.println("❌ [ERROR] Autenticación fallida: " + passResponse);
//                throw new AuthenticationException("Error de autenticación: " + passResponse);
//            }
//
//            System.out.println("✅ [LOG] Autenticación completada exitosamente");
//        } else {
//            System.err.println("❌ [ERROR] Socket o streams son null en authUser");
//            throw new IOException("Socket o streams no inicializados");
//        }
//    }
//
//    private int getEmailCount() throws IOException {
//        System.out.println("📊 [LOG] Enviando comando STAT...");
//        output.writeBytes("STAT \r\n");
//        String line = input.readLine();
//        System.out.println("📨 [LOG] Respuesta STAT: " + line);
//
//        // Parsear respuesta: +OK 14 6410
//        String[] data = line.split(" ");
//        if (data.length >= 2) {
//            try {
//                int count = Integer.parseInt(data[1]);
//                System.out.println("📧 [LOG] Cantidad de emails parseada: " + count);
//                return count;
//            } catch (NumberFormatException e) {
//                System.err.println("❌ [ERROR] No se pudo parsear cantidad de emails: " + line);
//                throw new IOException("Respuesta STAT inválida: " + line);
//            }
//        } else {
//            System.err.println("❌ [ERROR] Respuesta STAT malformada: " + line);
//            throw new IOException("Respuesta STAT malformada: " + line);
//        }
//    }
//
//    private List<Email> getEmails(int count) throws IOException {
//        List<Email> emails = new ArrayList<>();
//        System.out.println("📥 [LOG] Iniciando descarga de " + count + " emails...");
//
//        for (int i = 1; i <= count; i++) {
//            try {
//                System.out.println("📧 [LOG] Descargando email " + i + " de " + count + "...");
//                output.writeBytes("RETR " + i + "\r\n");
//                String text = readMultiline();
//
//                System.out.println("📄 [LOG] Email " + i + " texto crudo (primeros 200 chars):");
//                System.out.println("    " + text.substring(0, Math.min(200, text.length())).replace("\n", "\\n"));
//
//                Email email = Email.getEmail(text);
//                if (email != null) {
//                    System.out.println("✅ [LOG] Email " + i + " procesado correctamente");
//                    System.out.println("    FROM: " + email.getFrom());
//                    System.out.println("    SUBJECT: " + email.getSubject());
//                } else {
//                    System.err.println("⚠️ [WARNING] Email " + i + " no se pudo procesar (resultado null)");
//                }
//
//                emails.add(email);
//
//            } catch (Exception e) {
//                System.err.println("❌ [ERROR] Error procesando email " + i + ": " + e.getMessage());
//                e.printStackTrace();
//                emails.add(null); // Agregar null para mantener consistencia
//            }
//        }
//
//        System.out.println("✅ [LOG] Descarga completada. Emails válidos: " +
//                emails.stream().mapToInt(e -> e != null ? 1 : 0).sum() +
//                " de " + count);
//        return emails;
//    }
//
//    private String readMultiline() throws IOException {
//        System.out.println("📖 [LOG] Leyendo respuesta multilinea...");
//        String lines = "";
//        int lineCount = 0;
//
//        while (true) {
//            String line = input.readLine();
//            lineCount++;
//
//            if (line == null) {
//                System.err.println("❌ [ERROR] Servidor cerró conexión inesperadamente en línea " + lineCount);
//                throw new IOException("Server no responde (error al abrir el correo)");
//            }
//
//            if (line.equals(".")) {
//                System.out.println("✅ [LOG] Fin de mensaje multilinea alcanzado (líneas leídas: " + lineCount + ")");
//                break;
//            }
//
//            // Log cada 50 líneas para no saturar
//            if (lineCount % 50 == 0) {
//                System.out.println("📖 [LOG] Líneas leídas: " + lineCount);
//            }
//
//            lines = lines + "\n" + line;
//        }
//
//        System.out.println("📝 [LOG] Respuesta multilinea completa (total caracteres: " + lines.length() + ")");
//        return lines;
//    }
//}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import interfaces.IEmailListener;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthenticationException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import librerias.Email;

/**
 * EmailReceipt configurado para Gmail con SSL
 */
public class EmailReceipt implements Runnable {

    // Configuración para Gmail POP3 con SSL
    static final String HOST = "pop.gmail.com";
    private final static int PORT_POP = 995; // Puerto SSL para Gmail
    private final static String USER = "rodrigodev06@gmail.com";
    private final static String PASSWORD = "eyqh bfls noyl irvp";

    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    private IEmailListener emailListener;

    public IEmailListener getEmailListener() {
        return emailListener;
    }

    public void setEmailListener(IEmailListener emailListener) {
        this.emailListener = emailListener;
    }

    public EmailReceipt(Socket socket, BufferedReader input, DataOutputStream output) {
        this.socket = null;
        this.input = null;
        this.output = null;
    }

    public EmailReceipt() {
    }

    @Override
    public void run() {
        System.out.println(" C : Conectando a Gmail POP3 SSL <" + HOST + ":" + PORT_POP + ">");
        while (true) {
            try {
                // Crear conexión SSL para Gmail
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.socket = factory.createSocket(HOST, PORT_POP);

                List<Email> emails = null;

                // Configurar streams
                input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());

                System.out.println("_________ Conexión SSL establecida con Gmail __________");
                authUser(USER, PASSWORD);
                int cant = this.getEmailCount();

                if (cant > 0) {
                    System.out.println("📧 Encontrados " + cant + " emails nuevos");
                    emails = this.getEmails(cant);
                    System.out.println("📨 Emails procesados: " + emails.size());
                    this.deleteEmails(cant);
                } else {
                    System.out.println("📭 No hay emails nuevos");
                }

                output.writeBytes("QUIT \r\n");
                input.readLine();
                input.close();
                output.close();
                socket.close();
                System.out.println("__________ Conexión cerrada ___________");

                if (cant > 0 && emails != null) {
                    this.emailListener.onReceiptEmail(emails);
                }

            } catch (IOException e) {
                System.err.println("❌ Error de conexión: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("❌ Error general: " + e.getMessage());
            }

            System.out.println(" C : Desconectado de Gmail, esperando 5 segundos...");
            try {
                Thread.sleep(5000); // Esperar 30 segundos entre verificaciones
            } catch (InterruptedException ex) {
                Logger.getLogger(EmailReceipt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            output.writeBytes("DELE " + i + "\r\n");
        }
        System.out.println("🗑️ Eliminados " + emails + " emails del servidor");
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            // Leer mensaje de bienvenida
            String welcome = input.readLine();
            System.out.println("S: " + welcome);

            // Enviar USER
            output.writeBytes("USER " + email + "\r\n");
            String userResponse = input.readLine();
            System.out.println("S: " + userResponse);

            // Enviar PASSWORD
            output.writeBytes("PASS " + password + "\r\n");
            String passResponse = input.readLine();
            System.out.println("S: " + passResponse);

            if (passResponse.contains("-ERR")) {
                throw new AuthenticationException("Error de autenticación Gmail: " + passResponse);
            }

            System.out.println("✅ Autenticación exitosa con Gmail");
        }
    }

    private int getEmailCount() throws IOException {
        output.writeBytes("STAT \r\n");
        String line = input.readLine();
        System.out.println("S: " + line);

        // Respuesta esperada: +OK 14 6410 (emails count, total size)
        String[] data = line.split(" ");
        if (data.length >= 2) {
            return Integer.parseInt(data[1]);
        }
        return 0;
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            System.out.println("📩 Descargando email " + i + " de " + count);
            output.writeBytes("RETR " + i + "\r\n");
            String text = readMultiline();
            Email email = Email.getEmail(text);
            if (email != null) {
                emails.add(email);
            }
        }
        return emails;
    }

    private String readMultiline() throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = input.readLine();
            if (line == null) {
                throw new IOException("Servidor no responde (error al leer email)");
            }
            if (line.equals(".")) {
                break;
            }
            // Manejar líneas que empiezan con punto (escape del protocolo POP3)
            if (line.startsWith("..")) {
                line = line.substring(1);
            }
            lines.append("\n").append(line);
        }
        return lines.toString();
    }
}