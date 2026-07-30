package pl.klejczyk.tpm.machine.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitConfiguration {

    static final String EXCHANGE = "tpm.events";
    static final String WORKORDER_QUEUE = "q.machine.workorder";
    static final String WORKORDER_DLQ = "q.machine.workorder.dlq";

    @Bean
    TopicExchange tpmEvents() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    Queue workOrderDeadLetterQueue() {
        return QueueBuilder.durable(WORKORDER_DLQ).build();
    }

    @Bean
    Queue workOrderQueue() {
        return QueueBuilder.durable(WORKORDER_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(WORKORDER_DLQ)
                .build();
    }

    @Bean
    Binding workOrderStartedBinding() {
        return BindingBuilder.bind(workOrderQueue()).to(tpmEvents()).with("workorder.started");
    }

    @Bean
    Binding workOrderResolvedBinding() {
        return BindingBuilder.bind(workOrderQueue()).to(tpmEvents()).with("workorder.resolved");
    }

}