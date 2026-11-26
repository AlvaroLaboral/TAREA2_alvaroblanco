package servicio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import dao.AccesoDatosException;
import dao.EmpleadoDAO;
import modelo.Perfil;
import modelo.Sesion;

public class SesionServicio {

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private Sesion sesion = new Sesion(null, "Invitado", Perfil.INVITADO);

	public Sesion getSesion() {
		return sesion;
	}

	public boolean hayUsuarioLogueado() {
		return sesion.getPerfil() != Perfil.INVITADO;
	}

	public String login(String usuario, String contrasenia) {
		if (hayUsuarioLogueado()) {
			return "Ya hay una sesión iniciada. Haz logout primero.";
		}
		if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) {
			return "El usuario y la contraseña no pueden estar vacíos.";
		}
		if (esAdmin(usuario, contrasenia)) {
			sesion = new Sesion(usuario, "Administrador", Perfil.ADMIN);
			return null;
		}
		Sesion encontrada;
		try {
			encontrada = empleadoDAO.buscarPorCredenciales(usuario, contrasenia);
		} catch (AccesoDatosException e) {
			return "Error al acceder a la base de datos. Inténtalo más tarde.";
		}
		if (encontrada == null) {
			return "Usuario o contraseña incorrectos.";
		}
		sesion = encontrada;
		return null;
	}

	public void logout() {
		sesion = new Sesion(null, "Invitado", Perfil.INVITADO);
	}

	private boolean esAdmin(String usuario, String contrasenia) {
		return usuario.equals(leerPropiedad("admin.usuario"))
				&& contrasenia.equals(leerPropiedad("admin.password"));
	}

	private String leerPropiedad(String clave) {
		Properties props = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (is == null) {
				return null;
			}
			props.load(is);
		} catch (IOException e) {
			return null;
		}
		return props.getProperty(clave);
	}
}