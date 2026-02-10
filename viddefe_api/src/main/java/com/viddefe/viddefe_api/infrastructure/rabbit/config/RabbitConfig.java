package com.viddefe.viddefe_api.infrastructure.rabbit.config;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /* ===============================
     *  Message serialization
     * =============================== */

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    /* ===============================
     *  Exchange
     * =============================== */

    @Bean
    public DirectExchange notificationsExchange() {
        return new DirectExchange(
                RabbitQueues.NOTIFICATIONS_EXCHANGE,
                true,   // durable
                false   // autoDelete
        );
    }

    @Bean
    public DirectExchange attendanceExchange() {
        return new DirectExchange(
                RabbitQueues.ATTENDANCE_EXCHANGE,
                true,
                false
        );
    }

    /* ===============================
     *  WhatsApp Resilient Exchanges
     * =============================== */

    @Bean
    public DirectExchange whatsappExchange() {
        return new DirectExchange(
                RabbitQueues.WHATSAPP_EXCHANGE,
                true,   // durable
                false   // autoDelete
        );
    }

    @Bean
    public DirectExchange whatsappDlx() {
        return new DirectExchange(
                RabbitQueues.WHATSAPP_DLX,
                true,   // durable
                false   // autoDelete
        );
    }


    /* ===============================
     *  Queues
     * =============================== */

    @Bean
    public Queue accountNotificationsQueue() {
        return QueueBuilder.durable(RabbitQueues.ACCOUNT_QUEUE)
                .withArgument("x-max-priority", 10)
                .build();
    }

    @Bean
    public Queue passwordNotificationsQueue() {
        return QueueBuilder.durable(RabbitQueues.PASSWORD_QUEUE)
                .withArgument("x-max-priority", 10)
                .build();
    }

    @Bean
    public Queue ministryNotificationsQueue() {
        return QueueBuilder.durable(RabbitQueues.MINISTRY_QUEUE)
                .withArgument("x-max-priority", 10)
                .build();
    }

    @Bean
    public Queue attendanceQualityQueue() {
        return QueueBuilder
                .durable(RabbitQueues.ATTENDANCE_QUALITY_QUEUE)
                .withArgument("x-max-priority", 10)
                .build();
    }

    /* ===============================
     *  WhatsApp Resilient Queues
     * =============================== */

    @Bean
    public Queue whatsappQueue() {
        return QueueBuilder.durable(RabbitQueues.WHATSAPP_QUEUE)
                .withArgument("x-max-priority", 10)
                .withArgument("x-dead-letter-exchange", RabbitQueues.WHATSAPP_DLX)
                .withArgument("x-dead-letter-routing-key", RabbitQueues.WHATSAPP_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue whatsappRetryQueue() {
        return QueueBuilder.durable(RabbitQueues.WHATSAPP_RETRY_QUEUE)
                .withArgument("x-message-ttl", 30000) // 30 seconds
                .withArgument("x-dead-letter-exchange", RabbitQueues.WHATSAPP_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitQueues.WHATSAPP_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue whatsappDlq() {
        return QueueBuilder.durable(RabbitQueues.WHATSAPP_DLQ)
                .build();
    }

    /* ===============================
     *  Bindings
     * =============================== */

    @Bean
    public Binding accountBinding(
            Queue accountNotificationsQueue,
            DirectExchange notificationsExchange
    ) {
        return BindingBuilder
                .bind(accountNotificationsQueue)
                .to(notificationsExchange)
                .with(NotificationTypeEnum.ACCOUNT_CREATED.routingKey());
    }

    @Bean
    public Binding passwordBinding(
            Queue passwordNotificationsQueue,
            DirectExchange notificationsExchange
    ) {
        return BindingBuilder
                .bind(passwordNotificationsQueue)
                .to(notificationsExchange)
                .with(NotificationTypeEnum.PASSWORD_RESET.routingKey());
    }

    @Bean
    public Binding ministryBinding(
            Queue ministryNotificationsQueue,
            DirectExchange notificationsExchange
    ) {
        return BindingBuilder
                .bind(ministryNotificationsQueue)
                .to(notificationsExchange)
                .with(NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER.routingKey());
    }

    @Bean
    public Binding attendanceQualityBinding(
            Queue attendanceQualityQueue,
            DirectExchange attendanceExchange
    ) {
        return BindingBuilder
                .bind(attendanceQualityQueue)
                .to(attendanceExchange)
                .with(AttendanceRoutingKey.RECALCULATE_ATTENDANCE_QUALITY.routingKey());
    }

    /* ===============================
     *  WhatsApp Resilient Bindings
     * =============================== */

    @Bean
    public Binding whatsappBinding(
            Queue whatsappQueue,
            DirectExchange whatsappExchange
    ) {
        return BindingBuilder
                .bind(whatsappQueue)
                .to(whatsappExchange)
                .with(RabbitQueues.WHATSAPP_ROUTING_KEY);
    }

    @Bean
    public Binding whatsappRetryBinding(
            Queue whatsappRetryQueue,
            DirectExchange whatsappExchange
    ) {
        return BindingBuilder
                .bind(whatsappRetryQueue)
                .to(whatsappExchange)
                .with(RabbitQueues.WHATSAPP_RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding whatsappDlqBinding(
            Queue whatsappDlq,
            DirectExchange whatsappDlx
    ) {
        return BindingBuilder
                .bind(whatsappDlq)
                .to(whatsappDlx)
                .with(RabbitQueues.WHATSAPP_DLQ_ROUTING_KEY);
    }

}
