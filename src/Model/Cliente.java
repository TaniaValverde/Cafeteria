package Model;

import java.util.Objects;

/**
 * Represents a cafeteria customer.
 *
 * <p>Project requirement: register customers as either "frequent" or "visitor".</p>
 */
public class Cliente {

    /**
     * Customer type according to the specification:
     * frequent customers and visitors.
     */
    public enum TipoCliente {
        FRECUENTE,
        VISITANTE
    }

    private String id;
    private String nombre;
    private String telefono;
    private TipoCliente tipo;

    /**
     * Creates a customer instance.
     *
     * @param id customer identifier (non-empty)
     * @param nombre customer name (non-empty)
     * @param telefono phone number (optional, can be empty)
     * @param tipo customer type (non-null)
     */
    public Cliente(String id, String nombre, String telefono, TipoCliente tipo) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
        setTipo(tipo);
    }

    /** @return customer id */
    public String getId() {
        return id;
    }

    /** @return customer name */
    public String getNombre() {
        return nombre;
    }

    /** @return customer phone number (may be empty) */
    public String getTelefono() {
        return telefono;
    }

    /** @return customer type */
    public TipoCliente getTipo() {
        return tipo;
    }

    /**
     * Updates the customer id.
     * @param id non-empty customer id
     */
    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer id cannot be empty.");
        }
        this.id = id.trim();
    }

    /**
     * Updates the customer name.
     * @param nombre non-empty customer name
     */
    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Updates the phone number.
     * @param telefono phone number (nullable; stored as empty if null)
     */
    public final void setTelefono(String telefono) {
        this.telefono = (telefono == null) ? "" : telefono.trim();
    }

    /**
     * Updates the customer type.
     * @param tipo non-null type
     */
    public final void setTipo(TipoCliente tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Customer type cannot be null.");
        }
        this.tipo = tipo;
    }

    /**
     * Serializes the customer into a simple CSV line for persistence.
     *
     * <p>Format: id,nombre,telefono,tipo</p>
     *
     * @return csv line
     */
    public String toCsv() {
        return String.join(",",
                escape(id),
                escape(nombre),
                escape(telefono),
                tipo.name());
    }

    /**
     * Creates a customer from a CSV line produced by {@link #toCsv()}.
     *
     * @param line csv line
     * @return parsed Cliente
     */
    public static Cliente fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV line.");
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Incomplete customer CSV: " + line);
        }

        return new Cliente(
                unescape(parts[0]),
                unescape(parts[1]),
                unescape(parts[2]),
                TipoCliente.valueOf(parts[3].trim())
        );
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",").replace("\\\\", "\\").trim();
    }

    @Override
    public String toString() {
        return "Cliente{id='" + id + "', nombre='" + nombre + "', telefono='" + telefono + "', tipo=" + tipo + "}";
    }

    /**
     * Customers are considered equal if they share the same id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
