package pl.klejczyk.tpm.machine.domain;

public record Actor(String id, Role role) {

    public boolean hasRole(Role expected) {
        return role == expected;
    }
}
