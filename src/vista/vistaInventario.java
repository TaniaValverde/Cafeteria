package vista;

import Controlador.ProductoController;
import Model.Producto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class vistaInventario extends JFrame {

    private final ProductoController productoController;

    private final JTextField txtBuscar;
    private final JTable tabla;
    private final DefaultTableModel modelo;

    private final JButton btnRecargar;
    private final JButton btnGuardar;
    private final JButton btnSumar;
    private final JButton btnRestar;

    public vistaInventario(ProductoController productoController) {
        this.productoController = productoController;

        setTitle("Inventario - Control de Stock");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);

        Font f = new Font("SansSerif", Font.PLAIN, 14);

        // ===== Top Bar =====
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Inventario (Stock por Producto)");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JLabel lblBuscar = new JLabel("Buscar (código o nombre):");
        lblBuscar.setFont(f);

        txtBuscar = new JTextField(22);
        txtBuscar.setFont(f);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(f);
        btnBuscar.addActionListener(e -> filtrarTabla());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(f);
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTabla();
        });

        buscador.add(lblBuscar);
        buscador.add(txtBuscar);
        buscador.add(btnBuscar);
        buscador.add(btnLimpiar);

        top.add(titulo, BorderLayout.WEST);
        top.add(buscador, BorderLayout.EAST);

        // ===== Tabla =====
        String[] cols = {"Código", "Nombre", "Categoría", "Precio", "Stock"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Solo Stock editable
                return col == 4;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setFont(f);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);

        // ===== Bottom Bar =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBorder(new EmptyBorder(8, 12, 12, 12));

        btnSumar = new JButton("+ Stock");
        btnSumar.setFont(f);
        btnSumar.addActionListener(e -> cambiarStockSeleccionado(+1));

        btnRestar = new JButton("- Stock");
        btnRestar.setFont(f);
        btnRestar.addActionListener(e -> cambiarStockSeleccionado(-1));

        btnRecargar = new JButton("Recargar");
        btnRecargar.setFont(f);
        btnRecargar.addActionListener(e -> cargarTabla());

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnGuardar.addActionListener(e -> guardarCambios());
        JButton btnMenu = new JButton("Menú Principal");
        btnMenu.setFont(f);
        btnMenu.addActionListener(e -> volverAlMenu());

        bottom.add(btnSumar);
        bottom.add(btnRestar);
        bottom.add(btnRecargar);
        bottom.add(btnGuardar);
        bottom.add(btnMenu);

        // ===== Layout =====
        JPanel root = new JPanel(new BorderLayout());
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        // Inicial
        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);

        List<Producto> productos = productoController.listar();
        for (Producto p : productos) {
            Object[] row = {
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock()
            };
            modelo.addRow(row);
        }
    }

    private void filtrarTabla() {
        String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();

        if (q.isEmpty()) {
            cargarTabla();
            return;
        }

        modelo.setRowCount(0);
        List<Producto> productos = productoController.listar();

        for (Producto p : productos) {
            String cod = p.getCodigo().toLowerCase();
            String nom = p.getNombre().toLowerCase();

            if (cod.contains(q) || nom.contains(q)) {
                Object[] row = {
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecio(),
                    p.getStock()
                };
                modelo.addRow(row);
            }
        }
    }

    private void cambiarStockSeleccionado(int delta) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto en la tabla.",
                    "Inventario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object val = modelo.getValueAt(row, 4);
        int stockActual;

        try {
            stockActual = Integer.parseInt(String.valueOf(val));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El stock actual no es válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int nuevo = stockActual + delta;
        if (nuevo < 0) {
            JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.",
                    "Inventario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modelo.setValueAt(nuevo, row, 4);
    }

    private void guardarCambios() {
        // Guardamos fila por fila usando el método modificar del controller
        // modificar(codigo, categoria, precio, stock)
        int filas = modelo.getRowCount();
        if (filas == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos para guardar.",
                    "Inventario", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            for (int i = 0; i < filas; i++) {
                String codigo = String.valueOf(modelo.getValueAt(i, 0)).trim();
                String categoria = String.valueOf(modelo.getValueAt(i, 2)).trim();

                double precio;
                int stock;

                try {
                    precio = Double.parseDouble(String.valueOf(modelo.getValueAt(i, 3)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Precio inválido en producto " + codigo);
                }

                try {
                    stock = Integer.parseInt(String.valueOf(modelo.getValueAt(i, 4)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Stock inválido en producto " + codigo);
                }

                if (stock < 0) {
                    throw new IllegalArgumentException("Stock negativo en producto " + codigo);
                }

                // Esto guarda y persiste
                productoController.modificar(codigo, categoria, precio, stock);
            }

            JOptionPane.showMessageDialog(this, "Inventario guardado correctamente ✅",
                    "Inventario", JOptionPane.INFORMATION_MESSAGE);

            // Refrescar por si hay ordenamiento u otros cambios
            cargarTabla();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error guardando inventario:\n" + ex.getMessage(),
                    "Error IO", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void volverAlMenu() {
    dispose();

    SwingUtilities.invokeLater(() -> {
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f instanceof JFrame && f.isVisible()
                    && f.getTitle() != null
                    && f.getTitle().contains("Cafetería UCR")) {
                f.toFront();
                f.requestFocus();
                break;
            }
        }
    });
}

}
