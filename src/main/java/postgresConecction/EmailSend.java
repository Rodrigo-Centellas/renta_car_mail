/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 * EmailSend configurado para Gmail
 */
public class EmailSend implements Runnable {

    // Configuración para Gmail
    private final static String HOST = "smtp.gmail.com";
    private final static String PORT_SMTP = "587";
    private final static String USER = "rodrigodev06@gmail.com";
    private final static String PASSWORD = "eyqh bfls noyl irvp";
    private final static String MAIL = "rodrigodev06@gmail.com";

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.ssl.trust", HOST);

        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo()) };

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
            System.out.println("✅ Email enviado exitosamente a: " + email.getTo());

        } catch (NoSuchProviderException | AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("❌ Error de dirección de email: " + ex.getMessage());
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("❌ Error enviando email: " + ex.getMessage());
        }
    }
}