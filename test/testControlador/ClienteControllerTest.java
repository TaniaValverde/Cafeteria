package testControlador;

import Controlador.ClienteController;
import Model.Cliente;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class ClienteControllerTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private String clientesPath() throws Exception {
        File f = tmp.newFile("clientes.txt");
        return f.getAbsolutePath();
    }

    @Test
    public void iniciarVacio_siArchivoNuevo() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        assertNotNull(cc.listar());
        assertEquals(0, cc.listar().size());
    }

    @Test
    public void registrarYBuscarPorId_funciona() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());

        Cliente c = new Cliente("C001", "Ana", "8888-8888", Cliente.TipoCliente.FRECUENTE);
        cc.registrar(c);

        Cliente encontrado = cc.buscarPorId("C001");
        assertNotNull(encontrado);
        assertEquals("C001", encontrado.getId());
        assertEquals("Ana", encontrado.getNombre());
        assertEquals("8888-8888", encontrado.getTelefono());
        assertEquals(Cliente.TipoCliente.FRECUENTE, encontrado.getTipo());
    }

    @Test
    public void noPermiteIdDuplicado() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        cc.registrar(new Cliente("C001", "Ana", "8888-8888", Cliente.TipoCliente.FRECUENTE));

        try {
            cc.registrar(new Cliente("C001", "Tania", "9999-9999", Cliente.TipoCliente.VISITANTE));
            fail("Debe lanzar IllegalArgumentException por id duplicado");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        }
    }

    @Test
    public void modificar_actualizaCampos() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        cc.registrar(new Cliente("C001", "Ana", "8888-8888", Cliente.TipoCliente.FRECUENTE));

        cc.modificar("C001", "Ana Maria", "7777-7777", Cliente.TipoCliente.VISITANTE);

        Cliente mod = cc.buscarPorId("C001");
        assertEquals("Ana Maria", mod.getNombre());
        assertEquals("7777-7777", mod.getTelefono());
        assertEquals(Cliente.TipoCliente.VISITANTE, mod.getTipo());
    }

    @Test
    public void eliminar_quitaCliente() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        cc.registrar(new Cliente("C001", "Ana", "8888-8888", Cliente.TipoCliente.FRECUENTE));
        cc.registrar(new Cliente("C002", "Hugo", "", Cliente.TipoCliente.VISITANTE));

        cc.eliminar("C001");

        List<Cliente> lista = cc.listar();
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
        ClienteController cc = new ClienteController(clientesPath());
        try {
            cc.buscarPorId("NOEXISTE");
            fail("Debe lanzar IllegalArgumentException si no existe");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
        }
    }

    @Test
    public void validarIdVacio_enBuscar() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        try {
            cc.buscarPorId("   ");
            fail("Debe lanzar IllegalArgumentException si el id está vacío");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede estar vacío"));
        }
    }

    @Test
    public void registrarNull_lanzaExcepcion() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        try {
            cc.registrar(null);
            fail("Debe lanzar IllegalArgumentException si cliente es null");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede ser null"));
        }
    }

    @Test
    public void ordenarPorNombre_ordenAscendente() throws Exception {
        ClienteController cc = new ClienteController(clientesPath());
        cc.registrar(new Cliente("C1", "Zoe", "", Cliente.TipoCliente.VISITANTE));
        cc.registrar(new Cliente("C2", "Ana", "", Cliente.TipoCliente.FRECUENTE));
        cc.registrar(new Cliente("C3", "Luis", "", Cliente.TipoCliente.FRECUENTE));

        cc.ordenarPorNombre();
        List<Cliente> lista = cc.listar();
        assertEquals("Ana", lista.get(0).getNombre());
        assertEquals("Luis", lista.get(1).getNombre());
        assertEquals("Zoe", lista.get(2).getNombre());
    }

    @Test
    public void persistencia_seMantieneEntreInstancias() throws Exception {
        String path = clientesPath();

        ClienteController cc1 = new ClienteController(path);
        cc1.registrar(new Cliente("C001", "Ana", "8888-8888", Cliente.TipoCliente.FRECUENTE));
        cc1.registrar(new Cliente("C002", "Hugo", "", Cliente.TipoCliente.VISITANTE));

        ClienteController cc2 = new ClienteController(path);
        assertEquals(2, cc2.listar().size());
        assertNotNull(cc2.buscarPorId("C001"));
        assertNotNull(cc2.buscarPorId("C002"));
    }
}
