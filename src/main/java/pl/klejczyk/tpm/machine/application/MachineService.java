package pl.klejczyk.tpm.machine.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.klejczyk.tpm.machine.domain.Actor;
import pl.klejczyk.tpm.machine.domain.Machine;
import pl.klejczyk.tpm.machine.domain.MachineNotFound;
import pl.klejczyk.tpm.machine.domain.MachineRepository;
import pl.klejczyk.tpm.machine.infrastructure.messaging.DomainEventOccurred;
import pl.klejczyk.tpm.machine.infrastructure.messaging.MachineRegistered;

import java.util.UUID;

@Service
public class MachineService {

    private final MachineRepository repository;
    private final ApplicationEventPublisher events;

    public MachineService(MachineRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public Machine register(Actor actor, String name) {
        Machine machine = Machine.register(actor, UUID.randomUUID().toString(), name);
        Machine saved = repository.save(machine);

        events.publishEvent(new DomainEventOccurred(
                "machine.registered",
                "MachineRegistered",
                new MachineRegistered(saved.id(), saved.name())
        ));

        return saved;
    }

    @Transactional(readOnly = true)
    public Machine byId(String id) {
        return repository.findById(id).orElseThrow(() -> new MachineNotFound(id));
    }
}