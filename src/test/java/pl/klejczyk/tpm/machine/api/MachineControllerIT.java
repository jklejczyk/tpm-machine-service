package pl.klejczyk.tpm.machine.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import pl.klejczyk.tpm.machine.TestcontainersConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MachineControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private static RequestPostProcessor actor(String id, String role) {
        return jwt().jwt(token -> token.subject(id).claim("role", role));
    }

    @Test
    void registersMachineAndReturnsItRunning() throws Exception {
        mockMvc.perform(post("/machines").with(actor("mgr-1", "MANAGER")).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Hydraulic press\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Hydraulic press"))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void operatorMayNotRegisterAMachine() throws Exception {
        mockMvc.perform(post("/machines").with(actor("op-1", "OPERATOR")).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Hydraulic press\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsRegistrationWithoutName() throws Exception {
        mockMvc.perform(post("/machines").with(actor("mgr-1", "MANAGER")).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownMachine() throws Exception {
        mockMvc.perform(get("/machines/no-such-machine").with(actor("mgr-1", "MANAGER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/machines/no-such-machine"))
                .andExpect(status().isUnauthorized());
    }
}
