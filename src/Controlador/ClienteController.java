package Controlador;

import Model.Cliente;
import Persistencia.ClienteDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for customer management in the MVC architecture.
 *
 * Manages CRUD operations for {@link Cliente} and persists data using {@link ClienteDAO}.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;
    private final List<Cliente> clientes;

    /**
     * Creates the controller and loads customers from persistent storage.
     *
     * @param rutaArchivo path to the customer file (e.g., "data/clientes.txt")
     * @throws IOException if the file cannot be read
     */
    public ClienteController(String rutaArchivo) throws IOException {
        this.clienteDAO = new ClienteDAO(rutaArchivo);
        this.clientes = new ArrayList<>(clienteDAO.cargar());
    }

    /**
     * Returns a copy of the current customer list.
     *
     * @return list of customers
     */
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    /**
     * Finds a customer by id.
     *
     * @param id customer id (non-blank)
     * @return matching customer
     * @throws IllegalArgumentException if the id is invalid or the customer is not found
     */
    public Cliente buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id no puede estar vacío.");
        }
        String key = id.trim();

        for (Cliente c : clientes) {
            if (c.getId().equalsIgnoreCase(key)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No existe un cliente con id: " + id);
    }

    /**
     * Registers a new customer (id must be unique).
     *
     * @param cliente customer to register
     * @throws IOException if persistence fails
     * @throws IllegalArgumentException if {@code cliente} is null or the id already exists
     */
    public void registrar(Cliente cliente) throws IOException {
        if (cliente == null) throw new IllegalArgumentException("Cliente no puede ser null.");

        for (Cliente c : clientes) {
            if (c.getId().equalsIgnoreCase(cliente.getId())) {
                throw new IllegalArgumentException("Ya existe un cliente con ese id: " + cliente.getId());
            }
        }

        clientes.add(cliente);
        guardarCambios();
    }

    /**
     * Updates an existing customer identified by id.
     *
     * @param id customer id
     * @param nuevoNombre new name
     * @param nuevoTelefono new phone number
     * @param nuevoTipo new customer type
     * @throws IOException if persistence fails
     */
    public void modificar(String id, String nuevoNombre, String nuevoTelefono, Cliente.TipoCliente nuevoTipo) throws IOException {
        Cliente c = buscarPorId(id);

        c.setNombre(nuevoNombre);
        c.setTelefono(nuevoTelefono);
        c.setTipo(nuevoTipo);

        guardarCambios();
    }

    /**
     * Deletes a customer by id.
     *
     * @param id customer id
     * @throws IOException if persistence fails
     */
    public void eliminar(String id) throws IOException {
        Cliente c = buscarPorId(id);
        clientes.remove(c);
        guardarCambios();
    }

    /** Sorts customers by name in ascending order. */
    public void ordenarPorNombre() {
        clientes.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Persists the current customer list to storage.
     *
     * @throws IOException if persistence fails
     */
    public void guardarCambios() throws IOException {
        clienteDAO.guardar(clientes);
    }
}