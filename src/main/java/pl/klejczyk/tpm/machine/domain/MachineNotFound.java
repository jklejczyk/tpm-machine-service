package pl.klejczyk.tpm.machine.domain;

public class MachineNotFound extends RuntimeException {

    public MachineNotFound(String id) {
        super("Machine not found: " + id);
    }
}
