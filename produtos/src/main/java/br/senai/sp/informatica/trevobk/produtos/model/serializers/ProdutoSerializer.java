package br.senai.sp.informatica.trevobk.produtos.model.serializers;

import br.senai.sp.informatica.trevobk.produtos.model.Produto;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class ProdutoSerializer extends ObjectMapperSerializer<Produto> {
    public ProdutoSerializer() {
        super();
    }
}