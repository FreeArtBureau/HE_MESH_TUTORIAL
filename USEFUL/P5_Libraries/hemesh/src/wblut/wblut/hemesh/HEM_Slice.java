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
import java.util.Iterator;
import java.util.List;

import wblut.geom.Triangulation;
import wblut.geom.WB_AABB2D;
import wblut.geom.WB_ClassifyPolygonToPlane;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point2d;


import javolution.util.FastList;
import javolution.util.FastMap;

// TODO: Auto-generated Javadoc
/**
 * Planar cut of a mesh. Faces on positive side of cut plane are removed.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Slice extends HEM_Modifier {

	/** Cut plane. */
	private WB_Plane	P;

	/** Reverse planar cut. */
	private boolean		reverse		= false;

	/** Cap holes? */
	private boolean		capHoles	= true;

	private boolean		simpleCap	= true;

	/** Keep center of cut mesh? */
	private boolean		keepCenter	= false;

	/** Store cut faces */
	public HE_Selection	cut;

	/** Store cap faces */
	public HE_Selection	cap;

	private double		offset;

	/**
	 * Set offset.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEM_Slice setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_Slice.
	 */
	public HEM_Slice() {
		super();

	}

	/**
	 * Set cut plane.
	 *
	 * @param P cut plane
	 * @return self
	 */
	public HEM_Slice setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	public HEM_Slice setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set reverse option.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Slice setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Set option to cap holes.
	 *
	 * @param b true, false;
	 * @return self
	 */

	public HEM_Slice setCap(final Boolean b) {
		capHoles = b;
		return this;
	}

	public HEM_Slice setSimpleCap(final Boolean b) {
		simpleCap = b;
		return this;
	}

	/**
	 * Set option to reset mesh center.
	 *
	 * @param b true, false;
	 * @return self
	 */

	public HEM_Slice setKeepCenter(final Boolean b) {
		keepCenter = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		cut = new HE_Selection(mesh);
		cap = new HE_Selection(mesh);
		// no plane defined
		if (P == null) {
			return mesh;
		}

		// empty mesh
		if (mesh.numberOfVertices() == 0) {
			return mesh;
		}

		final WB_Plane lP = P.get();
		if (reverse) {
			lP.flipNormal();
		}
		lP.set(lP.getNormal(), lP.d() + offset);
		HEM_SliceSurface ss;
		ss = new HEM_SliceSurface().setPlane(lP);
		mesh.modify(ss);

		cut = ss.cut;
		final HE_Selection newFaces = new HE_Selection(mesh);
		HE_Face face;
		Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			final WB_ClassifyPolygonToPlane cptp = WB_Plane
					.classifyPolygonToPlane(face.toPolygon(), lP);
			if ((cptp == WB_ClassifyPolygonToPlane.POLYGON_IN_FRONT_OF_PLANE)
					|| (cptp == WB_ClassifyPolygonToPlane.POLYGON_ON_PLANE)) {
				newFaces.add(face);
			} else {
				if (cut.contains(face)) {
					cut.remove(face);
				}
			}

		}

		mesh.replaceFaces(newFaces.getFacesAsArray());
		cut.cleanSelection();
		mesh.cleanUnusedElementsByFace();
		final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getFaceOrder() < 3) {
				facesToRemove.add(face);
			}
		}
		mesh.removeFaces(facesToRemove);
		mesh.cleanUnusedElementsByFace();
		if (capHoles) {
			if (simpleCap) {
				cap.addFaces(mesh.capHoles());
				mesh.pairHalfedges();
				mesh.capHalfedges();
			} else {
				List<HE_Halfedge> unpairedHalfedges = mesh
						.getUnpairedHalfedges();
				if (unpairedHalfedges.size() > 0) {
					final FastList<HE_Vertex> verticesOnCutFaces = new FastList<HE_Vertex>();
					final FastList<WB_Point2d> mappedVertices = new FastList<WB_Point2d>();
					final FastMap<Integer, Integer> vertexKeyToMappedVertexIndex = new FastMap<Integer, Integer>();
					HE_Vertex v;
					for (final HE_Halfedge he : unpairedHalfedges) {
						v = he.getVertex();
						if (!verticesOnCutFaces.contains(v)) {
							verticesOnCutFaces.add(v);
							final WB_Point2d mappedV = lP.localPoint2D(v);
							mappedVertices.add(mappedV);
							vertexKeyToMappedVertexIndex.put(v.key(),
									mappedVertices.size() + 3);
						}
					}
					final WB_AABB2D AABB = new WB_AABB2D(mappedVertices);
					final WB_Point2d leftlower = AABB.getMin().multAndCopy(2)
							.sub(AABB.getCenter());
					final WB_Point2d rightupper = AABB.getMax().multAndCopy(2)
							.sub(AABB.getCenter());
					final WB_Point2d[] boundary = new WB_Point2d[4];

					boundary[0] = leftlower;
					boundary[1] = new WB_Point2d(leftlower.x, rightupper.y);
					boundary[2] = rightupper;
					boundary[3] = new WB_Point2d(rightupper.x, leftlower.y);

					final Triangulation triang = new Triangulation();

					triang.startWithBoundary(boundary);
					for (final WB_Point2d point : mappedVertices) {
						triang.addInteriorPoint(point);
					}
					for (final HE_Halfedge he : unpairedHalfedges) {
						final int c1 = vertexKeyToMappedVertexIndex.get(he
								.getVertex().key());
						final int c2 = vertexKeyToMappedVertexIndex.get(he
								.getEndVertex().key());
						triang.addConstraint(c1, c2);
					}
					final WB_Point2d[] dummy = new WB_Point2d[mappedVertices
							.size() + 4];
					for (int i = 0; i < mappedVertices.size() + 4; i++) {
						dummy[i] = new WB_Point2d(1, 1);

					}
					final List<WB_IndexedTriangle2D> tris = triang
							.getIndexedTrianglesAsList(dummy);
					for (final WB_IndexedTriangle2D tri : tris) {
						if ((tri.i1 > 3) && (tri.i2 > 3) && (tri.i3 > 3)) {// leave
																			// out
																			// triangles
																			// with
																			// boundary
																			// points

							final HE_Face newFace = new HE_Face();
							final HE_Halfedge he1 = new HE_Halfedge();
							final HE_Halfedge he2 = new HE_Halfedge();
							final HE_Halfedge he3 = new HE_Halfedge();
							he1.setVertex(verticesOnCutFaces.get(tri.i1 - 4));
							he2.setVertex(verticesOnCutFaces.get(tri.i3 - 4));
							he3.setVertex(verticesOnCutFaces.get(tri.i2 - 4));
							he1.setNext(he2);
							he1.setFace(newFace);
							he2.setNext(he3);
							he2.setFace(newFace);
							he3.setNext(he1);
							he3.setFace(newFace);
							mesh.add(he1);
							mesh.add(he2);
							mesh.add(he3);
							newFace.setHalfedge(he1);
							mesh.add(newFace);
							cap.add(newFace);

						}
					}
					mesh.pairHalfedges();
					int old = 0;
					unpairedHalfedges = mesh.getUnpairedHalfedges();
					while (unpairedHalfedges.size() != old) {
						old = unpairedHalfedges.size();
						for (final HE_Halfedge he : unpairedHalfedges) {
							mesh.remove(he.getFace());
						}
						mesh.cleanUnusedElementsByFace();
						unpairedHalfedges = mesh.getUnpairedHalfedges();
					}
				}
			}

		} else {
			mesh.pairHalfedges();
			mesh.capHalfedges();
		}

		// mesh.triangulateConcaveFaces();
		if (!keepCenter) {
			mesh.resetCenter();
		}
		return mesh;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return apply(selection.parent);
	}

}
