
package Model;

import Interfases.Imprimible;
import Interfases.Persistible;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public Factura(Pedido pedido, Cliente cliente, Mesa mesa, boolean paraLlevar) {
        this.idFactura = "FAC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.pedido = pedido;
        this.cliente = cliente;
        this.mesa = mesa;
        this.paraLlevar = paraLlevar;
        this.fecha = LocalDateTime.now();
        calcularTotales();
    }

    private void calcularTotales() {
        // ERROR DE COMPILACIÓN ESPERADO: en la clase Pedido hace falta implementar
        // el método getProductos() que devuelva List<Producto>
        List<Producto> productos = pedido.getProductos();

        subtotal = 0;
        for (Producto p : productos) {
            // ERROR DE COMPILACIÓN ESPERADO: en la clase Pedido hace falta implementar
            // el método getCantidadDeProducto(Producto) o similar para obtener la cantidad
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

    @Override
    public String generarImpresion() {
        String texto = "";

        texto = texto + "=== Cafetería UCR Sede del Sur ===\n";
        texto = texto + "Factura: " + idFactura + "\n";
        texto = texto + "Fecha: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n";

        // ERROR DE COMPILACIÓN ESPERADO: en la clase Cliente hace falta implementar
        // el método getNombre() para mostrar el nombre del cliente
        texto = texto + "Cliente: " + cliente.getNombre() + "\n";

        if (paraLlevar) {
            texto = texto + "Para llevar\n";
        } else {
            // ERROR DE COMPILACIÓN ESPERADO: en la clase Mesa hace falta implementar
            // el método getNumero() para mostrar el número de la mesa
            texto = texto + "Mesa: " + mesa.getNumero() + "\n";
        }

        texto = texto + "\n--- Productos ---\n";

        // ERROR DE COMPILACIÓN ESPERADO: depende de Pedido.getProductos()
        List<Producto> productos = pedido.getProductos();
        for (Producto p : productos) {
            // ERROR DE COMPILACIÓN ESPERADO: depende de Pedido.getCantidadDeProducto(Producto)
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

    @Override
    public void guardarEnArchivo(String archivo) throws IOException {
        // ERROR DE COMPILACIÓN / IMPLEMENTACIÓN ESPERADO:
        // Este método debe implementarse usando uno de los DAOs del paquete persistencia
        // (por ejemplo VentaDAO o un futuro FacturaDAO)
        // Por ahora se deja lanzando excepción para indicar que falta la integración
        throw new IOException("Guardar factura debe hacerse desde VentaDAO o similar (pendiente de implementación)");
    }

    @Override
    public String toString() {
        return "Factura " + idFactura + " - Total: ₡" + total;
    }
}