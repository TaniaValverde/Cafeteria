package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Venta {

    public static final int PARA_LLEVAR = 0;
    public static final double DEFAULT_TAX_RATE = 0.13;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private LocalDateTime fechaHora;
    private int mesaNumero;
    private int codigoPedido; // ✅ NUEVO
    private double taxRate;

    private String estado;
    private String metodoPago;

    private final List<LineaVenta> lineas = new ArrayList<>();

    public static class LineaVenta {
        private final Producto producto;
        private final int cantidad;
        private final double precioUnitario;

        public LineaVenta(Producto producto, int cantidad, double precioUnitario) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return precioUnitario * cantidad; }
    }

    public Venta(String id, LocalDateTime fechaHora, int mesaNumero, int codigoPedido, double taxRate) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.mesaNumero = mesaNumero;
        this.codigoPedido = codigoPedido; // ✅
        this.taxRate = taxRate;
        this.estado = "PENDIENTE";
        this.metodoPago = "";
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getMesaNumero() { return mesaNumero; }
    public int getCodigoPedido() { return codigoPedido; } // ✅
    public double getTaxRate() { return taxRate; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }

    public void setEstado(String estado) { this.estado = estado; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public boolean esParaLlevar() { return mesaNumero == PARA_LLEVAR; }

    public void agregarLinea(Producto producto, int cantidad) {
        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    public List<LineaVenta> getLineas() { return lineas; }

    public double getSubtotal() {
        return lineas.stream().mapToDouble(LineaVenta::getSubtotal).sum();
    }

    public double getImpuesto() { return getSubtotal() * taxRate; }
    public double getTotal() { return getSubtotal() + getImpuesto(); }

    // ✅ CSV actualizado
    public String toCsv() {
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < lineas.size(); i++) {
            LineaVenta lv = lineas.get(i);
            if (i > 0) items.append(";");
            items.append(lv.getProducto().getCodigo())
                    .append(":").append(lv.getCantidad())
                    .append(":").append(lv.getPrecioUnitario());
        }

        return String.join(",",
                id,
                fechaHora.format(DT_FORMAT),
                String.valueOf(mesaNumero),
                String.valueOf(codigoPedido), // ✅
                String.valueOf(taxRate),
                estado,
                metodoPago,
                items.toString());
    }

    public static Venta fromCsv(String line) {
        String[] parts = line.split(",", -1);

        String id = parts[0];
        LocalDateTime dt = LocalDateTime.parse(parts[1], DT_FORMAT);
        int mesa = Integer.parseInt(parts[2]);

        int codigoPedido = 0;
        double tax;
        String estado;
        String metodo;
        String items;

        if (parts.length >= 8) {
            codigoPedido = Integer.parseInt(parts[3]);
            tax = Double.parseDouble(parts[4]);
            estado = parts[5];
            metodo = parts[6];
            items = parts[7];
        } else {
            tax = Double.parseDouble(parts[3]);
            estado = parts[4];
            metodo = parts[5];
            items = parts.length > 6 ? parts[6] : "";
        }

        Venta v = new Venta(id, dt, mesa, codigoPedido, tax);
        v.setEstado(estado);
        v.setMetodoPago(metodo);

        if (!items.isEmpty()) {
            String[] itemParts = items.split(";");
            for (String it : itemParts) {
                String[] f = it.split(":");
                Producto p = new Producto(f[0], "N/A", "N/A",
                        Double.parseDouble(f[2]), 0);
                v.lineas.add(new LineaVenta(p,
                        Integer.parseInt(f[1]),
                        Double.parseDouble(f[2])));
            }
        }

        return v;
    }
}