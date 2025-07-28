package postgresConecction;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import librerias.Email;

public class EmailSend implements Runnable {

    private final static String PORT_SMTP = "25";
    private final static String PROTOCOL = "smtp";
    private final static String HOST = "mail.tecnoweb.org.bo";
    private final static String USER = "grupo20sa";
    private final static String PASSWORD = "grup020grup020*";
    private final static String MAIL = "grupo20sa@tecnoweb.org.bo";
    private final static String MAIL_PASSWORD = "grup020grup020*";

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
        System.out.println("📧 [SEND-LOG] EmailSend inicializado");
        System.out.println("📍 [SEND-LOG] Destinatario: " + (emailP != null ? emailP.getTo() : "NULL"));
        System.out.println("📝 [SEND-LOG] Subject: " + (emailP != null ? emailP.getSubject() : "NULL"));
    }

    @Override
    public void run() {
        System.out.println("\n🚀 [SEND-LOG] ========== INICIANDO ENVÍO DE EMAIL ==========");
        System.out.println("📊 [SEND-LOG] Configuración del servidor:");
        System.out.println("    HOST: " + HOST);
        System.out.println("    PUERTO: " + PORT_SMTP);
        System.out.println("    PROTOCOLO: " + PROTOCOL);
        System.out.println("    USUARIO: " + USER);
        System.out.println("    EMAIL ORIGEN: " + MAIL);

        // Validación inicial del email
        if (email == null) {
            System.err.println("❌ [SEND-ERROR] Email object es NULL");
            return;
        }

        if (email.getTo() == null || email.getTo().trim().isEmpty()) {
            System.err.println("❌ [SEND-ERROR] Destinatario es NULL o vacío");
            return;
        }

        System.out.println("✅ [SEND-LOG] Email object validado correctamente");
        System.out.println("📧 [SEND-LOG] Detalles del email a enviar:");
        System.out.println("    PARA: " + email.getTo());
        System.out.println("    SUBJECT: " + email.getSubject());
        System.out.println("    MENSAJE (primeros 100 chars): " +
                (email.getMessage() != null ?
                        email.getMessage().substring(0, Math.min(100, email.getMessage().length())) :
                        "NULL"));

        // Configuración de propiedades
        System.out.println("🔧 [SEND-LOG] Configurando propiedades SMTP...");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        properties.setProperty("mail.smtp.tls.enable", "true");
        properties.setProperty("mail.smtp.ssl.enable", "*");
        properties.setProperty("mail.smtp.auth", "false");

        System.out.println("📋 [SEND-LOG] Propiedades SMTP configuradas:");
        properties.forEach((key, value) ->
                System.out.println("    " + key + " = " + value));

        // Crear sesión
        System.out.println("🔐 [SEND-LOG] Creando sesión SMTP...");
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("🔑 [SEND-LOG] Authenticator solicitado - Usuario: " + USER);
                return new PasswordAuthentication(USER, MAIL_PASSWORD);
            }
        });

        // Habilitar debug de JavaMail si es necesario
        session.setDebug(false); // Cambiar a true para ver logs de JavaMail
        System.out.println("✅ [SEND-LOG] Sesión SMTP creada exitosamente");

        try {
            System.out.println("📝 [SEND-LOG] Creando mensaje MIME...");

            // Crear mensaje
            MimeMessage message = new MimeMessage(session);

            // Configurar remitente
            System.out.println("👤 [SEND-LOG] Configurando remitente: " + MAIL);
            message.setFrom(new InternetAddress(MAIL));

            // Configurar destinatario
            System.out.println("📮 [SEND-LOG] Configurando destinatario: " + email.getTo());
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo()) };
            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);

            // Configurar subject
            System.out.println("📋 [SEND-LOG] Configurando subject: " + email.getSubject());
            message.setSubject(email.getSubject());

            // Crear contenido multipart
            System.out.println("📄 [SEND-LOG] Creando contenido multipart HTML...");
            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);

            System.out.println("💾 [SEND-LOG] Guardando cambios del mensaje...");
            message.saveChanges();

            System.out.println("✅ [SEND-LOG] Mensaje MIME creado exitosamente");

            // Enviar email
            System.out.println("🚀 [SEND-LOG] Enviando email via Transport.send()...");
            long startTime = System.currentTimeMillis();

            Transport.send(message);

            long endTime = System.currentTimeMillis();
            System.out.println("✅ [SEND-SUCCESS] ¡EMAIL ENVIADO EXITOSAMENTE!");
            System.out.println("⏱️ [SEND-LOG] Tiempo de envío: " + (endTime - startTime) + "ms");
            System.out.println("📧 [SEND-LOG] Destinatario: " + email.getTo());
            System.out.println("📝 [SEND-LOG] Subject: " + email.getSubject());

        } catch (AddressException ex) {
            System.err.println("❌ [SEND-ERROR] Error de dirección de email:");
            System.err.println("    Tipo: AddressException");
            System.err.println("    Mensaje: " + ex.getMessage());
            System.err.println("    Email destinatario: " + email.getTo());
            System.err.println("    Email remitente: " + MAIL);

            // Validar formato de email
            String emailTo = email.getTo();
            if (emailTo != null) {
                if (!emailTo.contains("@")) {
                    System.err.println("🔍 [SEND-DEBUG] Email no contiene '@'");
                } else if (!emailTo.contains(".")) {
                    System.err.println("🔍 [SEND-DEBUG] Email no contiene '.' en el dominio");
                } else if (emailTo.startsWith("@") || emailTo.endsWith("@")) {
                    System.err.println("🔍 [SEND-DEBUG] Email inicia o termina con '@'");
                } else {
                    System.err.println("🔍 [SEND-DEBUG] Formato parece correcto, error interno");
                }
            }

            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);

        } catch (NoSuchProviderException ex) {
            System.err.println("❌ [SEND-ERROR] Proveedor SMTP no encontrado:");
            System.err.println("    Tipo: NoSuchProviderException");
            System.err.println("    Mensaje: " + ex.getMessage());
            System.err.println("    HOST configurado: " + HOST);
            System.err.println("    PUERTO configurado: " + PORT_SMTP);
            System.err.println("🔍 [SEND-DEBUG] Verificar configuración del servidor SMTP");

            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);

        } catch (MessagingException ex) {
            System.err.println("❌ [SEND-ERROR] Error de mensajería:");
            System.err.println("    Tipo: MessagingException");
            System.err.println("    Mensaje: " + ex.getMessage());
            System.err.println("    Causa: " + (ex.getCause() != null ? ex.getCause().getMessage() : "Desconocida"));

            // Análisis específico de errores comunes
            String mensaje = ex.getMessage().toLowerCase();
            if (mensaje.contains("connection refused")) {
                System.err.println("🔍 [SEND-DEBUG] Conexión rechazada - verificar HOST y PUERTO");
                System.err.println("🔍 [SEND-DEBUG] ¿El servidor " + HOST + ":" + PORT_SMTP + " está activo?");
            } else if (mensaje.contains("authentication")) {
                System.err.println("🔍 [SEND-DEBUG] Error de autenticación - verificar credenciales");
                System.err.println("🔍 [SEND-DEBUG] Usuario: " + USER);
                System.err.println("🔍 [SEND-DEBUG] Contraseña length: " + MAIL_PASSWORD.length());
            } else if (mensaje.contains("timeout")) {
                System.err.println("🔍 [SEND-DEBUG] Timeout - el servidor no responde a tiempo");
            } else if (mensaje.contains("relay")) {
                System.err.println("🔍 [SEND-DEBUG] Error de relay - servidor no permite relay");
            } else if (mensaje.contains("tls") || mensaje.contains("ssl")) {
                System.err.println("🔍 [SEND-DEBUG] Error de encriptación TLS/SSL");
                System.err.println("🔍 [SEND-DEBUG] TLS habilitado: true");
                System.err.println("🔍 [SEND-DEBUG] SSL habilitado: *");
            } else {
                System.err.println("🔍 [SEND-DEBUG] Error no categorizado");
            }

            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("📋 [SEND-DEBUG] Stack trace completo:");
            ex.printStackTrace();

        } catch (Exception ex) {
            System.err.println("❌ [SEND-ERROR] Error inesperado:");
            System.err.println("    Tipo: " + ex.getClass().getSimpleName());
            System.err.println("    Mensaje: " + ex.getMessage());
            System.err.println("📋 [SEND-DEBUG] Stack trace completo:");
            ex.printStackTrace();
        }

        System.out.println("🏁 [SEND-LOG] ========== FIN ENVÍO DE EMAIL ==========\n");
    }
}