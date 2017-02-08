package volvis;

import java.util.logging.Level;
import java.util.logging.Logger;
import volume.Volume;

/**
 *
 * @author Frits
 */
public class CustomThread extends Thread {
    private Volume volume;
    private RaycastRenderer raycastRenderer;
            
    private float[] viewMatrix;
    private float[] viewVec;
    private float[] uVec;
    private float[] vVec;
    
    private float[] pixelCoord = new float[3];
    private float[] entryPoint = new float[3];
    private float[] exitPoint = new float[3];

    private int increment;
    private float sampleStep;
    
    private int imageCenter;
    public int startX;
    public int startY;
    public int sizeX;
    public int sizeY;

    private int[][] rgb;
    
    private float[] temp = new float[3];
    private float[] delta = new float[3];
    private boolean wait = true;
    
    private float[] temp1 = new float[3];
    private float[] temp2 = new float[3];
    private float[] temp3 = new float[3];
    
    private boolean finished;

   public CustomThread( String name, Volume volume, RaycastRenderer raycastRenderer, float[] viewMatrix, float[] viewVec, float[] uVec, float[] vVec, int increment, float sampleStep, int imageCenter, int startX, int startY, int sizeX, int sizeY ) {
       super(name);
       this.volume = volume;
       this.raycastRenderer = raycastRenderer;
       
       this.viewMatrix = viewMatrix;
       this.viewVec = viewVec;
       this.uVec = uVec;
       this.vVec = vVec;
       
       this.increment = increment;
       this.sampleStep = sampleStep;
       
       this.imageCenter = imageCenter;
       this.startX = startX;
       this.startY = startY;
       this.sizeX = sizeX;
       this.sizeY = sizeY;
       
       rgb = new int[sizeX][sizeY];
       this.wait = true;
       
       finished = false;   
   }
   
   public void setVariables( Volume volume, RaycastRenderer raycastRenderer, float[] viewMatrix, float[] viewVec, float[] uVec, float[] vVec, int increment, float sampleStep, int imageCenter, int startX, int startY, int sizeX, int sizeY ){
       this.volume = volume;
       this.raycastRenderer = raycastRenderer;
       
       this.viewMatrix = viewMatrix;
       this.viewVec = viewVec;
       this.uVec = uVec;
       this.vVec = vVec;
       
       this.increment = increment;
       this.sampleStep = sampleStep;
       
       this.imageCenter = imageCenter;
       this.startX = startX;
       this.startY = startY;
       this.sizeX = sizeX;
       this.sizeY = sizeY;
       
       rgb = new int[sizeX][sizeY];
       this.wait = true;
       
       finished = false;   
   }
   
   public void go(){
       wait = false;
   }
   
   public boolean isRunning(){
       return !finished;
   }
   
    public int[][] getData() {
        return rgb;
    }
   
   public void run() {
       while ( true ){
           while ( wait )
           {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CustomThread.class.getName()).log(Level.SEVERE, null, ex);
                }
           }
           
          
          
       temp[0] = volume.getDimX() / 2.0f - viewVec[0] * imageCenter + uVec[0] * (startX - imageCenter);
       temp[1] = volume.getDimY() / 2.0f - viewVec[1] * imageCenter + uVec[1] * (startX - imageCenter);
       temp[2] = volume.getDimZ() / 2.0f - viewVec[2] * imageCenter + uVec[2] * (startX - imageCenter);
       
       delta[0] = uVec[0] * increment;
       delta[1] = uVec[1] * increment;
       delta[2] = uVec[2] * increment;
       
       
       float i0 = startX - imageCenter;
       float i1 = startX + sizeX - imageCenter;
       
        TFColor color = new TFColor(0,0,0,1);
        TFColor background = new TFColor(0,0,0,1);
    
        for (float j = startY - imageCenter; j < startY + sizeY - imageCenter; j += increment) {
             pixelCoord[0] = temp[0] + vVec[0] * (j);
             pixelCoord[1] = temp[1] + vVec[1] * (j);
             pixelCoord[2] = temp[2] + vVec[2] * (j);
             
            for (float i = i0; i < i1; i += increment) {
                // compute starting points of rays in a plane shifted backwards to a position behind the data set
                pixelCoord[0] += delta[0];
                pixelCoord[1] += delta[1];
                pixelCoord[2] += delta[2]; 
                
                raycastRenderer.computeEntryAndExit(pixelCoord, viewVec, entryPoint, exitPoint, temp1, temp2, temp3);
                          
                if ((entryPoint[0] > -1.0) && (exitPoint[0] > -1.0)) {
                    //System.out.println("Entry: " + entryPoint[0] + " " + entryPoint[1] + " " + entryPoint[2]);
                    //System.out.println("Exit: " + exitPoint[0] + " " + exitPoint[1] + " " + exitPoint[2]);
                    int pixelColor = 0;

                   if(raycastRenderer.mipMode) 
                        pixelColor = raycastRenderer.traceRayMIP(entryPoint,exitPoint,viewVec,sampleStep);

                   if(raycastRenderer.compositingMode){
                       pixelColor = raycastRenderer.traceRayComposite(entryPoint, exitPoint, viewVec, sampleStep, color, background);
                   }

        
                   int x0 = (int) i + imageCenter - startX;
                   int x1 = (int) i + imageCenter + increment - startX;
                   int x2 = sizeX;
                   
                   int y0 = (int) j + imageCenter - startY;
                   int y1 = (int) j + imageCenter + increment - startY;
                   int y2 = sizeY;
                   
                   for ( int x = x0; ( x < x1 ) && ( x < x2 ); x++ ){
                       for ( int y = y0; ( y < y1 ) && ( y < y2) ; y++ ){
                           rgb[x][y] = pixelColor;
                       }
                   }
                   
                }
            }
        }

        finished = true;
         wait = true;
        }
        }
   }

