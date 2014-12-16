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

import java.util.HashMap;

import wblut.core.WB_HasData;
import wblut.geom.WB_Distance;
import wblut.geom.WB_ExplicitSegment;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;

/**
 * Edge element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HE_Edge  implements WB_HasData {

	/** Halfedge associated with this edge. */
	private HE_Halfedge		_halfedge;

	/** Static edge key counter. */
	protected static int	_currentKey;

	/** Unique edge key. */
	protected final Integer	_key;

	/** General purpose label. */
	protected int			label;
	
	private HashMap<String, Object> _data;

	/**
	 * Instantiates a new HE_Edge.
	 */
	public HE_Edge() {
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

	/**
	 * Get edge center.
	 *
	 * @return edge center
	 */

	public WB_Point3d getEdgeCenter() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new NullPointerException("Vertices missing in edge.");
		}
		final WB_Point3d center = getStartVertex().addAndCopy(getEndVertex())
				.mult(0.5);
		return center;

	}

	/**
	 * Return tangent WB_Vector.
	 *
	 * @return tangent
	 */

	public WB_Vector3d getEdgeTangent() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new NullPointerException("Vertices missing in edge.");
		}
		final WB_Vector3d v = getEndVertex().subToVector(getStartVertex());
		v.normalize();
		return v;
	}

	/**
	 * Return edge segment. The semantically lower vertex is set first.
	 *
	 * @return segment
	 */

	public WB_ExplicitSegment toSegment() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new IllegalArgumentException("Vertices missing in edge.");
		}
		if (getStartVertex().smallerThan(getEndVertex())) {
			return new WB_ExplicitSegment(getStartVertex(), getEndVertex());
		} else {
			return new WB_ExplicitSegment(getEndVertex(), getStartVertex());
		}

	}

	public double getLength() {
		return WB_Distance.distance(getStartVertex(), getEndVertex());
	}

	public void setLabel(final int lab) {
		label = lab;

	}

	public int getLabel() {
		return label;

	}

	/**
	 * Get halfedge.
	 *
	 * @return halfedge
	 */

	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge.
	 *
	 * @param halfedge the new halfedge
	 */

	public void setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	/**
	 * Clear halfedge.
	 */

	public void clearHalfedge() {
		_halfedge = null;
	}

	/**
	 * Get first vertex.
	 *
	 * @return first vertex
	 */

	public HE_Vertex getStartVertex() {
		return _halfedge.getVertex();
	}

	/**
	 * Get second vertex.
	 *
	 * @return second vertex
	 */

	public HE_Vertex getEndVertex() {
		return _halfedge.getPair().getVertex();
	}

	/**
	 * Get first face of an edge.
	 *
	 * @return first face
	 */
	public HE_Face getFirstFace() {
		return getHalfedge().getFace();
	}

	/**
	 * Get second face of an edge.
	 *
	 * @return second face
	 */
	public HE_Face getSecondFace() {
		return getHalfedge().getPair().getFace();
	}

	/**
	 * Get edge normal.
	 *
	 * @return edge normal
	 */
	public WB_Normal3d getEdgeNormal() {

		if ((getFirstFace() == null) && (getSecondFace() == null)) {
			return null;
		}
		final WB_Normal3d n1 = (getFirstFace() != null) ? getFirstFace()
				.getFaceNormal() : new WB_Normal3d(0, 0, 0);
		final WB_Normal3d n2 = (getSecondFace() != null) ? getSecondFace()
				.getFaceNormal() : new WB_Normal3d(0, 0, 0);
		final WB_Normal3d n = new WB_Normal3d(n1.x + n2.x, n1.y + n2.y, n1.z + n2.z);
		n.normalize();
		return n;
	}

	/**
	 * Get area of faces bounding edge.
	 *
	 * @return area
	 */
	public double getEdgeArea() {
		if ((getFirstFace() == null) && (getSecondFace() == null)) {
			return Double.NaN;
		}
		double result = 0;
		int n = 0;
		if (getFirstFace() != null) {
			result += getFirstFace().getFaceArea();
			n++;
		}
		if (getSecondFace() != null) {
			result += getSecondFace().getFaceArea();
			n++;
		}

		return result / n;

	}

	/**
	 * Return angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getDihedralAngle() {

		if ((getFirstFace() == null) || (getSecondFace() == null)) {
			return Double.NaN;
		} else {
			final WB_Normal3d n1 = getFirstFace().getFaceNormal();
			final WB_Normal3d n2 = getSecondFace().getFaceNormal();
			return Math.PI - Math.acos(n1.dot(n2));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Edge key: " + key() + ", connects vertex "
				+ getStartVertex().key() + " to vertex " + getEndVertex().key()
				+ ".";
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

}
