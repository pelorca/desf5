package br.com.pelorca.desafio.msdesafio.repository;

import br.com.pelorca.desafio.msdesafio.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void deveEncontrarClientesPorNomeIgnorandoCaixa() {
        clienteRepository.save(Cliente.builder().nome("Ana Souza").email("ana@example.com").build());
        clienteRepository.save(Cliente.builder().nome("Ana Paula").email("anapaula@example.com").build());
        clienteRepository.save(Cliente.builder().nome("Bruno Lima").email("bruno@example.com").build());

        List<Cliente> encontrados = clienteRepository.findByNomeContainingIgnoreCase("ana");

        assertThat(encontrados).hasSize(2);
        assertThat(encontrados).extracting(Cliente::getNome)
                .containsExactlyInAnyOrder("Ana Souza", "Ana Paula");
    }

    @Test
    void deveVerificarExistenciaPorEmail() {
        clienteRepository.save(Cliente.builder().nome("Ana Souza").email("ana@example.com").build());

        assertThat(clienteRepository.existsByEmail("ana@example.com")).isTrue();
        assertThat(clienteRepository.existsByEmail("inexistente@example.com")).isFalse();
    }

    @Test
    void deveVerificarExistenciaPorEmailExcluindoId() {
        Cliente salvo = clienteRepository.save(Cliente.builder().nome("Ana Souza").email("ana@example.com").build());

        assertThat(clienteRepository.existsByEmailAndIdNot("ana@example.com", salvo.getId())).isFalse();
        assertThat(clienteRepository.existsByEmailAndIdNot("ana@example.com", -1L)).isTrue();
    }
}
