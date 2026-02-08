package Controlador;

import Model.Cliente;
import Persistencia.ClienteDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for customer management (MVC).
 *
 * <p>RF-02: Register customers as frequent or visitors.</p>
 * <p>RF-08: Persist clients in text/binary files (implemented via ClienteDAO).</p>
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;
    private final List<Cliente> clientes;

    /**
     * Creates the controller and loads clients from storage.
     *
     * @param rutaArchivo path for clients file (e.g. "data/clientes.txt")
     * @throws IOException if loading fails
     */
    public ClienteController(String rutaArchivo) throws IOException {
        this.clienteDAO = new ClienteDAO(rutaArchivo);
        this.clientes = new ArrayList<>(clienteDAO.cargar());
    }

    /** @return a copy of the client list */
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    /**
     * Searches a customer by id.
     *
     * @param id customer id
     * @return found customer
     * @throws IllegalArgumentException if not found or invalid id
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
     * @param cliente customer to add
     * @throws IOException if persistence fails
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
     * Updates an existing customer (by id).
     *
     * @param id customer id
     * @param nuevoNombre new name
     * @param nuevoTelefono new phone
     * @param nuevoTipo new type
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

    /**
     * Sorts customers by name (ascending).
     */
    public void ordenarPorNombre() {
        clientes.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Saves changes to persistent storage.
     *
     * @throws IOException if persistence fails
     */
    public void guardarCambios() throws IOException {
        clienteDAO.guardar(clientes);
    }
}
