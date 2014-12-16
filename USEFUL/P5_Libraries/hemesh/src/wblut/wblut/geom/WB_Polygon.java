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

public interface WB_Polygon {

	public void set(final WB_Polygon poly);

	/**
	 * Closest point on polygon to given point.
	 *
	 * @param p point
	 * @return closest point of polygon
	 */
	public WB_Point3d closestPoint(final WB_Point3d p);

	/**
	 * Index of closest point on polygon to given point.
	 *
	 * @param p point
	 * @return index of closest point of polygon
	 */
	public int closestIndex(final WB_Point3d p);

	/**
	 * Plane of polygon.
	 *
	 * @return plane
	 */
	public WB_Plane getPlane();

	/**
	 * Checks if point at index is convex.
	 *
	 * @param i index
	 * @return WB.VertexType.FLAT,WB.VertexType.CONVEX,WB.VertexType.CONCAVE
	 */
	public WB_VertexType2D isConvex(final int i);

	/**
	 * Triangulate polygon.
	 *
	 * @return arrayList of WB_IndexedTriangle, points are not copied
	 */
	public List<WB_IndexedTriangle> triangulate();

	public List<WB_IndexedSegment> getSegments();

	public WB_Polygon2D toPolygon2D();

	public int getN();

	public int getIndex(int i);

	public WB_Point3d getPoint(int i);

	public WB_Point3d[] getPoints();

}