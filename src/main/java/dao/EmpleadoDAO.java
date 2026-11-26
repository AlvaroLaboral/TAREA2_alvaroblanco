package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modelo.Perfil;
import modelo.Sesion;

public class EmpleadoDAO {

	public Sesion buscarPorCredenciales(String usuario, String password) {
		String sql = "SELECT usuario, nombreYapellidos, perfil FROM empleado WHERE usuario = ? AND password = ?";
		Connection con = ConexionBD.getConexion();
		if (con == null) {
			return null;
		}
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, usuario);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Sesion(rs.getString("usuario"), rs.getString("nombreYapellidos"),
							Perfil.valueOf(rs.getString("perfil")));
				}
			}
		} catch (SQLException e) {
			System.out.println("Error al buscar el empleado: " + e.getMessage());
		}
		return null;
	}
}