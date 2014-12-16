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

import java.util.ArrayList;

import wblut.WB_Epsilon;
import wblut.math.WB_Fast;



// TODO: Auto-generated Javadoc
/**
 * 2D circle.
 */
public class WB_Circle {

	/** Center. */
	private WB_Point2d	_center;

	/** Radius. */
	private double		_radius;

	/**
	 * Instantiates a new WB_Circle.
	 */
	public WB_Circle() {
		_center = new WB_Point2d();
		_radius = 0;
	}

	/**
	 * Center.
	 *
	 * @return center
	 */
	public WB_Point2d getCenter() {
		return _center;
	}

	/**
	 * Sets center.
	 *
	 * @param center new center
	 */
	public void setCenter(final WB_Point2d center) {
		_center = center;
	}

	public void set(final WB_Circle c) {
		_center = c.getCenter();
		_radius = c.getRadius();
	}

	/**
	 * Sets center.
	 *
	 * @param x new center
	 * @param y new center
	 */
	public void setCenter(final double x, final double y) {
		_center.set(x, y);
	}

	/**
	 * Returns radius.
	 *
	 * @return radius
	 */
	public double getRadius() {
		return _radius;
	}

	/**
	 * Sets radius.
	 *
	 * @param radius new radius
	 */
	public void setRadius(final double radius) {
		_radius = radius;
	}

	/**
	 * Instantiates a new WB_Circle.
	 *
	 * @param c center
	 * @param r radius
	 */
	public WB_Circle(final WB_Point2d c, final double r) {
		_center = c;
		_radius = r;
	}

	/**
	 * Instantiates a new WB_Circle.
	 *
	 * @param x x-coordinate of center
	 * @param y y-coordinate of center
	 * @param r radius
	 */
	public WB_Circle(final double x, final double y, final double r) {
		_center = new WB_Point2d(x, y);
		_radius = r;
	}

	public static WB_Circle getCircleThrough3Points(final WB_Point2d p0,
			final WB_Point2d p1, final WB_Point2d p2) {
		final WB_ExplicitTriangle2D T = new WB_ExplicitTriangle2D(p0, p1, p2);
		return T.getCircumcircle();
	}

	public static WB_Circle getCircleTangentTo3Lines(final WB_Line2D L0,
			final WB_Line2D L1, final WB_Line2D L2) {
		final WB_Point2d p0 = (WB_Point2d) WB_Intersection2D.closestPoint2D(L0,
				L1).object;
		final WB_Point2d p1 = (WB_Point2d) WB_Intersection2D.closestPoint2D(L1,
				L2).object;
		final WB_Point2d p2 = (WB_Point2d) WB_Intersection2D.closestPoint2D(L0,
				L2).object;

		final WB_ExplicitTriangle2D T = new WB_ExplicitTriangle2D(p0, p1, p2);
		return T.getIncircle();
	}

	public static ArrayList<WB_Circle> getCircleThrough2Points(
			final WB_Point2d p0, final WB_Point2d p1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>();
		final WB_Circle C0 = new WB_Circle(p0, r);
		final WB_Circle C1 = new WB_Circle(p1, r);
		final ArrayList<WB_Point2d> intersection = WB_Intersection2D
				.intersect2D(C0, C1);
		for (int i = 0; i < intersection.size(); i++) {
			result.add(new WB_Circle(intersection.get(i), r));

		}
		return result;
	}

	public static ArrayList<WB_Circle> getCircleTangentToLineThroughPoint(
			final WB_Line2D L, final WB_Point2d p, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>();
		double cPrime = L.c() + L.a() * p.x + L.b() * p.y;
		if (WB_Epsilon.isZero(cPrime)) {
			result.add(new WB_Circle(p.addAndCopy(L.a(), L.b(), r), r));
			result.add(new WB_Circle(p.addAndCopy(L.a(), L.b(), -r), r));
			return result;
		}
		double a, b;

		if (cPrime < 0) {
			a = -L.a();
			b = -L.b();
			cPrime *= -1;
		} else {

			a = L.a();
			b = L.b();
		}
		final double tmp1 = cPrime - r;
		double tmp2 = r * r - tmp1 * tmp1;
		if (WB_Epsilon.isZero(tmp2)) {
			result.add(new WB_Circle(p.addAndCopy(a, b, -tmp1), r));
			return result;
		} else if (tmp2 < 0) {
			return result;
		} else {
			tmp2 = Math.sqrt(tmp2);
			final WB_Point2d tmpp = new WB_Point2d(p.x - a * tmp1, p.y - b
					* tmp1);
			result.add(new WB_Circle(tmpp.addAndCopy(b, -a, tmp2), r));
			result.add(new WB_Circle(tmpp.addAndCopy(-b, a, tmp2), r));
			return result;
		}

	}

	public static ArrayList<WB_Circle> getCircleTangentTo2Lines(
			final WB_Line2D L0, final WB_Line2D L1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(4);
		final double discrm0 = r;// Math.sqrt(L0.a() * L0.a() + L0.b() * L0.b())
		// * r;
		final double discrm1 = r;// Math.sqrt(L1.a() * L1.a() + L1.b() * L1.b())
		// * r;
		final double invDenom = 1.0 / (-L1.a() * L0.b() + L0.a() * L1.b());
		double cx = -(L1.b() * (L0.c() + discrm0) - L0.b() * (L1.c() + discrm1))
				* invDenom;
		double cy = +(L1.a() * (L0.c() + discrm0) - L0.a() * (L1.c() + discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point2d(cx, cy), r));
		cx = -(L1.b() * (L0.c() + discrm0) - L0.b() * (L1.c() - discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() + discrm0) - L0.a() * (L1.c() - discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point2d(cx, cy), r));
		cx = -(L1.b() * (L0.c() - discrm0) - L0.b() * (L1.c() + discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() - discrm0) - L0.a() * (L1.c() + discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point2d(cx, cy), r));
		cx = -(L1.b() * (L0.c() - discrm0) - L0.b() * (L1.c() - discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() - discrm0) - L0.a() * (L1.c() - discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point2d(cx, cy), r));

		return result;
	}

	public static ArrayList<WB_Circle> getCircleThroughPointTangentToCircle(
			final WB_Point2d p, final WB_Circle C, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(4);
		final double dcp = WB_Distance2D.distance(p, C.getCenter());

		if (dcp > C.getRadius() + 2 * r) {
			return result;

		} else if (dcp < C.getRadius() - 2 * r) {
			return result;

		} else {
			final WB_Circle ctmp1 = new WB_Circle(p, r);
			WB_Circle ctmp2 = new WB_Circle(C.getCenter(), r + C.getRadius());
			ArrayList<WB_Point2d> intersection = WB_Intersection2D.intersect2D(
					ctmp1, ctmp2);
			for (int i = 0; i < intersection.size(); i++) {
				result.add(new WB_Circle(intersection.get(i), r));
			}
			ctmp2 = new WB_Circle(C.getCenter(), WB_Fast.abs(r - C.getRadius()));
			intersection = WB_Intersection2D.intersect2D(ctmp1, ctmp2);
			for (int i = 0; i < intersection.size(); i++) {
				result.add(new WB_Circle(intersection.get(i), r));
			}
		}

		return result;

	}

	public static ArrayList<WB_Circle> getCircleTangentToLineAndCircle(
			final WB_Line2D L, final WB_Circle C, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(8);
		final double d = WB_Distance2D.distance(C.getCenter(), L);
		if (d > 2 * r + C.getRadius()) {
			return result;
		}
		final WB_Line2D L1 = new WB_Line2D(L.getOrigin().addAndCopy(
				L.getDirection().y, -L.getDirection().x, r), L.getDirection());
		final WB_Line2D L2 = new WB_Line2D(L.getOrigin().addAndCopy(
				-L.getDirection().y, +L.getDirection().x, r), L.getDirection());
		final WB_Circle C1 = new WB_Circle(C.getCenter(), C.getRadius() + r);
		final WB_Circle C2 = new WB_Circle(C.getCenter(), WB_Fast.abs(C
				.getRadius() - r));
		final ArrayList<WB_Point2d> intersections = new ArrayList<WB_Point2d>();
		intersections.addAll(WB_Intersection2D.intersect2D(L1, C1));
		intersections.addAll(WB_Intersection2D.intersect2D(L1, C2));
		intersections.addAll(WB_Intersection2D.intersect2D(L2, C1));
		intersections.addAll(WB_Intersection2D.intersect2D(L2, C2));
		for (int i = 0; i < intersections.size(); i++) {
			result.add(new WB_Circle(intersections.get(i), r));
		}
		return result;
	}

	public static ArrayList<WB_Circle> getCircleTangentToTwoCircles(
			final WB_Circle C0, final WB_Circle C1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(2);

		final WB_Circle C0r = new WB_Circle(C0.getCenter(), C0.getRadius() + r);
		final WB_Circle C1r = new WB_Circle(C1.getCenter(), C1.getRadius() + r);

		final ArrayList<WB_Point2d> intersections = new ArrayList<WB_Point2d>();
		intersections.addAll(WB_Intersection2D.intersect2D(C0r, C1r));

		for (int i = 0; i < intersections.size(); i++) {
			result.add(new WB_Circle(intersections.get(i), r));
		}
		return result;
	}
}
