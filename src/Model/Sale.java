package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sale transaction in the cafeteria system (Model layer - MVC).
 *
 * A sale stores its identifier, date/time, service mode (table number or take-away),
 * payment metadata, customer snapshot data, and the list of sold items. It also
 * provides CSV serialization/deserialization to support file persistence.
 */
public class Sale {

    /** Special table number used to represent take-away service. */
    public static final int PARA_LLEVAR = 0;

    /** Default tax rate (IVA) applied to the sale total. */
    public static final double DEFAULT_TAX_RATE = 0.13;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private LocalDateTime fechaHora;
    private int mesaNumero;
    private int codigoPedido; // ✅ ya existe en tu ZIP
    private double taxRate;

    private String estado;
    private String metodoPago;

    // ✅ NUEVO: datos de cliente (persisten en CSV)
    private String clienteId;
    private String clienteNombre;
    private String clienteTipo; // FRECUENTE / VISITANTE (o null)

    private final List<LineaVenta> lineas = new ArrayList<>();

    /**
     * Represents an item line within a sale (product, quantity, unit price).
     */
    public static class LineaVenta {
        private final Product producto;
        private final int cantidad;
        private final double precioUnitario;

        public LineaVenta(Product producto, int cantidad, double precioUnitario) {
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

        public Product getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return precioUnitario * cantidad; }
    }

    /**
     * Creates a sale with the default tax rate.
     *
     * @param id sale identifier (non-blank)
     * @param fechaHora sale date/time (non-null)
     * @param mesaNumero table number (1..5) or 0 for take-away
     */
    public Sale(String id, LocalDateTime fechaHora, int mesaNumero) {
        this(id, fechaHora, mesaNumero, 0, DEFAULT_TAX_RATE);
    }

    /**
     * Creates a sale with a custom tax rate.
     *
     * @param id sale identifier (non-blank)
     * @param fechaHora sale date/time (non-null)
     * @param mesaNumero table number (1..5) or 0 for take-away
     * @param taxRate tax rate (>= 0)
     */
    public Sale(String id, LocalDateTime fechaHora, int mesaNumero, double taxRate) {
        this(id, fechaHora, mesaNumero, 0, taxRate);
    }

    /**
     * Creates a fully defined sale instance.
     *
     * @param id sale identifier (non-blank)
     * @param fechaHora sale date/time (non-null)
     * @param mesaNumero table number (1..5) or 0 for take-away
     * @param codigoPedido associated order code (non-negative)
     * @param taxRate tax rate (>= 0)
     */
    public Sale(String id, LocalDateTime fechaHora, int mesaNumero, int codigoPedido, double taxRate) {
        setId(id);
        setFechaHora(fechaHora);
        setMesaNumero(mesaNumero);

        this.codigoPedido = Math.max(0, codigoPedido);
        setTaxRate(taxRate);

        this.estado = "PENDIENTE";
        this.metodoPago = "";

        // cliente por defecto
        this.clienteId = null;
        this.clienteNombre = null;
        this.clienteTipo = null;
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getMesaNumero() { return mesaNumero; }
    public int getCodigoPedido() { return codigoPedido; }
    public double getTaxRate() { return taxRate; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }

    public String getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteTipo() { return clienteTipo; }

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

    /**
     * Stores a snapshot of customer data for persistence (CSV-safe fields only).
     *
     * @param c customer instance; if null, customer fields are cleared
     */
    public void setCliente(Client c) {
        if (c == null) {
            this.clienteId = null;
            this.clienteNombre = null;
            this.clienteTipo = null;
            return;
        }
        this.clienteId = emptyToNull(c.getId());
        this.clienteNombre = emptyToNull(c.getNombre());
        this.clienteTipo = (c.getTipo() != null) ? c.getTipo().name() : null;
    }

    public void setClienteCsv(String id, String nombre, String tipo) {
        this.clienteId = emptyToNull(id);
        this.clienteNombre = emptyToNull(nombre);
        this.clienteTipo = emptyToNull(tipo);
    }

    /**
     * Indicates whether the sale corresponds to take-away service.
     *
     * @return true if take-away, false otherwise
     */
    public boolean esParaLlevar() {
        return mesaNumero == PARA_LLEVAR;
    }

    /**
     * Adds a new line item using the product's current unit price.
     *
     * @param producto product to add
     * @param cantidad quantity to add (> 0)
     * @throws IllegalArgumentException if product is null or quantity is invalid
     */
    public void agregarLinea(Product producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser > 0.");
        }
        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    /**
     * Returns an unmodifiable view of the sale line items.
     *
     * @return unmodifiable list of line items
     */
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

    /**
     * Serializes the sale to a single CSV line for persistence, including its items and customer snapshot fields.
     *
     * @return CSV representation of the sale
     */
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
                safeCsv(id),
                safeCsv(fechaHora.format(DT_FORMAT)),
                String.valueOf(mesaNumero),
                String.valueOf(codigoPedido),
                String.valueOf(taxRate),
                safeCsv(estado),
                safeCsv(metodoPago),
                safeCsv(items.toString()),
                safeCsv(clienteId),
                safeCsv(clienteNombre),
                safeCsv(clienteTipo)
        );
    }

    /**
     * Deserializes a sale from a CSV line produced by {@link #toCsv()}.
     *
     * @param line CSV line
     * @return parsed sale instance
     * @throws IllegalArgumentException if the CSV is empty or invalid
     */
    public static Sale fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV de venta vacío.");
        }

        String[] parts = line.split(",", -1);

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
                tax = Double.parseDouble(parts[3]);
                if (parts.length >= 5) estado = parts[4];
                if (parts.length >= 6) metodo = parts[5];
                if (parts.length >= 7) items = parts[6];
            }

            Sale v = new Sale(id, dt, mesa, codigoPedido, tax);
            v.setEstado(estado);
            v.setMetodoPago(metodo);

            if (parts.length >= 11) {
                String cId = parts[8];
                String cNombre = parts[9];
                String cTipo = parts[10];
                v.setClienteCsv(cId, cNombre, cTipo);
            }

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

                    Product p = new Product(codigoProd, "N/A", "N/A", precioUnit, 0);
                    v.lineas.add(new LineaVenta(p, cantidad, precioUnit));
                }
            }

            return v;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("CSV de venta inválido.", ex);
        }
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return def; }
    }

    private static String safeCsv(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /** Equality is based only on the sale id.
     * @param o */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale)) return false;
        Sale venta = (Sale) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}