package Controlador;

import Model.Mesa;
import Model.Pedido;

public class MesaController {

    private final Mesa[] mesas;

    public MesaController() {
        mesas = new Mesa[5];
        mesas[0] = new Mesa(1);
        mesas[1] = new Mesa(2);
        mesas[2] = new Mesa(3);
        mesas[3] = new Mesa(4);
        mesas[4] = new Mesa(5);

    }

    public Mesa obtenerMesa(int numero) {

        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException("Numero Incorrecto de mesa.");
        }

        return mesas[numero - 1];

    }

    public boolean estaLibre(int numeroMesa) {
        Mesa mesa = obtenerMesa(numeroMesa);

        return mesa.estaLibre();

    }

    public void asignarPedido(int numeroMesa, Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no existe");
        }
        Mesa mesa = obtenerMesa(numeroMesa);

        mesa.asignarPedido(pedido);
    }

    public void liberarMesa(int numeroMesa) {

        Mesa mesa = obtenerMesa(numeroMesa);

        mesa.liberar();

    }

}
