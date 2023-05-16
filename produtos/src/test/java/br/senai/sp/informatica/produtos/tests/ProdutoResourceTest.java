package br.senai.sp.informatica.produtos.tests;

import br.senai.sp.informatica.produtos.utils.TokenUtils;
import br.senai.sp.informatica.trevobk.produtos.model.dto.ProdutoDTO;
import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.approvaltests.JsonApprovals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

// IMPORTANTE: Para execução dos testes é necessário que o flyway esteja ativado

@QuarkusTest
@QuarkusTestResource(ProdutoTestLifecycleManager.class)
public class ProdutoResourceTest {
    private String token;

    @BeforeEach
    public void geraToken() throws Exception {
        token = TokenUtils.generateTokenString("/JWTProprietarioClaims.json", null);
    }

    private RequestSpecification given() {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .header(new Header("Authorization", "Bearer " + token));
    }

    @Test
    public void testBuscarProdutos() {
        String resultado = given()
            .when().get("/api/produto/{idProduto}", 1)
            .then()
            .statusCode(200)
            .extract().asString();
        JsonApprovals.verifyJson(resultado);
    }

    @Test
    public void testSalvarProduto() {
        var produto = new ProdutoDTO();
        produto.setNome("Galaga II XP");
        produto.setDescricao("Melhor Performance nas Aplicações.");
        produto.setArea("Até 50 ha");
        produto.setCulturas(Set.of("Café", "Frutas"));

        String resultado = given()
            .header("Content-type", "application/json")
            .and()
            .body(produto)
            .when().post("/api/produto/")
            .then()
            .statusCode(200)
            .extract().asString();
        JsonApprovals.verifyJson(resultado);
    }
}
