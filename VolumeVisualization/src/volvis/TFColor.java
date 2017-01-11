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
    
    public static TFColor add(TFColor c1, TFColor c2) {
        float red, green, blue, alpha;
        red = c1.r + c2.r;
        green = c1.g + c2.g;
        blue = c1.b + c2.b;
        alpha = c1.a + c2.a;
        return new TFColor(red, green, blue, alpha);
    }
    
    public static TFColor multiply(TFColor c1, float scalar) {
        float red, green, blue, alpha;
        red = c1.r * scalar;
        green = c1.g * scalar;
        blue = c1.b * scalar;
        alpha = c1.a * scalar;
        return new TFColor(red, green, blue, alpha);
    }
    
    @Override
    public String toString() {
        String text = "(" + r + ", " + g + ", " + b + ", " + a + ")";
        return text;
    }
}
