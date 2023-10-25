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
    public static RGB combine(RGB src, RGB dest, double ap) {
        double m = src.A / ap;
        double n = (1 - src.A) * dest.A / ap;
        return new RGB(m * src.R + n * dest.R, m * src.G + n * dest.G, m * src.B + n * dest.B, ap);
    }
}
