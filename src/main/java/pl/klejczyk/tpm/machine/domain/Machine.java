package pl.klejczyk.tpm.machine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    protected Machine() {
        // required by Hibernate
    }

    private Machine(String id, String name, MachineStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public static Machine register(String id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Machine name must not be blank.");
        }
        return new Machine(id, name, MachineStatus.RUNNING);
    }

    public void sendToMaintenance() {
        this.status = MachineStatus.UNDER_MAINTENANCE;
    }

    public void returnToService() {
        this.status = MachineStatus.RUNNING;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public MachineStatus status() {
        return status;
    }
}
