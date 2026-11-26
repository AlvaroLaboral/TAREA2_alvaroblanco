package vista;

import java.util.Scanner;

import dao.ConexionBD;
import servicio.SesionServicio;

public class Main {

	static Scanner sc = new Scanner(System.in);
	static SesionServicio sesionServicio = new SesionServicio();

	public static void main(String[] args) {

		if (ConexionBD.getConexion() == null) {
			System.out.println(Color.ROJO + Color.NEGRITA + "No se pudo conectar con la base de datos. El programa se cerrará." + Color.RESET);
			return;
		}
		menuInvitado();
		ConexionBD.cerrarConexion();
	}

	static void menuInvitado() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ INVITADO ---" + Color.RESET);
			System.out.println();
			System.out.println(Color.VERDE + "1. Login" + Color.RESET);
			System.out.println(Color.ROJO + "0. Salir" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 1 -> {
				login();
				if (sesionServicio.hayUsuarioLogueado()) {
					menuSegunPerfil();
				}
			}
			case 0 -> System.out.println(Color.VERDE + "Hasta pronto" + Color.RESET);
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void menuSegunPerfil() {
		switch (sesionServicio.getSesion().getPerfil()) {
		case SECRETARIADO -> menuSecretariado();
		case ENFERMERIA -> menuEnfermeria();
		case CIRUGIA -> menuCirugia();
		case ADMIN -> menuAdmin();
		default -> {
		}
		}
	}

	static void menuSecretariado() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ SECRETARIADO (" + sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
			System.out.println();
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 0 -> logout();
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void menuEnfermeria() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ ENFERMERÍA (" + sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
			System.out.println();
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 0 -> logout();
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void menuCirugia() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ CIRUGÍA (" + sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
			System.out.println();
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 0 -> logout();
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void menuAdmin() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ ADMINISTRADOR ---" + Color.RESET);
			System.out.println();
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 0 -> logout();
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void login() {
		String usuario = pedir("Introduce nombre de usuario:");
		String contrasenia = pedir("Introduce la contraseña:");
		String error = sesionServicio.login(usuario, contrasenia);
		if (error == null) {
			System.out.println(Color.VERDE + "Login correcto. Bienvenido/a, " + sesionServicio.getSesion().getNombre()
					+ ", tu rol es " + sesionServicio.getSesion().getPerfil() + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + error + Color.RESET);
		}
	}

	static void logout() {
		System.out.println("Sesión cerrada. Hasta luego, " + sesionServicio.getSesion().getNombre());
		sesionServicio.logout();
	}

	static int leerOpcion() {
		try {
			return Integer.parseInt(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println(Color.ROJO + Color.NEGRITA + "Tienes que escribir un número" + Color.RESET);
			return -1;
		}
	}

	static String pedir(String mensaje) {
		System.out.println(mensaje);
		return sc.nextLine().trim();
	}
}