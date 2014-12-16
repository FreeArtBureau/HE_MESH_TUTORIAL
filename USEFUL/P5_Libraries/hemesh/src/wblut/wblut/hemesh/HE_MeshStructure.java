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

import wblut.geom.WB_AABB;
import wblut.geom.WB_Point3d;


import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Collection of mesh elements. Contains methods for adding, deleting and accessing elements
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public abstract class HE_MeshStructure {

	/** Linked HashMap of vertices.*/
	protected FastMap<Integer, HE_Vertex>	_hashedVertices;

	/** Iterator over vertices. */
	protected Iterator<HE_Vertex>			_vertexItr;

	/** Linked HashMap of halfedges. */
	protected FastMap<Integer, HE_Halfedge>	_hashedHalfedges;

	/** Iterator over halfedges. */
	protected Iterator<HE_Halfedge>			_halfedgeItr;

	/** Linked HashMap of edges. */
	protected FastMap<Integer, HE_Edge>		_hashedEdges;

	/** Iterator over edges. */
	protected Iterator<HE_Edge>				_edgeItr;

	/** Static structure key counter. */
	protected static int					_currentKey;

	/** Unique structure key. */
	protected final Integer					_key;

	/**
	 * Instantiates a new HE_Structure.
	 */
	protected HE_MeshStructure() {
		_key = new Integer(_currentKey);
		_currentKey++;
		_hashedVertices = new FastMap<Integer, HE_Vertex>();
		_hashedHalfedges = new FastMap<Integer, HE_Halfedge>();
		_hashedEdges = new FastMap<Integer, HE_Edge>();
		_hashedFaces = new FastMap<Integer, HE_Face>();

	}

	/**
	 * Get key.
	 *
	 * @return key
	 */

	public Integer key() {
		return _key;
	}

	/**
	 * Clear entire structure.
	 */
	public void clear() {
		_hashedVertices.clear();
		_hashedHalfedges.clear();
		_hashedEdges.clear();
		_hashedFaces.clear();
	}

	/**
	 * Clear vertices.
	 */
	public final void clearVertices() {
		_hashedVertices.clear();
	}

	/**
	 * Clear halfedges.
	 */
	public final void clearHalfedges() {
		_hashedHalfedges.clear();
	}

	/**
	 * Clear edges.
	 */
	public final void clearEdges() {
		_hashedEdges.clear();
	}

	/**
	 * Vertices as array.
	 *
	 * @return all vertices as HE_Vertex[]
	 */
	public final HE_Vertex[] getVerticesAsArray() {
		final HE_Vertex[] vertices = new HE_Vertex[numberOfVertices()];
		final Collection<HE_Vertex> _vertices = _hashedVertices.values();
		final Iterator<HE_Vertex> vitr = _vertices.iterator();
		int i = 0;
		while (vitr.hasNext()) {
			vertices[i] = vitr.next();
			i++;
		}
		return vertices;
	}

	/**
	 * Halfedges as array.
	 *
	 * @return all halfedges as HE_Halfedge[]
	 */
	public final HE_Halfedge[] getHalfedgesAsArray() {
		final HE_Halfedge[] halfedges = new HE_Halfedge[numberOfHalfedges()];
		final Iterator<HE_Halfedge> heItr = _hashedHalfedges.values()
				.iterator();
		int i = 0;
		while (heItr.hasNext()) {
			halfedges[i] = heItr.next();
			i++;
		}
		return halfedges;
	}

	/**
	 * Edges as array.
	 *
	 * @return all edges as HE_Edge[]
	 */
	public final HE_Edge[] getEdgesAsArray() {
		final HE_Edge[] edges = new HE_Edge[numberOfEdges()];
		final Iterator<HE_Edge> eItr = _hashedEdges.values().iterator();
		int i = 0;
		while (eItr.hasNext()) {
			edges[i] = eItr.next();
			i++;
		}
		return edges;
	}

	/**
	 * Vertices as arrayList.
	 *
	 * @return all vertices as FastList<HE_Vertex>
	 */
	public final List<HE_Vertex> getVerticesAsList() {
		final List<HE_Vertex> vertices = new FastList<HE_Vertex>();
		final Collection<HE_Vertex> _vertices = _hashedVertices.values();
		vertices.addAll(_vertices);
		return (vertices);
	}

	/**
	 * Halfedges as arrayList.
	 *
	 * @return all vertices as FastList<HE_Halfedge>
	 */
	public final List<HE_Halfedge> getHalfedgesAsList() {
		final List<HE_Halfedge> halfedges = new FastList<HE_Halfedge>();
		halfedges.addAll(_hashedHalfedges.values());
		return (halfedges);
	}

	/**
	 * Edges as arrayList.
	 *
	 * @return all vertices as FastList<HE_Edge>
	 */
	public final List<HE_Edge> getEdgesAsList() {
		final List<HE_Edge> edges = new FastList<HE_Edge>();
		edges.addAll(_hashedEdges.values());
		return (edges);
	}

	/**
	 * Get vertex.
	 *
	 * @param key vertex key
	 * @return vertex
	 */
	public final HE_Vertex getVertexByKey(final Integer key) {
		return _hashedVertices.get(key);
	}

	/**
	 * Get halfedge.
	 *
	 * @param key halfedge key
	 * @return halfedge
	 */
	public final HE_Halfedge getHalfedgeByKey(final Integer key) {
		return _hashedHalfedges.get(key);
	}

	/**
	 * Get edge.
	 *
	 * @param key edge key
	 * @return edge
	 */
	public final HE_Edge getEdgeByKey(final Integer key) {
		return _hashedEdges.get(key);
	}

	/**
	 * Check if structure contains edge.
	 *
	 * @param e edge
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Edge e) {
		return _hashedEdges.containsKey(e._key);
	}

	/**
	 * Check if structure contains halfedge.
	 *
	 * @param he halfedge
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Halfedge he) {
		return _hashedHalfedges.containsKey(he._key);
	}

	/**
	 * Check if structure contains vertex.
	 *
	 * @param v vertex
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Vertex v) {
		return _hashedVertices.containsKey(v.key());
	}

	/**
	 * Number of edges.
	 *
	 * @return the number of edges
	 */
	public final int numberOfEdges() {
		return _hashedEdges.size();
	}

	/**
	 * Number of halfedges.
	 *
	 * @return the number of halfedges
	 */
	public final int numberOfHalfedges() {
		return _hashedHalfedges.size();
	}

	/**
	 * Number of vertices.
	 *
	 * @return the number of vertices
	 */
	public final int numberOfVertices() {
		return _hashedVertices.size();
	}

	/**
	 * Add the edge.
	 *
	 * @param e edge to add
	 */
	public final void add(final HE_Edge e) {
		_hashedEdges.put(e.key(), e);

	}

	/**
	 * Adds the halfedge.
	 *
	 * @param he halfedge to add
	 */
	public final void add(final HE_Halfedge he) {
		_hashedHalfedges.put(he.key(), he);
	}

	/**
	 * Add the vertex.
	 *
	 * @param v vertex to add
	 */
	public final void add(final HE_Vertex v) {
		_hashedVertices.put(v.key(), v);
	}

	/**
	 * Adds vertices.
	 *
	 * @param vertices vertices to add as HE_Vertex[]
	 */
	public final void addVertices(final HE_Vertex[] vertices) {
		for (final HE_Vertex vertex : vertices) {
			add(vertex);
		}
	}

	/**
	 * Adds edges.
	 *
	 * @param edges edges to add as HE_Edge[]
	 */
	public final void addEdges(final HE_Edge[] edges) {
		for (final HE_Edge edge : edges) {
			add(edge);
		}
	}

	/**
	 * Adds halfedges.
	 *
	 * @param halfedges halfedges to add as HE_Halfedge[]
	 */
	public final void addHalfedges(final HE_Halfedge[] halfedges) {
		for (final HE_Halfedge halfedge : halfedges) {
			add(halfedge);
		}
	}

	/**
	 * Adds vertices.
	 *
	 * @param vertices vertices to add as FastList<HE_Vertex>
	 */
	public final void addVertices(final List<HE_Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			add(vertices.get(i));
		}
	}

	/**
	 * Adds edges.
	 *
	 * @param edges edges to add as FastList<HE_Edge>
	 */
	public final void addEdges(final List<HE_Edge> edges) {
		for (int i = 0; i < edges.size(); i++) {
			add(edges.get(i));
		}
	}

	/**
	 * Adds halfedges.
	 *
	 * @param halfedges halfedges to add as FastList<HE_Halfedge>
	 */
	public final void addHalfedges(final List<HE_Halfedge> halfedges) {
		for (int i = 0; i < halfedges.size(); i++) {
			add(halfedges.get(i));
		}
	}

	/**
	 * Removes edge.
	 *
	 * @param e edge to remove
	 */
	public void remove(final HE_Edge e) {
		_hashedEdges.remove(e._key);
	}

	/**
	 * Removes halfedge.
	 *
	 * @param he halfedge to remove
	 */
	public void remove(final HE_Halfedge he) {
		_hashedHalfedges.remove(he._key);
	}

	/**
	 * Removes vertex.
	 *
	 * @param v vertex to remove
	 */
	public void remove(final HE_Vertex v) {
		_hashedVertices.remove(v.key());
	}

	/**
	 * Removes vertices.
	 *
	 * @param vertices vertices to remove as HE_Vertex[]
	 */
	public final void removeVertices(final HE_Vertex[] vertices) {
		for (final HE_Vertex vertice : vertices) {
			remove(vertice);
		}
	}

	/**
	 * Removes edges.
	 *
	 * @param edges edges to remove as HE_Edge[]
	 */
	public final void removeEdges(final HE_Edge[] edges) {
		for (final HE_Edge edge : edges) {
			remove(edge);
		}
	}

	/**
	 * Removes halfedges.
	 *
	 * @param halfedges halfedges to remove as HE_Halfedge[]
	 */
	public final void removeHalfedges(final HE_Halfedge[] halfedges) {
		for (final HE_Halfedge halfedge : halfedges) {
			remove(halfedge);
		}
	}

	/**
	 * Removes vertices.
	 *
	 * @param vertices vertices to remove as FastList<HE_Vertex>
	 */
	public final void removeVertices(final List<HE_Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			remove(vertices.get(i));
		}
	}

	/**
	 * Removes edges.
	 *
	 * @param edges edges to remove as FastList<HE_Edge>
	 */
	public final void removeEdges(final List<HE_Edge> edges) {
		for (int i = 0; i < edges.size(); i++) {
			remove(edges.get(i));
		}
	}

	/**
	 * Removes halfedges.
	 *
	 * @param halfedges halfedges to remove as FastList<HE_Halfedge>
	 */
	public final void removeHalfedges(final List<HE_Halfedge> halfedges) {
		for (int i = 0; i < halfedges.size(); i++) {
			remove(halfedges.get(i));
		}
	}

	/**
	 * Replace vertices.
	 *
	 * @param vertices vertices to replace with as HE_Vertex[]
	 */
	public final void replaceVertices(final HE_Vertex[] vertices) {
		_hashedVertices.clear();
		final Collection<HE_Vertex> _vertices = _hashedVertices.values();
		_vertices.clear();
		for (final HE_Vertex vertice : vertices) {
			add(vertice);
		}
	}

	/**
	 * Replace edges.
	 *
	 * @param edges edges to replace with as HE_Edge[]
	 */
	public final void replaceEdges(final HE_Edge[] edges) {
		_hashedEdges.clear();
		for (final HE_Edge edge : edges) {
			add(edge);
		}
	}

	/**
	 * Replace halfedges.
	 *
	 * @param halfedges halfedges to replace with as HE_Halfedge[]
	 */
	public final void replaceHalfedges(final HE_Halfedge[] halfedges) {
		_hashedHalfedges.clear();
		for (final HE_Halfedge halfedge : halfedges) {
			add(halfedge);
		}
	}

	/**
	 * Replace vertices.
	 *
	 * @param vertices vertices to replace with as HE_Vertex[]
	 */
	public final void replaceVertices(final List<HE_Vertex> vertices) {
		_hashedVertices.clear();
		final Collection<HE_Vertex> _vertices = _hashedVertices.values();
		_vertices.clear();
		for (int i = 0; i < vertices.size(); i++) {
			add(vertices.get(i));
		}
	}

	/**
	 * Replace edges.
	 *
	 * @param edges edges to replace with as HE_Edge[]
	 */
	public final void replaceEdges(final List<HE_Edge> edges) {
		_hashedEdges.clear();
		for (int i = 0; i < edges.size(); i++) {
			add(edges.get(i));
		}
	}

	/**
	 * Replace halfedges.
	 *
	 * @param halfedges halfedges to replace with as HE_halfedge[]
	 */
	public final void replaceHalfedges(final List<HE_Halfedge> halfedges) {
		_hashedHalfedges.clear();
		for (int i = 0; i < halfedges.size(); i++) {
			add(halfedges.get(i));
		}
	}

	/**
	 * Vertex iterator.
	 *
	 * @return vertex iterator
	 */
	public Iterator<HE_Vertex> vItr() {
		return _hashedVertices.values().iterator();
	}

	/**
	 * Edge iterator.
	 *
	 * @return edge iterator
	 */
	public Iterator<HE_Edge> eItr() {
		return _hashedEdges.values().iterator();
	}

	/**
	 * Hslfedge iterator.
	 *
	 * @return halfedge iterator
	 */
	public Iterator<HE_Halfedge> heItr() {
		return _hashedHalfedges.values().iterator();
	}

	/** Linked HashMap of faces. */
	protected FastMap<Integer, HE_Face>	_hashedFaces;

	/** Iterator over faces. */
	protected Iterator<HE_Face>			_faceItr;

	/**
	 * Clear faces.
	 */
	public final void clearFaces() {
		_hashedFaces.clear();
	}

	/**
	 * Faces as array.
	 *
	 * @return all faces as HE_Face[]
	 */
	public final HE_Face[] getFacesAsArray() {
		final HE_Face[] faces = new HE_Face[numberOfFaces()];
		final Iterator<HE_Face> fItr = _hashedFaces.values().iterator();
		int i = 0;
		while (fItr.hasNext()) {
			faces[i] = fItr.next();
			i++;
		}
		return faces;
	}

	/**
	 * Faces as arrayList.
	 *
	 * @return all vertices as FastList<HE_Face>
	 */
	public final List<HE_Face> getFacesAsList() {
		final List<HE_Face> faces = new FastList<HE_Face>();
		faces.addAll(_hashedFaces.values());
		return (faces);
	}

	/**
	 * Get face.
	 *
	 * @param key face key
	 * @return face
	 */
	public final HE_Face getFaceByKey(final Integer key) {
		return _hashedFaces.get(key);
	}

	/**
	 * Check if structure contains face.
	 *
	 * @param f face
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Face f) {
		return _hashedFaces.containsKey(f._key);
	}

	/**
	 * Number of faces.
	 *
	 * @return the number of faces
	 */
	public final int numberOfFaces() {
		return _hashedFaces.size();
	}

	/**
	 * Add the face.
	 *
	 * @param f face to add
	 */
	public final void add(final HE_Face f) {
		_hashedFaces.put(f.key(), f);

	}

	/**
	 * Adds faces.
	 *
	 * @param faces faces to add as HE_Face[]
	 */
	public final void addFaces(final HE_Face[] faces) {
		for (final HE_Face face : faces) {
			add(face);
		}
	}

	/**
	 * Adds faces.
	 *
	 * @param faces faces to add as HE_Face[]
	 */
	public final void addFaces(final List<HE_Face> faces) {
		for (int i = 0; i < faces.size(); i++) {
			add(faces.get(i));
		}
	}

	/**
	 * Removes face.
	 *
	 * @param f face to remove
	 */
	public void remove(final HE_Face f) {
		_hashedFaces.remove(f._key);
	}

	/**
	 * Removes faces.
	 *
	 * @param faces faces to remove as HE_Face[]
	 */
	public final void removeFaces(final HE_Face[] faces) {
		for (final HE_Face face : faces) {
			remove(face);
		}
	}

	/**
	 * Removes faces.
	 *
	 * @param faces faces to remove as FastList<HE_Face>
	 */
	public final void removeFaces(final List<HE_Face> faces) {
		for (int i = 0; i < faces.size(); i++) {
			remove(faces.get(i));
		}
	}

	/**
	 * Replace faces.
	 *
	 * @param faces faces to replace with as HE_Face[]
	 */
	public final void replaceFaces(final HE_Face[] faces) {
		_hashedFaces.clear();
		for (final HE_Face face : faces) {
			add(face);
		}
	}

	/**
	 * Replace faces.
	 *
	 * @param faces faces to replace with as HE_Face[]
	 */
	public final void replaceFaces(final List<HE_Face> faces) {
		_hashedFaces.clear();
		for (int i = 0; i < faces.size(); i++) {
			add(faces.get(i));
		}
	}

	/**
	 * Face iterator.
	 *
	 * @return face iterator
	 */
	public Iterator<HE_Face> fItr() {
		return _hashedFaces.values().iterator();
	}

	/**
	 * Get range of vertex coordinates.
	 *
	 * @return array of limit values: min x, min y, min z, max x, max y, max z
	 */
	public double[] limits() {

		final double[] result = new double[6];
		for (int i = 0; i < 3; i++) {
			result[i] = Double.POSITIVE_INFINITY;
		}
		for (int i = 3; i < 6; i++) {
			result[i] = Double.NEGATIVE_INFINITY;
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[0] = Math.min(result[0], v.x);
			result[1] = Math.min(result[1], v.y);
			result[2] = Math.min(result[2], v.z);
			result[3] = Math.max(result[3], v.x);
			result[4] = Math.max(result[4], v.y);
			result[5] = Math.max(result[5], v.z);
		}
		return result;
	}

	/**
	 * Get axis-aligned bounding box surrounding mesh.
	 *
	 * @return WB_AABB axis-aligned bounding box
	 */

	public WB_AABB getAABB() {
		final double[] result = limits();
		final WB_Point3d min = new WB_Point3d(result[0], result[1], result[2]);
		final WB_Point3d max = new WB_Point3d(result[3], result[4], result[5]);
		return new WB_AABB(min, max);

	}

}
