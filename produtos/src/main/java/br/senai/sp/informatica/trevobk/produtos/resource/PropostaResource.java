package br.senai.sp.informatica.trevobk.produtos.resource;

import br.senai.sp.informatica.trevobk.produtos.components.exceptions.DataFalhaNaOperacaoException;
import br.senai.sp.informatica.trevobk.produtos.model.Produto;
import br.senai.sp.informatica.trevobk.produtos.model.Proposta;
import br.senai.sp.informatica.trevobk.produtos.model.dto.PropostaDTO;
import br.senai.sp.informatica.trevobk.produtos.model.mapper.PropostaMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/proposta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "proposta")
@RolesAllowed("cliente")
@SecurityScheme(securitySchemeName = "trevo-oauth", type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(password =
    @OAuthFlow(tokenUrl = "http:/localhost:8180/auth/realms/trevo/protocol/openid-connect/token")))
public class PropostaResource {
    @Inject
    PropostaMapper mapper;
    @Channel("proposta")
    MutinyEmitter<Proposta> emitter;

    @Operation(summary = "Grava nova Proposta")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Proposta criada", content = @Content),
        @APIResponse(responseCode = "404", description = "Produto não encontrado", content = @Content),
        @APIResponse(responseCode = "417", description = "Falha ao salvar a Proposta", content = @Content),
        @APIResponse(responseCode = "422", description = "Proposta com dados inválidos", content = @Content)})
    @Transactional
    @POST
    public Uni<Void> adicionar(@Valid PropostaDTO dto) {
        return Produto.<Produto>findById(dto.getIdProduto())
            .flatMap(produto -> Proposta.getSession()
                .flatMap(session -> session.merge(produto)
                    .chain(__ -> {
                        var proposta = mapper.toProposta(dto);
                        proposta.setProduto(produto);
                        return emitter.send(proposta);
                    })
                    .onFailure(PersistenceException.class)
                    .transform(throwable -> new DataFalhaNaOperacaoException("Falha ao salvar a Proposta"))
                    .onFailure(IllegalStateException.class)
                    .transform(throwable -> new DataFalhaNaOperacaoException("Falha ao salvar a Proposta"))
                )
            );
    }
}
