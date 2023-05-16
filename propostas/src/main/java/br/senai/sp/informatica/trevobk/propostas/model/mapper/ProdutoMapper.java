package br.senai.sp.informatica.trevobk.propostas.model.mapper;

import br.senai.sp.informatica.trevobk.propostas.model.Cultura;
import br.senai.sp.informatica.trevobk.propostas.model.Produto;
import br.senai.sp.informatica.trevobk.propostas.model.dto.ProdutoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface ProdutoMapper {
    @Mappings({
        @Mapping(target = "area", source = "area.tamanho"),
        @Mapping(target = "cadastro", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "imagem", ignore = true)
    })
    ProdutoDTO toProdutoDTO(Produto produto);

    default Set<String> map(Set<Cultura> culturas) {
        return culturas.stream()
            .map(Cultura::getNome)
            .collect(Collectors.toSet());
    }
}
