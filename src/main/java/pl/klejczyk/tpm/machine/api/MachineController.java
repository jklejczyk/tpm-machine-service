package pl.klejczyk.tpm.machine.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.klejczyk.tpm.machine.application.MachineService;

@RestController
@RequestMapping("/machines")
class MachineController {

    private final MachineService service;

    MachineController(MachineService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<MachineResponse> register(@AuthenticationPrincipal Jwt token, @Valid @RequestBody RegisterMachineRequest request) {
        MachineResponse response = MachineResponse.from(service.register(ActorFactory.from(token), request.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    MachineResponse byId(@PathVariable String id) {
        return MachineResponse.from(service.byId(id));
    }
}
