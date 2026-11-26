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
}