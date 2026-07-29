package pl.klejczyk.tpm.machine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MachineTest {

    @Test
    void isRunningAfterRegistration() {
        Machine machine = Machine.register("m-1", "Hydraulic press");

        assertThat(machine.status()).isEqualTo(MachineStatus.RUNNING);
        assertThat(machine.name()).isEqualTo("Hydraulic press");
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> Machine.register("m-1", "   ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void goesUnderMaintenanceWhenSentToService() {
        Machine machine = Machine.register("m-1", "Hydraulic press");

        machine.sendToMaintenance();

        assertThat(machine.status()).isEqualTo(MachineStatus.UNDER_MAINTENANCE);
    }

    @Test
    void returnsToRunningAfterService() {
        Machine machine = Machine.register("m-1", "Hydraulic press");
        machine.sendToMaintenance();

        machine.returnToService();

        assertThat(machine.status()).isEqualTo(MachineStatus.RUNNING);
    }

    @Test
    void toleratesRepeatedMaintenanceRequest() {
        Machine machine = Machine.register("m-1", "Hydraulic press");

        machine.sendToMaintenance();
        machine.sendToMaintenance();

        assertThat(machine.status()).isEqualTo(MachineStatus.UNDER_MAINTENANCE);
    }
}
