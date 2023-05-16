package br.senai.sp.informatica.trevobk.propostas.model.dto;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class PropostaDTO {
    @NotEmpty(message = "O nome é obrigatório")
    private String nome;
    @Email(message = "O e-mail é inválido")
    @NotEmpty(message = "O e-mail é obrigatório")
    private String email;
    @Pattern(regexp = "(\\([1-9][0-9]\\) )?9?[0-9]{4}-[0-9]{4}", message = "O nº do telefone é inválido")
    @NotEmpty(message = "O telefone é obrigatório")
    private String telefone;
    @Min(value = 1, message = "O código do produto é obrigatório")
    @NotNull(message = "O código do produto é obrigatório")
    private Long idProduto;
}
