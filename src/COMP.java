import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class COMP {
    public static void main(String[] args) throws Exception {

        List<String[]> cmds = new ArrayList<>();

        
        cmds.add(new String[] {"magick", "compare", ".\\Output\\gray.png", ".\\ExpectedOutput\\rast-gray.png", ".\\Comparison\\gray-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\smallgap.png", ".\\ExpectedOutput\\rast-smallgap.png", ".\\Comparison\\smallgap-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\smoothcolor.png", ".\\ExpectedOutput\\rast-smoothcolor.png", ".\\Comparison\\smoothcolor-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\checkers.png", ".\\ExpectedOutput\\rast-checkers.png", ".\\Comparison\\checkers-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\depth.png", ".\\ExpectedOutput\\rast-depth.png", ".\\Comparison\\depth-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\oldlogo.png", ".\\ExpectedOutput\\rast-oldlogo.png", ".\\Comparison\\elements-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\sRGB.png", ".\\ExpectedOutput\\rast-sRGB.png", ".\\Comparison\\sRGB-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\gammabox.png", ".\\ExpectedOutput\\rast-gammabox.png", ".\\Comparison\\gammabox-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\perspective.png", ".\\ExpectedOutput\\rast-perspective.png", ".\\Comparison\\perspective-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\textures.png", ".\\ExpectedOutput\\rast-textures.png", ".\\Comparison\\textures-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\matrix.png", ".\\ExpectedOutput\\rast-matrix.png", ".\\Comparison\\matrix-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\decals.png", ".\\ExpectedOutput\\rast-decals.png", ".\\Comparison\\decals-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\2d3d.png", ".\\ExpectedOutput\\rast-2d3d.png", ".\\Comparison\\2d3d-diff.png"});
        cmds.add(new String[] {"magick", "compare", ".\\Output\\alpha.png", ".\\ExpectedOutput\\rast-alpha.png", ".\\Comparison\\alpha-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\fsaa2.png", ".\\ExpectedOutput\\rast-fsaa2.png", ".\\Comparison\\fsaa2-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\fsaa8.png", ".\\ExpectedOutput\\rast-fsaa8.png", ".\\Comparison\\fsaa8-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\points1.png", ".\\ExpectedOutput\\rast-points2.png", ".\\Comparison\\points1-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\points2.png", ".\\ExpectedOutput\\rast-points2.png", ".\\Comparison\\points2-diff.png"});
        cmds.add(new String[] {"magick", "compare", "-fuzz", "2%" , ".\\Output\\cull.png", ".\\ExpectedOutput\\rast-cull.png", ".\\Comparison\\cull-diff.png"});
        

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
                                                    System.out.println("error: " + code);
                                                    System.out.println(Arrays.toString(cmd));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                );
            thread.start();
        }
    }
}
