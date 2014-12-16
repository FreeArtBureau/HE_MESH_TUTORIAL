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

/**
 *  3D Triangle.
 */
public class WB_ExplicitTriangle implements WB_Triangle {

	/** First point. */
	public WB_Point3d		p1;

	/** Second point. */
	public WB_Point3d		p2;

	/** Third point. */
	public WB_Point3d		p3;

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

	/**
	 * Instantiates a new WB_Triangle. No copies are made.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 */
	public WB_ExplicitTriangle(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		update();
	}

	/**
	 * Instantiates a new WB_Triangle.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @param copy copy points?
	 */
	public WB_ExplicitTriangle(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d p3, final boolean copy) {
		if (!copy) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		} else {
			this.p1 = p1.get();
			this.p2 = p2.get();
			this.p3 = p3.get();
		}
		update();
	}

	/**
	 * Update side lengths and corner angles.
	 */
	public void update() {
		a = WB_Distance.distance(p2, p3);
		b = WB_Distance.distance(p1, p3);
		c = WB_Distance.distance(p1, p2);

		cosA = ((p2.x - p1.x) * (p3.x - p1.x) + (p2.y - p1.y) * (p3.y - p1.y) + (p2.z - p1.z)
				* (p3.z - p1.z))
				/ (b * c);
		cosB = ((p1.x - p2.x) * (p3.x - p2.x) + (p1.y - p2.y) * (p3.y - p2.y) + (p1.z - p2.z)
				* (p3.z - p2.z))
				/ (a * c);
		cosC = ((p2.x - p3.x) * (p1.x - p3.x) + (p2.y - p3.y) * (p1.y - p3.y) + (p2.z - p3.z)
				* (p1.z - p3.z))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance.sqDistanceToLine(p1, p2,
				p3));
	}

	/**
	 * Get plane of triangle.
	 *
	 * @return WB_Plane
	 */
	public WB_Plane getPlane() {
		final WB_Plane P = new WB_Plane(p1, p2, p3);
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
	public WB_Point3d getPointFromTrilinear(final double x, final double y,
			final double z) {
		if (!degenerate) {

			final double abc = a * x + b * y + c * z;
			final WB_Point3d ea = p2.subAndCopy(p3);
			final WB_Point3d eb = p1.subAndCopy(p3);
			ea.mult(b * y);
			eb.mult(a * x);
			ea.add(eb);
			ea.div(abc);
			ea.add(p3);
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
		final WB_Vector3d m = WB_Vector3d.cross(p3.subToVector(p1),
				p2.subToVector(p1));
		double nu, nv, ood;
		final double x = WB_Fast.abs(m.x);
		final double y = WB_Fast.abs(m.y);
		final double z = WB_Fast.abs(m.z);
		if (x >= y && x >= z) {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.y, p.z, p2.y, p2.z, p3.y,
					p3.z);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.y, p.z, p3.y, p3.z, p1.y,
					p1.z);
			ood = 1.0 / m.x;
		} else if (y >= x && y >= z) {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.z, p2.x, p2.z, p3.x,
					p3.z);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.z, p3.x, p3.z, p1.x,
					p1.z);
			ood = -1.0 / m.y;
		} else {
			nu = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.y, p2.x, p2.y, p3.x,
					p3.y);
			nv = WB_ExplicitTriangle2D.twiceSignedTriArea2D(p.x, p.y, p3.x, p3.y, p1.x,
					p1.y);
			ood = -1.0 / m.z;
		}
		nu *= ood;
		nv *= ood;
		return new WB_Point3d(nu, nv, 1 - nu - nv);

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p1()
	 */
	public WB_Point3d p1() {

		return p1;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p2()
	 */
	public WB_Point3d p2() {
		return p2;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Triangle#p3()
	 */
	public WB_Point3d p3() {
		return p3;
	}
	
	public double getArea() {
		final WB_Plane P = getPlane();
		if (P == null) {
			return 0.0;
		}
		final WB_Normal3d n = getPlane().getNormal();

		final double x = WB_Fast.abs(n.x);
		final double y = WB_Fast.abs(n.y);
		final double z = WB_Fast.abs(n.z);
		double area = 0;
		int coord = 3;
		if (x >= y && x >= z) {
			coord = 1;
		} else if (y >= x && y >= z) {
			coord = 2;
		}

		switch (coord) {
		case 1:
			area = (p1.y * (p2.z - p3.z)) + (p2.y * (p3.z - p1.z))
					+ (p3.y * (p1.z - p2.z));
			break;
		case 2:
			area = (p1.x * (p2.z - p3.z)) + (p2.x * (p3.z - p1.z))
					+ (p3.x * (p1.z - p2.z));
			break;
		case 3:
			area = (p1.x * (p2.y - p3.y)) + (p2.x * (p3.y - p1.y))
					+ (p3.x * (p1.y - p2.y));
			break;

		}

		switch (coord) {
		case 1:
			area *= (0.5 / x);
			break;
		case 2:
			area *= (0.5 / y);
			break;
		case 3:
			area *= (0.5 / z);
		}

		return WB_Fast.abs(area);
	}


}
