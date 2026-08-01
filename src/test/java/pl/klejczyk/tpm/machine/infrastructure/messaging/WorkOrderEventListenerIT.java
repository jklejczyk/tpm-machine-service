package pl.klejczyk.tpm.machine.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.klejczyk.tpm.machine.TestcontainersConfiguration;
import pl.klejczyk.tpm.machine.domain.Machine;
import pl.klejczyk.tpm.machine.domain.MachineRepository;
import pl.klejczyk.tpm.machine.domain.MachineStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class WorkOrderEventListenerIT {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MachineRepository machines;

    private String aRunningMachine() {
        Machine machine = Machine.register(UUID.randomUUID().toString(), "Hydraulic press");
        machines.save(machine);
        return machine.id();
    }

    private void publish(String eventId, String type, String routingKey, String machineId) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.EXCHANGE, routingKey, new EventEnvelope<>(eventId, "test-correlation", type, 1, Instant.now(), new WorkOrderMachineEvent(machineId)));
    }

    private void awaitStatus(String machineId, MachineStatus expected) {
        await().atMost(TIMEOUT).untilAsserted(() -> assertThat(machines.findById(machineId)).get().extracting(Machine::status).isEqualTo(expected));
    }

    @Test
    void movesTheMachineIntoMaintenanceWhenARepairStarts() {
        String machineId = aRunningMachine();

        publish(UUID.randomUUID().toString(), "WorkOrderStarted", "workorder.started", machineId);

        awaitStatus(machineId, MachineStatus.UNDER_MAINTENANCE);
    }

    @Test
    void returnsTheMachineToServiceWhenTheRepairIsResolved() {
        String machineId = aRunningMachine();

        publish(UUID.randomUUID().toString(), "WorkOrderStarted", "workorder.started", machineId);
        awaitStatus(machineId, MachineStatus.UNDER_MAINTENANCE);

        publish(UUID.randomUUID().toString(), "WorkOrderResolved", "workorder.resolved", machineId);
        awaitStatus(machineId, MachineStatus.RUNNING);
    }

    @Test
    void ignoresAnEventItHasAlreadyProcessed() {
        String machineId = aRunningMachine();
        String eventId = UUID.randomUUID().toString();

        publish(eventId, "WorkOrderStarted", "workorder.started", machineId);
        awaitStatus(machineId, MachineStatus.UNDER_MAINTENANCE);

        Machine machine = machines.findById(machineId).orElseThrow();
        machine.returnToService();
        machines.save(machine);

        publish(eventId, "WorkOrderStarted", "workorder.started", machineId);

        await().during(Duration.ofSeconds(3)).atMost(TIMEOUT).untilAsserted(() -> assertThat(machines.findById(machineId)).get().extracting(Machine::status).isEqualTo(MachineStatus.RUNNING));
    }
}
