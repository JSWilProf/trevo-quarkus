package br.senai.sp.informatica.trevobk.produtos.model.mapper;

import br.senai.sp.informatica.trevobk.produtos.model.Area;
import br.senai.sp.informatica.trevobk.produtos.model.Cultura;
import br.senai.sp.informatica.trevobk.produtos.model.Produto;
import br.senai.sp.informatica.trevobk.produtos.model.dto.ProdutoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface ProdutoMapper {
    @Mappings({
        @Mapping(target = "area", source = "area.tamanho"),
        @Mapping(target = "culturas", qualifiedByName = "mapToStrings")
    })
    ProdutoDTO toProdutoDTO(Produto produto);

    @Mappings({
        @Mapping(target = "idProduto", ignore = true),
        @Mapping(target = "culturas", qualifiedByName = "mapToCulturas")
    })
    Produto toProduto(ProdutoDTO dto);

    default Area map(String area) {
        return Area.<Area>find("tamanho", area)
            .firstResult()
            .await()
            .atMost(Duration.ofSeconds(2));
    }

    @Named("mapToStrings")
    default Set<String> mapToStrings(Set<Cultura> culturas) {
        return culturas.stream()
            .map(Cultura::getNome)
            .collect(Collectors.toSet());
    }

    @Named("mapToCulturas")
    default Set<Cultura> mapToCulturas(Set<String> culturas) {
        return culturas.stream().map(cultura ->
            Cultura.<Cultura>find("nome", cultura)
                .firstResult()
                .await()
                .atMost(Duration.ofSeconds(2))
        ).collect(Collectors.toSet());
    }
}
