package at.fhv.dgr1992.differentialWheels;

/**
 * CameraPixelImage gives easy access to the r,g,b value of a rgb int value.
 */
public class CameraImagePixel {
    private int _r;
    private int _g;
    private int _b;

    /**
     * Constructs new camera image pixel from the given rgb int value.
     * @param rgb Integer value from a BufferedImage with BufferedImage.TYPE_INT_RGB
     */
    public CameraImagePixel(int rgb){
        _r= (rgb>>16)&255;
        _g= (rgb>>8)&255;
        _b= (rgb)&255;
    }

    /**
     * Red value of the pixel
     * @return Red value with range 0 - 255
     */
    public int getRed(){
        return _r;
    }

    /**
     * Green value of the pixel
     * @return Green value with range 0 - 255
     */
    public int getGreen(){
        return _g;
    }

    /**
     * Blue value of the pixel
     * @return Blue value with range 0 - 255
     */
    public int getBlue(){
        return _b;
    }
}
