package vista;

import Controlador.ClienteController;
import Model.Cliente;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class vistaCliente extends JFrame {

    private final ClienteController clienteController;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JComboBox<Cliente.TipoCliente> cmbTipo;

    private JTable tabla;
    private DefaultTableModel modelo;

    public vistaCliente(ClienteController controller) {
        this.clienteController = controller;

        setTitle("Gestión de Clientes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        recargarTabla();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ==== FORM ====
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        cmbTipo = new JComboBox<>(Cliente.TipoCliente.values());

        form.add(new JLabel("ID:"));
        form.add(txtId);
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Teléfono:"));
        form.add(txtTelefono);
        form.add(new JLabel("Tipo:"));
        form.add(cmbTipo);

        add(form, BorderLayout.NORTH);

        // ==== TABLE ====
        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Tipo"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ==== BUTTONS ====
        JPanel botones = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnMenu = new JButton("Menú Principal");

        botones.add(btnAgregar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnMenu);

        add(botones, BorderLayout.SOUTH);     

        // ==== EVENTS ====
        btnAgregar.addActionListener(e -> agregar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnMenu.addActionListener(e -> volverAlMenu());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                txtId.setText(modelo.getValueAt(row, 0).toString());
                txtNombre.setText(modelo.getValueAt(row, 1).toString());
                txtTelefono.setText(modelo.getValueAt(row, 2).toString());
                cmbTipo.setSelectedItem(modelo.getValueAt(row, 3));
                txtId.setEnabled(false);
            }
        });
    }

    private void agregar() {
        try {
            Cliente c = new Cliente(
                    txtId.getText().trim(),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (Cliente.TipoCliente) cmbTipo.getSelectedItem()
            );

            clienteController.registrar(c);
            recargarTabla();
            limpiar();

            JOptionPane.showMessageDialog(this, "Cliente registrado ✅");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificar() {
        try {
            clienteController.modificar(
                    txtId.getText().trim(),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (Cliente.TipoCliente) cmbTipo.getSelectedItem()
            );

            recargarTabla();
            limpiar();

            JOptionPane.showMessageDialog(this, "Cliente actualizado ✅");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        try {
            clienteController.eliminar(txtId.getText().trim());
            recargarTabla();
            limpiar();

            JOptionPane.showMessageDialog(this, "Cliente eliminado ✅");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recargarTabla() {
        modelo.setRowCount(0);

        List<Cliente> lista = clienteController.listar();
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                c.getId(),
                c.getNombre(),
                c.getTelefono(),
                c.getTipo()
            });
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        cmbTipo.setSelectedIndex(0);
        txtId.setEnabled(true);
        tabla.clearSelection();
    }
    private void volverAlMenu() {
    // Cierra esta ventana
    dispose();

    // Trae al frente el menú principal (si está abierto)
    SwingUtilities.invokeLater(() -> {
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f instanceof JFrame && f.isVisible()
                    && f.getTitle() != null
                    && f.getTitle().contains("Cafetería UCR Sede del Sur")) {
                f.toFront();
                f.requestFocus();
                break;
            }
        }
    });
}

}
