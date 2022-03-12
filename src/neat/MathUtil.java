package neat;

public class MathUtil {

	public static float sigmoid(double x) {
		return (float) (1 / (1 + Math.pow(Math.E, (-1 * x))));
	}
}
