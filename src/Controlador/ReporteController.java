package Controlador;

import Model.Venta;
import Persistencia.VentaDAO;
// import vista.VistaReporte;

import java.util.ArrayList;

/**
 * Controller responsible for generating sales reports.
 * <p>
 * This class provides reporting functionalities such as
 * sales by table and total accumulated sales.
 * </p>
 *
 * It acts as an intermediary between the persistence layer
 * and the reporting view 
 *
 * @author Project Team
 */
public class ReporteController {

    /**
     * Data access object used to retrieve sales information.
     */
    private final VentaDAO ventaDAO;

    // private VistaReporte vista;

    /**
     * Creates a new instance of {@code ReporteController}.
     * <p>
     * Initializes the sales data access object.
     * </p>
     */
    public ReporteController() {
        ventaDAO = new VentaDAO();
        // vista = new VistaReporte();
    }

    /**
     * Generates a sales report for a specific table.
     * <p>
     * The report calculates the total amount sold for the given
     * table number. A value of {@code 0} represents takeaway orders.
     * </p>
     *
     * @param numeroMesa Table number (1–5) or {@code 0} for takeaway
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
           
        }
    }

    /**
     * Generates a general sales report.
     * <p>
     * This report calculates the total accumulated sales
     * across all registered sales.
     * </p>
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
            
        }
    }
}