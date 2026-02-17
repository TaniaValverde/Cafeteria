package testPersistencia;

import Model.Producto;
import Model.Venta;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class VentaDAOTest {

    private Path tempDir;
    private Path archivoVentas;
    private Path archivoProductos;

    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("cafeteria_venta_test_");
        archivoVentas = tempDir.resolve("ventas_test.txt");
        archivoProductos = tempDir.resolve("productos_test.txt");

        ventaDAO = new VentaDAO(archivoVentas.toString());
        productoDAO = new ProductoDAO(archivoProductos.toString());
    }

    @After
    public void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }

    private Venta ventaEjemplo(String id, int mesa) {
        Venta v = new Venta(id, LocalDateTime.of(2026, 2, 16, 10, 0), mesa);
        v.agregarLinea(new Producto("P001", "Café", "Bebidas", 25.0, 100), 2);    // 50
        v.agregarLinea(new Producto("P002", "Galleta", "Snacks", 10.0, 100), 1); // 10
        return v; // subtotal 60
    }

    @Test
    public void cargar_archivoNoExiste_devuelveListaVacia() throws Exception {
        Files.deleteIfExists(archivoVentas);

        List<Venta> ventas = ventaDAO.cargar();

        assertNotNull("cargar() no debe retornar null", ventas);
        assertTrue("Si el archivo no existe, debe devolver lista vacía", ventas.isEmpty());
    }

    @Test
    public void guardar_listaVacia_creaArchivo_yCargarDevuelveVacia() throws Exception {
        ventaDAO.guardar(Collections.<Venta>emptyList());

        assertTrue("guardar() debe crear el archivo si no existe", Files.exists(archivoVentas));

        List<Venta> cargadas = ventaDAO.cargar();
        assertNotNull(cargadas);
        assertTrue("Si se guardó vacío, al cargar debe estar vacío", cargadas.isEmpty());
    }

    @Test
    public void guardar_yLuegoCargar_conservaVentas_yTotales() throws Exception {
        List<Venta> lista = new ArrayList<>();
        lista.add(ventaEjemplo("V001", 1));
        lista.add(ventaEjemplo("V002", Venta.PARA_LLEVAR));

        ventaDAO.guardar(lista);

        List<Venta> cargadas = ventaDAO.cargar();
        assertNotNull(cargadas);
        assertEquals(2, cargadas.size());

        // equals compara por id
        assertTrue(cargadas.contains(new Venta("V001", LocalDateTime.now(), 1)));
        assertTrue(cargadas.contains(new Venta("V002", LocalDateTime.now(), Venta.PARA_LLEVAR)));

        // Validar totales de V001 (debe reconstruir lineas desde CSV)
        Venta v001 = null;
        for (Venta v : cargadas) {
            if ("V001".equals(v.getId())) { v001 = v; break; }
        }
        assertNotNull("No se encontró V001", v001);

        assertEquals(60.0, v001.getSubtotal(), 0.0001);
        assertEquals(60.0 * v001.getTaxRate(), v001.getImpuesto(), 0.0001);
        assertEquals(v001.getSubtotal() + v001.getImpuesto(), v001.getTotal(), 0.0001);
    }

    @Test
    public void guardar_sobrescribeContenido_anterior() throws Exception {
        ventaDAO.guardar(Arrays.asList(ventaEjemplo("V001", 1)));
        ventaDAO.guardar(Arrays.asList(ventaEjemplo("V999", 2)));

        List<Venta> cargadas = ventaDAO.cargar();
        assertNotNull(cargadas);
        assertEquals("Debe sobrescribir el contenido anterior", 1, cargadas.size());

        assertTrue(cargadas.contains(new Venta("V999", LocalDateTime.now(), 2)));
        assertFalse(cargadas.contains(new Venta("V001", LocalDateTime.now(), 1)));
    }

    @Test
    public void guardar_creaDirectorios_siNoExisten() throws Exception {
        Path subDir = tempDir.resolve("data").resolve("sub");
        Path archivo2 = subDir.resolve("ventas.txt");

        VentaDAO dao2 = new VentaDAO(archivo2.toString());
        dao2.guardar(Arrays.asList(ventaEjemplo("V010", 3)));

        assertTrue("Debe crear directorios padre", Files.exists(subDir));
        assertTrue("Debe crear archivo al guardar", Files.exists(archivo2));
    }

    @Test
    public void guardarVenta_append_agregaSinBorrarLasExistentes() throws Exception {
        ventaDAO.guardar(Collections.<Venta>emptyList());

        ventaDAO.guardarVenta(ventaEjemplo("V100", 4));
        ventaDAO.guardarVenta(ventaEjemplo("V101", 5));

        List<Venta> cargadas = ventaDAO.cargar();
        assertEquals(2, cargadas.size());
        assertTrue(cargadas.contains(new Venta("V100", LocalDateTime.now(), 4)));
        assertTrue(cargadas.contains(new Venta("V101", LocalDateTime.now(), 5)));
    }

    @Test
    public void cargar_robusto_ignoraLineasInvalidas_yCargaLasBuenas() throws Exception {
        // Archivo con una línea mala + una buena
        String mala = "esto,no,es,una,venta,valida";
        Venta buena = ventaEjemplo("V200", 1);

        String contenido = ""
                + mala + "\n"
                + buena.toCsv() + "\n";

        Files.write(archivoVentas, contenido.getBytes());

        List<Venta> cargadas = ventaDAO.cargar();
        assertNotNull(cargadas);
        assertEquals("Debe ignorar la mala y cargar la buena", 1, cargadas.size());
        assertTrue(cargadas.contains(new Venta("V200", LocalDateTime.now(), 1)));
    }

    @Test
    public void getVentas_noDebeSerNull_yDebeReflejarArchivo() throws Exception {
        ventaDAO.guardar(Arrays.asList(
                ventaEjemplo("V300", 2),
                ventaEjemplo("V301", Venta.PARA_LLEVAR)
        ));

        ArrayList<Venta> ventas = ventaDAO.getVentas();

        assertNotNull(ventas);
        assertEquals(2, ventas.size());
        assertTrue(ventas.contains(new Venta("V300", LocalDateTime.now(), 2)));
        assertTrue(ventas.contains(new Venta("V301", LocalDateTime.now(), Venta.PARA_LLEVAR)));
    }

    @Test
    public void registrarVentaConStock_descuentaStock_yGuardaVenta() throws Exception {
        // Inventario inicial
        productoDAO.guardar(Arrays.asList(
                new Producto("P001", "Café", "Bebidas", 25.0, 10),
                new Producto("P002", "Galleta", "Snacks", 10.0, 5)
        ));

        // Venta consume P001x2 y P002x1
        Venta v = new Venta("V400", LocalDateTime.of(2026, 2, 16, 11, 0), 2);
        v.agregarLinea(new Producto("P001", "Café", "Bebidas", 25.0, 10), 2);
        v.agregarLinea(new Producto("P002", "Galleta", "Snacks", 10.0, 5), 1);

        ventaDAO.registrarVentaConStock(v, productoDAO);

        // stock actualizado
        List<Producto> productos = productoDAO.cargar();
        Producto p1 = null, p2 = null;
        for (Producto p : productos) {
            if ("P001".equals(p.getCodigo())) p1 = p;
            if ("P002".equals(p.getCodigo())) p2 = p;
        }

        assertNotNull(p1);
        assertNotNull(p2);

        assertEquals(8, p1.getStock()); // 10 - 2
        assertEquals(4, p2.getStock()); // 5 - 1

        // venta guardada
        List<Venta> ventas = ventaDAO.cargar();
        assertTrue(ventas.contains(new Venta("V400", LocalDateTime.now(), 2)));
    }

    @Test
    public void registrarVentaConStock_productoNoExiste_lanzaIllegalState_yNoGuardaVenta() throws Exception {
        // Solo existe P001
        productoDAO.guardar(Arrays.asList(
                new Producto("P001", "Café", "Bebidas", 25.0, 10)
        ));

        Venta v = new Venta("V401", LocalDateTime.of(2026, 2, 16, 12, 0), 1);
        v.agregarLinea(new Producto("P999", "Fantasma", "N/A", 1.0, 1), 1); // no existe

        try {
            ventaDAO.registrarVentaConStock(v, productoDAO);
            fail("Se esperaba IllegalStateException por producto inexistente");
        } catch (IllegalStateException ex) {
            // ok
        }

        // No debe guardar venta si falla
        List<Venta> ventas = ventaDAO.cargar();
        assertTrue(ventas.isEmpty());
    }

    @Test
    public void registrarVentaConStock_sinStock_lanzaIllegalState_yNoGuardaVenta() throws Exception {
        productoDAO.guardar(Arrays.asList(
                new Producto("P001", "Café", "Bebidas", 25.0, 1) // solo 1
        ));

        Venta v = new Venta("V402", LocalDateTime.of(2026, 2, 16, 13, 0), 1);
        v.agregarLinea(new Producto("P001", "Café", "Bebidas", 25.0, 1), 2); // pide 2

        try {
            ventaDAO.registrarVentaConStock(v, productoDAO);
            fail("Se esperaba IllegalStateException por stock insuficiente");
        } catch (IllegalStateException ex) {
            // ok (lo lanza Producto.descontarStock)
        }

        // venta no guardada
        List<Venta> ventas = ventaDAO.cargar();
        assertTrue(ventas.isEmpty());

        // stock NO debe cambiar porque no se guardó (y el método falla antes de guardar)
        List<Producto> productos = productoDAO.cargar();
        assertEquals(1, productos.get(0).getStock());
    }

    @Test
    public void guardarVenta_null_lanzaIllegalArgumentException() throws Exception {
        try {
            ventaDAO.guardarVenta(null);
            fail("Se esperaba IllegalArgumentException por venta null");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }
}
