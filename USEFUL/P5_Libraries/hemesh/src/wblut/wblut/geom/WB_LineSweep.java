/**
 * 
 */
package wblut.geom;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_LineSweep {
	public static WB_BSplineSurface getLineSweep(final WB_BSpline C,
			final WB_Vector3d v, final double f) {
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final WB_Point3d[][] points = new WB_Point3d[C.n() + 1][2];
		for (int i = 0; i <= C.n(); i++) {
			points[i][0] = new WB_Point3d(C.points()[i]);
			points[i][1] = points[i][0].addAndCopy(v, f);
		}
		return new WB_BSplineSurface(points, C.knot(), VKnot);

	}

	public static WB_RBSplineSurface getLineSweep(final WB_RBSpline C,
			final WB_Vector3d v, final double f) {
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final WB_Point3d[][] points = new WB_Point3d[C.n() + 1][2];
		final double[][] weights = new double[C.n() + 1][2];
		for (int i = 0; i <= C.n(); i++) {
			points[i][0] = new WB_Point3d(C.points()[i]);
			points[i][1] = new WB_Point3d(C.points()[i].addAndCopy(v, f));
			weights[i][0] = C.weights()[i];
			weights[i][1] = weights[i][0];
		}
		return new WB_RBSplineSurface(points, C.knot(), VKnot, weights);

	}
}
