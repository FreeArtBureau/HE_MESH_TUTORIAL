/*
 * Copyright (c) 2010, Frederik Vanhoutte This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * http://creativecommons.org/licenses/LGPL/2.1/ This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package wblut.geom;

import wblut.WB_Epsilon;

// TODO: Auto-generated Javadoc
/**
 * A subclass of WB_Triangle referencing points as array indices.
 */
public class WB_IndexedTriangle2D implements WB_Triangle2D {

	/** index of first point. */
	public int			i1;
	/** index of second point. */
	public int			i2;
	/** index of third point. */
	public int			i3;

	protected WB_Point2d[]	points;

	/** Length of side a. */
	protected double	a;

	/** Length of side b. */
	protected double	b;

	/** Length of side c. */
	protected double	c;

	/** Cosine of angle A. */
	protected double	cosA;

	/** Cosine of angle B. */
	protected double	cosB;

	/** Cosine of angle C. */
	protected double	cosC;

	/** Is triangle degenerate? */
	protected boolean	degenerate;

	public WB_IndexedTriangle2D(final int i1, final int i2, final int i3,
			final WB_Point2d[] points) {
		this.points = points;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		update();
	}

	public void update() {
		a = WB_Distance2D.distance(points[i2], points[i3]);
		b = WB_Distance2D.distance(points[i1], points[i3]);
		c = WB_Distance2D.distance(points[i1], points[i2]);

		cosA = ((points[i2].x - points[i1].x) * (points[i3].x - points[i1].x) + (points[i2].y - points[i1].y)
				* (points[i3].y - points[i1].y))
				/ (b * c);
		cosB = ((points[i1].x - points[i2].x) * (points[i3].x - points[i2].x) + (points[i1].y - points[i2].y)
				* (points[i3].y - points[i2].y))
				/ (a * c);
		cosC = ((points[i2].x - points[i3].x) * (points[i1].x - points[i3].x) + (points[i2].y - points[i3].y)
				* (points[i1].y - points[i3].y))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance2D.sqDistanceToLine(
				points[i1], points[i2], points[i3]));
	}

	/**
	 * Get circumcircle.
	 *
	 * @return circumcircle
	 */
	public WB_Circle getCircumcircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			result.setRadius(a
					* b
					* c
					/ Math.sqrt(2 * a * a * b * b + 2 * b * b * c * c + 2 * a
							* a * c * c - a * a * a * a - b * b * b * b - c * c
							* c * c));
			final double bx = points[i2].x - points[i1].x;
			final double by = points[i2].y - points[i1].y;
			final double cx = points[i3].x - points[i1].x;
			final double cy = points[i3].y - points[i1].y;
			double d = 2 * (bx * cy - by * cx);
			if (WB_Epsilon.isZero(d)) {
				return null;
			}
			d = 1.0 / d;
			final double b2 = bx * bx + by * by;
			final double c2 = cx * cx + cy * cy;
			final double x = (cy * b2 - by * c2) * d;
			final double y = (bx * c2 - cx * b2) * d;
			result.setCenter(x + points[i1].x, y + points[i1].y);
			return result;
		}
		return null;
	}

	/**
	 * Get incircle.
	 *
	 * @return incircle
	 */
	public WB_Circle getIncircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			final double abc = a + b + c;
			result.setRadius(0.5 * Math.sqrt(((b + c - a) * (c + a - b) * (a
					+ b - c))
					/ abc));
			final WB_Point2d ta = points[i1].multAndCopy(a);
			final WB_Point2d tb = points[i2].multAndCopy(b);
			final WB_Point2d tc = points[i3].multAndCopy(c);
			tc.add(ta).add(tb).div(abc);
			result.setCenter(tc);
			return result;
		}
		return null;
	}

	/**
	 * Get incenter.
	 *
	 * @return incenter
	 */
	public WB_Point2d getIncenter() {
		return getPointFromTrilinear(1, 1, 1);
	}

	/**
	 * Get centroid.
	 *
	 * @return centroid
	 */
	public WB_Point2d getCentroid() {
		return getPointFromTrilinear(b * c, c * a, a * b);
	}

	/**
	 * Get circumcenter.
	 *
	 * @return circumcenter
	 */
	public WB_Point2d getCircumcenter() {
		return getPointFromTrilinear(cosA, cosB, cosC);
	}

	/**
	 * Get orthocenter.
	 *
	 * @return orthocenter
	 */
	public WB_Point2d getOrthocenter() {
		final double a2 = a * a;
		final double b2 = b * b;
		final double c2 = c * c;
		return getPointFromBarycentric((a2 + b2 - c2) * (a2 - b2 + c2), (a2
				+ b2 - c2)
				* (-a2 + b2 + c2), (a2 - b2 + c2) * (-a2 + b2 + c2));
	}

	/**
	 * Get point from trilinear coordinates.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return point
	 */
	public WB_Point2d getPointFromTrilinear(final double x, final double y,
			final double z) {
		if (!degenerate) {

			final double abc = a * x + b * y + c * z;
			final WB_Point2d ea = points[i2].subAndCopy(points[i3]);
			final WB_Point2d eb = points[i1].subAndCopy(points[i3]);
			ea.mult(b * y);
			eb.mult(a * x);
			ea.add(eb);
			ea.div(abc);
			ea.add(points[i3]);
			return ea;

		}

		return null;

	}

	/**
	 * Get point from barycentric coordinates.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return point
	 */
	public WB_Point2d getPointFromBarycentric(final double x, final double y,
			final double z) {
		if (!degenerate) {
			return getPointFromTrilinear(x / a, y / b, z / c);
		}
		return null;
	}

	/**
	 * Barycentric coordinates of point.
	 *
	 * @param p point
	 * @return barycentric coordinates as WB_XYZ
	 */
	public WB_Point3d getBarycentric(final WB_Point2d p) {
		final double m = (points[i3].x - points[i1].x)
				* (points[i2].y - points[i1].y) - (points[i3].y - points[i1].y)
				* (points[i2].x - points[i1].x);

		double nu, nv, ood;

		nu = twiceSignedTriArea2D(p.x, p.y, points[i2].x, points[i2].y,
				points[i3].x, points[i3].y);
		nv = twiceSignedTriArea2D(p.x, p.y, points[i3].x, points[i3].y,
				points[i1].x, points[i1].y);
		ood = -1.0 / m;

		nu *= ood;
		nv *= ood;
		return new WB_Point3d(nu, nv, 1 - nu - nv);

	}

	/**
	 * Check if points points[i1] and points[i2] lie on same side of line A-B.
	 *
	 * @param points[i1] the points[i1]
	 * @param points[i2] the points[i2]
	 * @param A the a
	 * @param B the b
	 * @return true, false
	 */
	public static boolean sameSide2D(final WB_Point2d p1, final WB_Point2d p2,
			final WB_Point2d A, final WB_Point2d B) {
		final WB_Point2d t1 = B.get().sub(A);
		final WB_Point2d t2 = p1.get().sub(A);
		final WB_Point2d t3 = p2.get().sub(A);
		final double ct2 = t1.x * t2.y - t1.y * t2.x;
		final double ct3 = t1.x * t3.y - t1.y * t3.x;

		if (ct2 * ct3 >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}

	/**
	 * Check if point p lies in triangle A-B-C.
	 *
	 * @param p the p
	 * @param A the a
	 * @param B the b
	 * @param C the c
	 * @return true, false
	 */
	public static boolean pointInTriangle2D(final WB_Point2d p, final WB_Point2d A,
			final WB_Point2d B, final WB_Point2d C) {
		if (WB_Epsilon.isZeroSq(WB_Distance2D.sqDistanceToLine(A, B, C))) {
			return false;
		}
		if (sameSide2D(p, A, B, C) && sameSide2D(p, B, A, C)
				&& sameSide2D(p, C, A, B)) {
			return true;
		}
		return false;
	}

	/*
	 * public static boolean pointInTriangle2D(final WB_XY p, final WB_XY A,
	 * final WB_XY B, final WB_XY C) { final WB_XY e0 = B.subAndCopy(A); final
	 * WB_XY n0 = new WB_XY(e0.y, -e0.x); final double sign = e0.y * (C.x - A.x)
	 * - e0.x * (C.y - A.y); if (sign * n0.dot(p.subAndCopy(A)) <
	 * WB_Epsilon.EPSILON) { return false; } final WB_XY e1 = C.subAndCopy(B);
	 * final WB_XY n1 = new WB_XY(e1.y, -e1.x); if (sign *
	 * n1.dot(p.subAndCopy(B)) < WB_Epsilon.EPSILON) { return false; } final
	 * WB_XY e2 = A.subAndCopy(C); final WB_XY n2 = new WB_XY(e2.y, -e2.x); if
	 * (sign * n2.dot(p.subAndCopy(C)) < WB_Epsilon.EPSILON) { return false; }
	 * return true; }
	 */

	public static boolean pointInTriangle2D(final WB_Point2d p,
			final WB_ExplicitTriangle2D T) {
		return pointInTriangle2D(p, T.p1, T.p2, T.p3);
	}

	/**
	 * Check if point p lies in triangle A-B-C  using barycentric coordinates.
	 *
	 * @param p the p
	 * @param A the a
	 * @param B the b
	 * @param C the c
	 * @return true, false
	 */
	public static boolean pointInTriangleBary2D(final WB_Point2d p, final WB_Point2d A,
			final WB_Point2d B, final WB_Point2d C) {

		if (p == A) {
			return false;
		}
		if (p == B) {
			return false;
		}
		if (p == C) {
			return false;
		}
		if (WB_Epsilon.isZeroSq(WB_Distance2D.sqDistanceToLine(A, B, C))) {
			return false;
		}
		// Compute vectors
		final WB_Point2d v0 = C.get().sub(A);
		final WB_Point2d v1 = B.get().sub(A);
		final WB_Point2d v2 = p.get().sub(A);

		// Compute dot products
		final double dot00 = v0.dot(v0);
		final double dot01 = v0.dot(v1);
		final double dot02 = v0.dot(v2);
		final double dot11 = v1.dot(v1);
		final double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		final double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
		final double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		final double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u > WB_Epsilon.EPSILON) && (v > WB_Epsilon.EPSILON)
				&& (u + v < 1 - WB_Epsilon.EPSILON);
	}

	public static boolean pointInTriangleBary2D(final WB_Point2d p,
			final WB_ExplicitTriangle2D T) {
		return pointInTriangleBary2D(p, T.p1, T.p2, T.p3);
	}

	/**
	 * Twice signed tri area2 d.
	 *
	 * @param points[i1] the points[i1]
	 * @param points[i2] the points[i2]
	 * @param points[i3] the points[i3]
	 * @return the double
	 */
	public static double twiceSignedTriArea2D(final WB_Point2d p1, final WB_Point2d p2,
			final WB_Point2d p3) {
		return (p1.x - p3.x) * (p2.y - p3.y) - (p1.y - p3.y) * (p2.x - p3.x);
	}

	/**
	 * Twice signed tri area2 d.
	 *
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param x3 the x3
	 * @param y3 the y3
	 * @return the double
	 */
	public static double twiceSignedTriArea2D(final double x1, final double y1,
			final double x2, final double y2, final double x3, final double y3) {
		return (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle2D#getCenter()
	 */
	public WB_Point2d getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle2D#points[i1]()
	 */
	public WB_Point2d p1() {
		return points[i1];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle2D#points[i2]()
	 */
	public WB_Point2d p2() {
		return points[i2];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle2D#points[i3]()
	 */
	public WB_Point2d p3() {
		return points[i3];
	}
}