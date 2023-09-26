import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;;

public class PNGmaker {
    private BufferedImage image;
    private WritableRaster raster;

    public String outputFileName, texture;
    public int width, height, fsaaLevel;
    /**
     * positions-element: {x, y, z, w, XYx, XYy}
     */
    public List<double[]> positions, colors;
    public List<Integer> elements, texcoords;

    public Flags flags;
    
    PNGmaker() {
        positions = new ArrayList<>();
        colors = new ArrayList<>();
        flags = new Flags();
    }

    // Member Functions

    /**
     * Create the output file
     * @param path the directory of the output image
     */
    void createImage(String path) {
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Prepare a buffered PNG for later use
    void initPNG() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        raster = image.getRaster();
    }
    // Called when 'position' is called, reset and store pos-value into positions
    void processPositions(String[] positionStrings) {
        System.out.println("Positions:");
        if (!positions.isEmpty()) positions.clear();
        int size = Integer.parseInt(positionStrings[1]);
        for (int i = 2; i < positionStrings.length; i += size) {
            double[] pos = {0.0, 0.0, 0.0, 1.0, 0.0, 0.0};
            for (int off = 0; off < size; off++) {
                pos[off] = Double.parseDouble(positionStrings[i + off]);
            }
            pos[4] = (pos[0]/pos[3]+1)*width/2;
            pos[5] = (pos[1]/pos[3]+1)*height/2;
            positions.add(pos);
            System.out.print("output: ");
            for (double p : pos) {
                System.out.print(p + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    // Called when 'color' is called, reset and store color-value into colors
    void processColors(String[] colorStrings) {
        System.out.println("Colors:");
        if (!colors.isEmpty()) colors.clear();
        int size = Integer.parseInt(colorStrings[1]);
        for (int i = 2; i < colorStrings.length; i+= size) {
            double[] color = {0.0, 0.0, 0.0, 1.0};
            for (int off = 0; off < size; off++) {
                //update RGB / SRGB value depending of <this.Flags.sRGB>
                color[off] = Double.parseDouble(colorStrings[i + off]);
            }
            colors.add(color);
            System.out.print("color: ");
            for (double c : color) {
                System.out.print(c + " ");
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }

    // Called when 'drawArraysTriangles' is called, to create image
    void processDrawArraysTriangles(int first, int count) {
        for (int i = first; i < first + count; i += 3) {
            Drawer drawer = new Drawer(flags, width, height, positions.get(i), positions.get(i+1), positions.get(i+2), colors.get(i), colors.get(i+1), colors.get(i+2));
            drawer.drawArraysTriangles(raster);
        }
    }

    // Called when 'sRGB' is presented, indication of using sRGB value instead of RGB
    void processSRGB() {
        flags.sRGB = true;
    }
}
