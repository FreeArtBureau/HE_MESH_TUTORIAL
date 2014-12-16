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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import wblut.WB_Epsilon;
import wblut.core.WB_HasData;
import wblut.geom.WB_AABB;
import wblut.geom.WB_ClassifyPointToPlane;
import wblut.geom.WB_Distance;
import wblut.geom.WB_ExplicitPolygon;
import wblut.geom.WB_ExplicitSegment;
import wblut.geom.WB_Frame;
import wblut.geom.WB_IndexedSegment;
import wblut.geom.WB_IndexedTriangle;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_KDNeighbor;
import wblut.geom.WB_KDTree3Dold;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_PolygonType2D;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector3d;


import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastTable;

/**
 * Half-edge mesh data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Mesh extends HE_MeshStructure  implements WB_HasData {

	/** Stored mesh center. */
	private WB_Point3d	_center;

	/** Status of mesh center. */
	private boolean		_centerUpdated;

	/** General purpose label. */
	protected int		label;

	private HashMap<String, Object> _data;
	
	/**
	 * Instantiates a new HE_Mesh.
	 *
	 */
	public HE_Mesh() {
		super();
		_center = new WB_Point3d();
		_centerUpdated = false;
		label = -1;
	}

	public void setLabel(final int lab) {
		label = lab;

	}

	public int getLabel() {
		return label;

	}

	// CREATE

	/**
	 * Constructor.
	 * 
	 * @param creator
	 *            HE_Creator that generates this mesh
	 */
	public HE_Mesh(final HEC_Creator creator) {
		super();
		setNoCopy(creator.create());
		_centerUpdated = false;
		label = -1;
	}

	// MODIFY

	/**
	 * Modify the mesh.
	 * 
	 * @param modifier
	 *            HE_Modifier to apply
	 * @return self
	 */
	public HE_Mesh modify(final HEM_Modifier modifier) {
		return modifier.apply(this);
	}

	/**
	 * Modify selection. Elements should be part of this mesh.
	 * 
	 * @param modifier
	 *            HE_Modifier to apply
	 * @return self
	 */
	public HE_Mesh modifySelected(final HEM_Modifier modifier,
			final HE_Selection selection) {
		return modifier.apply(selection.get());
	}

	// SUBDIVIDE

	/**
	 * Subdivide the mesh.
	 * 
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @return self
	 */
	public HE_Mesh subdivide(final HES_Subdividor subdividor) {
		return subdividor.apply(this);
	}

	/**
	 * Subdivide selection of the mesh.
	 * 
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @param selection
	 *            HE_Selection
	 * @return self
	 */
	public HE_Mesh subdivideSelected(final HES_Subdividor subdividor,
			final HE_Selection selection) {
		return subdividor.apply(selection);

	}

	/**
	 * Subdivide the mesh a number of times.
	 *
	 * @param subdividor HE_Subdividor to apply
	 * @param rep subdivision iterations. WARNING: higher values will lead to
	 * unmanageable number of faces.
	 * @return self
	 */
	public HE_Mesh subdivide(final HES_Subdividor subdividor, final int rep) {
		for (int i = 0; i < rep; i++) {
			subdivide(subdividor);
		}
		return this;
	}

	/**
	 * Subdivide a selection of the mesh a number of times.
	 * 
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @param selection
	 *            HE_Selection initial selection
	 * @param rep subdivision iterations
	 * @return self
	 */
	public HE_Mesh subdivideSelected(final HES_Subdividor subdividor,
			final HE_Selection selection, final int rep) {
		for (int i = 0; i < rep; i++) {
			subdivideSelected(subdividor, selection);
		}
		return this;

	}

	
	public HE_Mesh simplify(final HES_Simplifier simplifier) {
		return simplifier.apply(this);
	}
	
	/**
	 * Deep copy of mesh.
	 * 
	 * @return copy as new HE_Mesh, includes selection
	 */
	public HE_Mesh get() {
		final HE_Mesh result = new HE_Mesh();
		final HashMap<Integer, Integer> vertexCorrelation = new HashMap<Integer, Integer>();
		final HashMap<Integer, Integer> faceCorrelation = new HashMap<Integer, Integer>();
		final HashMap<Integer, Integer> halfedgeCorrelation = new HashMap<Integer, Integer>();
		final HashMap<Integer, Integer> edgeCorrelation = new HashMap<Integer, Integer>();
		HE_Vertex rv;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			rv = new HE_Vertex();
			result.add(rv);
			vertexCorrelation.put(v.key(), rv.key());
		}
		HE_Face rf;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			rf = new HE_Face();
			result.add(rf);
			rf.label = f.label;
			faceCorrelation.put(f.key(), rf.key());
		}
		HE_Halfedge rhe;
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			rhe = new HE_Halfedge();
			result.add(rhe);
			halfedgeCorrelation.put(he.key(), rhe.key());
		}

		HE_Edge re;
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			re = new HE_Edge();
			result.add(re);
			edgeCorrelation.put(e.key(), re.key());

		}

		HE_Vertex sv;
		HE_Vertex tv;
		final Iterator<HE_Vertex> svItr = vItr();
		final Iterator<HE_Vertex> tvItr = result.vItr();
		Integer key;
		while (svItr.hasNext()) {
			sv = svItr.next();
			tv = tvItr.next();
			tv.set(sv);
			if (sv.getHalfedge() != null) {
				key = halfedgeCorrelation.get(sv.getHalfedge().key());
				tv.setHalfedge(result.getHalfedgeByKey(key));
			}
		}

		HE_Face sf;
		HE_Face tf;

		final Iterator<HE_Face> sfItr = fItr();
		final Iterator<HE_Face> tfItr = result.fItr();
		while (sfItr.hasNext()) {
			sf = sfItr.next();
			tf = tfItr.next();
			if (sf.getHalfedge() != null) {
				key = halfedgeCorrelation.get(sf.getHalfedge().key());
				tf.setHalfedge(result.getHalfedgeByKey(key));
			}

		}

		final Iterator<HE_Edge> seItr = eItr();
		final Iterator<HE_Edge> teItr = result.eItr();
		HE_Edge se;
		HE_Edge te;
		while (seItr.hasNext()) {
			se = seItr.next();
			te = teItr.next();
			if (se.getHalfedge() != null) {
				key = halfedgeCorrelation.get(se.getHalfedge().key());
				te.setHalfedge(result.getHalfedgeByKey(key));
			}

		}
		HE_Halfedge she;
		HE_Halfedge the;

		final Iterator<HE_Halfedge> sheItr = heItr();
		final Iterator<HE_Halfedge> theItr = result.heItr();
		while (sheItr.hasNext()) {
			she = sheItr.next();
			the = theItr.next();
			if (she.getPair() != null) {
				key = halfedgeCorrelation.get(she.getPair().key());
				the.setPair(result.getHalfedgeByKey(key));
			}
			if (she.getNextInFace() != null) {
				key = halfedgeCorrelation.get(she.getNextInFace().key());
				the.setNext(result.getHalfedgeByKey(key));
			}
			if (she.getVertex() != null) {
				key = vertexCorrelation.get(she.getVertex().key());
				the.setVertex(result.getVertexByKey(key));
			}
			if (she.getFace() != null) {
				key = faceCorrelation.get(she.getFace().key());
				the.setFace(result.getFaceByKey(key));
			}

			if (she.getEdge() != null) {
				key = edgeCorrelation.get(she.getEdge().key());
				the.setEdge(result.getEdgeByKey(key));
			}
		}
		result._center.set(_center);
		result._centerUpdated = _centerUpdated;
		return result;
	}

	/**
	 * Add all mesh elements to this mesh. No copies are made.
	 * 
	 * @param mesh mesh to add
	 */
	public void add(final HE_Mesh mesh) {
		addVertices(mesh.getVerticesAsArray());
		addFaces(mesh.getFacesAsArray());
		addEdges(mesh.getEdgesAsArray());
		addHalfedges(mesh.getHalfedgesAsArray());

	}

	/**
	 * Add all mesh elements to this mesh. No copies are made. Tries to join geometry.
	 * 
	 * @param mesh mesh to add
	 */
	public void fuse(final HE_Mesh mesh) {
		addVertices(mesh.getVerticesAsArray());
		addFaces(mesh.getFacesAsArray());
		addEdges(mesh.getEdgesAsArray());
		addHalfedges(mesh.getHalfedgesAsArray());
		set(new HE_Mesh(new HEC_FromPolygons().setPolygons(this
				.getPolygonList())));
	}

	/**
	 * Replace mesh with deep copy of target.
	 * 
	 * @param target
	 *            HE_Mesh to be duplicated
	 */
	public void set(final HE_Mesh target) {
		final HE_Mesh result = target.get();
		replaceVertices(result.getVerticesAsArray());
		replaceFaces(result.getFacesAsArray());
		replaceHalfedges(result.getHalfedgesAsArray());
		replaceEdges(result.getEdgesAsArray());

	}

	/**
	 * Replace mesh with shallow copy of target.
	 * 
	 * @param target
	 *            HE_Mesh to be duplicated
	 */
	private void setNoCopy(final HE_Mesh target) {
		_hashedVertices = target._hashedVertices;
		_hashedHalfedges = target._hashedHalfedges;
		_hashedEdges = target._hashedEdges;
		_hashedFaces = target._hashedFaces;

		_center = target._center;
		_centerUpdated = target._centerUpdated;

	}

	// CONVERT

	/**
	 * Return all vertex positions as an array .
	 * 
	 * @return 2D array of float. First index gives vertex. Second index gives
	 *         x-,y- or z-coordinate.
	 */
	public float[][] getVerticesAsFloat() {
		final float[][] result = new float[numberOfVertices()][3];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i][0] = (float) (v.x);
			result[i][1] = (float) (v.y);
			result[i][2] = (float) (v.z);
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex positions as an array .
	 * 
	 * @return 2D array of double. First index gives vertex. Second index gives
	 *         x-,y- or z-coordinate.
	 */
	public double[][] getVerticesAsDouble() {
		final double[][] result = new double[numberOfVertices()][3];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i][0] = (float) (v.x);
			result[i][1] = (float) (v.y);
			result[i][2] = (float) (v.z);
			i++;
		}
		return result;
	}

	public Map<Integer, Integer> vertexKeyToIndex() {
		final Map<Integer, Integer> map = new FastMap<Integer, Integer>();
		int i = 0;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			map.put(vItr.next().key(), i);
			i++;
		}
		return map;
	}

	/**
	 * Return all vertex positions.
	 * 
	 * @return array of WB_Point, values are copied.
	 */
	public WB_Point3d[] getVerticesAsNewPoint() {
		final WB_Point3d[] result = new WB_Point3d[numberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = new WB_Point3d(v);
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex positions.
	 * 
	 * @return array of WB_Point, no copies are made.
	 */
	public WB_Point3d[] getVerticesAsPoint() {
		final WB_Point3d[] result = new WB_Point3d[numberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = v;
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex normal.
	 * 
	 * @return array of WB_Normal.
	 */
	public WB_Normal3d[] getVertexNormals() {
		final WB_Normal3d[] result = new WB_Normal3d[numberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = v.getVertexNormal();
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex normal.
	 * 
	 * @return FastMap of WB_Normal.
	 */
	public Map<Integer, WB_Normal3d> getKeyedVertexNormals() {
		final Map<Integer, WB_Normal3d> result = new FastMap<Integer, WB_Normal3d>(
				numberOfVertices());
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result.put(v.key(), v.getVertexNormal());
		}
		return result;
	}

	/**
	 * Return the faces as array of vertex indices.
	 * 
	 * @return 2D array of int. First index gives face. Second index gives
	 *         vertices.
	 */
	public int[][] getFacesAsInt() {
		final int[][] result = new int[numberOfFaces()][];
		final FastMap<Integer, Integer> vertexKeys = new FastMap<Integer, Integer>();
		final Iterator<HE_Vertex> vItr = vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}

		final Iterator<HE_Face> fItr = fItr();
		HE_Halfedge he;
		HE_Face f;
		i = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = new int[f.getFaceOrder()];
			he = f.getHalfedge();
			int j = 0;
			do {
				result[i][j] = vertexKeys.get(he.getVertex().key());
				he = he.getNextInFace();
				j++;
			} while (he != f.getHalfedge());
			i++;
		}
		return result;
	}

	/**
	 * Return all face normals.
	 * 
	 * @return array of WB_Normal.
	 */
	public WB_Normal3d[] getFaceNormals() {
		final WB_Normal3d[] result = new WB_Normal3d[numberOfFaces()];
		int i = 0;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.getFaceNormal();
			i++;
		}
		return result;
	}

	/**
	 * Return all face normals.
	 * 
	 * @return FastMap of WB_Normal.
	 */
	public Map<Integer, WB_Normal3d> getKeyedFaceNormals() {
		final Map<Integer, WB_Normal3d> result = new FastMap<Integer, WB_Normal3d>(
				numberOfFaces());
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result.put(f.key(), f.getFaceNormal());
		}
		return result;
	}

	/**
	 * Return all face centers.
	 * 
	 * @return array of WB_Point.
	 */
	public WB_Point3d[] getFaceCenters() {
		final WB_Point3d[] result = new WB_Point3d[numberOfFaces()];
		int i = 0;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.getFaceCenter();
			i++;
		}
		return result;
	}

	/**
	 * Return all face centers.
	 * 
	 * @return FastMap of WB_Point.
	 */
	public Map<Integer, WB_Point3d> getKeyedFaceCenters() {
		final Map<Integer, WB_Point3d> result = new FastMap<Integer, WB_Point3d>(
				numberOfFaces());
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result.put(f.key(), f.getFaceCenter());
		}
		return result;
	}

	/**
	 * Return all edge normals.
	 * 
	 * @return array of WB_Normal.
	 */
	public WB_Normal3d[] getEdgeNormals() {
		final WB_Normal3d[] result = new WB_Normal3d[numberOfEdges()];
		int i = 0;
		HE_Edge e;
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = e.getEdgeNormal();
			i++;
		}
		return result;
	}

	/**
	 * Return all edge normals.
	 * 
	 * @return FastMap of WB_Normal.
	 */
	public Map<Integer, WB_Normal3d> getKeyedEdgeNormals() {
		final Map<Integer, WB_Normal3d> result = new FastMap<Integer, WB_Normal3d>(
				numberOfEdges());
		HE_Edge e;
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result.put(e.key(), e.getEdgeNormal());
		}
		return result;
	}

	/**
	 * Return all edge centers.
	 * 
	 * @return array of WB_Point.
	 */
	public WB_Point3d[] getEdgeCenters() {
		final WB_Point3d[] result = new WB_Point3d[numberOfEdges()];
		int i = 0;
		HE_Edge e;
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = e.getEdgeCenter();
			i++;
		}
		return result;
	}

	/**
	 * Return all edge centers.
	 * 
	 * @return FastMap of WB_Point.
	 */
	public Map<Integer, WB_Point3d> getKeyedEdgeCenters() {
		final Map<Integer, WB_Point3d> result = new FastMap<Integer, WB_Point3d>(
				numberOfEdges());
		HE_Edge e;
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result.put(e.key(), e.getEdgeCenter());
		}
		return result;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values 2D array of float. First index is number of vertices, second
	 * index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromFloat(final float[][] values) {
		int i = 0;
		_center.set(0, 0, 0);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;
		}

	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values array of WB_Point.
	 */
	public void setVerticesFromPoint(final WB_Point3d[] values) {
		int i = 0;
		_center.set(0, 0, 0);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i]);
			i++;
		}
		;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values 2D array of double. First index is number of vertices, second
	 * index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromDouble(final double[][] values) {
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;

		}
		;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values 2D array of int. First index is number of vertices, second
	 * index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromInt(final int[][] values) {
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;

		}
		;
	}

	/**
	 * Return the mesh as polygon soup.
	 * 
	 * @return array of WB_polygon
	 * 
	 */

	public WB_ExplicitPolygon[] getPolygons() {
		final WB_ExplicitPolygon[] result = new WB_ExplicitPolygon[numberOfFaces()];
		final Iterator<HE_Face> fItr = fItr();
		HE_Face f;
		int i = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.toPolygon();
			i++;
		}
		return result;
	}

	public List<WB_ExplicitPolygon> getPolygonList() {
		final List<WB_ExplicitPolygon> result = new FastList<WB_ExplicitPolygon>();
		final Iterator<HE_Face> fItr = fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			result.add(f.toPolygon());
		}
		return result;
	}

	public WB_ExplicitSegment[] getSegments() {
		final WB_ExplicitSegment[] result = new WB_ExplicitSegment[numberOfEdges()];
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		int i = 0;
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = new WB_ExplicitSegment(e.getStartVertex(),
					e.getEndVertex(), false);
			i++;
		}
		return result;
	}

	public WB_IndexedSegment[] getIndexedSegments() {
		final WB_IndexedSegment[] result = new WB_IndexedSegment[numberOfEdges()];
		final WB_Point3d[] points = getVerticesAsPoint();
		final FastMap<Integer, Integer> map = new FastMap<Integer, Integer>();
		map.putAll(vertexKeyToIndex());
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		int i = 0;
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = new WB_IndexedSegment(
					map.get(e.getStartVertex().key()), map.get(e.getEndVertex()
							.key()), points);
			i++;
		}
		return result;
	}

	public WB_Frame getFrame() {
		final WB_Frame frame = new WB_Frame(getVerticesAsPoint());
		final FastMap<Integer, Integer> map = new FastMap<Integer, Integer>();
		map.putAll(vertexKeyToIndex());
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			frame.addStrut(map.get(e.getStartVertex().key()),
					map.get(e.getEndVertex().key()));
		}

		return frame;

	}

	// TRANSFORM
	/**
	 * Apply transform to entire mesh.
	 *
	 * @param T WB_Transform to apply

	 * @return self
	 */
	public HE_Mesh transform(final WB_Transform T) {
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			T.apply(vItr.next());
		}
		return this;
	}

	/**
	 * Translate entire mesh.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return self
	 */
	public HE_Mesh move(final double x, final double y, final double z) {
		_center.add(x, y, z);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().add(x, y, z);
		}
		return this;
	}

	/**
	 * Translate entire mesh.
	 *
	 * @param v
	 * @return self
	 */
	public HE_Mesh move(final WB_Point3d v) {
		return move(v.x, v.y, v.z);
	}

	/**
	 * Translate entire mesh to given position.
	 *
	 * @param x
	 * @param y 
	 * @param z
	 * @return self
	 */
	public HE_Mesh moveTo(final double x, final double y, final double z) {
		if (!_centerUpdated) {
			getCenter();
		}
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().add(x - _center.x, y - _center.y, z - _center.z);
		}
		_center.set(x, y, z);
		return this;
	}

	/**
	 * Translate entire mesh to given position.
	 *
	 * @param v the v
	 * @return self
	 */
	public HE_Mesh moveTo(final WB_Point3d v) {
		return moveTo(v.x, v.y, v.z);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p1x x-coordinate of first point on axis
	 * @param p1y y-coordinate of first point on axis
	 * @param p1z z-coordinate of first point on axis
	 * @param p2x x-coordinate of second point on axis
	 * @param p2y y-coordinate of second point on axis
	 * @param p2z z-coordinate of second point on axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point3d(p1x, p1y, p1z),
				new WB_Vector3d(p2x - p1x, p2y - p1y, p2z - p1z));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applyInto(v, v);
		}
		raa.applyInto(_center, _center);
		;
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p1 first point on axis
	 * @param p2 second point on axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Point3d p1,
			final WB_Point3d p2) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, p2.subToVector(p1));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applyInto(v, v);
		}
		raa.applyInto(_center, _center);
		;
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p rotation point
	 * @param a axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Point3d p,
			final WB_Vector3d a) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applyInto(v, v);
		}
		raa.applyInto(_center, _center);
		;
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p rotation point
	 * @param a axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Point3d p,
			final WB_Normal3d a) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applyInto(v, v);
		}
		raa.applyInto(_center, _center);
		;
		return this;
	}

	/**
	 * Scale entire mesh around center point.
	 * 
	 * @param scaleFactorx
	 *            x-coordinate of scale factor
	 * @param scaleFactory
	 *            y-coordinate of scale factor
	 * @param scaleFactorz
	 *            z-coordinate of scale factor
	 * @param c
	 *            center
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactorx, final double scaleFactory,
			final double scaleFactorz, final WB_Point3d c) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(c.x + scaleFactorx * (v.x - c.x), c.y + scaleFactory
					* (v.y - c.y), c.z + scaleFactorz * (v.z - c.z));
		}
		_center.set(c.x + scaleFactorx * (-c.x + _center.x), c.y + scaleFactory
				* (-c.y + _center.y), c.z + scaleFactorz * (-c.z + _center.z));
		;
		return this;
	}

	/**
	 * Scale entire mesh around center point.
	 * 
	 * @param scaleFactor
	 *            scale
	 * @param c
	 *            center
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactor, final WB_Point3d c) {
		return scale(scaleFactor, scaleFactor, scaleFactor, c);
	}

	/**
	 * Scale entire mesh around bodycenter.
	 * 
	 * @param scaleFactorx
	 *            x-coordinate of scale factor
	 * @param scaleFactory
	 *            y-coordinate of scale factor
	 * @param scaleFactorz
	 *            z-coordinate of scale factor
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactorx, final double scaleFactory,
			final double scaleFactorz) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(_center.x + scaleFactorx * (v.x - _center.x), _center.y
					+ scaleFactory * (v.y - _center.y), _center.z
					+ scaleFactorz * (v.z - _center.z));
		}
		;
		return this;
	}

	/**
	 * Scale entire mesh around bodycenter.
	 * 
	 * @param scaleFactor
	 *            scale
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactor) {
		return scale(scaleFactor, scaleFactor, scaleFactor);
	}

	// DERIVED ELEMENTS

	/**
	 * Get the center (average of all vertex positions).
	 * 
	 * @return the center
	 */
	public WB_Point3d getCenter() {
		if (_centerUpdated) {
			return _center;
		} else {
			resetCenter();
			return _center;
		}
	}

	/**
	 * Reset the center to the average of all vertex positions).
	 * 
	 */
	public void resetCenter() {
		_center.set(0, 0, 0);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			_center.add(vItr.next());
		}
		_center.div(numberOfVertices());
		_centerUpdated = true;

	}

	// HELPERS

	/**
	 * Assign face to halfedge loop.
	 *
	 * @param face face
	 * @param halfedge halfedge loop
	 */
	protected static void assignFaceToLoop(final HE_Face face,
			final HE_Halfedge halfedge) {
		HE_Halfedge he = halfedge;
		do {
			he.setFace(face);
			he = he.getNextInFace();
		} while (he != halfedge);
	}

	/**
	 * Cycle halfedges.
	 *
	 * @param halfedges halfedges to cycle
	 * @param cycle HE_CycleOption.NORMAL, HE_CycleOption.REVERSE
	 */
	public static void cycleHalfedges(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {
			for (int j = 0; j < n - 1; j++) {
				he = halfedges.get(j);
				he.setNext(halfedges.get(j + 1));
			}
			he = halfedges.get(n - 1);
			he.setNext(halfedges.get(0));

		}
	}

	/**
	 * Cycle halfedges.
	 *
	 * @param halfedges halfedges to cycle
	 * @param cycle HE_CycleOption.NORMAL, HE_CycleOption.REVERSE
	 */
	public static void cycleHalfedgesReverse(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {
			he = halfedges.get(0);
			he.setNext(halfedges.get(n - 1));
			for (int j = 1; j < n; j++) {
				he = halfedges.get(j);
				he.setNext(halfedges.get(j - 1));
			}
		}
	}

	/**
	 * Collect all unpaired halfedges.
	 *
	 * @return the unpaired halfedges
	 */
	public List<HE_Halfedge> getUnpairedHalfedges() {
		final List<HE_Halfedge> unpairedHalfedges = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair() == null) {
				unpairedHalfedges.add(he);
			}
		}
		return unpairedHalfedges;
	}

	/**
	 * Collect all boundary halfedges.
	 *
	 * @return boundary halfedges
	 */
	public List<HE_Halfedge> getBoundaryHalfedges() {
		final List<HE_Halfedge> boundaryHalfedges = new FastList<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				boundaryHalfedges.add(he);
			}
		}
		return boundaryHalfedges;
	}

	/**
	 * Try to pair all unpaired halfedges.
	 */
	public void pairHalfedges() {
		class VertexInfo {
			FastList<HE_Halfedge>	out;
			FastList<HE_Halfedge>	in;

			VertexInfo() {
				out = new FastList<HE_Halfedge>();
				in = new FastList<HE_Halfedge>();
			}

		}

		final FastMap<Integer, VertexInfo> vertexLists = new FastMap<Integer, VertexInfo>();

		final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
		HE_Vertex v;
		VertexInfo vi;
		// System.out.println("HE_Mesh : collating " + unpairedHalfedges.size()
		// + " unpaired halfedges per vertex.");
		for (final HE_Halfedge he : unpairedHalfedges) {
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();

				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			v = he.getNextInFace().getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);

			}
			vi.in.add(he);
		}
		HE_Halfedge he;
		HE_Halfedge he2;
		HE_Edge e;
		// System.out.println("HE_Mesh : pairing unpaired halfedges per vertex.");
		for (final VertexInfo vInfo : vertexLists.values()) {
			for (int i = 0; i < vInfo.out.size(); i++) {
				he = vInfo.out.get(i);
				if (he.getPair() == null) {
					for (int j = 0; j < vInfo.in.size(); j++) {
						he2 = vInfo.in.get(j);
						if ((he2.getPair() == null)
								&& (he.getVertex() == he2.getNextInFace()
										.getVertex())
								&& (he2.getVertex() == he.getNextInFace()
										.getVertex())) {
							he.setPair(he2);
							e = new HE_Edge();
							e.setHalfedge(he);
							he.setEdge(e);
							he2.setEdge(e);
							add(e);
							break;

						}
					}
				}
			}
		}

	}

	public void pairHalfedges(final List<HE_Halfedge> unpairedHalfedges) {
		class VertexInfo {
			FastList<HE_Halfedge>	out;
			FastList<HE_Halfedge>	in;

			VertexInfo() {
				out = new FastList<HE_Halfedge>();
				in = new FastList<HE_Halfedge>();
			}

		}

		final FastMap<Integer, VertexInfo> vertexLists = new FastMap<Integer, VertexInfo>();
		HE_Vertex v;
		VertexInfo vi;
		System.out.println("HE_Mesh : collating " + unpairedHalfedges.size()
				+ " unpaired halfedges per vertex.");
		for (final HE_Halfedge he : unpairedHalfedges) {
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();

				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			v = he.getNextInFace().getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);

			}
			vi.in.add(he);
		}
		HE_Halfedge he;
		HE_Halfedge he2;
		HE_Edge e;
		System.out.println("HE_Mesh : pairing unpaired halfedges per vertex.");
		for (final VertexInfo vInfo : vertexLists.values()) {
			for (int i = 0; i < vInfo.out.size(); i++) {
				he = vInfo.out.get(i);
				if (he.getPair() == null) {
					for (int j = 0; j < vInfo.in.size(); j++) {
						he2 = vInfo.in.get(j);
						if ((he2.getPair() == null)
								&& (he.getVertex() == he2.getNextInFace()
										.getVertex())
								&& (he2.getVertex() == he.getNextInFace()
										.getVertex())) {
							he.setPair(he2);
							e = new HE_Edge();
							e.setHalfedge(he);
							he.setEdge(e);
							he2.setEdge(e);
							add(e);
							break;

						}
					}
				}
			}
		}
	}

	/**
	 * Cap all remaining unpaired halfedges. Only use after pairHalfedges();
	 */
	public void capHalfedges() {
		final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
		final int nuh = unpairedHalfedges.size();
		final HE_Halfedge[] newHalfedges = new HE_Halfedge[nuh];
		HE_Halfedge he1, he2;
		HE_Edge e;
		for (int i = 0; i < nuh; i++) {
			he1 = unpairedHalfedges.get(i);
			he2 = new HE_Halfedge();
			he2.setVertex(he1.getNextInFace().getVertex());
			he1.setPair(he2);
			newHalfedges[i] = he2;
			add(he2);
			e = new HE_Edge();
			add(e);
			e.setHalfedge(he1);
			he1.setEdge(e);
			he2.setEdge(e);
		}

		for (int i = 0; i < nuh; i++) {
			he1 = newHalfedges[i];
			if (he1.getNextInFace() == null) {
				for (int j = 0; j < nuh; j++) {
					he2 = newHalfedges[j];
					if (he2.getVertex() == he1.getPair().getVertex()) {
						he1.setNext(he2);
						break;
					}
				}
			}
		}
	}

	public void uncapHalfedges() {
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				he.getVertex().setHalfedge(he.getPair());
				he.getEdge().setHalfedge(he.getPair());
				he.getPair().clearPair();
				heItr.remove();
			}

		}

	}

	/**
	 * Cap holes.
	 *
	 * @return all new faces as FastList<HE_Face>
	 */
	public List<HE_Face> capHoles() {
		final List<HE_Face> caps = new FastList<HE_Face>();
		final List<HE_Halfedge> unpairedEdges = getUnpairedHalfedges();
		List<HE_Halfedge> loopedHalfedges;
		HE_Halfedge start;
		HE_Halfedge he;
		HE_Halfedge hen;
		HE_Face nf;
		List<HE_Halfedge> newHalfedges;
		HE_Halfedge phe;
		HE_Halfedge nhe;
		HE_Edge ne;
		while (unpairedEdges.size() > 0) {
			loopedHalfedges = new FastList<HE_Halfedge>();
			start = unpairedEdges.get(0);
			loopedHalfedges.add(start);
			he = start;
			hen = start;
			boolean stuck = false;
			do {
				for (int i = 0; i < unpairedEdges.size(); i++) {
					hen = unpairedEdges.get(i);
					if (hen.getVertex() == he.getNextInFace().getVertex()) {
						if (!loopedHalfedges.contains(hen)) {
							loopedHalfedges.add(hen);
						} else {
							stuck = true;
						}
						break;
					}
				}
				if (hen.getVertex() != he.getNextInFace().getVertex()) {
					stuck = true;
				}
				he = hen;
			} while ((hen.getNextInFace().getVertex() != start.getVertex())
					&& (!stuck));
			unpairedEdges.removeAll(loopedHalfedges);
			nf = new HE_Face();
			add(nf);
			caps.add(nf);
			newHalfedges = new FastList<HE_Halfedge>();
			for (int i = 0; i < loopedHalfedges.size(); i++) {
				phe = loopedHalfedges.get(i);
				nhe = new HE_Halfedge();
				add(nhe);
				newHalfedges.add(nhe);
				nhe.setVertex(phe.getNextInFace().getVertex());
				nhe.setPair(phe);
				nhe.setFace(nf);
				if (nf.getHalfedge() == null) {
					nf.setHalfedge(nhe);
				}
				ne = new HE_Edge();
				add(ne);
				ne.setHalfedge(nhe);
				nhe.setEdge(ne);
				phe.setEdge(ne);

			}
			cycleHalfedgesReverse(newHalfedges);

		}
		return caps;
	}

	/**
	 * Clean all mesh elements not used by any faces.
	 *
	 * @return self
	 */
	public HE_Mesh cleanUnusedElementsByFace() {
		final List<HE_Vertex> cleanedVertices = new FastList<HE_Vertex>();
		final List<HE_Halfedge> cleanedHalfedges = new FastList<HE_Halfedge>();
		final List<HE_Edge> cleanedEdges = new FastList<HE_Edge>();

		HE_Halfedge he;
		HE_Edge e;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				if (!cleanedVertices.contains(he.getVertex())) {
					cleanedVertices.add(he.getVertex());
					he.getVertex().setHalfedge(he);
				}
				if (!cleanedHalfedges.contains(he)) {
					cleanedHalfedges.add(he);
				}
				he.clearEdge();
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		final int n = cleanedHalfedges.size();
		for (int i = 0; i < n; i++) {
			he = cleanedHalfedges.get(i);
			if (!cleanedHalfedges.contains(he.getPair())) {
				he.clearPair();
				he.getVertex().setHalfedge(he);
			} else {
				if (he.getEdge() == null) {
					e = new HE_Edge();
					e.setHalfedge(he);
					he.setEdge(e);
					he.getPair().setEdge(e);
					cleanedEdges.add(e);
				}
			}
		}
		replaceVertices(cleanedVertices);
		replaceHalfedges(cleanedHalfedges);
		replaceEdges(cleanedEdges);
		return this;
	}

	// MESH OPERATIONS

	/**
	 * Reverse all faces. Flips normals.
	 * 
	 */
	public HE_Mesh flipAllFaces() {
		HE_Edge edge;
		HE_Halfedge he1;
		HE_Halfedge he2;
		HE_Vertex tmp;
		HE_Halfedge[] prevHe;
		HE_Halfedge he;
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			edge = eItr.next();
			he1 = edge.getHalfedge();
			he2 = he1.getPair();
			tmp = he1.getVertex();
			he1.setVertex(he2.getVertex());
			he2.setVertex(tmp);
			he1.getVertex().setHalfedge(he1);
			he2.getVertex().setHalfedge(he2);
		}
		prevHe = new HE_Halfedge[numberOfHalfedges()];
		int i = 0;
		Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			i++;
		}
		i = 0;
		heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			he.setNext(prevHe[i]);
			i++;

		}
		return this;
	}

	/**
	 * Collapse edge. End vertices are averaged.
	 * 
	 * @param e
	 *            edge to collapse
	 */
	public void collapseEdge(final HE_Edge e) {
		if (contains(e)) {
			final HE_Halfedge he = e.getHalfedge();
			final HE_Vertex v = he.getVertex();
			final HE_Halfedge hePair = e.getHalfedge().getPair();
			final HE_Vertex vp = hePair.getVertex();
			vp.add(v).div(2);
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			for (int i = 0; i < tmp.size(); i++) {
				tmp.get(i).setVertex(vp);
			}

			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (he.getFace() != null) {
				he.getFace().setHalfedge(hen);
			}
			if (hePair.getFace() != null) {
				hePair.getFace().setHalfedge(hePairn);
			}

			hep.setNext(hen);
			hePairp.setNext(hePairn);
			remove(he);
			remove(hePair);
			remove(e);
			remove(v);
			if (hen.getNextInFace() == hep) { // if face only contains 2 edges,
				// remove
				// face
				hePairp.setPair(hePairn);
				remove(hePairp.getEdge());
				hePairp.setEdge(hePairn.getEdge());
				remove(hep.getFace());
				remove(hep);
				remove(hen);

			}
		}

	}

	/**
	 * Collapse all zero-length edges.
	 * 
	 */
	public void collapseDegenerateEdges() {
		final FastList<HE_Edge> edgesToRemove = new FastList<HE_Edge>();
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (WB_Epsilon.isZeroSq(WB_Distance.sqDistance(e.getStartVertex(),
					e.getEndVertex()))) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			collapseEdge(edgesToRemove.get(i));
		}
	}

	/**
	 * Delete face and remove all references.
	 *
	 * @param f face to delete
	 */
	public void deleteFace(final HE_Face f) {
		HE_Halfedge he = f.getHalfedge();
		do {
			he.clearFace();
			he = he.getNextInFace();
		} while (he != f.getHalfedge());
		remove(f);
	}

	/**
	 * Delete edge. Adjacent faces are fused.
	 * 
	 * @param e
	 *            edge to delete
	 * @return fused face (or null)
	 */
	public HE_Face deleteEdge(final HE_Edge e) {
		HE_Face f = null;
		final HE_Halfedge he1 = e.getHalfedge();
		final HE_Halfedge he2 = e.getHalfedge().getPair();
		final HE_Halfedge he1n = e.getHalfedge().getNextInFace();
		final HE_Halfedge he2n = e.getHalfedge().getPair().getNextInFace();
		final HE_Halfedge he1p = e.getHalfedge().getPrevInFace();
		final HE_Halfedge he2p = e.getHalfedge().getPair().getPrevInFace();
		he1p.setNext(he2n);
		he2p.setNext(he1n);
		HE_Vertex v = he1.getVertex();
		if (v.getHalfedge() == he1) {
			v.setHalfedge(he1.getNextInVertex());
		}
		v = he2.getVertex();

		if (v.getHalfedge() == he2) {
			v.setHalfedge(he2.getNextInVertex());
		}
		if ((e.getFirstFace() != null) && (e.getSecondFace() != null)) {
			f = new HE_Face();
			add(f);
			f.setHalfedge(he1p);
			HE_Halfedge he = he1p;
			do {
				he.setFace(f);
				he = he.getNextInFace();
			} while (he != he1p);

		}
		if (e.getFirstFace() != null) {
			remove(e.getFirstFace());
		}
		if (e.getSecondFace() != null) {
			remove(e.getSecondFace());
		}
		remove(he1);
		remove(he2);
		remove(e);
		return f;
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param edge edge to split
	 * @param v position of new vertex
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final HE_Edge edge, final WB_Point3d v) {
		final HE_Selection out = new HE_Selection(this);
		final HE_Halfedge he0 = edge.getHalfedge();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex vNew = new HE_Vertex(v);
		final HE_Halfedge he0new = new HE_Halfedge();
		final HE_Halfedge he1new = new HE_Halfedge();
		he0new.setVertex(vNew);
		he1new.setVertex(vNew);
		vNew.setHalfedge(he0new);
		he0new.setNext(he0.getNextInFace());
		he1new.setNext(he1.getNextInFace());
		he0.setNext(he0new);
		he1.setNext(he1new);
		he0.setPair(he1new);
		he0new.setPair(he1);
		final HE_Edge edgeNew = new HE_Edge();
		edgeNew.label = edge.label;
		edgeNew.setHalfedge(he0new);
		he1new.setEdge(edge);
		he1.setEdge(edgeNew);
		he0new.setEdge(edgeNew);
		if (he0.getFace() != null) {
			he0new.setFace(he0.getFace());
		}
		if (he1.getFace() != null) {
			he1new.setFace(he1.getFace());
		}
		vNew.setLabel(1);
		add(vNew);
		add(he0new);
		add(he1new);
		add(edgeNew);
		out.add(vNew);
		out.add(edgeNew);
		return out;
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param key key of edge to split
	 * @param v position of new vertex
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final Integer key, final WB_Point3d v) {
		final HE_Edge edge = getEdgeByKey(key);
		return splitEdge(edge, v);
	}

	/**
	 * Insert vertex in edge.
	 * 
	 * @param edge
	 *            edge to split
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 */
	public void splitEdge(final HE_Edge edge, final double x, final double y,
			final double z) {
		splitEdge(edge, new WB_Point3d(x, y, z));
	}

	/**
	 * Insert vertex in edge.
	 * 
	 * @param key
	 *            key of edge to split
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 */
	public void splitEdge(final Integer key, final double x, final double y,
			final double z) {
		splitEdge(key, new WB_Point3d(x, y, z));
	}

	/**
	 * Split edge in half.
	 *
	 * @param edge edge to split.
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final HE_Edge edge) {
		final WB_Point3d v = edge.getStartVertex().addAndCopy(
				edge.getEndVertex());
		v.mult(0.5);
		return splitEdge(edge, v);
	}

	/**
	 * Split edge in half.
	 *
	 * @param key key of edge to split.
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final Integer key) {
		final HE_Edge edge = getEdgeByKey(key);
		final WB_Point3d v = edge.getStartVertex().addAndCopy(
				edge.getEndVertex());
		v.mult(0.5);
		return splitEdge(edge, v);
	}

	/**
	 * Split edge in two parts.
	 *
	 * @param edge edge to split
	 * @param f fraction of first part (0..1)
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final HE_Edge edge, final double f) {
		final WB_Point3d v = WB_Point3d.interpolate(edge.getStartVertex(),
				edge.getEndVertex(), f);
		return splitEdge(edge, v);
	}

	/**
	 * Split edge in two parts.
	 *
	 * @param key key of edge to split
	 * @param f fraction of first part (0..1)
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final Integer key, final double f) {
		final HE_Edge edge = getEdgeByKey(key);
		return splitEdge(edge, f);
	}

	/**
	 * Split all edges in half.
	 *
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges() {
		final HE_Selection selectionOut = new HE_Selection(this);

		final HE_Edge[] edges = getEdgesAsArray();
		final int n = numberOfEdges();
		for (int i = 0; i < n; i++) {
			selectionOut.union(splitEdge(edges[i], 0.5));
		}
		return selectionOut;
	}

	/**
	 * Split all edges in half, offset the center by a given distance along the edge normal.
	 * 
	 * @param offset
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges(final double offset) {
		final HE_Selection selectionOut = new HE_Selection(this);

		final HE_Edge[] edges = getEdgesAsArray();
		final int n = numberOfEdges();
		for (int i = 0; i < n; i++) {
			final WB_Point3d p = new WB_Point3d(edges[i].getEdgeNormal());
			p.mult(offset).add(edges[i].getEdgeCenter());
			selectionOut.union(splitEdge(edges[i], p));
		}
		return selectionOut;
	}

	/**
	 * Split edge in half.
	 *
	 * @param selection edges to split.
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges(final HE_Selection selection) {
		final HE_Selection selectionOut = new HE_Selection(this);
		selection.collectEdges();
		final Iterator<HE_Edge> eItr = selection.eItr();
		while (eItr.hasNext()) {
			selectionOut.union(splitEdge(eItr.next(), 0.5));
		}
		selection.addEdges(selectionOut.getEdgesAsArray());
		return selectionOut;
	}

	/**
	 * Split edge in half, offset the center by a given distance along the edge normal.
	 * 
	 * @param offset
	 * @param selection edges to split.
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges(final HE_Selection selection,
			final double offset) {
		final HE_Selection selectionOut = new HE_Selection(this);
		selection.collectEdges();
		final Iterator<HE_Edge> eItr = selection.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			final WB_Point3d p = new WB_Point3d(e.getEdgeNormal());
			p.mult(offset).add(e.getEdgeCenter());
			selectionOut.union(splitEdge(e, p));
		}
		selection.addEdges(selectionOut.getEdgesAsArray());
		return selectionOut;
	}

	/**
	 * Split edge in multiple parts.
	 * 
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final HE_Edge edge, final double[] f) {
		final double[] fArray = Arrays.copyOf(f, f.length);
		Arrays.sort(fArray);
		HE_Edge e = edge;
		final HE_Halfedge he0 = edge.getHalfedge();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex v0 = he0.getVertex();
		final HE_Vertex v1 = he1.getVertex();
		HE_Vertex v = new HE_Vertex();
		for (int i = 0; i < f.length; i++) {
			final double fi = fArray[i];
			if ((fi > 0) && (fi < 1)) {
				v = new HE_Vertex(WB_Point3d.interpolate(v0, v1, fi));
				e = (splitEdge(e, v).eItr().next());
			}
		}

	}

	/**
	 * Split edge in multiple parts.
	 * 
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final Integer key, final double[] f) {
		final HE_Edge edge = getEdgeByKey(key);
		splitEdge(edge, f);
	}

	/**
	 * Split edge in multiple parts.
	 * 
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final HE_Edge edge, final float[] f) {
		final float[] fArray = Arrays.copyOf(f, f.length);
		Arrays.sort(fArray);
		HE_Edge e = edge;
		final HE_Halfedge he0 = edge.getHalfedge();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex v0 = he0.getVertex();
		final HE_Vertex v1 = he1.getVertex();
		HE_Vertex v = new HE_Vertex();
		for (int i = 0; i < f.length; i++) {
			final double fi = fArray[i];
			if ((fi > 0) && (fi < 1)) {
				v = new HE_Vertex(WB_Point3d.interpolate(v0, v1, fi));
				e = (splitEdge(e, v).eItr().next());
			}
		}

	}

	/**
	 * Split edge in multiple parts.
	 * 
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final Integer key, final float[] f) {
		final HE_Edge edge = getEdgeByKey(key);
		splitEdge(edge, f);
	}

	/**
	 * Divide edge.
	 * 
	 * @param origE
	 *            edge to divide
	 * @param n
	 *            number of parts
	 */
	public void divideEdge(final HE_Edge origE, final int n) {
		if (n > 1) {
			final double[] f = new double[n - 1];
			final double in = 1.0 / n;
			for (int i = 0; i < n - 1; i++) {
				f[i] = (i + 1) * in;
			}
			splitEdge(origE, f);
		}
	}

	/**
	 * Divide edge.
	 * 
	 * @param key
	 *            key of edge to divide
	 * @param n
	 *            number of parts
	 */
	public void divideEdge(final Integer key, final int n) {
		final HE_Edge edge = getEdgeByKey(key);
		divideEdge(edge, n);
	}

	/**
	 * Find halfedge shared by vertex and face.
	 *
	 * @param f face
	 * @param v vertex
	 * @return halfedge
	 */
	private HE_Halfedge findHalfedge(final HE_Face f, final HE_Vertex v) {
		HE_Halfedge he = f.getHalfedge();
		do {
			if (he.getVertex() == v) {
				return he;
			}
			he = he.getNextInFace();
		} while (he != f.getHalfedge());
		return null;
	}

	/**
	 * Divide face along two vertices.
	 *
	 * @param face face to divide
	 * @param vi first vertex
	 * @param vj second vertex
	 * @return new face and edge
	 */
	public HE_Selection splitFace(final HE_Face face, final HE_Vertex vi,
			final HE_Vertex vj) {
		final HE_Selection out = new HE_Selection(this);
		final HE_Halfedge hei = findHalfedge(face, vi);
		final HE_Halfedge hej = findHalfedge(face, vj);
		HE_Halfedge heiPrev;
		HE_Halfedge hejPrev;
		HE_Halfedge he0new;
		HE_Halfedge he1new;
		HE_Edge edgeNew;
		HE_Face faceNew;
		HE_Halfedge he;
		if ((hei.getNextInFace() != hej) || (hei.getPrevInFace() != hej)) {
			heiPrev = hei.getPrevInFace();
			hejPrev = hej.getPrevInFace();
			he0new = new HE_Halfedge();
			he1new = new HE_Halfedge();
			he0new.setVertex(vj);
			he1new.setVertex(vi);
			he0new.setNext(hei);
			he1new.setNext(hej);
			heiPrev.setNext(he1new);
			hejPrev.setNext(he0new);
			he0new.setPair(he1new);
			edgeNew = new HE_Edge();
			edgeNew.setHalfedge(he0new);
			he0new.setEdge(edgeNew);
			he1new.setEdge(edgeNew);
			he0new.setFace(face);
			faceNew = new HE_Face();
			face.setHalfedge(hei);
			faceNew.setHalfedge(hej);
			faceNew.label = face.label;
			assignFaceToLoop(faceNew, hej);
			add(he0new);
			add(he1new);
			add(edgeNew);
			add(faceNew);
			out.add(edgeNew);
			out.add(faceNew);
			he = face.getHalfedge();
			do {
				he = he.getNextInFace();
			} while (he != face.getHalfedge());
			return out;
		}
		return null;
	}

	/**
	 * Divide face along two vertices.
	 *
	 * @param fkey key of face
	 * @param vkeyi key of first vertex
	 * @param vkeyj key of second vertex
	 * @return new face and edge
	 */
	public HE_Selection splitFace(final Integer fkey, final Integer vkeyi,
			final Integer vkeyj) {
		return splitFace(getFaceByKey(fkey), getVertexByKey(vkeyi),
				getVertexByKey(vkeyj));
	}

	/**
	 * Tri split face.
	 *
	 * @param face face
	 * @param v new vertex
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection triSplitFace(final HE_Face face, final WB_Point3d v) {
		HE_Halfedge he = face.getHalfedge();
		final HE_Vertex vi = new HE_Vertex(v);
		vi.setLabel(2);
		final HE_Selection out = new HE_Selection(this);
		int c = 0;
		boolean onEdge = false;
		do {
			c++;
			final WB_Plane P = new WB_Plane(he.getHalfedgeCenter(),
					he.getHalfedgeNormal());
			final double d = WB_Distance.distance(v, P);
			if (WB_Epsilon.isZero(d)) {
				onEdge = true;
				break;
			}
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (!onEdge) {
			add(vi);

			final HE_Halfedge[] he0 = new HE_Halfedge[c];
			final HE_Halfedge[] he1 = new HE_Halfedge[c];
			final HE_Halfedge[] he2 = new HE_Halfedge[c];
			c = 0;
			do {
				HE_Face f;
				if (c == 0) {
					f = face;
				} else {
					f = new HE_Face();
					f.label = face.label;
					add(f);
					out.add(f);
				}
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = new HE_Halfedge();
				he2[c] = new HE_Halfedge();
				add(he1[c]);
				add(he2[c]);
				he1[c].setVertex(he.getNextInFace().getVertex());
				he2[c].setVertex(vi);
				he1[c].setNext(he2[c]);
				he2[c].setNext(he);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace();
			} while (he != face.getHalfedge());
			vi.setHalfedge(he2[0]);
			for (int i = 0; i < c; i++) {
				he0[i].setNext(he1[i]);
				he1[i].setPair(he2[i == c - 1 ? 0 : i + 1]);
				final HE_Edge e = new HE_Edge();
				add(e);
				e.setHalfedge(he1[i]);
				he1[i].setEdge(e);
				he1[i].getPair().setEdge(e);
			}
			out.add(vi);
			return out;
		}
		return null;

	}

	/**
	 * Tri split face.
	 *
	 * @param face face
	 * @param x x-coordinate of new vertex
	 * @param y y-coordinate of new vertex
	 * @param z z-coordinate of new vertex
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFace(final HE_Face face, final double x,
			final double y, final double z) {
		return triSplitFace(face, new WB_Point3d(x, y, z));
	}

	/**
	 * Tri split face.
	 *
	 * @param face face
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFace(final HE_Face face) {
		return triSplitFace(face, face.getFaceCenter());
	}

	/**
	 * Tri split face with offset along face normal.
	 *
	 * @param face face
	 * @param d offset along face normal
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFace(final HE_Face face, final double d) {
		return triSplitFace(face,
				face.getFaceCenter().add(face.getFaceNormal(), d));
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param d offset along face normal
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFaces(final double d) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = getFacesAsArray();
		final int n = numberOfFaces();
		for (int i = 0; i < n; i++) {
			selectionOut.union(triSplitFace(faces[i], d));

		}
		return selectionOut;
	}

	/**
	 * Tri split faces.
	 *
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFaces() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = getFacesAsArray();
		final int n = numberOfFaces();
		for (int i = 0; i < n; i++) {
			selectionOut.union(triSplitFace(faces[i]));

		}
		return selectionOut;
	}

	/**
	 * Tri split faces.
	 *
	 * @param selection face selection to split
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFaces(final HE_Selection selection) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = selection.getFacesAsArray();
		final int n = selection.numberOfFaces();
		for (int i = 0; i < n; i++) {

			selectionOut.union(triSplitFace(faces[i]));

		}
		selection.union(selectionOut);
		return selectionOut;
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param d offset along face normal
	 * @param selection face selection to split
	 * @return  selection of new faces and new vertex
	 */
	public HE_Selection triSplitFaces(final HE_Selection selection,
			final double d) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = selection.getFacesAsArray();
		final int n = selection.numberOfFaces();
		for (int i = 0; i < n; i++) {

			selectionOut.union(triSplitFace(faces[i], d));

		}
		selection.union(selectionOut);
		return selectionOut;
	}

	/**
	 * Split face by connecting all face vertices with new vertex.
	 *
	 * @param key key of face
	 * @param v position of new vertex
	 * @return selection of new faces and new vertex
	 */

	public HE_Selection triSplitFace(final Integer key, final WB_Point3d v) {
		return triSplitFace(getFaceByKey(key), v);
	}

	/**
	 * Split face by connecting all face vertices with new vertex.
	 *
	 * @param key key of face
	 * @param x x-coordinate of new vertex
	 * @param y y-coordinate of new vertex
	 * @param z z-coordinate of new vertex
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection triSplitFace(final Integer key, final double x,
			final double y, final double z) {
		return triSplitFace(getFaceByKey(key), new WB_Point3d(x, y, z));
	}

	/**
	 * Quad split faces.
	 *
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection quadSplitFaces() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = numberOfFaces();
		final WB_Point3d[] faceCenters = new WB_Point3d[n];
		final int[] faceOrders = new int[n];
		HE_Face f;
		int i = 0;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceCenters[i] = f.getFaceCenter();
			faceOrders[i] = f.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		for (i = 0; i < n; i++) {
			f = faces[i];
			vi = new HE_Vertex(faceCenters[i]);
			vi.setLabel(2);
			add(vi);
			selectionOut.add(vi);
			HE_Halfedge startHE = f.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;

			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {
				HE_Face fc;
				if (c == 0) {
					fc = f;
				} else {
					fc = new HE_Face();
					fc.label = f.label;
					add(fc);
				}
				he0[c] = he;
				he.setFace(fc);
				fc.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				he3[c] = new HE_Halfedge();
				add(he2[c]);
				add(he3[c]);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he3[c].setVertex(vi);
				he2[c].setNext(he3[c]);
				he3[c].setNext(he);
				he1[c].setFace(fc);
				he2[c].setFace(fc);
				he3[c].setFace(fc);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			vi.setHalfedge(he3[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
			}
		}
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel selection to split
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection quadSplitFaces(final HE_Selection sel) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = sel.numberOfFaces();
		final WB_Point3d[] faceCenters = new WB_Point3d[n];
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = sel.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceCenters[i] = face.getFaceCenter();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(sel.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
		final HE_Face[] faces = sel.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			final HE_Vertex vi = new HE_Vertex(faceCenters[i]);
			add(vi);
			vi.setLabel(2);
			selectionOut.add(vi);
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;

			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {
				HE_Face f;
				if (c == 0) {
					f = face;
				} else {
					f = new HE_Face();
					add(f);
					f.label = face.label;
					sel.add(f);
				}
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				he3[c] = new HE_Halfedge();
				add(he2[c]);
				add(he3[c]);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he3[c].setVertex(vi);

				he2[c].setNext(he3[c]);
				he3[c].setNext(he);
				he1[c].setFace(f);
				he2[c].setFace(f);
				he3[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			vi.setHalfedge(he3[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
			}
		}
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection hybridSplitFaces() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = numberOfFaces();
		final WB_Point3d[] faceCenters = new WB_Point3d[n];
		final int[] faceOrders = new int[n];
		HE_Face f;
		int i = 0;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceCenters[i] = f.getFaceCenter();
			faceOrders[i] = f.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		for (i = 0; i < n; i++) {
			f = faces[i];
			if (f.getFaceOrder() == 3) {
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {

					final HE_Face fn = new HE_Face();
					fn.label = f.label;
					add(fn);
					he0[c] = he;
					he.setFace(fn);
					fn.setHalfedge(he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();
					add(he2[c]);
					add(hec[c]);
					hec[c].setVertex(he.getVertex());
					hec[c].setPair(he2[c]);
					hec[c].setFace(f);
					final HE_Edge e = new HE_Edge();
					add(e);
					e.setHalfedge(hec[c]);
					hec[c].setEdge(e);
					he2[c].setEdge(e);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
					he2[c].setNext(he0[c]);
					he1[c].setFace(fn);
					he2[c].setFace(fn);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				f.setHalfedge(hec[0]);
				for (int j = 0; j < c; j++) {
					he1[j].setNext(he2[j]);
					hec[j].setNext(hec[(j + 1) % c]);
				}

			}

			else if (f.getFaceOrder() > 3) {
				vi = new HE_Vertex(faceCenters[i]);
				vi.setLabel(2);
				add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;

				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					} else {
						fc = new HE_Face();
						fc.label = f.label;
						add(fc);
					}
					he0[c] = he;
					he.setFace(fc);
					fc.setHalfedge(he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					add(he2[c]);
					add(he3[c]);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
					he3[c].setVertex(vi);
					he2[c].setNext(he3[c]);
					he3[c].setNext(he);
					he1[c].setFace(fc);
					he2[c].setFace(fc);
					he3[c].setFace(fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				vi.setHalfedge(he3[0]);
				for (int j = 0; j < c; j++) {
					he1[j].setNext(he2[j]);
				}
			}
		}
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection hybridSplitFaces(final HE_Selection sel) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = sel.numberOfFaces();
		final WB_Point3d[] faceCenters = new WB_Point3d[n];
		final int[] faceOrders = new int[n];
		HE_Face f;
		int i = 0;
		final Iterator<HE_Face> fItr = sel.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceCenters[i] = f.getFaceCenter();
			faceOrders[i] = f.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(sel.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = sel.getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		for (i = 0; i < n; i++) {
			f = faces[i];
			if (f.getFaceOrder() == 3) {
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {

					final HE_Face fn = new HE_Face();
					fn.label = f.label;
					add(fn);
					sel.add(fn);
					he0[c] = he;
					he.setFace(fn);
					fn.setHalfedge(he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();
					add(he2[c]);
					add(hec[c]);
					hec[c].setVertex(he.getVertex());
					hec[c].setPair(he2[c]);
					hec[c].setFace(f);
					final HE_Edge e = new HE_Edge();
					add(e);
					e.setHalfedge(hec[c]);
					hec[c].setEdge(e);
					he2[c].setEdge(e);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
					he2[c].setNext(he0[c]);
					he1[c].setFace(fn);
					he2[c].setFace(fn);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				f.setHalfedge(hec[0]);
				for (int j = 0; j < c; j++) {
					he1[j].setNext(he2[j]);
					hec[j].setNext(hec[(j + 1) % c]);
				}

			}

			else if (f.getFaceOrder() > 3) {
				vi = new HE_Vertex(faceCenters[i]);
				vi.setLabel(2);
				add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;

				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					} else {
						fc = new HE_Face();
						fc.label = f.label;
						add(fc);
						sel.add(fc);
					}
					he0[c] = he;
					he.setFace(fc);
					fc.setHalfedge(he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					add(he2[c]);
					add(he3[c]);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
					he3[c].setVertex(vi);
					he2[c].setNext(he3[c]);
					he3[c].setNext(he);
					he1[c].setFace(fc);
					he2[c].setFace(fc);
					he3[c].setFace(fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				vi.setHalfedge(he3[0]);
				for (int j = 0; j < c; j++) {
					he1[j].setNext(he2[j]);
				}
			}
		}
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Mid split faces.
	 *
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection midSplitFaces() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = numberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		int i = 0;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {

				final HE_Face f = new HE_Face();
				f.label = face.label;
				add(f);
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				add(he2[c]);
				add(hec[c]);
				hec[c].setVertex(he.getVertex());
				hec[c].setPair(he2[c]);
				hec[c].setFace(face);
				final HE_Edge e = new HE_Edge();
				add(e);
				e.setHalfedge(hec[c]);
				hec[c].setEdge(e);
				he2[c].setEdge(e);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he2[c].setNext(he0[c]);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			face.setHalfedge(hec[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
				hec[j].setNext(hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}
	
	/**
	 * Mid split faces.
	 *
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection midSplitFacesHole() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = numberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		int i = 0;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {

				final HE_Face f = new HE_Face();
				f.label = face.label;
				add(f);
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				add(he2[c]);
				add(hec[c]);
				hec[c].setVertex(he.getVertex());
				hec[c].setPair(he2[c]);
				hec[c].setFace(face);
				final HE_Edge e = new HE_Edge();
				add(e);
				e.setHalfedge(hec[c]);
				hec[c].setEdge(e);
				he2[c].setEdge(e);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he2[c].setNext(he0[c]);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			face.setHalfedge(hec[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
				hec[j].setNext(hec[(j + 1) % c]);
			}
			deleteFace(face);
		}
		return selectionOut;
	}

	/**
	 * Mid split selected faces.
	 *
	 * @param selection selection to split
	 * @return  selection of new faces and new vertices
	 */
	public HE_Selection midSplitFaces(final HE_Selection selection) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = selection.numberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = selection.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(selection.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
		final HE_Face[] faces = selection.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {

				final HE_Face f = new HE_Face();
				add(f);
				f.label = face.label;
				selection.add(f);
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				add(he2[c]);
				add(hec[c]);
				hec[c].setVertex(he.getVertex());
				hec[c].setPair(he2[c]);
				hec[c].setFace(face);
				final HE_Edge e = new HE_Edge();
				add(e);
				e.setHalfedge(hec[c]);
				hec[c].setEdge(e);
				he2[c].setEdge(e);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he2[c].setNext(he0[c]);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			face.setHalfedge(hec[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
				hec[j].setNext(hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}

	public HE_Selection midSplitFacesHole(final HE_Selection selection) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = selection.numberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = selection.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(selection.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdges();
		selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
		final HE_Face[] faces = selection.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {

				final HE_Face f = new HE_Face();
				add(f);
				f.label = face.label;
				selection.add(f);
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				add(he2[c]);
				add(hec[c]);
				hec[c].setVertex(he.getVertex());
				hec[c].setPair(he2[c]);
				hec[c].setFace(face);
				final HE_Edge e = new HE_Edge();
				add(e);
				e.setHalfedge(hec[c]);
				hec[c].setEdge(e);
				he2[c].setEdge(e);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he2[c].setNext(he0[c]);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			face.setHalfedge(hec[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
				hec[j].setNext(hec[(j + 1) % c]);
			}
			deleteFace(face);
		}
		return selectionOut;
	}
	
	/**
	 * Triangulate all concave faces.
	 * 
	 */
	public void triangulateConcaveFaces() {
		final HE_Face[] f = getFacesAsArray();
		final int n = numberOfFaces();
		for (int i = 0; i < n; i++) {
			if (f[i].getFaceType() == WB_PolygonType2D.CONCAVE) {
				triangulate(f[i].key());
			}
		}
	}

	/**
	 * Triangulate face if concave.
	 * 
	 * @param key
	 *            key of face
	 */

	public void triangulateConcaveFace(final Integer key) {
		triangulateConcaveFace(getFaceByKey(key));
	}

	/**
	 * Triangulate face if concave.
	 * 
	 * @param face
	 *            key of face
	 */

	public void triangulateConcaveFace(final HE_Face face) {

		if (face.getFaceType() == WB_PolygonType2D.CONCAVE) {
			triangulate(face);
		}

	}

	/**
	 * Expand vertex to new edge.
	 *
	 * @param v vertex to expand
	 * @param f1 first face
	 * @param f2 second face
	 * @param vn position of new vertex
	 */
	public void expandVertexToEdge(final HE_Vertex v, final HE_Face f1,
			final HE_Face f2, final WB_Point3d vn) {
		HE_Halfedge he = v.getHalfedge();
		HE_Halfedge he1 = new HE_Halfedge();
		HE_Halfedge he2 = new HE_Halfedge();
		do {
			if (he.getFace() == f1) {
				he1 = he;
			}
			if (he.getFace() == f2) {
				he2 = he;
			}
			he = he.getNextInVertex();

		} while (he != v.getHalfedge());
		final HE_Vertex vNew = new HE_Vertex(vn);
		vNew.setHalfedge(he1);
		add(vNew);
		he = he1;
		do {
			he.setVertex(vNew);

			he = he.getNextInVertex();

		} while (he != he2);

		final HE_Halfedge he1p = he1.getPrevInFace();
		final HE_Halfedge he2p = he2.getPrevInFace();
		final HE_Halfedge he1new = new HE_Halfedge();
		final HE_Halfedge he2new = new HE_Halfedge();
		add(he1new);
		add(he2new);
		he1new.setVertex(v);
		he2new.setVertex(vNew);
		he1p.setNext(he1new);
		he1new.setNext(he1);
		he2p.setNext(he2new);
		he2new.setNext(he2);
		he1new.setPair(he2new);
		he1new.setFace(f1);
		he2new.setFace(f2);
		final HE_Edge eNew = new HE_Edge();
		add(eNew);
		eNew.setHalfedge(he1new);
		he1new.setEdge(eNew);
		he2new.setEdge(eNew);

	}

	/**
	 * Check consistency of datastructure.
	 *
	 * @param verbose true: print to console, HE.SILENT: no output
	 * @param force true: full scan, HE.BREAK: stop on first error
	 * @return true or false
	 */
	public boolean validate(final boolean verbose, final boolean force) {
		boolean result = true;
		if (verbose == true) {
			System.out.println("Checking face (" + numberOfFaces()
					+ ") properties");
		}

		HE_Face face;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in face " + face.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}

			} else {
				if (!contains(face.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in face "
								+ face.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				} else {
					if (face.getHalfedge().getFace() != null) {
						if (face.getHalfedge().getFace() != face) {
							if (verbose == true) {
								System.out.println("Wrong reference in face "
										+ face.key() + ".");
							}
							if (force == true) {
								result = false;
							} else {
								return false;
							}
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking vertex (" + numberOfVertices()
					+ ") properties");
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in vertex  " + v.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(v.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in vertex  "
								+ v.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (v.getHalfedge().getVertex() != null) {
					if (v.getHalfedge().getVertex() != v) {
						if (verbose == true) {
							System.out.println("Wrong reference in vertex  "
									+ v.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking edge (" + numberOfEdges()
					+ ") properties");
		}
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getHalfedge() == null) {
				if (verbose == true) {
					System.out.println("Null reference in edge  " + e.key()
							+ ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(e.getHalfedge())) {
					if (verbose == true) {
						System.out.println("External reference in edge  "
								+ e.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (e.getHalfedge().getEdge() != null) {
					if (e.getHalfedge().getEdge() != e) {
						if (verbose == true) {
							System.out.println("Wrong reference in edge  "
									+ e.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
		}

		if (verbose == true) {
			System.out.println("Checking half edge (" + numberOfHalfedges()
					+ ") properties");
		}
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getNextInFace() == null) {
				if (verbose == true) {
					System.out.println("Null reference (next) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(he.getNextInFace())) {
					if (verbose == true) {
						System.out
								.println("External reference (next) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if ((he.getFace() != null)
						&& (he.getNextInFace().getFace() != null)) {
					if (he.getFace() != he.getNextInFace().getFace()) {
						if (verbose == true) {
							System.out
									.println("Incosistent reference (face) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
			if (he.getPair() == null) {
				if (verbose == true) {
					System.out.println("Null reference (pair) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(he.getPair())) {
					if (verbose == true) {
						System.out
								.println("External reference (pair) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
				if (he.getPair().getPair() == null) {
					if (verbose == true) {
						System.out
								.println("No pair reference back to half edge  "
										+ he.key() + ".");
					}
				} else {
					if (he.getPair().getPair() != he) {
						if (verbose == true) {
							System.out
									.println("Wrong pair reference back to half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
				if ((he.getEdge() != null) && (he.getPair().getEdge() != null)) {
					if (he.getEdge() != he.getPair().getEdge()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (edge) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}

					}
				}
			}

			if ((he.getNextInFace() != null) && (he.getPair() != null)) {
				if ((he.getNextInFace().getVertex() != null)
						&& (he.getPair().getVertex() != null)) {
					if (he.getNextInFace().getVertex() != he.getPair()
							.getVertex()) {
						if (verbose == true) {
							System.out
									.println("Inconsistent reference (pair)/(next) in half edge  "
											+ he.key() + ".");
						}
						if (force == true) {
							result = false;
						} else {
							return false;
						}
					}
				}
			}
			if (he.getFace() == null) {

				if (verbose == true) {
					System.out.println("Null reference (face) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}

			} else {
				if (!contains(he.getFace())) {
					if (verbose == true) {
						System.out
								.println("External reference (face) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
			if (he.getVertex() == null) {
				if (verbose == true) {
					System.out.println("Null reference (vert) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(he.getVertex())) {
					if (verbose == true) {
						System.out
								.println("External reference (vert) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
			if (he.getEdge() == null) {
				if (verbose == true) {
					System.out.println("Null reference (edge) in half edge  "
							+ he.key() + ".");
				}
				if (force == true) {
					result = false;
				} else {
					return false;
				}
			} else {
				if (!contains(he.getEdge())) {
					if (verbose == true) {
						System.out
								.println("External reference (edge) in half edge  "
										+ he.key() + ".");
					}
					if (force == true) {
						result = false;
					} else {
						return false;
					}
				}
			}
		}
		if (verbose == true) {
			System.out.println("Validation complete!");
		}
		return result;
	}

	/**
	 * Check if point lies inside mesh.
	 * 
	 * @param p
	 *            point to check
	 * @param isConvex
	 *            do fast check, convex meshes only
	 * @return true or false
	 */
	public boolean contains(final WB_Point3d p, final boolean isConvex) {
		final WB_Point3d dir = new WB_Point3d(Math.random() - 0.5,
				Math.random() - 0.5, Math.random() - 0.5);
		final WB_Ray R = new WB_Ray(p, dir);
		int c = 0;
		WB_Plane P;
		WB_IntersectionResult lpi;
		HE_Face face;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			P = face.toPlane();
			if (isConvex) {
				if (P.classifyPointToPlane(p) == WB_ClassifyPointToPlane.POINT_BEHIND_PLANE) {
					return false;
				}
			} else {

				lpi = WB_Intersection.getIntersection(R, P);
				if (lpi.intersection) {
					if (pointIsInFace((WB_Point3d) lpi.object, face)) {
						if (!HE_Mesh.pointIsStrictlyInFace(
								(WB_Point3d) lpi.object, face)) {
							return contains(p, isConvex);
						}
						c++;
					}
				}
			}
		}

		return (isConvex) ? true : (c % 2 == 1);
	}

	/**
	 * Check if point lies inside or on edge of face.
	 *
	 * @param p point
	 * @param f the f
	 * @return true/false
	 */
	public static boolean pointIsInFace(final WB_Point3d p, final HE_Face f) {
		return WB_Epsilon.isZeroSq(WB_Distance.sqDistance(p,
				WB_Intersection.closestPoint(p, f.toPolygon())));
	}

	/**
	 * Check if point lies strictly inside face.
	 *
	 * @param p point
	 * @param f the f
	 * @return true/false
	 */
	public static boolean pointIsStrictlyInFace(final WB_Point3d p,
			final HE_Face f) {
		final WB_ExplicitPolygon poly = f.toPolygon();
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		if (!WB_Epsilon.isZeroSq(WB_Distance.sqDistance(p,
				WB_Intersection.closestPoint(p, tris)))) {
			return false;
		}
		if (WB_Epsilon.isZeroSq(WB_Distance.sqDistance(p,
				WB_Intersection.closestPointOnPeriphery(p, poly, tris)))) {
			return false;
		}
		return true;
	}

	public void fitInAABB(final WB_AABB AABB) {
		final WB_AABB self = getAABB();
		move(AABB.getMin().subToVector(self.getMin()));

		scale(AABB.getWidth() / self.getWidth(),
				AABB.getHeight() / self.getHeight(),
				AABB.getDepth() / self.getDepth(), AABB.getMin());
	}

	public void fitInAABBConstrained(final WB_AABB AABB) {
		final WB_AABB self = getAABB();
		move(AABB.getCenter().subToVector(self.getCenter()));
		double f = Math.min(AABB.getWidth() / self.getWidth(), AABB.getHeight()
				/ self.getHeight());
		f = Math.min(f, AABB.getDepth() / self.getDepth());
		scale(f, AABB.getCenter());
	}

	/**
	 * Delete face and remove all references.
	 *
	 * @param faces faces to delete
	 */
	public void delete(final HE_Selection faces) {
		HE_Face f;
		final Iterator<HE_Face> fItr = faces.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			remove(f);
		}
		cleanUnusedElementsByFace();
		capHalfedges();
	}

	/**
	 * Select all faces.
	 */
	public HE_Selection selectAllFaces() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addFaces(getFacesAsArray());
		return _selection;
	}

	/**
	 * Select all faces with given label.
	 */
	public HE_Selection selectFaces(final int label) {
		final HE_Selection _selection = new HE_Selection(this);

		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	public HE_Selection selectFaces(final WB_Vector3d v) {
		final HE_Selection _selection = new HE_Selection(this);
		final WB_Vector3d w = v.get();
		w.normalize();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();

		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getFaceNormal().dot(v) > (1.0 - WB_Epsilon.EPSILON)) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	public HE_Selection selectFaces(final WB_Plane P) {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addFaces(HE_Intersection.getPotentialIntersectedFaces(this,
				P));

		return _selection;
	}

	/**
	 * Select all faces except with given label.
	 */
	public HE_Selection selectOtherFaces(final int label) {
		final HE_Selection _selection = new HE_Selection(this);

		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() != label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 * Select all edges.
	 */
	public HE_Selection selectAllEdges() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addEdges(getEdgesAsArray());
		return _selection;
	}

	/**
	 * Select all halfedges.
	 */
	public HE_Selection selectAllHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addHalfedges(getHalfedgesAsArray());
		return _selection;
	}

	/**
	 * Select all vertices.
	 */
	public HE_Selection selectAllVertices() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addVertices(getVerticesAsArray());
		return _selection;
	}

	/**
	 * Select all vertices with given label.
	 */
	public HE_Selection selectVertices(final int label) {
		final HE_Selection _selection = new HE_Selection(this);

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() == label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 * Select all vertices except with given label.
	 */
	public HE_Selection selectOtherVertices(final int label) {
		final HE_Selection _selection = new HE_Selection(this);

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() != label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 * Select all halfedges on inside of boundary.
	 */
	public HE_Selection selectAllInnerBoundaryHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair().getFace() == null) {
				_selection.add(he);
			}
		}
		return _selection;
	}

	/**
	 * Select all halfedges on outside of boundary.
	 */
	public HE_Selection selectAllOuterBoundaryHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he);
			}
		}
		return _selection;
	}

	/**
	 * Select all edges on boundary.
	 */
	public HE_Selection selectAllBoundaryEdges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he.getEdge());
			}
		}
		return _selection;
	}

	/**
	 * Select all faces on boundary.
	 */
	public HE_Selection selectAllBoundaryFaces() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he.getPair().getFace());
			}
		}
		return _selection;
	}

	/**
	 * Select all vertices on boundary.
	 */
	public HE_Selection selectAllBoundaryVertices() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he.getVertex());
			}
		}
		return _selection;
	}

	/**
	 * Fuse all coplanar faces connected to face. New face can be concave.
	 * 
	 * @param face starting face
	 * @return new face
	 */
	public HE_Face fuseCoplanarFace(final HE_Face face, final double a) {
		List<HE_Face> neighbors;
		FastTable<HE_Face> facesToCheck = new FastTable<HE_Face>();
		final FastTable<HE_Face> newFacesToCheck = new FastTable<HE_Face>();
		facesToCheck.add(face);
		final HE_Selection sel = new HE_Selection(this);
		sel.add(face);
		HE_Face f;
		HE_Face fn;
		int ni = -1;
		int nf = 0;
		double sa = Math.sin(a);
		sa *= sa;
		while (ni < nf) {
			newFacesToCheck.clear();
			for (int i = 0; i < facesToCheck.size(); i++) {
				f = facesToCheck.get(i);
				neighbors = f.getNeighborFaces();
				for (int j = 0; j < neighbors.size(); j++) {
					fn = neighbors.get(j);
					if (!sel.contains(fn)) {
						if (f.getFaceNormal()
								.isParallel(fn.getFaceNormal(), sa)) {

							sel.add(fn);
							newFacesToCheck.add(fn);
						}
					}
				}
			}
			facesToCheck = newFacesToCheck;
			ni = nf;
			nf = sel.numberOfFaces();
		}

		if (sel.numberOfFaces() == 1) {
			return face;
		}
		final List<HE_Halfedge> halfedges = sel.getOuterHalfedgesInside();
		final HE_Face newFace = new HE_Face();
		add(newFace);
		newFace.setHalfedge(halfedges.get(0));
		for (int i = 0; i < halfedges.size(); i++) {
			final HE_Halfedge hei = halfedges.get(i);
			final HE_Halfedge hep = halfedges.get(i).getPair();
			for (int j = 0; j < halfedges.size(); j++) {
				final HE_Halfedge hej = halfedges.get(j);
				if ((i != j) && (hep.getVertex() == hej.getVertex())) {
					hei.setNext(hej);
				}
			}
			hei.setFace(newFace);
			hei.getVertex().setHalfedge(hei);
		}
		removeFaces(sel.getFacesAsArray());
		cleanUnusedElementsByFace();
		return newFace;
	}

	/**
	 * Fuse all planar faces. Can lead to concave faces.
	 * 
	 */
	public void fuseCoplanarFaces() {
		fuseCoplanarFaces(0);

	}

	/**
	 * Fuse all planar faces. Can lead to concave faces.
	 * 
	 */
	public void fuseCoplanarFaces(final double a) {
		final List<HE_Face> faces = this.getFacesAsList();
		for (int i = 0; i < faces.size(); i++) {
			final HE_Face f = faces.get(i);
			if (contains(f)) {
				fuseCoplanarFace(f, a);
			}
		}

	}

	/**
	 * Remove all redundant vertices in straight edges.
	 * 
	 */
	public void removeColinearVertices() {
		final Iterator<HE_Vertex> vItr = vItr();
		HE_Vertex v;
		HE_Halfedge he;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getVertexOrder() == 2) {
				he = v.getHalfedge();

				if (he.getHalfedgeTangent().isParallel(
						he.getNextInVertex().getHalfedgeTangent())) {
					he.getPrevInFace().setNext(he.getNextInFace());
					he.getPair().getPrevInFace()
							.setNext(he.getPair().getNextInFace());
					he.getPair().getNextInFace()
							.setVertex(he.getNextInFace().getVertex());
					if (he.getFace() != null) {
						if (he.getFace().getHalfedge() == he) {
							he.getFace().setHalfedge(he.getNextInFace());
						}
					}
					if (he.getPair().getFace() != null) {
						if (he.getPair().getFace().getHalfedge() == he
								.getPair()) {
							he.getPair().getFace()
									.setHalfedge(he.getPair().getNextInFace());
						}
					}
					vItr.remove();
					remove(he);
					remove(he.getPair());
					remove(he.getEdge());
				}

			}

		}

	}

	public void resetLabels() {
		resetVertexLabels();
		resetFaceLabels();
		resetEdgeLabels();
	}

	public void resetVertexLabels() {
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().setLabel(-1);
		}

	}

	public void resetFaceLabels() {
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			fItr.next().setLabel(-1);
		}

	}

	public void resetEdgeLabels() {
		final Iterator<HE_Edge> eItr = eItr();
		while (eItr.hasNext()) {
			eItr.next().setLabel(-1);
		}

	}

	/**
	 * Label all faces of a selection. 
	 * 
	 * @param sel selection
	 * @param label label to use
	 */
	public void labelFaceSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Face> fItr = sel.fItr();
		while (fItr.hasNext()) {
			fItr.next().label = label;
		}
	}

	/**
	 * Update selection to include all face swith given label
	 * 
	 * @param sel selection to update
	 * @param label label to search
	 */
	public void updateFaceSelection(final HE_Selection sel, final int label) {

		final Iterator<HE_Face> fItr = fItr();
		HE_Face f;
		sel.clear();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.label == label) {
				sel.add(f);
			}
		}
	}

	public void labelEdgeSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Edge> eItr = sel.eItr();
		while (eItr.hasNext()) {
			eItr.next().label = label;
		}
	}

	public void updateEdgeSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Edge> eItr = eItr();
		HE_Edge e;
		sel.clear();
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.label == label) {
				sel.add(e);
			}
		}
	}

	public void labelVertexSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Vertex> vItr = sel.vItr();
		while (vItr.hasNext()) {
			vItr.next().label = label;
		}
	}

	public void updateVertexSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Vertex> vItr = vItr();
		HE_Vertex v;
		sel.clear();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.label == label) {
				sel.add(v);
			}
		}
	}

	/**
	 * Return a KD-tree containing all face centers
	 * 
	 * @return WB_KDTree
	 */
	public WB_KDTree3Dold<Integer> getFaceTree() {
		final WB_KDTree3Dold<Integer> tree = new WB_KDTree3Dold<Integer>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			tree.put(f.getFaceCenter(), f.key());
		}
		return tree;
	}

	/**
	 * Return a KD-tree containing all vertices
	 * 
	 * @return WB_KDTree
	 */
	public WB_KDTree3Dold<Integer> getVertexTree() {
		final WB_KDTree3Dold<Integer> tree = new WB_KDTree3Dold<Integer>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tree.put(v, v.key());
		}
		return tree;
	}

	/**
	 * Return the closest vertex on the mesh.
	 * 
	 * @param p query point
	 * @param vertexTree KD-tree from mesh (from vertexTree())
	 * @return HE_Vertex closest vertex
	 */
	public HE_Vertex getClosestVertex(final WB_Point3d p,
			final WB_KDTree3Dold<Integer> vertexTree) {
		final WB_KDNeighbor<Integer>[] closestVertex = vertexTree
				.getNearestNeighbors(p, 1);
		if (closestVertex.length == 0) {
			return null;
		}
		return getVertexByKey(closestVertex[0].value());

	}

	/**
	 * Return the closest point on the mesh.
	 * 
	 * @param p query point
	 * @param vertexTree KD-tree from mesh (from vertexTree())
	 * @return WB_Point closest point
	 */
	public WB_Point3d getClosestPoint(final WB_Point3d p,
			final WB_KDTree3Dold<Integer> vertexTree) {
		final WB_KDNeighbor<Integer>[] closestVertex = vertexTree
				.getNearestNeighbors(p, 1);
		final HE_Vertex v = getVertexByKey(closestVertex[0].value());
		if (v == null) {
			return null;
		}
		final List<HE_Face> faces = v.getFaceStar();
		double d;
		double dmin = Double.POSITIVE_INFINITY;
		WB_Point3d result = new WB_Point3d();
		for (int i = 0; i < faces.size(); i++) {
			final WB_ExplicitPolygon poly = faces.get(i).toPolygon();
			final WB_Point3d tmp = WB_Intersection.closestPoint(p, poly);
			d = WB_Distance.sqDistance(tmp, p);
			if (d < dmin) {
				dmin = d;
				result = tmp;
			}
		}
		return result;
	}

	/**
	 * Split the closest face in the query point.
	 * 
	 * @param p query point
	 * @param vertexTree KD-tree from mesh (from vertexTree())
	 */
	public void addPointInClosestFace(final WB_Point3d p,
			final WB_KDTree3Dold<Integer> vertexTree) {
		final WB_KDNeighbor<Integer>[] closestVertex = vertexTree
				.getNearestNeighbors(p, 1);
		final HE_Vertex v = getVertexByKey(closestVertex[0].value());
		final List<HE_Face> faces = v.getFaceStar();
		double d;
		double dmin = Double.POSITIVE_INFINITY;
		HE_Face face = new HE_Face();
		for (int i = 0; i < faces.size(); i++) {
			final WB_ExplicitPolygon poly = faces.get(i).toPolygon();
			final WB_Point3d tmp = WB_Intersection.closestPoint(p, poly);
			d = WB_Distance.sqDistance(tmp, p);
			if (d < dmin) {
				dmin = d;
				face = faces.get(i);
				;
			}
		}
		final HE_Vertex nv = triSplitFace(face, p).vItr().next();
		vertexTree.put(nv, nv.key());
	}

	/**
	 * Get all faces shared between two vertices
	 * @param v1
	 * @param v2
	 * 
	 * @return shared faces as FastList<HE_Face>
	 */
	public List<HE_Face> getSharedFaces(final HE_Vertex v1, final HE_Vertex v2) {
		final List<HE_Face> result = v1.getFaceStar();
		final List<HE_Face> compare = v2.getFaceStar();
		final Iterator<HE_Face> it = result.iterator();
		while (it.hasNext()) {
			if (!compare.contains(it.next())) {
				it.remove();
			}
		}

		return result;
	}

	public List<WB_ExplicitPolygon> getBoundaryAsPolygons() {
		final List<WB_ExplicitPolygon> polygons = new FastList<WB_ExplicitPolygon>();
		final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
		final List<HE_Halfedge> loop = new FastList<HE_Halfedge>();
		final List<WB_Point3d> points = new FastList<WB_Point3d>();
		while (halfedges.size() > 0) {

			points.clear();
			loop.clear();
			HE_Halfedge he = halfedges.get(0);
			do {
				loop.add(he);
				points.add(he.getVertex());
				he = he.getNextInFace();
				if (loop.contains(he)) {
					break;
				}
			} while (he != halfedges.get(0));
			polygons.add(new WB_ExplicitPolygon(points));
			halfedges.removeAll(loop);
		}
		return polygons;

	}

	public List<HE_Halfedge> getBoundaryLoopHalfedges() {
		final List<HE_Halfedge> hes = new FastList<HE_Halfedge>();
		final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
		final List<HE_Halfedge> loop = new FastList<HE_Halfedge>();
		while (halfedges.size() > 0) {
			loop.clear();
			HE_Halfedge he = halfedges.get(0);
			hes.add(he);
			do {
				loop.add(he);
				he = he.getNextInFace();
				if (loop.contains(he)) {
					break;
				}
			} while (he != halfedges.get(0));
			halfedges.removeAll(loop);
		}
		return hes;

	}

	/**
	 * Try to identify and correct corner and edge welds. Can occur when combining meshes joined at a single vertex or edge.
	 * Needs two passes to complete.
	 */
	public void resolvePinchPoints() {

		Iterator<HE_Vertex> vItr = vItr();
		Iterator<HE_Halfedge> heItr;
		HE_Vertex v;
		HE_Halfedge he;
		boolean pinchFound;
		final FastList<HE_Halfedge> vHalfedges = new FastList<HE_Halfedge>();
		int run = 0;
		do {
			vItr = vItr();
			pinchFound = false;
			run++;
			// System.out.println("HE_Mesh, trying to resolve pinch points pass "
			// + run + ".");
			while (vItr.hasNext()) {
				v = vItr.next();
				heItr = heItr();
				vHalfedges.clear();
				while (heItr.hasNext()) {
					he = heItr.next();
					if (he.getVertex() == v) {
						vHalfedges.add(he);
					}
				}
				final List<HE_Halfedge> vStar = v.getHalfedgeStar();
				if (vStar.size() != vHalfedges.size()) {
					pinchFound = true;
					final HE_Vertex vc = new HE_Vertex(v);
					add(vc);
					for (int i = 0; i < vStar.size(); i++) {
						vStar.get(i).setVertex(vc);
					}
					vc.setHalfedge(vStar.get(0));
					for (int i = 0; i < vHalfedges.size(); i++) {
						he = vHalfedges.get(i);
						if (he.getVertex() == v) {
							v.setHalfedge(he);
							break;
						}

					}
				}
			}
		} while (pinchFound && run < 10);
	}

	public double getArea() {
		final Iterator<HE_Face> fItr = fItr();
		double A = 0.0;
		while (fItr.hasNext()) {
			A += fItr.next().getFaceArea();
		}
		return A;
	}

	/**
	 * Triangulate face.
	 * 
	 * @param key
	 *            key of face
	 */
	public void triangulate(final Integer key) {
		triangulate(getFaceByKey(key));

	}

	public void triangulate(final HE_Face face) {
		if (face.getFaceOrder() > 3) {
			final List<WB_IndexedTriangle2D> tris = face.triangulate();
			final List<HE_Vertex> vertices = face.getFaceVertices();
			HE_Halfedge he = face.getHalfedge();
			remove(face);
			do {
				he.getPair().clearEdge();
				he.getPair().clearPair();
				remove(he.getEdge());
				remove(he);
				he = he.getNextInFace();
			} while (he != face.getHalfedge());

			for (int i = 0; i < tris.size(); i++) {
				final WB_IndexedTriangle2D tri = tris.get(i);
				final HE_Face f = new HE_Face();
				add(f);
				f.setLabel(face.getLabel());
				final HE_Halfedge he1 = new HE_Halfedge();
				final HE_Halfedge he2 = new HE_Halfedge();
				final HE_Halfedge he3 = new HE_Halfedge();
				he1.setVertex(vertices.get(tri.i1));
				he2.setVertex(vertices.get(tri.i2));
				he3.setVertex(vertices.get(tri.i3));
				he1.getVertex().setHalfedge(he1);
				he2.getVertex().setHalfedge(he2);
				he3.getVertex().setHalfedge(he3);
				he1.setFace(f);
				he2.setFace(f);
				he3.setFace(f);
				he1.setNext(he2);
				he2.setNext(he3);
				he3.setNext(he1);

				f.setHalfedge(he1);
				add(he1);
				add(he2);
				add(he3);
			}

			pairHalfedges();
		}
	}

	private void triangulateNoPairing(final HE_Face face) {
		if (face.getFaceOrder() > 3) {
			final List<WB_IndexedTriangle2D> tris = face.triangulate();
			final List<HE_Vertex> vertices = face.getFaceVertices();
			HE_Halfedge he = face.getHalfedge();
			remove(face);
			do {
				if (he.getPair() != null) {
					he.getPair().clearEdge();
					he.getPair().clearPair();
				}
				if (he.getEdge() != null) {
					remove(he.getEdge());
				}
				remove(he);
				he = he.getNextInFace();
			} while (he != face.getHalfedge());

			for (int i = 0; i < tris.size(); i++) {
				final WB_IndexedTriangle2D tri = tris.get(i);
				final HE_Face f = new HE_Face();
				add(f);
				f.setLabel(face.getLabel());
				final HE_Halfedge he1 = new HE_Halfedge();
				final HE_Halfedge he2 = new HE_Halfedge();
				final HE_Halfedge he3 = new HE_Halfedge();
				he1.setVertex(vertices.get(tri.i1));
				he2.setVertex(vertices.get(tri.i2));
				he3.setVertex(vertices.get(tri.i3));
				he1.getVertex().setHalfedge(he1);
				he2.getVertex().setHalfedge(he2);
				he3.getVertex().setHalfedge(he3);
				he1.setFace(f);
				he2.setFace(f);
				he3.setFace(f);
				he1.setNext(he2);
				he2.setNext(he3);
				he3.setNext(he1);
				f.setHalfedge(he1);
				add(he1);
				add(he2);
				add(he3);
			}
		}
	}

	/**
	 * Triangulate all faces.
	 * 
	 */
	public void triangulate() {
		final HE_Face[] f = getFacesAsArray();
		final int n = numberOfFaces();
		for (int i = 0; i < n; i++) {
			triangulateNoPairing(f[i]);
		}
		pairHalfedges();
	}

	public void triangulate(final HE_Selection sel) {
		final HE_Face[] f = sel.getFacesAsArray();
		final int n = sel.numberOfFaces();
		for (int i = 0; i < n; i++) {
			triangulateNoPairing(f[i]);
		}
		pairHalfedges();
	}
	
	
	public void clean(){
		WB_ExplicitPolygon[] polygons=getPolygons();
		HEC_FromPolygons creator=new HEC_FromPolygons();
		creator.setPolygons(polygons);
		set(creator.create());
	}
	
	public void setData(final String s, final Object o) {
		if (_data == null) {
			_data = new HashMap<String, Object>();
		}
		_data.put(s, o);
	}

	public Object getData(final String s) {
		return _data.get(s);
	}
	
	public void smooth(){
	subdivide(new HES_CatmullClark());	
		
	}
	
	public void smooth(int rep){
		subdivide(new HES_CatmullClark(),rep);	
			
		}
	
	
	public void fixLoops(){
		for(HE_Halfedge he:getHalfedgesAsList()){
		    if(he.getPrevInFace()==null){
		    	HE_Halfedge hen=he.getNextInFace();
		    	while(hen.getNextInFace()!=he){
		    		hen=hen.getNextInFace();
		    	}
		    	hen.setNext(he);
		    	
		    }
		  }
		
		
	
	}
	
}