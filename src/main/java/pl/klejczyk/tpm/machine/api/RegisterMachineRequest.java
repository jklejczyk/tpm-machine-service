package pl.klejczyk.tpm.machine.api;

import jakarta.validation.constraints.NotBlank;

public record RegisterMachineRequest(@NotBlank String name) {
}