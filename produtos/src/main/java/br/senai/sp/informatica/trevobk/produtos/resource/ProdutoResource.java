package br.senai.sp.informatica.trevobk.produtos.resource;

import br.senai.sp.informatica.trevobk.produtos.components.DataPage;
import br.senai.sp.informatica.trevobk.produtos.components.exceptions.DataFalhaNaOperacaoException;
import br.senai.sp.informatica.trevobk.produtos.components.exceptions.DataNotFoundException;
import br.senai.sp.informatica.trevobk.produtos.components.exceptions.DataPageException;
import br.senai.sp.informatica.trevobk.produtos.components.providers.CustomExceptionHandler;
import br.senai.sp.informatica.trevobk.produtos.model.Produto;
import br.senai.sp.informatica.trevobk.produtos.model.dto.ProdutoDTO;
import br.senai.sp.informatica.trevobk.produtos.model.mapper.ProdutoMapper;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
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
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/produto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "produto")
@RolesAllowed({"cliente", "admin"})
@SecurityScheme(securitySchemeName = "trevo-oauth", type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(password =
    @OAuthFlow(tokenUrl = "http:/localhost:8180/auth/realms/trevo/protocol/openid-connect/token")))
public class ProdutoResource {
    @Inject
    ProdutoMapper mapper;
    @Channel("produto")
    MutinyEmitter<Produto> emitter;
    @Inject
    Logger logger;

    @Operation(summary = "Gera uma lista de todos os Produtos paginados")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Produtos encontrados",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProdutoDTO.class))}) })
    @GET
    public Uni<DataPage<ProdutoDTO>> buscar(
        @QueryParam("page") int page,
        @QueryParam("size") int size,
        @QueryParam("sort") String sort) {
        final int numPage = page < 0 ? 0 : page;
        final int pageSize = size <= 0 ? 10 : size;
        if(sort == null || sort.isBlank()) sort = "nome";

        return Produto.findAll(Sort.by(sort))
            .page(Page.of(numPage, pageSize))
            .<Produto>list()
            .flatMap(osProdutos ->
                Uni.createFrom().item(() -> {
                    var dados = osProdutos.stream()
                        .map(mapper::toProdutoDTO)
                        .toList();
                    return DataPage.<ProdutoDTO>builder()
                        .page(numPage)
                        .size(pageSize)
                        .items(dados)
                        .records(dados.size())
                        .build();
                }))
            .onFailure(PersistenceException.class)
            .transform(throwable -> new DataPageException("Falha na consulta aos Produtos"));
    }

    @Operation(summary = "Localiza um Produto por ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Produto encontrado",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProdutoDTO.class)) }),
        @APIResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content) })
    @GET
    @Path("{id}")
    public Uni<ProdutoDTO> buscar(Long id) {
        return Produto.<Produto>findById(id)
            .map(produto -> mapper.toProdutoDTO(produto))
            .onFailure(PersistenceException.class)
            .transform(throwable -> new DataNotFoundException("O Produto não foi encontrado"));
    }

    @POST
    @Transactional
    @APIResponse(responseCode = "200", description = "Caso produto seja castrado com sucesso")
    @APIResponse(responseCode = "400", description = "A estrutura de dados é inválida")
    @APIResponse(responseCode = "417",
        content = @Content(schema = @Schema(allOf = CustomExceptionHandler.CustomValidationExceptionHandler.class)))
    public Uni<ProdutoDTO> adicionar(ProdutoDTO dto) {
        var prod = mapper.toProduto(dto);
        return Produto.getSession()
            .flatMap(session -> session.merge(prod))
            .flatMap(obj -> obj.<Produto>persistAndFlush()
                .flatMap(produto -> Uni.createFrom()
                    .item(() -> {
                        emitter.send(produto)
                            .subscribe()
                            .with(unused -> logger.info("Produto enviado ao Kafka"),
                                throwable -> {
                                    logger.info("Falha no Envio do Produto ao Kafka");
                                    throwable.printStackTrace();
                                });
                        logger.info("Produto cadastrado: " + produto.toString());
                        return mapper.toProdutoDTO(produto);
                    })
                )
            ).onFailure(PersistenceException.class)
            .transform(throwable -> {
                throwable.printStackTrace();
                return new DataFalhaNaOperacaoException("Falha em cadastrar o Produto");
            });
    }
}