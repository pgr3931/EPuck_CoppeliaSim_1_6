package at.fhv.dgr1992.differentialWheels;

import java.awt.image.BufferedImage;

/**
 * CameraImage takes a BufferedImage to hold a image. It provides set and get functions to easy work with the image.
 */
public class CameraImage {
    private BufferedImage _image;
    private int _width;
    private int _height;

    /**
     * Constructs a new blank camera image
     * @param width width of the image
     * @param height height of the image
     */
    public CameraImage(int width, int height){
        _width = width;
        _height = height;
        _image = new BufferedImage(_width,_height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Set a pixel of the image
     * @param x x position of the pixel
     * @param y y position of the pixel
     * @param r Red value of the pixel
     * @param g Green value of the pixel
     * @param b Blue value of the pixel
     */
    public void setPixel(int x, int y, int r, int g, int b){
        int rgb = 0;

        //Set red value
        //Red value from bit 16 till 23
        if(r > 255){
            //Shift 16 bits to the left
            rgb = rgb | (255<<16);
        } else if(r > 0){
            rgb = rgb | (r<<16);
        }

        //Set green value
        //green value from bit 8 till 15
        if(g > 255){
            //Shift 8 bits to the left
            rgb = rgb | (255<<8);
        } else if(g > 0){
            rgb = rgb | (g<<8);
        }

        //Set blue value
        //blue value from bit 0 till 7
        if(b > 255){
            rgb = rgb | 255;
        } else if(b > 0){
            rgb = rgb | b;
        }

        _image.setRGB(x,y,rgb);
    }

    /**
     * Get a specific pixel of the image. X=0 Y=0 is in the left top corner.
     * @param x x position of the pixel
     * @param y y position of the pixel
     * @return Pixel on the specified position
     */
    public CameraImagePixel getPixel(int x, int y){
        int rgb = _image.getRGB(x,y);
        return new CameraImagePixel(rgb);
    }

    /**
     * Get the camera image as a BufferedImage
     * @return Camera image as BufferedImage
     */
    public BufferedImage getBufferedImage(){
        return _image;
    }
}
