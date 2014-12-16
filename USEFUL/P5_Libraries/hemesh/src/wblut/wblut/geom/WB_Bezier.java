/**
 * 
 */
package wblut.geom;


/**
 * 
 * 
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Bezier implements WB_Curve {

	protected WB_Point3d[]	points;
	protected int			n;

	public WB_Bezier(final WB_Point3d[] controlPoints) {
		points = controlPoints;
		n = points.length - 1;

	}

	public WB_Bezier(final WB_Homogeneous[] controlPoints) {
		n = controlPoints.length - 1;
		points = new WB_Point3d[n + 1];
		for (int i = 0; i < n + 1; i++) {
			points[i] = new WB_Point3d(controlPoints[i].project());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#curvePoint(double)
	 */
	public WB_Point3d curvePoint(final double u) {
		final double[] B = allBernstein(u);
		final WB_Point3d C = new WB_Point3d();
		for (int k = 0; k <= n; k++) {
			C.add(points[k], B[k]);
		}
		return C;
	}

	protected double[] allBernstein(final double u) {
		final double[] B = new double[n + 1];
		B[0] = 1.0;
		final double u1 = 1.0 - u;
		double saved, temp;
		;
		for (int j = 1; j <= n; j++) {
			saved = 0.0;
			for (int k = 0; k < j; k++) {
				temp = B[k];
				B[k] = saved + u1 * temp;
				saved = u * temp;
			}
			B[j] = saved;
		}

		return B;
	}

	protected static double[] allBernstein(final double u, final int n) {
		final double[] B = new double[n + 1];
		B[0] = 1.0;
		final double u1 = 1.0 - u;
		double saved, temp;
		;
		for (int j = 1; j <= n; j++) {
			saved = 0.0;
			for (int k = 0; k < j; k++) {
				temp = B[k];
				B[k] = saved + u1 * temp;
				saved = u * temp;
			}
			B[j] = saved;
		}

		return B;
	}

	public double n() {

		return n;
	}

	public WB_Point3d[] points() {

		return points;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	public double loweru() {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	public double upperu() {

		return 1;
	}

	public WB_Bezier elevateDegree() {

		final WB_Point3d[] npoints = new WB_Point3d[n + 2];
		npoints[0] = points[0];
		npoints[n + 1] = points[n];
		final double inp = 1.0 / (n + 1);
		for (int i = 1; i <= n; i++) {
			npoints[i] = WB_Point3d
					.interpolate(points[i], points[i - 1], i * inp);

		}
		return new WB_Bezier(npoints);

	}

}
