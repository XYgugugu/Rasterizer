import java.util.HashMap;
import java.util.Map;

public class Flags {
    public boolean depth = false, 
                    sRGB = false, 
                        hyp = false, 
                    cull = false, 
                    decals = false, 
                    frustum = false, 
                    uniformMatrix = false;
    public boolean pointUseTexture = false;     //only valid with <drawArraysPoints>    false - color   true - texture
    public int colorOrTexture = 0;  //0: default, render with color value - 1: use texture instead - 2: use both color and texture  
    public int color = 3;           //3: RGB - 4: RGBA
    public int fsaa = 1;
    public String texturePath;
    public double[][] unimatrix;
    //depth buffer when depth is enabled    (x,y) -> (z)
    public Map<Integer, Double> depthMap;
    //blending buffer when <alpha> is enabled (x,y) -> (RGBA)
    public Map<Integer, RGB> rgbMap;

    //Constructor
    public Flags() {
        depthMap = new HashMap<>();
        rgbMap = new HashMap<>();
        texturePath = null;
    }

    //utility vector dot product when <uniformMatrix> enabled
    public double dotProduct(double x, double y, double z, double w, int idx) {
        return x * unimatrix[idx][0] + y * unimatrix[idx][1] + z * unimatrix[idx][2] + w * unimatrix[idx][3];
    }
}