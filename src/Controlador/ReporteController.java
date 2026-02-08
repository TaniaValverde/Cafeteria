package Controlador;

import Model.Venta;
import Persistencia.VentaDAO;
// import vista.VistaReporte;

import java.util.ArrayList;

/**
 * Controlador encargado de generar reportes de ventas.
 * Reportes por mesa y totales generales.
 */
public class ReporteController {

    private final VentaDAO ventaDAO;
    // private VistaReporte vista;

    /**
     * Constructor del controlador de reportes.
     */
    public ReporteController() {
        ventaDAO = new VentaDAO();
        // vista = new VistaReporte();
    }

    /**
     * Genera reporte de ventas por número de mesa.
     *
     * @param numeroMesa mesa (1-5) o 0 para llevar
     */
    public void reporteVentasPorMesa(int numeroMesa) {
        try {
            double totalMesa = 0;

            // ERROR ESPERADO: VentaDAO debe implementar getVentas()
            ArrayList<Venta> ventas = new ArrayList<>();

            for (int i = 0; i < ventas.size(); i++) {
                Venta v = ventas.get(i);
                if (v.getMesaNumero() == numeroMesa) {
                    totalMesa = totalMesa + v.getTotal();
                }
            }

            String texto = "Reporte de ventas\n";
            texto = texto + "Mesa: " + numeroMesa + "\n";
            texto = texto + "Total vendido: ₡" + totalMesa + "\n";

            // MÉTODO ESPERADO EN VistaReporte
            // vista.mostrarReporte(texto);

        } catch (Exception e) {
            // vista.mostrarMensaje("Error al generar reporte");
        }
    }

    /**
     * Genera reporte total de todas las ventas registradas.
     */
    public void reporteTotalGeneral() {
        try {
            double totalGeneral = 0;

            // ERROR ESPERADO: VentaDAO debe implementar getVentas()
            ArrayList<Venta> ventas = new ArrayList<>();

            for (int i = 0; i < ventas.size(); i++) {
                totalGeneral = totalGeneral + ventas.get(i).getTotal();
            }

            String texto = "Reporte General de Ventas\n";
            texto = texto + "Total acumulado: ₡" + totalGeneral + "\n";

            // vista.mostrarReporte(texto);

        } catch (Exception e) {
            // vista.mostrarMensaje("Error al generar reporte general");
        }
    }
}
