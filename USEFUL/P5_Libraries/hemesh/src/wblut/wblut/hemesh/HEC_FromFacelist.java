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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance;
import wblut.geom.WB_KDNeighbor;
import wblut.geom.WB_KDTree3Dold;
import wblut.geom.WB_Point3d;



/**
 * Creates a new mesh from a list of vertices and faces.
 * Vertices can be duplicate.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_FromFacelist extends HEC_Creator {

	/** Vertices. */
	private WB_Point3d[]	vertices;

	/** Face indices. */
	private int[][]			faces;

	/** Duplicate vertices? */
	private boolean			duplicate;

	/**
	 * Instantiates a new HEC_Facelist.
	 *
	 */
	public HEC_FromFacelist() {
		super();
		override = true;
		duplicate = true;
	}

	/**
	 * Set vertex coordinates from an array of WB_point. No copies are made.
	 * 
	 * @param vs vertices
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final WB_Point3d[] vs) {
		vertices = vs;
		return this;
	}

	/**
	 * Set vertex coordinates from an arraylist of WB_point. 
	 *  
	 * @param vs vertices
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final Collection<WB_Point3d> vs) {

		final int n = vs.size();
		final Iterator<WB_Point3d> itr = vs.iterator();
		vertices = new WB_Point3d[n];
		int i = 0;
		while (itr.hasNext()) {
			vertices[i] = itr.next();
			i++;
		}
		return this;
	}

	/**
	 * Set vertex coordinates from an array of WB_point. 
	 *  
	 * @param vs vertices
	 * @param copy copy points?
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final WB_Point3d[] vs,
			final boolean copy) {
		if (copy) {
			final int n = vs.length;

			vertices = new WB_Point3d[n];
			for (int i = 0; i < n; i++) {
				vertices[i] = new WB_Point3d(vs[i]);

			}
		} else {
			vertices = vs;
		}
		return this;
	}

	/**
	 * Set vertex coordinates from a 2D array of double: 1st index=point, 2nd index (0..2) coordinates
	 * 
	 * @param vs Nx3 2D array of coordinates
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final double[][] vs) {
		final int n = vs.length;
		vertices = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			vertices[i] = new WB_Point3d(vs[i][0], vs[i][1], vs[i][2]);

		}
		return this;
	}

	/**
	 * Set vertex coordinates from array of double: x0, y0 ,z0 ,x1 ,y1 ,z1 ,...
	 * 
	 * @param vs array of coordinates
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final double[] vs) {
		final int n = vs.length;
		vertices = new WB_Point3d[n / 3];
		for (int i = 0; i < n; i += 3) {
			vertices[i] = new WB_Point3d(vs[i], vs[i + 1], vs[i + 2]);

		}
		return this;
	}

	/**
	 * Set vertex coordinates from a 2D array of float: 1st index=point, 2nd index (0..2) coordinates
	 * 
	 * @param vs Nx3 2D array of coordinates
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final float[][] vs) {
		final int n = vs.length;
		vertices = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			vertices[i] = new WB_Point3d(vs[i][0], vs[i][1], vs[i][2]);

		}
		return this;
	}

	/**
	 * Set vertex coordinates from array of float: x0, y0 ,z0 ,x1 ,y1 ,z1 ,...
	 * 
	 * @param vs array of coordinates
	 * @return self
	 */
	public HEC_FromFacelist setVertices(final float[] vs) {
		final int n = vs.length;
		vertices = new WB_Point3d[n / 3];
		for (int i = 0; i < n; i += 3) {
			vertices[i] = new WB_Point3d(vs[i], vs[i + 1], vs[i + 2]);

		}
		return this;
	}

	/**
	 * Set faces from 2D array of int: 1st index=face, 2nd=index of vertex.
	 *
	 * @param fs 2D array of vertex indices
	 * @return self
	 */
	public HEC_FromFacelist setFaces(final int[][] fs) {
		faces = fs;
		return this;
	}

	/**
	 * Set faces from 2D array of int: 1st index=face, 2nd=index of vertex.
	 *
	 * @param fs 2D array of vertex indices
	 * @return self
	 */
	public HEC_FromFacelist setFaces(final List<int[]> fs) {
		faces = new int[fs.size()][];
		int i = 0;
		for (final int[] indices : fs) {
			faces[i] = indices;
			i++;
		}
		return this;
	}

	/**
	 * Duplicate vertices in input?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HEC_FromFacelist setDuplicate(final boolean b) {
		duplicate = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		final HE_Mesh mesh = new HE_Mesh();

		if ((faces != null) && (vertices != null)) {
			final HE_Vertex[] uniqueVertices = new HE_Vertex[vertices.length];
			if (duplicate) {
				final WB_KDTree3Dold<Integer> kdtree = new WB_KDTree3Dold<Integer>();
				WB_KDNeighbor<Integer>[] neighbors;
				HE_Vertex v = new HE_Vertex(vertices[0]);
				kdtree.put(v, 0);
				uniqueVertices[0] = v;
				mesh.add(v);
				for (int i = 1; i < vertices.length; i++) {
					v = new HE_Vertex(vertices[i]);
					neighbors = kdtree.getNearestNeighbors(v, 1, false);
					if (neighbors[0].sqDistance() < WB_Epsilon.SQEPSILON) {
						uniqueVertices[i] = uniqueVertices[neighbors[0].value()];
						/*
						 * System.out.println("HEC_FromFaceList : vertex " + i +
						 * " already exists as vertex " + neighbors[0].value() +
						 * ".");
						 */
					} else {
						/*
						 * System.out.println("HEC_FromFaceList : vertex " + i +
						 * " is unique.");
						 */
						kdtree.put(v, i);
						uniqueVertices[i] = v;
						mesh.add(uniqueVertices[i]);
					}

				}
			} else {
				HE_Vertex v;
				for (int i = 0; i < vertices.length; i++) {
					v = new HE_Vertex(vertices[i]);
					v.setLabel(i);
					uniqueVertices[i] = v;
					mesh.add(uniqueVertices[i]);
				}

			}
			HE_Halfedge he;
			int id = 0;
			int order = 0;
			for (final int[] face : faces) {
				final ArrayList<HE_Halfedge> faceEdges = new ArrayList<HE_Halfedge>();
				final HE_Face hef = new HE_Face();
				hef.setLabel(id);

				id++;
				order = 0;
				final int fl = face.length;
				if (fl > 2) {
					for (int i = 0; i < fl; i++) {
						if (WB_Distance.sqDistance(uniqueVertices[face[i]],
								uniqueVertices[face[(i + 1) % fl]]) > WB_Epsilon.SQEPSILON) {
							he = new HE_Halfedge();
							faceEdges.add(he);
							he.setFace(hef);
							if (hef.getHalfedge() == null) {
								hef.setHalfedge(he);
							}
							he.setVertex(uniqueVertices[face[i]]);
							// if (he.vertex().halfedge() == null) {
							he.getVertex().setHalfedge(he);

							// }
							order++;
						}
					}
				}
				if (order > 0) {
					mesh.add(hef);
					HE_Mesh.cycleHalfedges(faceEdges);
					mesh.addHalfedges(faceEdges);
				}
			}
			mesh.pairHalfedges();
			mesh.capHalfedges();
			//mesh.resolvePinchPoints();

		}
		return mesh;
	}
}
