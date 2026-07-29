package pl.klejczyk.tpm.machine.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klejczyk.tpm.machine.domain.Machine;
import pl.klejczyk.tpm.machine.domain.MachineNotFound;
import pl.klejczyk.tpm.machine.domain.MachineRepository;

import java.util.UUID;

@Service
public class MachineService {

    private final MachineRepository repository;

    public MachineService(MachineRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Machine register(String name) {
        Machine machine = Machine.register(UUID.randomUUID().toString(), name);
        return repository.save(machine);
    }

    @Transactional(readOnly = true)
    public Machine byId(String id) {
        return repository.findById(id).orElseThrow(() -> new MachineNotFound(id));
    }
}