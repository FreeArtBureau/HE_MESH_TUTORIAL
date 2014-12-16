/**
 * 
 */
package wblut.geom;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RBezierSurface extends WB_BezierSurface {
	private final double[][]		weights;
	protected WB_Homogeneous[][]	wpoints;

	public WB_RBezierSurface(final WB_Point3d[][] controlPoints) {
		super(controlPoints);
		weights = new double[n + 1][m + 1];
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= m; j++) {
				weights[i][j] = 1.0;
			}
		}
		wpoints = new WB_Homogeneous[n + 1][m + 1];
		for (int i = 0; i < n + 1; i++) {
			for (int j = 0; j < m + 1; j++) {
				wpoints[i][j] = new WB_Homogeneous(points[i][j], weights[i][j]);
			}
		}

	}

	public WB_RBezierSurface(final WB_Point3d[][] controlPoints,
			final double[][] weights) {
		super(controlPoints);
		this.weights = weights;

		wpoints = new WB_Homogeneous[n + 1][m + 1];
		for (int i = 0; i < n + 1; i++) {
			for (int j = 0; j < m + 1; j++) {
				wpoints[i][j] = new WB_Homogeneous(points[i][j], weights[i][j]);
			}
		}

	}

	public WB_RBezierSurface(final WB_Homogeneous[][] controlPoints) {
		super(controlPoints);
		weights = new double[n + 1][m + 1];
		wpoints = new WB_Homogeneous[n + 1][m + 1];
		for (int i = 0; i < n + 1; i++) {
			for (int j = 0; j < m + 1; j++) {
				wpoints[i][j] = new WB_Homogeneous(controlPoints[i][j]);
				weights[i][j] = controlPoints[i][j].w;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#surfacePoint(double, double)
	 */
	@Override
	public WB_Point3d surfacePoint(final double u, final double v) {
		final WB_Homogeneous S = new WB_Homogeneous();
		if (n <= m) {
			final WB_Homogeneous[] Q = new WB_Homogeneous[m + 1];
			double[] B;
			for (int j = 0; j <= m; j++) {
				B = WB_Bezier.allBernstein(u, n);
				Q[j] = new WB_Homogeneous();
				for (int k = 0; k <= n; k++) {
					Q[j].add(wpoints[k][j], B[k]);
				}
			}
			B = WB_Bezier.allBernstein(v, m);
			for (int k = 0; k <= m; k++) {
				S.add(Q[k], B[k]);
			}
		} else {
			final WB_Homogeneous[] Q = new WB_Homogeneous[n + 1];
			double[] B;
			for (int i = 0; i <= n; i++) {
				B = WB_Bezier.allBernstein(v, m);
				Q[i] = new WB_Homogeneous();
				for (int k = 0; k <= m; k++) {
					Q[i].add(wpoints[i][k], B[k]);
				}
			}
			B = WB_Bezier.allBernstein(u, n);
			for (int k = 0; k <= n; k++) {
				S.add(Q[k], B[k]);
			}

		}

		return new WB_Point3d(S.project());

	}

	@Override
	public WB_Point3d[][] points() {
		return points;
	}

	@Override
	public int n() {
		return n;
	}

	@Override
	public int m() {
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	@Override
	public double loweru() {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	@Override
	public double upperu() {

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	@Override
	public double lowerv() {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	@Override
	public double upperv() {

		return 1;
	}

	@Override
	public WB_RBezierSurface elevateUDegree() {
		final WB_Homogeneous[][] npoints = new WB_Homogeneous[n + 2][m + 1];
		for (int j = 0; j <= m; j++) {
			npoints[0][j] = wpoints[0][j];
			npoints[n + 1][j] = wpoints[n][j];
			final double inp = 1.0 / (n + 1);
			for (int i = 1; i <= n; i++) {
				npoints[i][j] = WB_Homogeneous.interpolate(wpoints[i][j],
						wpoints[i - 1][j], i * inp);

			}
		}
		return new WB_RBezierSurface(npoints);

	}

	@Override
	public WB_RBezierSurface elevateVDegree() {
		final WB_Homogeneous[][] npoints = new WB_Homogeneous[n + 1][m + 2];
		for (int i = 0; i <= n; i++) {
			npoints[i][0] = wpoints[i][0];
			npoints[i][m + 1] = wpoints[i][m];
			final double inp = 1.0 / (n + 1);
			for (int j = 1; j <= m; j++) {
				npoints[i][j] = WB_Homogeneous.interpolate(wpoints[i][j],
						wpoints[i][j - 1], j * inp);

			}
		}
		return new WB_RBezierSurface(npoints);

	}

}