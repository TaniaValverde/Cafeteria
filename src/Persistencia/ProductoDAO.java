package Persistencia;

import Model.Producto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for {@link Producto} persistence using a local text file.
 *
 * This class fulfills the project persistence requirement by storing and
 * retrieving product data in a CSV-like format.
 */
public class ProductoDAO {

    private final Path archivo;

    /**
     * Creates a DAO pointing to the specified storage file.
     *
     * @param rutaArchivo file path (e.g., "data/productos.txt")
     */
    public ProductoDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    /**
     * Loads all products from the file.
     *
     * @return list of products (never null)
     * @throws IOException if a file access error occurs
     */
    public List<Producto> cargar() throws IOException {
        asegurarDirectorio();

        List<Producto> productos = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return productos;
        }

        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // formato: codigo,nombre,categoria,precio,stock
                String[] p = line.split(",", -1);
                if (p.length < 5) {
                    continue;
                }

                String codigo = p[0].trim();
                String nombre = p[1].trim();
                String categoria = p[2].trim();
                double precio = Double.parseDouble(p[3].trim());
                int stock = Integer.parseInt(p[4].trim());

                productos.add(new Producto(codigo, nombre, categoria, precio, stock));
            }
        }

        return productos;
    }

    /**
     * Saves the given product list to the file (overwrites existing content).
     *
     * @param productos products to persist
     * @throws IOException if a file access error occurs
     */
    public void guardar(List<Producto> productos) throws IOException {
        asegurarDirectorio();

        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Producto pr : productos) {
                String line = String.join(",",
                        pr.getCodigo(),
                        pr.getNombre(),
                        pr.getCategoria(),
                        String.valueOf(pr.getPrecio()),
                        String.valueOf(pr.getStock())
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