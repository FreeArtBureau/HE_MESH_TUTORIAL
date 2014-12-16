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
public class WB_IndexedPolygon implements WB_Polygon {

	/** Ordered array of WB_Point. */
	private WB_Point3d[]	allpoints;
	private int[]			indices;

	/** Number of points. */
	public int				n;

	/** Stored plane of polygon. */
	private WB_Plane		P;

	/** Status of stored plane. */
	private boolean			updated;

	/**
	 * Instantiates a new WB_Polygon.
	 */
	public WB_IndexedPolygon() {
		allpoints = new WB_Point3d[0];
		indices = new int[0];
		n = 0;
		updated = false;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public WB_IndexedPolygon(final WB_Point3d[] points, final int[] indices,
			final int n) {
		allpoints = points;
		this.indices = indices;
		this.n = n;
		P = getPlane();
		updated = true;
	}

	/**
	 * Set polygon.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public void set(final WB_Point3d[] points, final int[] indices, final int n) {
		allpoints = points;
		this.indices = indices;
		this.n = n;
		P = getPlane();
		updated = true;
	}

	/**
	 * Set polygon.
	 *
	 * @param poly source polygon, no copies are made
	 */
	public void set(final WB_Polygon poly) {
		allpoints = new WB_Point3d[n];
		indices = new int[n];
		for (int i = 0; i < n; i++) {
			allpoints[i] = poly.getPoint(i);
			indices[i] = poly.getIndex(i);
		}
		P = getPlane();
	}

	/**
	 * Get copy.
	 *
	 * @return copy
	 */
	public WB_IndexedPolygon get() {
		final int[] cindices = new int[n];
		for (int i = 0; i < n; i++) {

			cindices[i] = indices[i];
		}
		return new WB_IndexedPolygon(allpoints, cindices, n);

	}

	/**
	 * Closest point on polygon to given point.
	 *
	 * @param p point
	 * @return closest point of polygon
	 */
	public WB_Point3d closestPoint(final WB_Point3d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.sqDistance(p, allpoints[indices[i]]);
			if (cd < d) {
				id = indices[i];
				d = cd;
			}
		}
		return allpoints[id];
	}

	/**
	 * Index of closest point on polygon to given point.
	 *
	 * @param p point
	 * @return index of closest point of polygon
	 */
	public int closestIndex(final WB_Point3d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.sqDistance(p, allpoints[indices[i]]);
			if (cd < d) {
				id = indices[i];
				d = cd;
			}
		}
		return id;
	}

	/**
	 * Plane of polygon.
	 *
	 * @return plane
	 */
	public WB_Plane getPlane() {
		if (updated) {
			return P;
		}
		final WB_Normal3d normal = new WB_Normal3d();
		final WB_Point3d center = new WB_Point3d();
		WB_Point3d p0;
		WB_Point3d p1;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {

			p0 = allpoints[indices[j]];
			p1 = allpoints[indices[i]];
			normal.x += (p0.y - p1.y) * (p0.z + p1.z);
			normal.y += (p0.z - p1.z) * (p0.x + p1.x);
			normal.z += (p0.x - p1.x) * (p0.y + p1.y);
			center.add(p1);
		}
		normal.normalize();
		center.div(n);
		P = new WB_Plane(center, normal);
		updated = true;
		return P;

	}

	/**
	 * Checks if point at index is convex.
	 *
	 * @param i index
	 * @return WB.VertexType.FLAT,WB.VertexType.CONVEX,WB.VertexType.CONCAVE
	 */
	public WB_VertexType2D isConvex(final int i) {

		final WB_Vector3d vp = allpoints[(i == 0) ? indices[n - 1]
				: indices[i - 1]].subToVector(allpoints[indices[i]]);
		vp.normalize();
		final WB_Vector3d vn = allpoints[(i == n - 1) ? indices[0]
				: indices[i + 1]].subToVector(allpoints[indices[i]]);
		vn.normalize();

		final double cross = vp.cross(vn).mag2();

		if (WB_Epsilon.isZeroSq(cross)) {
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
	 * @return arrayList of WB_IndexedTriangle, points are not copied
	 */
	public List<WB_IndexedTriangle> triangulate() {
		final List<WB_IndexedTriangle> tris = new FastList<WB_IndexedTriangle>();
		final WB_Polygon2D tmp = toPolygon2D();
		final List<WB_IndexedTriangle2D> tris2d = tmp.indexedTriangulate();
		WB_IndexedTriangle2D tri2d;
		for (int i = 0; i < tris2d.size(); i++) {
			tri2d = tris2d.get(i);
			tris.add(new WB_IndexedTriangle(tri2d.i1, tri2d.i2, tri2d.i3,
					allpoints));

		}
		return tris;
	}

	/**
	 * Removes point.
	 *
	 * @param i index of point to remove
	 * @return new WB_Polygon with point removed
	 */
	public WB_IndexedPolygon removePoint(final int i) {
		final int[] newindices = new int[n - 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		for (int j = i; j < n - 1; j++) {
			newindices[j] = indices[j + 1];
		}
		return new WB_IndexedPolygon(allpoints, newindices, n - 1);

	}

	public void removePointSelf(final int i) {
		final int[] newindices = new int[n - 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		for (int j = i; j < n - 1; j++) {
			newindices[j] = indices[j + 1];
		}
		set(allpoints, newindices, n - 1);

	}

	/**
	 * Adds point.
	 *
	 * @param i index to put point
	 * @param p point
	 * @return new WB_Polygon with point added
	 */
	public WB_IndexedPolygon addPoint(final int i, final int id) {
		final int[] newindices = new int[n + 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		newindices[i] = id;
		for (int j = i + 1; j < n + 1; j++) {
			newindices[j] = indices[j - 1];
		}
		return new WB_IndexedPolygon(allpoints, newindices, n + 1);

	}

	public void addPointSelf(final int i, final int id) {
		final int[] newindices = new int[n + 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		newindices[i] = id;
		for (int j = i + 1; j < n + 1; j++) {
			newindices[j] = indices[j - 1];
		}
		set(allpoints, newindices, n + 1);

	}

	public List<WB_IndexedSegment> getSegments() {
		final List<WB_IndexedSegment> segments = new FastList<WB_IndexedSegment>(
				n);
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_IndexedSegment(i, j, allpoints));

		}
		return segments;
	}

	public static List<WB_IndexedPolygon> extractPolygons(
			final List<WB_IndexedSegment> segs, final WB_Point3d[] points) {
		final List<WB_IndexedPolygon> result = new FastList<WB_IndexedPolygon>();
		final List<WB_IndexedSegment> leftovers = new FastList<WB_IndexedSegment>();
		final List<WB_IndexedSegment> cleanedsegs = clean(segs, points);
		leftovers.addAll(cleanedsegs);
		while (leftovers.size() > 0) {
			final List<WB_IndexedSegment> currentPolygon = new FastList<WB_IndexedSegment>();
			final boolean loopFound = tryToFindLoop(leftovers, currentPolygon,
					points);
			if (loopFound) {
				final int[] indices = new int[currentPolygon.size()];
				for (int i = 0; i < currentPolygon.size(); i++) {
					indices[i] = currentPolygon.get(i).i1();
				}
				if (currentPolygon.size() > 2) {
					final WB_IndexedPolygon poly = new WB_IndexedPolygon(
							points, indices, currentPolygon.size());
					result.add(poly);
				}
			}
			leftovers.removeAll(currentPolygon);
		}
		return result;
	}

	public static List<WB_IndexedSegment> clean(
			final List<WB_IndexedSegment> segs, final WB_Point3d[] points) {
		final List<WB_IndexedSegment> cleanedsegs = new FastList<WB_IndexedSegment>();
		final WB_KDTree3Dold<Integer> tree = new WB_KDTree3Dold<Integer>();
		int i = 0;
		for (i = 0; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.sqDistance(segs.get(i)
					.getOrigin(), segs.get(i).getEnd()))) {
				tree.put(segs.get(i).getOrigin(), segs.get(i).i1());
				tree.put(segs.get(i).getEnd(), segs.get(i).i2());
				cleanedsegs.add(new WB_IndexedSegment(segs.get(i).i1(), segs
						.get(i).i2(), points));
				break;
			}

		}
		for (; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.sqDistance(segs.get(i)
					.getOrigin(), segs.get(i).getEnd()))) {
				WB_Point3d origin = segs.get(i).getOrigin();
				WB_Point3d end = segs.get(i).getEnd();
				int i1 = segs.get(i).i1();
				int i2 = segs.get(i).i2();
				WB_KDNeighbor<Integer>[] nn = tree.getNearestNeighbors(origin,
						1);

				if (WB_Epsilon.isZeroSq(nn[0].sqDistance())) {
					origin = nn[0].point();
					i1 = nn[0].value();
				} else {
					tree.put(segs.get(i).getOrigin(), segs.get(i).i1());
				}
				nn = tree.getNearestNeighbors(end, 1);
				if (WB_Epsilon.isZeroSq(nn[0].sqDistance())) {
					end = nn[0].point();
					i2 = nn[0].value();
				} else {
					tree.put(segs.get(i).getEnd(), segs.get(i).i2());
				}
				cleanedsegs.add(new WB_IndexedSegment(i1, i2, points));
			}

		}
		return cleanedsegs;
	}

	private static boolean tryToFindLoop(final List<WB_IndexedSegment> segs,
			final List<WB_IndexedSegment> loop, final WB_Point3d[] points) {
		final List<WB_IndexedSegment> localSegs = new FastList<WB_IndexedSegment>();
		localSegs.addAll(segs);
		WB_IndexedSegment start = localSegs.get(0);
		loop.add(localSegs.get(0));
		boolean found = false;
		do {
			found = false;
			for (int i = 0; i < localSegs.size(); i++) {
				if (localSegs.get(i).i1() == start.i2()) {
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

	public WB_Polygon2D toPolygon2D() {
		final WB_Point2d[] lpoints = new WB_Point2d[n];
		for (int i = 0; i < n; i++) {
			lpoints[i] = P.localPoint2D(getPoint(i));
		}
		return new WB_Polygon2D(lpoints, n);

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Polygon#getN()
	 */
	public int getN() {
		return n;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Polygon#getPoint(int)
	 */
	public WB_Point3d getPoint(final int i) {
		return allpoints[indices[i]];
	}

	public int getIndex(final int i) {
		return indices[i];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Polygon#getPoints()
	 */
	public WB_Point3d[] getPoints() {
		return allpoints;
	}

	public int[] getIndices() {
		return indices;
	}

}