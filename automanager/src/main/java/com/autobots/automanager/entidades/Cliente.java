package com.autobots.automanager.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Table(name = "cliente")
@Data
@Entity
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String nome;

	@Column
	private String cpf;

	@Column
	private String nomeSocial;

	@Column
	private Date dataNascimento;

	@Column
	private Date dataCadastro;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Documento> documentos = new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "empresa_id")
	@JsonIgnore
	private Empresa empresa;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public Long getEmpresaId() { return empresa == null ? null : empresa.getId(); }
}
