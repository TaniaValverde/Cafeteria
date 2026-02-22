package Controlador;

import Model.Producto;
import Persistencia.ProductoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller responsible for managing products in the system.
 *
 * <p>
 * This class implements the product-related business logic following
 * the MVC pattern. It allows registering, modifying, deleting,
 * searching, listing and sorting products.
 * </p>
 *
 * <p>
 * Functional Requirements:
 * <ul>
 *   <li>RF-01: Register, modify, delete and search products.</li>
 *   <li>RF-08: Persist products in text/binary files
 *       (implemented via {@link ProductoDAO}).</li>
 * </ul>
 * </p>
 *
 * This controller acts as an intermediary between the model layer
 * ({@link Producto}) and the persistence layer ({@link ProductoDAO}).
 *
 * @author Project Team
 */
public class ProductoController {

    /**
     * Data access object used for product persistence.
     */
    private final ProductoDAO productoDAO;

    /**
     * In-memory list of products currently loaded in the system.
     */
    private final List<Producto> productos;

    /**
     * Creates a new {@code ProductoController} and loads products
     * from persistent storage.
     *
     * @param rutaArchivo Path to the products file
     *                    (e.g. {@code "data/productos.txt"})
     * @throws IOException If loading products from storage fails
     */
    public ProductoController(String rutaArchivo) throws IOException {
        this.productoDAO = new ProductoDAO(rutaArchivo);
        this.productos = new ArrayList<>(productoDAO.cargar());
    }

    /**
     * Returns a copy of the current product list.
     *
     * @return List containing all registered products
     */
    public List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    /**
     * Searches for a product by its unique code.
     *
     * @param codigo Product code to search for
     * @return The matching {@link Producto}
     * @throws IllegalArgumentException If the code is invalid
     *                                  or the product does not exist
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
     * Adds a new product to the system.
     * <p>
     * The product code must be unique.
     * </p>
     *
     * @param producto Product to be added
     * @throws IOException If persistence fails
     * @throws IllegalArgumentException If the product is {@code null}
     *                                  or the code already exists
     */
    public void agregar(Producto producto) throws IOException {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null.");
        }

        // Unique code validation
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(producto.getCodigo())) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con ese código: " + producto.getCodigo()
                );
            }
        }

        productos.add(producto);
        guardarCambios();
    }

    /**
     * Modifies an existing product identified by its code.
     *
     * @param codigo Product code
     * @param nuevaCategoria New category
     * @param nuevoPrecio New price
     * @param nuevoStock New stock value
     * @throws IOException If persistence fails
     * @throws IllegalArgumentException If the product does not exist
     */
    public void modificar(String codigo, String nuevaCategoria,
                          double nuevoPrecio, int nuevoStock) throws IOException {

        Producto p = buscarPorCodigo(codigo);

        p.setCategoria(nuevaCategoria);
        p.setPrecio(nuevoPrecio);
        p.setStock(nuevoStock);

        guardarCambios();
    }

    /**
     * Updates only the stock of an existing product.
     *
     * @param codigo Product code
     * @param nuevoStock New stock value
     * @throws IOException If persistence fails
     * @throws IllegalArgumentException If the stock is negative
     *                                  or the product does not exist
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
     * Deletes a product identified by its code.
     *
     * @param codigo Product code
     * @throws IOException If persistence fails
     * @throws IllegalArgumentException If the product does not exist
     */
    public void eliminar(String codigo) throws IOException {
        Producto p = buscarPorCodigo(codigo);
        productos.remove(p);
        guardarCambios();
    }

    /**
     * Sorts the internal product list by product code (ascending).
     */
    public void ordenarPorCodigo() {
        productos.sort(
                Comparator.comparing(Producto::getCodigo, String.CASE_INSENSITIVE_ORDER)
        );
    }

    /**
     * Sorts the internal product list by price (ascending).
     */
    public void ordenarPorPrecio() {
        productos.sort(Comparator.comparingDouble(Producto::getPrecio));
    }

    /**
     * Saves the current state of the product list to persistent storage.
     *
     * @throws IOException If persistence fails
     */
    public void guardarCambios() throws IOException {
        productoDAO.guardar(productos);
    }
}