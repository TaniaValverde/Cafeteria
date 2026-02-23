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
import java.io.IOException;
import java.util.List;

/**
 * Modal dialog used to select an existing client for an order or sale.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class Dialogview extends JDialog {

    private final ClientController clienteController;

    private Client clienteSeleccionado = null;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JTextField txtNombreV;
    private JTextField txtTelefonoV;

    /**
     * Creates the view and initializes its Swing components.
     */

    public Dialogview(JFrame owner, ClientController clienteController) {
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

    private JComponent buildTabFrecuente() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarFrecuente();
                }
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

        btnVisitante.addActionListener(e -> {
            clienteSeleccionado = null;
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            clienteSeleccionado = null;
            dispose();
        });

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void cargarTabla() {
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

    private void seleccionarFrecuente() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.");
            return;
        }

        String id = modelo.getValueAt(row, 0).toString();

        for (Client c : clienteController.listar()) {
            if (c.getId().equals(id)) {
                clienteSeleccionado = c;
                break;
            }
        }

        dispose();
    }

    private JComponent buildTabVisitante() {
        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("<html><b>Visitante</b>: ingresa nombre y (opcional) teléfono. "
                + "Se registrará para que salga en la factura.</html>");
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(info);
        p.add(Box.createRigidArea(new Dimension(0, 14)));

        txtNombreV = new JTextField();
        txtTelefonoV = new JTextField();

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

        String id = String.valueOf(System.currentTimeMillis());

        Client nuevo = new Client(
                id,
                nombre,
                tel,
                Client.TipoCliente.VISITANTE
        );

        try {
            clienteController.registrar(nuevo);
        } catch (IOException ex) {
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

    public Client getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    private static class RegexFilter extends DocumentFilter {

        private final String allowedRegex;

        RegexFilter(String allowedRegex) {
            this.allowedRegex = allowedRegex;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + string + current.substring(offset);

            if (next.matches(allowedRegex)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) {
                text = "";
            }

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + text + current.substring(offset + length);

            if (next.matches(allowedRegex)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
