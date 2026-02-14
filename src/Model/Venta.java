package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sale (venta) performed by the cafeteria system.
 *
 * <p>A sale may be associated to a table (1..5) or be "para llevar" (0).</p>
 * <p>Includes line items, tax calculation and total amount.</p>
 */
public class Venta {

    /** Use table number 0 to represent "para llevar". */
    public static final int PARA_LLEVAR = 0;

    /** Default tax rate (modifiable via constructor). */
    public static final double DEFAULT_TAX_RATE = 0.13;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private LocalDateTime fechaHora;
    private int mesaNumero; // 1..5 or 0 for carryout
    private double taxRate;

    private final List<LineaVenta> lineas = new ArrayList<>();

    /**
     * Sale line item: product + quantity + unit price snapshot.
     * Storing unit price helps preserve historical accuracy if product price changes later.
     */
    public static class LineaVenta {
        private final Producto producto;
        private final int cantidad;
        private final double precioUnitario;

        public LineaVenta(Producto producto, int cantidad, double precioUnitario) {
            if (producto == null) throw new IllegalArgumentException("Product cannot be null.");
            if (cantidad <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
            if (precioUnitario < 0) throw new IllegalArgumentException("Unit price cannot be negative.");

            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return precioUnitario * cantidad; }
    }

    public Venta(String id, LocalDateTime fechaHora, int mesaNumero) {
        this(id, fechaHora, mesaNumero, DEFAULT_TAX_RATE);
    }

    public Venta(String id, LocalDateTime fechaHora, int mesaNumero, double taxRate) {
        setId(id);
        setFechaHora(fechaHora);
        setMesaNumero(mesaNumero);
        setTaxRate(taxRate);
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getMesaNumero() { return mesaNumero; }
    public double getTaxRate() { return taxRate; }

    /**
     * Returns an unmodifiable view of the line items (cannot be changed externally).
     */
    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale id cannot be empty.");
        }
        this.id = id.trim();
    }

    public final void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("Sale date-time cannot be null.");
        }
        this.fechaHora = fechaHora;
    }

    /**
     * Sets table number: 1..5 or 0 for carryout.
     */
    public final void setMesaNumero(int mesaNumero) {
        if (mesaNumero != PARA_LLEVAR && (mesaNumero < 1 || mesaNumero > 5)) {
            throw new IllegalArgumentException("Invalid table number. Use 1..5 or 0 for carryout.");
        }
        this.mesaNumero = mesaNumero;
    }

    public final void setTaxRate(double taxRate) {
        if (taxRate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
        this.taxRate = taxRate;
    }

    public boolean esParaLlevar() {
        return mesaNumero == PARA_LLEVAR;
    }

    /**
     * Adds a sale line using the current price of the product.
     *
     * <p>Note: stock discount is usually handled in controller/service (not here),
     * but you can enforce it externally.</p>
     */
    public void agregarLinea(Producto producto, int cantidad) {
        if (producto == null) throw new IllegalArgumentException("Product cannot be null.");
        if (cantidad <= 0) throw new IllegalArgumentException("Quantity must be > 0.");

        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    public double getSubtotal() {
        double sum = 0.0;
        for (LineaVenta lv : lineas) {
            sum += lv.getSubtotal();
        }
        return sum;
    }

    public double getImpuesto() {
        return getSubtotal() * taxRate;
    }

    public double getTotal() {
        return getSubtotal() + getImpuesto();
    }

    /**
     * Serializes this sale into a single CSV line.
     *
     * Format:
     * id,fechaHora,mesaNumero,taxRate,items
     *
     * items (semicolon separated):
     * codigo:cantidad:precioUnitario;codigo:cantidad:precioUnitario
     */
    public String toCsv() {
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < lineas.size(); i++) {
            LineaVenta lv = lineas.get(i);
            if (i > 0) items.append(";");
            items.append(escape(lv.getProducto().getCodigo()))
                 .append(":").append(lv.getCantidad())
                 .append(":").append(lv.getPrecioUnitario());
        }

        return String.join(",",
                escape(id),
                escape(fechaHora.format(DT_FORMAT)),
                String.valueOf(mesaNumero),
                String.valueOf(taxRate),
                escape(items.toString()));
    }

    /**
     * Parses a CSV line produced by {@link #toCsv()}.
     *
     * <p>Reconstructs products minimally with code and name "N/A" (category "N/A").</p>
     */
    public static Venta fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV line.");
        }

        try {
            String[] parts = splitCsv(line);
            if (parts.length < 5) {
                throw new IllegalArgumentException("Incomplete sale CSV.");
            }

            String id = unescape(parts[0]);
            LocalDateTime dt = LocalDateTime.parse(unescape(parts[1]), DT_FORMAT);
            int mesa = Integer.parseInt(parts[2].trim());
            double tax = Double.parseDouble(parts[3].trim());
            String items = unescape(parts[4]);

            Venta v = new Venta(id, dt, mesa, tax);

            if (!items.trim().isEmpty()) {
                String[] itemParts = items.split(";");
                for (String it : itemParts) {
                    if (it.trim().isEmpty()) continue;

                    String[] fields = it.split(":");
                    if (fields.length != 3) {
                        throw new IllegalArgumentException("Invalid item format in sale CSV.");
                    }

                    String codigoProd = unescape(fields[0]);
                    int cantidad = Integer.parseInt(fields[1].trim());
                    double precioUnit = Double.parseDouble(fields[2].trim());

                    // Minimal product reconstruction
                    Producto p = new Producto(codigoProd, "N/A", "N/A", precioUnit, 0);
                    v.lineas.add(new LineaVenta(p, cantidad, precioUnit));
                }
            }

            return v;

        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid sale CSV format.", ex);
        }
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",").replace("\\\\", "\\").trim();
    }

    /**
     * Splits a CSV line where commas can be escaped with "\,".
     */
    private static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (esc) {
                cur.append(c);
                esc = false;
            } else if (c == '\\') {
                cur.append(c);
                esc = true;
            } else if (c == ',') {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "Venta{id='" + id + "', fechaHora=" + fechaHora + ", mesaNumero=" + mesaNumero
                + ", subtotal=" + getSubtotal() + ", impuesto=" + getImpuesto()
                + ", total=" + getTotal() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta venta = (Venta) o;
        return id.equals(venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
