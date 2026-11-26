package vista;

import java.util.List;
import java.util.Scanner;

import dao.ConexionBD;
import modelo.Alergia;
import modelo.Cirujano;
import modelo.Especialidad;
import modelo.Medicamento;
import servicio.AlergiaServicio;
import servicio.EmpleadoServicio;
import servicio.SesionServicio;

public class Main {

	static Scanner sc = new Scanner(System.in);
	static SesionServicio sesionServicio = new SesionServicio();
	static AlergiaServicio alergiaServicio = new AlergiaServicio(sesionServicio);
	static EmpleadoServicio empleadoServicio = new EmpleadoServicio(sesionServicio);

	public static void main(String[] args) {

		if (ConexionBD.getConexion() == null) {
			System.out.println(Color.ROJO + Color.NEGRITA
					+ "No se pudo conectar con la base de datos. El programa se cerrará." + Color.RESET);
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
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ SECRETARIADO ("
					+ sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
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
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ ENFERMERÍA ("
					+ sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
			System.out.println();
			System.out.println("1. Ver alergias");
			System.out.println("2. Nueva alergia");
			System.out.println("3. Editar alergia");
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 0 -> logout();
			case 1 -> verAlergias();
			case 2 -> nuevaAlergia();
			case 3 -> editarAlergia();
			case -1 -> {
			}
			default -> System.out.println(Color.ROJO + Color.NEGRITA + "Opción incorrecta" + Color.RESET);
			}
		} while (opcion != 0);
	}

	static void menuCirugia() {
		int opcion;
		do {
			System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MENÚ CIRUGÍA ("
					+ sesionServicio.getSesion().getNombre() + ") ---" + Color.RESET);
			System.out.println();
			System.out.println("1. Ver mi ficha");
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 1 -> verMiFicha();
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
			System.out.println("1. Ver alergias");
			System.out.println("2. Nueva alergia");
			System.out.println("3. Editar alergia");
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 1 -> verAlergias();
			case 2 -> nuevaAlergia();
			case 3 -> editarAlergia();
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

	static void verAlergias() {
		List<Alergia> alergias = alergiaServicio.listarAlergias();
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- ALERGIAS REGISTRADAS ---" + Color.RESET);
		if (alergias.isEmpty()) {
			System.out.println("No hay alergias registradas.");
			return;
		}
		System.out.printf("%-4s %-20s %-20s %-20s %s%n", "ID", "Alergia", "Medicamento", "Principio activo",
				"Dosis máx.");
		for (Alergia a : alergias) {
			mostrarAlergia(a);
		}
	}

	static void nuevaAlergia() {
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- NUEVA ALERGIA ---" + Color.RESET);

		String nombre = pedir("Nombre de la alergia:");
		if (errorAlergia(alergiaServicio.validarTexto(nombre, "nombre de la alergia")))
			return;

		String nombreMed = pedir("Nombre del medicamento:");
		if (errorAlergia(alergiaServicio.validarTexto(nombreMed, "nombre del medicamento")))
			return;

		String principio = pedir("Principio activo:");
		if (errorAlergia(alergiaServicio.validarTexto(principio, "principio activo")))
			return;

		String dosisTexto = pedir("Dosis máxima diaria (mg):");
		if (errorAlergia(alergiaServicio.validarDosis(dosisTexto)))
			return;

		Long id = alergiaServicio.siguienteIdAlergia();
		Medicamento m = new Medicamento(id, nombreMed, principio, Integer.parseInt(dosisTexto));
		Alergia a = new Alergia(id, nombre, m);

		System.out.println("Se va a crear la siguiente alergia:");
		mostrarAlergia(a);
		if (!confirmar()) {
			System.out.println("Operación cancelada.");
			return;
		}

		String resultado = alergiaServicio.guardarAlergia(a);
		if (resultado == null) {
			System.out.println(Color.VERDE + "Alergia creada correctamente con id " + id + "." + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + resultado + Color.RESET);
		}
	}

	static void editarAlergia() {
		List<Alergia> alergias = alergiaServicio.listarAlergias();
		if (alergias.isEmpty()) {
			System.out.println("No hay alergias registradas para editar.");
			return;
		}

		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- EDITAR ALERGIA ---" + Color.RESET);
		for (Alergia a : alergias) {
			System.out.println(a.getIdAlergia() + ". " + a.getNombre());
		}

		long id = leerOpcion();
		Alergia a = alergiaServicio.buscarAlergia(id);
		if (a == null) {
			System.out.println(Color.ROJO + Color.NEGRITA + "No existe ninguna alergia con ese id." + Color.RESET);
			return;
		}

		Medicamento m = a.getMedicamento();
		System.out.println("Deja el campo en blanco para mantener el valor actual.");

		String nombre = pedir("Nombre de la alergia [" + a.getNombre() + "]:");
		if (nombre.isEmpty())
			nombre = a.getNombre();

		String nombreMed = pedir("Nombre del medicamento [" + m.getNombre() + "]:");
		if (nombreMed.isEmpty())
			nombreMed = m.getNombre();

		String principio = pedir("Principio activo [" + m.getPrincipioActivo() + "]:");
		if (principio.isEmpty())
			principio = m.getPrincipioActivo();

		String dosisTexto = pedir("Dosis máxima diaria en mg [" + m.getDosisMaxDiariaMg() + "]:");
		if (dosisTexto.isEmpty())
			dosisTexto = String.valueOf(m.getDosisMaxDiariaMg());
		if (errorAlergia(alergiaServicio.validarDosis(dosisTexto)))
			return;

		a.setNombre(nombre);
		m.setNombre(nombreMed);
		m.setPrincipioActivo(principio);
		m.setDosisMaxDiariaMg(Integer.parseInt(dosisTexto));

		System.out.println("La alergia quedará así:");
		mostrarAlergia(a);
		if (!confirmar()) {
			System.out.println("Operación cancelada. No se ha modificado nada.");
			return;
		}

		String resultado = alergiaServicio.guardarAlergia(a);
		if (resultado == null) {
			System.out.println(Color.VERDE + "Alergia actualizada correctamente." + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + resultado + Color.RESET);
		}
	}

	static void mostrarAlergia(Alergia a) {
		Medicamento m = a.getMedicamento();
		System.out.printf("%-4d %-20s %-20s %-20s %d mg/día%n", a.getIdAlergia(), a.getNombre(), m.getNombre(),
				m.getPrincipioActivo(), m.getDosisMaxDiariaMg());
	}

	static boolean confirmar() {
		System.out.println("¿Confirmas la operación? (S/N)");
		return sc.nextLine().trim().equalsIgnoreCase("S");
	}

	static boolean errorAlergia(String mensaje) {
		if (mensaje != null) {
			System.out.println(Color.ROJO + Color.NEGRITA + mensaje + " Operación cancelada." + Color.RESET);
			return true;
		}
		return false;
	}

	static void verMiFicha() {
		Cirujano c = empleadoServicio.verMiFicha();
		if (c == null) {
			System.out.println(Color.ROJO + Color.NEGRITA + "No se ha podido cargar tu ficha." + Color.RESET);
			return;
		}
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MI FICHA ---" + Color.RESET);
		System.out.printf("%-15s %s%n", "Nombre:", c.getNombreYapellidos());
		System.out.printf("%-15s %s%n", "NIF/NIE:", c.getNifNie());
		System.out.printf("%-15s %s%n", "Teléfono:", c.getTelefono());
		System.out.printf("%-15s %s%n", "Email:", c.getEmail());
		System.out.printf("%-15s %s%n", "Dirección:", c.getDireccion());
		System.out.println("Especialidades:");
		if (c.getEspecialidades().isEmpty()) {
			System.out.println("  (ninguna)");
		}
		for (Especialidad e : c.getEspecialidades()) {
			System.out.println("  - " + e.getNombre());
		}
	}
}