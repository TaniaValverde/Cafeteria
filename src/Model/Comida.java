/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.Serializable;

// COMENTARIO PARA EL COMPAÑERO QUE HAGA PRODUCTO:
// Esta clase hereda de Producto. Para que compile sin errores, 
// la clase Producto debe tener un constructor con estos parámetros:
// public Producto(String codigo, String nombre, String categoria, double precio, int stock)
// Además, debe tener getPrecio() y toString() (o al menos no dar error al llamar super.toString()).
// Cuando termines Producto, solo borra este comentario y prueba.

public class Comida extends Producto implements Serializable {

    private int calorias;

    public Comida(String codigo, String nombre, String categoria, double precio, int stock, int calorias) {
        super(codigo, nombre, categoria, precio, stock);
        this.calorias = calorias;
    }

    public Comida(String codigo, String nombre, String categoria, double precio, int stock) {
        this(codigo, nombre, categoria, precio, stock, 0);
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    @Override
    public String toString() {
        return super.toString() + " - Calorías: " + calorias;
    }
}