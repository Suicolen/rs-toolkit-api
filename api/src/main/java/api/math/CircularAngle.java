package api.math;

public class CircularAngle {

    public static final double UNIT = Math.PI / 1024.0; // How much of the circle each unit of SINE/COSINE is

    public static final int[] SINE = new int[2048];
    public static final int[] COSINE = new int[2048];

    static {
        for (int i = 0; i < 2048; i++) {
            SINE[i] = (int) (65536.0D * Math.sin((double) i * UNIT));
            COSINE[i] = (int) (65536.0D * Math.cos((double) i * UNIT));
        }
    }

}
