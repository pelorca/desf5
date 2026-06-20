package br.com.pelorca.desafio.msdesafio.dto;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String endereco
) {
}
