/**
 * 
 */
package wblut.geom;

import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HE_Mesh;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_BezierSurface implements WB_Surface {
	protected WB_Point3d[][]	points;
	protected int			n;
	protected int			m;

	public WB_BezierSurface() {

	}

	public WB_BezierSurface(final WB_Point3d[][] controlPoints) {
		n = controlPoints.length - 1;
		m = controlPoints[0].length - 1;
		points = controlPoints;

	}

	public WB_BezierSurface(final WB_Homogeneous[][] controlPoints) {
		n = controlPoints.length;
		m = controlPoints[0].length;
		points = new WB_Point3d[n + 1][m + 1];
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= m; j++) {
				points[i][j] = controlPoints[i][j].project();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#surfacePoint(double, double)
	 */
	public WB_Point3d surfacePoint(final double u, final double v) {
		final WB_Point3d S = new WB_Point3d();
		if (n <= m) {
			final WB_Point3d[] Q = new WB_Point3d[m + 1];
			double[] B;
			for (int j = 0; j <= m; j++) {
				B = WB_Bezier.allBernstein(u, n);
				Q[j] = new WB_Point3d();
				for (int k = 0; k <= n; k++) {
					Q[j].add(points[k][j], B[k]);
				}
			}
			B = WB_Bezier.allBernstein(v, m);
			for (int k = 0; k <= m; k++) {
				S.add(Q[k], B[k]);
			}
		} else {
			final WB_Point3d[] Q = new WB_Point3d[n + 1];
			double[] B;
			for (int i = 0; i <= n; i++) {
				B = WB_Bezier.allBernstein(v, m);
				Q[i] = new WB_Point3d();
				for (int k = 0; k <= m; k++) {
					Q[i].add(points[i][k], B[k]);
				}
			}
			B = WB_Bezier.allBernstein(u, n);
			for (int k = 0; k <= n; k++) {
				S.add(Q[k], B[k]);
			}

		}

		return S;

	}

	public WB_Point3d[][] points() {
		return points;
	}

	public int n() {
		return n;
	}

	public int m() {
		return m;
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

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	public double lowerv() {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	public double upperv() {

		return 1;
	}

	public WB_BezierSurface elevateUDegree() {
		final WB_Point3d[][] npoints = new WB_Point3d[n + 2][m + 1];
		for (int j = 0; j <= m; j++) {
			npoints[0][j] = points[0][j];
			npoints[n + 1][j] = points[n][j];
			final double inp = 1.0 / (n + 1);
			for (int i = 1; i <= n; i++) {
				npoints[i][j] = WB_Point3d.interpolate(points[i][j],
						points[i - 1][j], i * inp);

			}
		}
		return new WB_BezierSurface(npoints);

	}

	public WB_BezierSurface elevateVDegree() {
		final WB_Point3d[][] npoints = new WB_Point3d[n + 1][m + 2];
		for (int i = 0; i <= n; i++) {

			npoints[i][0] = points[i][0];
			npoints[i][m + 1] = points[i][m];
			final double inp = 1.0 / (n + 1);
			for (int j = 1; j <= m; j++) {
				npoints[i][j] = WB_Point3d.interpolate(points[i][j],
						points[i][j - 1], j * inp);

			}
		}
		return new WB_BezierSurface(npoints);

	}

	public HE_Mesh toControlHemesh() {
		final WB_Point3d[] cpoints = new WB_Point3d[(n + 1) * (m + 1)];
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= m; j++) {
				cpoints[i + (n + 1) * j] = points[i][j];
			}
		}
		final int[][] faces = new int[n * m][4];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				faces[i + n * j][0] = i + (n + 1) * j;
				faces[i + n * j][1] = i + 1 + (n + 1) * j;
				faces[i + n * j][2] = i + 1 + (n + 1) * (j + 1);
				faces[i + n * j][3] = i + (n + 1) * (j + 1);
			}
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setFaces(faces).setVertices(cpoints);
		return new HE_Mesh(fl);
	}

}
