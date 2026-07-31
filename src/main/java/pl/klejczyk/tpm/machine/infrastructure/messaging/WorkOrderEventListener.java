package pl.klejczyk.tpm.machine.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.klejczyk.tpm.machine.domain.Machine;
import pl.klejczyk.tpm.machine.domain.MachineRepository;
import pl.klejczyk.tpm.machine.support.CorrelationId;

import java.time.Clock;
import java.util.Optional;

@Component
class WorkOrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderEventListener.class);

    private static final String STARTED = "WorkOrderStarted";
    private static final String RESOLVED = "WorkOrderResolved";

    private final MachineRepository machines;
    private final ProcessedEventRepository processedEvents;
    private final Clock clock;

    WorkOrderEventListener(MachineRepository machines, ProcessedEventRepository processedEvents, Clock clock) {
        this.machines = machines;
        this.processedEvents = processedEvents;
        this.clock = clock;
    }

    @RabbitListener(queues = RabbitConfiguration.WORKORDER_QUEUE)
    @Transactional
    void onWorkOrderEvent(EventEnvelope<WorkOrderMachineEvent> envelope) {
        CorrelationId.set(envelope.correlationId());
        try {
            handle(envelope);
        } finally {
            CorrelationId.clear();
        }
    }

    private void handle(EventEnvelope<WorkOrderMachineEvent> envelope) {
        if (processedEvents.existsById(envelope.eventId())) {
            log.info("Skipping duplicate eventId={}", envelope.eventId());
            return;
        }

        String machineId = envelope.payload().machineId();
        Optional<Machine> found = machines.findById(machineId);

        if (found.isEmpty()) {
            log.warn("Event {} refers to unknown machine {}", envelope.type(), machineId);
        } else if (STARTED.equals(envelope.type())) {
            found.get().sendToMaintenance();
            log.info("Machine {} is now under maintenance", machineId);
        } else if (RESOLVED.equals(envelope.type())) {
            found.get().returnToService();
            log.info("Machine {} is back in service", machineId);
        }

        processedEvents.save(new ProcessedEvent(envelope.eventId(), clock.instant()));
    }
}
