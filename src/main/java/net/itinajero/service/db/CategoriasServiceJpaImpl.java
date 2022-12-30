package net.itinajero.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.itinajero.model.Categoria;
import net.itinajero.repository.CategoriasRepository;
import net.itinajero.service.ICategoriasService;

@Service
@Primary
public class CategoriasServiceJpaImpl implements ICategoriasService {

	@Autowired
	private CategoriasRepository categoriasRepository;

	public void guardar(Categoria categoria) {
		categoriasRepository.save(categoria);
	}

	@Override
	public List<Categoria> buscarTodas() {
		return categoriasRepository.findAll();
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		Optional<Categoria> optional = categoriasRepository.findById(idCategoria);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void eliminar(Integer idCategoria) {
		categoriasRepository.deleteById(idCategoria);
	}

	@Override
	public Page<Categoria> buscarTodas(Pageable page) {
		return categoriasRepository.findAll(page);
	}

}
