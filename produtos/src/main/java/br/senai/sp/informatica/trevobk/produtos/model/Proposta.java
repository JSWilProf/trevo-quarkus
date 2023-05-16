package br.senai.sp.informatica.trevobk.produtos.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Proposta extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProposta;
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String telefone;
    @ManyToOne
    @JoinColumn(name = "idProduto")
    private Produto produto;
}
