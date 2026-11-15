/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.model;
import sv.innovacion.util.PagoBase;
/**
 *
 * @author Frank
 */
public class Tarjeta extends PagoBase{
    
    private String numeroTarjeta;
    private String tipo;
    
    public Tarjeta(String numeroTarjeta, String tipo){
        
        this.numeroTarjeta = numeroTarjeta;
        this.tipo = tipo;
    }
    
    @Override
    public String getNombre(){
        return "Tarjeta" + tipo;
    }
    
    @Override
    public boolean ejecutarCobro(double monto){
        if(numeroTarjeta == null || !numeroTarjeta.matches("\\d{16}")){
            System.out.println("Erro: Numero de Tarjeta deber tener 16 digitos");
            return false;
        }
        System.out.println("Procesando pago con tarjeta...");
        System.out.println("Pago autorizado en esta simulacion.");
        return true;
    }
}