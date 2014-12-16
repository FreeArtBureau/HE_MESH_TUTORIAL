/**
 * 
 */
package wblut.geom;

import java.util.List;

import wblut.WB_Epsilon;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Distance {
	// POINT-POINT
	/**
	 * Squared distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Point3d q) {
		return ((q.x - p.x) * (q.x - p.x) + (q.y - p.y) * (q.y - p.y) + (q.z - p.z)
				* (q.z - p.z));
	}

	/**
	 * Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_Point3d q) {
		return Math.sqrt(sqDistance(p, q));
	}

	/**
	 * Squared distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point4d p, final WB_Point4d q) {
		return ((q.x - p.x) * (q.x - p.x) + (q.y - p.y) * (q.y - p.y)
				+ (q.z - p.z) * (q.z - p.z) + (q.w - p.w) * (q.w - p.w));
	}

	/**
	 * Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double distance(final WB_Point4d p, final WB_Point4d q) {
		return Math.sqrt(sqDistance(p, q));
	}

	// POINT-PLANE

	/**
	 * Squared distance between point and plane.
	 *
	 * @param p point
	 * @param P plane
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}

	/**
	 * Distance between point and plane.
	 *
	 * @param p point
	 * @param P plane
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_Plane P) {
		return P.getNormal().dot(p) - P.d();
	}

	// POINT-SEGMENT
	/**
	 * Squared distance between point and segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Segment S) {
		final WB_Vector3d ab = S.getEnd().subToVector(S.getOrigin());
		final WB_Vector3d ac = p.subToVector(S.getOrigin());
		final WB_Vector3d bc = p.subToVector(S.getEnd());
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and segment.
	 *
	 * @param p point
	 * @param S segment
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_Segment S) {
		return Math.sqrt(sqDistance(p, S));
	}

	/**
	 * Squared distance between point and segment.
	 *
	 * @param p point
	 * @param a segment start
	 * @param b segment end
	 * @return squared distance
	 */
	public static double sqDistanceToSegment(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		final WB_Vector3d ab = b.subToVector(a);
		final WB_Vector3d ac = p.subToVector(a);
		final WB_Vector3d bc = p.subToVector(b);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and segment.
	 *
	 * @param p point
	 * @param a segment start
	 * @param b segment end
	 * @return distance
	 */
	public static double distanceToSegment(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		return Math.sqrt(sqDistanceToSegment(p, a, b));
	}

	// POINT-LINE

	/**
	 * Squared distance between point and line.
	 *
	 * @param p point
	 * @param L line
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Line L) {
		final WB_Vector3d ab = L.getDirection();
		final WB_Vector3d ac = p.subToVector(L.getOrigin());
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and line.
	 *
	 * @param p point
	 * @param L line
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_Line L) {
		return Math.sqrt(sqDistance(p, L));
	}

	/**
	 * Squared distance between point and line.
	 *
	 * @param p point
	 * @param a point on line
	 * @param b point on line
	 * @return squared distance
	 */
	public static double sqDistanceToLine(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		final WB_Vector3d ab = b.subToVector(a);
		final WB_Vector3d ac = p.subToVector(a);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and line.
	 *
	 * @param p point
	 * @param a point on line
	 * @param b point on line
	 * @return distance
	 */
	public static double distanceToLine(final WB_Point3d p, final WB_Point3d a,
			final WB_Point3d b) {
		return Math.sqrt(sqDistanceToLine(p, a, b));
	}

	// POINT-RAY
	/**
	 * Squared distance between point and ray.
	 *
	 * @param p point
	 * @param R ray
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Ray R) {
		final WB_Vector3d ab = R.getDirection();
		final WB_Vector3d ac = p.subToVector(R.getOrigin());
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and ray.
	 *
	 * @param p point
	 * @param R ray
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_Ray R) {
		return Math.sqrt(sqDistance(p, R));
	}

	/**
	 * Squared distance between point and ray.
	 *
	 * @param p point
	 * @param a ray origin
	 * @param b point on ray
	 * @return squared distance
	 */
	public static double sqDistanceToRay(final WB_Point3d p,
			final WB_Point3d a, final WB_Point3d b) {
		final WB_Vector3d ab = b.subToVector(a);
		final WB_Vector3d ac = p.subToVector(a);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Distance between point and ray.
	 *
	 * @param p point
	 * @param a ray origin
	 * @param b point on ray
	 * @return distance
	 */
	public static double distanceToRay(final WB_Point3d p, final WB_Point3d a,
			final WB_Point3d b) {
		return Math.sqrt(sqDistanceToRay(p, a, b));
	}

	// POINT-AABB
	/**
	 * Squared distance between point and axis-aligned box.
	 *
	 * @param p point
	 * @param AABB AABB
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_AABB AABB) {
		double sqDist = 0;
		double v = p.x;
		if (v < AABB.min.x) {
			sqDist += (AABB.min.x - v) * (AABB.min.x - v);
		}
		if (v > AABB.max.x) {
			sqDist += (v - AABB.max.x) * (v - AABB.max.x);
		}
		v = p.y;
		if (v < AABB.min.y) {
			sqDist += (AABB.min.y - v) * (AABB.min.y - v);
		}
		if (v > AABB.max.y) {
			sqDist += (v - AABB.max.y) * (v - AABB.max.y);
		}
		v = p.z;
		if (v < AABB.min.z) {
			sqDist += (AABB.min.z - v) * (AABB.min.z - v);
		}
		if (v > AABB.max.z) {
			sqDist += (v - AABB.max.z) * (v - AABB.max.z);
		}
		return sqDist;
	}

	/**
	 * Distance between point and axis-aligned box.
	 *
	 * @param p point
	 * @param AABB AABB
	 * @return distance
	 */
	public static double distance(final WB_Point3d p, final WB_AABB AABB) {
		return Math.sqrt(sqDistance(p, AABB));
	}

	/**
	 * Squared distance between point and polygon.
	 *
	 * @param p point
	 * @param poly polygon
	 * @return squared distance
	 */
	public static double sqDistance(final WB_Point3d p, final WB_Polygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point3d tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = WB_Intersection.closestPoint(p, T);
			final double d2 = WB_Distance.distance(tmp, p);
			if (d2 < dmax2) {
				dmax2 = d2;
				if (WB_Epsilon.isZeroSq(dmax2)) {
					break;
				}
			}

		}

		return dmax2;
	}

	/**
	 * Distance between point and polygon.
	 *
	 * @param p point
	 * @param poly polygon
	 * @return squared distance
	 */
	public static double distance(final WB_Point3d p, final WB_Polygon poly) {
		return Math.sqrt(sqDistance(p, poly));
	}

	public static double sqDistance(final WB_Segment S, final WB_Segment T) {
		return WB_Intersection.getIntersection(S, T).sqDist;
	}

	public static double distance(final WB_Segment S, final WB_Segment T) {
		return Math.sqrt(WB_Intersection.getIntersection(S, T).sqDist);
	}

}
