package testControlador;

import Controlador.ClientController;
import Model.Client;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class ClientControllerTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private String clientesPath() throws Exception {
        File f = tmp.newFile("clientes.txt");
        return f.getAbsolutePath();
    }

    @Test
    public void iniciarVacio_siArchivoNuevo() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        assertNotNull(cc.listar());
        assertEquals(0, cc.listar().size());
    }

    @Test
    public void registrarYBuscarPorId_funciona() throws Exception {
        ClientController cc = new ClientController(clientesPath());

        Client c = new Client("C001", "Ana", "8888-8888", Client.TipoCliente.FRECUENTE);
        cc.registrar(c);

        Client encontrado = cc.buscarPorId("C001");
        assertNotNull(encontrado);
        assertEquals("C001", encontrado.getId());
        assertEquals("Ana", encontrado.getNombre());
        assertEquals("8888-8888", encontrado.getTelefono());
        assertEquals(Client.TipoCliente.FRECUENTE, encontrado.getTipo());
    }

    @Test
    public void noPermiteIdDuplicado() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        cc.registrar(new Client("C001", "Ana", "8888-8888", Client.TipoCliente.FRECUENTE));

        try {
            cc.registrar(new Client("C001", "Tania", "9999-9999", Client.TipoCliente.VISITANTE));
            fail("Debe lanzar IllegalArgumentException por id duplicado");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        }
    }

    @Test
    public void modificar_actualizaCampos() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        cc.registrar(new Client("C001", "Ana", "8888-8888", Client.TipoCliente.FRECUENTE));

        cc.modificar("C001", "Ana Maria", "7777-7777", Client.TipoCliente.VISITANTE);

        Client mod = cc.buscarPorId("C001");
        assertEquals("Ana Maria", mod.getNombre());
        assertEquals("7777-7777", mod.getTelefono());
        assertEquals(Client.TipoCliente.VISITANTE, mod.getTipo());
    }

    @Test
    public void eliminar_quitaCliente() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        cc.registrar(new Client("C001", "Ana", "8888-8888", Client.TipoCliente.FRECUENTE));
        cc.registrar(new Client("C002", "Hugo", "", Client.TipoCliente.VISITANTE));

        cc.eliminar("C001");

        List<Client> lista = cc.listar();
        assertEquals(1, lista.size());
        assertEquals("C002", lista.get(0).getId());

        try {
            cc.buscarPorId("C001");
            fail("Debe lanzar IllegalArgumentException al buscar cliente eliminado");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
        }
    }

    @Test
    public void buscarInexistente_lanzaExcepcion() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        try {
            cc.buscarPorId("NOEXISTE");
            fail("Debe lanzar IllegalArgumentException si no existe");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
        }
    }

    @Test
    public void validarIdVacio_enBuscar() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        try {
            cc.buscarPorId("   ");
            fail("Debe lanzar IllegalArgumentException si el id está vacío");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede estar vacío"));
        }
    }

    @Test
    public void registrarNull_lanzaExcepcion() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        try {
            cc.registrar(null);
            fail("Debe lanzar IllegalArgumentException si cliente es null");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede ser null"));
        }
    }

    @Test
    public void ordenarPorNombre_ordenAscendente() throws Exception {
        ClientController cc = new ClientController(clientesPath());
        cc.registrar(new Client("C1", "Zoe", "", Client.TipoCliente.VISITANTE));
        cc.registrar(new Client("C2", "Ana", "", Client.TipoCliente.FRECUENTE));
        cc.registrar(new Client("C3", "Luis", "", Client.TipoCliente.FRECUENTE));

        cc.ordenarPorNombre();
        List<Client> lista = cc.listar();
        assertEquals("Ana", lista.get(0).getNombre());
        assertEquals("Luis", lista.get(1).getNombre());
        assertEquals("Zoe", lista.get(2).getNombre());
    }

    @Test
    public void persistencia_seMantieneEntreInstancias() throws Exception {
        String path = clientesPath();

        ClientController cc1 = new ClientController(path);
        cc1.registrar(new Client("C001", "Ana", "8888-8888", Client.TipoCliente.FRECUENTE));
        cc1.registrar(new Client("C002", "Hugo", "", Client.TipoCliente.VISITANTE));

        ClientController cc2 = new ClientController(path);
        assertEquals(2, cc2.listar().size());
        assertNotNull(cc2.buscarPorId("C001"));
        assertNotNull(cc2.buscarPorId("C002"));
    }
}
