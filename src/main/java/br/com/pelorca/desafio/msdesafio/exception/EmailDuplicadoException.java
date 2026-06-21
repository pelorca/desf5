package br.com.pelorca.desafio.msdesafio.exception;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException(String email) {
        super("Já existe um cliente cadastrado com o email: " + email);
    }
}
