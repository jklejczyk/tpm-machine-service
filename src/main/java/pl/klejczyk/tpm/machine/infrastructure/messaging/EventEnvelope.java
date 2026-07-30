package pl.klejczyk.tpm.machine.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventEnvelope<T>(
        String eventId,
        String correlationId,
        String type,
        int version,
        Instant occurredAt,
        T payload) {
}
