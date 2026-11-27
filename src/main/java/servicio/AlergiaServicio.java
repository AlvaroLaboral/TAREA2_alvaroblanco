package servicio;

import java.util.ArrayList;
import java.util.List;

import dao.AccesoDatosException;
import dao.AlergiaDAO;
import modelo.Alergia;
import modelo.Perfil;

public class AlergiaServicio {

	private AlergiaDAO alergiaDAO = new AlergiaDAO();
	private SesionServicio sesionServicio;

	public AlergiaServicio(SesionServicio sesionServicio) {
		this.sesionServicio = sesionServicio;
	}

	public boolean puedeGestionarAlergias() {
		Perfil p = sesionServicio.getSesion().getPerfil();
		return p == Perfil.ENFERMERIA || p == Perfil.ADMIN;
	}

	public List<Alergia> listarAlergias() {
		try {
			return alergiaDAO.listar();
		} catch (AccesoDatosException e) {
			return new ArrayList<>();
		}
	}

	public String avisoLectura() {
		int n = alergiaDAO.getFicherosIlegibles();
		if (n > 0) {
			return "Aviso: " + n + " fichero(s) de alergias no se han podido leer.";
		}
		return null;
	}

	public Alergia buscarAlergia(Long id) {
		return alergiaDAO.buscar(id);
	}

	public Long siguienteIdAlergia() {
		try {
			return alergiaDAO.mayorId() + 1;
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

	public String validarDosis(String dosis) {
		try {
			int valor = Integer.parseInt(dosis.trim());
			if (valor <= 0) {
				return "La dosis máxima debe ser mayor que 0.";
			}
			return null;
		} catch (NumberFormatException | NullPointerException e) {
			return "La dosis máxima debe ser un número entero de miligramos.";
		}
	}

	public String guardarAlergia(Alergia a) {
		if (!puedeGestionarAlergias()) {
			return "No tienes permisos para gestionar alergias.";
		}
		try {
			alergiaDAO.guardar(a);
		} catch (AccesoDatosException e) {
			return "No se pudo guardar la alergia.";
		}
		return null;
	}
}