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
@Table(name = "servico")
public class Servico extends RepresentationModel<Servico> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;
    private String descricao;
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
