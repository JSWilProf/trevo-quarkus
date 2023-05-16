package br.senai.sp.informatica.trevobk.produtos.model.mapper;

import br.senai.sp.informatica.trevobk.produtos.model.dto.PropostaDTO;
import br.senai.sp.informatica.trevobk.produtos.model.Proposta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "cdi")
public interface PropostaMapper {
    @Mappings({
        @Mapping(target = "idProposta", ignore = true),
        @Mapping(target = "produto", ignore = true)
    })
    Proposta toProposta(PropostaDTO propostaDTO);

    @Mapping(target = "idProduto", source = "produto.idProduto")
    PropostaDTO toPropostaDTO(Proposta proposta);
}
