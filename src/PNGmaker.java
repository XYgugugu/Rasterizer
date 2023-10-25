import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    public List<double[]> transformedPositions;   //transformed position with uniformmatrix
    public List<Integer> elements, texcoords;

    public Flags flags;
    
    PNGmaker() {
        flags = new Flags();
        positions = new ArrayList<>();
        colors = new ArrayList<>();

        elements = new ArrayList<>();
        texcoords = new ArrayList<>();
    }

    // Member Functions

    /**
     * Create the output file
     * @param path the directory of the output image
     */
    void createImage() {
        try {
            ImageIO.write(image, "png", new File(outputFileName));
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
        // System.out.println("Positions:");
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
        }
        //Update transformed positions if there is an uniform matrix to update the positions
        if (flags.uniformMatrix == true) UpdateTransformedPosition();
    }

    // Called when 'color' is called, reset and store color-value into colors
    void processColors(String[] colorStrings) {
        // System.out.println("Colors:");
        if (!colors.isEmpty()) colors.clear();
        int size = Integer.parseInt(colorStrings[1]);
        flags.color = size;
        if (size == 4) flags.rgbMap = new HashMap<>();
        for (int i = 2; i < colorStrings.length; i+= size) {
            double[] color = {0.0, 0.0, 0.0, 1.0};
            for (int off = 0; off < size; off++) {
                //update RGB / SRGB value depending of <this.Flags.sRGB>
                color[off] = Double.parseDouble(colorStrings[i + off]);
            }
            colors.add(color);
        }
    }

    // Called when 'elements' is called, update the List<Integer> elements with non-negative value
    void processElements(String[] elementsStrings) {
        if (!elements.isEmpty()) elements.clear();;
        for (int i = 1; i < elementsStrings.length; i++) {
            elements.add(Integer.parseInt(elementsStrings[i]));
        }
    }

    // Called when 'drawArraysTriangles' is called, to create image
    void processDrawArraysTriangles(int first, int count) {
        //determine which position list to use
        List<double[]> posList = flags.uniformMatrix ? transformedPositions : positions;
        for (int i = first; i < first + count; i += 3) {
            Drawer drawer = new Drawer
                                    (
                                        flags, width, height, 
                                        posList.get(i), posList.get(i+1), posList.get(i+2), 
                                        colors.get(i), colors.get(i+1), colors.get(i+2)
                                    );
            drawer.drawTriangles(raster);
        }
    }

    // Called when 'drawElementsTriangles' is called, to create image
    void processDrawElementsTriangles(int count, int offset) {
        //determine which position list to use
        List<double[]> posList = flags.uniformMatrix ? transformedPositions : positions;
        for (int i = offset; i < offset + count; i += 3) {
            Drawer drawer = new Drawer
                                    (
                                        flags, width, height, 
                                        posList.get(elements.get(i)), posList.get(elements.get(i+1)), posList.get(elements.get(i+2)), 
                                        colors.get(elements.get(i)), colors.get(elements.get(i+1)), colors.get(elements.get(i+2))
                                    );
            drawer.drawTriangles(raster);
        }
    }

    // Update Flags
    // Called when 'sRGB' is presented, indication of using sRGB value instead of RGB
    void processSRGB() {
        flags.sRGB = true;
    }
    // Called when 'hyp' is presented, indication of using hyperbolic
    void processHYP() {
        flags.hyp = true;
    }
    // Called when 'depth' is presented, indication of using depth buffer
    void processDepth() {
        flags.depth = true;
        flags.depthMap = new HashMap<>();
    }
    // Called when 'uniformMatrix' is presented, indication of multiplying the matrix to three starting vertex
    void processUniformMatrix(String[] ns) {
        flags.uniformMatrix = true;
        flags.unimatrix = new double[4][4];
        for (int i = 0; i < 16; i++) {
            flags.unimatrix[i % 4][i / 4] = Double.parseDouble(ns[i + 1]);
        }
        UpdateTransformedPosition();
    }
    // Called when UniformMatrix/Position is updated
    private void UpdateTransformedPosition() {
        transformedPositions = new ArrayList<>();
        for (double[] pos : positions) {
            double x = pos[0], y = pos[1], z = pos[2], w = pos[3];
            double[] tpos = new double[6];
            tpos[0] = flags.dotProduct(x, y, z, w, 0);
            tpos[1] = flags.dotProduct(x, y, z, w, 1);
            tpos[2] = flags.dotProduct(x, y, z, w, 2);
            tpos[3] = flags.dotProduct(x, y, z, w, 3);
            tpos[4] = (tpos[0]/tpos[3]+1)*width/2;
            tpos[5] = (tpos[1]/tpos[3]+1)*height/2;
            transformedPositions.add(tpos);
        }
    }

}
