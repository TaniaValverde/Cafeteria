package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a cafeteria customer in the Model layer (MVC). Supports the
 * project requirement of registering customers as frequent or visitor and
 * provides CSV serialization for file persistence.
 */
public class Client {

    /**
     * Customer classification required by the system.
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
     * Creates a new customer with basic validation.
     *
     * @param id customer identifier (non-null, non-blank)
     * @param nombre customer name (non-null, non-blank)
     * @param telefono phone number (optional; null becomes empty)
     * @param tipo customer type (non-null)
     * @throws IllegalArgumentException if id/name is blank or type is null
     */
    public Client(String id, String nombre, String telefono, TipoCliente tipo) {
        setId(id);
        setNombre(nombre);
        setTelefono(telefono);
        setTipo(tipo);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

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
     * Serializes this customer into one CSV line for persistence. Format:
     * id,nombre,telefono,tipo (commas and backslashes are escaped).
     *
     * @return CSV representation of the customer
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
     * @param line CSV line
     * @return parsed customer
     * @throws IllegalArgumentException if the line is null/blank or invalid
     */
    public static Client fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV line.");
        }

        try {
            String[] parts = splitCsvEscaped(line);
            if (parts.length < 4) {
                throw new IllegalArgumentException("Incomplete customer CSV: " + line);
            }

            return new Client(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    TipoCliente.valueOf(parts[3].trim())
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid customer CSV format.", ex);
        }
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",").replace("\\\\", "\\").trim();
    }

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
     * Equality is based only on the customer id.
     *
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client cliente = (Client) o;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
