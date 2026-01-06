package com.viddefe.viddefe_api.notifications.Infrastructure.email;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Path;

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
    public void send(@Valid NotificationDto notificationDto) {
        sendInternal(notificationDto, null);
    }

    @Override
    public void sendWithAttachment(@Valid NotificationDto dto,@NotNull Path attachment) {

        if (attachment == null || attachment.toString().isBlank()) {
            throw new IllegalArgumentException("Attachment path must not be null");
        }

        sendInternal(dto, attachment);
    }

    private void sendInternal(NotificationDto dto, Path attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, attachment != null, "UTF-8");

            // Render HTML
            Context context = new Context();
            context.setVariables(dto.getVariables());
            String html = templateEngine.process(dto.getTemplate(), context);

            helper.setTo(dto.getTo());
            helper.setSubject(resolveSubject(dto.getTemplate()));
            helper.setText(html, true);

            if (attachment != null) {
                helper.addAttachment(
                        attachment.getFileName().toString(),
                        new FileSystemResource(attachment)
                );
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    /**
     * Simple subject resolver.
     * Later this can move to a SubjectResolver or enum-based strategy.
     */
    private String resolveSubject(String template) {
        if (template.contains("invitation")) {
            return "You're invited to Viddefe";
        }
        if (template.contains("reset")) {
            return "Reset your password";
        }
        return "Viddefe Notification";
    }
}
