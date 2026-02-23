package testPersistencia;

import Model.Product;
import Persistencia.ProductDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.*;

public class ProductDAOTest {

    private Path tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("cafeteria_test_");
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

    private ProductDAO dao(Path archivo) {
        return new ProductDAO(archivo.toString());
    }

    // RF-08 + RA6: si no existe archivo, debe devolver lista vacía (no null)
    @Test
    public void cargar_archivoNoExiste_devuelveListaVacia() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        Files.deleteIfExists(archivo);

        List<Product> productos = dao(archivo).cargar();

        assertNotNull("cargar() nunca debe retornar null", productos);
        assertTrue("Si el archivo no existe, debe devolver lista vacía", productos.isEmpty());
    }

    // RF-08: guardar lista vacía crea archivo y luego cargar devuelve vacía
    @Test
    public void guardar_listaVacia_creaArchivo_yCargarDevuelveVacia() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        ProductDAO d = dao(archivo);

        d.guardar(Collections.<Product>emptyList());

        assertTrue("guardar() debe crear el archivo si no existe", Files.exists(archivo));

        List<Product> cargados = d.cargar();
        assertNotNull(cargados);
        assertTrue("Si se guardó lista vacía, al cargar debe devolver vacía", cargados.isEmpty());
    }

    // RF-08: guardar y cargar conserva datos (flujo real)
    @Test
    public void guardar_yLuegoCargar_conservaDatos() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        ProductDAO d = dao(archivo);

        List<Product> lista = new ArrayList<>();
        lista.add(new Product("P001", "Café", "Bebidas", 25.0, 10));
        lista.add(new Product("P002", "Galleta", "Snacks", 12.5, 30));

        d.guardar(lista);

        List<Product> cargados = d.cargar();
        assertNotNull(cargados);
        assertEquals("Debe cargar la misma cantidad de productos guardados", 2, cargados.size());

        // equals() compara solo codigo
        assertTrue(cargados.contains(new Product("P001", "X", "X", 0, 0)));
        assertTrue(cargados.contains(new Product("P002", "X", "X", 0, 0)));

        Product p1 = null;
        for (Product p : cargados) {
            if ("P001".equals(p.getCodigo())) { p1 = p; break; }
        }
        assertNotNull("No se encontró P001", p1);
        assertEquals("Café", p1.getNombre());
        assertEquals("Bebidas", p1.getCategoria());
        assertEquals(25.0, p1.getPrecio(), 0.0001);
        assertEquals(10, p1.getStock());
    }

    // RF-08: guardar sobrescribe (TRUNCATE_EXISTING)
    @Test
    public void guardar_sobrescribeContenido_anterior() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        ProductDAO d = dao(archivo);

        d.guardar(Arrays.asList(new Product("P001", "Café", "Bebidas", 25.0, 10)));
        d.guardar(Arrays.asList(new Product("P999", "Prueba", "Test", 1.0, 1)));

        List<Product> cargados = d.cargar();
        assertEquals("Debe sobrescribir el contenido anterior", 1, cargados.size());
        assertTrue(cargados.contains(new Product("P999", "X", "X", 0, 0)));
        assertFalse(cargados.contains(new Product("P001", "X", "X", 0, 0)));
    }

    // RA6: robustez -> crea directorios automáticamente
    @Test
    public void guardar_creaDirectorios_siNoExisten() throws Exception {
        Path archivo = tempDir.resolve("data").resolve("sub").resolve("productos.txt");
        ProductDAO d = dao(archivo);

        d.guardar(Arrays.asList(new Product("P010", "Té", "Bebidas", 10.0, 5)));

        assertTrue("Debe crear directorio padre", Files.exists(archivo.getParent()));
        assertTrue("Debe crear el archivo al guardar", Files.exists(archivo));
    }

    // RA6: cargar ignora líneas vacías e incompletas (tu DAO ya hace continue)
    @Test
    public void cargar_ignoraLineasVacias_yLineasIncompletas() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        ProductDAO d = dao(archivo);

        String contenido = ""
                + "\n"
                + "   \n"
                + "P001,Café,Bebidas,25.0,10\n"
                + "incompleta,solo,dos\n"
                + "P002,Galleta,Snacks,12.5,30\n";

        Files.write(archivo, contenido.getBytes());

        List<Product> cargados = d.cargar();
        assertEquals("Debe cargar solo líneas válidas (5 campos)", 2, cargados.size());
        assertTrue(cargados.contains(new Product("P001", "X", "X", 0, 0)));
        assertTrue(cargados.contains(new Product("P002", "X", "X", 0, 0)));
    }

    // RF-09: manejo de errores -> números inválidos
    // Tu implementación ACTUAL lanza NumberFormatException (y está bien testearlo).
    @Test(expected = NumberFormatException.class)
    public void cargar_conNumeroInvalido_lanzaNumberFormatException() throws Exception {
        Path archivo = tempDir.resolve("productos.txt");
        ProductDAO d = dao(archivo);

        String contenido = "P001,Café,Bebidas,no_es_numero,10\n";
        Files.write(archivo, contenido.getBytes());

        d.cargar();
    }

    // RF-09 / RA6: error de E/S -> ruta inválida (usar directorio como archivo)
    @Test
    public void guardar_enRutaQueEsDirectorio_debeLanzarIOException() throws Exception {
        Path carpeta = tempDir.resolve("carpeta_no_archivo");
        Files.createDirectories(carpeta);

        ProductDAO d = dao(carpeta); // OJO: ruta apunta a un DIRECTORIO

        try {
            d.guardar(Arrays.asList(new Product("P001", "Café", "Bebidas", 25.0, 10)));
            fail("Se esperaba IOException al intentar escribir en un directorio");
        } catch (IOException ex) {
            // ok
            assertTrue(true);
        }
    }

}
