import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Drawer {

    //static:
    //Drawing function with FSAA enabled
    public static void FSAAoutput(WritableRaster src, WritableRaster target, int width, int height, int fsaa) {
        if (fsaa == 1) return;
        Drawer d = new Drawer();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] targetRGBA = {0.0, 0.0, 0.0, 0.0};
                int ratio = 0;
                for (int j = y * fsaa; j < y * fsaa + fsaa; j++) {
                    for (int i = x * fsaa; i < x * fsaa + fsaa; i++) {
                        double[] srcRGBA = new double[4];
                        src.getPixel(i, j, srcRGBA);
                        if (srcRGBA[3] == 0) continue;
                        for (int idx = 0; idx < 3; idx++) {
                            targetRGBA[idx] += d.ReverseGammaCorection(srcRGBA[idx] / 255);
                        }
                        targetRGBA[3] += srcRGBA[3];
                        // ratio += srcRGBA[3];
                        ratio++;
                    }
                }
                for (int idx = 0; idx < 3; idx++) {
                    targetRGBA[idx] /= ratio;
                    targetRGBA[idx] = d.GammaCorection(targetRGBA[idx]) * 255;
                }
                targetRGBA[3] /= (fsaa * fsaa);
                target.setPixel(x, y, targetRGBA);
            }
        }
    }
    Drawer() {
        flags = new Flags();
        flags.sRGB = true;
    }
    // x,y,z,w,X,Y,R,G,B,A,s,t;
    private class Vector {
        double[] vector;
        //Concatenate position and color vector into one
        Vector(double[] pos, double[] color, double[] coord, boolean hyp) {
            vector = new double[pos.length+color.length+coord.length];
            int i = 0;
            // Perform a separate operation for hyperbolic mode
            if (hyp == true) {
                double w = pos[3];
                for (double d : pos) {
                    vector[i++] = d;
                }
                
                for (double d : color) {
                    vector[i++] = d/w;
                }
                for (double d : coord) {
                    vector[i++] = d/w;
                }
                vector[2] /= w;
                vector[3] = 1/w;
            } else {
                for (double d : pos) {
                    vector[i++] = d;
                }
                for (double d : color) {
                    vector[i++] = d;
                }
                for (double d : coord) {
                    vector[i++] = d;
                }
            }
        }
        //Copy constructor
        Vector(Vector other) {
            vector = Arrays.copyOf(other.vector, other.vector.length);
        }
        
        //Getter
        public double GetDimension(int d) {
            assert d < vector.length;
            return vector[d];
        }

        @Override
        public String toString() {
            return "Vector{" + "values=" + Arrays.toString(vector) + "}";
            // return Double.toString(vector[5]);
        }
        // public String XYtoString() {
        //     return String.format("(%f, %f)", vector[4], vector[5]);
        // }
        // public String RGBtoString() {
        //     return String.format("(rgb = (%f, %f, %f))", vector[6], vector[7], vector[8]);
        // }
        // Basic Math
        public void add(double[] v) {
            assert vector.length == v.length;
            for (int i = 0; i < vector.length; i++) {
                vector[i] += v[i];
                // if (i == 10 || i == 11) {
                //     vector[i] -= Math.floor(vector[i]);
                // }
            }
        }
        public void add(Vector p) {
            add(p.vector);
        }
        public void sub(double[] v) {
            assert vector.length == v.length;
            for (int i = 0; i < vector.length; i++) {
                vector[i] -= v[i];
                // if (i == 10 || i == 11) {
                //     vector[i] -= Math.floor(vector[i]);
                // }
            }
        }
        public void sub(Vector p) {
            sub(p.vector);
        }
        public void multiply(double d) {
            assert d != 0;
            for (int i = 0; i < vector.length; i++) {
                vector[i] *= d;
                // if (i == 10 || i == 11) {
                //     vector[i] -= Math.floor(vector[i]);
                // }
            }
        }
        public void divide(double d) {
            assert d != 0;
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= d;
                // if (i == 10 || i == 11) {
                //     vector[i] -= Math.floor(vector[i]);
                // }
            }
        }
        public static double compare(Vector a, Vector b, int d) {
            return a.vector[d] - b.vector[d];
        }
    }
    private int width, height;
    private Flags flags;
    /**
     * This region is specifically for:         drawArraysPoints
     */
    Drawer(Flags f, int w, int h) {
        flags = f;
        width = w;
        height = h;
    }
    /**
     * This region is specifically for:         drawTriangles
     */
    private Vector t, b, m;
    private boolean cullShouldDraw = true;
    // Assign three vertex in order based on Y-coordinate value
    Drawer(
            Flags f, int w, int h, 
            double[] pos0, double[] pos1, double[] pos2, 
            double[] color0, double[] color1, double[] color2,
            double[] coord0, double[] coord1, double[] coord2
            ) {
        flags = f;
        width = w;
        height = h;
        if (flags.cull) cullShouldDraw = CullIsOutter(pos0, pos1, pos2);
        // Find three Vectors in order
        Vector x = new Vector(pos0, color0, coord0, f.hyp);
        Vector y = new Vector(pos1, color1, coord1, f.hyp);
        Vector z = new Vector(pos2, color2, coord2, f.hyp);
        
        //find top-mid-bot vertex based on their Y-coordinate
        if (Vector.compare(x, y, 5) < 0 && Vector.compare(x, z, 5) < 0) {
            t = x;
            if (Vector.compare(y, z, 5) < 0) {
                m = y;
                b = z;
            } else {
                m = z;
                b = y;
            }
        } else if (Vector.compare(y, z, 5) < 0) {
            t = y;
            if (Vector.compare(x, z, 5) < 0) {
                m = x;
                b = z;
            } else {
                m = z;
                b = x;
            }
        } else {
            t = z;
            if (Vector.compare(x, y, 5) < 0) {
                m = x;
                b = y;
            } else {
                m = y;
                b = x;
            }
        }
    
    }
    public void drawTriangles(WritableRaster raster) {
        //If <cull> enabled and triangle is an inner triangle, do nothing
        if (cullShouldDraw == false) return;
        //dimension for Y-pos
        int d = 5;
        //DDA t to b:
        Vector plong = null, slong = null;
        Vector[] pslong;
        if ((pslong = DDA17(t, b, d)) == null) return;
        plong = pslong[0];
        slong = pslong[1];
        //Find Vectors in the top half of the triangle:
        //DDA t to m
        Vector p = null, s = null;
        Vector[] ps;
        if ((ps = DDA17(t, m, d)) != null) {
            // there is a top half triangle
            p = ps[0];
            s = ps[1];
            DDA8(p, s, plong, slong, m.GetDimension(d), raster);
        }
        //Find points in the bottom half of the triangle:
        //DDA m to b
        if ((ps = DDA17(m, b, d)) != null) {
            // there is a bottom half triangle
            p = ps[0];
            s = ps[1];
            DDA8(p, s, plong, slong, b.GetDimension(d), raster);
        }
    }
    /**
     * Run DDA step 1-7 
     * https://cs418.cs.illinois.edu/website/text/dda.html 
     * @return null if no point found, otherwise Vector[] of p and s
     */
    private Vector[] DDA17(Vector a, Vector b, int dimension) {
        // Step 1
        if (Vector.compare(a, b, dimension) == 0) return null;
        // Step 2
        if (Vector.compare(a, b, dimension) > 0) {
            //swap
            Vector temp = a;
            a = b;
            b = temp;
        }
        // Step 3
        Vector s = new Vector(b);
        s.sub(a);
        // Step 4
        s.divide(s.GetDimension(dimension));
        // Step 5
        double e = Math.ceil(a.GetDimension(dimension)) - a.GetDimension(dimension);
        // Step 6
        Vector p = new Vector(s);
        p.multiply(e);
        // Step 7
        p.add(a);
        return new Vector[] {p, s};
    }

    /**
     * Run DDA loop step 8
     * https://cs418.cs.illinois.edu/website/text/dda.html 
     */
    private void DDA8(Vector p, Vector s, Vector plong, Vector slong, double endPoint, WritableRaster raster) {
        int X = 4, Y = 5;
        while (p.GetDimension(Y) < endPoint) {
            // System.out.println("DDA8: " + plong.XYtoString() + plong.RGBtoString() + " to " + p.XYtoString() + p.RGBtoString());
            DDADraw(p, plong, X, raster);
            p.add(s);
            plong.add(slong);
        }
        return;
    }
    /**
     * Draw pixels
     */
    private void DDADraw(Vector a, Vector b, int xDimension, WritableRaster raster) {
        Vector[] ps = DDA17(a, b, xDimension);
        if (ps == null) return;
        double endPoint = Math.max(a.GetDimension(xDimension), b.GetDimension(xDimension));
        
        BufferedImage textureIMG = null;
        WritableRaster textureRaster = null;
        int textureWidth = 0, textureHeight = 0;
        if (flags.colorOrTexture > 0) {
            try {
                textureIMG = ImageIO.read(new File(flags.texturePath));
                textureRaster = textureIMG.getRaster();
                textureWidth = textureRaster.getWidth();
                textureHeight = textureRaster.getHeight();
            } catch (Exception e) {
                System.out.println("Can not access " + flags.texturePath);
            }
        }

        while (ps[0].GetDimension(xDimension) < endPoint) {
            // System.out.println(String.format("(x,y) = (%f, %f)", ps[0].GetDimension(xDimension), ps[0].GetDimension(xDimension + 1)));
            int x = (int) ps[0].GetDimension(xDimension);
            int y = (int) ps[0].GetDimension(xDimension + 1);
            if (x < 0 || x >= width || y < 0 || y >= height) {
                ps[0].add(ps[1]);
                continue;
            }
            //if <depth> is enabled, ignore pixels with same (x,y), but further distance (z)
            if (flags.depth) {
                Double currentZ = ps[0].GetDimension(2);
                int xy = y * width + x;
                // System.out.println("xy: " + xy);
                Double recordedZ = flags.depthMap.get(xy);
                if (recordedZ != null && recordedZ < currentZ) {
                    ps[0].add(ps[1]);
                    continue;
                }
                flags.depthMap.put(xy, currentZ);
            }
            double WPrime = ps[0].GetDimension(xDimension - 1);
            
            RGB rgb = new RGB(
                                ps[0].GetDimension(xDimension + 2), 
                                ps[0].GetDimension(xDimension + 3), 
                                ps[0].GetDimension(xDimension + 4), 
                                flags.color == 3 ? 1.0 : ps[0].GetDimension(xDimension + 5),
                                flags.hyp,
                                WPrime,
                                this
                            );
            //textures are involved
            if (flags.colorOrTexture > 0) {
                //compute texture pixel to <RGB rgb>
                double s = ps[0].GetDimension(10)  / (flags.hyp ? WPrime : 1);
                double t = ps[0].GetDimension(11)  / (flags.hyp ? WPrime : 1);
                s -= Math.floor(s);
                t -= Math.floor(t);
                int tx = (int) (textureWidth * s);
                int ty = (int) (textureHeight * t);
                double[] textureRGBA = textureRaster.getPixel(tx, ty, new double[4]);
                RGB TextureRGBA = new RGB(textureRGBA[0] / 255.0, textureRGBA[1] / 255.0, textureRGBA[2] / 255.0, (flags.color == 3 && flags.decals == false) ? 1.0 : textureRGBA[3] / 255.0);
                if (flags.colorOrTexture == 2)  //both textures and vertex colors are involved
                {
                    //compute the vertex color: the base
                    RGB vertexRGBA = rgb;
                    //compute the actual pixel color
                    rgb = RGB.combine(TextureRGBA, vertexRGBA, this);
                } 
                else                            //only textures colors are used
                {
                    rgb = TextureRGBA;
                }
            }

            if (flags.decals == false && flags.color == 3) {
                raster.setPixel(x, y, new double[]{rgb.R * 255.0, rgb.G * 255.0, rgb.B * 255.0, 255.0});
            } else {
                //handle transparency
                int xy = y * width + x;
                RGB base = flags.rgbMap.get(xy);
                if (base == null) {
                    //no existing base
                    flags.rgbMap.put(xy, rgb);
                    raster.setPixel(x, y, new double[]{rgb.R * 255.0, rgb.G * 255.0, rgb.B * 255.0, rgb.A * 255.0});
                } else {
                    //overlap
                    RGB over = RGB.combine(rgb, base, this);
                    // if (x == 75 && y == 75) {
                    //     System.out.println(String.format("Base:\t(%f, %f, %f, %f)", base.R*255.0, base.G*255.0, base.B*255.0, base.A*255.0));
                    //     System.out.println(String.format("New:\t(%f, %f, %f, %f)", rgb.R*255.0, rgb.G*255.0, rgb.B*255.0, rgb.A*255.0));
                    //     System.out.println(String.format("Combine:\t(%f, %f, %f, %f)", over.R*255.0, over.G*255.0, over.B*255.0, over.A*255.0));
                    // }
                    flags.rgbMap.put(xy, over);
                    raster.setPixel(x, y, new double[]{over.R * 255.0, over.G * 255.0, over.B * 255.0, over.A * 255.0});
                }
            }
            ps[0].add(ps[1]);
        }
    }
    
    /**
     * Draw single square
     * @param color Is null when texture is used instead
     */
    public void drawSquare(WritableRaster raster, double[] pos, double diameter, double[] color) {
        double z = pos[2];
        double offset = diameter / 2;
        // int x = (int) Math.ceil(Math.max(0, pos[4] - offset)), y = (int) Math.ceil(Math.max(0, pos[5] - offset));
        int x = (int) Math.ceil(pos[4] - offset);
        int y = (int) Math.ceil(pos[5] - offset);
        
        if (color != null) //draw with given color 
        {
            RGB rgb = new RGB(color[0], color[1], color[2], color[3], false, 0, this);
            // for (int j = y; j < (int) Math.ceil(Math.min(pos[5] + offset, height)); j++) {
            //     for (int i = x; i < (int) Math.ceil(Math.min(pos[4] + offset, width)); i++) {
            //         //if <depth> is enabled, ignore pixels with same (x,y), but further distance (z)
            //         if (flags.depth) {
            //             int ij = j * width + i;
            //             Double recordedZ = flags.depthMap.get(ij);
            //             if (recordedZ != null && recordedZ < z) {
            //                 continue;
            //             }
            //             flags.depthMap.put(ij, z);
            //         }
            //         raster.setPixel(i, j, new double[]{rgb.R * 255.0, rgb.G * 255.0, rgb.B * 255.0, 255.0});
            //     }
            // }
            for (int j = y; j < (int) Math.ceil(pos[5] + offset); j++) {
                if (j < 0) continue;
                if (j >= height) break;
                for (int i = x; i < (int) Math.ceil(pos[4] + offset); i++) {
                    if (i < 0) continue;
                    if (i >= width) break;
                    if (flags.depth) {
                        int ij = j * width + i;
                        Double recordedZ = flags.depthMap.get(ij);
                        if (recordedZ != null && recordedZ < z) {
                            continue;
                        }
                        flags.depthMap.put(ij, z);
                    }
                    raster.setPixel(i, j, new double[]{rgb.R * 255.0, rgb.G * 255.0, rgb.B * 255.0, 255.0});
                }
            }
        }
        else                //draw with textures
        {
            try {
                //set up
                BufferedImage textureIMG = ImageIO.read(new File(flags.texturePath));
                WritableRaster textureRaster = textureIMG.getRaster();
                int textureWidth = textureRaster.getWidth(), textureHeight = textureRaster.getHeight();
                // int jd = (int) Math.floor(Math.min(diameter, height - y));
                // int id = (int) Math.floor(Math.min(diameter, width - x));
                // for (int j = y; j < (int) Math.ceil(Math.min(pos[5] + offset, height)); j++) {
                //     for (int i = x; i < (int) Math.ceil(Math.min(pos[4] + offset, width)); i++) {
                //         if (flags.depth) {
                //             int ij = j * width + i;
                //             Double recordedZ = flags.depthMap.get(ij);
                //             if (recordedZ != null && recordedZ < z) {
                //                 continue;
                //             }
                //             flags.depthMap.put(ij, z);
                //         }
                //         double rx = (1.0 * (i - x) / diameter);
                //         rx -= Math.floor(rx);
                //         double ry = (1.0 * (j - y) / diameter);
                //         ry -= Math.floor(ry);
                //         int tx = (int) Math.ceil(textureWidth * rx);
                //         int ty = (int) Math.ceil(textureHeight * ry);
                //         double[] textureRGBA = textureRaster.getPixel(tx, ty, new double[4]);
                //         raster.setPixel(i, j, textureRGBA);

                //         // raster.setPixel(i, j, new double[]{0,0,0,255});
                //     }
                // }
                for (int j = y; j < (int) Math.ceil(pos[5] + offset); j++) {
                    if (j < 0) continue;
                    if (j >= height) break;
                    for (int i = x; i < (int) Math.ceil(pos[4] + offset); i++) {
                        if (i < 0) continue;
                        if (i >= width) break;
                        if (flags.depth) {
                            int ij = j * width + i;
                            Double recordedZ = flags.depthMap.get(ij);
                            if (recordedZ != null && recordedZ < z) {
                                continue;
                            }
                            flags.depthMap.put(ij, z);
                        }
                        double rx = 1.0 * (i - x) / diameter;
                        double ry = 1.0 * (j - y) / diameter;
                        rx -= Math.floor(rx);
                        ry -= Math.floor(ry);
                        int tx = (int) (textureWidth * rx);
                        int ty = (int) (textureHeight * ry);
                        double[] textureRGBA = textureRaster.getPixel(tx, ty, new double[4]);
                        raster.setPixel(i, j, textureRGBA);
                    }
                }

            } catch (Exception e) {
                System.out.println("Can not access " + flags.texturePath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Called to perform Gamma Correction on a color value
     * Gamma Correction will only be done if and only if Flags.sRGB == true
     * @param value the color value in range of 0.0-1.0
     * @return returns the corrected color value as a double ranging 0.0-1.0
     */
    public double GammaCorection(double value) {
        if (flags.sRGB == false) return value;
        if (value <= 0.0031308) 
        {
            return 12.92 * value;
        } else 
        {
            return 1.055 * Math.pow(value, 1/2.4) - 0.055;
        }
    }
    /**
     * Reverse version of Gamma Correction as previous
     */
    public double ReverseGammaCorection(double value) {
        if (flags.sRGB == false) return value;
        if (value <= 0.04045) 
        {
            return (value / 12.92);
        } else 
        {
            return (Math.pow(((value + 0.055) / 1.055), 2.4));
        }
    }

    /**
     * @return true if <cull> is disabled or triangle is counter clockwise (outter)
     */
    private boolean CullIsOutter(double[] pos0, double[] pos1, double[] pos2) {
        // x = pos[4] and y = pos[5]
        double xlong = pos0[4] - pos1[4];
        double ylong = pos0[5] - pos1[5];

        double xshort = pos1[4] - pos2[4];
        double yshort = pos1[5] - pos2[5];

        double crossProduct = xlong * yshort - xshort * ylong;

        return crossProduct < 0;
    }
}