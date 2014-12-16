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
import wblut.math.WB_Fast;

// TODO: Auto-generated Javadoc
/**
 * A subclass of WB_Triangle referencing points as array indices.
 */
public class WB_IndexedTriangle implements WB_Triangle {

	/** index of first point. */
	private int					i1;
	/** index of second point. */
	private int					i2;
	/** index of third point. */
	private final int			i3;

	private final WB_Point3d[]	points;
	/** Length of side a. */
	protected double			a;

	/** Length of side b. */
	protected double			b;

	/** Length of side c. */
	protected double			c;

	/** Cosine of angle A. */
	protected double			cosA;

	/** Cosine of angle B. */
	protected double			cosB;

	/** Cosine of angle C. */
	protected double			cosC;

	/** Is triangle degenerate? */
	protected boolean			degenerate;

	/**
	 * Instantiates a new indexed triangle.
	 *
	 * @param i1 index of first point
	 * @param i2 index of second point
	 * @param i3 index of third point
	 * @param points points as WB_Point[]
	 */
	public WB_IndexedTriangle(final int i1, final int i2, final int i3,
			final WB_Point3d[] points) {
		this.points = points;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		update();
	}

	/**
	 * Reverse the triangle.
	 */
	public void reverse() {
		final int t = i1;
		i1 = i2;
		i2 = t;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p1()
	 */
	public WB_Point3d p1() {
		return points[i1];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p2()
	 */
	public WB_Point3d p2() {
		return points[i2];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p3()
	 */
	public WB_Point3d p3() {
		return points[i3];
	}

	public int i1() {
		return i1;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p2()
	 */
	public int i2() {
		return i2;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p3()
	 */
	public int i3() {
		return i3;
	}

	/**
	 * Update side lengths and corner angles.
	 */
	public void update() {
		a = WB_Distance.distance(points[i2], points[i3]);
		b = WB_Distance.distance(points[i1], points[i3]);
		c = WB_Distance.distance(points[i1], points[i2]);

		cosA = ((points[i2].x - points[i1].x) * (points[i3].x - points[i1].x)
				+ (points[i2].y - points[i1].y) * (points[i3].y - points[i1].y) + (points[i2].z - points[i1].z)
				* (points[i3].z - points[i1].z))
				/ (b * c);
		cosB = ((points[i1].x - points[i2].x) * (points[i3].x - points[i2].x)
				+ (points[i1].y - points[i2].y) * (points[i3].y - points[i2].y) + (points[i1].z - points[i2].z)
				* (points[i3].z - points[i2].z))
				/ (a * c);
		cosC = ((points[i2].x - points[i3].x) * (points[i1].x - points[i3].x)
				+ (points[i2].y - points[i3].y) * (points[i1].y - points[i3].y) + (points[i2].z - points[i3].z)
				* (points[i1].z - points[i3].z))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance.sqDistanceToLine(
				points[i1], points[i2], points[i3]));
	}

	/**
	 * Get plane of triangle.
	 *
	 * @return WB_Plane
	 */
	public WB_Plane getPlane() {
		final WB_Plane P = new WB_Plane(points[i1], points[i2], points[i3]);
		if (P.getNormal().mag2() < WB_Epsilon.SQEPSILON) {
			return null;
		}
		return P;
	}

	/**
	 * Get incenter.
	 *
	 * @return incenter
	 */
	public WB_Point3d getCenter() {
		return getPointFromTrilinear(1, 1, 1);
	}

	/**
	 * Get centroid.
	 *
	 * @return centroid
	 */
	public WB_Point3d getCentroid() {
		return getPointFromTrilinear(b * c, c * a, a * b);
	}

	/**
	 * Get circumcenter.
	 *
	 * @return circumcenter
	 */
	public WB_Point3d getCircumcenter() {
		return getPointFromTrilinear(cosA, cosB, cosC);
	}

	/**
	 * Get orthocenter.
	 *
	 * @return orthocenter
	 */
	public WB_Point3d getOrthocenter() {
		final double a2 = a * a;
		final double b2 = b * b;
		final double c2 = c * c;
		return getPointFromBarycentric((a2 + b2 - c2) * (a2 - b2 + c2), (a2 + b2 - c2)
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
	public WB_Point3d getPointFromTrilinear(final double x, final double y, final double z) {
		if (!degenerate) {

			final double abc = a * x + b * y + c * z;
			final WB_Point3d ea = points[i2].subAndCopy(points[i3]);
			final WB_Point3d eb = points[i1].subAndCopy(points[i3]);
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
	public WB_Point3d getPointFromBarycentric(final double x, final double y,
			final double z) {
		if (!degenerate) {
			return getPointFromTrilinear(x / a, y / b, z / c);
		}
		return null;
	}

	/**
	 * Barycentric.
	 *
	 * @param p the p
	 * @return the w b_ point
	 */
	public WB_Point3d getBarycentric(final WB_Point3d p) {
		final WB_Vector3d m = WB_Vector3d.cross(points[i3].subToVector(points[i1]),
				points[i2].subToVector(points[i1]));
		double nu, nv, ood;
		final double x = WB_Fast.abs(m.x);
		final double y = WB_Fast.abs(m.y);
		final double z = WB_Fast.abs(m.z);
		if (x >= y && x >= z) {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.y, p.z, points[i2].y,
					points[i2].z, points[i3].y, points[i3].z);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.y, p.z, points[i3].y,
					points[i3].z, points[i1].y, points[i1].z);
			ood = 1.0 / m.x;
		} else if (y >= x && y >= z) {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.z, points[i2].x,
					points[i2].z, points[i3].x, points[i3].z);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.z, points[i3].x,
					points[i3].z, points[i1].x, points[i1].z);
			ood = -1.0 / m.y;
		} else {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.y, points[i2].x,
					points[i2].y, points[i3].x, points[i3].y);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.y, points[i3].x,
					points[i3].y, points[i1].x, points[i1].y);
			ood = -1.0 / m.z;
		}
		nu *= ood;
		nv *= ood;
		return new WB_Point3d(nu, nv, 1 - nu - nv);

	}

}