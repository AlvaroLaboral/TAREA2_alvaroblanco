package vista;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import dao.ConexionBD;
import modelo.Alergia;
import modelo.Categoria;
import modelo.Cirujano;
import modelo.Empleado;
import modelo.Enfermero;
import modelo.Especialidad;
import modelo.Historial;
import modelo.Medicamento;
import modelo.Paciente;
import modelo.Secretario;
import servicio.AlergiaServicio;
import servicio.EmpleadoServicio;
import servicio.PacienteServicio;
import servicio.SesionServicio;

public class Main {

	static Scanner sc = new Scanner(System.in);
	static SesionServicio sesionServicio = new SesionServicio();
	static AlergiaServicio alergiaServicio = new AlergiaServicio(sesionServicio);
	static EmpleadoServicio empleadoServicio = new EmpleadoServicio(sesionServicio);
	static PacienteServicio pacienteServicio = new PacienteServicio(sesionServicio);

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
			System.out.println("1. Nuevo empleado");
			System.out.println("2. Nuevo paciente");
			System.out.println("3. Modificar paciente");
			System.out.println("4. Buscar pacientes");
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 1 -> nuevoEmpleado();
			case 2 -> nuevoPaciente();
			case 3 -> modificarPaciente();
			case 4 -> buscarPacientes();
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
			System.out.println("1. Nuevo empleado");
			System.out.println("2. Nuevo paciente");
			System.out.println("3. Modificar paciente");
			System.out.println("4. Buscar pacientes");
			System.out.println("5. Ver alergias");
			System.out.println("6. Nueva alergia");
			System.out.println("7. Editar alergia");
			System.out.println(Color.ROJO + "0. Logout" + Color.RESET);
			opcion = leerOpcion();
			switch (opcion) {
			case 1 -> nuevoEmpleado();
			case 2 -> nuevoPaciente();
			case 3 -> modificarPaciente();
			case 4 -> buscarPacientes();
			case 5 -> verAlergias();
			case 6 -> nuevaAlergia();
			case 7 -> editarAlergia();
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
	
	static void nuevoEmpleado() {
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- NUEVO EMPLEADO ---" + Color.RESET);
		System.out.println("(Escribe 'cancelar' en cualquier campo para salir)");

		String tipoTexto = pedirValido("Tipo de empleado: 1. Secretario/a  2. Enfermero/a  3. Cirujano/a",
				v -> v.matches("[123]") ? null : "Elige 1, 2 o 3.");
		if (tipoTexto == null) { cancelado(); return; }
		int tipo = Integer.parseInt(tipoTexto);

		String nombre = pedirValido("Nombre y apellidos:", v -> empleadoServicio.validarTexto(v, "nombre y apellidos"));
		if (nombre == null) { cancelado(); return; }

		String telefono = pedirValido("Teléfono:", empleadoServicio::validarTelefono);
		if (telefono == null) { cancelado(); return; }

		String email = pedirValido("Email:", empleadoServicio::validarEmail);
		if (email == null) { cancelado(); return; }

		String nif = pedirValido("NIF/NIE:", empleadoServicio::validarNifNie);
		if (nif == null) { cancelado(); return; }
		nif = nif.toUpperCase();

		String direccion = pedirValido("Dirección:", v -> empleadoServicio.validarTexto(v, "dirección"));
		if (direccion == null) { cancelado(); return; }

		String usuario = pedirValido("Nombre de usuario:", empleadoServicio::validarUsuario);
		if (usuario == null) { cancelado(); return; }

		String password = pedirValido("Contraseña:", empleadoServicio::validarPassword);
		if (password == null) { cancelado(); return; }

		Empleado nuevo;
		switch (tipo) {
		case 1 -> {
			String anios = pedirValido("Años de experiencia en la clínica:",
					v -> v.matches("\\d+") ? null : "Debe ser un número entero mayor o igual que 0.");
			if (anios == null) { cancelado(); return; }
			nuevo = new Secretario(nombre, telefono, email, nif, direccion, usuario, password, Integer.parseInt(anios));
		}
		case 2 -> {
			String cat = pedirValido("Categoría (A, B o C):",
					v -> v.toUpperCase().matches("[ABC]") ? null : "La categoría debe ser A, B o C.");
			if (cat == null) { cancelado(); return; }
			nuevo = new Enfermero(nombre, telefono, email, nif, direccion, usuario, password,
					Categoria.valueOf(cat.toUpperCase()));
		}
		default -> {
			List<Especialidad> especialidades = pedirEspecialidades();
			while (empleadoServicio.validarEspecialidades(especialidades) != null) {
				System.out.println(Color.ROJO + Color.NEGRITA + "Un cirujano debe tener al menos una especialidad. Inténtalo de nuevo." + Color.RESET);
				especialidades = pedirEspecialidades();
			}
			nuevo = new Cirujano(nombre, telefono, email, nif, direccion, usuario, password, especialidades);
		}
		}

		System.out.println("Se va a dar de alta a " + nombre + " con usuario " + usuario + ".");
		if (!confirmar()) {
			cancelado();
			return;
		}

		String resultado = empleadoServicio.altaEmpleado(nuevo);
		if (resultado == null) {
			System.out.println(Color.VERDE + "Empleado dado de alta correctamente." + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + resultado + Color.RESET);
		}
	}

	static void cancelado() {
		System.out.println(Color.ROJO + "Registro cancelado." + Color.RESET);
	}
	static List<Especialidad> pedirEspecialidades() {
		List<Especialidad> lista = new ArrayList<>();
		String otra;
		do {
			System.out.println("Especialidades disponibles:");
			for (Especialidad e : Especialidad.values()) {
				if (!lista.contains(e)) {
					System.out.println(e.getId() + ". " + e.getNombre());
				}
			}
			String elegida = pedirValido("Elige una especialidad:", v -> {
				Especialidad e = v.matches("\\d+") ? Especialidad.porId(Integer.parseInt(v)) : null;
				if (e == null) {
					return "Esa especialidad no existe.";
				}
				if (lista.contains(e)) {
					return "Ya has elegido esa especialidad.";
				}
				return null;
			});
			if (elegida == null) {
				return null;
			}
			lista.add(Especialidad.porId(Integer.parseInt(elegida)));

			if (lista.size() == Especialidad.values().length) {
				System.out.println("Ya tiene todas las especialidades.");
				break;
			}
			otra = pedirValido("¿Quieres añadir otra especialidad? (S/N)",
					v -> v.equalsIgnoreCase("S") || v.equalsIgnoreCase("N") ? null : "Responde S o N.");
			if (otra == null) {
				return null;
			}
		} while (otra.equalsIgnoreCase("S"));
		return lista;
	}

	static boolean error(String mensaje) {
		if (mensaje != null) {
			System.out.println(Color.ROJO + Color.NEGRITA + mensaje + " Registro cancelado." + Color.RESET);
			return true;
		}
		return false;
	}
	
	static String pedirValido(String mensaje, Function<String, String> validador) {
		while (true) {
			String valor = pedir(mensaje);
			if (valor.equalsIgnoreCase("cancelar")) {
				return null;
			}
			String error = validador.apply(valor);
			if (error == null) {
				return valor;
			}
			System.out.println(Color.ROJO + Color.NEGRITA + error + " Inténtalo de nuevo." + Color.RESET);
		}
	}
	
	static String pedirValidoOpcional(String mensaje, String actual, Function<String, String> validador) {
		while (true) {
			String valor = pedir(mensaje + " [" + actual + "]:");
			if (valor.equalsIgnoreCase("cancelar")) {
				return null;
			}
			if (valor.isEmpty()) {
				return actual;
			}
			String error = validador.apply(valor);
			if (error == null) {
				return valor;
			}
			System.out.println(Color.ROJO + Color.NEGRITA + error + " Inténtalo de nuevo." + Color.RESET);
		}
	}

	static void nuevoPaciente() {
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- NUEVO PACIENTE ---" + Color.RESET);
		System.out.println("(Escribe 'cancelar' en cualquier campo para salir)");

		String nombre = pedirValido("Nombre completo:", v -> pacienteServicio.validarTexto(v, "nombre"));
		if (nombre == null) { cancelado(); return; }

		String fecha = pedirValido("Fecha de nacimiento (dd/mm/aaaa):", pacienteServicio::validarFecha);
		if (fecha == null) { cancelado(); return; }

		String telefono = pedirValido("Teléfono:", pacienteServicio::validarTelefono);
		if (telefono == null) { cancelado(); return; }

		String email = pedirValido("Email:", pacienteServicio::validarEmail);
		if (email == null) { cancelado(); return; }

		String direccion = pedirValido("Dirección:", v -> pacienteServicio.validarTexto(v, "dirección"));
		if (direccion == null) { cancelado(); return; }

		String nif = pedirValido("NIF/NIE:", v -> pacienteServicio.validarNifNie(v, null));
		if (nif == null) { cancelado(); return; }

		String descripcion = pedir("Descripción del historial (puede quedar vacía):");
		if (descripcion.equalsIgnoreCase("cancelar")) { cancelado(); return; }

		List<Alergia> alergias = pedirAlergias(new ArrayList<>());
		if (alergias == null) { cancelado(); return; }

		Historial h = new Historial(descripcion);
		h.setAlergias(alergias);
		Paciente p = new Paciente(nombre, LocalDate.parse(fecha, PacienteServicio.FORMATO_FECHA), telefono, email,
				direccion, nif.toUpperCase(), null, null, h);
		p.setIdPaciente(null);
		h.setNumHistoria(null);

		System.out.println("Se va a dar de alta el siguiente paciente:");
		mostrarPaciente(p);
		if (!confirmar()) { cancelado(); return; }

		String resultado = pacienteServicio.altaPaciente(p);
		if (resultado == null) {
			System.out.println(Color.VERDE + "Paciente dado de alta correctamente con nº de historia "
					+ p.getHistorial().getNumHistoria() + "." + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + resultado + Color.RESET);
		}
	}

	static void modificarPaciente() {
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- MODIFICAR PACIENTE ---" + Color.RESET);
		String idTexto = pedirValido("Id del paciente (búscalo antes en 'Buscar pacientes' si no lo sabes):",
				v -> v.matches("\\d+") ? null : "El id debe ser un número.");
		if (idTexto == null) { cancelado(); return; }
		Paciente p = pacienteServicio.buscarPorId(Long.parseLong(idTexto));
		if (p == null) {
			System.out.println(Color.ROJO + Color.NEGRITA + "No existe ningún paciente con ese id." + Color.RESET);
			return;
		}

		mostrarPaciente(p);
		System.out.println("Deja el campo en blanco para mantener el valor actual. Escribe 'cancelar' para salir.");

		String nombre = pedirValidoOpcional("Nombre completo", p.getNombre(), v -> pacienteServicio.validarTexto(v, "nombre"));
		if (nombre == null) { cancelado(); return; }

		String fecha = pedirValidoOpcional("Fecha de nacimiento", p.getFechaNacimiento().format(PacienteServicio.FORMATO_FECHA),
				pacienteServicio::validarFecha);
		if (fecha == null) { cancelado(); return; }

		String telefono = pedirValidoOpcional("Teléfono", p.getTelefono(), pacienteServicio::validarTelefono);
		if (telefono == null) { cancelado(); return; }

		String email = pedirValidoOpcional("Email", p.getEmail(), pacienteServicio::validarEmail);
		if (email == null) { cancelado(); return; }

		String direccion = pedirValidoOpcional("Dirección", p.getDireccion(), v -> pacienteServicio.validarTexto(v, "dirección"));
		if (direccion == null) { cancelado(); return; }

		String nif = pedirValidoOpcional("NIF/NIE", p.getNifNie(), v -> pacienteServicio.validarNifNie(v, p.getIdPaciente()));
		if (nif == null) { cancelado(); return; }

		String descripcion = pedir("Descripción del historial [" + p.getHistorial().getDescripcion() + "]:");
		if (descripcion.equalsIgnoreCase("cancelar")) { cancelado(); return; }
		if (descripcion.isEmpty()) {
			descripcion = p.getHistorial().getDescripcion();
		}

		String cambiarAlergias = pedirValido("¿Quieres cambiar sus alergias? (S/N)",
				v -> v.equalsIgnoreCase("S") || v.equalsIgnoreCase("N") ? null : "Responde S o N.");
		if (cambiarAlergias == null) { cancelado(); return; }
		if (cambiarAlergias.equalsIgnoreCase("S")) {
			List<Alergia> alergias = pedirAlergias(new ArrayList<>());
			if (alergias == null) { cancelado(); return; }
			p.getHistorial().setAlergias(alergias);
		}

		p.setNombre(nombre);
		p.setFechaNacimiento(LocalDate.parse(fecha, PacienteServicio.FORMATO_FECHA));
		p.setTelefono(telefono);
		p.setEmail(email);
		p.setDireccion(direccion);
		p.setNifNie(nif.toUpperCase());
		p.getHistorial().setDescripcion(descripcion);

		System.out.println("El paciente quedará así:");
		mostrarPaciente(p);
		if (!confirmar()) { cancelado(); return; }

		String resultado = pacienteServicio.modificarPaciente(p);
		if (resultado == null) {
			System.out.println(Color.VERDE + "Paciente modificado correctamente." + Color.RESET);
		} else {
			System.out.println(Color.ROJO + Color.NEGRITA + resultado + Color.RESET);
		}
	}

	static List<Alergia> pedirAlergias(List<Alergia> elegidas) {
		List<Alergia> disponibles = pacienteServicio.listarAlergias();
		if (disponibles.isEmpty()) {
			System.out.println("No hay alergias registradas en el sistema.");
			return elegidas;
		}
		String tiene = pedirValido("¿El paciente tiene alergias? (S/N)",
				v -> v.equalsIgnoreCase("S") || v.equalsIgnoreCase("N") ? null : "Responde S o N.");
		if (tiene == null) {
			return null;
		}
		String otra = tiene;
		while (otra.equalsIgnoreCase("S") && elegidas.size() < disponibles.size()) {
			System.out.println("Alergias disponibles:");
			for (Alergia a : disponibles) {
				if (!contiene(elegidas, a.getIdAlergia())) {
					System.out.println(a.getIdAlergia() + ". " + a.getNombre() + " (" + a.getMedicamento().getNombre() + ")");
				}
			}
			String elegida = pedirValido("Elige una alergia:", v -> {
				Alergia a = v.matches("\\d+") ? buscar(disponibles, Long.parseLong(v)) : null;
				if (a == null) {
					return "Esa alergia no existe.";
				}
				if (contiene(elegidas, a.getIdAlergia())) {
					return "Ya has elegido esa alergia.";
				}
				return null;
			});
			if (elegida == null) {
				return null;
			}
			elegidas.add(buscar(disponibles, Long.parseLong(elegida)));
			if (elegidas.size() < disponibles.size()) {
				otra = pedirValido("¿Quieres añadir otra alergia? (S/N)",
						v -> v.equalsIgnoreCase("S") || v.equalsIgnoreCase("N") ? null : "Responde S o N.");
				if (otra == null) {
					return null;
				}
			}
		}
		return elegidas;
	}

	static Alergia buscar(List<Alergia> lista, Long id) {
		for (Alergia a : lista) {
			if (a.getIdAlergia().equals(id)) {
				return a;
			}
		}
		return null;
	}

	static boolean contiene(List<Alergia> lista, Long id) {
		return buscar(lista, id) != null;
	}

	static void buscarPacientes() {
		System.out.println(Color.CIAN + Color.NEGRITA + "\n--- BUSCAR PACIENTES ---" + Color.RESET);
		System.out.println("Rellena los filtros que quieras. Deja en blanco los que no quieras usar.");
		String nombre = pedir("Nombre:");
		String telefono = pedir("Teléfono:");
		String email = pedir("Email:");
		String nif = pedir("NIF/NIE:").toUpperCase();

		List<Paciente> pacientes = pacienteServicio.buscar(nombre, telefono, email, nif);
		if (pacientes == null) {
			System.out.println(Color.ROJO + Color.NEGRITA + "Error al acceder a la base de datos." + Color.RESET);
			return;
		}
		if (pacientes.isEmpty()) {
			System.out.println("No se ha encontrado ningún paciente con esos datos.");
			return;
		}
		System.out.println(Color.VERDE + pacientes.size() + " paciente(s) encontrado(s):" + Color.RESET);
		for (Paciente p : pacientes) {
			mostrarPaciente(p);
		}
	}

	static void mostrarPaciente(Paciente p) {
		System.out.println("----------------------------------------");
		if (p.getIdPaciente() != null) {
			System.out.printf("%-20s %d%n", "Id:", p.getIdPaciente());
		}
		System.out.printf("%-20s %s%n", "Nombre:", p.getNombre());
		System.out.printf("%-20s %s%n", "Fecha nacimiento:", p.getFechaNacimiento().format(PacienteServicio.FORMATO_FECHA));
		System.out.printf("%-20s %s%n", "Teléfono:", p.getTelefono());
		System.out.printf("%-20s %s%n", "Email:", p.getEmail());
		System.out.printf("%-20s %s%n", "Dirección:", p.getDireccion());
		System.out.printf("%-20s %s%n", "NIF/NIE:", p.getNifNie());
		Historial h = p.getHistorial();
		if (h.getNumHistoria() != null) {
			System.out.printf("%-20s %d%n", "Nº de historia:", h.getNumHistoria());
		}
		System.out.printf("%-20s %s%n", "Descripción:", h.getDescripcion().isEmpty() ? "(sin descripción)" : h.getDescripcion());
		if (h.getAlergias().isEmpty()) {
			System.out.printf("%-20s %s%n", "Alergias:", "ninguna");
		} else {
			System.out.println("Alergias:");
			for (Alergia a : h.getAlergias()) {
				System.out.println("  - " + a.getNombre() + " (" + a.getMedicamento().getNombre() + ", máx. "
						+ a.getMedicamento().getDosisMaxDiariaMg() + " mg/día)");
			}
		}
	}
}