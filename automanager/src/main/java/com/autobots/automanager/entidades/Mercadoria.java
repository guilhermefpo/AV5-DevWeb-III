package com.autobots.automanager.entidades;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mercadoria")
public class Mercadoria extends RepresentationModel<Mercadoria> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Nome da mercadoria é obrigatório")
    private String nome;
    private String descricao;
    @PositiveOrZero(message = "Estoque não pode ser negativo")
    private Integer estoque;
    @NotNull(message = "Preço é obrigatório")
    @PositiveOrZero(message = "Preço não pode ser negativo")
    private BigDecimal preco;
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    @JsonIgnore
    private Empresa empresa;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getEmpresaId() {
        return empresa == null ? null : empresa.getId();
    }

    @PrePersist
    void definirDataCadastro() { if (dataCadastro == null) dataCadastro = LocalDateTime.now(); }
}
