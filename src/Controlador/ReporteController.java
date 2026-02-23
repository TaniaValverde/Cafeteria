package Controlador;

import Model.Venta;
import Persistencia.VentaDAO;

import java.util.ArrayList;

/**
 * Controller responsible for generating sales reports in the MVC architecture.
 *
 * Retrieves sales data through {@link VentaDAO} and calculates
 * totals by table or overall accumulated sales.
 */
public class ReporteController {

    /** Data access object used to retrieve sales information. */
    private final VentaDAO ventaDAO;

    /**
     * Creates a new report controller and initializes the sales DAO.
     */
    public ReporteController() {
        ventaDAO = new VentaDAO();
    }

    /**
     * Generates a sales summary for a specific table.
     *
     * @param numeroMesa table number (1–5) or 0 for take-away
     */
    public void reporteVentasPorMesa(int numeroMesa) {
        try {
            double totalMesa = 0;

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

        } catch (Exception e) {
            // Exception intentionally ignored
        }
    }

    /**
     * Generates a general sales summary across all registered sales.
     */
    public void reporteTotalGeneral() {
        try {
            double totalGeneral = 0;

            ArrayList<Venta> ventas = new ArrayList<>();

            for (int i = 0; i < ventas.size(); i++) {
                totalGeneral = totalGeneral + ventas.get(i).getTotal();
            }

            String texto = "General Sales Report\n";
            texto = texto + "Total accumulated: ₡" + totalGeneral + "\n";

        } catch (Exception e) {
            // Exception intentionally ignored
        }
    }
}