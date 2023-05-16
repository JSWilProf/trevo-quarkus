package br.senai.sp.informatica.trevobk.produtos.model.serializers;

import br.senai.sp.informatica.trevobk.produtos.model.Proposta;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PropostaSerializer extends ObjectMapperSerializer<Proposta> {
    public PropostaSerializer() {
        super();
    }
}