import java.util.Map;

public class Flags {
    public boolean depth = false, 
                    sRGB = false, 
                        hyp = false, 
                    cull = false, 
                    decals = false, 
                    frustum = false, 
                    fsaa = false,
                    uniformMatrix = false;
    public int color;
    public double[][] unimatrix;
    //depth buffer when depth is enabled    (x,y) -> (z)
    public Map<Integer, Double> depthMap;
    //blending buffer when <alpha> is enabled (x,y) -> (RGBA)
    public Map<Integer, RGB> rgbMap;

    //utility vector dot product when <uniformMatrix> enabled
    public double dotProduct(double x, double y, double z, double w, int idx) {
        return x * unimatrix[idx][0] + y * unimatrix[idx][1] + z * unimatrix[idx][2] + w * unimatrix[idx][3];
    }
}