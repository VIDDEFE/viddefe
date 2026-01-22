package com.viddefe.viddefe_api.notifications.Infrastructure.email;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements Notificator {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public Channels channel() {
        return Channels.EMAIL;
    }

    @Override
    @Async
    public void send(@Valid NotificationDto dto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(dto.getVariables());

            String html = templateEngine.process(dto.getTemplate(), context);

            helper.setTo(dto.getTo());
            helper.setSubject(dto.getSubject());
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
