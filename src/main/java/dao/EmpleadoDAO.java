package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Cirujano;
import modelo.Especialidad;
import modelo.Perfil;
import modelo.Sesion;
import java.sql.Statement;
import modelo.Empleado;
import modelo.Enfermero;
import modelo.Secretario;

public class EmpleadoDAO {

	public Sesion buscarPorCredenciales(String usuario, String password) {
		String sql = "SELECT usuario, nombreYapellidos, perfil FROM empleado WHERE usuario = ? AND password = ?";
		try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
			ps.setString(1, usuario);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Sesion(rs.getString("usuario"), rs.getString("nombreYapellidos"),
							Perfil.valueOf(rs.getString("perfil")));
				}
				return null;
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al buscar el empleado", e);
		}
	}

	public Cirujano buscarCirujanoPorUsuario(String usuario) {
		String sql = "SELECT e.idEmpleado, e.nombreYapellidos, e.telefono, e.email, e.nifNie, e.direccion, e.usuario "
				+ "FROM empleado e JOIN cirujano c ON e.idEmpleado = c.idEmpleado WHERE e.usuario = ?";
		Connection con = ConexionBD.obtener();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, usuario);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				Cirujano c = new Cirujano(rs.getString("nombreYapellidos"), rs.getString("telefono"),
						rs.getString("email"), rs.getString("nifNie"), rs.getString("direccion"),
						rs.getString("usuario"), null, new ArrayList<>());
				c.setIdEmpleado(rs.getLong("idEmpleado"));
				c.setEspecialidades(buscarEspecialidades(con, c.getIdEmpleado()));
				return c;
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al buscar el cirujano", e);
		}
	}

	private List<Especialidad> buscarEspecialidades(Connection con, Long idEmpleado) throws SQLException {
		List<Especialidad> lista = new ArrayList<>();
		String sql = "SELECT idEspecialidad FROM cirujano_especialidad WHERE idEmpleado = ? ORDER BY idEspecialidad";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, idEmpleado);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Especialidad e = Especialidad.porId(rs.getInt("idEspecialidad"));
					if (e != null) {
						lista.add(e);
					}
				}
			}
		}
		return lista;
	}
	
	public boolean existeUsuario(String usuario) {
		return existe("SELECT 1 FROM empleado WHERE usuario = ?", usuario);
	}

	public boolean existeNifNie(String nifNie) {
		return existe("SELECT 1 FROM empleado WHERE nifNie = ?", nifNie);
	}

	private boolean existe(String sql, String valor) {
		try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
			ps.setString(1, valor);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new AccesoDatosException("Error al comprobar el empleado", e);
		}
	}

	public void insertar(Empleado e) {
		Connection con = ConexionBD.obtener();
		try {
			con.setAutoCommit(false);
			long id = insertarEmpleado(con, e);
			e.setIdEmpleado(id);
			if (e instanceof Secretario s) {
				ejecutar(con, "INSERT INTO secretario (idEmpleado, aniosExperiencia) VALUES (?, ?)", id,
						s.getAniosExperiencia());
			} else if (e instanceof Enfermero en) {
				ejecutar(con, "INSERT INTO enfermero (idEmpleado, categoria) VALUES (?, ?)", id,
						en.getCategoria().name());
			} else if (e instanceof Cirujano c) {
				ejecutar(con, "INSERT INTO cirujano (idEmpleado) VALUES (?)", id);
				for (Especialidad esp : c.getEspecialidades()) {
					ejecutar(con, "INSERT INTO cirujano_especialidad (idEmpleado, idEspecialidad) VALUES (?, ?)", id,
							esp.getId());
				}
			}
			con.commit();
		} catch (SQLException | RuntimeException ex) {
			try {
				con.rollback();
			} catch (SQLException ignorada) {
			}
			throw new AccesoDatosException("Error al dar de alta el empleado", ex);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException ignorada) {
			}
		}
	}

	private long insertarEmpleado(Connection con, Empleado e) throws SQLException {
		String sql = "INSERT INTO empleado (nombreYapellidos, telefono, email, nifNie, direccion, usuario, password, perfil) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, e.getNombreYapellidos());
			ps.setString(2, e.getTelefono());
			ps.setString(3, e.getEmail());
			ps.setString(4, e.getNifNie());
			ps.setString(5, e.getDireccion());
			ps.setString(6, e.getUsuario());
			ps.setString(7, e.getPassword());
			ps.setString(8, perfilDe(e).name());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				rs.next();
				return rs.getLong(1);
			}
		}
	}

	private void ejecutar(Connection con, String sql, Object... valores) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			for (int i = 0; i < valores.length; i++) {
				ps.setObject(i + 1, valores[i]);
			}
			ps.executeUpdate();
		}
	}

	private Perfil perfilDe(Empleado e) {
		if (e instanceof Secretario) {
			return Perfil.SECRETARIADO;
		} else if (e instanceof Enfermero) {
			return Perfil.ENFERMERIA;
		}
		return Perfil.CIRUGIA;
	}
}