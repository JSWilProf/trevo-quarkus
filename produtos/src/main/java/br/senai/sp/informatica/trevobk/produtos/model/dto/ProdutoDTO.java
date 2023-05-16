package br.senai.sp.informatica.trevobk.produtos.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProdutoDTO {
    private String nome;
    private String descricao;
    private String area;
    private Set<String> culturas;
}
