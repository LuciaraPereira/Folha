package br.com.biblioteca.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorErros {

    // Trata os erros de validação, como campos obrigatórios.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
            MethodArgumentNotValidException erro
    ) {
        String mensagem = erro.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (mensagem.isBlank()) {
            mensagem = "Confira os dados informados.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", mensagem));
    }

    // Trata erros de restrição do banco, incluindo registros vinculados.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(
            DataIntegrityViolationException erro
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "message",
                        "Não foi possível concluir a operação. "
                                + "Verifique se o registro está vinculado "
                                + "a outros dados ou se já existe um cadastro "
                                + "com as mesmas informações."
                ));
    }
}