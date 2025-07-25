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