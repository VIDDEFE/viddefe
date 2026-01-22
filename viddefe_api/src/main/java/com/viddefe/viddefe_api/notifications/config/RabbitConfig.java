package com.viddefe.viddefe_api.notifications.config;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.RabbitQueues;
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
}
