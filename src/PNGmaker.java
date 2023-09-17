import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;;

public class PNGmaker {
    private BufferedImage image;
    private WritableRaster raster;

    public String outputFileName, texture;
    public int width, height, fsaaLevel;
    public List<double[]> positions, colors;
    public List<Integer> elements, texcoords;
    public boolean 
                    depth = false, 
                    sRGB = false, 
                    hyp = false, 
                    cull = false, 
                    decals = false, 
                    frustum = false, 
                    fsaa = false;
    
    PNGmaker() {
        positions = new ArrayList<>();
        colors = new ArrayList<>();
    }

    // Member Functions

    // Prepare a buffered PNG for later use
    void initPNG() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        raster = image.getRaster();
    }
    // Called when 'position' is called, reset and store pos-value into positions
    void processPositions(String[] positionStrings) {
        if (!positions.isEmpty()) positions.clear();
        int size = Integer.parseInt(positionStrings[1]);
        for (int i = 2; i < positionStrings.length; i += size) {
            double[] pos = {0.0, 0.0, 0.0, 1.0};
            for (int off = 0; off < size; off++) {
                pos[off] = Double.parseDouble(positionStrings[i + off]);
            }
            positions.add(pos);
            System.out.print("output: ");
            for (double p : pos) {
                System.out.print(p + " ");
            }
            System.out.println("");
        }
    }
}
