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
 * DAO de Ventas:
 * - Guarda ventas en un archivo de texto (CSV en una sola línea).
 * - Lee ventas desde el archivo.
 * - Puede registrar una venta y actualizar el stock de productos.
 */
public class VentaDAO {

    private static final String RUTA_VENTAS_POR_DEFECTO = "data/ventas.txt";
    private final Path archivo;

    /** Constructor por defecto: usa data/ventas.txt */
    public VentaDAO() {
        this(RUTA_VENTAS_POR_DEFECTO);
    }

    /** Constructor con ruta personalizada */
    public VentaDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    /**
     * Carga todas las ventas desde el archivo.
     * Si el archivo no existe, devuelve lista vacía.
     */
    public List<Venta> cargar() throws IOException {
        asegurarDirectorio();

        List<Venta> ventas = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return ventas;
        }

        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Si una línea está dañada, la saltamos pero avisamos.
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
     * Guarda todas las ventas (sobrescribe el archivo completo).
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
     * Guarda una venta al final del archivo (APPEND).
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
     * Registra una venta y actualiza el stock.
     *
     * ¿Qué hace?
     * 1) Carga productos desde ProductoDAO
     * 2) Descuenta stock por cada línea de la venta
     * 3) Guarda productos actualizados
     * 4) Guarda la venta en el archivo de ventas
     *
     * Si falta un producto o no hay stock suficiente, lanza error y NO guarda la venta.
     */
    public void registrarVentaConStock(Venta venta, ProductoDAO productoDAO) throws IOException {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser null.");
        if (productoDAO == null) throw new IllegalArgumentException("ProductoDAO no puede ser null.");

        // 1) Cargar productos
        List<Producto> productos = productoDAO.cargar();

        // 2) Validar y descontar stock
        for (Venta.LineaVenta lv : venta.getLineas()) {
            String codigo = lv.getProducto().getCodigo();
            int cantidad = lv.getCantidad();

            Producto p = buscarProductoPorCodigo(productos, codigo);
            if (p == null) {
                throw new IllegalStateException("No existe el producto con codigo: " + codigo);
            }

            // Esto lanza error si no hay stock suficiente
            p.descontarStock(cantidad);
        }

        // 3) Guardar productos actualizados
        productoDAO.guardar(productos);

        // 4) Guardar la venta
        guardarVenta(venta);
    }

    /**
     * Método cómodo por si tu ReporteController usa ArrayList.
     */
    public ArrayList<Venta> getVentas() throws IOException {
        return new ArrayList<>(cargar());
    }

    // ----------------- Helpers -----------------

    private Producto buscarProductoPorCodigo(List<Producto> productos, String codigo) {
        if (codigo == null) return null;
        for (Producto p : productos) {
            if (codigo.equals(p.getCodigo())) {
                return p;
            }
        }
        return null;
    }

    private void asegurarDirectorio() throws IOException {
        Path parent = archivo.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}

