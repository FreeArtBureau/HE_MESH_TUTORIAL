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
public class WB_BSplineSurface implements WB_Surface {
	protected WB_NurbsKnot		uknot;
	protected WB_NurbsKnot		vknot;
	protected WB_Point3d[][]	points;
	protected int			p;
	protected int			n;
	protected int			q;
	protected int			m;

	public WB_BSplineSurface() {

	}

	public WB_BSplineSurface(final WB_Point3d[][] controlPoints,
			final WB_NurbsKnot uknot, final WB_NurbsKnot vknot) {
		if (uknot.n != controlPoints.length - 1) {
			throw new IllegalArgumentException(
					"U knot size and/or degree doesn't match number of control points.");
		}
		if (vknot.n != controlPoints[0].length - 1) {
			throw new IllegalArgumentException(
					"V knot size and/or degree doesn't match number of control points.");
		}
		p = uknot.p();
		n = uknot.n();
		q = vknot.p();
		m = vknot.n();
		this.uknot = uknot;
		this.vknot = vknot;

		points = controlPoints;

	}

	public WB_BSplineSurface(final WB_Homogeneous[][] controlPoints,
			final WB_NurbsKnot uknot, final WB_NurbsKnot vknot) {
		if (uknot.n != controlPoints.length - 1) {
			throw new IllegalArgumentException(
					"U knot size and/or degree doesn't match number of control points.");
		}
		if (vknot.n != controlPoints[0].length - 1) {
			throw new IllegalArgumentException(
					"V knot size and/or degree doesn't match number of control points.");
		}
		p = uknot.p();
		n = uknot.n();
		q = vknot.p();
		m = vknot.n();
		this.uknot = uknot;
		this.vknot = vknot;
		points = new WB_Point3d[n + 1][m + 1];
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= m; j++) {
				points[i][j] = controlPoints[i][j].project();
			}
		}

	}

	public WB_BSplineSurface(final WB_Point3d[][] controlPoints,
			final int udegree, final int vdegree) {
		uknot = new WB_NurbsKnot(controlPoints.length, udegree);
		vknot = new WB_NurbsKnot(controlPoints[0].length, vdegree);
		p = uknot.p();
		n = uknot.n();
		q = vknot.p();
		m = vknot.n();
		points = controlPoints;
	}

	public WB_BSplineSurface(final WB_Point3d point00, final WB_Point3d point10,
			final WB_Point3d point01, final WB_Point3d point11) {
		uknot = new WB_NurbsKnot(2, 1);
		vknot = new WB_NurbsKnot(2, 1);
		p = uknot.p();
		n = uknot.n();
		q = vknot.p();
		m = vknot.n();
		points = new WB_Point3d[2][2];
		points[0][0] = point00;
		points[0][1] = point01;
		points[1][0] = point10;
		points[1][1] = point11;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#surfacePoint(double, double)
	 */
	public WB_Point3d surfacePoint(final double u, final double v) {

		final int uspan = uknot.span(u);
		final double[] Nu = uknot.basisFunctions(uspan, u);
		final int vspan = vknot.span(v);
		final double[] Nv = vknot.basisFunctions(vspan, v);
		final int uind = uspan - p;
		final WB_Point3d S = new WB_Point3d();
		WB_Point3d tmp;
		for (int el = 0; el <= q; el++) {
			tmp = new WB_Point3d();
			final int vind = vspan - q + el;
			for (int k = 0; k <= p; k++) {
				tmp.add(Nu[k] * points[uind + k][vind].x, Nu[k]
						* points[uind + k][vind].y, Nu[k]
						* points[uind + k][vind].z);

			}
			S.add(tmp.mult(Nv[el]));
		}

		return S;

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

	public WB_BSplineSurface insertUKnot(final double u) {
		return insertUKnot(u, 1);
	}

	public WB_BSplineSurface insertUKnotMax(final double u) {
		final int k = uknot.multiplicity(u);
		return insertUKnot(u, p - k);
	}

	public WB_BSplineSurface insertUKnot(final double u, final int r) {

		final int nq = n + r;
		final int k = uknot.span(u);
		final int s = uknot.multiplicity(u, k);
		if (r + s > p) {
			throw new IllegalArgumentException(
					"Attempting to increase knot multiplicity above curve degree.");
		}
		final WB_NurbsKnot UQ = new WB_NurbsKnot(n + 1 + r, p);
		for (int i = 0; i <= k; i++) {
			UQ.setValue(i, uknot.value(i));
		}
		for (int i = 1; i <= r; i++) {
			UQ.setValue(k + i, u);
		}
		for (int i = k + 1; i <= n + p + 1; i++) {
			UQ.setValue(i + r, uknot.value(i));
		}

		int L = 0;
		final double[][] alpha = new double[p - s + 1][r + 1];
		for (int j = 1; j <= r; j++) {
			L = k - p + j;
			for (int i = 0; i <= p - j - s; i++) {
				alpha[i][j] = (u - uknot.value(L + i))
						/ (uknot.value(i + k + 1) - uknot.value(L + i));
			}
		}
		final WB_Point3d[][] Q = new WB_Point3d[nq + 1][m + 1];
		final WB_Point3d[] RW = new WB_Point3d[p - s + 1];
		for (int row = 0; row <= m; row++) {
			for (int i = 0; i <= k - p; i++) {
				Q[i][row] = new WB_Point3d(points[i][row]);
			}
			for (int i = k - s; i <= n; i++) {
				Q[i + r][row] = new WB_Point3d(points[i][row]);
			}
			for (int i = 0; i <= p - s; i++) {
				RW[i] = new WB_Point3d(points[k - p + i][row]);
			}
			for (int j = 1; j <= r; j++) {
				L = k - p + j;
				for (int i = 0; i <= p - j - s; i++) {
					RW[i] = WB_Point3d.interpolate(RW[i], RW[i + 1], alpha[i][j]);
				}
				Q[L][row] = RW[0];
				Q[k + r - j - s][row] = RW[p - j - s];
			}
			for (int i = L + 1; i < k - s; i++) {
				Q[i][row] = RW[i - L];
			}

		}

		return new WB_BSplineSurface(Q, UQ, vknot);
	}

	public WB_BSplineSurface insertVKnot(final double v) {
		return insertVKnot(v, 1);
	}

	public WB_BSplineSurface insertVKnotMax(final double v) {
		final int k = vknot.multiplicity(v);
		return insertVKnot(v, q - k);
	}

	public WB_BSplineSurface insertVKnot(final double v, final int r) {

		final int mq = m + r;
		final int k = vknot.span(v);
		final int s = vknot.multiplicity(v, k);
		if (r + s > q) {
			throw new IllegalArgumentException(
					"Attempting to increase knot multiplicity above curve degree.");
		}
		final WB_NurbsKnot VQ = new WB_NurbsKnot(m + 1 + r, q);
		for (int i = 0; i <= k; i++) {
			VQ.setValue(i, vknot.value(i));
		}
		for (int i = 1; i <= r; i++) {
			VQ.setValue(k + i, v);
		}
		for (int i = k + 1; i <= m + q + 1; i++) {
			VQ.setValue(i + r, vknot.value(i));
		}

		int L = 0;
		final double[][] alpha = new double[q - s + 1][r + 1];
		for (int j = 1; j <= r; j++) {
			L = k - q + j;
			for (int i = 0; i <= q - j - s; i++) {
				alpha[i][j] = (v - vknot.value(L + i))
						/ (vknot.value(i + k + 1) - vknot.value(L + i));
			}
		}
		final WB_Point3d[][] Q = new WB_Point3d[n + 1][mq + 1];
		final WB_Point3d[] RW = new WB_Point3d[q - s + 1];
		for (int col = 0; col <= n; col++) {
			for (int i = 0; i <= k - q; i++) {
				Q[col][i] = new WB_Point3d(points[col][i]);
			}
			for (int i = k - s; i <= m; i++) {
				Q[col][i + r] = new WB_Point3d(points[col][i]);
			}
			for (int i = 0; i <= q - s; i++) {
				RW[i] = new WB_Point3d(points[col][k - q + i]);
			}
			for (int j = 1; j <= r; j++) {
				L = k - q + j;
				for (int i = 0; i <= q - j - s; i++) {
					RW[i] = WB_Point3d.interpolate(RW[i], RW[i + 1], alpha[i][j]);
				}
				Q[col][L] = RW[0];
				Q[col][k + r - j - s] = RW[q - j - s];
			}
			for (int i = L + 1; i < k - s; i++) {
				Q[col][i] = RW[i - L];
			}

		}

		return new WB_BSplineSurface(Q, uknot, VQ);
	}

	public WB_BSpline isoCurveU(final double u) {

		final WB_Point3d[] cpoints = new WB_Point3d[m + 1];
		final int span = uknot.span(u);
		double[] N;
		for (int j = 0; j <= m; j++) {

			N = uknot.basisFunctions(span, u);
			cpoints[j] = new WB_Point3d();
			for (int i = 0; i <= p; i++) {
				final WB_Point3d tmp = points[span - p + i][j];
				cpoints[j].add(N[i] * tmp.x, N[i] * tmp.y, N[i] * tmp.z);
			}
		}
		return new WB_BSpline(cpoints, vknot);

	}

	public WB_BSpline isoCurveV(final double v) {
		final WB_Point3d[] cpoints = new WB_Point3d[n + 1];
		final int span = vknot.span(v);
		double[] N;
		for (int i = 0; i <= n; i++) {

			N = vknot.basisFunctions(span, v);
			cpoints[i] = new WB_Point3d();
			for (int j = 0; j <= q; j++) {
				final WB_Point3d tmp = points[i][span - q + j];
				cpoints[i].add(N[j] * tmp.x, N[j] * tmp.y, N[j] * tmp.z);
			}
		}
		return new WB_BSpline(cpoints, uknot);
	}

	public WB_Point3d[][] points() {
		return points;
	}

	public int p() {
		return p;
	}

	public int n() {
		return n;
	}

	public int q() {
		return q;
	}

	public int m() {
		return m;
	}

	public WB_NurbsKnot uknot() {
		return uknot;
	}

	public WB_NurbsKnot vknot() {
		return vknot;
	}

	public WB_BSplineSurface[] splitU(final double u) {

		final WB_BSplineSurface newBSplineSurface = insertUKnotMax(u);
		final int k = newBSplineSurface.uknot().span(u);
		final int km = newBSplineSurface.uknot().m;
		final WB_NurbsKnot knot1 = new WB_NurbsKnot(k + 1 - p, p);
		for (int i = 0; i < knot1.m; i++) {
			knot1.setValue(i, newBSplineSurface.uknot().value(i));
		}
		knot1.setValue(knot1.m, u);
		knot1.normalize();
		final WB_Point3d[][] points1 = new WB_Point3d[k + 1 - p][m + 1];
		for (int j = 0; j <= m; j++) {
			for (int i = 0; i < k + 1 - p; i++) {
				points1[i][j] = newBSplineSurface.points[i][j];
			}
		}
		final WB_NurbsKnot knot2 = new WB_NurbsKnot(km - k, p);
		for (int i = 0; i <= p; i++) {
			knot2.setValue(i, u);
		}

		for (int i = k + 1; i <= km; i++) {
			knot2.setValue(i - k + p, newBSplineSurface.uknot().value(i));
		}

		knot2.normalize();
		final WB_Point3d[][] points2 = new WB_Point3d[km - k][m + 1];
		for (int j = 0; j <= m; j++) {
			for (int i = 0; i < km - k; i++) {
				points2[i][j] = newBSplineSurface.points[k - p + i][j];
			}
		}
		final WB_BSplineSurface[] splitSurfaces = new WB_BSplineSurface[2];
		splitSurfaces[0] = new WB_BSplineSurface(points1, knot1, vknot);
		splitSurfaces[1] = new WB_BSplineSurface(points2, knot2, vknot);
		return splitSurfaces;
	}

	public WB_BSplineSurface[] splitV(final double v) {

		final WB_BSplineSurface newBSplineSurface = insertVKnotMax(v);
		final int k = newBSplineSurface.vknot().span(v);
		final int km = newBSplineSurface.vknot().m;
		final WB_NurbsKnot knot1 = new WB_NurbsKnot(k + 1 - q, q);
		for (int i = 0; i < knot1.m; i++) {
			knot1.setValue(i, newBSplineSurface.vknot().value(i));
		}
		knot1.setValue(knot1.m, v);
		knot1.normalize();
		final WB_Point3d[][] points1 = new WB_Point3d[n + 1][k + 1 - q];
		for (int j = 0; j <= n; j++) {
			for (int i = 0; i < k + 1 - q; i++) {
				points1[j][i] = newBSplineSurface.points[j][i];
			}
		}
		final WB_NurbsKnot knot2 = new WB_NurbsKnot(km - k, q);
		for (int i = 0; i <= q; i++) {
			knot2.setValue(i, v);
		}

		for (int i = k + 1; i <= km; i++) {
			knot2.setValue(i - k + q, newBSplineSurface.vknot().value(i));
		}

		knot2.normalize();
		final WB_Point3d[][] points2 = new WB_Point3d[n + 1][km - k];
		for (int j = 0; j <= n; j++) {
			for (int i = 0; i < km - k; i++) {
				points2[j][i] = newBSplineSurface.points[j][k - q + i];
			}
		}
		final WB_BSplineSurface[] splitSurfaces = new WB_BSplineSurface[2];
		splitSurfaces[0] = new WB_BSplineSurface(points1, uknot, knot1);
		splitSurfaces[1] = new WB_BSplineSurface(points2, uknot, knot2);
		return splitSurfaces;
	}

	public WB_BSplineSurface[] split(final double u, final double v) {
		final WB_BSplineSurface[] splitSurfaces = new WB_BSplineSurface[4];
		WB_BSplineSurface[] tmp = new WB_BSplineSurface[2];
		tmp = splitU(u);
		splitSurfaces[0] = tmp[0];
		splitSurfaces[2] = tmp[1];
		tmp = splitSurfaces[0].splitV(v);
		splitSurfaces[0] = tmp[0];
		splitSurfaces[1] = tmp[1];
		tmp = splitSurfaces[2].splitV(v);
		splitSurfaces[2] = tmp[0];
		splitSurfaces[3] = tmp[1];
		return splitSurfaces;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	public double loweru() {

		return uknot.value(0);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	public double upperu() {

		return uknot.value(uknot.m);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	public double lowerv() {

		return vknot.value(0);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	public double upperv() {

		return vknot.value(vknot.m);
	}

}
