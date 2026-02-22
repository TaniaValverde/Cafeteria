package Model;

import Interfases.Imprimible;
import Interfases.Persistible;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents an invoice generated from a {@link Pedido}.
 *
 * This model class calculates subtotal, tax (IVA), and total, and implements
 * {@link Imprimible} to generate a printable text format and {@link Persistible}
 * to support file-based persistence as required by the project.
 */
public class Factura implements Imprimible, Persistible {

    private final String idFactura;
    private final Pedido pedido;
    private final Cliente cliente;
    private final Mesa mesa;
    private final boolean paraLlevar;
    private final LocalDateTime fecha;
    private double subtotal;
    private double impuesto;
    private double total;

    private static final double IVA = 0.13;

    /**
     * Creates a new invoice for the given order context and calculates totals.
     *
     * @param pedido order associated with the invoice
     * @param cliente customer linked to the invoice (may be null depending on system rules)
     * @param mesa table assigned to the order (null when {@code paraLlevar} is true)
     * @param paraLlevar indicates whether the order is take-away
     */
    public Factura(Pedido pedido, Cliente cliente, Mesa mesa, boolean paraLlevar) {
        this.idFactura = "FAC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.pedido = pedido;
        this.cliente = cliente;
        this.mesa = mesa;
        this.paraLlevar = paraLlevar;
        this.fecha = LocalDateTime.now();
        calcularTotales();
    }

    /**
     * Computes subtotal, IVA tax, and total based on the products in the order.
     */
    private void calcularTotales() {
        List<Producto> productos = pedido.getProductos();

        subtotal = 0;
        for (Producto p : productos) {
            int cantidad = pedido.getCantidadDeProducto(p);
            subtotal = subtotal + (p.getPrecio() * cantidad);
        }
        impuesto = subtotal * IVA;
        total = subtotal + impuesto;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public boolean isParaLlevar() {
        return paraLlevar;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    /**
     * Generates the printable invoice text, including items, quantities, and totals.
     *
     * @return printable invoice content
     */
    @Override
    public String generarImpresion() {
        String texto = "";

        texto = texto + "=== Cafetería UCR Sede del Sur ===\n";
        texto = texto + "Factura: " + idFactura + "\n";
        texto = texto + "Fecha: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n";

        texto = texto + "Cliente: " + cliente.getNombre() + "\n";

        if (paraLlevar) {
            texto = texto + "Para llevar\n";
        } else {
            texto = texto + "Mesa: " + mesa.getNumero() + "\n";
        }

        texto = texto + "\n--- Productos ---\n";

        List<Producto> productos = pedido.getProductos();
        for (Producto p : productos) {
            int cant = pedido.getCantidadDeProducto(p);
            double subtotalProducto = p.getPrecio() * cant;

            texto = texto + p.getNombre() + " x" + cant
                  + "   ₡" + String.format("%.2f", subtotalProducto) + "\n";
        }

        texto = texto + "\n";
        texto = texto + "Subtotal: ₡" + String.format("%.2f", subtotal) + "\n";
        texto = texto + "IVA (13%): ₡" + String.format("%.2f", impuesto) + "\n";
        texto = texto + "TOTAL:    ₡" + String.format("%.2f", total) + "\n";
        texto = texto + "================================\n";
        texto = texto + "¡Gracias por su compra!\n";

        return texto;
    }

    /**
     * Persists the invoice data to a file.
     *
     * @param archivo target file path/name
     * @throws IOException if the persistence operation fails or is not implemented
     */
    @Override
    public void guardarEnArchivo(String archivo) throws IOException {
        throw new IOException("Guardar factura debe hacerse desde VentaDAO o similar (pendiente de implementación)");
    }

    @Override
    public String toString() {
        return "Factura " + idFactura + " - Total: ₡" + total;
    }
}