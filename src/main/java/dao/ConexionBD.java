package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

	private static Connection conexion;

	private ConexionBD() {
	}

	public static Connection getConexion() {
		try {
			if (conexion == null || conexion.isClosed()) {
				Properties props = new Properties();
				try (InputStream is = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
					if (is == null) {
						System.out.println("No se encuentra el fichero db.properties");
						return null;
					}
					props.load(is);
				}
				String url = props.getProperty("url");
				String usuario = props.getProperty("usuario");
				String password = props.getProperty("password");
				conexion = DriverManager.getConnection(url, usuario, password);
			}
		} catch (IOException e) {
			System.out.println("Error al leer db.properties: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("Error al conectar con la BD: " + e.getMessage());
		}
		return conexion;
	}

	public static void cerrarConexion() {
		try {
			if (conexion != null && !conexion.isClosed()) {
				conexion.close();
			}
		} catch (SQLException e) {
			System.out.println("Error al cerrar la conexión: " + e.getMessage());
		}
	}
}