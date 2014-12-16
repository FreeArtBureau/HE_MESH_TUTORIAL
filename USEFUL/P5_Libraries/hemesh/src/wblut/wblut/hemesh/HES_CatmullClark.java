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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wblut.geom.WB_Intersection;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;



/**
 * Catmull-Clark subdivision of a mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */

public class HES_CatmullClark extends HES_Subdividor {

	/** Keep edges? */
	private boolean					keepEdges;

	/** Keep boundary? */
	private boolean					keepBoundary	= true;

	private WB_Parameter<Double>	blendFactor;

	public HES_CatmullClark() {
		super();
		blendFactor = new WB_ConstantParameter<Double>(1.0);

	}

	/**
	 * Keep edges of selection fixed when subdividing selection?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HES_CatmullClark setKeepEdges(final boolean b) {
		keepEdges = b;
		return this;

	}

	/**
	 * Keep boundary edges fixed?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HES_CatmullClark setKeepBoundary(final boolean b) {
		keepBoundary = b;
		return this;

	}

	public HES_CatmullClark setBlendFactor(final double f) {
		blendFactor = new WB_ConstantParameter<Double>(f);
		return this;
	}

	public HES_CatmullClark setBlendFactor(final WB_Parameter<Double> f) {
		blendFactor = f;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.resetVertexLabels();
		final HashMap<Integer, WB_Point3d> avgFC = new HashMap<Integer, WB_Point3d>();
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Halfedge he;
		WB_Point3d p;
		while (vItr.hasNext()) {
			v = vItr.next();
			he = v.getHalfedge();
			final WB_Point3d afc = new WB_Point3d();
			int c = 0;
			do {
				if (he.getFace() != null) {
					afc.add(he.getFace().getFaceCenter());
					c++;
				}
				he = he.getNextInVertex();
			} while (he != v.getHalfedge());
			afc.div(c);
			avgFC.put(v.key(), afc);
		}

		mesh.quadSplitFaces();
		final HashMap<Integer, WB_Point3d> newPositions = new HashMap<Integer, WB_Point3d>();
		final HE_Selection all = mesh.selectAllFaces();
		final List<HE_Vertex> boundary = all.getOuterVertices();
		final List<HE_Vertex> inner = all.getInnerVertices();

		HE_Vertex n;
		List<HE_Vertex> neighbors;
		vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() == -1) {
				p = avgFC.get(v.key());
				neighbors = v.getNeighborVertices();
				final int order = neighbors.size();
				final double io = 1.0 / order;
				for (int i = 0; i < order; i++) {
					n = neighbors.get(i);
					p.add(2.0 * io * n.x, 2.0 * io * n.y, 2.0 * io * n.z);
				}
				p.add(v.multAndCopy(order - 3));
				p.div(order);
				newPositions.put(
						v.key(),
						WB_Point3d.interpolate(v, p,
								blendFactor.value(v.x, v.y, v.z)));
			} else {
				p = new WB_Point3d();
				neighbors = v.getNeighborVertices();
				final int order = neighbors.size();
				boolean edgePoint = false;
				for (int i = 0; i < order; i++) {
					n = neighbors.get(i);
					p.add(n);
					if (n.getLabel() == -1) {
						edgePoint = true;
					}
				}
				p.div(order);
				if (edgePoint) {
					newPositions.put(
							v.key(),
							WB_Point3d.interpolate(v, p,
									blendFactor.value(v.x, v.y, v.z)));
				} else {
					newPositions.put(v.key(), v);
				}

			}

		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (keepBoundary) {
				newPositions.put(v.key(), v);
			} else {
				p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				double c = 1;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if (boundary.contains(n)) {
						p.add(n);
						nc++;
						c++;
					}
				}
				newPositions.put(
						v.key(),
						(nc > 1) ? WB_Point3d.interpolate(v, p.scale(1.0 / c),
								blendFactor.value(v.x, v.y, v.z)) : v);
			}
		}

		vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(newPositions.get(v.key()));
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(newPositions.get(v.key()));
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.subdividors.HEB_Subdividor#subdivideSelected(wblut.hemesh
	 * .HE_Mesh, wblut.hemesh.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		selection.parent.resetVertexLabels();
		final HashMap<Integer, WB_Point3d> avgFC = new HashMap<Integer, WB_Point3d>();
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = selection.parent.vItr();
		HE_Halfedge he;
		WB_Point3d p;
		while (vItr.hasNext()) {
			v = vItr.next();
			he = v.getHalfedge();
			final WB_Point3d afc = new WB_Point3d();
			int c = 0;
			do {
				if (he.getFace() != null) {
					if (selection.contains(he.getFace())) {
						afc.add(he.getFace().getFaceCenter());
						c++;
					}
				}
				he = he.getNextInVertex();
			} while (he != v.getHalfedge());
			afc.div(c);
			avgFC.put(v.key(), afc);
		}
		selection.parent.quadSplitFaces(selection);
		final HashMap<Integer, WB_Point3d> newPositions = new HashMap<Integer, WB_Point3d>();
		selection.collectVertices();
		final List<HE_Vertex> boundary = selection.getBoundaryVertices();
		final List<HE_Vertex> outer = selection.getOuterVertices();
		final List<HE_Vertex> inner = selection.getInnerVertices();
		List<HE_Face> sharedFaces;

		vItr = outer.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (boundary.contains(v)) {
				vItr.remove();
			}
		}

		HE_Vertex n;
		List<HE_Vertex> neighbors;
		int id = 0;
		vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() == -1) {
				p = avgFC.get(v.key());
				neighbors = v.getNeighborVertices();
				final int order = neighbors.size();
				final double io = 1.0 / order;
				for (int i = 0; i < order; i++) {
					n = neighbors.get(i);
					p.add(2.0 * io * n.x, 2.0 * io * n.y, 2.0 * io * n.z);
				}
				p.add(v.multAndCopy(order - 3));
				p.div(order);
				newPositions.put(
						v.key(),
						WB_Point3d.interpolate(v, p,
								blendFactor.value(v.x, v.y, v.z)));
			} else {
				p = new WB_Point3d();
				neighbors = v.getNeighborVertices();
				final int order = neighbors.size();
				boolean edgePoint = false;
				for (int i = 0; i < order; i++) {
					n = neighbors.get(i);
					p.add(n);
					if (n.getLabel() == -1) {
						edgePoint = true;
					}
				}
				p.div(order);
				if (edgePoint) {
					newPositions.put(
							v.key(),
							WB_Point3d.interpolate(v, p,
									blendFactor.value(v.x, v.y, v.z)));
				} else {
					newPositions.put(v.key(), v);
				}
			}
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (keepBoundary) {
				newPositions.put(v.key(), v);
			} else {
				p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				double c = 1;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if ((boundary.contains(n)) && (selection.contains(n))) {
						p.add(n);
						nc++;
						c++;
					}
				}
				newPositions.put(
						v.key(),
						(nc > 1) ? WB_Point3d.interpolate(v, p.scale(1.0 / c),
								blendFactor.value(v.x, v.y, v.z)) : v);
			}
			id++;
		}
		List<WB_Plane> planes;
		List<HE_Face> faceStar;
		HE_Face f;
		WB_Plane P;
		vItr = outer.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			planes = new ArrayList<WB_Plane>();
			if (keepEdges) {
				newPositions.put(v.key(), v);
			} else {

				faceStar = v.getFaceStar();
				for (int i = 0; i < faceStar.size(); i++) {
					f = faceStar.get(i);
					if (!selection.contains(f)) {
						P = f.toPlane();
						boolean unique = true;
						for (int j = 0; j < planes.size(); j++) {
							if (WB_Plane.isEqual(planes.get(j), P)) {
								unique = false;
								break;
							}
						}
						if (unique) {
							planes.add(P);
						}
					}
				}

				p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				double c = 1;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if (outer.contains(n)) {
						sharedFaces = selection.parent.getSharedFaces(v, n);
						boolean singleFaceGap = true;
						for (int j = 0; j < sharedFaces.size(); j++) {
							if (selection.contains(sharedFaces.get(j))) {
								singleFaceGap = false;
								break;
							}
						}

						if (!singleFaceGap) {
							p.add(n);
							c++;
							nc++;
						}
					}
				}
				if (nc > 1) {
					p.scale(1.0 / c);
					if (planes.size() == 1) {
						p = WB_Intersection.closestPoint(p, planes.get(0));
					}/*
					 * else if (planes.size() == 2) { final WB_Line L =
					 * WB_Intersect.intersect(planes.get(0), planes.get(1)).L; p
					 * = WB_ClosestPoint.closestPoint(p, L); p.set(v); }
					 */else {
						p.set(v);
					}

				} else {

					p.set(v);
				}

				newPositions.put(
						v.key(),
						WB_Point3d.interpolate(v, p,
								blendFactor.value(v.x, v.y, v.z)));
			}
			id++;
		}

		vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(newPositions.get(v.key()));
		}

		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(newPositions.get(v.key()));
		}

		vItr = outer.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(newPositions.get(v.key()));
		}

		return selection.parent;
	}
}
