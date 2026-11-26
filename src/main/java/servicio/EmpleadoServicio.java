package servicio;

import dao.AccesoDatosException;
import dao.EmpleadoDAO;
import modelo.Cirujano;
import modelo.Perfil;

public class EmpleadoServicio {

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private SesionServicio sesionServicio;

	public EmpleadoServicio(SesionServicio sesionServicio) {
		this.sesionServicio = sesionServicio;
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
}