package com.viddefe.viddefe_api.notifications.config;

import com.viddefe.viddefe_api.notifications.common.RabbitQueues;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

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

    @Bean
    public DirectExchange notificationsExchange() {
        return new DirectExchange(
                RabbitQueues.NOTIFICATIONS_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(RabbitQueues.NOTIFICATIONS_QUEUE)
                .withArgument("x-max-priority", 10) // 🔥 priority queue
                .build();
    }

    @Bean
    public Binding notificationsBinding(
            Queue notificationsQueue,
            DirectExchange notificationsExchange
    ) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(notificationsExchange)
                .with(RabbitQueues.NOTIFICATIONS_ROUTING);
    }
}
