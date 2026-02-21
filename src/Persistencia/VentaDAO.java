package Persistencia;

import Model.Producto;
import Model.Venta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private static final String RUTA_VENTAS_POR_DEFECTO = "data/ventas.txt";
    private final Path archivo;

    public VentaDAO() {
        this(RUTA_VENTAS_POR_DEFECTO);
    }

    public VentaDAO(String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo);
    }

    public List<Venta> cargar() throws IOException {
        asegurarDirectorio();

        List<Venta> ventas = new ArrayList<>();
        if (!Files.exists(archivo)) return ventas;

        try (BufferedReader br = Files.newBufferedReader(archivo)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    ventas.add(Venta.fromCsv(line));
                } catch (Exception ex) {
                    System.out.println("Linea de venta invalida (se ignora): " + line);
                }
            }
        }
        return ventas;
    }

    public void guardar(List<Venta> ventas) throws IOException {
        asegurarDirectorio();
        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (Venta v : ventas) {
                bw.write(v.toCsv());
                bw.newLine();
            }
        }
    }

    public void guardarVenta(Venta venta) throws IOException {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser null.");
        asegurarDirectorio();

        try (BufferedWriter bw = Files.newBufferedWriter(
                archivo,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            bw.write(venta.toCsv());
            bw.newLine();
        }
    }

    public void actualizarVenta(Venta ventaActualizada) throws IOException {
        if (ventaActualizada == null) throw new IllegalArgumentException("Venta no puede ser null.");

        List<Venta> ventas = cargar();
        boolean encontrada = false;

        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId().equals(ventaActualizada.getId())) {
                ventas.set(i, ventaActualizada);
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            throw new IllegalStateException("No se encontró la venta con ID: " + ventaActualizada.getId());
        }

        guardar(ventas);
    }

    public void eliminarPendientePorCodigoPedido(int codigoPedido) throws IOException {
    List<Venta> ventas = cargar();
    boolean cambio = false;

    for (int i = ventas.size() - 1; i >= 0; i--) {
        Venta v = ventas.get(i);
        if ("PENDIENTE".equalsIgnoreCase(v.getEstado())
                && v.getCodigoPedido() == codigoPedido) {
            ventas.remove(i);
            cambio = true;
        }
    }

    if (cambio) guardar(ventas);
}
   
    public List<Venta> listarPendientes() throws IOException {
        List<Venta> ventas = cargar();
        List<Venta> pendientes = new ArrayList<>();

        for (Venta v : ventas) {
            if ("PENDIENTE".equalsIgnoreCase(v.getEstado())) {
                pendientes.add(v);
            }
        }
        return pendientes;
    }

    public void registrarVentaConStock(Venta venta, ProductoDAO productoDAO) throws IOException {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser null.");
        if (productoDAO == null) throw new IllegalArgumentException("ProductoDAO no puede ser null.");

        List<Producto> productos = productoDAO.cargar();

        for (Venta.LineaVenta lv : venta.getLineas()) {
            String codigo = lv.getProducto().getCodigo();
            int cantidad = lv.getCantidad();

            Producto p = buscarProductoPorCodigo(productos, codigo);
            if (p == null) throw new IllegalStateException("No existe el producto con codigo: " + codigo);

            p.descontarStock(cantidad);
        }

        productoDAO.guardar(productos);
        guardarVenta(venta);
    }

    public ArrayList<Venta> getVentas() throws IOException {
        return new ArrayList<>(cargar());
    }

    private Producto buscarProductoPorCodigo(List<Producto> productos, String codigo) {
        if (codigo == null) return null;
        for (Producto p : productos) {
            if (codigo.equals(p.getCodigo())) return p;
        }
        return null;
    }

    private void asegurarDirectorio() throws IOException {
        Path parent = archivo.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}