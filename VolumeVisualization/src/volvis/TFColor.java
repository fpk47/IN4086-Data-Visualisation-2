/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

/**
 *
 * @author michel
 */
public class TFColor {
    public float r, g, b, a;

    public TFColor() {
        r = g = b = a = 1.0f;
    }
    
    public TFColor(float red, float green, float blue, float alpha) {
        r = red;
        g = green;
        b = blue;
        a = alpha;
    }
    
    @Override
    public String toString() {
        String text = "(" + r + ", " + g + ", " + b + ", " + a + ")";
        return text;
    }
}
