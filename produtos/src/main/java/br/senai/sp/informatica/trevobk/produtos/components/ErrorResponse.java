package br.senai.sp.informatica.trevobk.produtos.components;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String mensagem;
    private List<Erro> erros;

    @Getter
    @AllArgsConstructor
    public static class Erro {
        private String campo;
        private String mensagem;

        public Erro(ConstraintViolation<?> constraint) {
            campo = Stream.of(constraint.getPropertyPath().toString().split("\\.")).skip(2).collect(Collectors.joining("."));
            mensagem = constraint.getMessage();
        }
    }
}