package br.senai.sp.informatica.trevobk.propostas.components.services;

import br.senai.sp.informatica.trevobk.propostas.model.Proposta;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

@ApplicationScoped
public class ElasticService {
    private RestClientTransport transport;
    private ElasticsearchClient client;
    @Inject
    private Logger logger;
    @ConfigProperty(name = "elasticsearsh.host")
    String elasticHost;
    @ConfigProperty(name = "elasticsearsh.port")
    int elasticPort;
    @ConfigProperty(name = "elasticsearsh.user")
    String elasticUser;
    @ConfigProperty(name = "elasticsearsh.password")
    String elasticPassword;

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    void startup(@Observes StartupEvent event) {
        logger.infov("Iniciando Cliente...");
        try {
            var is = ElasticService.class.getClassLoader().getResourceAsStream("/ca/ca.crt");
            var factory = CertificateFactory.getInstance("X.509");
            var ca = factory.generateCertificate(is);
            var store = KeyStore.getInstance("pkcs12");
            store.load(null, null);
            store.setCertificateEntry("ca", ca);
            var context = SSLContexts.custom().loadTrustMaterial(store, null).build();

            credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticUser, elasticPassword));
            var restClient = RestClient.builder(
                new HttpHost(elasticHost, elasticPort, "https"))
                .setHttpClientConfigCallback(http ->
                    http.setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(context))
                .build();

            transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            client = new ElasticsearchClient(transport);

            var response = restClient.performRequest(new Request("GET", "/"));
            logger.infov("Conectado", EntityUtils.toString(response.getEntity()));
        } catch (Exception ex) {
            logger.errorv("Falha no acesso ao Elasticsearch Server", ex.getMessage());
            ex.printStackTrace();
        }
    }

    void shutdown(@Observes ShutdownEvent event) {
        logger.error("Finalizando Cliente....");
        try {
            transport.close();
        } catch (IOException ex) {
           logger.errorv("Erro finalizando o ElasticService", ex.getMessage());
        }
    }

    public void indexa(Proposta proposta) {
        try {
            client.index(req -> req.index("pedidos")
                .id(proposta.getEmail())
                .document(proposta));
            var produto = proposta.getProduto();
            client.index(req -> req.index("produtos")
                .id(produto.getNome())
                .document(produto));
        } catch (Exception ex) {
            logger.errorv("Falha ao indexar a Proposta nº " + proposta.getIdProposta(), ex.getMessage());
        }
    }
}
