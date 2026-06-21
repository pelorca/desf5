package br.com.pelorca.desafio.msdesafio.service;

import br.com.pelorca.desafio.msdesafio.dto.ClienteRequestDTO;
import br.com.pelorca.desafio.msdesafio.dto.ClienteResponseDTO;
import br.com.pelorca.desafio.msdesafio.exception.ClienteNotFoundException;
import br.com.pelorca.desafio.msdesafio.exception.EmailDuplicadoException;
import br.com.pelorca.desafio.msdesafio.model.Cliente;
import br.com.pelorca.desafio.msdesafio.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteExistente() {
        return Cliente.builder().id(1L).nome("Ana Souza").email("ana@example.com")
                .telefone("11999990001").endereco("Rua A, 100").build();
    }

    @Test
    void deveListarTodos() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteExistente()));

        List<ClienteResponseDTO> resultado = clienteService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Ana Souza");
    }

    @Test
    void deveBuscarPorIdQuandoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente()));

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    void deveLancarExceptionQuandoBuscarPorIdInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void deveBuscarPorNome() {
        when(clienteRepository.findByNomeContainingIgnoreCase("Ana")).thenReturn(List.of(clienteExistente()));

        List<ClienteResponseDTO> resultado = clienteService.buscarPorNome("Ana");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveContar() {
        when(clienteRepository.count()).thenReturn(5L);

        assertThat(clienteService.contar()).isEqualTo(5L);
    }

    @Test
    void deveCriarClienteQuandoEmailNaoExiste() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");
        when(clienteRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente());

        ClienteResponseDTO resultado = clienteService.criar(dto);

        assertThat(resultado.email()).isEqualTo("ana@example.com");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExceptionAoCriarComEmailDuplicado() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");
        when(clienteRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.criar(dto))
                .isInstanceOf(EmailDuplicadoException.class);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteQuandoExisteEEmailDisponivel() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana S. Lima", "ana.lima@example.com", "11999990099", "Rua Z, 999");
        Cliente existente = clienteExistente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.existsByEmailAndIdNot("ana.lima@example.com", 1L)).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);

        ClienteResponseDTO resultado = clienteService.atualizar(1L, dto);

        assertThat(resultado).isNotNull();
        verify(clienteRepository, times(1)).save(existente);
    }

    @Test
    void deveLancarExceptionAoAtualizarClienteInexistente() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana S. Lima", "ana.lima@example.com", "11999990099", "Rua Z, 999");
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(99L, dto))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void deveLancarExceptionAoAtualizarComEmailDeOutroCliente() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana S. Lima", "outro@example.com", "11999990099", "Rua Z, 999");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente()));
        when(clienteRepository.existsByEmailAndIdNot("outro@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.atualizar(1L, dto))
                .isInstanceOf(EmailDuplicadoException.class);
    }

    @Test
    void deveDeletarClienteQuandoExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.deletar(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExceptionAoDeletarClienteInexistente() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletar(99L))
                .isInstanceOf(ClienteNotFoundException.class);
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
