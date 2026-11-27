package servicio;

import java.util.List;

import dao.AccesoDatosException;
import dao.EmpleadoDAO;
import modelo.Cirujano;
import modelo.Empleado;
import modelo.Especialidad;
import modelo.Perfil;

public class EmpleadoServicio {

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private SesionServicio sesionServicio;

	public EmpleadoServicio(SesionServicio sesionServicio) {
		this.sesionServicio = sesionServicio;
	}

	public boolean puedeGestionarEmpleados() {
		Perfil p = sesionServicio.getSesion().getPerfil();
		return p == Perfil.SECRETARIADO || p == Perfil.ADMIN;
	}

	public Cirujano verMiFicha() {
		if (sesionServicio.getSesion().getPerfil() != Perfil.CIRUGIA) {
			return null;
		}
		try {
			return empleadoDAO.buscarCirujanoPorUsuario(sesionServicio.getSesion().getUsuario());
		} catch (AccesoDatosException e) {
			return null;
		}
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

	public String validarNifNie(String doc) {
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
			if (empleadoDAO.existeNifNie(d)) {
				return "Ya existe un empleado con ese NIF/NIE.";
			}
		} catch (AccesoDatosException e) {
			return "Error al acceder a la base de datos.";
		}
		return null;
	}

	public String validarUsuario(String usuario) {
		if (usuario == null || usuario.isBlank()) {
			return "El nombre de usuario no puede estar vacío.";
		}
		if (usuario.contains(" ")) {
			return "El nombre de usuario no puede contener espacios.";
		}
		if (sesionServicio.esUsuarioAdmin(usuario)) {
			return "Ya existe un usuario con ese nombre.";
		}
		try {
			if (empleadoDAO.existeUsuario(usuario)) {
				return "Ya existe un usuario con ese nombre.";
			}
		} catch (AccesoDatosException e) {
			return "Error al acceder a la base de datos.";
		}
		return null;
	}

	public String validarPassword(String password) {
		if (password == null || password.isBlank()) {
			return "La contraseña no puede estar vacía.";
		}
		if (password.contains(" ")) {
			return "La contraseña no puede contener espacios.";
		}
		return null;
	}

	public String validarEspecialidades(List<Especialidad> especialidades) {
		if (especialidades == null || especialidades.isEmpty()) {
			return "Un cirujano debe tener al menos una especialidad.";
		}
		return null;
	}

	public String altaEmpleado(Empleado e) {
		if (!puedeGestionarEmpleados()) {
			return "No tienes permisos para dar de alta empleados.";
		}
		String error = validarUsuario(e.getUsuario());
		if (error != null) {
			return error;
		}
		try {
			empleadoDAO.insertar(e);
		} catch (AccesoDatosException ex) {
			return "No se pudo dar de alta el empleado. No se ha guardado nada.";
		}
		return null;
	}
}