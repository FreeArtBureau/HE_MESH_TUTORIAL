/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.math.WB_Fast;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Intersection {

	// SEGMENT-PLANE
	/**
	 * Intersect.
	 *
	 * @param S the s
	 * @param P the p
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Segment S,
			final WB_Plane P) {
		final WB_Vector3d ab = S.getEnd().subToVector(S.getOrigin());
		double t = (P.d() - P.getNormal().dot(S.getOrigin()))
				/ P.getNormal().dot(ab);
		if (t >= -WB_Epsilon.EPSILON && t <= 1.0 + WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = S.getPoint(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = t;
		i.t2 = t;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	/**
	 * Intersect.
	 *
	 * @param a the a
	 * @param b the b
	 * @param P the p
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Point3d a,
			final WB_Point3d b, final WB_Plane P) {
		final WB_Vector3d ab = b.subToVector(a);
		double t = (P.d() - P.getNormal().dot(a)) / P.getNormal().dot(ab);
		if (t >= -WB_Epsilon.EPSILON && t <= 1.0 + WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = new WB_Point3d(a.x + t * (b.x - a.x), a.y + t
					* (b.y - a.y), a.z + t * (b.z - a.z));
			i.dimension = 0;
			i.sqDist = 0;
			return i;

		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = 0;
		i.t2 = 0;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	// RAY-PLANE
	/**
	 * Intersect.
	 *
	 * @param R the s
	 * @param P the p
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Ray R,
			final WB_Plane P) {
		final WB_Vector3d ab = R.getDirection();
		double t = (P.d() - P.getNormal().dot(R.getOrigin()))
				/ P.getNormal().dot(ab);

		if (t >= -WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, Double.POSITIVE_INFINITY);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = R.getPoint(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = t;
		i.t2 = t;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	public static WB_IntersectionResult getIntersection(final WB_Ray R,
			final WB_AABB aabb) {
		final WB_Vector3d d = R.getDirection();
		final WB_Point3d p = R.getOrigin();
		double tmin = 0.0;
		double tmax = Double.POSITIVE_INFINITY;
		if (WB_Epsilon.isZero(d.x)) {
			if ((p.x < aabb.min.x) || (p.x > aabb.max.x)) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.x;
			double t1 = (aabb.min.x - p.x) * ood;
			double t2 = (aabb.max.x - p.x) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}
		if (WB_Epsilon.isZero(d.y)) {
			if ((p.y < aabb.min.y) || (p.y > aabb.max.y)) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.y;
			double t1 = (aabb.min.y - p.y) * ood;
			double t2 = (aabb.max.y - p.y) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}
		if (WB_Epsilon.isZero(d.z)) {
			if ((p.z < aabb.min.z) || (p.z > aabb.max.z)) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.z;
			double t1 = (aabb.min.z - p.z) * ood;
			double t2 = (aabb.max.z - p.z) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}

		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = tmin;
		i.t2 = 0;
		i.object = R.getPoint(tmin);
		i.dimension = 0;
		i.sqDist = WB_Distance.sqDistance(p, (WB_Point3d) i.object);

		return i;
	}

	// LINE-PLANE

	/**
	 * Intersect.
	 *
	 * @param L the l
	 * @param P the p
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Line L,
			final WB_Plane P) {
		final WB_Vector3d ab = L.getDirection();
		final double denom = P.getNormal().dot(ab);
		if (!WB_Epsilon.isZero(denom)) {
			final double t = (P.d() - P.getNormal().dot(L.getOrigin())) / denom;

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = L.getPoint(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		} else {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		}
	}

	// PLANE-PLANE

	/**
	 * Intersect.
	 *
	 * @param P1 the p1
	 * @param P2 the p2
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Plane P1,
			final WB_Plane P2) {

		final WB_Normal3d N1 = P1.getNormal().get();
		final WB_Normal3d N2 = P2.getNormal().get();
		final WB_Vector3d N1xN2 = new WB_Vector3d(N1.cross(N2));
		final double d1 = P1.d();
		final double d2 = P2.d();
		if (WB_Epsilon.isZeroSq(N1xN2.mag2())) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		} else {
			final double N1N2 = N1.dot(N2);
			final double det = 1 - N1N2 * N1N2;
			final double c1 = (d1 - d2 * N1N2) / det;
			final double c2 = (d2 - d1 * N1N2) / det;
			final WB_Point3d O = new WB_Point3d(N1.multAndCopy(c1).add(
					N2.multAndCopy(c2)));

			final WB_Line L = new WB_Line(O, N1xN2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Line(O, N1xN2);
			i.dimension = 1;
			i.sqDist = 0;
			return i;

		}

	}

	// PLANE-PLANE-PLANE
	/**
	 * Intersect.
	 *
	 * @param P1 the p1
	 * @param P2 the p2
	 * @param P3 the p3
	 * @return the w b_ intersection
	 */
	public static WB_IntersectionResult getIntersection(final WB_Plane P1,
			final WB_Plane P2, final WB_Plane P3) {

		final WB_Normal3d N1 = P1.getNormal().get();
		final WB_Normal3d N2 = P2.getNormal().get();
		final WB_Normal3d N3 = P3.getNormal().get();

		final double denom = N1.dot(N2.cross(N3));

		if (WB_Epsilon.isZero(denom)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		} else {
			final WB_Point3d N1xN2 = new WB_Point3d(N1.cross(N2));
			final WB_Point3d N2xN3 = new WB_Point3d(N2.cross(N3));
			final WB_Point3d N3xN1 = new WB_Point3d(N3.cross(N1));
			final double d1 = P1.d();
			final double d2 = P2.d();
			final double d3 = P3.d();
			final WB_Point3d p = N2xN3.multAndCopy(d1);
			p.add(N3xN1.multAndCopy(d2));
			p.add(N1xN2.multAndCopy(d3));
			p.div(denom);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = p;
			i.dimension = 0;
			i.sqDist = 0;
			return i;

		}

	}

	// AABB-AABB

	/**
	 * Check intersection.
	 *
	 * @param one the one
	 * @param other the other
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_AABB one,
			final WB_AABB other) {
		if (one.max.x < other.min.x || one.min.x > other.max.x) {
			return false;
		}
		if (one.max.y < other.min.y || one.min.y > other.max.y) {
			return false;
		}
		if (one.max.z < other.min.z || one.min.z > other.max.z) {
			return false;
		}
		return true;
	}

	// OBB-OBB

	/**
	 * Check intersection.
	 *
	 * @param AABB the aABB
	 * @param P the p
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_AABB AABB, final WB_Plane P) {
		final WB_Point3d c = AABB.max.addAndCopy(AABB.min).mult(0.5);
		final WB_Point3d e = AABB.max.subAndCopy(c);
		final double r = e.x * WB_Fast.abs(P.getNormal().x) + e.y
				* WB_Fast.abs(P.getNormal().y) + e.z
				* WB_Fast.abs(P.getNormal().z);
		final double s = P.getNormal().dot(c) - P.d();
		return WB_Fast.abs(s) <= r;
	}

	// OBB-PLANE

	/**
	 * Check intersection.
	 *
	 * @param AABB the aABB
	 * @param S the s
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_AABB AABB,
			final WB_Sphere S) {
		final double d2 = WB_Distance.sqDistance(S.getCenter(), AABB);
		return d2 <= S.getRadius() * S.getRadius();
	}

	// OBB-SPHERE

	/**
	 * Check intersection.
	 *
	 * @param T the t
	 * @param S the s
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_Triangle T,
			final WB_Sphere S) {
		final WB_Point3d p = closestPoint(S.getCenter(), T);

		return (p.subToVector(S.getCenter())).mag2() <= S.getRadius()
				* S.getRadius();
	}

	// TRIANGLE-AABB

	/**
	 * Check intersection.
	 *
	 * @param T the t
	 * @param AABB the aABB
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_Triangle T,
			final WB_AABB AABB) {
		double p0, p1, p2, r;
		final WB_Point3d c = AABB.max.addAndCopy(AABB.min).mult(0.5);
		final double e0 = (AABB.max.x - AABB.min.x) * 0.5;
		final double e1 = (AABB.max.y - AABB.min.y) * 0.5;
		final double e2 = (AABB.max.z - AABB.min.z) * 0.5;
		final WB_Point3d v0 = T.p1().get();
		final WB_Point3d v1 = T.p2().get();
		final WB_Point3d v2 = T.p3().get();

		v0.sub(c);
		v1.sub(c);
		v2.sub(c);

		final WB_Vector3d f0 = v1.subToVector(v0);
		final WB_Vector3d f1 = v2.subToVector(v1);
		final WB_Vector3d f2 = v0.subToVector(v2);

		// a00
		final WB_Vector3d a = new WB_Vector3d(0, -f0.z, f0.y);// u0xf0
		if (a.isZero()) {
			a.set(0, v0.y, v0.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a01
		a.set(0, -f1.z, f1.y);// u0xf1
		if (a.isZero()) {
			a.set(0, v1.y, v1.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a02
		a.set(0, -f2.z, f2.y);// u0xf2
		if (a.isZero()) {
			a.set(0, v2.y, v2.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a10
		a.set(f0.z, 0, -f0.x);// u1xf0
		if (a.isZero()) {
			a.set(v0.x, 0, v0.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a11
		a.set(f1.z, 0, -f1.x);// u1xf1
		if (a.isZero()) {
			a.set(v1.x, 0, v1.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a12
		a.set(f2.z, 0, -f2.x);// u1xf2
		if (a.isZero()) {
			a.set(v2.x, 0, v2.z);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a20
		a.set(-f0.y, f0.x, 0);// u2xf0
		if (a.isZero()) {
			a.set(v0.x, v0.y, 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a21
		a.set(-f1.y, f1.x, 0);// u2xf1
		if (a.isZero()) {
			a.set(v1.x, v1.y, 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a22
		a.set(-f2.y, f2.x, 0);// u2xf2
		if (a.isZero()) {
			a.set(v2.x, v2.y, 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Fast.abs(a.x) + e1 * WB_Fast.abs(a.y) + e2
					* WB_Fast.abs(a.z);
			if (WB_Fast.max(WB_Fast.min(p0, p1, p2), -WB_Fast.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		if (WB_Fast.max(v0.x, v1.x, v2.x) < -e0
				|| WB_Fast.max(v0.x, v1.x, v2.x) > e0) {
			return false;
		}
		if (WB_Fast.max(v0.y, v1.y, v2.y) < -e1
				|| WB_Fast.max(v0.y, v1.y, v2.y) > e1) {
			return false;
		}
		if (WB_Fast.max(v0.z, v1.z, v2.z) < -e2
				|| WB_Fast.max(v0.z, v1.z, v2.z) > e2) {
			return false;
		}

		WB_Vector3d n = f0.cross(f1);
		WB_Plane P;
		if (!n.isZero()) {
			P = new WB_Plane(n, n.dot(v0));
		} else {
			n = f0.cross(f2);
			n = f0.cross(n);
			if (!n.isZero()) {
				P = new WB_Plane(n, n.dot(v0));
			} else {
				final WB_Vector3d t = T.p3().subToVector(T.p1());
				final double a1 = T.p1().dot(t);
				final double a2 = T.p2().dot(t);
				final double a3 = T.p3().dot(t);
				if (a1 < WB_Fast.min(a2, a3)) {
					if (a2 < a3) {
						return checkIntersection(new WB_ExplicitSegment(T.p1(),
								T.p3()), AABB);
					} else {
						return checkIntersection(new WB_ExplicitSegment(T.p1(),
								T.p2()), AABB);
					}
				} else if (a2 < WB_Fast.min(a1, a3)) {
					if (a1 < a3) {
						return checkIntersection(new WB_ExplicitSegment(T.p2(),
								T.p3()), AABB);
					} else {
						return checkIntersection(new WB_ExplicitSegment(T.p2(),
								T.p1()), AABB);
					}
				} else {
					if (a1 < a2) {
						return checkIntersection(new WB_ExplicitSegment(T.p3(),
								T.p2()), AABB);
					} else {
						return checkIntersection(new WB_ExplicitSegment(T.p3(),
								T.p1()), AABB);
					}
				}

			}

		}
		return checkIntersection(AABB, P);

	}

	// SEGMENT-AABB
	/**
	 * Check intersection.
	 *
	 * @param S the s
	 * @param AABB the aABB
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_Segment S,
			final WB_AABB AABB) {
		final WB_Vector3d e = AABB.max.subToVector(AABB.min);
		final WB_Vector3d d = S.getEnd().subToVector(S.getOrigin());
		final WB_Point3d m = new WB_Point3d(S.getEnd().x + S.getOrigin().x
				- AABB.min.x - AABB.max.x, S.getEnd().y + S.getOrigin().y
				- AABB.min.y - AABB.max.y, S.getEnd().z + S.getOrigin().z
				- AABB.min.z - AABB.max.z);
		double adx = WB_Fast.abs(d.x);
		if (WB_Fast.abs(m.x) > e.x + adx) {
			return false;
		}
		double ady = WB_Fast.abs(d.y);
		if (WB_Fast.abs(m.y) > e.y + ady) {
			return false;
		}
		double adz = WB_Fast.abs(d.z);
		if (WB_Fast.abs(m.z) > e.z + adz) {
			return false;
		}
		adx += WB_Epsilon.EPSILON;
		ady += WB_Epsilon.EPSILON;
		adz += WB_Epsilon.EPSILON;
		if (WB_Fast.abs(m.y * d.z - m.z * d.y) > e.y * adz + e.z * ady) {
			return false;
		}
		if (WB_Fast.abs(m.z * d.x - m.x * d.z) > e.x * adz + e.z * adx) {
			return false;
		}
		if (WB_Fast.abs(m.x * d.y - m.y * d.x) > e.x * ady + e.y * adx) {
			return false;
		}
		return true;

	}

	// SPHERE-SPHERE

	/**
	 * Check intersection.
	 *
	 * @param S1 the s1
	 * @param S2 the s2
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_Sphere S1,
			final WB_Sphere S2) {
		final WB_Vector3d d = S1.getCenter().subToVector(S2.getCenter());
		final double d2 = d.mag2();
		final double radiusSum = S1.getRadius() + S2.getRadius();
		return d2 <= radiusSum * radiusSum;
	}

	// RAY-SPHERE

	/**
	 * Check intersection.
	 *
	 * @param R the r
	 * @param S the s
	 * @return true, if successful
	 */
	public static boolean checkIntersection(final WB_Ray R, final WB_Sphere S) {
		final WB_Vector3d m = R.getOrigin().subToVector(S.getCenter());
		final double c = m.dot(m) - S.getRadius() * S.getRadius();
		if (c <= 0) {
			return true;
		}
		final double b = m.dot(R.getDirection());
		if (b >= 0) {
			return false;
		}
		final double disc = b * b - c;
		if (disc < 0) {
			return false;
		}
		return true;
	}

	public static boolean checkIntersection(final WB_Ray R, final WB_AABB AABB) {
		double t0 = 0;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / R.direction.x;
		double tnear = (AABB.min.x - R.origin.x) * irx;
		double tfar = (AABB.max.x - R.origin.x) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / R.direction.y;
		tnear = (AABB.min.y - R.origin.y) * iry;
		tfar = (AABB.max.y - R.origin.y) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / R.direction.z;
		tnear = (AABB.min.z - R.origin.z) * irz;
		tfar = (AABB.max.z - R.origin.z) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}

	public static ArrayList<WB_AABBNode> getIntersection(final WB_Ray R,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection(R, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static boolean checkIntersection(final WB_Line L, final WB_AABB AABB) {
		double t0 = Double.NEGATIVE_INFINITY;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / L.direction.x;
		double tnear = (AABB.min.x - L.origin.x) * irx;
		double tfar = (AABB.max.x - L.origin.x) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / L.direction.y;
		tnear = (AABB.min.y - L.origin.y) * iry;
		tfar = (AABB.max.y - L.origin.y) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / L.direction.z;
		tnear = (AABB.min.z - L.origin.z) * irz;
		tfar = (AABB.max.z - L.origin.z) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}

	public static ArrayList<WB_AABBNode> getIntersection(final WB_Line L,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection(L, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_AABBNode> getIntersection(final WB_Segment S,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection(S, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_AABBNode> getIntersection(final WB_Plane P,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection(current.getAABB(), P)) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_ExplicitSegment> getIntersection(
			final WB_Polygon poly, final WB_Plane P) {

		final WB_ClassifyPolygonToPlane cptp = P.classifyPolygonToPlane(poly);
		final ArrayList<WB_ExplicitSegment> result = new ArrayList<WB_ExplicitSegment>();
		/*
		 * if (cptp == WB_ClassifyPolygonToPlane.POLYGON_ON_PLANE) { return
		 * poly.toSegments(); } if ((cptp ==
		 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE) || (cptp ==
		 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE)) { return result; }
		 */
		final ArrayList<WB_Point3d> splitVerts = new ArrayList<WB_Point3d>();
		final int numVerts = poly.getN();
		if (numVerts > 0) {
			WB_Point3d a = poly.getPoint(numVerts - 1);
			WB_ClassifyPointToPlane aSide = P.classifyPointToPlane(a);
			WB_Point3d b;
			WB_ClassifyPointToPlane bSide;
			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i;
				b = poly.getPoint(n);
				bSide = P.classifyPointToPlane(b);
				if (bSide == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
					if (aSide == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
						i = WB_Intersection.getIntersection(b, a, P);
						splitVerts.add((WB_Point3d) i.object);
					}
				} else if (bSide == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
					if (aSide == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
						i = WB_Intersection.getIntersection(a, b, P);
						splitVerts.add((WB_Point3d) i.object);
					}
				}
				if (aSide == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
					splitVerts.add(a);

				}
				a = b;
				aSide = bSide;

			}
		}

		for (int i = 0; i < splitVerts.size(); i += 2) {
			if (splitVerts.get(i + 1) != null) {
				result.add(new WB_ExplicitSegment(splitVerts.get(i), splitVerts
						.get(i + 1)));
			}

		}

		return result;

	}

	/**
	 * Closest points between two segments.
	 *
	 * @param S1 first segment
	 * @param S2 second segment
	 * @return WB_IntersectionResult
	 */
	public static WB_IntersectionResult getIntersection(final WB_Segment S1,
			final WB_Segment S2) {
		final WB_Vector3d d1 = new WB_Vector3d(S1.getEnd());
		d1.sub(S1.getOrigin());
		final WB_Vector3d d2 = new WB_Vector3d(S2.getEnd());
		d2.sub(S2.getOrigin());
		final WB_Vector3d r = new WB_Vector3d(S1.getOrigin());
		r.sub(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);

		if (WB_Epsilon.isZero(a) && WB_Epsilon.isZero(e)) {
			// Both segments are degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.mag2();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_ExplicitSegment(S1.getOrigin(),
						S2.getOrigin());
			}
			return i;
		}

		if (WB_Epsilon.isZero(a)) {
			// First segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.mag2();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_ExplicitSegment(S1.getOrigin(), closestPoint(
						S1.getOrigin(), S2));
			}
			return i;
		}

		if (WB_Epsilon.isZero(e)) {
			// Second segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.mag2();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S2.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_ExplicitSegment(S2.getOrigin(), closestPoint(
						S2.getOrigin(), S1));
			}
			return i;
		}

		double t1 = 0;
		double t2 = 0;
		final double c = d1.dot(r);
		final double b = d1.dot(d2);
		final double denom = a * e - b * b;

		if (!WB_Epsilon.isZero(denom)) {
			// Non-parallel segments
			t1 = WB_Fast.clamp((b * f - c * e) / denom, 0, 1);
		} else {
			// Parallel segments, non-parallel code handles case where
			// projections of segments are disjoint.
			final WB_Line L1 = new WB_Line(S1.getOrigin(), S1.getDirection());
			double s1 = 0;
			double e1 = WB_Geom.pointAlongLine(S1.getEnd(), L1);
			double s2 = WB_Geom.pointAlongLine(S2.getOrigin(), L1);
			double e2 = WB_Geom.pointAlongLine(S2.getEnd(), L1);
			double tmp;
			if (e2 < s2) {
				tmp = s2;
				s2 = e2;
				e2 = tmp;
			}
			if (s2 < s1) {
				tmp = s2;
				s2 = s1;
				s1 = tmp;
				tmp = e2;
				e2 = e1;
				e1 = tmp;
			}

			if (s2 < e1) {
				// Projections are overlapping
				final WB_Point3d start = L1.getPoint(s2);
				WB_Point3d end = L1.getPoint(Math.min(e1, e2));

				if (WB_Epsilon.isZeroSq(WB_Distance.sqDistance(S2.getOrigin(),
						L1))) {
					// Segments are overlapping
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = WB_Distance.sqDistance(start, end);
					i.intersection = true;
					if (WB_Epsilon.isZeroSq(i.sqDist)) {
						i.dimension = 0;
						i.object = start;
					} else {
						i.dimension = 1;
						i.object = new WB_ExplicitSegment(start, end);
					}
					return i;
				} else {
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = WB_Distance.sqDistance(start, end);
					i.intersection = false;
					i.dimension = 1;
					start.add(end);
					start.scale(0.5);
					end = closestPoint(start, S2);
					i.object = new WB_ExplicitSegment(start, end);
					return i;
				}
			}
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

		final WB_IntersectionResult i = new WB_IntersectionResult();
		final WB_Point3d p1 = S1.getPoint(t1);
		final WB_Point3d p2 = S2.getPoint(t2);
		i.sqDist = WB_Distance.sqDistance(p1, p2);
		i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_ExplicitSegment(p1, p2);
		}
		return i;

	}

	/**
	 * Closest point on plane.
	 *
	 * @param p point
	 * @param P plane
	 * @return closest point on plane
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p, final WB_Plane P) {
		final WB_Normal3d n = P.getNormal();
		final double t = n.dot(p) - P.d();
		return new WB_Point3d(p.x - t * n.x, p.y - t * n.y, p.z - t * n.z);
	}

	/**
	 * Closest point on plane.
	 *
	 * @param p point
	 * @param P plane
	 * @return closest point on plane
	 */
	public static WB_Point3d closestPoint(final WB_Plane P, final WB_Point3d p) {
		return closestPoint(P, p);
	}

	/**
	 * Closest point on segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return closest point on segment
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p, final WB_Segment S) {
		final WB_Vector3d ab = S.getEnd().subToVector(S.getOrigin());
		final WB_Vector3d ac = p.subToVector(S.getOrigin());
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
				return new WB_Point3d(S.getPoint(t));
			}
		}
	}

	/**
	 * Closest point on segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return closest point on segment
	 */
	public static WB_Point3d closestPoint(final WB_Segment S, final WB_Point3d p) {
		return closestPoint(p, S);
	}

	/**
	 * Closest point on segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return parameterized position t of closest point on segment (0=origin, 1=end)
	 */
	public static double closestPointT(final WB_Point3d p, final WB_Segment S) {
		final WB_Vector3d ab = S.getEnd().subToVector(S.getOrigin());
		final WB_Vector3d ac = p.subToVector(S.getOrigin());
		double t = ac.dot(ab);
		if (t <= WB_Epsilon.EPSILON) {
			return 0;
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= (denom - WB_Epsilon.EPSILON)) {
				t = 1;
				return 1;
			} else {
				t = t / denom;
				return t;
			}
		}
	}

	/**
	 * Closest point on segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return parameterized position t of closest point on segment (0=origin, 1=end)
	 */
	public static double closestPointT(final WB_Segment S, final WB_Point3d p) {
		return closestPointT(p, S);
	}

	/**
	 * Closest point to segment.
	 *
	 * @param p point
	 * @param a start point
	 * @param b end point
	 * @return closest point on segment
	 */
	public static WB_Point3d closestPointToSegment(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		final WB_Vector3d ab = b.subToVector(a);
		final WB_Vector3d ac = p.subToVector(a);
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
				return new WB_Point3d(a.x + t * ab.x, a.y + t * ab.y, a.z + t
						* ab.z);
			}
		}
	}

	/**
	 * Closest point on line.
	 *
	 * @param p point
	 * @param L line
	 * @return closest point on line
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p, final WB_Line L) {
		final WB_Vector3d ca = new WB_Vector3d(p.x - L.getOrigin().y, p.y
				- L.getOrigin().x, p.z - L.getOrigin().z);
		return L.getPoint(ca.dot(L.getDirection()));
	}

	/**
	 * Closest point on line.
	 *
	 * @param p point
	 * @param a point on line
	 * @param b point on line
	 * @return closest point on line
	 */
	public static WB_Point3d closestPointToLine(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		return closestPoint(p, new WB_Line(a, b));
	}

	/**
	 * Closest point on ray.
	 *
	 * @param p point
	 * @param R ray
	 * @return closest point on ray
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p, final WB_Ray R) {
		final WB_Vector3d ac = p.subToVector(R.getOrigin());
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return R.getOrigin().get();
		} else {
			return new WB_Point3d(R.getPoint(t));
		}
	}

	/**
	 * Closest point on ray.
	 *
	 * @param p point
	 * @param a start point
	 * @param b point on ray
	 * @return closest point on ray
	 */
	public static WB_Point3d closestPointToRay(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		return closestPoint(p, new WB_Ray(a, b));
	}

	/**
	 * Closest point on axis-aligned box.
	 *
	 * @param p point
	 * @param AABB AABB
	 * @return closest point on axis-aligned box
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p, final WB_AABB AABB) {
		final WB_Point3d result = new WB_Point3d();
		double v = p.x;
		if (v < AABB.min.x) {
			v = AABB.min.x;
		}
		if (v > AABB.max.x) {
			v = AABB.max.x;
		}
		result.x = v;
		v = p.y;
		if (v < AABB.min.y) {
			v = AABB.min.y;
		}
		if (v > AABB.max.y) {
			v = AABB.max.y;
		}
		result.y = v;
		v = p.z;
		if (v < AABB.min.z) {
			v = AABB.min.z;
		}
		if (v > AABB.max.z) {
			v = AABB.max.z;
		}
		result.z = v;
		return result;
	}

	/**
	 * Closest point on axis-aligned box.
	 *
	 * @param p point
	 * @param AABB AABB
	 * @param result
	 */
	public static void closestPoint(final WB_Point3d p, final WB_AABB AABB,
			final WB_Point3d result) {
		double v = p.x;
		if (v < AABB.min.x) {
			v = AABB.min.x;
		}
		if (v > AABB.max.x) {
			v = AABB.max.x;
		}
		result.x = v;
		v = p.y;
		if (v < AABB.min.y) {
			v = AABB.min.y;
		}
		if (v > AABB.max.y) {
			v = AABB.max.y;
		}
		result.y = v;
		v = p.z;
		if (v < AABB.min.z) {
			v = AABB.min.z;
		}
		if (v > AABB.max.z) {
			v = AABB.max.z;
		}
		result.z = v;
	}

	// POINT-TRIANGLE

	/**
	 * Closest point on triangle.
	 *
	 * @param p point
	 * @param T triangle
	 * @return closest point on triangle
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p,
			final WB_Triangle T) {
		final WB_Vector3d ab = T.p2().subToVector(T.p1());
		final WB_Vector3d ac = T.p3().subToVector(T.p1());
		final WB_Vector3d ap = p.subToVector(T.p1());
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1().get();
		}

		final WB_Vector3d bp = p.subToVector(T.p2());
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2().get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1().addAndCopy(ab.mult(v));
		}

		final WB_Vector3d cp = p.subToVector(T.p3());
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3().get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1().addAndCopy(ac.mult(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2().addAndCopy((T.p3().subToVector(T.p2())).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1().addAndCopy(ab.mult(v).add(ac.mult(w)));
	}

	/**
	 * Closest point on triangle.
	 *
	 * @param p point
	 * @param a first point
	 * @param b second point
	 * @param c third point
	 * @return closest point on triangle
	 */
	public static WB_Point3d closestPointToTriangle(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b, final WB_Point3d c) {
		final WB_Vector3d ab = b.subToVector(a);
		final WB_Vector3d ac = c.subToVector(a);
		final WB_Vector3d ap = p.subToVector(a);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return a.get();
		}

		final WB_Vector3d bp = p.subToVector(b);
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

		final WB_Vector3d cp = p.subToVector(c);
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
			return b.addAndCopy((c.subToVector(b)).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return a.addAndCopy(ab.mult(v).add(ac.mult(w)));
	}

	/**
	 * Closest point on periphery of triangle.
	 *
	 * @param p point
	 * @param T triangle
	 * @return closest point on periphery of triangle
	 */
	public static WB_Point3d closestPointOnPeriphery(final WB_Point3d p,
			final WB_Triangle T) {
		final WB_Vector3d ab = T.p2().subToVector(T.p1());
		final WB_Vector3d ac = T.p3().subToVector(T.p1());
		final WB_Vector3d ap = p.subToVector(T.p1());
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1().get();
		}

		final WB_Vector3d bp = p.subToVector(T.p2());
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2().get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1().addAndCopy(ab.mult(v));
		}

		final WB_Vector3d cp = p.subToVector(T.p3());
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3().get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1().addAndCopy(ac.mult(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2().addAndCopy((T.p3().subToVector(T.p2())).mult(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3().subToVector(T.p2());
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1().get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2().get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3().get();
		}
		final WB_Point3d A = closestPointToSegment(p, T.p2(), T.p3());
		final double dA2 = WB_Distance.sqDistance(p, A);
		final WB_Point3d B = closestPointToSegment(p, T.p1(), T.p3());
		final double dB2 = WB_Distance.sqDistance(p, B);
		final WB_Point3d C = closestPointToSegment(p, T.p1(), T.p2());
		final double dC2 = WB_Distance.sqDistance(p, C);
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
	 * Closest point on polygon.
	 *
	 * @param p point
	 * @param poly polygon
	 * @return closest point on polygon
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p,
			final WB_Polygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point3d closest = new WB_Point3d();
		WB_Point3d tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint(p, T);
			final double d2 = WB_Distance.distance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	/**
	 * Closest point on triangulated polygon.
	 *
	 * @param p point
	 * @param tris triangulation of polygon
	 * @return closest point on polygon
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p,
			final List<? extends WB_Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point3d closest = new WB_Point3d();
		WB_Point3d tmp;
		WB_Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint(p, T);
			final double d2 = WB_Distance.distance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	/**
	 * Closest point on periphery of polygon.
	 *
	 * @param p point
	 * @param poly polygon
	 * @return closest point on periphery of polygon
	 */
	public static WB_Point3d closestPointOnPeriphery(final WB_Point3d p,
			final WB_Polygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point3d closest = new WB_Point3d();
		WB_Point3d tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint(p, T);
			final double d2 = WB_Distance.sqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_IndexedSegment S;
			for (int i = 0, j = poly.getN() - 1; i < poly.getN(); j = i, i++) {
				S = new WB_IndexedSegment(poly.getIndex(j), poly.getIndex(i),
						poly.getPoints());
				tmp = closestPoint(p, S);
				final double d2 = WB_Distance.sqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}

		return closest;
	}

	/**
	 * Closest point on periphery of triangulated polygon.
	 *
	 * @param p point
	 * @param tris triangulation of polygon
	 * @return closest point on polygon
	 */
	public static WB_Point3d closestPointOnPeriphery(final WB_Point3d p,
			final WB_Polygon poly, final List<WB_IndexedTriangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point3d closest = new WB_Point3d();
		WB_Point3d tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint(p, T);
			final double d2 = WB_Distance.sqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_ExplicitSegment S;
			for (int i = 0, j = poly.getN() - 1; i < poly.getN(); j = i, i++) {
				S = new WB_ExplicitSegment(poly.getPoint(j), poly.getPoint(i));
				tmp = closestPoint(p, S);
				final double d2 = WB_Distance.sqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}
		return closest;
	}

	// LINE-LINE
	/**
	 * Closest point between two lines.
	 *
	 * @param L1 first line
	 * @param L2 second line
	 * @return WB_IntersectionResult
	 */
	public static WB_IntersectionResult closestPoint(final WB_Line L1,
			final WB_Line L2) {
		final double a = L1.getDirection().dot(L1.getDirection());
		final double b = L1.getDirection().dot(L2.getDirection());
		final WB_Vector3d r = L1.getOrigin().subToVector(L2.getOrigin());
		final double c = L1.getDirection().dot(r);
		final double e = L2.getDirection().dot(L2.getDirection());
		final double f = L2.getDirection().dot(r);
		double denom = a * e - b * b;
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point3d p2 = new WB_Point3d(L2.getPoint(t2));
			final double d2 = WB_Distance.sqDistance(L1.getOrigin().get(), p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.object = new WB_ExplicitSegment(L1.getOrigin().get(), p2);
			i.dimension = 1;
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = (b * f - c * e) * denom;
		final double t2 = (a * f - b * c) * denom;
		final WB_Point3d p1 = new WB_Point3d(L1.getPoint(t1));
		final WB_Point3d p2 = new WB_Point3d(L2.getPoint(t2));
		final double d2 = WB_Distance.sqDistance(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;

	}

	// POINT-TETRAHEDRON
	/**
	 * Closest point on tetrahedron.
	 *
	 * @param p point
	 * @param T tetrahedron
	 * @return closest point on tetrahedron
	 */
	public static WB_Point3d closestPoint(final WB_Point3d p,
			final WB_Tetrahedron T) {
		WB_Point3d closestPt = p.get();
		double bestSqDist = Double.POSITIVE_INFINITY;
		if (WB_Plane.pointOtherSideOfPlane(p, T.p4, T.p1, T.p2, T.p3)) {
			final WB_Point3d q = closestPointToTriangle(p, T.p1, T.p2, T.p3);
			final double sqDist = (q.subToVector(p)).mag2();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p2, T.p1, T.p3, T.p4)) {
			final WB_Point3d q = closestPointToTriangle(p, T.p1, T.p3, T.p4);
			final double sqDist = (q.subToVector(p)).mag2();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p3, T.p1, T.p4, T.p2)) {
			final WB_Point3d q = closestPointToTriangle(p, T.p1, T.p4, T.p2);
			final double sqDist = (q.subToVector(p)).mag2();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p1, T.p2, T.p4, T.p3)) {
			final WB_Point3d q = closestPointToTriangle(p, T.p2, T.p4, T.p3);
			final double sqDist = (q.subToVector(p)).mag2();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		return new WB_Point3d(closestPt);

	}

}
