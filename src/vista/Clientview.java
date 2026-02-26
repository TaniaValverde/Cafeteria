package vista;

import Controlador.ClientController;
import Model.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

/**
 * Swing view used to manage clients (create, update, delete, and list).
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class Clientview extends JFrame {

    private final ClientController clienteController;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JComboBox<Client.TipoCliente> cmbTipo;

    private JTable tabla;
    private DefaultTableModel modelo;

    // Para evitar abrir OSK muchas veces
    private boolean tecladoAbierto = false;

    /**
     * Creates the view and initializes its Swing components.
     * @param controller controller for clients
     */
    public Clientview(ClientController controller) {
        this.clienteController = controller;

        setTitle("Gestión de Clientes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Cerrar teclado al cerrar la ventana (X)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cerrarTecladoWindows();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                cerrarTecladoWindows();
            }
        });

        buildUI();
        recargarTabla();
    }

    // ======= Teclado flotante (Windows) =======

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private void abrirTecladoWindows() {
        if (!isWindows()) return;

        // Si ya se intentó abrir, no lo hagas cada vez que cambia el foco
        if (tecladoAbierto) return;

        try {
            // Teclado en pantalla clásico (flotante)
            new ProcessBuilder("cmd", "/c", "start", "", "osk").start();
            tecladoAbierto = true;
        } catch (Exception ignored) { }
    }

    private void cerrarTecladoWindows() {
        if (!isWindows()) return;

        try {
            // Cierra el proceso del teclado en pantalla
            new ProcessBuilder("cmd", "/c", "taskkill", "/IM", "osk.exe", "/F").start();
        } catch (Exception ignored) { }
        tecladoAbierto = false;
    }

    private void instalarTecladoEnCampos() {
        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                abrirTecladoWindows();
            }
        };

        txtId.addFocusListener(fa);
        txtNombre.addFocusListener(fa);
        txtTelefono.addFocusListener(fa);

        // Si quieres que al abrir el combo también salga el teclado (opcional):
        // cmbTipo.getEditor().getEditorComponent().addFocusListener(fa);
    }

    // =========================================

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        cmbTipo = new JComboBox<>(Client.TipoCliente.values());

        applyInputFilters();

        // Instala el evento que abre el teclado al entrar a los campos
        instalarTecladoEnCampos();

        form.add(new JLabel("ID:"));
        form.add(txtId);
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Teléfono:"));
        form.add(txtTelefono);
        form.add(new JLabel("Tipo:"));
        form.add(cmbTipo);

        add(form, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

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

    private void applyInputFilters() {
        ((AbstractDocument) txtId.getDocument())
                .setDocumentFilter(new RegexFilter("\\d*"));

        ((AbstractDocument) txtTelefono.getDocument())
                .setDocumentFilter(new RegexFilter("\\d*"));

        ((AbstractDocument) txtNombre.getDocument())
                .setDocumentFilter(new RegexFilter("[\\p{L} ]*"));
    }

    private void agregar() {
        try {
            Client c = new Client(
                    txtId.getText().trim(),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    (Client.TipoCliente) cmbTipo.getSelectedItem()
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
                    (Client.TipoCliente) cmbTipo.getSelectedItem()
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
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recargarTabla() {
        modelo.setRowCount(0);

        List<Client> lista = clienteController.listar();
        for (Client c : lista) {
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
        // Cierra el teclado al salir de esta ventana
        cerrarTecladoWindows();

        dispose();

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

    private static class RegexFilter extends DocumentFilter {

        private final String allowedRegex;

        RegexFilter(String allowedRegex) {
            this.allowedRegex = allowedRegex;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + string + current.substring(offset);

            if (next.matches(allowedRegex)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) text = "";

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + text + current.substring(offset + length);

            if (next.matches(allowedRegex)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}