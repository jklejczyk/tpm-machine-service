package pl.klejczyk.tpm.machine.api;

import pl.klejczyk.tpm.machine.domain.Machine;

public record MachineResponse(String id, String name, String status) {

    public static MachineResponse from(Machine machine) {
        return new MachineResponse(machine.id(), machine.name(), machine.status().name());
    }
}
