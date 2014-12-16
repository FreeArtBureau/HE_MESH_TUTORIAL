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

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;
import wblut.geom.WB_VertexType2D;

/**
 * Half-edge element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Halfedge {

	/** Start vertex of halfedge. */
	private HE_Vertex		_vertex;

	/** Halfedge pair. */
	private HE_Halfedge		_pair;

	/** Next halfedge in face. */
	private HE_Halfedge		_next;

	/** Previous halfedge in face. */
	private HE_Halfedge		_prev;

	/** Associated edge. */
	private HE_Edge			_edge;

	/** Associated face. */
	private HE_Face			_face;

	/** Static halfedge key counter. */
	protected static int	_currentKey;

	/** Unique halfedge key. */
	protected final Integer	_key;

	/** General purpose label. */
	protected int			label;

	/**
	 * Instantiates a new HE_Halfedge.
	 */
	public HE_Halfedge() {
		_key = new Integer(_currentKey);
		_currentKey++;
		label = -1;
	}

	/**
	 * Get key.
	 *
	 * @return key
	 */
	public Integer key() {
		return _key;
	}

	public void setLabel(final int lab) {
		label = lab;

	}

	public int getLabel() {
		return label;

	}

	/**
	 * Get previous halfedge in face.
	 *
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInFace() {
		return _prev;
	}

	/**
	 * Get next halfedge in face.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInFace() {
		return _next;
	}

	/**
	 * Get next halfedge in vertex.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInVertex() {
		if (_pair == null) {
			return null;
		}
		return _pair.getNextInFace();
	}

	/**
	 * Get previous halfedge in vertex.
	 *
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInVertex() {
		if (_prev == null) {
			return null;
		}
		return getPrevInFace().getPair();
	}

	/**
	 * Get paired halfedge.
	 *
	 * @return paired halfedge
	 */
	public HE_Halfedge getPair() {
		return _pair;
	}

	/**
	 * Set next halfedge in face.
	 *
	 * @param he next halfedge
	 */
	public void setNext(final HE_Halfedge he) {
		_next = he;
		he.setPrev(this);
	}

	/**
	 * Sets previous halfedge in face, only to be called by setNext.
	 *
	 * @param he next halfedge
	 */
	private void setPrev(final HE_Halfedge he) {
		_prev = he;

	}

	/**
	 * Mutually pair halfedges.
	 *
	 * @param he halfedge to pair
	 */
	public void setPair(final HE_Halfedge he) {
		_pair = he;
		he.setPairInt(this);
	}

	/**
	 * Pair halfedges, only to be called by setPair.
	 *
	 * @param he halfedge to pair
	 */
	private void setPairInt(final HE_Halfedge he) {
		_pair = he;
	}

	/**
	 * Get type of face vertex associated with halfedge.
	 *
	 * @return HE.FLAT, HE.CONVEX, HE.CONCAVE
	 */
	public WB_VertexType2D getHalfedgeType() {

		if (_vertex == null) {
			return null;
		}
		WB_Vector3d v = _vertex.subToVector(getPrevInFace()._vertex);
		v.normalize();
		final WB_Vector3d vn = getNextInFace()._vertex.subToVector(_vertex);
		vn.normalize();
		v = v.cross(vn);
		final WB_Normal3d n;
		if (_face == null) {
			n = _pair._face.getFaceNormal().mult(-1);
		} else {
			n = _face.getFaceNormal();
		}
		final double dot = n.dot(v);

		if (v.isParallel(vn)) {
			return WB_VertexType2D.FLAT;
		} else if (dot > 0) {
			return WB_VertexType2D.CONVEX;
		} else {
			return WB_VertexType2D.CONCAVE;
		}

	}

	/**
	 * Get tangent WB_Vector of halfedge.
	 *
	 * @return tangent
	 */
	public WB_Vector3d getHalfedgeTangent() {
		if (_edge != null) {
			return (_edge.getHalfedge() == this) ? _edge.getEdgeTangent()
					: _edge.getEdgeTangent().multAndCopy(-1);
		}
		if ((_pair != null) && (_vertex != null) && (_pair.getVertex() != null)) {
			final WB_Vector3d v = _pair.getVertex().subToVector(_vertex);
			v.normalize();
			return v;
		}
		return null;
	}

	/**
	 * Get center of halfedge.
	 *
	 * @return center
	 */
	public WB_Point3d getHalfedgeCenter() {
		if (_edge != null) {
			return _edge.getEdgeCenter();
		}
		if ((_next != null) && (_vertex != null) && (_next.getVertex() != null)) {
			return _next.getVertex().addAndCopy(_vertex).mult(0.5);
		}
		return null;
	}

	/**
	 * Get edge of halfedge.
	 *
	 * @return edge
	 */
	public HE_Edge getEdge() {
		return _edge;
	}

	/**
	 * Sets the edge.
	 *
	 * @param edge the new edge
	 */
	public void setEdge(final HE_Edge edge) {
		_edge = edge;
	}

	/**
	 * Get face of halfedge.
	 *
	 * @return face
	 */
	public HE_Face getFace() {
		return _face;
	}

	/**
	 * Sets the face.
	 *
	 * @param face the new face
	 */
	public void setFace(final HE_Face face) {
		if (_face != null) {
			_face._sorted = false;
		}
		_face = face;
		_face._sorted = false;
	}

	/**
	 * Get vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getVertex() {
		return _vertex;
	}

	/**
	 * Sets the vertex.
	 *
	 * @param vertex the new vertex
	 */
	public void setVertex(final HE_Vertex vertex) {
		_vertex = vertex;
	}

	/**
	 * Get end vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getEndVertex() {
		if (_pair != null) {
			return _pair._vertex;
		}
		return _next._vertex;
	}

	/**
	 * Clear next.
	 */
	public void clearNext() {
		if (_next != null) {
			_next.clearPrev();
		}
		_next = null;
	}

	/**
	 * Clear prev, only to be called by clearNext.
	 */
	private void clearPrev() {
		_prev = null;
	}

	/**
	 * Clear mutual pairing.
	 */
	public void clearPair() {
		if (_pair != null) {
			_pair.clearPairInt();
		}
		_pair = null;

	}

	/**
	 * Unilateral clearing of pairing.Only to be called by clearPair.
	 */
	private void clearPairInt() {
		_pair = null;
	}

	/**
	 * Clear edge.
	 */
	public void clearEdge() {
		_edge = null;
	}

	/**
	 * Clear face.
	 */
	public void clearFace() {
		if (_face != null) {
			_face._sorted = false;
		}
		_face = null;

	}

	/**
	 * Clear vertex.
	 */
	public void clearVertex() {
		_vertex = null;
	}

	/**
	 * Get halfedge normal.
	 *
	 * @return in-face normal of face, points inwards
	 */
	public WB_Normal3d getHalfedgeNormal() {
		WB_Normal3d fn;
		if ((getFace() == null) && (getPair() == null)) {
			return null;
		}

		if (getFace() == null) {
			if (getPair().getFace() == null) {
				return null;
			}
			fn = getPair().getFace().getFaceNormal();
		} else {
			fn = getFace().getFaceNormal();
		}
		final HE_Vertex vn = getNextInFace().getVertex();
		final WB_Normal3d _normal = new WB_Normal3d(vn);
		_normal.sub(getVertex());
		_normal.set(fn.cross(_normal));
		_normal.normalize();
		return _normal;

	}

	/**
	 * Get area of faces bounding halfedge.
	 *
	 * @return area
	 */
	public double getHalfedgeArea() {
		if (getEdge() == null) {
			return 0;
		}
		return 0.5 * getEdge().getEdgeArea();
	}

	/**
	 * Get angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getHalfedgeDihedralAngle() {
		if (getEdge() == null) {
			return Double.NaN;
		}
		return getEdge().getDihedralAngle();
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Halfedge key: " + key() + ", belongs to edge "
				+ getEdge().key() + ".";
	}

}
