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

import wblut.geom.WB_Point3d;
import wblut.math.WB_MTRandom;



/**
 * Planar subdivision of a mesh. Divides all edges in half. Non-triangular faces
 * are divided in new faces connecting each vertex with the two adjacent mid
 * edge vertices and the face center. Triangular faces are divided in four new
 * triangular faces by connecting the mid edge points. Faces are tris or quads.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HES_Planar extends HES_Subdividor {

	/** Random subdivision. */
	private boolean				random;

	/** Triangular division of triangles? */
	private boolean				keepTriangles;

	/** Random range. */
	private double				range;

	private final WB_MTRandom	randomGen;

	/**
	 * Instantiates a new HES_Planar.
	 */
	public HES_Planar() {
		super();
		random = false;
		range = 1;
		keepTriangles = true;
		randomGen = new WB_MTRandom();
	}

	/**
	 * Set random mode.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HES_Planar setRandom(final boolean b) {
		random = b;
		return this;
	}

	/**
	 * Set random seed.
	 *
	 * @param seed seed
	 * @return self
	 */
	public HES_Planar setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/**
	 * Set preservation of triangular faces.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HES_Planar setKeepTriangles(final boolean b) {
		keepTriangles = b;
		return this;
	}

	/**
	 * Set range of random variation.
	 *
	 * @param r range (0..1)
	 * @return self
	 */
	public HES_Planar setRange(final double r) {
		range = r;
		if (range > 1) {
			range = 1;
		}
		if (range < 0) {
			range = 0;
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final HashMap<Integer, HE_Vertex> faceVertices = new HashMap<Integer, HE_Vertex>();
		HE_Face face;
		Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (!random) {
				final HE_Vertex fv = new HE_Vertex(face.getFaceCenter());
				faceVertices.put(face.key(), fv);
			} else {
				HE_Halfedge he = face.getHalfedge();
				HE_Vertex fv = new HE_Vertex();
				int trial = 0;
				do {
					double c = 0;
					fv = new HE_Vertex();
					do {
						final WB_Point3d tmp = new WB_Point3d(he.getVertex());
						final double t = 0.5 + (randomGen.nextDouble() - 0.5)
								* range;
						tmp.mult(t);
						fv.add(tmp);
						c += t;
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					fv.div(c);
					trial++;
				} while ((!HE_Mesh.pointIsStrictlyInFace(fv, face))
						&& (trial < 10));
				if (trial == 10) {
					fv.set(face.getFaceCenter());
				}
				faceVertices.put(face.key(), fv);
			}

		}

		final int n = mesh.numberOfEdges();
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addVertices(mesh.getVerticesAsList());
		final HE_Edge[] origE = mesh.getEdgesAsArray();
		for (int i = 0; i < n; i++) {
			if (random) {
				final double f = 0.5 + (randomGen.nextDouble() - 0.5) * range;
				mesh.splitEdge(origE[i], f);
			} else {
				mesh.splitEdge(origE[i]);
			}
		}
		final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();

		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			// loop

			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}

			if ((face.getFaceOrder() == 6) && keepTriangles) {
				HE_Halfedge origHE1 = startHE;
				final HE_Face centerFace = new HE_Face();
				newFaces.add(centerFace);
				centerFace.setLabel(face.getLabel());
				final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
				do {
					final HE_Face newFace = new HE_Face();
					newFaces.add(newFace);
					newFace.setLabel(face.getLabel());
					newFace.setHalfedge(origHE1);
					final HE_Halfedge origHE2 = origHE1.getNextInFace();
					final HE_Halfedge origHE3 = origHE2.getNextInFace();
					final HE_Halfedge newHE = new HE_Halfedge();
					final HE_Halfedge newHEp = new HE_Halfedge();
					mesh.add(newHE);
					mesh.add(newHEp);
					faceHalfedges.add(newHEp);
					origHE2.setNext(newHE);
					newHE.setNext(origHE1);
					newHE.setVertex(origHE3.getVertex());
					newHE.setFace(newFace);
					origHE1.setFace(newFace);
					origHE2.setFace(newFace);
					newHEp.setVertex(origHE1.getVertex());
					newHE.setPair(newHEp);
					final HE_Edge e = new HE_Edge();
					mesh.add(e);
					e.setHalfedge(newHE);
					newHE.setEdge(e);
					newHEp.setEdge(e);
					newHEp.setFace(centerFace);
					centerFace.setHalfedge(newHEp);
					origHE1 = origHE3;
				} while (origHE1 != startHE);
				HE_Mesh.cycleHalfedges(faceHalfedges);
			} else {
				HE_Halfedge origHE1 = startHE;
				do {
					final HE_Face newFace = new HE_Face();
					newFaces.add(newFace);
					newFace.setLabel(face.getLabel());
					newFace.setHalfedge(origHE1);
					final HE_Halfedge origHE2 = origHE1.getNextInFace();
					final HE_Halfedge origHE3 = origHE2.getNextInFace();
					final HE_Halfedge newHE1 = new HE_Halfedge();
					final HE_Halfedge newHE2 = new HE_Halfedge();
					mesh.add(newHE1);
					mesh.add(newHE2);
					origHE2.setNext(newHE1);
					newHE1.setNext(newHE2);
					newHE2.setNext(origHE1);
					newHE1.setVertex(origHE3.getVertex());
					final HE_Vertex fv = faceVertices.get(origHE1.getFace()
							.key());
					newHE2.setVertex(fv);
					if (fv.getHalfedge() == null) {
						fv.setHalfedge(newHE2);
					}
					if (!mesh.contains(fv)) {
						mesh.add(fv);
					}
					newHE1.setFace(newFace);
					newHE2.setFace(newFace);
					origHE1.setFace(newFace);
					origHE2.setFace(newFace);
					origHE1 = origHE3;
				} while (origHE1 != startHE);
			}
			face.setLabel(0);

		}// end of face loop
		mesh.pairHalfedges();
		mesh.replaceFaces(newFaces);
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
		selection.cleanSelection();
		if (selection.numberOfFaces() == 0) {
			return selection.parent;
		}

		final HashMap<Integer, HE_Vertex> faceVertices = new HashMap<Integer, HE_Vertex>();
		HE_Face face;
		Iterator<HE_Face> fItr = selection.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (!random) {
				final HE_Vertex fv = new HE_Vertex(face.getFaceCenter());
				faceVertices.put(face.key(), fv);
			} else {
				HE_Halfedge he = face.getHalfedge();
				HE_Vertex fv = new HE_Vertex();
				int trial = 0;
				do {
					double c = 0;
					fv = new HE_Vertex();
					do {
						final WB_Point3d tmp = new WB_Point3d(he.getVertex());
						final double t = 0.5 + (randomGen.nextDouble() - 0.5)
								* range;
						tmp.mult(t);
						fv.add(tmp);
						c += t;
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					fv.div(c);
					trial++;
				} while ((!HE_Mesh.pointIsStrictlyInFace(fv, face))
						&& (trial < 10));
				if (trial == 10) {
					fv.set(face.getFaceCenter());
				}
				faceVertices.put(face.key(), fv);
			}
		}

		selection.collectEdges();

		final HE_Selection newVertices = new HE_Selection(selection.parent);
		final HE_Edge[] edges = selection.getEdgesAsArray();
		final int ne = selection.numberOfEdges();
		for (int i = 0; i < ne; i++) {
			HE_Vertex v;
			if (random) {
				final double f = 0.5 + (randomGen.nextDouble() - 0.5) * range;
				v = selection.parent.splitEdge(edges[i], f).vItr().next();
			} else {
				v = selection.parent.splitEdge(edges[i]).vItr().next();
			}

			if (v != null) {
				newVertices.add(v);
			}
		}

		final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();
		fItr = selection.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			HE_Halfedge startHE = face.getHalfedge();
			while (!newVertices.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}

			if ((face.getFaceOrder() == 6) && keepTriangles) {
				HE_Halfedge origHE1 = startHE;
				final HE_Face centerFace = new HE_Face();
				centerFace.setLabel(face.getLabel());
				newFaces.add(centerFace);
				final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
				do {
					final HE_Face newFace = new HE_Face();
					newFaces.add(newFace);
					newFace.setLabel(face.getLabel());
					newFace.setHalfedge(origHE1);
					final HE_Halfedge origHE2 = origHE1.getNextInFace();
					final HE_Halfedge origHE3 = origHE2.getNextInFace();
					final HE_Halfedge newHE = new HE_Halfedge();
					final HE_Halfedge newHEp = new HE_Halfedge();
					selection.parent.add(newHE);
					selection.parent.add(newHEp);
					faceHalfedges.add(newHEp);
					origHE2.setNext(newHE);
					newHE.setNext(origHE1);
					newHE.setVertex(origHE3.getVertex());
					newHE.setFace(newFace);
					origHE1.setFace(newFace);
					origHE2.setFace(newFace);
					newHEp.setVertex(origHE1.getVertex());
					newHE.setPair(newHEp);
					final HE_Edge e = new HE_Edge();
					selection.parent.add(e);
					e.setHalfedge(newHE);
					newHE.setEdge(e);
					newHEp.setEdge(e);
					newHEp.setFace(centerFace);
					centerFace.setHalfedge(newHEp);
					origHE1 = origHE3;
				} while (origHE1 != startHE);
				HE_Mesh.cycleHalfedges(faceHalfedges);
			} else {
				HE_Halfedge origHE1 = startHE;
				do {
					final HE_Face newFace = new HE_Face();
					newFaces.add(newFace);
					newFace.setLabel(face.getLabel());
					newFace.setHalfedge(origHE1);
					final HE_Halfedge origHE2 = origHE1.getNextInFace();
					final HE_Halfedge origHE3 = origHE2.getNextInFace();
					final HE_Halfedge newHE1 = new HE_Halfedge();
					final HE_Halfedge newHE2 = new HE_Halfedge();
					selection.parent.add(newHE1);
					selection.parent.add(newHE2);
					origHE2.setNext(newHE1);
					newHE1.setNext(newHE2);
					newHE2.setNext(origHE1);
					newHE1.setVertex(origHE3.getVertex());
					final HE_Vertex fv = faceVertices.get(origHE1.getFace()
							.key());
					newHE2.setVertex(fv);
					if (fv.getHalfedge() == null) {
						fv.setHalfedge(newHE2);
					}
					if (!selection.parent.contains(fv)) {
						selection.parent.add(fv);
					}
					newHE1.setFace(newFace);
					newHE2.setFace(newFace);
					origHE1.setFace(newFace);
					origHE2.setFace(newFace);
					origHE1 = origHE3;
				} while (origHE1 != startHE);
			}

		}// end of face loop
		selection.parent.pairHalfedges();

		selection.parent.removeFaces(selection.getFacesAsArray());

		selection.parent.addFaces(newFaces);
		return selection.parent;
	}

}
