import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.imageio.ImageIO;
import java.awt.image.WritableRaster;

public class RGBATEST {
    public static void main(String[] args) throws Exception {
        String expected = "./src/ExpectedOutput/rast-sRGB.png";
        String output = "./src/Output/sRGB.png";

        BufferedImage expIMG = ImageIO.read(new File(expected));
        BufferedImage outIMG = ImageIO.read(new File(output));
        
        WritableRaster expraster = expIMG.getRaster();
        WritableRaster outraster = outIMG.getRaster();

        int x = 10, y = 15;

        double[] expRGBA = expraster.getPixel(x, y, new double[4]);
        double[] outRGBA = outraster.getPixel(x, y, new double[4]);

        System.out.println("expected RGBA at (75, 75) is: " + Arrays.toString(expRGBA));
        System.out.println("output RGBA at (75, 75) is: " + Arrays.toString(outRGBA));
    }
}
