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

import java.util.Iterator;
import java.util.List;

import wblut.geom.WB_Point3d;


import javolution.util.FastList;

/**
 * Collection of mesh elements. Contains methods to manipulate selections
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HE_Selection extends HE_MeshStructure {

	public HE_Mesh	parent;

	/**
	 * Instantiates a new HE_Selection.
	 */
	public HE_Selection(final HE_Mesh parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Get outer edges.
	 *
	 * @return outer edges as FastList<HE_Edge>
	 */
	public List<HE_Edge> getOuterEdges() {
		final HE_Selection sel = get();
		sel.collectEdges();
		final List<HE_Edge> result = new FastList<HE_Edge>();
		final Iterator<HE_Edge> eItr = sel.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			final HE_Face f1 = e.getFirstFace();
			final HE_Face f2 = e.getSecondFace();
			if ((f1 == null) || (f2 == null) || (!contains(f1))
					|| (!contains(f2))) {
				result.add(e);
			}

		}
		return result;
	}

	/**
	 * Get inner edges.
	 *
	 * @return inner edges as FastList<HE_Edge>
	 */
	public List<HE_Edge> getInnerEdges() {
		final HE_Selection sel = get();
		sel.collectEdges();
		final List<HE_Edge> result = new FastList<HE_Edge>();
		final Iterator<HE_Edge> eItr = sel.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			final HE_Face f1 = e.getFirstFace();
			final HE_Face f2 = e.getSecondFace();
			if ((f1 != null) && (f2 != null) && (contains(f1))
					&& (contains(f2))) {
				result.add(e);
			}

		}
		return result;
	}

	/**
	 * Get outer vertices.
	 *
	 * @return outer vertices as FastList<HE_Vertex>
	 */
	public List<HE_Vertex> getOuterVertices() {

		final List<HE_Vertex> result = new FastList<HE_Vertex>();
		final List<HE_Edge> outerEdges = getOuterEdges();
		for (int i = 0; i < outerEdges.size(); i++) {
			final HE_Edge e = outerEdges.get(i);
			final HE_Vertex v1 = e.getStartVertex();
			final HE_Vertex v2 = e.getEndVertex();
			if (!result.contains(v1)) {
				result.add(v1);
			}
			if (!result.contains(v2)) {
				result.add(v2);
			}

		}
		return result;
	}

	/**
	 * Get inner vertices.
	 *
	 * @return inner vertices as FastList<HE_Vertex>
	 */
	public List<HE_Vertex> getInnerVertices() {
		final HE_Selection sel = get();
		sel.collectVertices();
		final List<HE_Vertex> result = new FastList<HE_Vertex>();
		final List<HE_Vertex> outerVertices = getOuterVertices();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = sel.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (!outerVertices.contains(v)) {
				result.add(v);
			}
		}
		return result;
	}

	/**
	 * Get vertices in selection on mesh boundary.
	 *
	 * @return boundary vertices in selection as FastList<HE_Vertex>
	 */
	public List<HE_Vertex> getBoundaryVertices() {

		final List<HE_Vertex> result = new FastList<HE_Vertex>();
		final List<HE_Edge> outerEdges = getOuterEdges();
		for (int i = 0; i < outerEdges.size(); i++) {
			final HE_Edge e = outerEdges.get(i);
			if ((e.getFirstFace() == null) || (e.getSecondFace() == null)) {
				final HE_Vertex v1 = e.getStartVertex();
				final HE_Vertex v2 = e.getEndVertex();
				if (!result.contains(v1)) {
					result.add(v1);
				}
				if (!result.contains(v2)) {
					result.add(v2);
				}
			}

		}
		return result;
	}

	/**
	 * Get outer halfedges.
	 *
	 * @return outside halfedges of outer edges as FastList<HE_halfedge>
	 */
	public List<HE_Halfedge> getOuterHalfedges() {
		final HE_Selection sel = get();
		sel.collectHalfedges();
		final List<HE_Halfedge> result = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = sel.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			final HE_Face f1 = he.getFace();
			if ((f1 == null) || (!contains(f1))) {
				result.add(he);
			}
		}
		return result;
	}

	/**
	 * Get outer halfedges.
	 *
	 * @return inside halfedges of outer edges as FastList<HE_halfedge>
	 */
	public List<HE_Halfedge> getOuterHalfedgesInside() {
		final HE_Selection sel = get();
		sel.collectHalfedges();
		final List<HE_Halfedge> result = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = sel.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			final HE_Face f1 = he.getPair().getFace();
			if ((f1 == null) || (!contains(f1))) {
				result.add(he);
			}
		}
		return result;
	}

	/**
	 * Get innerhalfedges.
	 *
	 * @return inner halfedges as FastList<HE_halfedge>
	 */
	public List<HE_Halfedge> getInnerHalfedges() {
		final HE_Selection sel = get();
		sel.collectHalfedges();
		final List<HE_Halfedge> result = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = sel.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (contains(he.getPair().getFace()) && contains(he.getFace())) {
				result.add(he);
			}
		}
		return result;
	}

	/**
	 * Copy selection.
	 *
	 * @return copy of selection
	 */
	public HE_Selection get() {
		final HE_Selection copy = new HE_Selection(parent);
		copy.addFaces(getFacesAsArray());
		copy.addVertices(getVerticesAsArray());
		copy.addHalfedges(getHalfedgesAsArray());
		copy.addEdges(getEdgesAsArray());
		return copy;
	}

	/**
	 * Add selection.
	 *
	 * @param sel selection to add
	 */
	public void union(final HE_Selection sel) {

		addFaces(sel.getFacesAsArray());
		addVertices(sel.getVerticesAsArray());
		addHalfedges(sel.getHalfedgesAsArray());
		addEdges(sel.getEdgesAsArray());
	}

	/**
	 * Remove selection.
	 *
	 * @param sel selection to remove
	 */
	public void subtract(final HE_Selection sel) {
		removeFaces(sel.getFacesAsArray());
		removeVertices(sel.getVerticesAsArray());
		removeEdges(sel.getEdgesAsArray());
		removeHalfedges(sel.getHalfedgesAsArray());
	}

	/**
	 * Remove elements outside selection.
	 *
	 * @param sel selection to check
	 */
	public void intersect(final HE_Selection sel) {
		final FastList<HE_Face> newFaces = new FastList<HE_Face>();
		HE_Face f;
		final Iterator<HE_Face> fItr = sel.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (contains(f)) {
				newFaces.add(f);
			}
		}
		replaceFaces(newFaces);
		final FastList<HE_Edge> newEdges = new FastList<HE_Edge>();
		final Iterator<HE_Edge> eItr = sel.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (contains(e)) {
				newEdges.add(e);
			}
		}
		replaceEdges(newEdges);
		final FastList<HE_Halfedge> newHalfedges = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = sel.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (contains(he)) {
				newHalfedges.add(he);
			}
		}
		replaceHalfedges(newHalfedges);
		final FastList<HE_Vertex> newVertices = new FastList<HE_Vertex>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = sel.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (contains(v)) {
				newVertices.add(v);
			}
		}
		replaceVertices(newVertices);
	}

	/**
	 * Grow face selection outwards by one face.
	 */
	public void grow() {
		final FastList<HE_Face> currentFaces = new FastList<HE_Face>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			currentFaces.add(f);
			addFaces(f.getNeighborFaces());
		}
	}

	/**
	 * Grow face selection outwards.
	 *
	 * @param n number of faces to grow
	 */
	public void grow(final int n) {
		for (int i = 0; i < n; i++) {
			grow();
		}
	}

	/**
	 * Grow face selection inwards by one face.
	 */
	public void shrink() {
		final List<HE_Edge> outerEdges = getOuterEdges();

		for (int i = 0; i < outerEdges.size(); i++) {
			final HE_Edge e = outerEdges.get(i);
			final HE_Face f1 = e.getHalfedge().getFace();
			final HE_Face f2 = e.getHalfedge().getPair().getFace();
			if ((f1 == null) || (!contains(f1))) {
				remove(f2);
			}
			if ((f2 == null) || (!contains(f2))) {
				remove(f1);
			}

		}
	}

	/**
	 * Shrink face selection inwards.
	 *
	 * @param n number of faces to shrink
	 */
	public void shrink(final int n) {
		for (int i = 0; i < n; i++) {
			shrink();
		}
	}

	/**
	 * Select faces surrounding current face selection.
	 */
	public void surround() {
		final FastList<HE_Face> currentFaces = new FastList<HE_Face>();
		HE_Face face;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			currentFaces.add(face);
			addFaces(face.getNeighborFaces());
		}
		removeFaces(currentFaces);
	}

	/**
	 * Select faces surrounding current face selection at a distance of n-1 faces.
	 *
	 * @param n distance to current selection
	 */
	public void surround(final int n) {

		grow(n - 1);
		surround();

	}

	/**
	 * Add faces with certain number of edges in selection to selection.
	 *
	 * @param threshold number of edges that have to belong to the selection before
	 * a face is added
	 */
	public void smooth(final int threshold) {
		final FastList<HE_Halfedge> currentHalfedges = new FastList<HE_Halfedge>();
		HE_Halfedge hei;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			hei = heItr.next();
			currentHalfedges.add(hei);
		}
		for (int i = 0; i < currentHalfedges.size(); i++) {
			final HE_Face f = currentHalfedges.get(i).getPair().getFace();
			if ((f != null) && (!contains(f))) {
				int ns = 0;
				HE_Halfedge he = f.getHalfedge();
				do {
					if (contains(he.getPair().getFace())) {
						ns++;
					}
					he = he.getNextInFace();
				} while (he != f.getHalfedge());
				if (ns >= threshold) {
					add(f);
				}

			}

		}

	}

	/**
	 * Add faces with certain proportion of edges in selection to selection.
	 *
	 * @param threshold number of edges that have to belong to the selection before
	 * a face is added
	 */
	public void smooth(final double threshold) {
		final FastList<HE_Halfedge> currentHalfedges = new FastList<HE_Halfedge>();
		HE_Halfedge hei;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			hei = heItr.next();
			currentHalfedges.add(hei);
		}
		for (int i = 0; i < currentHalfedges.size(); i++) {
			final HE_Face f = currentHalfedges.get(i).getPair().getFace();
			if ((f != null) && (!contains(f))) {
				int ns = 0;
				HE_Halfedge he = f.getHalfedge();
				do {
					if (contains(he.getPair().getFace())) {
						ns++;
					}
					he = he.getNextInFace();
				} while (he != f.getHalfedge());
				if (ns >= threshold * f.getFaceOrder()) {
					add(f);
				}

			}

		}

	}

	/**
	 * Select all mesh element.
	 *
	 * @param mesh mesh containing elements
	 * @return current selection
	 */
	public HE_Selection selectAll(final HE_Mesh mesh) {
		clear();
		addFaces(mesh.getFacesAsArray());
		addEdges(mesh.getEdgesAsArray());
		addHalfedges(mesh.getHalfedgesAsArray());
		addVertices(mesh.getVerticesAsArray());
		return this;
	}

	/**
	 * Invert current selection.
	 *
	 * @return inverted selection
	 */
	public HE_Selection invertSelection() {

		final HE_Selection invertedSelection = new HE_Selection(parent);
		HE_Face f;
		final Iterator<HE_Face> fItr = parent.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (!contains(f)) {
				invertedSelection.add(f);
			}
		}
		final Iterator<HE_Edge> eItr = parent.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (!contains(e)) {
				invertedSelection.add(e);
			}
		}
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = parent.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (!contains(he)) {
				invertedSelection.add(he);
			}
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (!contains(v)) {
				invertedSelection.add(v);
			}
		}

		replaceVertices(invertedSelection.getVerticesAsArray());
		replaceEdges(invertedSelection.getEdgesAsArray());
		replaceHalfedges(invertedSelection.getHalfedgesAsArray());
		replaceFaces(invertedSelection.getFacesAsArray());

		return this;
	}

	/**
	 * Invert current face selection.
	 *
	 * @return inverted face selection
	 */
	public HE_Selection invertFaces() {

		final HE_Selection invertedSelection = new HE_Selection(parent);
		HE_Face f;
		final Iterator<HE_Face> fItr = parent.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (!contains(f)) {
				invertedSelection.add(f);
			}
		}
		replaceFaces(invertedSelection.getFacesAsArray());
		return this;
	}

	/**
	 * Invert current edge election.
	 *
	 * @return inverted edge selection
	 */
	public HE_Selection invertEdges() {

		final HE_Selection invertedSelection = new HE_Selection(parent);
		final Iterator<HE_Edge> eItr = parent.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (!contains(e)) {
				invertedSelection.add(e);
			}
		}
		replaceEdges(invertedSelection.getEdgesAsArray());

		return this;
	}

	/**
	 * Invert current vertex selection.
	 *
	 * @return inverted vertex selection
	 */
	public HE_Selection invertVertices() {

		final HE_Selection invertedSelection = new HE_Selection(parent);

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = parent.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (!contains(v)) {
				invertedSelection.add(v);
			}
		}
		replaceVertices(invertedSelection.getVerticesAsArray());
		return this;
	}

	/**
	 * Invert current halfedge selection.
	 *
	 * @return inverted halfedge selection
	 */
	public HE_Selection invertHalfedges() {

		final HE_Selection invertedSelection = new HE_Selection(parent);
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = parent.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (!contains(he)) {
				invertedSelection.add(he);
			}
		}

		replaceHalfedges(invertedSelection.getHalfedgesAsArray());

		return this;
	}

	/**
	 * Clean current selection, removes all elements no longer part of mesh.
	 *
	 * @return current selection
	 */

	public HE_Selection cleanSelection() {

		final FastList<HE_Vertex> verticesToClean = new FastList<HE_Vertex>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (!parent.contains(v)) {
				verticesToClean.add(v);
			}
		}

		final FastList<HE_Face> facesToClean = new FastList<HE_Face>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (!parent.contains(f)) {
				facesToClean.add(f);
			}
		}
		final FastList<HE_Edge> edgesToClean = new FastList<HE_Edge>();
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (!parent.contains(e)) {
				edgesToClean.add(e);
			}
		}

		final FastList<HE_Halfedge> halfedgesToClean = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (!parent.contains(he)) {
				halfedgesToClean.add(he);
			}

		}
		removeEdges(edgesToClean);
		removeHalfedges(halfedgesToClean);
		removeVertices(verticesToClean);
		removeFaces(facesToClean);
		return this;
	}

	/**
	 * Collect vertices belonging to selection elements.
	 */
	public void collectVertices() {
		List<HE_Vertex> tmpVertices = new FastList<HE_Vertex>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			tmpVertices = f.getFaceVertices();
			addVertices(tmpVertices);
		}
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			add(e.getStartVertex());
			add(e.getEndVertex());
		}
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			add(he.getVertex());

		}
	}

	/**
	 * Collect faces belonging to selection elements.
	 */
	public void collectFaces() {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			addFaces(v.getFaceStar());
		}
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			add(e.getFirstFace());
			add(e.getSecondFace());
		}
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			add(he.getFace());

		}
	}

	public void getFacesWithNormal(final WB_Point3d n, final double ta) {
		HE_Face f;
		final Iterator<HE_Face> fItr = parent.fItr();
		final double d = n.get().normalize();
		final double cta = d * Math.cos(ta);
		while (fItr.hasNext()) {
			f = fItr.next();

			if (f.getFaceNormal().dot(n) > cta) {
				add(f);
			}
		}

	}

	/**
	 * Collect edges belonging to face selection.
	 */
	public void collectEdges() {
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			addEdges(f.getFaceEdges());
		}

	}

	/**
	 * Collect halfedges belonging to face selection.
	 */
	public void collectHalfedges() {
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			addHalfedges(f.getFaceHalfedges());
		}
		final Iterator<HE_Halfedge> heItr = heItr();
		final FastList<HE_Halfedge> newhalfedges = new FastList<HE_Halfedge>();
		while (heItr.hasNext()) {
			newhalfedges.add(heItr.next().getPair());
		}
		addHalfedges(newhalfedges);
	}
}
