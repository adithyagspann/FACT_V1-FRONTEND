/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Adithya
 */
public class MailProperty {

    private String fromMail, password, host, socketProperty, port, socketFactory_class, mail_smtp_auth;
    private Properties mailProperties, connectionProperties;
    private File mailPath;

    public MailProperty(String mailConfig) throws FileNotFoundException, IOException {
        mailProperties = new Properties();
        mailPath = new File(mailConfig);
//               FileInputStream fis = new FileInputStream(mailPath);
        mailProperties.load(new FileInputStream(mailPath));

        socketFactory_class = "javax.net.ssl.SSLSocketFactory"; //SSL Factory Class
        mail_smtp_auth = "true";
        connectionProperties = new Properties();
        connectionProperties.put("mail.smtp.port", mailProperties.getProperty("mail.smtp.port"));
        connectionProperties.put("mail.smtp.socketFactory.port", mailProperties.getProperty("mail.smtp.socketFactory.port"));
        connectionProperties.put("mail.smtp.host", mailProperties.getProperty("mail.smtp.host"));
        connectionProperties.put("mail.smtp.socketFactory.class", socketFactory_class);
        connectionProperties.put("mail.smtp.auth", mail_smtp_auth);
    }

    public void setFromMail(String fromMail) {
        mailProperties.setProperty("fromEmail", fromMail);
    }

    public void setPassword(String password) {
        mailProperties.setProperty("password", password);
    }

    public void setHost(String host) {
        mailProperties.setProperty("host", host);
    }

    public void setPort(String port) {
        mailProperties.setProperty("port", port);
    }

    public void setSocketPort(String socketPort) {
        mailProperties.setProperty("mail.smtp.socketFactory.port", socketPort);
    }

    public String getFromMail() {
        return mailProperties.getProperty("fromEmail");
    }

    public String getPassword() {
        return mailProperties.getProperty("password");
    }

    public String getHost() {
        return mailProperties.getProperty("mail.smtp.host");
    }

    public String getSocketProperty() {
        return mailProperties.getProperty("mail.smtp.socketFactory.port");
    }

    public String getPort() {
        return mailProperties.getProperty("mail.smtp.port");
    }

    public String getSocketFactory_class() {
        return socketFactory_class;
    }

    public String getMail_smtp_auth() {
        return mail_smtp_auth;
    }

    public Properties getMailProperties() {
        System.out.println("Properties: " + connectionProperties);
        return connectionProperties;
    }

    public void storeSMTp() throws FileNotFoundException, IOException {
        mailProperties.store(new FileOutputStream(mailPath), new Date().toString());

    }

}
