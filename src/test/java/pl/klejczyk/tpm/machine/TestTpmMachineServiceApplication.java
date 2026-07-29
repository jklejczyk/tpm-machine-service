package pl.klejczyk.tpm.machine;

import org.springframework.boot.SpringApplication;

public class TestTpmMachineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TpmMachineServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
