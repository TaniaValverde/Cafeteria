package Controlador;

import Model.Producto;
import Persistencia.ProductoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for product management in the MVC architecture.
 *
 * Manages CRUD operations for {@link Producto} and persists data using {@link ProductoDAO}.
 */
public class ProductoController {

    private final ProductoDAO productoDAO;
    private final List<Producto> productos;

    /**
     * Creates the controller and loads products from persistent storage.
     *
     * @param rutaArchivo path to the products file (e.g., "data/productos.txt")
     * @throws IOException if loading fails
     */
    public ProductoController(String rutaArchivo) throws IOException {
        this.productoDAO = new ProductoDAO(rutaArchivo);
        this.productos = new ArrayList<>(productoDAO.cargar());
    }

    /**
     * Returns a copy of the current product list.
     *
     * @return list of products
     */
    public List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    /**
     * Finds a product by its code.
     *
     * @param codigo product code (non-blank)
     * @return matching product
     * @throws IllegalArgumentException if the code is invalid or not found
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
     * Registers a new product (code must be unique).
     *
     * @param producto product to add
     * @throws IOException if persistence fails
     * @throws IllegalArgumentException if product is null or code already exists
     */
    public void agregar(Producto producto) throws IOException {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null.");
        }

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
     * Updates category, price, and stock of an existing product.
     *
     * @param codigo product code
     * @param nuevaCategoria new category
     * @param nuevoPrecio new price
     * @param nuevoStock new stock
     * @throws IOException if persistence fails
     * @throws IllegalArgumentException if the product does not exist
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
     * @param codigo product code
     * @param nuevoStock new stock value (must be >= 0)
     * @throws IOException if persistence fails
     * @throws IllegalArgumentException if stock is negative or product does not exist
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
     * @throws IllegalArgumentException if the product does not exist
     */
    public void eliminar(String codigo) throws IOException {
        Producto p = buscarPorCodigo(codigo);
        productos.remove(p);
        guardarCambios();
    }

    /** Sorts products by code (ascending). */
    public void ordenarPorCodigo() {
        productos.sort(
                Comparator.comparing(Producto::getCodigo, String.CASE_INSENSITIVE_ORDER)
        );
    }

    /** Sorts products by price (ascending). */
    public void ordenarPorPrecio() {
        productos.sort(Comparator.comparingDouble(Producto::getPrecio));
    }

    /**
     * Persists the current product list to storage.
     *
     * @throws IOException if persistence fails
     */
    public void guardarCambios() throws IOException {
        productoDAO.guardar(productos);
    }
}