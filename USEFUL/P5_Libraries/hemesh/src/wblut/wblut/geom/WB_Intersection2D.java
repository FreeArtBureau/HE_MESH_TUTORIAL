/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.math.WB_Fast;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Intersection2D {

	/**
	 * Intersect2 d.
	 *
	 * @param S1 the s1
	 * @param S2 the s2
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult intersect2D(final WB_Segment2D S1,
			final WB_Segment2D S2) {
		final double a1 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
				S1.getOrigin(), S1.getEnd(), S2.getEnd());
		final double a2 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
				S1.getOrigin(), S1.getEnd(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEnd(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getPoint(t1);
				i.dimension = 0;
				i.sqDist = 0;
				return i;

			}

		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = 0;
		i.t2 = 0;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;

	}

	/**
	 * Intersect2 d into.
	 *
	 * @param S1 the s1
	 * @param S2 the s2
	 * @param i the i
	 */
	public static void intersect2DInto(final WB_Segment2D S1,
			final WB_Segment2D S2, final WB_IntersectionResult i) {
		final double a1 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
				S1.getOrigin(), S1.getEnd(), S2.getEnd());
		final double a2 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
				S1.getOrigin(), S1.getEnd(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_ExplicitTriangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEnd(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getPoint(t1);
				i.dimension = 0;
				i.sqDist = 0;

			}

		} else {
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
		}
	}

	public static WB_ExplicitSegment2D[] splitSegment2D(
			final WB_ExplicitSegment2D S, final WB_Line2D L) {
		final WB_ExplicitSegment2D[] result = new WB_ExplicitSegment2D[2];
		final WB_IntersectionResult ir2D = closestPoint2D(S, L);
		if (!ir2D.intersection) {
			return null;
		}
		if (ir2D.dimension == 0) {
			if (L.classifyPointToLine2D(S.getOrigin()) == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
				result[0] = new WB_ExplicitSegment2D(S.getOrigin(),
						(WB_Point2d) ir2D.object);
				result[1] = new WB_ExplicitSegment2D((WB_Point2d) ir2D.object,
						S.getEnd());
			} else if (L.classifyPointToLine2D(S.getOrigin()) == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
				result[1] = new WB_ExplicitSegment2D(S.getOrigin(),
						(WB_Point2d) ir2D.object);
				result[0] = new WB_ExplicitSegment2D((WB_Point2d) ir2D.object,
						S.getEnd());
			}
		}
		return result;

	}

	public static double[] intervalIntersection(final double u0,
			final double u1, final double v0, final double v1) {
		if ((u0 >= u1) || (v0 >= v1)) {
			throw new IllegalArgumentException(
					"Interval degenerate or reversed.");
		}
		final double[] result = new double[3];
		if ((u1 < v0) || (u0 > v1)) {
			return result;
		}
		if (u1 > v0) {
			if (u0 < v1) {
				result[0] = 2;
				if (u0 < v0) {
					result[1] = v0;
				} else {
					result[1] = u0;
				}
				if (u1 > v1) {
					result[2] = v1;
				} else {
					result[2] = u1;
				}
			} else {
				result[0] = 1;
				result[1] = u0;
			}
		} else {
			result[0] = 1;
			result[1] = u1;
		}
		return result;

	}

	public static WB_Polygon2D[] splitPolygon2D(final WB_Polygon2D poly,
			final WB_Line2D L) {
		int numFront = 0;
		int numBack = 0;

		final ArrayList<WB_Point2d> frontVerts = new ArrayList<WB_Point2d>(20);
		final ArrayList<WB_Point2d> backVerts = new ArrayList<WB_Point2d>(20);

		final int numVerts = poly.n;
		if (numVerts > 0) {
			WB_Point2d a = poly.points[numVerts - 1];
			WB_ClassifyPointToLine2D aSide = L.classifyPointToLine2D(a);
			WB_Point2d b;
			WB_ClassifyPointToLine2D bSide;

			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i = new WB_IntersectionResult();
				b = poly.points[n];
				bSide = L.classifyPointToLine2D(b);
				if (bSide == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
					if (aSide == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
						i = closestPoint2D(L, new WB_ExplicitSegment2D(a, b));
						WB_Point2d p1 = null;
						if (i.dimension == 0) {
							p1 = (WB_Point2d) i.object;
						} else if (i.dimension == 1) {
							p1 = ((WB_Segment2D) i.object).getOrigin();
						}
						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
					if (aSide == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
						i = closestPoint2D(L, new WB_ExplicitSegment2D(a, b));

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */
						final WB_Point2d p1 = (WB_Point2d) i.object;

						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					} else if (aSide == WB_ClassifyPointToLine2D.POINT_ON_LINE) {
						backVerts.add(a);
						numBack++;
					}
					backVerts.add(b);
					numBack++;
				} else {
					frontVerts.add(b);
					numFront++;
					if (aSide == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
						backVerts.add(b);
						numBack++;
					}
				}
				a = b;
				aSide = bSide;

			}

		}
		final WB_Polygon2D[] result = new WB_Polygon2D[2];
		result[0] = new WB_Polygon2D(frontVerts);
		result[1] = new WB_Polygon2D(backVerts);
		return result;

	}

	public static ArrayList<WB_Point2d> intersect2D(final WB_Circle C0,
			final WB_Circle C1) {
		final ArrayList<WB_Point2d> result = new ArrayList<WB_Point2d>();
		final WB_Point2d u = C1.getCenter().subAndCopy(C0.getCenter());
		final double d2 = u.mag2();
		final double d = Math.sqrt(d2);
		if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
			result.add(WB_Point2d.interpolate(C0.getCenter(), C1.getCenter(),
					C0.getRadius() / (C0.getRadius() + C1.getRadius())));
			return result;
		}
		if (d > (C0.getRadius() + C1.getRadius())
				|| d < WB_Fast.abs(C0.getRadius() - C1.getRadius())) {
			return result;
		}
		final double r02 = C0.getRadius() * C0.getRadius();
		final double r12 = C1.getRadius() * C1.getRadius();
		final double a = (r02 - r12 + d2) / (2 * d);
		final double h = Math.sqrt(r02 - a * a);
		final WB_Point2d c = u.multAndCopy(a / d).add(C0.getCenter());
		final double p0x = c.x + h * (C1.getCenter().y - C0.getCenter().y) / d;
		final double p0y = c.y - h * (C1.getCenter().x - C0.getCenter().x) / d;
		final double p1x = c.x - h * (C1.getCenter().y - C0.getCenter().y) / d;
		final double p1y = c.y + h * (C1.getCenter().x - C0.getCenter().x) / d;
		final WB_Point2d p0 = new WB_Point2d(p0x, p0y);
		result.add(p0);
		final WB_Point2d p1 = new WB_Point2d(p1x, p1y);
		if (!WB_Epsilon.isZeroSq(WB_Distance2D.sqDistance(p0, p1))) {
			result.add(new WB_Point2d(p1x, p1y));
		}
		return result;
	}

	public static ArrayList<WB_Point2d> intersect2D(final WB_Line2D L,
			final WB_Circle C) {
		final ArrayList<WB_Point2d> result = new ArrayList<WB_Point2d>();

		final double b = 2 * (L.getDirection().x
				* (L.getOrigin().x - C.getCenter().x) + L.getDirection().y
				* (L.getOrigin().y - C.getCenter().y));
		final double c = C.getCenter().mag2()
				+ L.getOrigin().mag2()
				- 2
				* (C.getCenter().x * L.getOrigin().x + C.getCenter().y
						* L.getOrigin().y) - C.getRadius() * C.getRadius();
		double disc = b * b - 4 * c;
		if (disc < -WB_Epsilon.EPSILON) {
			return result;
		}

		if (WB_Epsilon.isZero(disc)) {
			result.add(L.getPoint(-0.5 * b));
			return result;
		}
		disc = Math.sqrt(disc);
		result.add(L.getPoint(0.5 * (-b + disc)));
		result.add(L.getPoint(0.5 * (-b - disc)));
		return result;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static boolean getIntersection2DProper(final WB_Point2d a,
			final WB_Point2d b, final WB_Point2d c, final WB_Point2d d) {
		if (WB_Predicates2D.orient2d(a, b, c) == 0
				|| WB_Predicates2D.orient2d(a, b, d) == 0
				|| WB_Predicates2D.orient2d(c, d, a) == 0
				|| WB_Predicates2D.orient2d(c, d, b) == 0) {
			return false;
		} else if (WB_Predicates2D.orient2d(a, b, c)
				* WB_Predicates2D.orient2d(a, b, d) > 0
				|| WB_Predicates2D.orient2d(c, d, a)
						* WB_Predicates2D.orient2d(c, d, b) > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param S the s
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p,
			final WB_Segment2D S) {
		final WB_Point2d ab = S.getEnd().subAndCopy(S.getOrigin());
		final WB_Point2d ac = p.subAndCopy(S.getOrigin());
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return S.getOrigin().get();
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return S.getEnd().get();
			} else {
				t = t / denom;
				return new WB_Point2d(S.getPoint(t));
			}
		}
	}

	public static WB_Point2d closestPoint2D(final WB_Segment2D S,
			final WB_Point2d p) {
		return closestPoint2D(p, S);
	}

	/**
	 * Closest point to segment.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointToSegment2D(final WB_Point2d p,
			final WB_Point2d a, final WB_Point2d b) {
		final WB_Point2d ab = b.subAndCopy(a);
		final WB_Point2d ac = p.subAndCopy(a);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return a.get();
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return b.get();
			} else {
				t = t / denom;
				return new WB_Point2d(a.x + t * ab.x, a.y + t * ab.y);
			}
		}
	}

	/**
	 * Closest point to segment into.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @param result the result
	 */
	public static void closestPointToSegment2DInto(final WB_Point2d p,
			final WB_Point2d a, final WB_Point2d b, final WB_Point2d result) {
		final WB_Point2d ab = b.subAndCopy(a);
		final WB_Point2d ac = p.subAndCopy(a);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			result.set(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				result.set(b);
			} else {
				t = t / denom;
				result.set(a.x + t * ab.x, a.y + t * ab.y);
			}
		}
	}

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param L the l
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p,
			final WB_Line2D L) {

		if (WB_Epsilon.isZero(L.getDirection().x)) {
			return new WB_Point2d(L.getOrigin().x, p.y);
		}
		if (WB_Epsilon.isZero(L.getDirection().y)) {
			return new WB_Point2d(p.x, L.getOrigin().y);
		}

		final double m = L.getDirection().y / L.getDirection().x;
		final double b = L.getOrigin().y - m * L.getOrigin().x;

		final double x = (m * p.y + p.x - m * b) / (m * m + 1);
		final double y = (m * m * p.y + m * p.x + b) / (m * m + 1);

		return new WB_Point2d(x, y);

	}

	/**
	 * Closest point to line.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointToLine2D(final WB_Point2d p,
			final WB_Point2d a, final WB_Point2d b) {
		final WB_Line2D L = new WB_Line2D();
		L.setFromPoints(a, b);
		return closestPoint2D(p, L);
	}

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param R the r
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p, final WB_Ray2D R) {
		final WB_Point2d ac = p.subAndCopy(R.getOrigin());
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return R.getOrigin().get();
		} else {
			return R.getPoint(t);
		}
	}

	/**
	 * Closest point to ray.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointToRay2D(final WB_Point2d p,
			final WB_Point2d a, final WB_Point2d b) {
		final WB_Ray2D R = new WB_Ray2D();
		R.setFromPoints(a, b);
		return closestPoint2D(p, R);
	}

	/**
	 * Closest point.
	 *
	 * @param S1 the s1
	 * @param S2 the s2
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult closestPoint2D(final WB_Segment2D S1,
			final WB_Segment2D S2) {
		final WB_Point2d d1 = S1.getEnd().subAndCopy(S1.getOrigin());
		final WB_Point2d d2 = S2.getEnd().subAndCopy(S2.getOrigin());
		final WB_Point2d r = S1.getOrigin().subAndCopy(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);

		if (WB_Epsilon.isZero(a) || WB_Epsilon.isZero(e)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_ExplicitSegment2D(S1.getOrigin().get(), S2
					.getOrigin().get());
			i.dimension = 1;
			i.sqDist = r.mag2();
			return i;
		}

		double t1 = 0;
		double t2 = 0;
		if (WB_Epsilon.isZero(a)) {

			t2 = WB_Fast.clamp(f / e, 0, 1);
		} else {
			final double c = d1.dot(r);
			if (WB_Epsilon.isZero(e)) {

				t1 = WB_Fast.clamp(-c / a, 0, 1);
			} else {
				final double b = d1.dot(d2);
				final double denom = a * e - b * b;
				if (!WB_Epsilon.isZero(denom)) {
					t1 = WB_Fast.clamp((b * f - c * e) / denom, 0, 1);
				} else {
					t1 = 0;
				}
				final double tnom = b * t1 + f;
				if (tnom < 0) {
					t1 = WB_Fast.clamp(-c / a, 0, 1);
				} else if (tnom > e) {
					t2 = 1;
					t1 = WB_Fast.clamp((b - c) / a, 0, 1);
				} else {
					t2 = tnom / e;
				}
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = (t1 > 0) && (t1 < 1) && (t2 > 0) && (t2 < 1);
		i.t1 = t1;
		i.t2 = t2;
		final WB_Point2d p1 = S1.getPoint(t1);
		final WB_Point2d p2 = S2.getPoint(t2);
		i.sqDist = WB_Distance2D.sqDistance(p1, p2);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_ExplicitSegment2D(p1, p2);
		}
		return i;

	}

	/**
	 * Closest point.
	 *
	 * @param L1 the l1
	 * @param L2 the l2
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult closestPoint2D(final WB_Line2D L1,
			final WB_Line2D L2) {
		final double a = L1.getDirection().dot(L1.getDirection());
		final double b = L1.getDirection().dot(L2.getDirection());
		final WB_Point2d r = L1.getOrigin().subAndCopy(L2.getOrigin());
		final double c = L1.getDirection().dot(r);
		final double e = L2.getDirection().dot(L2.getDirection());
		final double f = L2.getDirection().dot(r);
		double denom = a * e - b * b;
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point2d p2 = new WB_Point2d(L2.getPoint(t2));
			final double d2 = WB_Distance2D
					.sqDistance(L1.getOrigin().get(), p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.dimension = 1;
			i.object = new WB_ExplicitSegment2D(L1.getOrigin().get(), p2);
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = (b * f - c * e) * denom;
		final double t2 = (a * f - b * c) * denom;
		final WB_Point2d p1 = new WB_Point2d(L1.getPoint(t1));
		final WB_Point2d p2 = new WB_Point2d(L2.getPoint(t2));
		final double d2 = WB_Distance2D.sqDistance(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;

	}

	public static WB_IntersectionResult closestPoint2D(final WB_Line2D L,
			final WB_Segment2D S) {
		final WB_IntersectionResult i = closestPoint2D(L,
				new WB_Line2D(S.getOrigin(), S.getDirection()));
		if (i.dimension == 0) {
			return i;
		}
		if (i.t2 <= WB_Epsilon.EPSILON) {
			i.t2 = 0;
			i.object = new WB_ExplicitSegment2D(
					((WB_Segment2D) i.object).getOrigin(), S.getOrigin().get());
			i.sqDist = ((WB_Segment2D) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		if (i.t2 >= S.getLength() - WB_Epsilon.EPSILON) {
			i.t2 = 1;
			i.object = new WB_ExplicitSegment2D(
					((WB_Segment2D) i.object).getOrigin(), S.getEnd().get());
			i.sqDist = ((WB_Segment2D) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		return i;
	}

	public static WB_IntersectionResult closestPoint2D(final WB_Segment2D S,
			final WB_Line2D L) {

		return closestPoint2D(L, S);
	}

	// POINT-TRIANGLE

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param T the t
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p,
			final WB_ExplicitTriangle2D T) {
		final WB_Point2d ab = T.p2.subAndCopy(T.p1);
		final WB_Point2d ac = T.p3.subAndCopy(T.p1);
		final WB_Point2d ap = p.subAndCopy(T.p1);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Point2d bp = p.subAndCopy(T.p2);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.addAndCopy(ab.mult(v));
		}

		final WB_Point2d cp = p.subAndCopy(T.p3);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.addAndCopy(ac.mult(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.addAndCopy((T.p3.subAndCopy(T.p2)).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1.addAndCopy(ab.mult(v).add(ac.mult(w)));
	}

	/**
	 * Closest point to triangle.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointToTriangle2D(final WB_Point2d p,
			final WB_Point2d a, final WB_Point2d b, final WB_Point2d c) {
		final WB_Point2d ab = b.subAndCopy(a);
		final WB_Point2d ac = c.subAndCopy(a);
		final WB_Point2d ap = p.subAndCopy(a);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return a.get();
		}

		final WB_Point2d bp = p.subAndCopy(b);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return b.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return a.addAndCopy(ab.mult(v));
		}

		final WB_Point2d cp = p.subAndCopy(c);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return c.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return a.addAndCopy(ac.mult(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return b.addAndCopy((c.subAndCopy(b)).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return a.addAndCopy(ab.mult(v).add(ac.mult(w)));
	}

	/**
	 * Closest point on periphery.
	 *
	 * @param p the p
	 * @param T the t
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointOnPeriphery2D(final WB_Point2d p,
			final WB_ExplicitTriangle2D T) {
		final WB_Point2d ab = T.p2.subAndCopy(T.p1);
		final WB_Point2d ac = T.p3.subAndCopy(T.p1);
		final WB_Point2d ap = p.subAndCopy(T.p1);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Point2d bp = p.subAndCopy(T.p2);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.addAndCopy(ab.mult(v));
		}

		final WB_Point2d cp = p.subAndCopy(T.p3);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.addAndCopy(ac.mult(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.addAndCopy((T.p3.subAndCopy(T.p2)).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3.subAndCopy(T.p2);
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1.get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2.get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3.get();
		}
		final WB_Point2d A = closestPointToSegment2D(p, T.p2, T.p3);
		final double dA2 = WB_Distance2D.sqDistance(p, A);
		final WB_Point2d B = closestPointToSegment2D(p, T.p1, T.p3);
		final double dB2 = WB_Distance2D.sqDistance(p, B);
		final WB_Point2d C = closestPointToSegment2D(p, T.p1, T.p2);
		final double dC2 = WB_Distance2D.sqDistance(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}

	}

	// POINT-POLYGON

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param poly the poly
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p,
			final WB_Polygon2D poly) {
		final List<WB_ExplicitTriangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point2d closest = new WB_Point2d();
		WB_Point2d tmp;
		WB_ExplicitTriangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.distance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	/**
	 * Closest point.
	 *
	 * @param p the p
	 * @param tris the tris
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPoint2D(final WB_Point2d p,
			final ArrayList<? extends WB_ExplicitTriangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point2d closest = new WB_Point2d();
		WB_Point2d tmp;
		WB_ExplicitTriangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.distance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	/**
	 * Closest point on periphery.
	 *
	 * @param p the p
	 * @param poly the poly
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointOnPeriphery2D(final WB_Point2d p,
			final WB_Polygon2D poly) {
		final List<WB_ExplicitTriangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point2d closest = new WB_Point2d();
		WB_Point2d tmp;
		WB_ExplicitTriangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.sqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_IndexedSegment2D S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment2D(j, i, poly.points);
				tmp = closestPoint2D(p, S);
				final double d2 = WB_Distance2D.sqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}

		return closest;
	}

	/**
	 * Closest point on periphery.
	 *
	 * @param p the p
	 * @param poly the poly
	 * @param tris the tris
	 * @return the w b_ point
	 */
	public static WB_Point2d closestPointOnPeriphery2D(final WB_Point2d p,
			final WB_Polygon2D poly, final ArrayList<WB_ExplicitTriangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point2d closest = new WB_Point2d();
		WB_Point2d tmp;
		WB_ExplicitTriangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.sqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment2D S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment2D(j, i, poly.points);
				tmp = closestPoint2D(p, S);
				final double d2 = WB_Distance2D.sqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}
		return closest;
	}

}
