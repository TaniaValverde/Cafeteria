package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venta {

    public static final int PARA_LLEVAR = 0;
    public static final double DEFAULT_TAX_RATE = 0.13;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private LocalDateTime fechaHora;
    private int mesaNumero;
    private int codigoPedido; // ✅ ya existe en tu ZIP
    private double taxRate;

    private String estado;
    private String metodoPago;

    private final List<LineaVenta> lineas = new ArrayList<>();

    public static class LineaVenta {
        private final Producto producto;
        private final int cantidad;
        private final double precioUnitario;

        public LineaVenta(Producto producto, int cantidad, double precioUnitario) {
            if (producto == null) {
                throw new IllegalArgumentException("Producto no puede ser null.");
            }
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser > 0.");
            }
            if (precioUnitario < 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
            }
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return precioUnitario * cantidad; }
    }

    // ✅ Constructor que pide VentaTest: new Venta("V1", now, 1)
    public Venta(String id, LocalDateTime fechaHora, int mesaNumero) {
        this(id, fechaHora, mesaNumero, 0, DEFAULT_TAX_RATE);
    }

    // ✅ Constructor que pide VentaTest: new Venta("V1", now, 1, 0.10)
    public Venta(String id, LocalDateTime fechaHora, int mesaNumero, double taxRate) {
        this(id, fechaHora, mesaNumero, 0, taxRate);
    }

    // ✅ Constructor completo (ya existía en tu ZIP, lo mantenemos)
    public Venta(String id, LocalDateTime fechaHora, int mesaNumero, int codigoPedido, double taxRate) {
        setId(id);
        setFechaHora(fechaHora);
        setMesaNumero(mesaNumero);

        this.codigoPedido = Math.max(0, codigoPedido);
        setTaxRate(taxRate);

        this.estado = "PENDIENTE";
        this.metodoPago = "";
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getMesaNumero() { return mesaNumero; }
    public int getCodigoPedido() { return codigoPedido; }
    public double getTaxRate() { return taxRate; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }

    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID no puede ser vacío.");
        }
        this.id = id.trim();
    }

    public final void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("fechaHora no puede ser null.");
        }
        this.fechaHora = fechaHora;
    }

    public final void setMesaNumero(int mesaNumero) {
        boolean valido = mesaNumero == PARA_LLEVAR || (mesaNumero >= 1 && mesaNumero <= 5);
        if (!valido) {
            throw new IllegalArgumentException("Mesa inválida (0 para llevar o 1..5).");
        }
        this.mesaNumero = mesaNumero;
    }

    public final void setTaxRate(double taxRate) {
        if (taxRate < 0) {
            throw new IllegalArgumentException("taxRate no puede ser negativo.");
        }
        this.taxRate = taxRate;
    }

    public void setEstado(String estado) { this.estado = estado; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public boolean esParaLlevar() {
        return mesaNumero == PARA_LLEVAR;
    }

    public void agregarLinea(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser > 0.");
        }
        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    // ✅ VentaTest exige lista INMODIFICABLE
    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public double getSubtotal() {
        return lineas.stream().mapToDouble(LineaVenta::getSubtotal).sum();
    }

    public double getImpuesto() {
        return getSubtotal() * taxRate;
    }

    public double getTotal() {
        return getSubtotal() + getImpuesto();
    }

    // ✅ CSV actualizado (tu formato de 8 campos)
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
                String.valueOf(codigoPedido),
                String.valueOf(taxRate),
                estado == null ? "" : estado,
                metodoPago == null ? "" : metodoPago,
                items.toString());
    }

    public static Venta fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV de venta vacío.");
        }

        String[] parts = line.split(",", -1);

        // Para tu formato: mínimo deberían existir 4 (y normalmente 8)
        if (parts.length < 4) {
            throw new IllegalArgumentException("CSV de venta incompleto.");
        }

        try {
            String id = parts[0];
            LocalDateTime dt = LocalDateTime.parse(parts[1], DT_FORMAT);
            int mesa = Integer.parseInt(parts[2]);

            int codigoPedido = 0;
            double tax;
            String estado = "PENDIENTE";
            String metodo = "";
            String items = "";

            if (parts.length >= 8) {
                codigoPedido = parseIntSafe(parts[3], 0);
                tax = Double.parseDouble(parts[4]);
                estado = parts[5];
                metodo = parts[6];
                items = parts[7];
            } else {
                // fallback por compatibilidad (si quedara un formato viejo)
                tax = Double.parseDouble(parts[3]);
                if (parts.length >= 5) estado = parts[4];
                if (parts.length >= 6) metodo = parts[5];
                if (parts.length >= 7) items = parts[6];
            }

            Venta v = new Venta(id, dt, mesa, codigoPedido, tax);
            v.setEstado(estado);
            v.setMetodoPago(metodo);

            if (items != null && !items.isEmpty()) {
                String[] itemParts = items.split(";");
                for (String it : itemParts) {
                    if (it == null || it.isEmpty()) continue;

                    String[] f = it.split(":");
                    if (f.length < 3) {
                        throw new IllegalArgumentException("Item inválido en CSV de venta.");
                    }

                    String codigoProd = f[0];
                    int cantidad = Integer.parseInt(f[1]);
                    double precioUnit = Double.parseDouble(f[2]);

                    Producto p = new Producto(codigoProd, "N/A", "N/A", precioUnit, 0);
                    v.lineas.add(new LineaVenta(p, cantidad, precioUnit));
                }
            }

            return v;
        } catch (Exception ex) {
            // importante para VentaDAOTest: línea mala debe fallar -> DAO la ignora
            throw new IllegalArgumentException("CSV de venta inválido.", ex);
        }
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    // ✅ VentaDAOTest usa contains(new Venta("V001", now, 1)) -> equals por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}