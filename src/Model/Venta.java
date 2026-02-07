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
 * <p>
 * A sale may be associated to a table (1..5) or be "para llevar".</p>
 * <p>
 * Includes line items, tax calculation and total amount.</p>
 */
public class Venta {

    /**
     * Use table number 0 to represent "para llevar".
     */
    public static final int PARA_LLEVAR = 0;

    /**
     * Default tax rate (modifiable via constructor).
     */
    public static final double DEFAULT_TAX_RATE = 0.13;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private LocalDateTime fechaHora;
    private int mesaNumero; // 1..5 or 0 for carryout
    private double taxRate;

    private final List<LineaVenta> lineas;

    /**
     * Sale line item: product + quantity + unit price snapshot. Storing unit
     * price here helps keep historical accuracy if prices change later.
     */
    public static class LineaVenta {

        private final Producto producto;
        private final int cantidad;
        private final double precioUnitario;

        /**
         * @param producto product (non-null)
         * @param cantidad quantity (> 0)
         * @param precioUnitario unit price (>= 0)
         */
        public LineaVenta(Producto producto, int cantidad, double precioUnitario) {
            if (producto == null) {
                throw new IllegalArgumentException("Product cannot be null.");
            }
            if (cantidad <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0.");
            }
            if (precioUnitario < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative.");
            }

            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        /**
         * @return product
         */
        public Producto getProducto() {
            return producto;
        }

        /**
         * @return quantity
         */
        public int getCantidad() {
            return cantidad;
        }

        /**
         * @return unit price snapshot
         */
        public double getPrecioUnitario() {
            return precioUnitario;
        }

        /**
         * @return line subtotal (unit price * quantity)
         */
        public double getSubtotal() {
            return precioUnitario * cantidad;
        }
    }

    /**
     * Creates a sale with default tax rate.
     *
     * @param id sale identifier (non-empty)
     * @param fechaHora sale date-time (non-null)
     * @param mesaNumero 1..5 for table, or 0 for "para llevar"
     */
    public Venta(String id, LocalDateTime fechaHora, int mesaNumero) {
        this(id, fechaHora, mesaNumero, DEFAULT_TAX_RATE);
    }

    /**
     * Creates a sale specifying tax rate.
     *
     * @param id sale identifier (non-empty)
     * @param fechaHora sale date-time (non-null)
     * @param mesaNumero 1..5 for table, or 0 for "para llevar"
     * @param taxRate tax rate (>= 0) e.g. 0.13 for 13%
     */
    public Venta(String id, LocalDateTime fechaHora, int mesaNumero, double taxRate) {
        setId(id);
        setFechaHora(fechaHora);
        setMesaNumero(mesaNumero);
        setTaxRate(taxRate);
        this.lineas = new ArrayList<>();
    }

    /**
     * @return sale id
     */
    public String getId() {
        return id;
    }

    /**
     * @return sale date-time
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * @return table number (1..5) or 0 for carryout
     */
    public int getMesaNumero() {
        return mesaNumero;
    }

    /**
     * @return tax rate used by this sale
     */
    public double getTaxRate() {
        return taxRate;
    }

    /**
     * @return unmodifiable list of line items
     */
    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * @param id non-empty sale id
     */
    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale id cannot be empty.");
        }
        this.id = id.trim();
    }

    /**
     * @param fechaHora non-null date-time
     */
    public final void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("Sale date-time cannot be null.");
        }
        this.fechaHora = fechaHora;
    }

    /**
     * Sets the table number.
     *
     * @param mesaNumero 1..5 or 0 for carryout
     */
    public final void setMesaNumero(int mesaNumero) {
        if (mesaNumero != PARA_LLEVAR && (mesaNumero < 1 || mesaNumero > 5)) {
            throw new IllegalArgumentException("Invalid table number. Use 1..5 or 0 for carryout.");
        }
        this.mesaNumero = mesaNumero;
    }

    /**
     * @param taxRate tax rate (>= 0)
     */
    public final void setTaxRate(double taxRate) {
        if (taxRate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
        this.taxRate = taxRate;
    }

    /**
     * Adds a product line using the product's current price. You may want to
     * call {@link Producto#descontarStock(int)} outside this method (e.g., in a
     * controller/service), depending on your architecture decisions.
     *
     * @param producto product (non-null)
     * @param cantidad quantity (> 0)
     */
    public void agregarLinea(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    /**
     * @return subtotal (sum of line subtotals)
     */
    public double getSubtotal() {
        double sum = 0.0;
        for (LineaVenta lv : lineas) {
            sum += lv.getSubtotal();
        }
        return sum;
    }

    /**
     * @return tax amount (subtotal * taxRate)
     */
    public double getImpuesto() {
        return getSubtotal() * taxRate;
    }

    /**
     * @return total (subtotal + tax)
     */
    public double getTotal() {
        return getSubtotal() + getImpuesto();
    }

    /**
     * @return true if this sale is carryout
     */
    public boolean esParaLlevar() {
        return mesaNumero == PARA_LLEVAR;
    }

    /**
     * Serializes this sale to a single CSV line for persistence.
     *
     * Format: id,fechaHora,mesaNumero,taxRate,items
     *
     * items format (semicolon separated):
     * codigo:cantidad:precioUnitario;codigo:cantidad:precioUnitario
     */
    public String toCsv() {
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < lineas.size(); i++) {
            LineaVenta lv = lineas.get(i);
            if (i > 0) {
                items.append(";");
            }
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
     * Builds a Venta from a CSV line produced by {@link #toCsv()}.
     *
     * <p>
     * Note: products are reconstructed only with code (category/price/stock
     * should be resolved from your product catalog if needed).</p>
     *
     * @param line csv line
     * @return parsed sale
     */
    public static Venta fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV line.");
        }

        String[] parts = splitCsv(line);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Incomplete sale CSV: " + line);
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
                String[] fields = it.split(":");
                if (fields.length != 3) {
                    continue;
                }

                String codigoProd = unescape(fields[0]);
                int cantidad = Integer.parseInt(fields[1].trim());
                double precioUnit = Double.parseDouble(fields[2].trim());

                // Minimal product reconstruction (code only).
                Producto p = new Producto(codigoProd, "N/A", "N/A", precioUnit, 0);

                v.lineas.add(new LineaVenta(p, cantidad, precioUnit));
            }
        }

        return v;
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
        return out.toArray(String[]::new);
    }

    @Override
    public String toString() {
        return "Venta{id='" + id + "', fechaHora=" + fechaHora + ", mesaNumero=" + mesaNumero
                + ", subtotal=" + getSubtotal() + ", impuesto=" + getImpuesto()
                + ", total=" + getTotal() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        Venta venta = (Venta) o;
        return id.equals(venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
