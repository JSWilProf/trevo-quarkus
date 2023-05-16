package br.senai.sp.informatica.trevobk.propostas.model.deserializer;

import br.senai.sp.informatica.trevobk.propostas.model.dto.PropostaDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PropostaDeserializer extends ObjectMapperDeserializer<PropostaDTO> {
    public PropostaDeserializer() {
        super(PropostaDTO.class);
    }
}
