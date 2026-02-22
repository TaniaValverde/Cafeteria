
package vista;

import Controlador.ClienteController;
import Model.Cliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class vistaDialogo extends JDialog {

    private final ClienteController clienteController;

    // ===== Resultado =====
    private Cliente clienteSeleccionado = null;

    // ===== Tab Frecuente =====
    private JTable tabla;
    private DefaultTableModel modelo;

    // ===== Tab Visitante =====
    private JTextField txtNombreV;
    private JTextField txtTelefonoV;

    public vistaDialogo (JFrame owner, ClienteController clienteController) {
        super(owner, "Cliente del pedido", true);
        this.clienteController = clienteController;

        setSize(820, 520);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        cargarTabla();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("  Selecciona un cliente frecuente o registra un visitante para este pedido");
        header.setBorder(new EmptyBorder(10, 10, 0, 10));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Frecuente", buildTabFrecuente());
        tabs.addTab("Visitante", buildTabVisitante());
        add(tabs, BorderLayout.CENTER);
    }

    // ==========================
    // ===== TAB FRECUENTE ======
    // ==========================
    private JComponent buildTabFrecuente() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Tipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // doble click = seleccionar
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) seleccionarFrecuente();
            }
        });

        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnSeleccionar = new JButton("Seleccionar");
        JButton btnVisitante = new JButton("Usar como visitante (sin cliente)");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnVisitante);
        bottom.add(btnSeleccionar);
        bottom.add(btnCancelar);

        btnSeleccionar.addActionListener(e -> seleccionarFrecuente());

        // visitante: devuelve null (pedido sin cliente)
        btnVisitante.addActionListener(e -> {
            clienteSeleccionado = null;
            dispose();
        });

        // cancelar: cierra sin seleccionar
        btnCancelar.addActionListener(e -> {
            clienteSeleccionado = null;
            dispose();
        });

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void cargarTabla() {
        modelo.setRowCount(0);

        List<Cliente> lista = clienteController.listar();
        for (Cliente c : lista) {
            // Si quieres mostrar SOLO FRECUENTES, descomenta:
            // if (c.getTipo() != Cliente.TipoCliente.FRECUENTE) continue;

            modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getTipo()
            });
        }
    }

    private void seleccionarFrecuente() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.");
            return;
        }

        String id = modelo.getValueAt(row, 0).toString();

        // Recuperar el objeto real desde el controller
        for (Cliente c : clienteController.listar()) {
            if (c.getId().equals(id)) {
                clienteSeleccionado = c;
                break;
            }
        }

        dispose();
    }

    // ==========================
    // ===== TAB VISITANTE ======
    // ==========================
    private JComponent buildTabVisitante() {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("<html><b>Visitante</b>: ingresa nombre y (opcional) teléfono. " +
                "Se registrará para que salga en la factura.</html>");
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(info);
        p.add(Box.createRigidArea(new Dimension(0, 14)));

        txtNombreV = new JTextField();
        txtTelefonoV = new JTextField();

        // Filtros (parecidos a tu vistaCliente)
        ((AbstractDocument) txtTelefonoV.getDocument()).setDocumentFilter(new RegexFilter("\\d*"));
        ((AbstractDocument) txtNombreV.getDocument()).setDocumentFilter(new RegexFilter("[\\p{L} ]*"));

        p.add(labelField("Nombre del visitante:", txtNombreV));
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        p.add(labelField("Teléfono (opcional):", txtTelefonoV));
        p.add(Box.createRigidArea(new Dimension(0, 18)));

        JButton btnUsar = new JButton("Usar y registrar visitante");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(btnUsar);
        row.add(btnCancelar);

        btnUsar.addActionListener(e -> usarVisitante());
        btnCancelar.addActionListener(e -> {
            clienteSeleccionado = null;
            dispose();
        });

        p.add(row);

        return p;
    }

    private JPanel labelField(String label, JTextField field) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrap.add(l);
        wrap.add(Box.createRigidArea(new Dimension(0, 6)));
        wrap.add(field);
        return wrap;
    }

    private void usarVisitante() {
        String nombre = txtNombreV.getText().trim();
        String tel = txtTelefonoV.getText().trim();

        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del visitante.");
            return;
        }

        // Tu vistaCliente exige ID numérico, por eso usamos timestamp
        String id = String.valueOf(System.currentTimeMillis());

        Cliente nuevo = new Cliente(
                id,
                nombre,
                tel,
                Cliente.TipoCliente.VISITANTE
        );

        // Intentar guardar el visitante (para que exista en el sistema)
        try {
            clienteController.registrar(nuevo);
        } catch (IOException ex) {
            // No bloquea el pedido: igual se usa para factura
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el visitante, pero se usará en la factura.\n" + ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clienteSeleccionado = nuevo;
        dispose();
    }

    // ===== Getter para VistaPedido =====
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    // =========================
    // ====== DOCUMENT FILTER ===
    // =========================
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
