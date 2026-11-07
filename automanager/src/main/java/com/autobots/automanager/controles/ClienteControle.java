package com.autobots.automanager.controles;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.autobots.automanager.dtos.ClienteDTO;
import com.autobots.automanager.dtos.ClienteRespostaDTO;
import com.autobots.automanager.servicos.ClienteServicos;
import com.autobots.automanager.seguranca.AcessoUsuario;

@RestController
@RequestMapping("/cliente")
@Validated
public class ClienteControle {

	@Autowired
	private ClienteServicos clienteServicos;
	@Autowired
	private AcessoUsuario acessoUsuario;

	@GetMapping("/{id}")
	@PreAuthorize("@acessoUsuario.podeLerCliente(#id)")
	@ResponseStatus(HttpStatus.OK)
	public ClienteRespostaDTO obterCliente(@PathVariable("id") @P("id") long id) {
		return links(clienteServicos.buscarPorId(id));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMINISTRADOR','GERENTE','VENDEDOR','CLIENTE')")
	@ResponseStatus(HttpStatus.OK)
	public List<ClienteRespostaDTO> obterClientes() {
		List<ClienteRespostaDTO> clientes = acessoUsuario.ehCliente()
				? clienteServicos.buscarClientesPorCpf(acessoUsuario.documentoAtual())
				: clienteServicos.buscarClientes();
		return clientes.stream().map(this::links).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClienteRespostaDTO cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDto,
			@org.springframework.web.bind.annotation.RequestParam Long empresaId) {
		return links(clienteServicos.cadastrarCliente(clienteDto, empresaId));
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ClienteRespostaDTO atualizarCliente(@PathVariable("id") long id, @RequestBody ClienteDTO atualizacao) {
		return links(clienteServicos.atualizarCliente(id, atualizacao));
	}

	private ClienteRespostaDTO links(ClienteRespostaDTO cliente) {
		cliente.add(linkTo(methodOn(ClienteControle.class).obterCliente(cliente.getId())).withSelfRel());
		cliente.add(linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("clientes"));
		return cliente;
	}

	@SuppressWarnings("null")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirCliente(@PathVariable("id") long id) {
		clienteServicos.excluirCliente(id);
	}

}
