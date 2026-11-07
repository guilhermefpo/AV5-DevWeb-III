package com.autobots.automanager.entidades;

import javax.persistence.*;
import com.autobots.automanager.enumeracao.PerfilUsuario;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Entity
@Table(name = "usuario")
public class Usuario extends RepresentationModel<Usuario> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Nome é obrigatório") private String nome;
    @Column(unique = true)
    private String documento;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Email email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Perfil é obrigatório") private PerfilUsuario perfil;
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    @JsonIgnore
    private Empresa empresa;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Credencial credencial;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Documento> documentos = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Endereco endereco;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<Telefone> telefones = new ArrayList<>();
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getEmpresaId() {
        return empresa == null ? null : empresa.getId();
    }
}
