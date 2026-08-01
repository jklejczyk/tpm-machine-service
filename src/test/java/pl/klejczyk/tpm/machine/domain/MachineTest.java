package pl.klejczyk.tpm.machine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MachineTest {

    private static final Actor MANAGER = new Actor("mgr-1", Role.MANAGER);
    private static final Actor OPERATOR = new Actor("op-1", Role.OPERATOR);

    private Machine registered() {
        return Machine.register(MANAGER, "m-1", "Hydraulic press");
    }

    @Test
    void isRunningAfterRegistration() {
        Machine machine = registered();

        assertThat(machine.status()).isEqualTo(MachineStatus.RUNNING);
        assertThat(machine.name()).isEqualTo("Hydraulic press");
    }

    @Test
    void onlyAManagerMayRegisterAMachine() {
        assertThatThrownBy(() -> Machine.register(OPERATOR, "m-1", "Hydraulic press"))
                .isInstanceOf(UnauthorizedOperation.class);
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> Machine.register(MANAGER, "m-1", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void goesUnderMaintenanceWhenSentToService() {
        Machine machine = registered();

        machine.sendToMaintenance();

        assertThat(machine.status()).isEqualTo(MachineStatus.UNDER_MAINTENANCE);
    }

    @Test
    void returnsToRunningAfterService() {
        Machine machine = registered();
        machine.sendToMaintenance();

        machine.returnToService();

        assertThat(machine.status()).isEqualTo(MachineStatus.RUNNING);
    }

    @Test
    void toleratesRepeatedMaintenanceRequest() {
        Machine machine = registered();

        machine.sendToMaintenance();
        machine.sendToMaintenance();

        assertThat(machine.status()).isEqualTo(MachineStatus.UNDER_MAINTENANCE);
    }
}
