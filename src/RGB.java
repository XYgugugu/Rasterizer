public class RGB {
    public double R, G, B, A;
    RGB(double r, double g, double b, double a, boolean hyp, double wp, Drawer d) {
        A = a;
        if (hyp) {
            R = d.GammaCorection(r / wp);
            G = d.GammaCorection(g / wp);
            B = d.GammaCorection(b / wp);
        } else {
            R = d.GammaCorection(r);
            G = d.GammaCorection(g);
            B = d.GammaCorection(b);
        }
    }
    RGB(double r, double g, double b, double a) {
        R = r;
        G = g;
        B = b;
        A = a;
    }
    public static RGB combine(RGB src, RGB dest, Drawer d) {
        double ap = src.A + dest.A * (1 - src.A);

        double sR = d.ReverseGammaCorection(src.R);
        double sG = d.ReverseGammaCorection(src.G);
        double sB = d.ReverseGammaCorection(src.B);
        
        double dR = d.ReverseGammaCorection(dest.R);
        double dG = d.ReverseGammaCorection(dest.G);
        double dB = d.ReverseGammaCorection(dest.B);
        
        double m = src.A / ap;
        double n = (1 - src.A) * dest.A / ap;
        return new RGB(d.GammaCorection(m * sR + n * dR), d.GammaCorection(m * sG + n * dG), d.GammaCorection(m * sB + n * dB), ap);
    }
}
