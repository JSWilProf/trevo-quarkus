package br.senai.sp.informatica.trevobk.propostas.resource;

import br.senai.sp.informatica.trevobk.propostas.model.Produto;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

@ApplicationScoped
public class ProdutoResource {
    @Inject
    private Logger logger;

    @Incoming("produto")
    @Transactional
    public void salvar(Produto produto) {
        logger.info("Produto para cadastrar: " + produto.toString());
        try {
            logger.info("Produto recebido: " + produto.toString());
            Produto.getEntityManager().merge(produto).persist();
            logger.info("Produto Salvo: " + produto.toString());
        } catch (PersistenceException ex) {
            logger.errorv("Falha ao salvar a Produto %s", produto.getNome());
            ex.printStackTrace();
        }
    }
}
