package br.com.pelorca.desafio.msdesafio.controller;

import br.com.pelorca.desafio.msdesafio.dto.ClienteRequestDTO;
import br.com.pelorca.desafio.msdesafio.dto.ClienteResponseDTO;
import br.com.pelorca.desafio.msdesafio.exception.ClienteNotFoundException;
import br.com.pelorca.desafio.msdesafio.exception.EmailDuplicadoException;
import br.com.pelorca.desafio.msdesafio.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClienteControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private ClienteResponseDTO clienteResponse() {
        return new ClienteResponseDTO(1L, "Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");
    }

    @Test
    void deveListarTodosClientes() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of(clienteResponse()));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana Souza"));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(clienteResponse());

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        when(clienteService.buscarPorId(99L)).thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarClientesPorNome() throws Exception {
        when(clienteService.buscarPorNome("Ana")).thenReturn(List.of(clienteResponse()));

        mockMvc.perform(get("/api/clientes/nome/Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana Souza"));
    }

    @Test
    void deveContarClientes() throws Exception {
        when(clienteService.contar()).thenReturn(3L);

        mockMvc.perform(get("/api/clientes/contar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    void deveCriarCliente() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");
        when(clienteService.criar(any(ClienteRequestDTO.class))).thenReturn(clienteResponse());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar400QuandoCriarComDadosInvalidos() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("", "email-invalido", null, null);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoCriarComEmailDuplicado() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");
        when(clienteService.criar(any(ClienteRequestDTO.class)))
                .thenThrow(new EmailDuplicadoException("ana@example.com"));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Ana S. Lima", "ana.lima@example.com", "11999990099", "Rua Z, 999");
        when(clienteService.atualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(clienteResponse());

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar404QuandoAtualizarClienteInexistente() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Ana S. Lima", "ana.lima@example.com", "11999990099", "Rua Z, 999");
        when(clienteService.atualizar(eq(99L), any(ClienteRequestDTO.class)))
                .thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarCliente() throws Exception {
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).deletar(1L);
    }

    @Test
    void deveRetornar404QuandoDeletarClienteInexistente() throws Exception {
        doThrow(new ClienteNotFoundException(99L)).when(clienteService).deletar(99L);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }
}
