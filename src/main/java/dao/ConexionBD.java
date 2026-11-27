package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

	private static Connection conexion;
	private static String ultimoError;

	private ConexionBD() {
	}

	public static Connection getConexion() {
		try {
			if (conexion == null || conexion.isClosed()) {
				Properties props = new Properties();
				try (InputStream is = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
					if (is == null) {
						ultimoError = "no se encuentra el fichero db.properties";
						return null;
					}
					props.load(is);
				}
				conexion = DriverManager.getConnection(props.getProperty("url"), props.getProperty("usuario"),
						props.getProperty("password"));
			}
		} catch (IOException | SQLException e) {
			ultimoError = e.getMessage();
			conexion = null;
		}
		return conexion;
	}

	public static String getUltimoError() {
		return ultimoError;
	}

	public static void cerrarConexion() {
		try {
			if (conexion != null && !conexion.isClosed()) {
				conexion.close();
			}
		} catch (SQLException e) {
			conexion = null;
		}
	}

	static Connection obtener() {
		Connection con = getConexion();
		if (con == null) {
			throw new AccesoDatosException("No hay conexión con la base de datos");
		}
		return con;
	}
}