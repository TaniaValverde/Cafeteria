package testModel;

import Model.Client;
import Model.Invoice;
import Model.Table;
import Model.Order;
import Model.Product;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ReportTest {

    private static final double IVA = 0.13;
    private static final double DELTA = 0.0001;

    private Client cliente;
    private Product p1;
    private Product p2;

    @Before
    public void setUp() {
        cliente = new Client("1001", "Ana Pérez", "88887777", Client.TipoCliente.FRECUENTE);

        p1 = new Product("2001", "Café", "BEBIDA", 1000.0, 50);
        p2 = new Product("2002", "Galleta", "COMIDA", 500.0, 50);
    }

    private Order pedidoParaLlevarConProductos() {
        Order pedido = new Order(1, Order.PARA_LLEVAR, null);
        pedido.agregarProducto(p1, 2); // 2000
        pedido.agregarProducto(p2, 1); //  500
        return pedido;
    }

    private Order pedidoMesaConProductos(int numeroMesa) {
        Order pedido = new Order(2, Order.MESA, numeroMesa);
        pedido.agregarProducto(p1, 1); // 1000
        pedido.agregarProducto(p2, 3); // 1500
        return pedido;
    }

    @Test
    public void facturaNoDebeSerNula_yDebeTenerId() {
        Invoice f = new Invoice(pedidoParaLlevarConProductos(), cliente, null, true);

        assertNotNull(f);
        assertNotNull(f.getIdFactura());
        assertFalse(f.getIdFactura().trim().isEmpty());
        assertTrue("El id debería iniciar con FAC-", f.getIdFactura().startsWith("FAC-"));
    }

    @Test
    public void calcularTotal_paraLlevar_debeAplicarIVA() {
        Order pedido = pedidoParaLlevarConProductos();
        Invoice f = new Invoice(pedido, cliente, null, true);

        double subtotalEsperado = (1000.0 * 2) + (500.0 * 1); // 2500
        double totalEsperado = subtotalEsperado + (subtotalEsperado * IVA);

        assertEquals(totalEsperado, f.getTotal(), DELTA);
        assertTrue("El total no debe ser negativo", f.getTotal() >= 0);
    }

    @Test
    public void calcularTotal_conMesa_debeAplicarIVA() {
        int mesaNum = 1;
        Table mesa = new Table(mesaNum);

        Order pedido = pedidoMesaConProductos(mesaNum);
        Invoice f = new Invoice(pedido, cliente, mesa, false);

        double subtotalEsperado = (1000.0 * 1) + (500.0 * 3); // 2500
        double totalEsperado = subtotalEsperado + (subtotalEsperado * IVA);

        assertEquals(totalEsperado, f.getTotal(), DELTA);
    }

    @Test
    public void generarImpresion_debeIncluirDatosClave_paraLlevar() {
        Invoice f = new Invoice(pedidoParaLlevarConProductos(), cliente, null, true);

        String imp = f.generarImpresion();

        assertNotNull(imp);
        assertTrue(imp.contains("Factura:"));
        assertTrue(imp.contains("Cliente: " + cliente.getNombre()));
        assertTrue("Debe indicar 'Para llevar'", imp.toLowerCase().contains("para llevar"));
        assertTrue("Debe listar productos", imp.contains("--- Productos ---"));
        assertTrue("Debe mostrar TOTAL", imp.toUpperCase().contains("TOTAL"));
    }

    @Test
    public void generarImpresion_debeIncluirMesa_siNoEsParaLlevar() {
        int mesaNum = 2;
        Table mesa = new Table(mesaNum);

        Invoice f = new Invoice(pedidoMesaConProductos(mesaNum), cliente, mesa, false);

        String imp = f.generarImpresion();

        assertNotNull(imp);
        assertTrue("Debe incluir el número de mesa", imp.contains("Mesa: " + mesaNum));
    }

    @Test
    public void toString_debeSerLegible_yNoVacio() {
        Invoice f = new Invoice(pedidoParaLlevarConProductos(), cliente, null, true);

        String s = f.toString();
        assertNotNull(s);
        assertFalse(s.trim().isEmpty());
        assertTrue(s.contains("Factura"));
        assertTrue(s.contains("Total"));
    }

    @Test
    public void guardarEnArchivo_debeLanzarIOException_porImplementacionPendiente() {
        Invoice f = new Invoice(pedidoParaLlevarConProductos(), cliente, null, true);

        try {
            f.guardarEnArchivo("factura.txt");
            fail("Se esperaba IOException porque guardarEnArchivo está pendiente de implementación.");
        } catch (IOException ex) {
            assertNotNull(ex.getMessage());
        }
    }
}
