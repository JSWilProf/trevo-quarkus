package br.senai.sp.informatica.trevobk.propostas.components.providers;

import br.senai.sp.informatica.trevobk.propostas.components.ErrorResponse;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataChaveDuplicadaException;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataFalhaNaOperacaoException;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataPageException;

import javax.json.bind.JsonbException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

public class CustomExceptionHandler {
    @Provider
    public static class CustomJsonbExceptionHandler implements ExceptionMapper<JsonbException> {
        @Override
        public Response toResponse(JsonbException exception) {
            var status = Response.Status.BAD_REQUEST;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), "A estrutura dos dados é inválida", null))
                .build();
        }
    }

    @Provider
    public static class CustomValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException exception) {
            var erros = exception.getConstraintViolations()
                .stream().map(ErrorResponse.Erro::new)
                .collect(Collectors.toList());

            var status = Response.Status.BAD_REQUEST;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), "Dados Inválidos", erros))
                .build();
        }
    }

    @Provider
    public static class CustomNotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {
        @Override
        public Response toResponse(NotFoundException exception) {
            var status = Response.Status.NOT_FOUND;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage(), null))
                .build();
        }
    }

    @Provider
    public static class CustomDBConstraintExceptionHandler implements ExceptionMapper<DataChaveDuplicadaException> {
        @Override
        public Response toResponse(DataChaveDuplicadaException exception) {
            var status = Response.Status.EXPECTATION_FAILED;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage(), null))
                .build();
        }
    }

    @Provider
    public static class CustomFalhaNaOperacaoExceptionHandler implements ExceptionMapper<DataFalhaNaOperacaoException> {
        @Override
        public Response toResponse(DataFalhaNaOperacaoException exception) {
            var status = Response.Status.EXPECTATION_FAILED;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage(), null))
                .build();
        }
    }

    @Provider
    public static class CustomFalhaNaPaginacaoExceptionHandler implements ExceptionMapper<DataPageException> {
        @Override
        public Response toResponse(DataPageException exception) {
            var status = Response.Status.BAD_REQUEST;
            return Response.status(status)
                .entity(new ErrorResponse(status.getStatusCode(), exception.getMessage(), null))
                .build();
        }
    }
}