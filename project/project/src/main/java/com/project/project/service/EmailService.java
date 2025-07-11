package com.project.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.project.project.exception.EmailFaliureException;
import com.project.project.model.VerificationToken;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage (){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
    return simpleMailMessage;
    }

    public void sendEmail(VerificationToken verificationToken) throws EmailFaliureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify Your Email to Complete Registration");
        String text = """
                      Please click the link below to verify your email address:
                      /auth/verify?token=""" + verificationToken.getToken();
        message.setText(text);
        //message.setText("please click the link below to verify your email address:\n"+
       // url + "/auth/verify?token=" + verificationToken.getToken());
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new EmailFaliureException();
    }
}
}