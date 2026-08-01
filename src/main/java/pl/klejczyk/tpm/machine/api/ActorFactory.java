package pl.klejczyk.tpm.machine.api;

import org.springframework.security.oauth2.jwt.Jwt;
import pl.klejczyk.tpm.machine.domain.Actor;
import pl.klejczyk.tpm.machine.domain.Role;

final class ActorFactory {

    private ActorFactory() {
    }

    static Actor from(Jwt token) {
        String id = token.getSubject();
        String role = token.getClaimAsString("role");

        if (id == null || id.isBlank() || role == null || role.isBlank()) {
            throw new IllegalArgumentException("Token must carry a subject and a role claim.");
        }
        return new Actor(id, Role.valueOf(role));
    }
}
