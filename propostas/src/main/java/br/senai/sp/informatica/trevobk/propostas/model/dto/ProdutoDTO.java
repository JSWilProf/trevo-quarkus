package br.senai.sp.informatica.trevobk.propostas.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProdutoDTO {
    private String nome;
    private String descricao;
    private String area;
    private String imagem;
    private Set<String> culturas;
    private String cadastro;
    private String status;
}
