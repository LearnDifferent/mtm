package com.github.learndifferent.mtm.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class SendEmailManager {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public SendEmailManager(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String content) {

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        mailSender.send(message);
    }
}
