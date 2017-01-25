/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

/**
 *
 * @author Frits
 */
public class DragState {
    private static boolean drag = false;
    
    public static boolean isDragged(){
        return drag;
    }
    
    public static void setDragged( boolean status ){
        drag = status;
    }
}
