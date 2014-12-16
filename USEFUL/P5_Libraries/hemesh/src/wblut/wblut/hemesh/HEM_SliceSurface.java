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
import java.util.Map;

import wblut.geom.WB_AABBTree;
import wblut.geom.WB_ClassifyPointToPlane;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Plane;



// TODO: Auto-generated Javadoc
/**
 * Planar cut of a mesh. No faces are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_SliceSurface extends HEM_Modifier {

	/** Cut plane. */
	private WB_Plane	P;

	/** Stores cut faces. */
	public HE_Selection	cut;

	/** Stores new edges. */
	public HE_Selection	newEdges;

	/**
	 * Instantiates a new HEM_SliceSurface.
	 */
	public HEM_SliceSurface() {
		super();
	}

	/**
	 * Set cut plane.
	 *
	 * @param P cut plane
	 * @return self
	 */
	public HEM_SliceSurface setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	public HEM_SliceSurface setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	private double	offset;

	/**
	 * Set offset.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEM_SliceSurface setOffset(final double d) {
		offset = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		cut = new HE_Selection(mesh);
		newEdges = new HE_Selection(mesh);
		// no plane defined
		if (P == null) {
			return mesh;
		}

		// empty mesh
		if (mesh.numberOfVertices() == 0) {
			return mesh;
		}

		// check if plane intersects mesh
		final WB_Plane lP = new WB_Plane(P.getOrigin(), P.getNormal());
		lP.set(P.getNormal(), P.d() + offset);
		if (!WB_Intersection.checkIntersection(mesh.getAABB(), lP)) {
			return mesh;
		}
		final WB_AABBTree tree = new WB_AABBTree(mesh, 4);
		final HE_Selection faces = new HE_Selection(mesh);
		faces.addFaces(HE_Intersection.getPotentialIntersectedFaces(tree, lP));
		faces.collectVertices();
		faces.collectEdges();
		WB_ClassifyPointToPlane tmp;
		final HashMap<Integer, WB_ClassifyPointToPlane> vertexClass = new HashMap<Integer, WB_ClassifyPointToPlane>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = faces.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = lP.classifyPointToPlane(v);
			vertexClass.put(v.key(), tmp);
		}

		List<HE_Vertex> faceVertices = new ArrayList<HE_Vertex>();
		final HE_Selection split = new HE_Selection(mesh);

		final HashMap<Integer, Double> edgeInt = new HashMap<Integer, Double>();
		final Iterator<HE_Edge> eItr = faces.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (vertexClass.get(e.getStartVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {

				} else {
					edgeInt.put(e.key(), 0.0);
				}
			} else if (vertexClass.get(e.getStartVertex().key()) == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			} else {
				if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			}
		}

		for (final Map.Entry<Integer, Double> en : edgeInt.entrySet()) {
			final HE_Edge ce = mesh.getEdgeByKey(en.getKey());
			final double u = en.getValue();
			if (ce.getFirstFace() != null) {
				if (!split.contains(ce.getFirstFace())) {
					split.add(ce.getFirstFace());
				}
			}
			if (ce.getSecondFace() != null) {
				if (!split.contains(ce.getSecondFace())) {
					split.add(ce.getSecondFace());
				}
			}
			if (u == 0.0) {
				if (!split.contains(ce.getStartVertex())) {
					split.add(ce.getStartVertex());
				}
			} else if (u == 1.0) {
				if (!split.contains(ce.getEndVertex())) {
					split.add(ce.getEndVertex());
				}
			} else {
				split.add(mesh.splitEdge(ce, u).vItr().next());

			}
		}
		HE_Face f;
		final Iterator<HE_Face> fItr = split.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceVertices = f.getFaceVertices();
			int firstVertex = -1;
			int secondVertex = -1;
			final int n = faceVertices.size();
			for (int j = 0; j < n; j++) {
				v = faceVertices.get(j);
				if (split.contains(v)) {
					if (firstVertex == -1) {
						firstVertex = j;
						j++;// if one cut point is found, skip next point.
						// There should be at least one other vertex in
						// between for a proper cut.
					} else {
						secondVertex = j;
						break;
					}
				}
			}
			if ((firstVertex != -1) && (secondVertex != -1)) {
				cut.add(f);
				final HE_Selection out = mesh.splitFace(f,
						faceVertices.get(firstVertex),
						faceVertices.get(secondVertex));
				final HE_Face nf = out.fItr().next();
				cut.add(nf);
				final HE_Edge ne = out.eItr().next();
				ne.setLabel(1);
				newEdges.add(ne);
			}
		}

		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		cut = new HE_Selection(selection.parent);
		newEdges = new HE_Selection(selection.parent);
		// no plane defined
		if (P == null) {
			return selection.parent;
		}

		// empty mesh
		if (selection.parent.numberOfVertices() == 0) {
			return selection.parent;
		}
		final WB_Plane lP = new WB_Plane(P.getOrigin(), P.getNormal());
		lP.set(lP.getNormal(), lP.d() + offset);
		final WB_AABBTree tree = new WB_AABBTree(selection.parent, 4);
		final HE_Selection faces = new HE_Selection(selection.parent);
		faces.addFaces(HE_Intersection.getPotentialIntersectedFaces(tree, lP));

		final HE_Selection lsel = selection.get();
		lsel.intersect(faces);

		lsel.collectEdges();
		lsel.collectVertices();
		// empty mesh
		if (lsel.numberOfVertices() == 0) {
			return lsel.parent;
		}

		// check if plane intersects mesh
		boolean positiveVertexExists = false;
		boolean negativeVertexExists = false;
		WB_ClassifyPointToPlane tmp;
		final HashMap<Integer, WB_ClassifyPointToPlane> vertexClass = new HashMap<Integer, WB_ClassifyPointToPlane>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = lsel.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = lP.classifyPointToPlane(v);
			vertexClass.put(v.key(), tmp);
			if (tmp == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
				positiveVertexExists = true;

			}
			if (tmp == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
				negativeVertexExists = true;
			}
		}

		if (positiveVertexExists && negativeVertexExists) {
			List<HE_Vertex> faceVertices = new ArrayList<HE_Vertex>();
			final HE_Selection split = new HE_Selection(lsel.parent);

			final HashMap<Integer, Double> edgeInt = new HashMap<Integer, Double>();
			final Iterator<HE_Edge> eItr = lsel.eItr();
			HE_Edge e;
			while (eItr.hasNext()) {
				e = eItr.next();
				if (vertexClass.get(e.getStartVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {

					} else {
						edgeInt.put(e.key(), 0.0);
					}
				} else if (vertexClass.get(e.getStartVertex().key()) == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
						edgeInt.put(e.key(), 1.0);
					} else if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_IN_FRONT_OF_PLANE) {
						edgeInt.put(e.key(),
								HE_Intersection.getIntersection(e, lP));
					}
				} else {
					if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_ON_PLANE) {
						edgeInt.put(e.key(), 1.0);
					} else if (vertexClass.get(e.getEndVertex().key()) == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
						edgeInt.put(e.key(),
								HE_Intersection.getIntersection(e, lP));
					}
				}
			}

			for (final Map.Entry<Integer, Double> en : edgeInt.entrySet()) {
				final HE_Edge ce = lsel.parent.getEdgeByKey(en.getKey());
				final double u = en.getValue();
				if ((!split.contains(ce.getFirstFace()))
						&& (lsel.contains(ce.getFirstFace()))) {
					split.add(ce.getFirstFace());
				}
				if ((!split.contains(ce.getSecondFace()))
						&& (lsel.contains(ce.getSecondFace()))) {
					split.add(ce.getSecondFace());
				}
				if (u == 0.0) {
					if (!split.contains(ce.getStartVertex())) {
						split.add(ce.getStartVertex());
					}
				} else if (u == 1.0) {
					if (!split.contains(ce.getEndVertex())) {
						split.add(ce.getEndVertex());
					}
				} else {
					split.add(lsel.parent.splitEdge(ce, u).vItr().next());

				}
			}
			HE_Face f;
			final Iterator<HE_Face> fItr = split.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				faceVertices = f.getFaceVertices();
				int firstVertex = -1;
				int secondVertex = -1;
				final int n = faceVertices.size();
				for (int j = 0; j < n; j++) {
					v = faceVertices.get(j);
					if (split.contains(v)) {
						if (firstVertex == -1) {
							firstVertex = j;
							j++;// if one cut point is found, skip next point.
							// There should be at least one other vertex in
							// between for a proper cut.
						} else {
							secondVertex = j;
							break;
						}
					}
				}
				if ((firstVertex != -1) && (secondVertex != -1)) {
					cut.add(f);
					final HE_Selection out = lsel.parent.splitFace(f,
							faceVertices.get(firstVertex),
							faceVertices.get(secondVertex));

					final HE_Face nf = out.fItr().next();
					cut.add(nf);
					final HE_Edge ne = out.eItr().next();
					newEdges.add(ne);
				}

			}
		}

		return lsel.parent;
	}
}
