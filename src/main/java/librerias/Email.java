/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias;

import javax.mail.internet.MimeUtility;

/**
 *
 
 */
public class Email {
    public static final String SUBJECT = "Request response";
    private String from;
    private String to;
    private String subject;
    private String message;

    public Email() {
    }

    public Email(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public Email(String from, String subject) {
        this.from = from;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMesssage(String messsage) {
        this.message = messsage;
    }

    public static Email getEmail(String plain_text) {
        return new Email(getFrom(plain_text), getSubject(plain_text));
    }

    private static String getFrom(String plain_text) {
        String search = "Return-Path: <";
        int index_begin = plain_text.indexOf(search) + search.length();
        int index_end = plain_text.indexOf(">");
        return plain_text.substring(index_begin, index_end);
    }

    private static String getSubject(String plain_text) {
        try {
            String search = "Subject: ";
            int index = plain_text.indexOf(search);
            if (index == -1)
                return null;

            index += search.length();
            StringBuilder rawSubject = new StringBuilder();

            String[] lines = plain_text.substring(index).split("\n");

            // Primera línea del subject
            rawSubject.append(lines[0].trim());

            // Concatenar las siguientes líneas si son parte del Subject (espacio, tab o
            // codificación)
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.startsWith(" ") || line.startsWith("\t") || line.startsWith("=?")) {
                    rawSubject.append(line.trim());
                } else {
                    break;
                }
            }

            String subjectPart = rawSubject.toString().trim();

            // Quitar "RV:" si es una respuesta
            if (subjectPart.startsWith("RV:")) {
                subjectPart = subjectPart.substring(3).trim();
            }

            // Separar y decodificar partes MIME dentro del texto
            StringBuilder finalSubject = new StringBuilder();
            int start = 0;
            while (start < subjectPart.length()) {
                int mimeStart = subjectPart.indexOf("=?", start);
                if (mimeStart == -1) {
                    finalSubject.append(subjectPart.substring(start));
                    break;
                }

                finalSubject.append(subjectPart.substring(start, mimeStart));

                int mimeEnd = subjectPart.indexOf("?=", mimeStart);
                if (mimeEnd == -1) {
                    finalSubject.append(subjectPart.substring(mimeStart));
                    break;
                }

                String mimeChunk = subjectPart.substring(mimeStart, mimeEnd + 2);
                try {
                    finalSubject.append(MimeUtility.decodeText(mimeChunk));
                } catch (Exception e) {
                    finalSubject.append(mimeChunk);
                }

                start = mimeEnd + 2;
            }

            String decodedSubject = finalSubject.toString().trim();
            System.out.println("Subject extracted: " + decodedSubject);
            return decodedSubject;

        } catch (Exception e) {
            System.out.println("Error extracting subject: " + e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "[From: " + from + ", To: " + to + ", Subject: " + subject + ", Message: " + message + "]";
    }
}
