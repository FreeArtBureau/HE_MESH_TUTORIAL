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



/**
 * 2D line.
 */
public class WB_Line2D extends WB_Linear2D {

	/**
	 * Instantiates a new WB_Line.
	 */
	public WB_Line2D() {
		super();

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * @param o origin
	 * @param d direction
	 */
	public WB_Line2D(final WB_Point2d o, final WB_Point2d d) {
		super(o, d, true);

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * 
	 * @param p1x first point on line
	 * @param p1y first point on line
	 * @param p2x second point on line
	 * @param p2y second point on line
	 */
	public WB_Line2D(final double p1x, final double p1y, final double p2x,
			final double p2y) {
		super(p1x, p1y, p2x, p2y);
	}

	/**
	 * Sets new line parameters.
	 *
	 * @param p1 first point on line
	 * @param p2 second point on line
	 */
	public void setFromPoints(final WB_Point2d p1, final WB_Point2d p2) {
		super.set(p1, p2);

	}

	public double getT(final WB_Point2d p) {
		double t = Double.NaN;
		final WB_Point2d proj = WB_Intersection2D.closestPoint2D(p, this);
		final double x = WB_Fast.abs(direction.x);
		final double y = WB_Fast.abs(direction.y);
		if (x >= y) {
			t = (proj.x - origin.x) / (direction.x);
		} else {
			t = (proj.y - origin.y) / (direction.y);
		}
		return t;
	}

	/**
	 * Classify point to line.
	 *
	  */
	public WB_ClassifyPointToLine2D classifyPointToLine2D(final WB_Point2d p) {

		final double dist = -direction.y * p.x + direction.x * p.y + origin.x
				* direction.y - origin.y * direction.x;

		if (dist > WB_Epsilon.EPSILON) {
			return WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_ClassifyPointToLine2D.POINT_BEHIND_LINE;
		}
		return WB_ClassifyPointToLine2D.POINT_ON_LINE;
	}

	/**
	 * Classify point to line.
	 *
	  */
	public static WB_ClassifyPointToLine2D classifyPointToLine2D(
			final WB_Point2d p, final WB_Line2D L) {

		final double dist = L.a() * p.x + L.b() * p.y + L.c();

		if (dist > WB_Epsilon.EPSILON) {
			return WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_ClassifyPointToLine2D.POINT_BEHIND_LINE;
		}
		return WB_ClassifyPointToLine2D.POINT_ON_LINE;
	}

	public WB_ClassifySegmentToLine2D classifySegmentToLine2D(
			final WB_Segment2D seg) {
		final WB_ClassifyPointToLine2D a = classifyPointToLine2D(seg
				.getOrigin());
		final WB_ClassifyPointToLine2D b = classifyPointToLine2D(seg.getEnd());
		if (a == WB_ClassifyPointToLine2D.POINT_ON_LINE) {
			if (b == WB_ClassifyPointToLine2D.POINT_ON_LINE) {
				return WB_ClassifySegmentToLine2D.SEGMENT_ON_LINE;
			} else if (b == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
				return WB_ClassifySegmentToLine2D.SEGMENT_IN_FRONT_OF_LINE;
			} else {
				return WB_ClassifySegmentToLine2D.SEGMENT_BEHIND_LINE;
			}
		}
		if (b == WB_ClassifyPointToLine2D.POINT_ON_LINE) {
			if (a == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
				return WB_ClassifySegmentToLine2D.SEGMENT_IN_FRONT_OF_LINE;
			} else {
				return WB_ClassifySegmentToLine2D.SEGMENT_BEHIND_LINE;
			}
		}
		if ((a == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE)
				&& (b == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE)) {
			return WB_ClassifySegmentToLine2D.SEGMENT_SPANNING_LINE;
		}
		if ((a == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE)
				&& (b == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE)) {
			return WB_ClassifySegmentToLine2D.SEGMENT_SPANNING_LINE;
		}

		if (a == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
			return WB_ClassifySegmentToLine2D.SEGMENT_IN_FRONT_OF_LINE;
		}
		return WB_ClassifySegmentToLine2D.SEGMENT_BEHIND_LINE;
	}

	public WB_ClassifyPolygonToLine2D classifyPolygonToLine2D(
			final WB_Polygon2D P) {

		int numFront = 0;
		int numBack = 0;

		for (int i = 0; i < P.n; i++) {

			if (classifyPointToLine2D(P.points[i]) == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
				numFront++;

			} else if (classifyPointToLine2D(P.points[i]) == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
				numBack++;
			}

			if (numFront > 0 && numBack > 0) {
				return WB_ClassifyPolygonToLine2D.POLYGON_SPANNING_LINE;
			}

		}
		if (numFront > 0) {
			return WB_ClassifyPolygonToLine2D.POLYGON_IN_FRONT_OF_LINE;
		}
		if (numBack > 0) {
			return WB_ClassifyPolygonToLine2D.POLYGON_BEHIND_LINE;
		}
		return null;
	}

	public static WB_Line2D getLineTangentToCircleAtPoint(final WB_Circle C,
			final WB_Point2d p) {
		final WB_Point2d v = p.subAndCopy(C.getCenter());
		return new WB_Line2D(p, new WB_Point2d(-v.y, v.x));

	}

	public static ArrayList<WB_Line2D> getLinesTangentToCircleThroughPoint(
			final WB_Circle C, final WB_Point2d p) {
		final ArrayList<WB_Line2D> result = new ArrayList<WB_Line2D>(2);
		final double dcp = WB_Distance2D.distance(C.getCenter(), p);

		if (WB_Epsilon.isZero(dcp - C.getRadius())) {
			final WB_Point2d u = p.subAndCopy(C.getCenter());
			result.add(new WB_Line2D(p, new WB_Point2d(-u.y, u.x)));
		} else if (dcp < C.getRadius()) {
			return result;
		} else {
			final WB_Point2d u = p.subAndCopy(C.getCenter());
			final double ux2 = u.x * u.x;
			final double ux4 = ux2 * ux2;
			final double uy2 = u.y * u.y;
			final double r2 = C.getRadius() * C.getRadius();
			final double r4 = r2 * r2;
			final double num = r2 * uy2;
			final double denom = ux2 + uy2;
			final double rad = Math.sqrt(-r4 * ux2 + r2 * ux4 + r2 * ux2 * uy2);

			result.add(new WB_Line2D(p, new WB_Point2d(-((r2 * u.y) + rad)
					/ denom, (r2 - (num + u.y * rad) / denom) / u.x)));
			result.add(new WB_Line2D(p, new WB_Point2d(-((r2 * u.y) - rad)
					/ denom, (r2 - (num - u.y * rad) / denom) / u.x)));

		}

		return result;

	}

	public static ArrayList<WB_Line2D> getLinesTangentTo2Circles(
			final WB_Circle C0, final WB_Circle C1) {
		final ArrayList<WB_Line2D> result = new ArrayList<WB_Line2D>(4);
		final WB_Point2d w = C1.getCenter().subAndCopy(C0.getCenter());
		final double wlensqr = w.mag2();
		final double rsum = C0.getRadius() + C1.getRadius();
		if (wlensqr <= rsum * rsum + WB_Epsilon.SQEPSILON) {
			return result;
		}
		final double rdiff = C1.getRadius() - C0.getRadius();
		if (!WB_Epsilon.isZero(rdiff)) {
			final double r0sqr = C0.getRadius() * C0.getRadius();
			final double r1sqr = C1.getRadius() * C1.getRadius();
			final double c0 = -r0sqr;
			final double c1 = 2 * r0sqr;
			final double c2 = C1.getRadius() * C1.getRadius() - r0sqr;
			final double invc2 = 1.0 / c2;
			final double discr = Math.sqrt(WB_Fast.abs(c1 * c1 - 4 * c0 * c2));
			double s, oms, a;
			s = -0.5 * (c1 + discr) * invc2;
			if (s >= 0.5) {
				a = Math.sqrt(WB_Fast.abs(wlensqr - r0sqr / (s * s)));
			} else {
				oms = 1.0 - s;
				a = Math.sqrt(WB_Fast.abs(wlensqr - r1sqr / (oms * oms)));
			}
			WB_Point2d[] dir = getDirections(w, a);

			WB_Point2d org = new WB_Point2d(C0.getCenter().x + s * w.x,
					C0.getCenter().y + s * w.y);
			result.add(new WB_Line2D(org, dir[0]));
			result.add(new WB_Line2D(org, dir[1]));

			s = -0.5 * (c1 - discr) * invc2;
			if (s >= 0.5) {
				a = Math.sqrt(WB_Fast.abs(wlensqr - r0sqr / (s * s)));
			} else {
				oms = 1.0 - s;
				a = Math.sqrt(WB_Fast.abs(wlensqr - r1sqr / (oms * oms)));
			}
			dir = getDirections(w, a);

			org = new WB_Point2d(C0.getCenter().x + s * w.x, C0.getCenter().y
					+ s * w.y);
			result.add(new WB_Line2D(org, dir[0]));
			result.add(new WB_Line2D(org, dir[1]));

		} else {
			final WB_Point2d mid = (C0.getCenter().addAndCopy(C1.getCenter()))
					.mult(0.5);
			final double a = Math.sqrt(WB_Fast.abs(wlensqr - 4 * C0.getRadius()
					* C0.getRadius()));
			final WB_Point2d[] dir = getDirections(w, a);
			result.add(new WB_Line2D(mid, dir[0]));
			result.add(new WB_Line2D(mid, dir[1]));

			final double invwlen = 1.0 / Math.sqrt(wlensqr);
			w.x *= invwlen;
			w.y *= invwlen;
			result.add(new WB_Line2D(new WB_Point2d(mid.x + C0.getRadius()
					* w.y, mid.y - C0.getRadius() * w.x), w));
			result.add(new WB_Line2D(new WB_Point2d(mid.x - C0.getRadius()
					* w.y, mid.y + C0.getRadius() * w.x), w));

		}

		return result;
	}

	private static WB_Point2d[] getDirections(final WB_Point2d w, final double a) {
		final WB_Point2d[] dir = new WB_Point2d[2];
		final double asqr = a * a;
		final double wxsqr = w.x * w.x;
		final double wysqr = w.y * w.y;
		final double c2 = wxsqr + wysqr;
		final double invc2 = 1.0 / c2;
		double c0, c1, discr, invwx;
		final double invwy;

		if (WB_Fast.abs(w.x) >= WB_Fast.abs(w.y)) {
			c0 = asqr - wxsqr;
			c1 = -2 * a * w.y;
			discr = Math.sqrt(WB_Fast.abs(c1 * c1 - 4 * c0 * c2));
			invwx = 1.0 / w.x;
			final double dir0y = -0.5 * (c1 + discr) * invc2;
			dir[0] = new WB_Point2d((a - w.y * dir0y) * invwx, dir0y);
			final double dir1y = -0.5 * (c1 - discr) * invc2;
			dir[1] = new WB_Point2d((a - w.y * dir1y) * invwx, dir1y);

		} else {
			c0 = asqr - wysqr;
			c1 = -2 * a * w.x;
			discr = Math.sqrt(WB_Fast.abs(c1 * c1 - 4 * c0 * c2));
			invwy = 1.0 / w.y;
			final double dir0x = -0.5 * (c1 + discr) * invc2;
			dir[0] = new WB_Point2d(dir0x, (a - w.x * dir0x) * invwy);
			final double dir1x = -0.5 * (c1 - discr) * invc2;
			dir[1] = new WB_Point2d(dir1x, (a - w.x * dir1x) * invwy);

		}

		return dir;
	}

	public static WB_Line2D getPerpendicularLineThroughPoint(final WB_Line2D L,
			final WB_Point2d p) {
		return new WB_Line2D(p, new WB_Point2d(-L.getDirection().y,
				L.getDirection().x));

	}

	public static WB_Line2D getParallelLineThroughPoint(final WB_Line2D L,
			final WB_Point2d p) {
		return new WB_Line2D(p, L.getDirection());

	}

	public static WB_Line2D getBisector(final WB_Point2d p, final WB_Point2d q) {
		return new WB_Line2D(WB_Point2d.interpolate(p, q, 0.5), new WB_Point2d(
				p.y - q.y, q.x - p.x));
	}

	public static WB_Line2D[] getParallelLines(final WB_Line2D L, final double d) {
		final WB_Line2D[] result = new WB_Line2D[2];
		result[0] = new WB_Line2D(
				new WB_Point2d(L.getOrigin().x - d * L.getDirection().y,
						L.getOrigin().y + d * L.getDirection().x),
				L.getDirection());
		result[1] = new WB_Line2D(
				new WB_Point2d(L.getOrigin().x + d * L.getDirection().y,
						L.getOrigin().y - d * L.getDirection().x),
				L.getDirection());
		return result;
	}

	public static WB_Line2D[] getPerpendicularLinesTangentToCircle(
			final WB_Line2D L, final WB_Circle C) {
		final WB_Line2D[] result = new WB_Line2D[2];
		result[0] = new WB_Line2D(new WB_Point2d(C.getCenter().x
				+ C.getRadius() * L.getDirection().x, C.getCenter().y
				+ C.getRadius() * L.getDirection().y), new WB_Point2d(
				-L.getDirection().y, L.getDirection().x));

		result[1] = new WB_Line2D(new WB_Point2d(C.getCenter().x
				- C.getRadius() * L.getDirection().x, C.getCenter().y
				- C.getRadius() * L.getDirection().y), new WB_Point2d(
				-L.getDirection().y, L.getDirection().x));
		return result;
	}

}