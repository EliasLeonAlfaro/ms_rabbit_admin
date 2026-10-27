package com.pedidos670.ms_rabbit_admin.service;

import com.pedidos670.ms_rabbit_admin.dto.CreateBindingRequest;
import com.pedidos670.ms_rabbit_admin.dto.CreateExchangeRequest;
import com.pedidos670.ms_rabbit_admin.dto.CreateQueueRequest;
import com.pedidos670.ms_rabbit_admin.exception.RabbitAdminOperationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public void createQueue(CreateQueueRequest request) {

        String queueName = normalizeName(
                request.getName(),
                "cola"
        );

        Queue queue = new Queue(
                queueName,
                request.isDurable(),
                request.isExclusive(),
                request.isAutoDelete()
        );

        try {

            String declaredQueue =
                    rabbitAdmin.declareQueue(queue);

            if (declaredQueue == null) {
                throw new RabbitAdminOperationException(
                        "RabbitMQ no confirmó la creación de la cola"
                );
            }

            log.info(
                    "Queue created successfully. name={}, durable={}, exclusive={}, autoDelete={}",
                    queueName,
                    request.isDurable(),
                    request.isExclusive(),
                    request.isAutoDelete()
            );

        } catch (AmqpException ex) {

            log.error(
                    "Error creating queue. name={}",
                    queueName,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible crear la cola: " + queueName,
                    ex
            );
        }
    }

    public boolean deleteQueue(String name) {

        String queueName =
                normalizeName(name, "cola");

        try {

            boolean deleted =
                    rabbitAdmin.deleteQueue(queueName);

            if (deleted) {
                log.info(
                        "Queue deleted successfully. name={}",
                        queueName
                );
            } else {
                log.warn(
                        "Queue could not be deleted or does not exist. name={}",
                        queueName
                );
            }

            return deleted;

        } catch (AmqpException ex) {

            log.error(
                    "Error deleting queue. name={}",
                    queueName,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible eliminar la cola: "
                            + queueName,
                    ex
            );
        }
    }

    public void createExchange(
            CreateExchangeRequest request
    ) {

        String exchangeName =
                normalizeName(
                        request.getName(),
                        "exchange"
                );

        Exchange exchange =
                buildExchange(
                        exchangeName,
                        request
                );

        try {

            rabbitAdmin.declareExchange(exchange);

            log.info(
                    "Exchange created successfully. name={}, type={}, durable={}, autoDelete={}",
                    exchangeName,
                    request.getType(),
                    request.isDurable(),
                    request.isAutoDelete()
            );

        } catch (AmqpException ex) {

            log.error(
                    "Error creating exchange. name={}",
                    exchangeName,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible crear el exchange: "
                            + exchangeName,
                    ex
            );
        }
    }

    public boolean deleteExchange(String name) {

        String exchangeName =
                normalizeName(name, "exchange");

        try {

            boolean deleted =
                    rabbitAdmin.deleteExchange(
                            exchangeName
                    );

            if (deleted) {

                log.info(
                        "Exchange deleted successfully. name={}",
                        exchangeName
                );

            } else {

                log.warn(
                        "Exchange could not be deleted or does not exist. name={}",
                        exchangeName
                );
            }

            return deleted;

        } catch (AmqpException ex) {

            log.error(
                    "Error deleting exchange. name={}",
                    exchangeName,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible eliminar el exchange: "
                            + exchangeName,
                    ex
            );
        }
    }

    public void createBinding(
            CreateBindingRequest request
    ) {

        String queueName =
                normalizeName(
                        request.getQueue(),
                        "cola"
                );

        String exchangeName =
                normalizeName(
                        request.getExchange(),
                        "exchange"
                );

        String routingKey =
                normalizeRoutingKey(
                        request.getRoutingKey()
                );

        Binding binding =
                buildBinding(
                        queueName,
                        exchangeName,
                        routingKey
                );

        try {

            rabbitAdmin.declareBinding(binding);

            log.info(
                    "Binding created successfully. queue={}, exchange={}, routingKey={}",
                    queueName,
                    exchangeName,
                    routingKey
            );

        } catch (AmqpException ex) {

            log.error(
                    "Error creating binding. queue={}, exchange={}, routingKey={}",
                    queueName,
                    exchangeName,
                    routingKey,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible crear el binding",
                    ex
            );
        }
    }

    public void deleteBinding(
            CreateBindingRequest request
    ) {

        String queueName =
                normalizeName(
                        request.getQueue(),
                        "cola"
                );

        String exchangeName =
                normalizeName(
                        request.getExchange(),
                        "exchange"
                );

        String routingKey =
                normalizeRoutingKey(
                        request.getRoutingKey()
                );

        Binding binding =
                buildBinding(
                        queueName,
                        exchangeName,
                        routingKey
                );

        try {

            rabbitAdmin.removeBinding(binding);

            log.info(
                    "Binding deleted successfully. queue={}, exchange={}, routingKey={}",
                    queueName,
                    exchangeName,
                    routingKey
            );

        } catch (AmqpException ex) {

            log.error(
                    "Error deleting binding. queue={}, exchange={}, routingKey={}",
                    queueName,
                    exchangeName,
                    routingKey,
                    ex
            );

            throw new RabbitAdminOperationException(
                    "No fue posible eliminar el binding",
                    ex
            );
        }
    }

    private Exchange buildExchange(
            String name,
            CreateExchangeRequest request
    ) {

        String type =
                request.getType()
                        .trim()
                        .toLowerCase();

        return switch (type) {

            case "direct" ->
                    new DirectExchange(
                            name,
                            request.isDurable(),
                            request.isAutoDelete()
                    );

            case "topic" ->
                    new TopicExchange(
                            name,
                            request.isDurable(),
                            request.isAutoDelete()
                    );

            case "fanout" ->
                    new FanoutExchange(
                            name,
                            request.isDurable(),
                            request.isAutoDelete()
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Tipo de exchange no soportado: "
                                    + request.getType()
                    );
        };
    }

    private Binding buildBinding(
            String queue,
            String exchange,
            String routingKey
    ) {

        return new Binding(
                queue,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null
        );
    }

    private String normalizeName(
            String value,
            String resource
    ) {

        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(
                    "El nombre de "
                            + resource
                            + " no puede estar vacío"
            );
        }

        String normalized =
                value.trim();

        if (normalized.length() > 255) {
            throw new IllegalArgumentException(
                    "El nombre de "
                            + resource
                            + " no puede superar 255 caracteres"
            );
        }

        return normalized;
    }

    private String normalizeRoutingKey(
            String routingKey
    ) {

        if (routingKey == null) {
            return "";
        }

        return routingKey.trim();
    }
}