package com.example.a30467984.deaddyspy.utils;

/**
 * Created by 30467984 on 4/8/2019.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.example.a30467984.deaddyspy.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mailing extends Activity {
    public String jobNo;
    public String teamNo;
    private static final String username = "sergei.libster@gmail.com";
    private static final String password = "ser1ver1";
    private static final String emailid = "alert@daddyspy.com";
    private static final String subject = "Photo";
    private static final String message = "Hello";
    private Multipart multipart = new MimeMultipart();
    private MimeBodyPart messageBodyPart = new MimeBodyPart();
    public File mediaFile;
    private Context context;
    private Activity activity;

    public Mailing(Context context){
        this.context = context;
     //   this.activity = activity;
    }


    public void sendMail(String email, String subject, String messageBody) {

        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Properties readPropertiesFromFile(Context context) {

        //reads the configuration file
        PropertiesReader propertiesReader = new PropertiesReader(context);
        Properties p=propertiesReader.getProperties("config.properties");

        //recovery of the parameters
//        ip_address = p.getProperty("ip");
//        hostname = p.getProperty("host");
//        port = p.getProperty("port");
        return p;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        final Properties confProperties = readPropertiesFromFile(context);
        final String username = confProperties.getProperty("username");
        final String password = confProperties.getProperty("password");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private Message createMessage(String email, String subject, String messageBody,Session session) throws
            MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("notification@daddyspy.com", "Deaddy Spy"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }


    public class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = ProgressDialog.show(Mailing.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressDialog.dismiss();
        }

        protected Void doInBackground(javax.mail.Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
