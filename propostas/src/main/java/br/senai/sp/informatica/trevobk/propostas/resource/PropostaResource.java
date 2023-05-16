package br.senai.sp.informatica.trevobk.propostas.resource;

import br.senai.sp.informatica.trevobk.propostas.components.DataPage;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataException;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataNotFoundException;
import br.senai.sp.informatica.trevobk.propostas.components.exceptions.DataPageException;
import br.senai.sp.informatica.trevobk.propostas.components.services.ElasticService;
import br.senai.sp.informatica.trevobk.propostas.model.Proposta;
import br.senai.sp.informatica.trevobk.propostas.model.dto.PropostaDTO;
import br.senai.sp.informatica.trevobk.propostas.model.mapper.PropostaMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static java.util.stream.Collectors.toList;

@Path("/api/proposta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "proposta")
@RolesAllowed("cliente")
@SecurityScheme(securitySchemeName = "trevo-oauth", type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(password =
    @OAuthFlow(tokenUrl = "http://172.30.10.10:8180/auth/realms/trevo/protocol/openid-connect/token")))
public class PropostaResource {
    @Inject
    private PropostaMapper mapper;
    @Inject
    private ElasticService service;
    @Inject
    private Logger logger;

    @Operation(summary = "Gera uma lista de todos os Propostas paginados")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Propostas encontrados",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = PropostaDTO.class))}) })
    @GET
    public DataPage<PropostaDTO> buscar(
        @QueryParam("page") int page,
        @QueryParam("size") int size,
        @QueryParam("sort") String sort) throws DataException {
        final int numPage = page < 0 ? 0 : page;
        final int pageSize = size <= 0 ? 10 : size;
        if(sort == null || sort.isBlank()) sort = "nome";

        PanacheQuery<Proposta> lista = Proposta.findAll(Sort.by(sort));
        lista.page(Page.ofSize(size));

        if(page + 1 > lista.pageCount())
            throw new DataPageException("Paginação além do limite de páginas disponíveis para esta consulta");

        return DataPage.<PropostaDTO>builder()
            .page(page)
            .size(size)
            .total(lista.pageCount())
            .items(lista.page(Page.of(page,size)).stream()
                .map(obj -> mapper.toPropostaDTO(obj))
                .collect(toList()))
            .build();
    }

    @Operation(summary = "Localiza um Proposta por ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Proposta encontrado",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = PropostaDTO.class)) }),
        @APIResponse(responseCode = "404", description = "Proposta não encontrado",
            content = @Content) })
    @GET
    @Path("{id}")
    public PropostaDTO buscar(Long id) throws DataException{
        var aProposta = Proposta.<Proposta>findByIdOptional(id);
        if(aProposta.isPresent()) {
            return mapper.toPropostaDTO(aProposta.get());
        } else {
            throw  new DataNotFoundException("O Proposta não foi encontrado");
        }
    }

    @Incoming("proposta")
    @Transactional
    public void adicionar(Proposta proposta) {
        try {
            logger.info("Proposta recebida: " + proposta.toString());
            Proposta.persist(proposta);
            logger.info("Proposta Salva: " + proposta.toString());
            service.indexa(proposta);
            logger.info("Proposta indexada: " + proposta.toString());
        } catch (PersistenceException ex) {
            logger.errorv("Falha ao processar a Proposta de %s", proposta.getNome());
        }
    }
}
