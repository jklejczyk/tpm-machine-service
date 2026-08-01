package pl.klejczyk.tpm.machine.domain;

public class UnauthorizedOperation extends RuntimeException {

    private UnauthorizedOperation(String message) {
        super(message);
    }

    public static UnauthorizedOperation forActor(Actor actor, String operation) {
        return new UnauthorizedOperation(
                "Role " + actor.role() + " is not allowed to perform '" + operation + "'.");
    }
}
