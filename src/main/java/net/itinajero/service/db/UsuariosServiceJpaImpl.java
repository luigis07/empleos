package net.itinajero.service.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import net.itinajero.model.Usuario;
import net.itinajero.repository.UsuariosRepository;
import net.itinajero.service.IUsuariosService;

@Service
@Primary
public class UsuariosServiceJpaImpl implements IUsuariosService {

	@Autowired
	private UsuariosRepository usuariosRepository;

	@Override
	public void guardar(Usuario usuario) {
		usuariosRepository.save(usuario);
	}

	@Override
	public void eliminar(Integer idUsuario) {
		usuariosRepository.deleteById(idUsuario);
	}

	@Override
	public List<Usuario> buscarTodos() {
		return usuariosRepository.findAll();
	}

	@Override
	public Usuario buscarPorUsername(String username) {
		return usuariosRepository.findByUsername(username);
	}

}
