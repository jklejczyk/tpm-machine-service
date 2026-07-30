package pl.klejczyk.tpm.machine.infrastructure.messaging;

import java.time.Instant;

public record EventEnvelope<T>(
        String eventId,
        String correlationId,
        String type,
        int version,
        Instant occurredAt,
        T payload) {
}
