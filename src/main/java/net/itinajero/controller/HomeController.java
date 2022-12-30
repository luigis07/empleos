package net.itinajero.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.itinajero.model.Perfil;
import net.itinajero.model.Usuario;
import net.itinajero.model.Vacante;
import net.itinajero.service.ICategoriasService;
import net.itinajero.service.IUsuariosService;
import net.itinajero.service.IVacantesService;

@Controller
public class HomeController {

	@Autowired
	private ICategoriasService categoriasService;

	@Autowired
	private IVacantesService vacantesService;

	@Autowired
	private IUsuariosService usuariosService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/index")
	public String mostrarIndex(Authentication auth, HttpSession session) {
		String username = auth.getName();
		System.out.println("Nombre del usuario: " + username);
		
		for (GrantedAuthority rol : auth.getAuthorities()) {
			System.out.println("Rol: " + rol.getAuthority());
		}
		
		if (session.getAttribute("usuario") == null) {
			Usuario usuario = usuariosService.buscarPorUsername(username);
			usuario.setPassword(null); //Asi no se almacena en la sesion
			System.out.println("Usuario: " + usuario);
			session.setAttribute("usuario", usuario);
		}
		
		return "redirect:/";
	}

	@GetMapping("/signup")
	public String registrarse(Usuario usuario) {
		return "formRegistro";
	}

	@PostMapping("/signup")
	public String guardarRegistro(Usuario usuario, RedirectAttributes attributes) {
		
		String pwdPlano = usuario.getPassword();
		String pwdEncriptado = passwordEncoder.encode(pwdPlano);
		usuario.setPassword(pwdEncriptado);
		usuario.setEstatus(1); // Activado por defecto
		usuario.setFechaRegistro(new Date());

		// Creamos el Perfil que le asignaos al usuario nuevo
		Perfil perfil = new Perfil();
		perfil.setId(3); // Perfil USUARIO
		usuario.agregar(perfil);

		// Guardamos el usuario en la base de datos. El Perfil se guarda automaticamente
		usuariosService.guardar(usuario);
		attributes.addFlashAttribute("msg", "El registro fue guardado correctamente!");

		return "redirect:/usuarios/index";
	}

	@GetMapping("/tabla")
	public String mostrarTabla(Model model) {
		List<Vacante> lista = vacantesService.buscarTodas();
		model.addAttribute("vacantes", lista);

		return "tabla";
	}

	@GetMapping("/detalle")
	public String mostrarDetalle(Model model) {
		Vacante vacante = new Vacante();
		vacante.setNombre("Ingeniero de comunicaciones");
		vacante.setDescripcion("Se solicita ingeniero para dar soporte a intranet");
		vacante.setFecha(new Date());
		vacante.setSalario(9700.0);
		model.addAttribute("vacante", vacante);
		return "detalle";
	}

	@GetMapping("/listado")
	public String mostrarListado(Model model) {
		List<String> lista = new LinkedList<String>();
		lista.add("Ingeniero  de Sistemas");
		lista.add("Auxiliar de Contabilidad");
		lista.add("Vendedor");
		lista.add("Arquitecto");

		model.addAttribute("empleos", lista);

		return "listado";
	}

	@GetMapping("/")
	public String mostrarHome(Model model) {
//		List<Vacante> lista = vacantesService.buscarTodas();
//		model.addAttribute("vacantes", lista);
		return "home";
	}

	@GetMapping("/search")
	public String buscar(@ModelAttribute("search") Vacante vacante, Model model) {
		System.out.println("Buscando por : " + vacante);

		ExampleMatcher matcher = ExampleMatcher.
		// where descripcion like '%?%'
				matching().withMatcher("descripcion", ExampleMatcher.GenericPropertyMatchers.contains());

		Example<Vacante> example = Example.of(vacante, matcher);
		List<Vacante> lista = vacantesService.buscarByExample(example);
		model.addAttribute("vacantes", lista);
		return "home";
	}
	
	@GetMapping("/login")
	public String mostrarLogin() {
		return "formLogin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, null, null);
		return "redirect:/loogin";
	}
	
	@GetMapping("/bcrypt/{texto}")
	@ResponseBody
	public String encriptar(@PathVariable("texto") String texto) {
		return texto + " Encriptado en Bcrypt: " + passwordEncoder.encode(texto);
	}
	
	/**
	 * InitBinder para Strings si los detecta vacios en el Data Binding los settea a
	 * NULL
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute
	public void setGenericos(Model model) {
		Vacante vacanteSearch = new Vacante();
		vacanteSearch.reset();
		model.addAttribute("vacantes", vacantesService.buscarDestacadas());
		model.addAttribute("categorias", vacantesService.buscarTodas());
		model.addAttribute("search", vacanteSearch);
	}

}
