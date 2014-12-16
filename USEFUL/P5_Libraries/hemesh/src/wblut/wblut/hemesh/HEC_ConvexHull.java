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
package wblut.hemesh;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import javolution.util.FastMap;
import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;
import wblut.geom.WB_ClassifyPointToPlane;
import wblut.geom.WB_Point3d;

/**
 * Creates the convex hull of a collection of points.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_ConvexHull extends HEC_Creator {

	/** Points. */
	private WB_Point3d[]				points;

	/** Number of points. */
	private int							numberOfPoints;

	private boolean						useQuickHull;

	public FastMap<Integer, Integer>	vertexToPointIndex;

	/**
	 * Instantiates a new HEC_ConvexHull.
	 *
	 */
	public HEC_ConvexHull() {
		super();
		override = true;
		useQuickHull = true;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points array of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final WB_Point3d[] points) {

		this.points = points;

		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points array of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final HE_Vertex[] points) {
		this.points = new WB_Point3d[points.length];
		for (int i = 0; i < points.length; i++) {
			this.points[i] = new WB_Point3d(points[i]);
		}
		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points any Collection of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final Collection<WB_Point3d> points) {

		this.points = new WB_Point3d[points.size()];
		final Iterator<WB_Point3d> itr = points.iterator();
		int i = 0;
		while (itr.hasNext()) {
			this.points[i] = itr.next();
			i++;
		}
		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points any Collection of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPointsFromVertices(
			final Collection<HE_Vertex> points) {

		this.points = new WB_Point3d[points.size()];
		final Iterator<HE_Vertex> itr = points.iterator();
		int i = 0;
		while (itr.hasNext()) {
			this.points[i] = itr.next();
			i++;
		}
		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points 2D array of double of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points 2D array of float of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set points that define vertices.
	 *
	 * @param points 2D array of float of vertex positions
	 * @return self
	 */
	public HEC_ConvexHull setPoints(final int[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set number of points.
	 *
	 * @param N number of points
	 * @return self
	 */
	public HEC_ConvexHull setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Use QHull3D?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HEC_ConvexHull setUseQuickHull(final boolean b) {
		useQuickHull = b;
		;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		if (useQuickHull) {
			return createWithQuickHull();
		}
		if (points == null) {
			return new HE_Mesh();
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		final WB_Point3d cog = new WB_Point3d();
		for (int i = 0; i < numberOfPoints; i++) {
			cog.add(points[i]);
		}
		cog.div(numberOfPoints);

		HE_Mesh mesh = new HE_Mesh(new HEC_Cube().setEdge(.01).setCenter(cog));
		vertexToPointIndex = new FastMap<Integer, Integer>();
		for (int i = 0; i < numberOfPoints; i++) {

			mesh = addPointToHull(mesh, i);
		}
		return mesh;
	}

	public HE_Mesh createWithQuickHull() {
		if (points == null) {
			return new HE_Mesh();
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		final Point3d[] locpoints = new Point3d[numberOfPoints];
		for (int i = 0; i < numberOfPoints; i++) {
			locpoints[i] = new Point3d(points[i].x, points[i].y, points[i].z);
		}
		final QuickHull3D hull = new QuickHull3D();
		hull.build(locpoints);
		// /hull.triangulate();
		final int[][] faceIndices = hull.getFaces(QuickHull3D.POINT_RELATIVE);
		final HEC_FromFacelist ffl = new HEC_FromFacelist().setVertices(points)
				.setFaces(faceIndices).setDuplicate(false);
		final HE_Mesh result = ffl.createBase();
		vertexToPointIndex = new FastMap<Integer, Integer>();
		final Iterator<HE_Vertex> vItr = result.vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexToPointIndex.put(vItr.next().key(), i++);
		}

		result.cleanUnusedElementsByFace();
		return result;

	}

	private HE_Selection getAllFacesVisibleToPoint(final HE_Mesh mesh,
			final WB_Point3d p) {
		final HE_Selection visibleFaces = new HE_Selection(mesh);
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.toPlane().classifyPointToPlane(p) == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
				visibleFaces.add(f);
			}
		}
		return visibleFaces;

	}

	private HE_Mesh addPointToHull(final HE_Mesh mesh, final int i) {
		final WB_Point3d p = points[i];

		final HE_Selection visibleFaces = getAllFacesVisibleToPoint(mesh, p);
		if (visibleFaces.numberOfFaces() > 0) {
			final Iterator<HE_Face> fItr = visibleFaces.fItr();
			while (fItr.hasNext()) {
				mesh.deleteFace(fItr.next());

			}
			mesh.cleanUnusedElementsByFace();
			final List<HE_Halfedge> halfedges = mesh.getUnpairedHalfedges();
			final HE_Vertex v = new HE_Vertex(p);
			vertexToPointIndex.put(v.key(), i);
			mesh.add(v);
			final int n = halfedges.size();

			for (int j = 0; j < n; j++) {
				final HE_Halfedge he = halfedges.get(j);
				final HE_Halfedge hen = new HE_Halfedge();
				mesh.add(hen);
				final HE_Halfedge hei = new HE_Halfedge();
				final HE_Halfedge heo = new HE_Halfedge();
				mesh.add(hei);
				mesh.add(heo);
				hen.setPair(he);
				final HE_Edge e = new HE_Edge();
				mesh.add(e);
				he.setEdge(e);
				hen.setEdge(e);
				e.setHalfedge(he);
				hen.setVertex(he.getNextInFace().getVertex());
				hei.setVertex(he.getVertex());
				heo.setVertex(v);
				v.setHalfedge(heo);
				final HE_Face nf = new HE_Face();
				mesh.add(nf);
				nf.setHalfedge(hei);
				hen.setFace(nf);
				hei.setFace(nf);
				heo.setFace(nf);
				hen.setNext(hei);
				hei.setNext(heo);
				heo.setNext(hen);
			}
			mesh.pairHalfedges();

		}

		final Collection<Integer> c = vertexToPointIndex.keySet();
		final Iterator<Integer> itr = c.iterator();
		while (itr.hasNext()) {
			final int key = itr.next();
			if (mesh.getVertexByKey(key) == null) {
				itr.remove();
			}
		}
		return mesh;
	}
}
