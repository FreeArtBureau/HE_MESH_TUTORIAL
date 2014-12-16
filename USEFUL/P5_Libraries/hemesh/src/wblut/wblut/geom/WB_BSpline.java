/**
 * 
 */
package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_Binomial;

/**
 * 
 * 
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_BSpline implements WB_Curve {

	protected WB_NurbsKnot		knot;
	protected WB_Point3d[]	points;
	protected int			p;
	protected int			n;

	public WB_BSpline() {

	}

	public WB_BSpline(final WB_Point3d[] controlPoints, final WB_NurbsKnot knot) {
		if (knot.n != controlPoints.length - 1) {
			throw new IllegalArgumentException(
					"Knot size and/or order doesn't match number of control points.");
		}
		p = knot.p();
		n = knot.n();
		this.knot = knot;
		points = controlPoints;

	}

	public WB_BSpline(final WB_Homogeneous[] controlPoints, final WB_NurbsKnot knot) {
		if (knot.n != controlPoints.length - 1) {
			throw new IllegalArgumentException(
					"Knot size and/or order doesn't match number of control points.");
		}
		p = knot.p();
		n = knot.n();
		this.knot = knot;
		points = new WB_Point3d[n + 1];
		for (int i = 0; i < n + 1; i++) {
			points[i] = new WB_Point3d(controlPoints[i].project());
		}

	}

	public WB_BSpline(final WB_Point3d[] controlPoints, final int order) {
		knot = new WB_NurbsKnot(controlPoints.length, order);
		p = knot.p();
		n = knot.n();
		points = controlPoints;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#curvePoint(double)
	 */
	public WB_Point3d curvePoint(final double u) {
		final int span = knot.span(u);
		final double[] N = knot.basisFunctions(span, u);
		final WB_Point3d C = new WB_Point3d();
		final int p = knot.p();
		for (int i = 0; i <= p; i++) {
			final WB_Point3d tmp = points[span - p + i];
			C.add(N[i] * tmp.x, N[i] * tmp.y, N[i] * tmp.z);
		}
		return C;
	}

	public WB_BSpline insertKnot(final double u) {
		return insertKnot(u, 1);
	}

	public WB_BSpline insertKnotMax(final double u) {
		final int k = knot.multiplicity(u);
		return insertKnot(u, p - k);
	}

	public WB_BSpline insertKnot(final double u, final int r) {

		final int mp = n + p + 1;
		final int nq = n + r;
		final int k = knot.span(u);
		final int s = knot.multiplicity(u, k);
		if (r + s > p) {
			throw new IllegalArgumentException(
					"Attempting to increase knot multiplicity above curve order.");
		}
		final WB_NurbsKnot UQ = new WB_NurbsKnot(n + 1 + r, p);
		final WB_Point3d[] Q = new WB_Point3d[nq + 1];
		for (int i = 0; i <= k; i++) {
			UQ.setValue(i, knot.value(i));
		}
		for (int i = 1; i <= r; i++) {
			UQ.setValue(k + i, u);
		}
		for (int i = k + 1; i <= mp; i++) {
			UQ.setValue(i + r, knot.value(i));
		}
		for (int i = 0; i <= k - p; i++) {
			Q[i] = points[i];
		}
		for (int i = k - s; i <= n; i++) {
			Q[i + r] = points[i];
		}
		final WB_Point3d[] RW = new WB_Point3d[p + 1];
		for (int i = 0; i <= p - s; i++) {
			RW[i] = new WB_Point3d(points[k - p + i]);
		}
		int L = 0;
		for (int j = 1; j <= r; j++) {
			L = k - p + j;
			for (int i = 0; i <= p - j - s; i++) {
				final double alpha = (u - knot.value(L + i))
						/ (knot.value(i + k + 1) - knot.value(L + i));
				RW[i] = WB_Point3d.interpolate(RW[i], RW[i + 1], alpha);
			}
			Q[L] = RW[0];
			Q[k + r - j - s] = RW[p - j - s];

		}
		for (int i = L + 1; i < k - s; i++) {

			Q[i] = RW[i - L];
		}
		return new WB_BSpline(Q, UQ);
	}

	public WB_BSpline refineKnot(final WB_NurbsKnot K) {

		return refineKnot(K.values());

	}

	public WB_BSpline refineKnot(final double[] X) {

		final int r = X.length - 1;

		final double[] lX = new double[r + 1];

		for (int i = 0; i < r; i++) {
			if (X[i] > X[i + 1]) {
				throw new IllegalArgumentException(
						"Provided values are not non-decreasing.");
			}

		}
		int id = 0;
		int rt = 0;// how many times has this knot value already appeared in X
		double pv = Double.NaN;// what was the previous knot value in X
		for (int i = 0; i <= r; i++) {
			if (X[i] == pv) {
				rt++;
			} else {
				rt = 0;
				pv = X[i];
			}
			final int tmp = knot.multiplicity(X[i]);
			if (tmp < (p - rt)) {
				lX[id] = X[i];
				id++;
			}
		}
		if (id == 0) {
			return this;
		}
		final double[] fX = new double[id];
		for (int i = 0; i < id; i++) {
			fX[i] = lX[i];

		}
		return refineKnotRestricted(fX);

	}

	private WB_BSpline refineKnotRestricted(final double[] X) {
		final int r = X.length - 1;
		final int a = knot.span(X[0]);
		final int b = knot.span(X[r]) + 1;
		final WB_Point3d[] Q = new WB_Point3d[n + r + 2];
		final WB_NurbsKnot Ubar = new WB_NurbsKnot(n + r + 2, p);
		for (int j = 0; j <= a - p; j++) {
			Q[j] = new WB_Point3d(points[j]);
		}
		for (int j = b - 1; j <= n; j++) {
			Q[j + r + 1] = new WB_Point3d(points[j]);
		}
		for (int j = 0; j <= a; j++) {
			Ubar.setValue(j, knot.value(j));
		}
		for (int j = b + p; j <= knot.m; j++) {
			Ubar.setValue(j + r + 1, knot.value(j));
		}
		int i = b + p - 1;
		int k = b + p + r;
		for (int j = r; j >= 0; j--) {
			while ((X[j] <= knot.value(i)) && (i > a)) {
				Q[k - p - 1] = new WB_Point3d(points[i - p - 1]);
				Ubar.setValue(k, knot.value(i));
				k = k - 1;
				i = i - 1;
			}
			Q[k - p - 1] = new WB_Point3d(Q[k - p]);
			for (int el = 1; el <= p; el++) {
				final int ind = k - p + el;
				double alpha = Ubar.value(k + el) - X[j];
				if (WB_Epsilon.isZero(alpha)) {
					Q[ind - 1] = new WB_Point3d(Q[ind]);
				} else {
					alpha /= (Ubar.value(k + el) - knot.value(i - p + el));
					Q[ind - 1] = WB_Point3d
							.interpolate(Q[ind], Q[ind - 1], alpha);
				}
			}
			Ubar.setValue(k, X[j]);
			k--;
		}
		return new WB_BSpline(Q, Ubar);

	}

	public WB_Point3d[] points() {
		return points;
	}

	public int p() {
		return p;
	}

	public int n() {
		return n;
	}

	public WB_NurbsKnot knot() {
		return knot;
	}

	public WB_BSpline[] split(final double u) {

		final WB_BSpline newBSpline = insertKnotMax(u);
		final int k = newBSpline.knot().span(u);
		final int m = newBSpline.knot().m;
		final WB_NurbsKnot knot1 = new WB_NurbsKnot(k + 1 - p, p);
		for (int i = 0; i < knot1.m; i++) {
			knot1.setValue(i, newBSpline.knot().value(i));
		}
		knot1.setValue(knot1.m, u);
		knot1.normalize();
		final WB_Point3d[] points1 = new WB_Point3d[k + 1 - p];
		for (int i = 0; i < k + 1 - p; i++) {
			points1[i] = newBSpline.points[i];
		}
		final WB_NurbsKnot knot2 = new WB_NurbsKnot(m - k, p);
		for (int i = 0; i <= p; i++) {
			knot2.setValue(i, u);
		}
		for (int i = k + 1; i <= m; i++) {
			knot2.setValue(i - k + p, newBSpline.knot().value(i));
		}
		knot2.normalize();
		final WB_Point3d[] points2 = new WB_Point3d[m - k];
		for (int i = 0; i < m - k; i++) {
			points2[i] = newBSpline.points[k - p + i];
		}
		final WB_BSpline[] splitCurves = new WB_BSpline[2];
		splitCurves[0] = new WB_BSpline(points1, knot1);
		splitCurves[1] = new WB_BSpline(points2, knot2);
		return splitCurves;
	}

	public WB_BSpline elevateDegree(final int t) {
		final int m = n + p + 1;
		final int ph = p + t;
		final int ph2 = ph / 2;
		final double[][] bezalfs = new double[p + t + 1][p + 1];
		final WB_Point3d[] bpts = new WB_Point3d[p + 1];
		final WB_Point3d[] ebpts = new WB_Point3d[p + t + 1];
		final WB_Point3d[] nextbpts = new WB_Point3d[p - 1];
		final double[] alfs = new double[p - 1];
		bezalfs[0][0] = bezalfs[ph][p] = 1.0;
		int mpi;
		for (int i = 1; i <= ph2; i++) {
			final double inv = 1.0 / WB_Binomial.bin(ph, i);
			mpi = Math.min(p, i);
			for (int j = Math.max(0, i - t); j <= mpi; j++) {
				bezalfs[i][j] = inv * WB_Binomial.bin(p, j)
						* WB_Binomial.bin(t, i - j);
			}
		}

		for (int i = ph2 + 1; i <= ph - 1; i++) {
			mpi = Math.min(p, i);
			for (int j = Math.max(0, i - t); j <= mpi; j++) {
				bezalfs[i][j] = bezalfs[ph - i][p - j];
			}
		}
		int mh = ph;
		int kind = ph + 1;
		int r = -1;
		int a = p;
		int b = p + 1;
		int cind = 1;
		int i = 0;
		int j = 0;
		int k = 0;
		int lbz, rbz, oldr;
		double ua = knot.value(0);
		final int s = knot.s();
		final WB_Point3d[] Q = new WB_Point3d[n + t * (s + 1) + 1];
		Q[0] = new WB_Point3d(points[0]);
		final WB_NurbsKnot Uh = new WB_NurbsKnot(n + t * (s + 1) + 1, ph);
		for (i = 0; i <= ph; i++) {
			Uh.setValue(i, ua);
		}
		for (i = 0; i <= p; i++) {
			bpts[i] = new WB_Point3d(points[i]);
		}
		while (b < m) {
			i = b;
			while ((b < m) && (knot.value(b) == knot.value(b + 1))) {
				b++;
			}
			final int mul = b - i + 1;
			mh += mul + t;
			final double ub = knot.value(b);
			oldr = r;
			r = p - mul;
			if (oldr > 0) {
				lbz = (oldr + 2) / 2;
			} else {
				lbz = 1;
			}
			if (r > 0) {
				rbz = ph - (r + 1) / 2;
			} else {
				rbz = ph;
			}
			if (r > 0) {
				final double numer = ub - ua;
				for (k = p; k > mul; k--) {
					alfs[k - mul - 1] = numer / (knot.value(a + k) - ua);
				}
				for (j = 1; j <= r; j++) {
					final int save = r - j;
					final int sj = mul + j;
					for (k = p; k >= sj; k--) {
						bpts[k] = WB_Point3d.interpolate(bpts[k - 1], bpts[k],
								alfs[k - sj]);
					}
					nextbpts[save] = new WB_Point3d(bpts[p]);
				}
			}

			for (i = lbz; i <= ph; i++) {
				ebpts[i] = new WB_Point3d();
				mpi = Math.min(p, i);
				for (j = Math.max(0, i - t); j <= mpi; j++) {
					ebpts[i].add(bpts[j], bezalfs[i][j]);
				}
			}

			if (oldr > 1) {
				int first = kind - 2;
				int last = kind;
				final double den = ub - ua;
				final double bet = (ub - Uh.value(kind - 1)) / den;
				for (int tr = 1; tr < oldr; tr++) {
					i = first;
					j = last;
					int kj = j - kind + 1;
					while (j - i > tr) {
						if (i < cind) {
							final double alf = (ub - Uh.value(i))
									/ (ua - Uh.value(i));
							Q[i] = WB_Point3d.interpolate(Q[i - 1], Q[i], alf);
						}
						if (j >= lbz) {
							if (j - tr <= kind - ph + oldr) {
								final double gam = (ub - Uh.value(j - tr))
										/ den;
								ebpts[kj] = WB_Point3d.interpolate(ebpts[kj + 1],
										ebpts[kj], gam);
							} else {
								ebpts[kj] = WB_Point3d.interpolate(ebpts[kj + 1],
										ebpts[kj], bet);
							}
						}
						i++;
						j--;
						kj--;
					}
					first--;
					last++;
				}
			}
			if (a != p) {
				for (i = 0; i < ph - oldr; i++) {
					Uh.setValue(kind, ua);
					kind++;
				}
			}
			for (j = lbz; j <= rbz; j++) {
				Q[cind] = new WB_Point3d(ebpts[j]);
				cind++;
			}
			if (b < m) {
				for (j = 0; j < r; j++) {
					bpts[j] = nextbpts[j];
				}
				for (j = r; j <= p; j++) {
					bpts[j] = new WB_Point3d(points[b - p + j]);
				}
				a = b;
				b++;
				ua = ub;

			} else {
				for (i = 0; i <= ph; i++) {
					Uh.setValue(kind + i, ub);
				}
			}
		}

		return new WB_BSpline(Q, Uh);
	}

	public WB_Point3d[][] curveDerivCPoints(final int d, final int r1,
			final int r2) {
		final WB_Point3d[][] PK = new WB_Point3d[d + 1][r2 - r1 + 1];
		final int r = r2 - r1;
		for (int i = 0; i <= r; i++) {
			PK[0][i] = points[r1 + i];
		}
		for (int k = 1; k <= d; k++) {
			final int tmp = p - k + 1;
			for (int i = 0; i <= r - k; i++) {
				PK[k][i] = PK[k - 1][i + 1].subAndCopy(PK[k - 1][i]).mult(
						tmp
								/ (knot.value(r1 + i + p + 1) - knot.value(r1
										+ i + k)));
			}
		}
		return PK;
	}

	public WB_Point3d[] curveDerivs(final double u, final int d) {
		final WB_Point3d[] CK = new WB_Point3d[d + 1];
		final int du = Math.min(d, p);
		for (int k = p + 1; k <= d; k++) {
			CK[k] = new WB_Point3d();
		}
		final int span = knot.span(u);
		final double[][] N = knot.allBasisFunctions(span, u, p);
		final WB_Point3d[][] PK = curveDerivCPoints(du, span - p, span);
		for (int k = 0; k <= du; k++) {
			CK[k] = new WB_Point3d();
			for (int j = 0; j <= p - k; j++) {
				CK[k].add(PK[k][j].multAndCopy(N[j][p - k]));
			}
		}
		return CK;
	}

	public WB_Point3d[] curveDerivsNorm(final double u, final int d) {
		final WB_Point3d[] CK = new WB_Point3d[d + 1];
		final int du = Math.min(d, p);
		for (int k = p + 1; k <= d; k++) {
			CK[k] = new WB_Point3d();
		}
		final int span = knot.span(u);
		final double[][] N = knot.allBasisFunctions(span, u, p);
		final WB_Point3d[][] PK = curveDerivCPoints(du, span - p, span);
		for (int k = 0; k <= du; k++) {
			CK[k] = new WB_Point3d();
			for (int j = 0; j <= p - k; j++) {
				CK[k].add(PK[k][j].multAndCopy(N[j][p - k]));
			}
			CK[k].normalize();
		}
		return CK;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#curveFirstDeriv(double)
	 */
	public WB_Vector3d curveFirstDeriv(final double u) {
		final WB_Vector3d[] CK = new WB_Vector3d[2];
		if (p == 0) {
			return null;
		}
		final int span = knot.span(u);
		final double[][] N = knot.allBasisFunctions(span, u, p);
		final WB_Point3d[][] PK = curveDerivCPoints(1, span - p, span);
		for (int k = 0; k <= 1; k++) {
			CK[k] = new WB_Vector3d();
			for (int j = 0; j <= p - k; j++) {
				CK[k].add(PK[k][j].multAndCopy(N[j][p - k]));
			}
			CK[k].normalize();
		}
		return CK[1];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#loweru()
	 */
	public double loweru() {

		return knot.value(0);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#upperu()
	 */
	public double upperu() {

		return knot.value(knot.m);
	}

}
