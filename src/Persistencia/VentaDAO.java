package Persistencia;

import Model.Producto;
import Model.Venta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for {@link Venta} persistence using a local text file.
 *
 * This class supports the project persistence requirement by loading and storing sales
 * in CSV format, and provides utility operations such as appending new sales, updating
 * existing records, listing pending sales, and registering a sale while updating stock.
 */
public class VentaDAO {

    private static final String RUTA_VENTAS_POR_DEFECTO = "data/ventas.txt";
    private final Path archivo;

    /** Creates a DAO using the default sales file path. */
    public VentaDAO() {
        this(RUTA_VENTAS_POR_DEFECTO);
    }

    /**
     * Creates a DAO pointing to the specified storage file.
     *
     * @param rutaArchivo file path (e.g., "data/ventas.txt")
     */
    public VentaDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    /**
     * Loads all sales from the storage file.
     * Invalid lines are ignored to keep the load process robust.
     *
     * @return list of sales (never null)
     * @throws IOException if a file access error occurs
     */
    public List<Venta> cargar() throws IOException {
        asegurarDirectorio();

        List<Venta> ventas = new ArrayList<>();
        if (!Files.exists(archivo)) return ventas;

        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    ventas.add(Venta.fromCsv(line));
                } catch (Exception ex) {
                    System.out.println("Linea de venta invalida (se ignora): " + line);
                }
            }
        }
        return ventas;
    }

    /**
     * Saves all sales to the storage file (overwrites existing content).
     *
     * @param ventas sales to persist
     * @throws IOException if a file access error occurs
     */
    public void guardar(List<Venta> ventas) throws IOException {
        asegurarDirectorio();
        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Venta v : ventas) {
                bw.write(v.toCsv());
                bw.newLine();
            }
        }
    }

    /**
     * Appends a single sale to the storage file.
     *
     * @param venta sale to persist
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if {@code venta} is null
     */
    public void guardarVenta(Venta venta) throws IOException {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser null.");
        asegurarDirectorio();

        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            bw.write(venta.toCsv());
            bw.newLine();
        }
    }

    /**
     * Updates an existing sale by matching its id.
     *
     * @param ventaActualizada updated sale instance
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if {@code ventaActualizada} is null
     * @throws IllegalStateException if the sale id is not found
     */
    public void actualizarVenta(Venta ventaActualizada) throws IOException {
        if (ventaActualizada == null) throw new IllegalArgumentException("Venta no puede ser null.");

        List<Venta> ventas = cargar();
        boolean encontrada = false;

        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId().equals(ventaActualizada.getId())) {
                ventas.set(i, ventaActualizada);
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            throw new IllegalStateException("No se encontró la venta con ID: " + ventaActualizada.getId());
        }

        guardar(ventas);
    }

    /**
     * Removes pending sales associated with a specific order code.
     *
     * @param codigoPedido order code to match
     * @throws IOException if a file access error occurs
     */
    public void eliminarPendientePorCodigoPedido(int codigoPedido) throws IOException {
        List<Venta> ventas = cargar();
        boolean cambio = false;

        for (int i = ventas.size() - 1; i >= 0; i--) {
            Venta v = ventas.get(i);
            if ("PENDIENTE".equalsIgnoreCase(v.getEstado())
                    && v.getCodigoPedido() == codigoPedido) {
                ventas.remove(i);
                cambio = true;
            }
        }

        if (cambio) guardar(ventas);
    }

    /**
     * Returns all sales currently marked as pending.
     *
     * @return list of pending sales
     * @throws IOException if a file access error occurs
     */
    public List<Venta> listarPendientes() throws IOException {
        List<Venta> ventas = cargar();
        List<Venta> pendientes = new ArrayList<>();

        for (Venta v : ventas) {
            if ("PENDIENTE".equalsIgnoreCase(v.getEstado())) {
                pendientes.add(v);
            }
        }
        return pendientes;
    }

    /**
     * Registers a sale and updates product stock accordingly.
     * Stock is decreased based on each sale line item, then both products and the sale are persisted.
     *
     * @param venta sale to register
     * @param productoDAO DAO used to load and persist products
     * @throws IOException if a file access error occurs
     * @throws IllegalArgumentException if arguments are null
     * @throws IllegalStateException if a referenced product does not exist
     */
    public void registrarVentaConStock(Venta venta, ProductoDAO productoDAO) throws IOException {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser null.");
        if (productoDAO == null) throw new IllegalArgumentException("ProductoDAO no puede ser null.");

        List<Producto> productos = productoDAO.cargar();

        for (Venta.LineaVenta lv : venta.getLineas()) {
            String codigo = lv.getProducto().getCodigo();
            int cantidad = lv.getCantidad();

            Producto p = buscarProductoPorCodigo(productos, codigo);
            if (p == null) throw new IllegalStateException("No existe el producto con codigo: " + codigo);

            p.descontarStock(cantidad);
        }

        productoDAO.guardar(productos);
        guardarVenta(venta);
    }

    /**
     * Convenience method that returns all sales as an {@link ArrayList}.
     *
     * @return list of sales
     * @throws IOException if a file access error occurs
     */
    public ArrayList<Venta> getVentas() throws IOException {
        return new ArrayList<>(cargar());
    }

    private Producto buscarProductoPorCodigo(List<Producto> productos, String codigo) {
        if (codigo == null) return null;
        for (Producto p : productos) {
            if (codigo.equals(p.getCodigo())) return p;
        }
        return null;
    }

    /** Ensures the parent directory of the storage file exists. */
    private void asegurarDirectorio() throws IOException {
        Path parent = archivo.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}