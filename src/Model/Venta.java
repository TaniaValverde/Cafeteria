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
    private double taxRate;

    private String estado;     // PENDIENTE | PAGADA
    private String metodoPago; // EFECTIVO | TARJETA | SINPE | ""

    private final List<LineaVenta> lineas = new ArrayList<>();

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

        this.estado = "PENDIENTE";
        this.metodoPago = ""; // vacío hasta cobrar
    }

    public String getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getMesaNumero() { return mesaNumero; }
    public double getTaxRate() { return taxRate; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }

    public void setEstado(String estado) {
        if (estado == null || estado.isBlank()) throw new IllegalArgumentException("Estado no puede ser vacío.");
        this.estado = estado.toUpperCase();
    }

    public void setMetodoPago(String metodoPago) {
        if (metodoPago == null) metodoPago = "";
        this.metodoPago = metodoPago.trim().toUpperCase();
    }

    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Sale id cannot be empty.");
        this.id = id.trim();
    }

    public final void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) throw new IllegalArgumentException("Sale date-time cannot be null.");
        this.fechaHora = fechaHora;
    }

    public final void setMesaNumero(int mesaNumero) {
        if (mesaNumero != PARA_LLEVAR && (mesaNumero < 1 || mesaNumero > 5)) {
            throw new IllegalArgumentException("Invalid table number. Use 1..5 or 0 for carryout.");
        }
        this.mesaNumero = mesaNumero;
    }

    public final void setTaxRate(double taxRate) {
        if (taxRate < 0) throw new IllegalArgumentException("Tax rate cannot be negative.");
        this.taxRate = taxRate;
    }

    public boolean esParaLlevar() {
        return mesaNumero == PARA_LLEVAR;
    }

    public void agregarLinea(Producto producto, int cantidad) {
        if (producto == null) throw new IllegalArgumentException("Product cannot be null.");
        if (cantidad <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
        lineas.add(new LineaVenta(producto, cantidad, producto.getPrecio()));
    }

    public double getSubtotal() {
        double sum = 0.0;
        for (LineaVenta lv : lineas) sum += lv.getSubtotal();
        return sum;
    }

    public double getImpuesto() { return getSubtotal() * taxRate; }
    public double getTotal() { return getSubtotal() + getImpuesto(); }

    /**
     * CSV (nuevo):
     * id,fechaHora,mesaNumero,taxRate,estado,metodoPago,items
     *
     * Compatibilidad:
     * - viejo: id,fechaHora,mesaNumero,taxRate,estado,items
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
                escape(estado),
                escape(metodoPago),
                escape(items.toString()));
    }

    public static Venta fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) throw new IllegalArgumentException("Invalid CSV line.");

        try {
            String[] parts = splitCsv(line);

            if (parts.length < 6) throw new IllegalArgumentException("Incomplete sale CSV.");

            String id = unescape(parts[0]);
            LocalDateTime dt = LocalDateTime.parse(unescape(parts[1]), DT_FORMAT);
            int mesa = Integer.parseInt(parts[2].trim());
            double tax = Double.parseDouble(parts[3].trim());
            String estado = unescape(parts[4]);

            String metodoPago = "";
            String items;

            if (parts.length >= 7) {
                metodoPago = unescape(parts[5]);
                items = unescape(parts[6]);
            } else {
                items = unescape(parts[5]); // formato viejo
            }

            Venta v = new Venta(id, dt, mesa, tax);
            v.setEstado(estado);
            v.setMetodoPago(metodoPago);

            if (!items.trim().isEmpty()) {
                String[] itemParts = items.split(";");
                for (String it : itemParts) {
                    if (it.trim().isEmpty()) continue;

                    String[] fields = it.split(":");
                    if (fields.length != 3) throw new IllegalArgumentException("Invalid item format in sale CSV.");

                    String codigoProd = unescape(fields[0]);
                    int cantidad = Integer.parseInt(fields[1].trim());
                    double precioUnit = Double.parseDouble(fields[2].trim());

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

    private static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (esc) { cur.append(c); esc = false; }
            else if (c == '\\') { cur.append(c); esc = true; }
            else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
            else { cur.append(c); }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    @Override public boolean equals(Object o) { return (o instanceof Venta) && id.equals(((Venta)o).id); }
    @Override public int hashCode() { return Objects.hash(id); }
}