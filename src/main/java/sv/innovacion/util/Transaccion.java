/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sv.innovacion.model.Estado;
/**
 *
 * @author Frank
 */
public class Transaccion {
    
    private static int contador = 1;
    
    private int id;
    private double monto;
    private String metodo;
    private Estado estado;
    private LocalDateTime fecha;
    
    //Constructor
    public Transaccion(double monto, String metodo, Estado estado){
        this.id = contador++;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = LocalDateTime.now();
    }
    
    public int getId(){
        return id;
    }
    
    public double getMonto(){
        return monto;
    }
    
    public String getMetodo(){
        return metodo;
    }
    
    public Estado getEstado(){
        return estado;
    }
    
    public String getFecha(){
        return fecha.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );
    }
    
    @Override
    public String toString(){
        return "Transsaccion #" + id
                + " | Monto: $" + String.format("%.2f", monto)
                + " | Metodo: " + metodo
                + " | Estado: " + estado
                + " | Fecha: " + getFecha();
    }
}