package br.senai.sp.informatica.trevobk.propostas.model;

import javax.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Produto extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(nullable = false, columnDefinition = "CHARACTER LARGE OBJECT")
    private String descricao;
    @OneToOne
    @JoinColumn(name = "idArea")
    private Area area;
    @ManyToMany
    @JoinTable(name = "culturas_do_produto",
        joinColumns = {@JoinColumn(name = "idProduto")},
        inverseJoinColumns = {@JoinColumn(name = "idCultura")})
    private Set<Cultura> culturas;
}
