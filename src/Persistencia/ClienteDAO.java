package Persistencia;

import Model.Cliente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Cliente persistence using a CSV-like text file.
 *
 * <p>Requirement RF-08: store clients in local text/binary files.</p>
 */
public class ClienteDAO {

    private final Path archivo;

    /**
     * Creates a DAO with a given file path.
     *
     * @param rutaArchivo file path (e.g. "data/clientes.txt")
     */
    public ClienteDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    /**
     * Loads all clients from the file.
     *
     * @return list of clients (never null)
     * @throws IOException if file operations fail
     */
    public List<Cliente> cargar() throws IOException {
        asegurarDirectorio();

        List<Cliente> clientes = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return clientes;
        }

        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: id,nombre,telefono,tipo
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;

                String id = p[0].trim();
                String nombre = p[1].trim();
                String telefono = p[2].trim();
                Cliente.TipoCliente tipo = Cliente.TipoCliente.valueOf(p[3].trim());

                clientes.add(new Cliente(id, nombre, telefono, tipo));
            }
        }

        return clientes;
    }

    /**
     * Saves the given client list to the file (overwrites).
     *
     * @param clientes list to save
     * @throws IOException if file operations fail
     */
    public void guardar(List<Cliente> clientes) throws IOException {
        asegurarDirectorio();

        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Cliente c : clientes) {
                String line = String.join(",",
                        c.getId(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getTipo().name()
                );
                bw.write(line);
                bw.newLine();
            }
        }
    }

    private void asegurarDirectorio() throws IOException {
        Path parent = archivo.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
