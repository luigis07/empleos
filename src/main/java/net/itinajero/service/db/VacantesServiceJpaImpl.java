package net.itinajero.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.itinajero.model.Vacante;
import net.itinajero.repository.VacantesRepository;
import net.itinajero.service.IVacantesService;

@Service
@Primary
public class VacantesServiceJpaImpl implements IVacantesService {

	@Autowired
	VacantesRepository vacantesRepository;

	public List<Vacante> buscarTodas() {
		return vacantesRepository.findAll();
	}

	public Vacante buscarPorId(Integer idVacante) {
		Optional<Vacante> optional = vacantesRepository.findById(idVacante);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	public void guardar(Vacante vacante) {
		vacantesRepository.save(vacante);
	}

	@Override
	public List<Vacante> buscarDestacadas() {
		return vacantesRepository.findByDestacadoAndEstatusOrderByIdDesc(1, "Aprobada");
	}

	@Override
	public void eliminar(Integer idVacante) {
		vacantesRepository.deleteById(idVacante);
	}

	@Override
	public List<Vacante> buscarByExample(Example<Vacante> example) {
		return vacantesRepository.findAll(example);
	}

	@Override
	public Page<Vacante> buscarTodas(Pageable page) {
		return vacantesRepository.findAll(page);
	}

}
