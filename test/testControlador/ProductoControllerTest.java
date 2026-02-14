package testControlador;

import Controlador.ProductoController;
import Model.Producto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class ProductoControllerTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private String productosPath() throws Exception {
        File f = tmp.newFile("productos.txt");
        return f.getAbsolutePath();
    }

    @Test
    public void iniciarVacio_siArchivoNuevo() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        assertNotNull(pc.listar());
        assertEquals(0, pc.listar().size());
    }

    @Test
    public void agregarYBuscarPorCodigo_funciona() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        Producto p = new Producto("P001", "Cafe Negro", "Bebida", 1200.0, 10);
        pc.agregar(p);

        Producto encontrado = pc.buscarPorCodigo("P001");
        assertNotNull(encontrado);
        assertEquals("P001", encontrado.getCodigo());
        assertEquals("Cafe Negro", encontrado.getNombre());
        assertEquals("Bebida", encontrado.getCategoria());
        assertEquals(1200.0, encontrado.getPrecio(), 0.0001);
        assertEquals(10, encontrado.getStock());
    }

    @Test
    public void noPermiteCodigoDuplicado() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        pc.agregar(new Producto("P001", "Cafe Negro", "Bebida", 1200.0, 10));

        try {
            pc.agregar(new Producto("P001", "Sandwich", "Comida", 2000.0, 5));
            fail("Debe lanzar IllegalArgumentException por código duplicado");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
        }
    }

    @Test
    public void modificar_actualizaCampos() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        pc.agregar(new Producto("P001", "Cafe Negro", "Bebida", 1200.0, 10));

        pc.modificar("P001", "Comida", 2500.0, 3);

        Producto mod = pc.buscarPorCodigo("P001");
        assertEquals("Comida", mod.getCategoria());
        assertEquals(2500.0, mod.getPrecio(), 0.0001);
        assertEquals(3, mod.getStock());
        assertEquals("Cafe Negro", mod.getNombre());
    }

    @Test
    public void eliminar_quitaProducto() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        pc.agregar(new Producto("P001", "Cafe Negro", "Bebida", 1200.0, 10));
        pc.agregar(new Producto("P002", "Sandwich", "Comida", 2000.0, 5));

        pc.eliminar("P001");

        List<Producto> lista = pc.listar();
        assertEquals(1, lista.size());
        assertEquals("P002", lista.get(0).getCodigo());

        try {
            pc.buscarPorCodigo("P001");
            fail("Debe lanzar IllegalArgumentException al buscar producto eliminado");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
        }
    }

    @Test
    public void buscarInexistente_lanzaExcepcion() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        try {
            pc.buscarPorCodigo("NOEXISTE");
            fail("Debe lanzar IllegalArgumentException si no existe");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
        }
    }

    @Test
    public void validarCodigoVacio_enBuscar() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        try {
            pc.buscarPorCodigo("   ");
            fail("Debe lanzar IllegalArgumentException si el código está vacío");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede estar vacío"));
        }
    }

    @Test
    public void agregarNull_lanzaExcepcion() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        try {
            pc.agregar(null);
            fail("Debe lanzar IllegalArgumentException si producto es null");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().toLowerCase().contains("no puede ser null"));
        }
    }

    @Test
    public void ordenarPorCodigo_ordenAscendente() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        pc.agregar(new Producto("P010", "X", "Bebida", 100, 1));
        pc.agregar(new Producto("P002", "Y", "Bebida", 100, 1));
        pc.agregar(new Producto("P001", "Z", "Bebida", 100, 1));

        pc.ordenarPorCodigo();
        List<Producto> lista = pc.listar();
        assertEquals("P001", lista.get(0).getCodigo());
        assertEquals("P002", lista.get(1).getCodigo());
        assertEquals("P010", lista.get(2).getCodigo());
    }

    @Test
    public void ordenarPorPrecio_ordenAscendente() throws Exception {
        ProductoController pc = new ProductoController(productosPath());
        pc.agregar(new Producto("P1", "A", "Bebida", 3000, 1));
        pc.agregar(new Producto("P2", "B", "Bebida", 1000, 1));
        pc.agregar(new Producto("P3", "C", "Bebida", 2000, 1));

        pc.ordenarPorPrecio();
        List<Producto> lista = pc.listar();
        assertEquals("P2", lista.get(0).getCodigo());
        assertEquals("P3", lista.get(1).getCodigo());
        assertEquals("P1", lista.get(2).getCodigo());
    }

    @Test
    public void persistencia_seMantieneEntreInstancias() throws Exception {
        String path = productosPath();

        ProductoController pc1 = new ProductoController(path);
        pc1.agregar(new Producto("P001", "Cafe Negro", "Bebida", 1200.0, 10));
        pc1.agregar(new Producto("P002", "Sandwich", "Comida", 2000.0, 5));

        ProductoController pc2 = new ProductoController(path);
        assertEquals(2, pc2.listar().size());
        assertNotNull(pc2.buscarPorCodigo("P001"));
        assertNotNull(pc2.buscarPorCodigo("P002"));
    }
}
