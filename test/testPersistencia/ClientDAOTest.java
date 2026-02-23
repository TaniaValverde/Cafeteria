package testPersistencia;

import Model.Client;
import Persistencia.ClientDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.*;

public class ClientDAOTest {

    private Path tempDir;
    private Path archivo;
    private ClientDAO dao;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("cafeteria_cliente_test_");
        archivo = tempDir.resolve("clientes_test.txt");
        dao = new ClientDAO(archivo.toString());
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

    @Test
    public void cargar_archivoNoExiste_devuelveListaVacia() throws Exception {
        Files.deleteIfExists(archivo);

        List<Client> clientes = dao.cargar();

        assertNotNull("cargar() nunca debe retornar null", clientes);
        assertTrue("Si no existe el archivo, debe devolver lista vacía", clientes.isEmpty());
    }

    @Test
    public void guardar_listaVacia_creaArchivo_yCargarDevuelveVacia() throws Exception {
        dao.guardar(Collections.<Client>emptyList());

        assertTrue("guardar() debe crear el archivo si no existe", Files.exists(archivo));

        List<Client> cargados = dao.cargar();
        assertNotNull(cargados);
        assertTrue("Si se guardó lista vacía, al cargar debe devolver vacía", cargados.isEmpty());
    }

    @Test
    public void guardar_yLuegoCargar_conservaDatos() throws Exception {
        List<Client> lista = new ArrayList<>();
        lista.add(new Client("C001", "Ana", "7777-1111", Client.TipoCliente.FRECUENTE));
        lista.add(new Client("C002", "Luis", "8888-2222", Client.TipoCliente.VISITANTE));

        dao.guardar(lista);

        List<Client> cargados = dao.cargar();

        assertNotNull(cargados);
        assertEquals("Debe cargar la misma cantidad guardada", 2, cargados.size());

        // equals() compara por id
        assertTrue(cargados.contains(new Client("C001", "X", "X", Client.TipoCliente.FRECUENTE)));
        assertTrue(cargados.contains(new Client("C002", "X", "X", Client.TipoCliente.VISITANTE)));

        // Validar datos del primero
        Client c1 = null;
        for (Client c : cargados) {
            if ("C001".equals(c.getId())) { c1 = c; break; }
        }
        assertNotNull("No se encontró C001", c1);

        assertEquals("Ana", c1.getNombre());
        assertEquals("7777-1111", c1.getTelefono());
        assertEquals(Client.TipoCliente.FRECUENTE, c1.getTipo());
    }

    @Test
    public void guardar_sobrescribeContenido_anterior() throws Exception {
        dao.guardar(Arrays.asList(new Client("C001", "Ana", "1111", Client.TipoCliente.FRECUENTE)
        ));

        dao.guardar(Arrays.asList(new Client("C999", "Prueba", "", Client.TipoCliente.VISITANTE)
        ));

        List<Client> cargados = dao.cargar();
        assertEquals("Debe sobrescribir (TRUNCATE_EXISTING)", 1, cargados.size());

        assertTrue(cargados.contains(new Client("C999", "X", "X", Client.TipoCliente.VISITANTE)));
        assertFalse(cargados.contains(new Client("C001", "X", "X", Client.TipoCliente.FRECUENTE)));
    }

    @Test
    public void guardar_creaDirectorios_siNoExisten() throws Exception {
        Path subDir = tempDir.resolve("data").resolve("sub");
        Path archivo2 = subDir.resolve("clientes.txt");
        ClientDAO dao2 = new ClientDAO(archivo2.toString());

        dao2.guardar(Arrays.asList(new Client("C010", "Maria", "9999", Client.TipoCliente.FRECUENTE)
        ));

        assertTrue("Debe crear directorios padre", Files.exists(subDir));
        assertTrue("Debe crear archivo al guardar", Files.exists(archivo2));
    }

    @Test
    public void cargar_ignoraLineasVacias_yLineasIncompletas() throws Exception {
        String contenido = ""
                + "\n"
                + "   \n"
                + "incompleta,solo,dos\n"
                + "C001,Ana,7777-1111,FRECUENTE\n"
                + "otra_incompleta\n"
                + "C002,Luis,8888-2222,VISITANTE\n";

        Files.write(archivo, contenido.getBytes());

        List<Client> cargados = dao.cargar();

        assertNotNull(cargados);
        assertEquals("Debe cargar solo líneas válidas (4 campos)", 2, cargados.size());
        assertTrue(cargados.contains(new Client("C001", "X", "X", Client.TipoCliente.FRECUENTE)));
        assertTrue(cargados.contains(new Client("C002", "X", "X", Client.TipoCliente.VISITANTE)));
    }

    @Test
    public void cargar_conTipoInvalido_lanzaIllegalArgumentException() throws Exception {
        String contenido = "C001,Ana,7777-1111,NO_EXISTE\n";
        Files.write(archivo, contenido.getBytes());

        try {
            dao.cargar();
            fail("Se esperaba IllegalArgumentException por TipoCliente inválido");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void guardar_enRutaQueEsDirectorio_debeLanzarIOException() throws Exception {
        Path carpeta = tempDir.resolve("carpeta_no_archivo");
        Files.createDirectories(carpeta);

        ClientDAO daoDir = new ClientDAO(carpeta.toString());

        try {
            daoDir.guardar(Arrays.asList(new Client("C001", "Ana", "7777", Client.TipoCliente.FRECUENTE)
            ));
            fail("Se esperaba IOException al intentar escribir en un directorio");
        } catch (IOException ex) {
            assertTrue(true);
        }
    }

}
