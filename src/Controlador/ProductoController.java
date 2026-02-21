package Controlador;

import Model.Producto;
import Persistencia.ProductoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for product management (MVC).
 *
 * <p>RF-01: Register, modify, delete and search products.</p>
 * <p>RF-08: Persist products in text/binary files (implemented via ProductoDAO).</p>
 */
public class ProductoController {

    private final ProductoDAO productoDAO;
    private final List<Producto> productos;

    /**
     * Creates the controller and loads products from storage.
     *
     * @param rutaArchivo path for products file (e.g. "data/productos.txt")
     * @throws IOException if loading fails
     */
    public ProductoController(String rutaArchivo) throws IOException {
        this.productoDAO = new ProductoDAO(rutaArchivo);
        this.productos = new ArrayList<>(productoDAO.cargar());
    }

    /** @return a copy of the product list */
    public List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    /**
     * Searches a product by its code.
     *
     * @param codigo product code
     * @return found product
     * @throws IllegalArgumentException if not found or invalid code
     */
    public Producto buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        String c = codigo.trim();

        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(c)) {
                return p;
            }
        }
        throw new IllegalArgumentException("No existe un producto con código: " + codigo);
    }

    /**
     * Adds a new product (code must be unique).
     *
     * @param producto product to add
     * @throws IOException if persistence fails
     */
    public void agregar(Producto producto) throws IOException {
        if (producto == null) throw new IllegalArgumentException("Producto no puede ser null.");

        // Unique code validation
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(producto.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un producto con ese código: " + producto.getCodigo());
            }
        }

        productos.add(producto);
        guardarCambios();
    }

    /**
     * Updates an existing product (by code).
     *
     * @param codigo product code to update
     * @param nuevaCategoria new category
     * @param nuevoPrecio new price
     * @param nuevoStock new stock
     * @throws IOException if persistence fails
     */
    public void modificar(String codigo, String nuevaCategoria, double nuevoPrecio, int nuevoStock) throws IOException {
        Producto p = buscarPorCodigo(codigo);

        p.setCategoria(nuevaCategoria);
        p.setPrecio(nuevoPrecio);
        p.setStock(nuevoStock);

        guardarCambios();
    }

    /**
     * Updates ONLY the stock of an existing product (by code).
     *
     * @param codigo product code
     * @param nuevoStock new stock
     * @throws IOException if persistence fails
     */
    public void actualizarStock(String codigo, int nuevoStock) throws IOException {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        Producto p = buscarPorCodigo(codigo);
        p.setStock(nuevoStock);

        guardarCambios();
    }

    /**
     * Deletes a product by its code.
     *
     * @param codigo product code
     * @throws IOException if persistence fails
     */
    public void eliminar(String codigo) throws IOException {
        Producto p = buscarPorCodigo(codigo);
        productos.remove(p);
        guardarCambios();
    }

    /**
     * Sorts the internal list by code (ascending).
     */
    public void ordenarPorCodigo() {
        productos.sort(Comparator.comparing(Producto::getCodigo, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sorts the internal list by price (ascending).
     */
    public void ordenarPorPrecio() {
        productos.sort(Comparator.comparingDouble(Producto::getPrecio));
    }

    /**
     * Saves changes to persistent storage.
     *
     * @throws IOException if persistence fails
     */
    public void guardarCambios() throws IOException {
        productoDAO.guardar(productos);
    }
}