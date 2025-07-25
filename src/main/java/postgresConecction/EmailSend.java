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
 *

 */
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
        //this.email.setFrom(MAIL);
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        //properties.put("mail.transport.protocol", PROTOCOL);
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        properties.setProperty("mail.smtp.tls.enable", "true");//cuando user tecnoweb
        properties.setProperty("mail.smtp.ssl.enable", "*");// cuando usen Gmail
        properties.setProperty("mail.smtp.auth", "false");// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody

        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, MAIL_PASSWORD);
            }
        });

        try {
            MimeMessage message;
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo())};

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
        } catch (NoSuchProviderException | AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
    }

}
