package br.senai.sp.informatica.trevobk.propostas.model.deserializer;

import br.senai.sp.informatica.trevobk.propostas.model.Produto;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ProdutoDeserializer extends ObjectMapperDeserializer<Produto> {
    public ProdutoDeserializer() {
        super(Produto.class);
    }
}
