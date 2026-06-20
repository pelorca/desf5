package br.com.pelorca.desafio.msdesafio.mapper;

import br.com.pelorca.desafio.msdesafio.dto.ClienteRequestDTO;
import br.com.pelorca.desafio.msdesafio.dto.ClienteResponseDTO;
import br.com.pelorca.desafio.msdesafio.model.Cliente;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteMapperTest {

    @Test
    void deveConverterRequestDtoParaEntity() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana Souza", "ana@example.com", "11999990001", "Rua A, 100");

        Cliente cliente = ClienteMapper.toEntity(dto);

        assertThat(cliente.getId()).isNull();
        assertThat(cliente.getNome()).isEqualTo("Ana Souza");
        assertThat(cliente.getEmail()).isEqualTo("ana@example.com");
        assertThat(cliente.getTelefone()).isEqualTo("11999990001");
        assertThat(cliente.getEndereco()).isEqualTo("Rua A, 100");
    }

    @Test
    void deveConverterEntityParaResponseDto() {
        Cliente cliente = Cliente.builder()
                .id(1L).nome("Ana Souza").email("ana@example.com")
                .telefone("11999990001").endereco("Rua A, 100").build();

        ClienteResponseDTO dto = ClienteMapper.toResponseDTO(cliente);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("Ana Souza");
        assertThat(dto.email()).isEqualTo("ana@example.com");
        assertThat(dto.telefone()).isEqualTo("11999990001");
        assertThat(dto.endereco()).isEqualTo("Rua A, 100");
    }

    @Test
    void deveConverterListaDeEntitiesParaListaDeResponseDto() {
        Cliente c1 = Cliente.builder().id(1L).nome("Ana").email("ana@example.com").build();
        Cliente c2 = Cliente.builder().id(2L).nome("Bruno").email("bruno@example.com").build();

        List<ClienteResponseDTO> dtos = ClienteMapper.toResponseDTOList(List.of(c1, c2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(ClienteResponseDTO::nome).containsExactly("Ana", "Bruno");
    }

    @Test
    void deveAtualizarEntityExistenteComDadosDoRequestDto() {
        Cliente cliente = Cliente.builder().id(1L).nome("Ana Souza").email("ana@example.com")
                .telefone("11999990001").endereco("Rua A, 100").build();
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana S. Lima", "ana.lima@example.com", "11999990099", "Rua Z, 999");

        ClienteMapper.updateEntityFromDto(cliente, dto);

        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNome()).isEqualTo("Ana S. Lima");
        assertThat(cliente.getEmail()).isEqualTo("ana.lima@example.com");
        assertThat(cliente.getTelefone()).isEqualTo("11999990099");
        assertThat(cliente.getEndereco()).isEqualTo("Rua Z, 999");
    }
}
