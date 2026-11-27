package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelo.Alergia;
import modelo.Historial;
import modelo.Paciente;

public class PacienteDAO {

	public boolean existeNifNie(String nifNie, Long idExcluido) {
		String sql = "SELECT 1 FROM paciente WHERE nifNie = ? AND idPaciente <> ?";
		try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
			ps.setString(1, nifNie);
			ps.setLong(2, idExcluido == null ? 0 : idExcluido);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al comprobar el NIF/NIE", e);
		}
	}

	public void insertar(Paciente p) {
		Connection con = ConexionBD.obtener();
		try {
			con.setAutoCommit(false);
			long numHistoria = siguienteNumHistoria(con);
			String sql = "INSERT INTO paciente (nombre, fechaNacimiento, telefono, email, direccion, nifNie, numHistoria, descripcion) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				rellenar(ps, p);
				ps.setLong(7, numHistoria);
				ps.setString(8, p.getHistorial().getDescripcion());
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					rs.next();
					p.setIdPaciente(rs.getLong(1));
				}
			}
			p.getHistorial().setNumHistoria(numHistoria);
			guardarAlergias(con, p.getHistorial());
			con.commit();
		} catch (SQLException e) {
			deshacer(con);
			throw new AccesoDatosException("Error al dar de alta el paciente", e);
		} finally {
			restaurar(con);
		}
	}

	public void actualizar(Paciente p) {
		Connection con = ConexionBD.obtener();
		try {
			con.setAutoCommit(false);
			String sql = "UPDATE paciente SET nombre = ?, fechaNacimiento = ?, telefono = ?, email = ?, direccion = ?, "
					+ "nifNie = ?, descripcion = ? WHERE idPaciente = ?";
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				rellenar(ps, p);
				ps.setString(7, p.getHistorial().getDescripcion());
				ps.setLong(8, p.getIdPaciente());
				ps.executeUpdate();
			}
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM historial_alergia WHERE numHistoria = ?")) {
				ps.setLong(1, p.getHistorial().getNumHistoria());
				ps.executeUpdate();
			}
			guardarAlergias(con, p.getHistorial());
			con.commit();
		} catch (SQLException e) {
			deshacer(con);
			throw new AccesoDatosException("Error al modificar el paciente", e);
		} finally {
			restaurar(con);
		}
	}

	public Paciente buscarPorId(Long id) {
		List<Paciente> lista = consultar("SELECT * FROM paciente WHERE idPaciente = ?", List.of(id));
		return lista.isEmpty() ? null : lista.get(0);
	}

	public List<Paciente> buscar(String nombre, String telefono, String email, String nifNie) {
		StringBuilder sql = new StringBuilder("SELECT * FROM paciente WHERE 1 = 1");
		List<Object> valores = new ArrayList<>();
		if (!nombre.isBlank()) {
			sql.append(" AND nombre LIKE ?");
			valores.add("%" + nombre + "%");
		}
		if (!telefono.isBlank()) {
			sql.append(" AND telefono LIKE ?");
			valores.add("%" + telefono + "%");
		}
		if (!email.isBlank()) {
			sql.append(" AND email LIKE ?");
			valores.add("%" + email + "%");
		}
		if (!nifNie.isBlank()) {
			sql.append(" AND nifNie LIKE ?");
			valores.add("%" + nifNie + "%");
		}
		sql.append(" ORDER BY nombre");
		return consultar(sql.toString(), valores);
	}

	public List<Long> idsAlergias(Long numHistoria) {
		List<Long> ids = new ArrayList<>();
		String sql = "SELECT idAlergia FROM historial_alergia WHERE numHistoria = ? ORDER BY idAlergia";
		try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
			ps.setLong(1, numHistoria);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ids.add(rs.getLong("idAlergia"));
				}
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al leer las alergias del paciente", e);
		}
		return ids;
	}

	private List<Paciente> consultar(String sql, List<Object> valores) {
		List<Paciente> pacientes = new ArrayList<>();
		try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
			for (int i = 0; i < valores.size(); i++) {
				ps.setObject(i + 1, valores.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					pacientes.add(crearPaciente(rs));
				}
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al buscar pacientes", e);
		}
		return pacientes;
	}

	private Paciente crearPaciente(ResultSet rs) throws SQLException {
		String descripcion = rs.getString("descripcion");
		Historial h = new Historial(descripcion == null ? "" : descripcion);
		h.setNumHistoria(rs.getLong("numHistoria"));
		Date fecha = rs.getDate("fechaNacimiento");
		Paciente p = new Paciente(rs.getString("nombre"), fecha == null ? null : fecha.toLocalDate(),
				rs.getString("telefono"), rs.getString("email"), rs.getString("direccion"), rs.getString("nifNie"),
				null, null, h);
		p.setIdPaciente(rs.getLong("idPaciente"));
		return p;
	}

	private void rellenar(PreparedStatement ps, Paciente p) throws SQLException {
		ps.setString(1, p.getNombre());
		ps.setDate(2, Date.valueOf(p.getFechaNacimiento()));
		ps.setString(3, p.getTelefono());
		ps.setString(4, p.getEmail());
		ps.setString(5, p.getDireccion());
		ps.setString(6, p.getNifNie());
	}

	private long siguienteNumHistoria(Connection con) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement("SELECT COALESCE(MAX(numHistoria), 0) + 1 FROM paciente");
				ResultSet rs = ps.executeQuery()) {
			rs.next();
			return rs.getLong(1);
		}
	}

	private void guardarAlergias(Connection con, Historial h) throws SQLException {
		String sql = "INSERT INTO historial_alergia (numHistoria, idAlergia) VALUES (?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (Alergia a : h.getAlergias()) {
				ps.setLong(1, h.getNumHistoria());
				ps.setLong(2, a.getIdAlergia());
				ps.executeUpdate();
			}
		}
	}

	private void deshacer(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e) {
		}
	}

	private void restaurar(Connection con) {
		try {
			con.setAutoCommit(true);
		} catch (SQLException e) {
		}
	}
}