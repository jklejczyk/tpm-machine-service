package pl.klejczyk.tpm.machine.support;

import org.slf4j.MDC;

/**
 * Correlation id carried through the diagnostic context of the current thread.
 * <p>
 * Shared by the HTTP layer (which seeds it from the request) and the messaging layer
 * (which reads it when publishing and restores it when consuming), so neither adapter
 * has to depend on the other.
 */
public final class CorrelationId {

    public static final String MDC_KEY = "correlationId";

    private CorrelationId() {
    }

    public static String current() {
        return MDC.get(MDC_KEY);
    }

    public static void set(String value) {
        MDC.put(MDC_KEY, value);
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }
}
