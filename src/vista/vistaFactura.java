/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

/**
 * Vista encargada de mostrar la factura generada.
 * Simula la impresión de una factura en una impresora térmica.
 * Cumple con el patrón MVC: solo presenta información.
 */
public class vistaFactura extends JFrame {

    private JTextArea areaFactura;

    /**
     * Constructor de la vista de factura.
     */
    public vistaFactura() {
        setTitle("Factura - Cafetería UCR Sede del Sur");
        setSize(350, 500); // Formato tipo impresora térmica
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setLineWrap(true);
        areaFactura.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(areaFactura);
        add(scroll);
    }

    /**
     * Muestra el contenido de la factura en pantalla.
     *
     * @param textoFactura Texto generado por Factura.generarImpresion()
     */
    public void mostrarFactura(String textoFactura) {
        areaFactura.setText(textoFactura);
        setVisible(true);
    }

    /**
     * Muestra un mensaje simple al usuario.
     *
     * @param mensaje Mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}

