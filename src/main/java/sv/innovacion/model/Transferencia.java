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
public class Transferencia extends PagoBase{
    
    private String referencia;
    
    public Transferencia(String referencia){
        this.referencia = referencia;
    }
    
    @Override
    public String getNombre(){
        return "Transferencia bancaria";
    }
    
    @Override
    public boolean ejecutarCobro(double monto){
        if(referencia == null || referencia.trim().isEmpty()){
            System.out.println("Debe ingresar una referencia");
            return false;
        }
        
        System.out.println("Transferencia simulada con referencia: " + referencia);
        return true;
    }
}