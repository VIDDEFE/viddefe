package com.viddefe.viddefe_api.config.rabbit;

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

}
