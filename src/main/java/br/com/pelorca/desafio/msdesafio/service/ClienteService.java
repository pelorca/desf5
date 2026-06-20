package br.com.pelorca.desafio.msdesafio.service;

import br.com.pelorca.desafio.msdesafio.dto.ClienteRequestDTO;
import br.com.pelorca.desafio.msdesafio.dto.ClienteResponseDTO;
import br.com.pelorca.desafio.msdesafio.exception.ClienteNotFoundException;
import br.com.pelorca.desafio.msdesafio.exception.EmailDuplicadoException;
import br.com.pelorca.desafio.msdesafio.mapper.ClienteMapper;
import br.com.pelorca.desafio.msdesafio.model.Cliente;
import br.com.pelorca.desafio.msdesafio.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteResponseDTO> listarTodos() {
        return ClienteMapper.toResponseDTOList(clienteRepository.findAll());
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = buscarEntityPorId(id);
        return ClienteMapper.toResponseDTO(cliente);
    }

    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        return ClienteMapper.toResponseDTOList(clienteRepository.findByNomeContainingIgnoreCase(nome));
    }

    public long contar() {
        return clienteRepository.count();
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.email())) {
            throw new EmailDuplicadoException(dto.email());
        }
        Cliente salvo = clienteRepository.save(ClienteMapper.toEntity(dto));
        return ClienteMapper.toResponseDTO(salvo);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarEntityPorId(id);
        if (clienteRepository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new EmailDuplicadoException(dto.email());
        }
        ClienteMapper.updateEntityFromDto(cliente, dto);
        Cliente atualizado = clienteRepository.save(cliente);
        return ClienteMapper.toResponseDTO(atualizado);
    }

    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException(id);
        }
        clienteRepository.deleteById(id);
    }

    private Cliente buscarEntityPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }
}
