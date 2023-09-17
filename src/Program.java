import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Program {
    private static PNGmaker pngMaker;
    public static void main(String[] args) throws Exception {
        // ./yourprogram exampleInput.txt
        if (args.length != 1) {
            System.out.println("Invalid Input");
            return;
        }
        //Process Input File
        ProcessFile(args[0]);
    }
    
    // Read and process the txt file line by line 
    private static void ProcessFile(String path) throws Exception {
        pngMaker = new PNGmaker();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            //read line by line
            while ((line = reader.readLine()) != null) {
                // ignore useless lines
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                System.out.println(line);
                String[] infos = line.split("\\s+");
                updatePNGmaker(infos);
            }
        } catch (IOException e) {
            System.err.println("Invalid Input File Path");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("UnExpected Error Occured");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Send instructions to PNG maker based on infos from the txt file
    private static void updatePNGmaker(String[] infos) {
        switch (infos[0]) {
            case "png":
                pngMaker.width = Integer.parseInt(infos[1]);
                pngMaker.height = Integer.parseInt(infos[2]);
                pngMaker.outputFileName = infos[3];
                pngMaker.initPNG();
                break;
            case "position":
                pngMaker.processPositions(infos);
                break;
            case "color":
                break;
            case "drawArraysTriangles":
                break;
            case "depth":
                break;
            case "sRGB":
                break;
            case "hyp":
                break;
            case "elements":
                break;
            case "uniformMatrix":
                break;
            case "drawElementsTriangles":
                break;
            case "cull":
                break;
            case "decals":
                break;
            case "texture":
                break;
            case "texcoord":
                break;
            case "frustum":
                break;
            case "fsaa":
                break;
            case "pointsize":
                break;
            case "drawArraysPoints":
                break;
            default:
                break;
        }
    }
}

/*
 *        // ...
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        WritableRaster raster = b.getRaster();
        // ...
        raster.SetPixel(x,y, new Color(red,green,blue,alpha));
        // ...
        ImageIO.write(image, "png", new File(filename));
 */