package volvis;

import volume.Volume;

/**
 *
 * @author Frits
 */
public class CustomThread extends Thread {
    private final Volume volume;
    private RaycastRenderer raycastRenderer;
            
    private final float[] viewMatrix;
    private final float[] viewVec;
    private final float[] uVec;
    private final float[] vVec;
    
    private float[] pixelCoord = new float[3];
    private float[] entryPoint = new float[3];
    private float[] exitPoint = new float[3];

    private final int increment;
    private final float sampleStep;
    
    private final int imageCenter;
    public final int startX;
    public final int startY;
    public final int sizeX;
    public final int sizeY;

    private int[][] rgb;
    
    private boolean finished;

   CustomThread( String name, Volume volume, RaycastRenderer raycastRenderer, float[] viewMatrix, float[] viewVec, float[] uVec, float[] vVec, int increment, float sampleStep, int imageCenter, int startX, int startY, int sizeX, int sizeY ) {
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
       
       finished = false;   
   }
   
   public boolean isRunning(){
       return !finished;
   }
   
    public int[][] getData() {
        return rgb;
    }
   
   public void run() {
       long totalTime = 0;
       
        for (int j = startY; j < startY + sizeX; j += increment) {
            for (int i = startX; i < startX + sizeY; i += increment) {
                // compute starting points of rays in a plane shifted backwards to a position behind the data set
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter) - viewVec[0] * imageCenter
                        + volume.getDimX() / 2.0f;
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter) - viewVec[1] * imageCenter
                        + volume.getDimY() / 2.0f;
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter) - viewVec[2] * imageCenter
                        + volume.getDimZ() / 2.0f;
        

            
                
                raycastRenderer.computeEntryAndExit(pixelCoord, viewVec, entryPoint, exitPoint);
                          
                if ((entryPoint[0] > -1.0) && (exitPoint[0] > -1.0)) {
                    //System.out.println("Entry: " + entryPoint[0] + " " + entryPoint[1] + " " + entryPoint[2]);
                    //System.out.println("Exit: " + exitPoint[0] + " " + exitPoint[1] + " " + exitPoint[2]);
                    int pixelColor = 0;

                    /* set color to green if MipMode- see slicer function*/
                   long start_time = System.currentTimeMillis();
                   
                   if(raycastRenderer.mipMode) 
                        pixelColor = raycastRenderer.traceRayMIP(entryPoint,exitPoint,viewVec,sampleStep);

                   if(raycastRenderer.compositingMode)
                       pixelColor = raycastRenderer.traceRayComposite(entryPoint, exitPoint, viewVec, sampleStep);

        
                   
                   for ( int x = i; ( x < i + increment ) && ( x < startX + sizeX ); x++ ){
                       for ( int y = j; ( y < j + increment ) && ( y < startY + sizeY) ; y++ ){
                           rgb[x-startX][y-startY] = pixelColor;
                       }
                   }
                   
                                  long end_time = System.currentTimeMillis();
                totalTime += end_time-start_time;

                }
            }
        }
        
        System.out.println(totalTime);
        finished = true;
   }
   }

