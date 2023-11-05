import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class TEST {
    public static void main(String[] args) throws Exception {
        // String expected = "./src/ExpectedOutput/rast-alpha.png";
        // String output = "./src/Output/alpha.png";

        // BufferedImage expIMG = ImageIO.read(new File(expected));
        // BufferedImage outIMG = ImageIO.read(new File(output));
        
        // WritableRaster expraster = expIMG.getRaster();
        // WritableRaster outraster = outIMG.getRaster();

        // for (int x = 0; x < 120; x++) {
        //     for (int y = 0; y < 120; y++) {
        //         double[] expRGBA = expraster.getPixel(x, y, new double[4]);
        //         double[] outRGBA = outraster.getPixel(x, y, new double[4]);
        //         if (x == 75 && y == 75) {
        //             System.out.println("expected RGBA at (75, 75) is: " + Arrays.toString(expRGBA));
        //             System.out.println("output RGBA at (75, 75) is: " + Arrays.toString(outRGBA));
        //         }
        //     }
        // }
        // String path = "./src/block-I-orange-background.png";
        // BufferedImage img = ImageIO.read(new File(path));
        // WritableRaster ras = img.getRaster();
        // System.out.println(ras.getWidth() + " " + ras.getHeight());
        // System.out.println(Arrays.toString(ras.getPixel(93, 93, new double[4])));

        List<String[]> cmds = new ArrayList<>();
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-gray.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-smallgap.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-smoothcolor.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-checkers.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-depth.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-elements.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-sRGB.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-gammabox.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-perspective.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-textures.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-matrix.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-decals.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-2d3d.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-alpha.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-fsaa2.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-fsaa8.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-cull.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-points1.txt'"});
        cmds.add(new String[] {"make", "run", "file=" + "'./input/rast-points2.txt'"});
        

        for (String[] cmd : cmds) {
            Thread thread = new Thread
                                (() -> 
                                        {
                                            try {
                                                ProcessBuilder processBuilder = new ProcessBuilder(cmd);
                                                System.out.println(String.format("Run cmd: %s", Arrays.toString(cmd)));
                                                processBuilder.redirectErrorStream(true);
                                                Process p = processBuilder.start();
                                                int code = p.waitFor();
                                                if (code != 0) {
                                                    System.out.println(cmd[2] + " error: " + code);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                );
            thread.start();
        }
    }



    public static double GammaCorection(double value) {
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
    public static double ReverseGammaCorection(double value) {
        if (value <= 0.04045) 
        {
            return (value / 12.92);
        } else 
        {
            return (Math.pow(((value + 0.055) / 1.055), 2.4));
        }
    }
}
