package pl.klejczyk.tpm.machine.infrastructure.messaging;

// internal
public record DomainEventOccurred(
        String routingKey,
        String type,
        Object payload,
        String correlationId) {
}
