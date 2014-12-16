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

import java.util.List;

import wblut.WB_Epsilon;


import javolution.util.FastList;

// TODO: Auto-generated Javadoc
/**
 * Planar polygon class.
 */
public class WB_Polygon2D {

	/** Ordered array of WB_Point. */
	public WB_Point2d[]	points;

	/** Number of points. */
	public int			n;

	/**
	 * Instantiates a new WB_Polygon.
	 */
	public WB_Polygon2D() {
		points = new WB_Point2d[0];
		n = 0;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public WB_Polygon2D(final WB_Point2d[] points, final int n) {
		this.points = points;
		this.n = n;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 *
	 * @param points array of WB_Point
	 * @param n number of points
	 * @param copy copy points?
	 */
	public WB_Polygon2D(final WB_Point2d[] points, final int n,
			final boolean copy) {
		if (copy == false) {
			this.points = points;
		} else {
			this.points = new WB_Point2d[n];
			for (int i = 0; i < n; i++) {
				this.points[i] = points[i].get();
			}

		}
		this.n = n;
	}

	/**
	 * Instantiates a new WB_Polygon2D.
	 *
	 * @param points arrayList of WB_XY
	 */
	public WB_Polygon2D(final List<WB_Point2d> points) {
		n = points.size();
		this.points = new WB_Point2d[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
	}

	/**
	 * Set polygon.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public void set(final WB_Point2d[] points, final int n) {
		this.points = points;
		this.n = n;
	}

	/**
	 * Set polygon.
	 *
	 * @param poly source polygon, no copies are made
	 */
	public void set(final WB_Polygon2D poly) {
		points = poly.points;
		n = poly.n;
	}

	/**
	 * Set polygon.
	 *
	 * @param points arrayList of WB_Point, no copies are made
	 * @param n number of points
	 */
	public void set(final List<WB_Point2d> points, final int n) {
		this.points = new WB_Point2d[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
		this.n = n;
	}

	/**
	 * Get deep copy.
	 *
	 * @return copy
	 */
	public WB_Polygon2D get() {
		final WB_Point2d[] newPoints = new WB_Point2d[n];
		for (int i = 0; i < n; i++) {
			newPoints[i] = points[i].get();
		}
		return new WB_Polygon2D(newPoints, n);

	}

	/**
	 * Get shallow copy.
	 *
	 * @return copy
	 */
	public WB_Polygon2D getNoCopy() {
		return new WB_Polygon2D(points, n);

	}

	/**
	 * Closest point on polygon to given point.
	 *
	 * @param p point
	 * @return closest point of polygon
	 */
	public WB_Point2d closestPoint(final WB_Point2d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance2D.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return points[id];
	}

	/**
	 * Index of closest point on polygon to given point.
	 *
	 * @param p point
	 * @return index of closest point of polygon
	 */
	public int closestIndex(final WB_Point2d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance2D.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return id;
	}

	/**
	 * Checks if point at index is convex.
	 *
	 * @param i index
	 * @return WB.VertexType.FLAT,WB.VertexType.CONVEX,WB.VertexType.CONCAVE
	 */
	public WB_VertexType2D isConvex(final int i) {
		final WB_Point2d vp = points[(i == 0) ? n - 1 : i - 1]
				.subAndCopy(points[i]);
		vp.normalize();
		final WB_Point2d vn = points[(i == n - 1) ? 0 : i + 1]
				.subAndCopy(points[i]);
		vn.normalize();

		final double cross = vp.x * vn.y - vp.y * vn.x;

		if (WB_Epsilon.isZero(cross)) {
			return WB_VertexType2D.FLAT;
		} else if (Math.acos(vp.dot(vn)) < Math.PI) {
			return WB_VertexType2D.CONVEX;
		} else {
			return WB_VertexType2D.CONCAVE;
		}
	}

	/**
	 * Triangulate polygon.
	 *
	 * @return arrayList of WB_Triangle, points are not copied
	 */
	public List<WB_ExplicitTriangle2D> triangulate() {
		final Triangulation tri = new Triangulation();
		tri.startWithBoundary(points);
		return tri.getExplicitTrianglesAsList();
	}

	/**
	 * Triangulate polygon.
	 *
	 * @return arrayList of WB_IndexedTriangle, points are not copied
	 */
	public List<WB_IndexedTriangle2D> indexedTriangulate() {
		final Triangulation tri = new Triangulation();
		tri.startWithBoundary(points, true);
		return tri.getIndexedTrianglesAsList(points, true);
	}

	/**
	 * Removes point.
	 *
	 * @param i index of point to remove
	 * @return new WB_Polygon with point removed
	 */
	public WB_Polygon2D removePoint(final int i) {
		final WB_Point2d[] newPoints = new WB_Point2d[n - 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		for (int j = i; j < n - 1; j++) {
			newPoints[j] = points[j + 1];
		}
		return new WB_Polygon2D(newPoints, n - 1);

	}

	/**
	 * Remove flat points.
	 *
	 * @return new WB_Polygon with superfluous points removed
	 */
	public WB_Polygon2D removeFlatPoints() {
		return removeFlatPoints(0);
	}

	private WB_Polygon2D removeFlatPoints(final int start) {
		for (int i = start; i < n; i++) {
			if (isConvex(i) == WB_VertexType2D.FLAT) {
				return removePoint(i).removeFlatPoints(i);
			}
		}
		return this;
	}

	/**
	 * Adds point.
	 *
	 * @param i index to put point
	 * @param p point
	 * @return new WB_Polygon with point added
	 */
	public WB_Polygon2D addPoint(final int i, final WB_Point2d p) {
		final WB_Point2d[] newPoints = new WB_Point2d[n + 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		newPoints[i] = p;
		for (int j = i + 1; j < n + 1; j++) {
			newPoints[j] = points[j - 1];
		}
		return new WB_Polygon2D(newPoints, n + 1);

	}

	/**
	 * Refine polygon and smooth with simple Laplacian filter.
	 *
	 * @return new refined WB_Polygon
	 */
	public WB_Polygon2D smooth() {
		final WB_Point2d[] newPoints = new WB_Point2d[2 * n];

		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			newPoints[2 * i] = points[j].addAndCopy(points[i]);
			newPoints[2 * i].mult(0.5);
			newPoints[2 * i + 1] = points[i].get();
		}
		final WB_Point2d[] sPoints = new WB_Point2d[2 * n];
		for (int i = 0, j = 2 * n - 1; i < 2 * n; j = i, i++) {
			int k = i + 1;
			if (k == 2 * n) {
				k = 0;
			}
			sPoints[i] = newPoints[j].addAndCopy(newPoints[k]);
			sPoints[i].mult(0.5);
		}

		return new WB_Polygon2D(sPoints, 2 * n);

	}

	public static void trimConvexPolygon(final WB_Polygon2D poly, final double d) {
		final WB_Polygon2D cpoly = poly.get();
		final int n = cpoly.n; // get number of vertices
		// iterate over n-1 edges
		final WB_Polygon2D frontPoly = new WB_Polygon2D();// needed by
		// splitPolygon
		// to store one half
		final WB_Polygon2D backPoly = new WB_Polygon2D();// needed by
		// splitPolygon
		// to store other half
		WB_Point2d p1, p2, origin;
		WB_Point2d v, normal;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			p1 = cpoly.points[i];// startpoint of edge
			p2 = cpoly.points[j];// endpoint of edge
			// vector along edge
			v = p2.subAndCopy(p1);
			v.normalize();
			// edge normal is perpendicular to edge and plane normal
			normal = new WB_Point2d(v.y, -v.x);
			// center of edge
			origin = p1.addAndCopy(p2).mult(0.5);
			// offset cutting plane origin by the desired distance d
			origin.add(d * normal.x, d * normal.y);

			splitPolygonInto(poly, new WB_Line2D(origin, v), frontPoly,
					backPoly);
			poly.set(frontPoly);

		}
	}

	public void trimConvexPolygon(final double d) {
		trimConvexPolygon(this, d);
	}

	public static void trimConvexPolygon(final WB_Polygon2D poly,
			final double[] d) {

		// iterate over n-1 edges
		final WB_Polygon2D frontPoly = new WB_Polygon2D();// needed by
		// splitPolygon
		// to store one half
		final WB_Polygon2D backPoly = new WB_Polygon2D();// needed by
		// splitPolygon
		// to store other half
		WB_Point2d p1, p2, origin;
		WB_Point2d v, normal;
		for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
			p1 = poly.points[i];// startpoint of edge
			p2 = poly.points[j];// endpoint of edge
			// vector along edge
			v = p2.subAndCopy(p1);
			v.normalize();
			// edge normal is perpendicular to edge and plane normal
			normal = new WB_Point2d(v.y, -v.x);
			// center of edge
			origin = p1.addAndCopy(p2).mult(0.5);
			// offset cutting plane origin by the desired distance d
			origin.add(d[i] * normal.x, d[i] * normal.y);

			splitPolygonInto(poly, new WB_Line2D(origin, v), frontPoly,
					backPoly);
			poly.set(frontPoly);

		}
	}

	public void trimConvexPolygon(final double[] d) {
		trimConvexPolygon(this, d);
	}

	/**
	 * Split polygon into.
	 *
	 * @param poly the poly
	 * @param L split line
	 * @param frontPoly front subpoly
	 * @param backPoly back subpoly
	 */
	public static void splitPolygonInto(final WB_Polygon2D poly,
			final WB_Line2D L, final WB_Polygon2D frontPoly,
			final WB_Polygon2D backPoly) {
		int numFront = 0;
		int numBack = 0;

		final FastList<WB_Point2d> frontVerts = new FastList<WB_Point2d>(20);
		final FastList<WB_Point2d> backVerts = new FastList<WB_Point2d>(20);

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
						i = WB_Intersection2D.closestPoint2D(L,
								new WB_ExplicitSegment2D(a, b));
						frontVerts.add((WB_Point2d) i.object);
						numFront++;
						backVerts.add((WB_Point2d) i.object);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
					if (aSide == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
						i = WB_Intersection2D.closestPoint2D(L,
								new WB_ExplicitSegment2D(a, b));

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */

						frontVerts.add((WB_Point2d) i.object);
						numFront++;
						backVerts.add((WB_Point2d) i.object);
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
			frontPoly.set(frontVerts, numFront);
			backPoly.set(backVerts, numBack);
		}

	}

	public void splitPolygonInto(final WB_Line2D L,
			final WB_Polygon2D frontPoly, final WB_Polygon2D backPoly) {
		splitPolygonInto(get(), L, frontPoly, backPoly);

	}

	public List<WB_IndexedSegment2D> toSegments() {
		final List<WB_IndexedSegment2D> segments = new FastList<WB_IndexedSegment2D>(
				n);
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_IndexedSegment2D(j, i, points));

		}
		return segments;
	}

	public List<WB_ExplicitSegment2D> toExplicitSegments() {
		final List<WB_ExplicitSegment2D> segments = new FastList<WB_ExplicitSegment2D>(
				n);
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_ExplicitSegment2D(points[j], points[i]));

		}
		return segments;
	}

	public WB_Polygon2D negate() {
		final WB_Point2d[] negPoints = new WB_Point2d[n];
		for (int i = 0; i < n; i++) {
			negPoints[i] = points[n - 1 - i];
		}
		return new WB_Polygon2D(negPoints, n);

	}

	public static List<WB_Polygon2D> negate(final List<WB_Polygon2D> polys) {
		final List<WB_Polygon2D> neg = new FastList<WB_Polygon2D>();
		for (int i = 0; i < polys.size(); i++) {
			neg.add(polys.get(i).negate());
		}
		return neg;

	}

	public static List<WB_ExplicitSegment2D> intersectionSeg(
			final WB_Polygon2D P, final WB_Polygon2D Q) {
		final FastList<WB_ExplicitSegment2D> pos = new FastList<WB_ExplicitSegment2D>();
		final FastList<WB_ExplicitSegment2D> neg = new FastList<WB_ExplicitSegment2D>();
		final FastList<WB_ExplicitSegment2D> coSame = new FastList<WB_ExplicitSegment2D>();
		final FastList<WB_ExplicitSegment2D> coDiff = new FastList<WB_ExplicitSegment2D>();
		final FastList<WB_ExplicitSegment2D> intersect = new FastList<WB_ExplicitSegment2D>();
		final WB_BSPTree2D tree = new WB_BSPTree2D();
		tree.build(P);
		for (int i = 0, j = Q.n - 1; i < Q.n; j = i, i++) {
			pos.clear();
			neg.clear();
			coSame.clear();
			coDiff.clear();
			final WB_ExplicitSegment2D S = new WB_ExplicitSegment2D(
					Q.points[j], Q.points[i]);
			tree.partitionSegment(S, pos, neg, coSame, coDiff);
			intersect.addAll(pos);
			intersect.addAll(coSame);

		}
		tree.build(Q);
		for (int i = 0, j = P.n - 1; i < P.n; j = i, i++) {
			pos.clear();
			neg.clear();
			coSame.clear();
			coDiff.clear();
			final WB_ExplicitSegment2D S = new WB_ExplicitSegment2D(
					P.points[j], P.points[i]);
			tree.partitionSegment(S, pos, neg, coSame, coDiff);
			intersect.addAll(pos);
			intersect.addAll(coSame);

		}

		return intersect;

	}

	public static List<WB_Polygon2D> intersection(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		return extractPolygons(intersectionSeg(P, Q));
	}

	public static List<WB_ExplicitSegment2D> unionSeg(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		final WB_Polygon2D nP = P.negate();
		final WB_Polygon2D nQ = Q.negate();
		return WB_ExplicitSegment2D.negate(intersectionSeg(nP, nQ));
	}

	public static List<WB_Polygon2D> union(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		return extractPolygons(unionSeg(P, Q));
	}

	public static List<WB_ExplicitSegment2D> subtractSeg(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		final WB_Polygon2D nQ = Q.negate();
		return intersectionSeg(P, nQ);
	}

	public static List<WB_Polygon2D> subtract(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		return extractPolygons(subtractSeg(P, Q));
	}

	public static List<WB_Polygon2D> exclusiveOr(final WB_Polygon2D P,
			final WB_Polygon2D Q) {
		final List<WB_Polygon2D> tmp = subtract(P, Q);
		tmp.addAll(subtract(Q, P));
		return tmp;
	}

	public static List<WB_Polygon2D> extractPolygons(
			final List<WB_ExplicitSegment2D> segs) {
		final List<WB_Polygon2D> result = new FastList<WB_Polygon2D>();
		final List<WB_ExplicitSegment2D> leftovers = new FastList<WB_ExplicitSegment2D>();
		final List<WB_ExplicitSegment2D> cleanedsegs = clean(segs);
		leftovers.addAll(cleanedsegs);
		while (leftovers.size() > 0) {
			final FastList<WB_ExplicitSegment2D> currentPolygon = new FastList<WB_ExplicitSegment2D>();
			final boolean loopFound = tryToFindLoop(leftovers, currentPolygon);
			if (loopFound) {
				final FastList<WB_Point2d> points = new FastList<WB_Point2d>();
				for (int i = 0; i < currentPolygon.size(); i++) {
					points.add(currentPolygon.get(i).getOrigin());

				}
				if (points.size() > 2) {
					WB_Polygon2D poly = new WB_Polygon2D(points);
					poly = poly.removeFlatPoints();
					result.add(poly);
				}
			}
			leftovers.removeAll(currentPolygon);
		}
		return result;
	}

	private static List<WB_ExplicitSegment2D> clean(
			final List<WB_ExplicitSegment2D> segs) {
		final List<WB_ExplicitSegment2D> cleanedsegs = new FastList<WB_ExplicitSegment2D>();
		final WB_KDTree2Dold<Integer> tree = new WB_KDTree2Dold<Integer>();
		int i = 0;
		for (i = 0; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance2D.sqDistance(segs.get(i)
					.getOrigin(), segs.get(i).getEnd()))) {
				tree.put(segs.get(i).getOrigin(), 2 * i);
				tree.put(segs.get(i).getEnd(), 2 * i + 1);
				cleanedsegs.add(new WB_ExplicitSegment2D(segs.get(i)
						.getOrigin(), segs.get(i).getEnd()));
				break;
			}

		}
		for (; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance2D.sqDistance(segs.get(i)
					.getOrigin(), segs.get(i).getEnd()))) {
				WB_Point2d origin = segs.get(i).getOrigin();
				WB_Point2d end = segs.get(i).getEnd();

				WB_KDNeighbor2D<Integer>[] nn = tree.getNearestNeighbors(
						origin, 1);

				if (WB_Epsilon.isZeroSq(nn[0].sqDistance())) {
					origin = nn[0].point();
				} else {
					tree.put(segs.get(i).getOrigin(), 2 * i);
				}
				nn = tree.getNearestNeighbors(end, 1);
				if (WB_Epsilon.isZeroSq(nn[0].sqDistance())) {
					end = nn[0].point();
				} else {
					tree.put(segs.get(i).getEnd(), 2 * i + 1);
				}
				cleanedsegs.add(new WB_ExplicitSegment2D(origin, end));
			}

		}
		return cleanedsegs;
	}

	private static boolean tryToFindLoop(final List<WB_ExplicitSegment2D> segs,
			final List<WB_ExplicitSegment2D> loop) {
		final List<WB_ExplicitSegment2D> localSegs = new FastList<WB_ExplicitSegment2D>();
		localSegs.addAll(segs);
		WB_Segment2D start = localSegs.get(0);
		loop.add(localSegs.get(0));
		boolean found = false;
		do {
			found = false;
			for (int i = 0; i < localSegs.size(); i++) {
				if (WB_Epsilon.isZeroSq(WB_Distance2D.sqDistance(
						localSegs.get(i).getOrigin(), start.getEnd()))) {
					// if (localSegs.get(i).origin() == start.end()) {
					start = localSegs.get(i);
					loop.add(localSegs.get(i));
					found = true;
					break;
				}
			}
			if (found) {
				localSegs.remove(start);
			}

		} while ((start != segs.get(0)) && found);
		if ((loop.size() > 0) && (start == segs.get(0))) {
			return true;
		}
		return false;
	}

	public WB_ExplicitPolygon toPolygon() {
		final WB_Point3d[] points3D = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			points3D[i] = new WB_Point3d(points[i].x, points[i].y, 0);
		}
		return new WB_ExplicitPolygon(points3D, n);

	}

}