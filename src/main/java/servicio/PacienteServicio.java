package servicio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import dao.AccesoDatosException;
import dao.AlergiaDAO;
import dao.PacienteDAO;
import modelo.Alergia;
import modelo.Paciente;
import modelo.Perfil;

public class PacienteServicio {

	public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);

	private PacienteDAO pacienteDAO = new PacienteDAO();
	private AlergiaDAO alergiaDAO = new AlergiaDAO();
	private SesionServicio sesionServicio;

	public PacienteServicio(SesionServicio sesionServicio) {
		this.sesionServicio = sesionServicio;
	}

	public boolean puedeGestionarPacientes() {
		Perfil p = sesionServicio.getSesion().getPerfil();
		return p == Perfil.SECRETARIADO || p == Perfil.ADMIN;
	}

	public String validarTexto(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			return "El campo " + campo + " no puede estar vacío.";
		}
		return null;
	}

	public String validarTelefono(String telefono) {
		if (telefono == null || !telefono.matches("[6789]\\d{8}")) {
			return "El teléfono debe tener 9 dígitos y empezar por 6, 7, 8 o 9.";
		}
		return null;
	}

	public String validarEmail(String email) {
		if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$")) {
			return "El email no tiene un formato válido.";
		}
		return null;
	}

	public String validarFecha(String fecha) {
		try {
			LocalDate f = LocalDate.parse(fecha, FORMATO_FECHA);
			if (f.isAfter(LocalDate.now())) {
				return "La fecha de nacimiento no puede ser futura.";
			}
			if (f.isBefore(LocalDate.now().minusYears(130))) {
				return "La fecha de nacimiento no es válida.";
			}
			return null;
		} catch (DateTimeParseException e) {
			return "La fecha debe tener el formato dd/mm/aaaa y ser una fecha real.";
		}
	}

	public String validarNifNie(String doc, Long idPaciente) {
		if (doc == null) {
			return "El NIF/NIE no puede estar vacío.";
		}
		String d = doc.trim().toUpperCase();
		if (!d.matches("[0-9XYZ]\\d{7}[A-Z]")) {
			return "El NIF/NIE debe tener 8 caracteres y una letra (ej: 12345678Z o X1234567L).";
		}
		String numero = d.substring(0, 8).replace('X', '0').replace('Y', '1').replace('Z', '2');
		char letraCorrecta = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(Integer.parseInt(numero) % 23);
		if (d.charAt(8) != letraCorrecta) {
			return "La letra del NIF/NIE no es correcta.";
		}
		try {
			if (pacienteDAO.existeNifNie(d, idPaciente)) {
				return "Ya existe un paciente con ese NIF/NIE.";
			}
		} catch (AccesoDatosException e) {
			return "Error al acceder a la base de datos.";
		}
		return null;
	}

	public List<Alergia> listarAlergias() {
		try {
			return alergiaDAO.listar();
		} catch (AccesoDatosException e) {
			return new ArrayList<>();
		}
	}

	public String altaPaciente(Paciente p) {
		if (!puedeGestionarPacientes()) {
			return "No tienes permisos para gestionar pacientes.";
		}
		try {
			pacienteDAO.insertar(p);
		} catch (AccesoDatosException e) {
			return "No se pudo dar de alta el paciente. No se ha guardado nada.";
		}
		return null;
	}

	public String modificarPaciente(Paciente p) {
		if (!puedeGestionarPacientes()) {
			return "No tienes permisos para gestionar pacientes.";
		}
		try {
			pacienteDAO.actualizar(p);
		} catch (AccesoDatosException e) {
			return "No se pudo modificar el paciente. No se ha cambiado nada.";
		}
		return null;
	}

	public Paciente buscarPorId(Long id) {
		try {
			Paciente p = pacienteDAO.buscarPorId(id);
			if (p != null) {
				cargarAlergias(p);
			}
			return p;
		} catch (AccesoDatosException e) {
			return null;
		}
	}

	public List<Paciente> buscar(String nombre, String telefono, String email, String nifNie) {
		try {
			List<Paciente> pacientes = pacienteDAO.buscar(nombre, telefono, email, nifNie);
			for (Paciente p : pacientes) {
				cargarAlergias(p);
			}
			return pacientes;
		} catch (AccesoDatosException e) {
			return null;
		}
	}

	private void cargarAlergias(Paciente p) {
		List<Alergia> alergias = new ArrayList<>();
		for (Long id : pacienteDAO.idsAlergias(p.getHistorial().getNumHistoria())) {
			Alergia a = alergiaDAO.buscar(id);
			if (a != null) {
				alergias.add(a);
			}
		}
		p.getHistorial().setAlergias(alergias);
	}
}