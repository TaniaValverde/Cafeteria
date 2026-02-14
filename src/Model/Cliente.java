package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a cafeteria customer.
 *
 * <p>Project requirement: register customers as either "frequent" or "visitor".</p>
 */
public class Cliente {

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
     * @param telefono phone number (nullable → becomes empty string)
     * @param tipo customer type (non-null)
     */
    public Cliente(String id, String nombre, String telefono, TipoCliente tipo) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
        setTipo(tipo);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public TipoCliente getTipo() { return tipo; }

    public final void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer id cannot be empty.");
        }
        this.id = id.trim();
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Phone is optional. If null → becomes empty string.
     */
    public final void setTelefono(String telefono) {
        this.telefono = (telefono == null) ? "" : telefono.trim();
    }

    public final void setTipo(TipoCliente tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Customer type cannot be null.");
        }
        this.tipo = tipo;
    }

    /**
     * Serializes the customer into a CSV line.
     * Format: id,nombre,telefono,tipo
     *
     * <p>Escapes:
     * <ul>
     *   <li>Backslash "\" is escaped as "\\\\"</li>
     *   <li>Comma "," is escaped as "\\,"</li>
     * </ul>
     */
    public String toCsv() {
        return String.join(",",
                escape(id),
                escape(nombre),
                escape(telefono),
                tipo.name());
    }

    /**
     * Parses a CSV line produced by {@link #toCsv()}.
     *
     * <p>This method supports escaped commas (\,) and escaped backslashes (\\).</p>
     *
     * @param line csv line
     * @return parsed Cliente
     */
    public static Cliente fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV line.");
        }

        try {
            String[] parts = splitCsvEscaped(line);
            if (parts.length < 4) {
                throw new IllegalArgumentException("Incomplete customer CSV: " + line);
            }

            return new Cliente(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    TipoCliente.valueOf(parts[3].trim())
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid customer CSV format.", ex);
        }
    }

    /**
     * Escapes backslashes and commas so the CSV stays one line.
     */
    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,");
    }

    /**
     * Unescapes commas and backslashes (reverse of {@link #escapeescape(String)}).
     */
    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",").replace("\\\\", "\\").trim();
    }

    /**
     * Splits a CSV line where commas can be escaped with "\,".
     * Keeps the backslashes so {@link #unescape(String)} can process them later.
     */
    private static String[] splitCsvEscaped(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escaping) {
                cur.append(c);
                escaping = false;
            } else if (c == '\\') {
                cur.append(c);
                escaping = true;
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
