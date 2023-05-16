package br.senai.sp.informatica.trevobk.produtos.components.exceptions;

public class DataException extends Exception {
    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable t) {
        super(message, t);
    }
}
