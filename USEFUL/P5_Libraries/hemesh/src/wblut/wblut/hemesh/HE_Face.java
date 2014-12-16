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
import java.util.List;

import wblut.core.WB_HasData;
import wblut.geom.WB_ExplicitPolygon;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Polygon2D;
import wblut.geom.WB_PolygonType2D;
import wblut.geom.WB_Vector3d;
import wblut.geom.WB_VertexType2D;
import wblut.math.WB_Fast;


import javolution.util.FastList;

/**
 * Face element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Face  implements WB_HasData {

	/** Halfedge associated with this face. */
	private HE_Halfedge		_halfedge;

	/** Status of sorting. */
	protected boolean		_sorted;

	/** Static face key counter. */
	protected static int	_currentKey;

	/** Unique face key. */
	protected final Integer	_key;

	/** General purpose label. */
	protected int			label;
	
	private HashMap<String, Object> _data;

	/**
	 * Instantiates a new HE_Face.
	 */
	public HE_Face() {
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
	 * Get face center.
	 *
	 * @return center
	 */
	public WB_Point3d getFaceCenter() {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		final WB_Point3d _center = new WB_Point3d();
		int c = 0;
		do {
			_center.add(he.getVertex());
			c++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		_center.div(c);
		return _center;
	}

	/**
	 * Get face normal.  Returns stored value if update status is true.
	 *
	 * @return normal
	 */
	public WB_Normal3d getFaceNormal() {
		if (_halfedge == null) {
			return null;
		}
		// calculate normal with Newell's method
		HE_Halfedge he = _halfedge;
		final WB_Normal3d _normal = new WB_Normal3d();
		HE_Vertex p0;
		HE_Vertex p1;
		do {
			p0 = he.getVertex();
			p1 = he.getNextInFace().getVertex();

			_normal.x += (p0.y - p1.y) * (p0.z + p1.z);
			_normal.y += (p0.z - p1.z) * (p0.x + p1.x);
			_normal.z += (p0.x - p1.x) * (p0.y + p1.y);

			he = he.getNextInFace();
		} while (he != _halfedge);
		_normal.normalize();
		return _normal;
	}

	/**
	 * Get face area.
	 *
	 * @return area
	 */
	public double getFaceArea() {
		if (_halfedge == null) {
			return Double.NaN;
		}
		final WB_Normal3d n = getFaceNormal();
		final double x = WB_Fast.abs(n.x);
		final double y = WB_Fast.abs(n.y);
		final double z = WB_Fast.abs(n.z);
		double area = 0;
		int coord = 3;
		if (x >= y && x >= z) {
			coord = 1;
		} else if (y >= x && y >= z) {
			coord = 2;
		}
		HE_Halfedge he = _halfedge;
		do {
			switch (coord) {
			case 1:
				area += (he.getVertex().y * (he.getNextInFace().getVertex().z - he
						.getPrevInFace().getVertex().z));
				break;
			case 2:
				area += (he.getVertex().x * (he.getNextInFace().getVertex().z - he
						.getPrevInFace().getVertex().z));
				break;
			case 3:
				area += (he.getVertex().x * (he.getNextInFace().getVertex().y - he
						.getPrevInFace().getVertex().y));
				break;

			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		switch (coord) {
		case 1:
			area *= (0.5 / x);
			break;
		case 2:
			area *= (0.5 / y);
			break;
		case 3:
			area *= (0.5 / z);
		}

		return WB_Fast.abs(area);

	}

	/**
	 * Get face type.
	 *
	 * @return WB_PolygonType2D.CONVEX, WB_PolygonType2D.CONCAVE
	 */
	public WB_PolygonType2D getFaceType() {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
				return WB_PolygonType2D.CONCAVE;
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return WB_PolygonType2D.CONVEX;
	}

	/**
	 * Get vertices of face as arraylist of HE_Vertex.
	 *
	 * @return vertices
	 */
	public List<HE_Vertex> getFaceVertices() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Vertex> fv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return fv;
		}
		HE_Halfedge he = _halfedge;
		do {

			if (!fv.contains(he.getVertex())) {
				fv.add(he.getVertex());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return fv;

	}

	/**
	 * Get number of vertices in face.
	 *
	 * @return number of vertices
	 */
	public int getFaceOrder() {

		int result = 0;
		if (_halfedge == null) {
			return 0;
		}
		HE_Halfedge he = _halfedge;
		do {

			result++;
			he = he.getNextInFace();
		} while (he != _halfedge);

		return result;

	}

	/**
	 * Get halfedges of face as arraylist of HE_Halfedge. The halfedge of the leftmost vertex is returned first.
	 *
	 * @return halfedges
	 */
	public List<HE_Halfedge> getFaceHalfedges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Halfedge> fhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fhe.contains(he)) {
				fhe.add(he);
			}

			he = he.getNextInFace();
		} while (he != _halfedge);

		return fhe;

	}

	/**
	 * Get edges of face as arraylist of HE_Edge.  The edge of the leftmost vertex is returned first.
	 *
	 * @return edges
	 */
	public List<HE_Edge> getFaceEdges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Edge> fe = new FastList<HE_Edge>();
		if (_halfedge == null) {
			return fe;
		}
		HE_Halfedge he = _halfedge;
		do {

			if (!fe.contains(he.getEdge())) {
				fe.add(he.getEdge());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return fe;

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
		_sorted = false;
	}

	/**
	 * Clear halfedge.
	 */
	public void clearHalfedge() {
		_halfedge = null;
		_sorted = false;
	}

	/**
	 * Get plane of face.
	 *
	 * @return plane
	 */
	public WB_Plane toPlane() {
		return new WB_Plane(getFaceCenter(), getFaceNormal());
	}

	/**
	 * Get plane of face.
	 *
	 * @return plane
	 */
	public WB_Plane toPlane(final double d) {
		final WB_Vector3d fn = getFaceNormal();
		return new WB_Plane(getFaceCenter().add(fn, d), fn);
	}

	/**
	 * Sort halfedges in lexicographic order.
	 */
	public void sort() {
		if (_halfedge != null) {

			HE_Halfedge he = _halfedge;
			HE_Halfedge leftmost = he;
			do {
				he = he.getNextInFace();
				if (he.getVertex().compareTo(leftmost.getVertex()) < 0) {
					leftmost = he;
				}
			} while (he != _halfedge);
			_halfedge = leftmost;
			_sorted = true;
		}
	}

	/**
	 * Triangulate the face, returns indexed 2D triangles. The index refers to the face vertices()
	 * @return ArrayList of WB_IndexedTriangle
	 */
	public List<WB_IndexedTriangle2D> triangulate() {
		return toPolygon2D().indexedTriangulate();
	}

	/**
	 * Get the face as a WB_Polygon2D.
	 * 
	 * @return face as WB_Polygon2D
	 */
	public WB_ExplicitPolygon toPolygon() {
		final int n = getFaceOrder();
		if (n == 0) {
			return null;
		}

		final WB_Point3d[] points = new WB_Point3d[n];
		if (!_sorted) {
			sort();
		}

		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i] = new WB_Point3d(he.getVertex().x, he.getVertex().y,
					he.getVertex().z);

			he = he.getNextInFace();
			i++;
		} while (he != _halfedge);

		return new WB_ExplicitPolygon(points, n);
	}

	/**
	 * Get the face as a WB_Polygon2D.
	 * 
	 * @return face as WB_Polygon2D
	 */
	public WB_Polygon2D toPolygon2D() {

		return toPolygon().toPolygon2D();
	}

	/**
	 * Get neighboring faces as arraylist of HE_Face. The face of the leftmost halfedge is returned first.
	 *
	 * @return neighboring faces
	 */
	public List<HE_Face> getNeighborFaces() {
		if (!isSorted()) {
			sort();
		}
		final List<HE_Face> ff = new FastList<HE_Face>();
		if (getHalfedge() == null) {
			return ff;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hep = he.getPair();
			if (hep.getFace() != null) {
				if (hep.getFace() != this) {
					if (!ff.contains(hep.getFace())) {
						ff.add(hep.getFace());
					}
				}
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());

		return ff;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Face key: " + key() + ".";
	}

	public boolean isSorted() {
		return _sorted;
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
