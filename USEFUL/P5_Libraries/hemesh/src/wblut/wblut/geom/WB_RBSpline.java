/**
 * 
 */
package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_Binomial;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RBSpline extends WB_BSpline {

	private final double[]		weights;
	protected WB_Homogeneous[]	wpoints;

	public WB_RBSpline(final WB_Point3d[] controlPoints, final WB_NurbsKnot knot) {
		super(controlPoints, knot);

		weights = new double[n + 1];
		wpoints = new WB_Homogeneous[n + 1];
		for (int i = 0; i < n + 1; i++) {
			weights[i] = 1.0;
			wpoints[i] = new WB_Homogeneous(points[i], weights[i]);

		}

	}

	public WB_RBSpline(final WB_Point3d[] controlPoints, final WB_NurbsKnot knot,
			final double[] weights) {
		super(controlPoints, knot);
		if (weights.length != controlPoints.length) {
			throw new IllegalArgumentException(
					"Number of weights doesn't match number of control points.");
		}
		this.weights = weights;
		wpoints = new WB_Homogeneous[n + 1];
		for (int i = 0; i < n + 1; i++) {
			wpoints[i] = new WB_Homogeneous(points[i], weights[i]);

		}

	}

	public WB_RBSpline(final WB_Homogeneous[] controlPoints, final WB_NurbsKnot knot) {
		if (knot.n != controlPoints.length - 1) {
			throw new IllegalArgumentException(
					"Knot size and/or order doesn't match number of control points.");
		}
		p = knot.p();
		n = knot.n();
		this.knot = knot;
		wpoints = controlPoints;
		points = new WB_Point3d[n + 1];
		for (int i = 0; i < n + 1; i++) {
			points[i] = new WB_Point3d(controlPoints[i].project());
		}
		weights = new double[n + 1];
		for (int i = 0; i < n + 1; i++) {
			weights[i] = controlPoints[i].w;
		}

	}

	public WB_RBSpline(final WB_Point3d[] controlPoints, final int order) {
		this(controlPoints, new WB_NurbsKnot(controlPoints.length, order));

	}

	public WB_RBSpline(final WB_Homogeneous[] controlPoints, final int order) {
		this(controlPoints, new WB_NurbsKnot(controlPoints.length, order));

	}

	public WB_RBSpline(final WB_Point3d[] controlPoints, final int order,
			final double[] weights) {
		super(controlPoints, order);
		this.weights = weights;
		wpoints = new WB_Homogeneous[n + 1];
		for (int i = 0; i < n + 1; i++) {
			wpoints[i] = new WB_Homogeneous(points[i], weights[i]);

		}

	}

	public WB_Homogeneous[] wpoints() {
		return wpoints;
	}

	public double[] weights() {
		return weights;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Curve#curvePoint(double)
	 */
	@Override
	public WB_Point3d curvePoint(final double u) {
		final int span = knot.span(u);
		final double[] N = knot.basisFunctions(span, u);
		final WB_Homogeneous CH = new WB_Homogeneous();
		final int p = knot.p();
		for (int i = 0; i <= p; i++) {
			CH.add(wpoints[span - p + i], N[i]);
		}
		return new WB_Point3d(CH.project());
	}

	public void setWeight(final int i, final double w) {
		if ((i < 0) || (i > n)) {
			throw new IllegalArgumentException(
					"Index outside of weights range.");
		}
		weights[i] = w;
		wpoints[i] = new WB_Homogeneous(points[i], w);

	}

	public double getWeight(final int i) {
		if ((i < 0) || (i > n)) {
			throw new IllegalArgumentException(
					"Index outside of weights range.");
		}
		return weights[i];

	}

	public void updateHomogeneous() {
		for (int i = 0; i < n + 1; i++) {
			wpoints[i] = new WB_Homogeneous(points[i], weights[i]);

		}
	}

	@Override
	public WB_RBSpline insertKnot(final double u) {
		return insertKnot(u, 1);
	}

	@Override
	public WB_RBSpline insertKnotMax(final double u) {
		final int k = knot.multiplicity(u);
		return insertKnot(u, p - k);
	}

	@Override
	public WB_RBSpline insertKnot(final double u, final int r) {

		final int mp = n + p + 1;
		final int nq = n + r;
		final int k = knot.span(u);
		final int s = knot.multiplicity(u, k);
		if (r + s > p) {
			throw new IllegalArgumentException(
					"Attempting to increase knot multiplicity above curve order.");
		}
		final WB_NurbsKnot UQ = new WB_NurbsKnot(n + 1 + r, p);
		final WB_Homogeneous[] Q = new WB_Homogeneous[nq + 1];
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
			Q[i] = wpoints[i];
		}
		for (int i = k - s; i <= n; i++) {
			Q[i + r] = wpoints[i];
		}
		final WB_Homogeneous[] RW = new WB_Homogeneous[p + 1];
		for (int i = 0; i <= p - s; i++) {
			RW[i] = new WB_Homogeneous(wpoints[k - p + i]);
		}
		int L = 0;
		for (int j = 1; j <= r; j++) {
			L = k - p + j;
			for (int i = 0; i <= p - j - s; i++) {
				final double alpha = (u - knot.value(L + i))
						/ (knot.value(i + k + 1) - knot.value(L + i));
				RW[i] = WB_Homogeneous.interpolate(RW[i], RW[i + 1], alpha);
			}
			Q[L] = RW[0];
			Q[k + r - j - s] = RW[p - j - s];

		}
		for (int i = L + 1; i < k - s; i++) {

			Q[i] = RW[i - L];
		}
		return new WB_RBSpline(Q, UQ);
	}

	@Override
	public WB_RBSpline refineKnot(final double[] X) {

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

	@Override
	public WB_RBSpline refineKnot(final WB_NurbsKnot K) {

		return refineKnot(K.values());

	}

	private WB_RBSpline refineKnotRestricted(final double[] X) {
		final int r = X.length - 1;
		final int a = knot.span(X[0]);
		final int b = knot.span(X[r]) + 1;
		final WB_Homogeneous[] Q = new WB_Homogeneous[n + r + 2];
		final WB_NurbsKnot Ubar = new WB_NurbsKnot(n + r + 2, p);
		for (int j = 0; j <= a - p; j++) {
			Q[j] = new WB_Homogeneous(wpoints[j]);
		}
		for (int j = b - 1; j <= n; j++) {
			Q[j + r + 1] = new WB_Homogeneous(wpoints[j]);
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
				Q[k - p - 1] = new WB_Homogeneous(wpoints[i - p - 1]);
				Ubar.setValue(k, knot.value(i));
				k = k - 1;
				i = i - 1;
			}
			Q[k - p - 1] = new WB_Homogeneous(Q[k - p]);
			for (int el = 1; el <= p; el++) {
				final int ind = k - p + el;
				double alpha = Ubar.value(k + el) - X[j];
				if (WB_Epsilon.isZero(alpha)) {
					Q[ind - 1] = new WB_Homogeneous(Q[ind]);
				} else {
					alpha /= (Ubar.value(k + el) - knot.value(i - p + el));
					Q[ind - 1] = WB_Homogeneous.interpolate(Q[ind], Q[ind - 1],
							alpha);
				}
			}
			Ubar.setValue(k, X[j]);
			k--;
		}
		return new WB_RBSpline(Q, Ubar);

	}

	@Override
	public WB_RBSpline[] split(final double u) {

		final WB_RBSpline newRBSpline = insertKnotMax(u);
		final int k = newRBSpline.knot().span(u);
		final int m = newRBSpline.knot().m;
		final WB_NurbsKnot knot1 = new WB_NurbsKnot(k + 1 - p, p);
		for (int i = 0; i < knot1.m; i++) {
			knot1.setValue(i, newRBSpline.knot().value(i));
		}
		knot1.setValue(knot1.m, u);
		knot1.normalize();
		final WB_Homogeneous[] wpoints1 = new WB_Homogeneous[k + 1 - p];
		for (int i = 0; i < k + 1 - p; i++) {
			wpoints1[i] = newRBSpline.wpoints[i];
		}
		final WB_NurbsKnot knot2 = new WB_NurbsKnot(m - k, p);
		for (int i = 0; i <= p; i++) {
			knot2.setValue(i, u);
		}
		for (int i = k + 1; i <= m; i++) {
			knot2.setValue(i - k + p, newRBSpline.knot().value(i));
		}
		knot2.normalize();
		final WB_Homogeneous[] wpoints2 = new WB_Homogeneous[m - k];
		for (int i = 0; i < m - k; i++) {
			wpoints2[i] = newRBSpline.wpoints[k - p + i];
		}
		final WB_RBSpline[] splitCurves = new WB_RBSpline[2];
		splitCurves[0] = new WB_RBSpline(wpoints1, knot1);
		splitCurves[1] = new WB_RBSpline(wpoints2, knot2);
		return splitCurves;
	}

	@Override
	public WB_RBSpline elevateDegree(final int t) {
		final int m = n + p + 1;
		final int ph = p + t;
		final int ph2 = ph / 2;
		final double[][] bezalfs = new double[p + t + 1][p + 1];
		final WB_Homogeneous[] bpts = new WB_Homogeneous[p + 1];
		final WB_Homogeneous[] ebpts = new WB_Homogeneous[p + t + 1];
		final WB_Homogeneous[] nextbpts = new WB_Homogeneous[p - 1];
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
		final WB_Homogeneous[] Q = new WB_Homogeneous[n + t * (s + 1) + 1];
		Q[0] = new WB_Homogeneous(wpoints[0]);
		final WB_NurbsKnot Uh = new WB_NurbsKnot(n + t * (s + 1) + 1, ph);
		for (i = 0; i <= ph; i++) {
			Uh.setValue(i, ua);
		}
		for (i = 0; i <= p; i++) {
			bpts[i] = new WB_Homogeneous(wpoints[i]);
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
						bpts[k] = WB_Homogeneous.interpolate(bpts[k - 1],
								bpts[k], alfs[k - sj]);
					}
					nextbpts[save] = new WB_Homogeneous(bpts[p]);
				}
			}

			for (i = lbz; i <= ph; i++) {
				ebpts[i] = new WB_Homogeneous();
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
							Q[i] = WB_Homogeneous.interpolate(Q[i - 1], Q[i],
									alf);
						}
						if (j >= lbz) {
							if (j - tr <= kind - ph + oldr) {
								final double gam = (ub - Uh.value(j - tr))
										/ den;
								ebpts[kj] = WB_Homogeneous.interpolate(
										ebpts[kj + 1], ebpts[kj], gam);
							} else {
								ebpts[kj] = WB_Homogeneous.interpolate(
										ebpts[kj + 1], ebpts[kj], bet);
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
				Q[cind] = new WB_Homogeneous(ebpts[j]);
				cind++;
			}
			if (b < m) {
				for (j = 0; j < r; j++) {
					bpts[j] = nextbpts[j];
				}
				for (j = r; j <= p; j++) {
					bpts[j] = new WB_Homogeneous(wpoints[b - p + j]);
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

		return new WB_RBSpline(Q, Uh);
	}
}
