package Model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    public static final String PARA_LLEVAR = "PARA_LLEVAR";
    public static final String MESA = "MESA";

    private int codigoPedido;
    private String tipoPedido;
    private Integer numeroMesa;

    private List<Producto> productos;
    private List<Integer> cantidades;

    public Pedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (tipoPedido == null) {

            throw new IllegalArgumentException("No hay pedido");
        }

        if (!tipoPedido.equals(MESA) && !tipoPedido.equals(PARA_LLEVAR)) {

            throw new IllegalArgumentException("Tipo de pedido incorrecto");
        }

        if (tipoPedido.equals(MESA)) {
            if (numeroMesa == null || numeroMesa < 1 || numeroMesa > 5) {

                throw new IllegalArgumentException("Numero de mesa invalido");
            }
        } else {

            if (numeroMesa != null) {

                throw new IllegalArgumentException("Pedido para llevar no debe tener mesa");
            }
        }
        this.codigoPedido = codigoPedido;
        this.tipoPedido = tipoPedido;
        this.numeroMesa = numeroMesa;

        this.productos = new ArrayList<>();
        this.cantidades = new ArrayList<>();

    }

    public void agregarProducto(Producto producto, int cantidad) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // Buscar si el producto ya existe en la lista
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).equals(producto)) {
                // Si existe, sumar cantidad
                cantidades.set(i, cantidades.get(i) + cantidad);
                return;
            }
        }

        // Si no existe, agregarlo nuevo
        productos.add(producto);
        cantidades.add(cantidad);
    }

    public int getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(int codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public String getTipoPedido() {
        return tipoPedido;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public int getCantidadDeProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser null");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).equals(producto)) {
                return cantidades.get(i);
            }
        }

        return 0; // si no está en el pedido
    }

}
