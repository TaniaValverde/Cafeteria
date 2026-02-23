package Controlador;

import Model.Sale;
import Persistencia.SaleDAO;

import java.util.ArrayList;

/**
 * Controller responsible for generating sales reports in the MVC architecture.
 *
 * Retrieves sales data through {@link SaleDAO} and calculates
 * totals by table or overall accumulated sales.
 */
public class ReportController {

    /** Data access object used to retrieve sales information. */
    private final SaleDAO ventaDAO;

    /**
     * Creates a new report controller and initializes the sales DAO.
     */
    public ReportController() {
        ventaDAO = new SaleDAO();
    }

    /**
     * Generates a sales summary for a specific table.
     *
     * @param numeroMesa table number (1–5) or 0 for take-away
     */
    public void reporteVentasPorMesa(int numeroMesa) {
        try {
            double totalMesa = 0;

            ArrayList<Sale> ventas = new ArrayList<>();

            for (int i = 0; i < ventas.size(); i++) {
                Sale v = ventas.get(i);
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

            ArrayList<Sale> ventas = new ArrayList<>();

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