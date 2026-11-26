package vista;

import dao.ConexionBD;

public class Main {

	public static void main(String[] args) {
		if (ConexionBD.getConexion() != null) {
			System.out.println("Conectado a la BD");
		}
		ConexionBD.cerrarConexion();
	}
}