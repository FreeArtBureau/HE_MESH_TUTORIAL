/**
 * 
 */
package wblut.hemesh;

import java.util.List;

import wblut.geom.WB_AABBNode;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Distance;
import wblut.geom.WB_ExplicitSegment;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;


import javolution.util.FastList;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HE_Intersection {
	public static HE_Face	lastface;

	public static WB_Point3d getIntersection(final HE_Face face,
			final WB_Line line) {
		final WB_Plane P = face.toPlane();
		WB_Point3d p = null;
		final WB_IntersectionResult lpi = WB_Intersection.getIntersection(line,
				P);
		if (lpi.intersection) {
			if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
				p = (WB_Point3d) lpi.object;
			}
		}
		return p;
	}

	public static WB_Point3d getIntersection(final HE_Face face,
			final WB_Ray ray) {
		final WB_Plane P = face.toPlane();
		WB_Point3d p = null;
		final WB_IntersectionResult lpi = WB_Intersection.getIntersection(ray,
				P);
		if (lpi.intersection) {
			if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
				p = (WB_Point3d) lpi.object;
			}
		}
		return p;
	}

	public static WB_Point3d getIntersection(final HE_Face face,
			final WB_Segment segment) {
		final WB_Plane P = face.toPlane();
		WB_Point3d p = null;
		final WB_IntersectionResult lpi = WB_Intersection.getIntersection(
				segment, P);
		if (lpi.intersection) {
			if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
				p = (WB_Point3d) lpi.object;
			}
		}
		return p;
	}

	/**
	 * Intersect edge with plane.
	 *
	 * @param e the e
	 * @param P plane
	 * @return u -1: no intersection, 0..1: position of intersection
	 */
	public static double getIntersection(final HE_Edge e, final WB_Plane P) {
		final WB_IntersectionResult i = WB_Intersection.getIntersection(
				e.getStartVertex(), e.getEndVertex(), P);

		if (i.intersection == false) {
			return -1.0;// intersection beyond endpoints
		}

		return i.t1;// intersection on edge
	}

	public static List<WB_Point3d> getIntersection(final WB_AABBTree tree,
			final WB_Ray ray) {
		WB_Plane P;
		WB_IntersectionResult lpi;

		final List<WB_Point3d> p = new FastList<WB_Point3d>();
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(ray,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(ray, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					p.add((WB_Point3d) lpi.object);
				}
			}
		}

		return p;
	}

	public static List<WB_Point3d> getIntersection(final WB_AABBTree tree,
			final WB_Segment segment) {
		WB_Plane P;
		WB_IntersectionResult lpi;

		final List<WB_Point3d> p = new FastList<WB_Point3d>();
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(
				segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(segment, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					p.add((WB_Point3d) lpi.object);
				}
			}
		}

		return p;
	}

	public static List<WB_Point3d> getIntersection(final WB_AABBTree tree,
			final WB_Line line) {
		WB_Plane P;
		WB_IntersectionResult lpi;

		final List<WB_Point3d> p = new FastList<WB_Point3d>();
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(line,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(line, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					p.add((WB_Point3d) lpi.object);
				}
			}
		}

		return p;
	}

	public static List<WB_ExplicitSegment> getIntersection(
			final WB_AABBTree tree, final WB_Plane P) {
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection
				.getIntersection(P, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		final List<WB_ExplicitSegment> cuts = new FastList<WB_ExplicitSegment>();
		for (final HE_Face face : candidates) {
			cuts.addAll(WB_Intersection.getIntersection(face.toPolygon(), P));
		}

		return cuts;
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final WB_AABBTree tree, final WB_Plane P) {
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection
				.getIntersection(P, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		return candidates;
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final WB_AABBTree tree, final WB_Ray R) {
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection
				.getIntersection(R, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		return candidates;
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final WB_AABBTree tree, final WB_Line L) {
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection
				.getIntersection(L, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		return candidates;
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final WB_AABBTree tree, final WB_Segment segment) {
		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(
				segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		return candidates;
	}

	public static WB_Point3d getClosestIntersection(final WB_AABBTree tree,
			final WB_Ray ray) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(ray,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(ray, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(ray.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 < d2min) {
						lastface = face;
						d2min = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static WB_Point3d getFurthestIntersection(final WB_AABBTree tree,
			final WB_Ray ray) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(ray,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(ray, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(ray.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 > d2max) {
						lastface = face;
						d2max = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static WB_Point3d getClosestIntersection(final WB_AABBTree tree,
			final WB_Line line) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(line,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(line, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(line.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 < d2min) {
						lastface = face;
						d2min = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static WB_Point3d getFurthestIntersection(final WB_AABBTree tree,
			final WB_Line line) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(line,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(line, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(line.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 > d2max) {
						lastface = face;
						d2max = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static WB_Point3d getClosestIntersection(final WB_AABBTree tree,
			final WB_Segment segment) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(
				segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(segment, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(segment.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 < d2min) {
						lastface = face;
						d2min = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static WB_Point3d getFurthestIntersection(final WB_AABBTree tree,
			final WB_Segment segment) {

		WB_Plane P;
		WB_IntersectionResult lpi;
		WB_Point3d p = null;

		final List<HE_Face> candidates = new FastList<HE_Face>();
		final List<WB_AABBNode> nodes = WB_Intersection.getIntersection(
				segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}

		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			P = face.toPlane();
			lpi = WB_Intersection.getIntersection(segment, P);
			if (lpi.intersection) {
				if (HE_Mesh.pointIsInFace((WB_Point3d) lpi.object, face)) {
					d2 = WB_Distance.sqDistance(segment.getOrigin(),
							(WB_Point3d) lpi.object);
					if (d2 > d2max) {
						lastface = face;
						d2max = d2;
						p = (WB_Point3d) lpi.object;
					}
				}
			}
		}

		return p;

	}

	public static List<WB_Point3d> getIntersection(final HE_Mesh mesh,
			final WB_Ray ray) {

		return getIntersection(new WB_AABBTree(mesh, 10), ray);
	}

	public static List<WB_Point3d> getIntersection(final HE_Mesh mesh,
			final WB_Segment segment) {
		return getIntersection(new WB_AABBTree(mesh, 10), segment);
	}

	public static List<WB_Point3d> getIntersection(final HE_Mesh mesh,
			final WB_Line line) {
		return getIntersection(new WB_AABBTree(mesh, 10), line);
	}

	public static List<WB_ExplicitSegment> getIntersection(final HE_Mesh mesh,
			final WB_Plane P) {
		return getIntersection(new WB_AABBTree(mesh, 10), P);
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final HE_Mesh mesh, final WB_Plane P) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), P);
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final HE_Mesh mesh, final WB_Ray R) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), R);
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final HE_Mesh mesh, final WB_Line L) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), L);
	}

	public static List<HE_Face> getPotentialIntersectedFaces(
			final HE_Mesh mesh, final WB_Segment segment) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), segment);
	}

	public static WB_Point3d getClosestIntersection(final HE_Mesh mesh,
			final WB_Ray ray) {

		return getClosestIntersection(new WB_AABBTree(mesh, 10), ray);

	}

	public static WB_Point3d getFurthestIntersection(final HE_Mesh mesh,
			final WB_Ray ray) {
		return getFurthestIntersection(new WB_AABBTree(mesh, 10), ray);

	}

	public static WB_Point3d getClosestIntersection(final HE_Mesh mesh,
			final WB_Line line) {
		return getClosestIntersection(new WB_AABBTree(mesh, 10), line);

	}

	public static WB_Point3d getFurthestIntersection(final HE_Mesh mesh,
			final WB_Line line) {
		return getFurthestIntersection(new WB_AABBTree(mesh, 10), line);

	}

	public static WB_Point3d getClosestIntersection(final HE_Mesh mesh,
			final WB_Segment segment) {

		return getClosestIntersection(new WB_AABBTree(mesh, 10), segment);

	}

	public static WB_Point3d getFurthestIntersection(final HE_Mesh mesh,
			final WB_Segment segment) {

		return getFurthestIntersection(new WB_AABBTree(mesh, 10), segment);
	}
}
