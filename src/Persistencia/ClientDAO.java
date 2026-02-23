package Persistencia;

import Model.Client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for {@link Client} persistence using a local text file.
 *
 * This class supports the project persistence requirement by loading and saving
 * customer records in a simple CSV-like format.
 */
public class ClientDAO {

    private final Path archivo;

    /**
     * Creates a DAO pointing to the given storage file.
     *
     * @param rutaArchivo file path (e.g., "data/clientes.txt")
     */
    public ClientDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    /**
     * Loads all customers from the file.
     *
     * @return list of customers (never null)
     * @throws IOException if a file access error occurs
     */
    public List<Client> cargar() throws IOException {
        asegurarDirectorio();

        List<Client> clientes = new ArrayList<>();
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
                Client.TipoCliente tipo = Client.TipoCliente.valueOf(p[3].trim());

                clientes.add(new Client(id, nombre, telefono, tipo));
            }
        }

        return clientes;
    }

    /**
     * Saves the given customer list to the file (overwrites existing content).
     *
     * @param clientes customers to persist
     * @throws IOException if a file access error occurs
     */
    public void guardar(List<Client> clientes) throws IOException {
        asegurarDirectorio();

        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Client c : clientes) {
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

    /** Ensures the parent directory of the storage file exists. */
    private void asegurarDirectorio() throws IOException {
        Path parent = archivo.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}