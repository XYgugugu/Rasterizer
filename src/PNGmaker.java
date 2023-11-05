import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;;

public class PNGmaker {
    private BufferedImage image;
    private WritableRaster raster;
    
    private BufferedImage fsaaimage;
    private WritableRaster fsaaraster;

    public String outputFileName;
    public int width, height, fsaaLevel = 1;
    /**
     * positions-element: {x, y, z, w, XYx, XYy}
     */
    public List<double[]> positions, colors, texcoords;
    public List<double[]> transformedPositions;   //transformed position with uniformmatrix
    public List<Integer> elements;
    public List<Double> pointSizes;

    public Flags flags;
    
    PNGmaker() {
        flags = new Flags();

        positions = new ArrayList<>();
        colors = new ArrayList<>();
        texcoords = new ArrayList<>();

        elements = new ArrayList<>();
        pointSizes = new ArrayList<>();
    }

    // Member Functions

    /**
     * Create the output file
     * @param path the directory of the output image
     */
    void createImage() {
        try {
            Drawer.FSAAoutput(fsaaraster, raster, width, height, fsaaLevel);
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
            pos[4] = (pos[0]/pos[3]+1)*width/2 * fsaaLevel;
            pos[5] = (pos[1]/pos[3]+1)*height/2 * fsaaLevel;
            positions.add(pos);
        }
        //Update transformed positions if there is an uniform matrix to update the positions
        if (flags.uniformMatrix == true) UpdateTransformedPosition();
    }

    // Called when 'color' is called, reset and store color-value into colors
    void processColors(String[] colorStrings) {
        flags.colorOrTexture = flags.decals == true ? 2 : 0;
        flags.pointUseTexture = false;
        if (!colors.isEmpty()) colors.clear();
        int size = Integer.parseInt(colorStrings[1]);
        flags.color = size;
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

    // Called when "texcoord" is called, update the <s, t> value
    void processTexcoord(String[] coordStrings) {
        assert coordStrings[1].equals("2") : "size (" + coordStrings[1] + ") should always be 2";
        if (!texcoords.isEmpty()) texcoords.clear();
        flags.colorOrTexture = flags.decals == true ? 2 : 1;
        for (int i = 2; i < coordStrings.length; i+=2) {
            texcoords.add(new double[] {Double.parseDouble(coordStrings[i]), Double.parseDouble(coordStrings[i + 1])});
        }
    }

    // Called when "pointsize" is called, update the diameter of each square
    void processPointSize(String[] pointSizeStrings) {
        assert pointSizeStrings[1].equals("1") : "size (" + pointSizeStrings[1] + ") should always be 1";
        if (!pointSizes.isEmpty()) pointSizes.clear();
        for (int i = 2; i < pointSizeStrings.length; i++) {
            pointSizes.add(Double.parseDouble(pointSizeStrings[i]));
        }
    }

    // Called when 'drawArraysTriangles' is called, to create image
    void processDrawArraysTriangles(int first, int count) {
        //determine which position list to use
        List<double[]> posList = flags.uniformMatrix ? transformedPositions : positions;
        boolean hasColor = (flags.colorOrTexture != 1);
        boolean hasCoords = (flags.colorOrTexture > 0);

        WritableRaster targetRaster = fsaaLevel > 1 ? fsaaraster : raster;

        for (int i = first; i < first + count; i += 3) {
            Drawer drawer = new Drawer
                                    (
                                        flags, width * fsaaLevel, height * fsaaLevel, 
                                        posList.get(i), posList.get(i+1), posList.get(i+2), 
                                        hasColor ? colors.get(i) : new double[]{0.0,0.0,0.0,0.0}, 
                                        hasColor ? colors.get(i+1) : new double[]{0.0,0.0,0.0,0.0}, 
                                        hasColor ? colors.get(i+2) : new double[]{0.0,0.0,0.0,0.0},
                                        hasCoords ? texcoords.get(i) : new double[]{0.0,0.0}, 
                                        hasCoords ? texcoords.get(i+1) : new double[]{0.0,0.0}, 
                                        hasCoords ? texcoords.get(i+2) : new double[]{0.0,0.0}
                                    );
            drawer.drawTriangles(targetRaster);
        }
    }

    // Called when 'drawElementsTriangles' is called, to create image
    void processDrawElementsTriangles(int count, int offset) {
        //determine which position list to use
        List<double[]> posList = flags.uniformMatrix ? transformedPositions : positions;
        boolean hasColor = (flags.colorOrTexture != 1);
        boolean hasCoords = (flags.colorOrTexture > 0);
        
        for (int i = offset; i < offset + count; i += 3) {
            Drawer drawer = new Drawer
                                    (
                                        flags, width, height, 
                                        posList.get(elements.get(i)), posList.get(elements.get(i+1)), posList.get(elements.get(i+2)), 
                                        hasColor ? colors.get(elements.get(i)) : new double[]{0.0,0.0,0.0,0.0},  
                                        hasColor ? colors.get(elements.get(i+1)) : new double[]{0.0,0.0,0.0,0.0},  
                                        hasColor ? colors.get(elements.get(i+2)) : new double[]{0.0,0.0,0.0,0.0}, 
                                        hasCoords ? texcoords.get(elements.get(i)) : new double[]{0.0,0.0}, 
                                        hasCoords ? texcoords.get(elements.get(i+1)) : new double[]{0.0,0.0}, 
                                        hasCoords ? texcoords.get(elements.get(i+2)) : new double[]{0.0,0.0}
                                    );
            drawer.drawTriangles(raster);
        }
    }

    // Called when 'drawArraysPoints' is called, to create image
    public void processDrawArraysPoints(int first, int count) {
        Drawer drawer = new Drawer(flags, width, height);
        for (int i = 0; i < count; i++) {
            drawer.drawSquare(raster, positions.get(first + i), pointSizes.get(i), flags.pointUseTexture ? null : colors.get(first + i));
        }
    }

    // Called when "texture" is called, update the path to the source file
    void processTexture(String path) {
        flags.texturePath = path;
        flags.pointUseTexture = true;
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
    }
    // Called when 'cull' is presented, indication of disregarding inner triangles
    void processCull() {
        flags.cull = true;
    }
    // Called when 'fsaa' is presented, indication of full-screen anti-aliasing
    void processFsaa(String fsaa) {
        int FSAA = Integer.parseInt(fsaa);
        if (FSAA <= 1) return;  //function as if FSAA was disabled
        flags.fsaa = FSAA;
        fsaaLevel = FSAA;
        fsaaimage = new BufferedImage(width * FSAA, height * FSAA, BufferedImage.TYPE_4BYTE_ABGR);
        fsaaraster = fsaaimage.getRaster();
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

    // Called when 'decals' is presented, indication of transparent textures with vertex colors also included
    void processDecals() {
        flags.decals = true;
        flags.colorOrTexture = 2;
    }
}
