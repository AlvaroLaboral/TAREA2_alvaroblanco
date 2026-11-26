package dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import modelo.Alergia;

public class AlergiaDAO {

	private static final Path CARPETA_ALERGIAS = Path.of("alergias");

	public List<Alergia> listar() {
		List<Alergia> alergias = new ArrayList<>();
		if (!Files.isDirectory(CARPETA_ALERGIAS)) {
			return alergias;
		}
		try (DirectoryStream<Path> ficheros = Files.newDirectoryStream(CARPETA_ALERGIAS, "alergia_*.dat")) {
			for (Path fichero : ficheros) {
				Alergia a = leer(fichero);
				if (a != null) {
					alergias.add(a);
				}
			}
		} catch (IOException e) {
			throw new AccesoDatosException("Error al leer la carpeta de alergias", e);
		}
		alergias.sort(Comparator.comparing(Alergia::getIdAlergia));
		return alergias;
	}

	public Alergia buscar(Long id) {
		Path fichero = ruta(id);
		if (!Files.exists(fichero)) {
			return null;
		}
		return leer(fichero);
	}

	public void guardar(Alergia a) {
		try {
			Files.createDirectories(CARPETA_ALERGIAS);
			try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(ruta(a.getIdAlergia())))) {
				oos.writeObject(a);
			}
		} catch (IOException e) {
			throw new AccesoDatosException("Error al guardar la alergia", e);
		}
	}

	private Alergia leer(Path fichero) {
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(fichero))) {
			return (Alergia) ois.readObject();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			return null;
		}
	}

	private Path ruta(Long id) {
		return CARPETA_ALERGIAS.resolve("alergia_" + id + ".dat");
	}
}