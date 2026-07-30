package pl.klejczyk.tpm.machine.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkOrderMachineEvent(String machineId) {
}
