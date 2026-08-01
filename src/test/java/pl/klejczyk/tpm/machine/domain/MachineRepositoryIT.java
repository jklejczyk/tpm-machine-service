package pl.klejczyk.tpm.machine.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.klejczyk.tpm.machine.TestcontainersConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MachineRepositoryIT {

    @Autowired
    private MachineRepository repository;

    @Test
    void persistsAndRestoresMachineState() {
        Machine machine = Machine.register(new Actor("mgr-1", Role.MANAGER), "m-1", "Hydraulic press");
        machine.sendToMaintenance();
        repository.save(machine);

        Optional<Machine> restored = repository.findById("m-1");

        assertThat(restored).isPresent();
        assertThat(restored.get().name()).isEqualTo("Hydraulic press");
        assertThat(restored.get().status()).isEqualTo(MachineStatus.UNDER_MAINTENANCE);
    }
}