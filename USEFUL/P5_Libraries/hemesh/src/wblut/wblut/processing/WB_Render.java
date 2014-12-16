/**
 * 
 */
package wblut.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBNode;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Curve;
import wblut.geom.WB_ExplicitTriangle2D;
import wblut.geom.WB_Frame;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Line;
import wblut.geom.WB_Line2D;
import wblut.geom.WB_FrameNode;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point2d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Polygon2D;
import wblut.geom.WB_PolygonType2D;
import wblut.geom.WB_Polyline;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Segment2D;
import wblut.geom.WB_SimpleMesh;
import wblut.geom.WB_FrameStrut;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector3d;
import wblut.geom.WB_VertexType2D;
import wblut.hemesh.HET_Selector;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Render {

	/** Home applet. */
	protected PApplet	_home;

	public WB_Render(final PApplet home) {
		_home = home;

	}

	// RENDER

	/**
	 * Draw one face.
	 * 
	 * @param key
	 *            key of face
	 */
	public void drawFace(final Integer key, final HE_Mesh mesh) {
		List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		tmpVertices = f.getFaceVertices();
		drawConvexShapeFromVertices(tmpVertices, false, null);

	}

	/**
	 * Draw one face.
	 * 
	 * @param f
	 *            face
	 */
	public void drawFace(final HE_Face f) {
		if(f.getFaceOrder()>2){
		if (f.getFaceType() == WB_PolygonType2D.CONVEX) {
			List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
			tmpVertices = f.getFaceVertices();
			drawConvexShapeFromVertices(tmpVertices, false, null);
		} else {
			drawConcaveFace(f);

		}
		}
	}

	public void drawFace(final HE_Face f, final PGraphics pg) {
		if(f.getFaceOrder()>2){
		if (f.getFaceType() == WB_PolygonType2D.CONVEX) {
			List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
			tmpVertices = f.getFaceVertices();
			drawConvexShapeFromVertices(tmpVertices, false, null, pg);
		} else {
			drawConcaveFace(f, pg);

		}
	}
	}

	/**
	 * Draw one face.
	 * 
	 * @param key
	 *            key of face
	 */
	public void drawFace(final Integer key, final boolean smooth,
			final HE_Mesh mesh) {
		drawFace(mesh.getFaceByKey(key),  smooth,
				 mesh) ;
	
	}

	/**
	 * Draw one face.
	 * 
	 * @param f
	 *            face
	 */
	public void drawFace(final HE_Face f, final boolean smooth,
			final HE_Mesh mesh) {
		if(f.getFaceOrder()>2){
		if (f.getFaceType() == WB_PolygonType2D.CONVEX) {
			List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
			tmpVertices = f.getFaceVertices();
			drawConvexShapeFromVertices(tmpVertices, smooth, mesh);
		} else {
			drawConcaveFace(f);

		}
		}
	}

	/**
	 * Draw one facenormal.
	 * 
	 * @param f
	 *            face
	 */
	public void drawFaceNormal(final HE_Face f, final double d) {
		final WB_Point3d p1 = f.getFaceCenter();
		final WB_Point3d p2 = new WB_Point3d(f.getFaceNormal().multAndCopy(d))
				.add(p1);
		_home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 */
	public void drawFacenormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceNormal(fItr.next(), d);
		}
	}

	/**
	* Draw one edge.
	* 
	* @param key
	*            key of edge
	*/
	public void drawEdge(final Integer key, final HE_Mesh mesh) {
		final HE_Edge e = mesh.getEdgeByKey(key);
		_home.line((float) e.getStartVertex().x, (float) e.getStartVertex().y,
				(float) e.getStartVertex().z, (float) e.getEndVertex().x,
				(float) e.getEndVertex().y, (float) e.getEndVertex().z);
	}

	/**
	 * Draw one edge.
	 * 
	 * @param e
	 *            edge
	 */
	public void drawEdge(final HE_Edge e) {
		_home.line((float) e.getStartVertex().x, (float) e.getStartVertex().y,
				(float) e.getStartVertex().z, (float) e.getEndVertex().x,
				(float) e.getEndVertex().y, (float) e.getEndVertex().z);
	}

	/**
	 * Draw one vertex as box.
	 * 
	 * @param key
	 *            key of vertex
	 * @param d
	 *            size of box
	 */
	public void drawVertex(final Integer key, final double d, final HE_Mesh mesh) {

		final HE_Vertex v = mesh.getVertexByKey(key);
		_home.pushMatrix();
		_home.translate((float) (v.x), (float) (v.y), (float) (v.z));
		_home.box((float) d);
		_home.popMatrix();

	}

	/**
	 * Draw one vertex as box.
	 * 
	 * @param v
	 *            vertex
	 * @param d
	 *            size of box
	 */
	public void drawVertex(final HE_Vertex v, final double d) {

		_home.pushMatrix();
		_home.translate((float) (v.x), (float) (v.y), (float) (v.z));
		_home.box((float) d);
		_home.popMatrix();

	}

	public void drawPoint(final WB_Point3d v, final double d) {
		_home.pushMatrix();
		_home.translate((float) (v.x), (float) (v.y), (float) (v.z));
		_home.box((float) d);
		_home.popMatrix();
	}

	public void drawPoint(final Collection<? extends WB_Point3d> points,
			final double d) {
		for (final WB_Point3d v : points) {
			drawPoint(v, d);
		}
	}

	/**
	 * Draw one half-edge.
	 * 
	 * @param key
	 *            key of half-edge
	 * @param d
	 *            offset from edge
	 */
	public void drawHalfedge(final Integer key, final double d, final double s,
			final HE_Mesh mesh) {
		final HE_Halfedge he = mesh.getHalfedgeByKey(key);
		drawHalfedge(he, d, s);

	}
	
	public void drawHalfedgeSimple(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point3d c = he.getHalfedgeCenter();
		c.add(he.getHalfedgeNormal().mult(d));

		_home.line((float) he.getVertex().x, (float) he.getVertex().y,
				(float) he.getVertex().z, (float) c.x, (float) c.y, (float) c.z);
		
		_home.pushMatrix();
		_home.translate((float) c.x, (float) c.y, (float) c.z);
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Draw one half-edge.
	 * 
	 * @param he
	 *            halfedge
	 * @param d
	 *            offset from edge
	 */
	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point3d c = he.getHalfedgeCenter();
		c.add(he.getHalfedgeNormal().mult(d));

		_home.stroke(255, 0, 0);
		_home.line((float) he.getVertex().x, (float) he.getVertex().y,
				(float) he.getVertex().z, (float) c.x, (float) c.y, (float) c.z);
		if (he.getHalfedgeType() == WB_VertexType2D.CONVEX) {
			_home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
			_home.stroke(255, 0, 0);
		} else {
			_home.stroke(0, 0, 255);
		}
		_home.pushMatrix();
		_home.translate((float) c.x, (float) c.y, (float) c.z);
		_home.box((float) s);
		_home.popMatrix();
	}

	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s, final double f) {
		final WB_Point3d c = WB_Point3d.interpolate(he.getVertex(),
				he.getEndVertex(), f);
		c.add(he.getHalfedgeNormal().mult(d));

		_home.stroke(255, 0, 0);
		_home.line((float) he.getVertex().x, (float) he.getVertex().y,
				(float) he.getVertex().z, (float) c.x, (float) c.y, (float) c.z);
		if (he.getHalfedgeType() == WB_VertexType2D.CONVEX) {
			_home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
			_home.stroke(255, 0, 0);
		} else {
			_home.stroke(0, 0, 255);
		}
		_home.pushMatrix();
		_home.translate((float) c.x, (float) c.y, (float) c.z);
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 */
	public void drawFaces(final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next());
		}
	}

	public void drawFaces(final Collection<HE_Mesh> meshes) {
		final Iterator<HE_Mesh> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawFaces(mItr.next());
		}
	}

	public void drawFaces(final HE_Mesh mesh, final PGraphics pg) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), pg);
		}
	}

	public void drawFaces(final WB_SimpleMesh mesh) {
		final int nf = mesh.getFaces().length;
		for (int i = 0; i < nf; i++) {
			final int[] verts = mesh.getFaces()[i];
			final int nv = verts.length;
			_home.beginShape(PConstants.POLYGON);
			for (int j = 0; j < nv; j++) {
				final WB_Point3d p = mesh.getVertex(verts[j]);
				_home.vertex(p.xf(), p.yf(), p.zf());

			}

		}
		_home.endShape(PConstants.CLOSE);
	}

	/**
	 * Draw mesh face types. 
	 * 
	 */
	public void drawFaceTypes(final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getFaceType() == WB_PolygonType2D.CONVEX) {
				_home.fill(0, 255, 0);
			} else if (f.getFaceType() == WB_PolygonType2D.CONCAVE) {
				_home.fill(255, 0, 0);
			} else {
				_home.fill(0, 0, 255);
			}
			drawFace(f);
		}
	}

	/**
	 * Draw mesh face normals.
	 * 
	 */
	public void drawFaceNormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		WB_Point3d fc;
		WB_Normal3d fn;
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			fc = f.getFaceCenter();
			fn = f.getFaceNormal();
			_home.line((float) fc.x, (float) fc.y, (float) fc.z,
					(float) (fc.x + d * fn.x), (float) (fc.y + d * fn.y),
					(float) (fc.z + d * fn.z));
		}
	}

	/**
	 * Draw mesh faces matching label. Typically used with noStroke();
	 * 
	 */
	public void drawFaces(final int label, final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				drawFace(f);
			}
		}
	}

	public void drawFaces(final int label, final HE_Mesh mesh,
			final PGraphics pg) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				drawFace(f, pg);
			}
		}
	}

	/**
	 * Draw mesh edges.
	 * 
	 */
	public void drawEdges(final HE_Mesh mesh) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			_home.line((float) e.getStartVertex().x,
					(float) e.getStartVertex().y, (float) e.getStartVertex().z,
					(float) e.getEndVertex().x, (float) e.getEndVertex().y,
					(float) e.getEndVertex().z);

		}
	}

	public void drawEdges(final WB_SimpleMesh mesh) {
		final int nf = mesh.getFaces().length;
		for (int i = 0; i < nf; i++) {
			final int[] verts = mesh.getFaces()[i];
			final int nv = verts.length;
			for (int j = 0, k = nv - 1; j < nv; k = j, j++) {
				final WB_Point3d p = mesh.getVertex(verts[k]);
				final WB_Point3d q = mesh.getVertex(verts[j]);
				if (p.smallerThan(q)) {
					_home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
				}
			}

		}

	}

	public void drawEdges(final Collection<HE_Mesh> meshes) {
		final Iterator<HE_Mesh> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawEdges(mItr.next());
		}
	}

	public void drawEdges(final HE_Mesh mesh, final PGraphics pg) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			pg.line((float) e.getStartVertex().x, (float) e.getStartVertex().y,
					(float) e.getStartVertex().z, (float) e.getEndVertex().x,
					(float) e.getEndVertex().y, (float) e.getEndVertex().z);

		}
	}

	/**
	 * Draw mesh edges.
	 * 
	 */
	public void drawEdges(final int label, final HE_Mesh mesh) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getLabel() == label) {
				_home.line((float) e.getStartVertex().x,
						(float) e.getStartVertex().y,
						(float) e.getStartVertex().z,
						(float) e.getEndVertex().x, (float) e.getEndVertex().y,
						(float) e.getEndVertex().z);
			}

		}
	}

	public void drawEdges(final int label, final HE_Mesh mesh,
			final PGraphics pg) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getLabel() == label) {
				pg.line((float) e.getStartVertex().x,
						(float) e.getStartVertex().y,
						(float) e.getStartVertex().z,
						(float) e.getEndVertex().x, (float) e.getEndVertex().y,
						(float) e.getEndVertex().z);
			}

		}
	}

	/**
	 * Draw mesh boundary edges.
	 * 
	 */
	public void drawBoundaryEdges(final HE_Mesh mesh) {
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) he.getNextInFace()
								.getVertex().x, (float) he.getNextInFace()
								.getVertex().y, (float) he.getNextInFace()
								.getVertex().z);
			}

		}
	}

	/**
	 * Draw mesh vertices as box.
	 * 
	 * @param d
	 *            size of box
	 */
	public void drawVertices(final double d, final HE_Mesh mesh) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			_home.pushMatrix();
			_home.translate((float) v.x, (float) v.y, (float) v.z);
			_home.box((float) d);
			_home.popMatrix();

		}

	}

	public void drawVertexNormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		WB_Normal3d vn;
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();

			vn = v.getVertexNormal();
			draw(v, vn, d);
		}
	}

	/**
	 * Draw mesh halfedges, for debugging purposes.
	 * 
	 * @param d
	 *            offset from edge
	 */
	public void drawHalfedges(final double d, final HE_Mesh mesh) {

		WB_Point3d c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = he.getHalfedgeCenter();
				c.add(he.getHalfedgeNormal().mult(d));

				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) c.x, (float) c.y,
						(float) c.z);
				if (he.getHalfedgeType() == WB_VertexType2D.CONVEX) {
					_home.stroke(0, 255, 0);
					_home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
					_home.stroke(255, 0, 0);
					_home.fill(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
					_home.fill(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate((float) c.x, (float) c.y, (float) c.z);
				_home.box((float) d);
				_home.popMatrix();
			} else {
				c = he.getHalfedgeCenter();
				c.add(he.getPair().getHalfedgeNormal().mult(-d));

				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) c.x, (float) c.y,
						(float) c.z);
				_home.stroke(0, 255, 255);

				_home.pushMatrix();
				_home.translate((float) c.x, (float) c.y, (float) c.z);
				_home.box((float) d);
				_home.popMatrix();

			}
		}
	}

	public void drawHalfedges(final double d, final double f, final HE_Mesh mesh) {

		WB_Point3d c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = WB_Point3d
						.interpolate(he.getVertex(), he.getEndVertex(), f);
				c.add(he.getHalfedgeNormal().mult(d));

				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) c.x, (float) c.y,
						(float) c.z);

				if (he.getHalfedgeType() == WB_VertexType2D.CONVEX) {
					_home.stroke(0, 255, 0);
					_home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
					_home.stroke(255, 0, 0);
					_home.fill(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
					_home.fill(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate((float) c.x, (float) c.y, (float) c.z);
				_home.box((float) d);
				_home.popMatrix();
			} else {
				c = WB_Point3d
						.interpolate(he.getVertex(), he.getEndVertex(), f);
				c.add(he.getPair().getHalfedgeNormal().mult(-d));

				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) c.x, (float) c.y,
						(float) c.z);
				_home.stroke(0, 255, 255);

				_home.pushMatrix();
				_home.translate((float) c.x, (float) c.y, (float) c.z);
				_home.box((float) d);
				_home.popMatrix();

			}
		}
	}

	/**
	 * Draw unpaired mesh halfedges, for debugging purposes.
	 * 
	 */
	public void drawUnpairedHalfedges(final HE_Mesh mesh) {

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair() == null) {
				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) he.getNextInFace()
								.getVertex().x, (float) he.getNextInFace()
								.getVertex().y, (float) he.getNextInFace()
								.getVertex().z);
			}

		}
	}

	/**
	 * Draw unpaired mesh halfedges, for debugging purposes.
	 * 
	 */
	public void drawBoundaryHalfedges(final HE_Mesh mesh) {

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair().getFace() == null) {
				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) he.getNextInFace()
								.getVertex().x, (float) he.getNextInFace()
								.getVertex().y, (float) he.getNextInFace()
								.getVertex().z);
			}

		}
	}

	/**
	 * Draw faces of selection. Typically used with noStroke();
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawFaces(final HE_Selection selection) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = selection.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next());
		}
	}

	/**
	 * Draw edges of selection.
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawEdges(final HE_Selection selection) {
		final Iterator<HE_Edge> eItr = selection.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			_home.line((float) e.getStartVertex().x,
					(float) e.getStartVertex().y, (float) e.getStartVertex().z,
					(float) e.getEndVertex().x, (float) e.getEndVertex().y,
					(float) e.getEndVertex().z);
		}
	}

	/**
	 * Draw vertices of selection as boxes.
	 *
	 * @param d size of box
	 * @param selection selection to draw
	 */
	public void drawVertices(final double d, final HE_Selection selection) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = selection.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			_home.pushMatrix();
			_home.translate((float) v.x, (float) v.y, (float) v.z);
			_home.box((float) d);
			_home.popMatrix();

		}
	}

	/**
	 * Draw mesh half-edges of selection, for debugging purposes.
	 *
	 * @param d offset from edge
	 * @param selection selection to draw
	 */
	public void drawHalfedges(final double d, final HE_Selection selection) {
		WB_Point3d c;
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = selection.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() != null) {
				c = he.getHalfedgeCenter();
				c.add(he.getHalfedgeNormal().mult(d));
				_home.stroke(255, 0, 0);
				_home.line((float) he.getVertex().x, (float) he.getVertex().y,
						(float) he.getVertex().z, (float) c.x, (float) c.y,
						(float) c.z);
				if (he.getHalfedgeType() == WB_VertexType2D.CONVEX) {
					_home.stroke(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_VertexType2D.CONCAVE) {
					_home.stroke(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate((float) c.x, (float) c.y, (float) c.z);
				_home.box((float) d);
				_home.popMatrix();
			}
		}
	}

	/**
	 * Draw one face using vertex normals.
	 * 
	 * @param key
	 *            key of face
	 */
	public void drawFaceSmooth(final Integer key, final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		drawFace(f, true, mesh);

	}

	/**
	 * Draw mesh faces using vertex normals. Typically used with noStroke().
	 * 
	 */
	public void drawFacesSmooth(final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), true, mesh);
		}

	}

	/**
	 * Draw faces of selection using vertex normals. Typically used with
	 * noStroke().
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawFacesSmooth(final HE_Selection selection) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = selection.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), true, selection.parent);
		}

	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 *
	 * @param selector selector tool
	 * @return key of face at mouse position
	 */
	public Integer drawFaces(final HET_Selector selector, final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		selector.clear();
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			selector.start(f.key());
			drawFace(f);
		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh edges.
	 *
	 * @param selector selector tool
	 * @return key of edge at mouse position
	 */

	public Integer drawEdges(final HET_Selector selector, final HE_Mesh mesh) {
		selector.clear();
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			selector.start(e.key());
			_home.beginShape();
			_home.vertex((float) e.getStartVertex().x,
					(float) e.getStartVertex().y, (float) e.getStartVertex().z);
			_home.vertex((float) e.getEndVertex().x,
					(float) e.getEndVertex().y, (float) e.getEndVertex().z);
			_home.endShape();

		}

		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh vertices as box.
	 *
	 * @param selector selector tool
	 * @param d size of box
	 * @return key of vertex at mouse position
	 */
	public Integer drawVertices(final HET_Selector selector, final double d,
			final HE_Mesh mesh) {
		selector.clear();
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {

			v = vItr.next();
			selector.start(v.key());
			_home.pushMatrix();
			_home.translate((float) v.x, (float) v.y, (float) v.z);
			_home.box((float) d);
			_home.popMatrix();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 *
	 * @param selection selection to draw
	 * @param selector selector tool
	 * @return key of face at mouse position
	 */
	public Integer drawFaces(final HE_Selection selection,
			final HET_Selector selector) {
		new ArrayList<HE_Vertex>();
		selector.clear();
		final Iterator<HE_Face> fItr = selection.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			selector.start(f.key());
			drawFace(f);
		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh edges.
	 *
	 * @param selection selection to draw
	 * @param selector selector tool
	 * @return key of edge at mouse position
	 */

	public Integer drawEdges(final HE_Selection selection,
			final HET_Selector selector) {
		selector.clear();
		final Iterator<HE_Edge> eItr = selection.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			selector.start(e.key());
			_home.beginShape();
			_home.vertex((float) e.getStartVertex().x,
					(float) e.getStartVertex().y, (float) e.getStartVertex().z);
			_home.vertex((float) e.getEndVertex().x,
					(float) e.getEndVertex().y, (float) e.getEndVertex().z);
			_home.endShape();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh vertices as box.
	 *
	 * @param d size of box
	 * @param selection selection to draw
	 * @param selector selector tool
	 * @return key of vertex at mouse position
	 */
	public Integer drawVertices(final double d, final HE_Selection selection,
			final HET_Selector selector) {
		selector.clear();
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {

			v = vItr.next();
			selector.start(v.key());
			_home.pushMatrix();
			_home.translate((float) v.x, (float) v.y, (float) v.z);
			_home.box((float) d);
			_home.popMatrix();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	private void drawConcaveFace(final HE_Face f) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point3d v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			_home.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1);
			v1 = vertices.get(tri.i2);
			v2 = vertices.get(tri.i3);

			_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);

			_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);

			_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
			_home.endShape();

		}
	}

	private void drawConcaveFace(final HE_Face f, final PGraphics pg) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point3d v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			pg.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1);
			v1 = vertices.get(tri.i2);
			v2 = vertices.get(tri.i3);

			pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);

			pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);

			pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
			pg.endShape();

		}
	}

	/**
	 * Draw arbitrary convex face. Used internally by drawFace().
	 *
	 * @param vertices vertices of face
	 * @param smooth use vertex normals?
	 */
	private void drawConvexShapeFromVertices(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_Mesh mesh) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Normal3d n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Normal3d n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Normal3d n2 = v2.getVertexNormal();
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n1.x, (float) n1.y, (float) n1.z);
				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				_home.normal((float) n2.x, (float) n2.y, (float) n2.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.endShape();
			} else {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);

				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);

				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.endShape();

			}
		} else if (degree == 4) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final WB_Normal3d n0 = v0.getVertexNormal();
				final WB_Normal3d n1 = v1.getVertexNormal();
				final WB_Normal3d n2 = v2.getVertexNormal();
				final WB_Normal3d n3 = v3.getVertexNormal();

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n1.x, (float) n1.y, (float) n1.z);
				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				_home.normal((float) n2.x, (float) n2.y, (float) n2.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n2.x, (float) n2.y, (float) n2.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.normal((float) n3.x, (float) n3.y, (float) n3.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);

				_home.endShape();

			}
		} else if (degree == 5) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);

				final WB_Normal3d n0 = v0.getVertexNormal();
				final WB_Normal3d n1 = v1.getVertexNormal();
				final WB_Normal3d n2 = v2.getVertexNormal();
				final WB_Normal3d n3 = v3.getVertexNormal();
				final WB_Normal3d n4 = v4.getVertexNormal();

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n1.x, (float) n1.y, (float) n1.z);
				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				_home.normal((float) n2.x, (float) n2.y, (float) n2.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n2.x, (float) n2.y, (float) n2.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.normal((float) n3.x, (float) n3.y, (float) n3.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				_home.normal((float) n0.x, (float) n0.y, (float) n0.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.normal((float) n3.x, (float) n3.y, (float) n3.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				_home.normal((float) n4.x, (float) n4.y, (float) n4.z);
				_home.vertex((float) v4.x, (float) v4.y, (float) v4.z);

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				_home.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				_home.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				_home.vertex((float) v4.x, (float) v4.y, (float) v4.z);
				_home.endShape();
			}
		} else {
			final ArrayList<HE_Vertex> subset = new ArrayList<HE_Vertex>();
			int div = 3;
			final int rem = vertices.size() - 5;
			if (rem == 1) {
				div = 3;
			} else if (rem == 2) {
				div = 4;
			} else {
				div = 5;
			}

			for (int i = 0; i < div; i++) {
				subset.add(vertices.get(i));
			}
			final ArrayList<HE_Vertex> toRemove = new ArrayList<HE_Vertex>();
			toRemove.add(vertices.get(1));
			if (div > 3) {
				toRemove.add(vertices.get(2));
			}
			if (div > 4) {
				toRemove.add(vertices.get(3));
			}
			vertices.removeAll(toRemove);
			drawConvexShapeFromVertices(subset, smooth, mesh);
			drawConvexShapeFromVertices(vertices, smooth, mesh);
		}

	}

	private void drawConvexShapeFromVertices(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_Mesh mesh, final PGraphics pg) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				pg.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Normal3d n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Normal3d n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Normal3d n2 = v2.getVertexNormal();
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n1.x, (float) n1.y, (float) n1.z);
				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				pg.normal((float) n2.x, (float) n2.y, (float) n2.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.endShape();
			} else {
				pg.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);

				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);

				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.endShape();

			}
		} else if (degree == 4) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final WB_Normal3d n0 = v0.getVertexNormal();
				final WB_Normal3d n1 = v1.getVertexNormal();
				final WB_Normal3d n2 = v2.getVertexNormal();
				final WB_Normal3d n3 = v3.getVertexNormal();

				pg.beginShape(PConstants.TRIANGLES);
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n1.x, (float) n1.y, (float) n1.z);
				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				pg.normal((float) n2.x, (float) n2.y, (float) n2.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n2.x, (float) n2.y, (float) n2.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.normal((float) n3.x, (float) n3.y, (float) n3.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);

				pg.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				pg.beginShape(PConstants.TRIANGLES);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);

				pg.endShape();

			}
		} else if (degree == 5) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);

				final WB_Normal3d n0 = v0.getVertexNormal();
				final WB_Normal3d n1 = v1.getVertexNormal();
				final WB_Normal3d n2 = v2.getVertexNormal();
				final WB_Normal3d n3 = v3.getVertexNormal();
				final WB_Normal3d n4 = v4.getVertexNormal();

				pg.beginShape(PConstants.TRIANGLES);
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n1.x, (float) n1.y, (float) n1.z);
				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				pg.normal((float) n2.x, (float) n2.y, (float) n2.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n2.x, (float) n2.y, (float) n2.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.normal((float) n3.x, (float) n3.y, (float) n3.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				pg.normal((float) n0.x, (float) n0.y, (float) n0.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.normal((float) n3.x, (float) n3.y, (float) n3.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				pg.normal((float) n4.x, (float) n4.y, (float) n4.z);
				pg.vertex((float) v4.x, (float) v4.y, (float) v4.z);

				pg.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				pg.beginShape(PConstants.TRIANGLES);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.vertex((float) v1.x, (float) v1.y, (float) v1.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.vertex((float) v2.x, (float) v2.y, (float) v2.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				pg.vertex((float) v0.x, (float) v0.y, (float) v0.z);
				pg.vertex((float) v3.x, (float) v3.y, (float) v3.z);
				pg.vertex((float) v4.x, (float) v4.y, (float) v4.z);
				pg.endShape();
			}
		} else {
			final ArrayList<HE_Vertex> subset = new ArrayList<HE_Vertex>();
			int div = 3;
			final int rem = vertices.size() - 5;
			if (rem == 1) {
				div = 3;
			} else if (rem == 2) {
				div = 4;
			} else {
				div = 5;
			}

			for (int i = 0; i < div; i++) {
				subset.add(vertices.get(i));
			}
			final ArrayList<HE_Vertex> toRemove = new ArrayList<HE_Vertex>();
			toRemove.add(vertices.get(1));
			if (div > 3) {
				toRemove.add(vertices.get(2));
			}
			if (div > 4) {
				toRemove.add(vertices.get(3));
			}
			vertices.removeAll(toRemove);
			drawConvexShapeFromVertices(subset, smooth, mesh);
			drawConvexShapeFromVertices(vertices, smooth, mesh);
		}

	}

	/**
	 * Draw mesh edges as Bezier curves.
	 * 
	 */
	public void drawBezierEdges(final HE_Mesh mesh) {
		HE_Halfedge he;
		WB_Point3d p0;
		WB_Point3d p1;
		WB_Point3d p2;
		WB_Point3d p3;
		HE_Face f;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			_home.beginShape();
			he = f.getHalfedge();
			p0 = he.getPrevInFace().getHalfedgeCenter();
			_home.vertex((float) p0.x, (float) p0.y, (float) p0.z);

			do {

				p1 = he.getVertex();
				p2 = he.getVertex();
				p3 = he.getHalfedgeCenter();

				_home.bezierVertex((float) p1.x, (float) p1.y, (float) p1.z,
						(float) p2.x, (float) p2.y, (float) p2.z, (float) p3.x,
						(float) p3.y, (float) p3.z);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
			_home.endShape();

		}
	}

	public void drawPolygonEdges(final Collection<? extends WB_Polygon> polygons) {
		final Iterator<? extends WB_Polygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonEdges(polyItr.next());
		}

	}

	public void drawPolygonEdges(final WB_Polygon polygon) {
		WB_Point3d v1, v2;
		final int n = polygon.getN();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.getPoint(i);
			v2 = polygon.getPoint(j);
			_home.line(v1.xf(), v1.yf(), v1.zf(), v2.xf(), v2.yf(), v2.zf());
		}
	}

	public void drawPolygonVertices(final Collection<WB_Polygon> polygons,
			final double d) {
		final Iterator<WB_Polygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonVertices(polyItr.next(), d);
		}

	}

	public void drawPolygonVertices(final WB_Polygon polygon, final double d) {
		WB_Point3d v1;
		final int n = polygon.getN();
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			_home.pushMatrix();
			_home.translate(v1.xf(), v1.yf(), v1.zf());
			_home.box((float) d);
			_home.popMatrix();
		}
	}

	public void drawPolygon(final Collection<? extends WB_Polygon> polygons) {
		final Iterator<? extends WB_Polygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon(polyItr.next());
		}

	}

	public void drawPolygon(final WB_Polygon polygon) {
		WB_Point3d v1;
		final int n = polygon.getN();
		_home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			_home.vertex(v1.xf(), v1.yf(), v1.zf());

		}
		_home.endShape(PConstants.CLOSE);
	}

	public void drawPolylineEdges(final Collection<WB_Polyline> polylines) {
		final Iterator<WB_Polyline> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineEdges(polyItr.next());
		}

	}

	public void drawPolylineEdges(final WB_Polyline polyline) {
		WB_Point3d v1, v2;
		final int n = polyline.n;
		for (int i = 0; i < n - 1; i++) {
			v1 = polyline.points[i];
			v2 = polyline.points[i + 1];
			_home.line(v1.xf(), v1.yf(), v1.zf(), v2.xf(), v2.yf(), v2.zf());
		}
	}

	public void drawPolylineVertices(final Collection<WB_Polyline> polylines,
			final double d) {
		final Iterator<WB_Polyline> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineVertices(polyItr.next(), d);
		}

	}

	public void drawPolylineVertices(final WB_Polyline polyline, final double d) {
		WB_Point3d v1;
		final int n = polyline.n;
		for (int i = 0; i < n; i++) {
			v1 = polyline.points[i];
			_home.pushMatrix();
			_home.translate(v1.xf(), v1.yf(), v1.zf());
			_home.box((float) d);
			_home.popMatrix();
		}
	}

	public void drawTriangleEdges(final Collection<WB_Triangle> triangles) {
		final Iterator<WB_Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangleEdges(triItr.next());
		}

	}

	public void drawTriangleEdges(final WB_Triangle triangle) {

		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		_home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());

	}

	public void drawTriangle(final Collection<WB_Triangle> triangles) {

		final Iterator<WB_Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle(triItr.next());
		}

	}

	public void drawTriangle(final WB_Triangle triangle) {
		_home.beginShape();
		_home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		_home.vertex(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());
		_home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		_home.endShape();
	}

	public void drawPolygon2DEdges(final Collection<WB_Polygon2D> polygons) {
		final Iterator<WB_Polygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DEdges(polyItr.next());
		}

	}

	public void drawPolygon2DEdges(final WB_Polygon2D polygon) {
		WB_Point2d v1, v2;
		final int n = polygon.n;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.points[i];
			v2 = polygon.points[j];
			_home.line(v1.xf(), v1.yf(), v2.xf(), v2.yf());
		}
	}

	public void drawPolygon2DVertices(final Collection<WB_Polygon2D> polygons,
			final double d) {
		final Iterator<WB_Polygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DVertices(polyItr.next(), d);
		}

	}

	public void drawPolygon2DVertices(final WB_Polygon2D polygon, final double d) {
		WB_Point2d v1;
		final int n = polygon.n;
		for (int i = 0; i < n; i++) {
			v1 = polygon.points[i];

			_home.ellipse(v1.xf(), v1.yf(), (float) d, (float) d);

		}
	}

	public void drawPolygon2D(final Collection<WB_Polygon2D> polygons) {
		final Iterator<WB_Polygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2D(polyItr.next());
		}

	}

	public void drawPolygon2D(final WB_Polygon2D polygon) {
		WB_Point2d v1;
		final int n = polygon.n;
		_home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.points[i];
			_home.vertex(v1.xf(), v1.yf());

		}
		_home.endShape(PConstants.CLOSE);
	}

	public void drawTriangle2DEdges(
			final Collection<WB_ExplicitTriangle2D> triangles) {
		final Iterator<WB_ExplicitTriangle2D> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2DEdges(triItr.next());
		}

	}

	public void drawTriangle2DEdges(final WB_ExplicitTriangle2D[] triangles) {

		for (final WB_ExplicitTriangle2D triangle : triangles) {
			drawTriangle2DEdges(triangle);
		}

	}

	public void drawTriangle2DEdges(final WB_ExplicitTriangle2D triangle) {

		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		_home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p3().xf(),
				triangle.p3().yf());

	}

	public void drawTriangle2D(final Collection<WB_ExplicitTriangle2D> triangles) {

		final Iterator<WB_ExplicitTriangle2D> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2D(triItr.next());
		}

	}

	public void drawTriangle2D(final WB_ExplicitTriangle2D[] triangles) {

		for (final WB_ExplicitTriangle2D triangle : triangles) {
			drawTriangle2D(triangle);
		}

	}

	public void drawTriangle2D(final WB_ExplicitTriangle2D triangle) {
		_home.beginShape();
		_home.vertex(triangle.p1().xf(), triangle.p1().yf());
		_home.vertex(triangle.p2().xf(), triangle.p2().yf());
		_home.vertex(triangle.p3().xf(), triangle.p3().yf());
		_home.endShape();
	}

	public void drawSegment2D(final Collection<? extends WB_Segment2D> segments) {
		final Iterator<? extends WB_Segment2D> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment2D(segItr.next());
		}

	}

	public void drawSegment2D(final WB_Segment2D[] segments) {
		for (final WB_Segment2D segment : segments) {
			drawSegment2D(segment);
		}

	}

	public void drawSegment2D(final WB_Segment2D segment) {

		_home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getEnd().xf(), segment.getEnd().yf());

	}

	public void drawSegment(final Collection<? extends WB_Segment> segments) {
		final Iterator<? extends WB_Segment> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment(segItr.next());
		}

	}

	public void drawSegment(final WB_Segment segment) {

		_home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getOrigin().zf(), segment.getEnd().xf(),
				segment.getEnd().yf(), segment.getEnd().zf());

	}

	public void draw(final WB_Circle C) {

		_home.ellipse(C.getCenter().xf(), C.getCenter().yf(),
				2 * (float) C.getRadius(), 2 * (float) C.getRadius());

	}

	public void draw(final Collection<WB_Circle> circles) {
		final Iterator<WB_Circle> citr = circles.iterator();
		while (citr.hasNext()) {
			draw(citr.next());
		}

	}

	public void draw(final WB_Line2D L, final double s) {

		_home.line(L.getOrigin().xf() - (float) (s * L.getDirection().x), L
				.getOrigin().yf() - (float) (s * L.getDirection().y), L
				.getOrigin().xf() + (float) (s * L.getDirection().x), L
				.getOrigin().yf() + (float) (s * L.getDirection().y));

	}

	public void draw(final WB_Ray R, final double s) {

		_home.line(R.getOrigin().xf(), R.getOrigin().yf(), R.getOrigin().zf(),
				R.getOrigin().xf() + (float) (s * R.getDirection().x), R
						.getOrigin().yf() + (float) (s * R.getDirection().y), R
						.getOrigin().zf() + (float) (s * R.getDirection().z));

	}

	public void draw(final WB_Curve C, final int steps) {
		final int n = Math.max(1, steps);
		WB_Point3d p0 = C.curvePoint(0);
		WB_Point3d p1;
		final double du = 1.0 / n;
		for (int i = 0; i < n; i++) {
			p1 = C.curvePoint((i + 1) * du);
			_home.line(p0.xf(), p0.yf(), p0.zf(), p1.xf(), p1.yf(), p1.zf());
			p0 = p1;
		}

	}

	public void draw(final Collection<WB_Curve> curves, final int steps) {
		final Iterator<WB_Curve> citr = curves.iterator();
		while (citr.hasNext()) {
			draw(citr.next(), steps);
		}
	}

	public void draw2D(final WB_Curve C, final int steps) {
		final int n = Math.max(1, steps);
		WB_Point3d p0 = C.curvePoint(0);
		WB_Point3d p1;
		final double du = 1.0 / n;
		for (int i = 0; i < n; i++) {
			p1 = C.curvePoint((i + 1) * du);
			_home.line(p0.xf(), p0.yf(), p1.xf(), p1.yf());
			p0 = p1;
		}

	}

	public void draw2D(final Collection<WB_Curve> curves, final int steps) {
		final Iterator<WB_Curve> citr = curves.iterator();
		while (citr.hasNext()) {
			draw2D(citr.next(), steps);
		}
	}

	public void draw(final WB_Frame frame) {
		final ArrayList<WB_FrameStrut> struts = frame.getStruts();
		for (int i = 0; i < frame.getNumberOfStruts(); i++) {
			draw(struts.get(i));
		}
	}

	public void drawNodes(final WB_Frame frame, final double s) {
		final ArrayList<WB_FrameNode> nodes = frame.getNodes();
		for (int i = 0; i < frame.getNumberOfNodes(); i++) {
			draw(nodes.get(i), s);
		}
	}

	public void draw(final WB_FrameStrut strut) {
		_home.line(strut.start().xf(), strut.start().yf(), strut.start().zf(),
				strut.end().xf(), strut.end().yf(), strut.end().zf());
	}

	public void draw(final WB_FrameNode node, final double s) {
		_home.pushMatrix();
		_home.translate(node.xf(), node.yf(), node.zf());
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	* Get calling applet.
	* 
	* @return home applet
	*/
	public PApplet home() {
		return _home;
	}

	public void draw(final WB_Point3d p, final WB_Point3d q) {
		_home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
	}

	public void draw(final WB_Point3d p, final WB_Vector3d v, final double d) {
		_home.line(p.xf(), p.yf(), p.zf(), p.xf() + (float) d * v.xf(), p.yf()
				+ (float) d * v.yf(), p.zf() + (float) d * v.zf());
	}

	public void draw(final WB_Point3d p, final WB_Normal3d n, final double d) {
		_home.line(p.xf(), p.yf(), p.zf(), p.xf() + (float) d * n.xf(), p.yf()
				+ (float) d * n.yf(), p.zf() + (float) d * n.zf());
	}

	public void draw(final WB_Plane P, final double W) {
		final double hw = 0.5 * W;
		WB_Point3d p = P.extractPoint(-hw, -hw);
		_home.beginShape(PConstants.QUAD);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(hw, -hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(hw, hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(-hw, hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		_home.endShape();
	}

	public void draw(final WB_Line L, final double W) {
		final double hw = 0.5 * W;
		final WB_Point3d p1 = L.getPoint(-hw);
		final WB_Point3d p2 = L.getPoint(hw);
		_home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
	}

	public void draw(final WB_AABB AABB) {
		_home.pushMatrix();
		_home.translate(AABB.getCenter().xf(), AABB.getCenter().yf(), AABB
				.getCenter().zf());
		_home.box((float) AABB.getWidth(), (float) AABB.getHeight(),
				(float) AABB.getDepth());
		_home.popMatrix();
	}

	public void draw(final WB_AABBTree tree) {
		drawNode(tree.getRoot());

	}

	public void draw(final WB_AABBTree tree, final int level) {
		drawNode(tree.getRoot(), level);

	}

	public void drawLeafs(final WB_AABBTree tree) {
		drawLeafNode(tree.getRoot());

	}

	public void draw(final WB_AABBNode node) {
		draw(node.getAABB());
	}

	private void drawNode(final WB_AABBNode node) {
		draw(node.getAABB());

		if (node.getPosChild() != null) {
			drawNode(node.getPosChild());
		}
		if (node.getNegChild() != null) {
			drawNode(node.getNegChild());
		}
		if (node.getMidChild() != null) {
			drawNode(node.getMidChild());
		}
	}

	private void drawNode(final WB_AABBNode node, final int level) {
		if (node.getLevel() == level) {
			draw(node.getAABB());
		}
		if (node.getLevel() < level) {
			if (node.getPosChild() != null) {
				drawNode(node.getPosChild(), level);
			}
			if (node.getNegChild() != null) {
				drawNode(node.getNegChild(), level);
			}
			if (node.getMidChild() != null) {
				drawNode(node.getMidChild(), level);
			}
		}
	}

	private void drawLeafNode(final WB_AABBNode node) {
		if (node.isLeaf()) {
			draw(node.getAABB());
		} else {
			if (node.getPosChild() != null) {
				drawLeafNode(node.getPosChild());
			}
			if (node.getNegChild() != null) {
				drawLeafNode(node.getNegChild());
			}
			if (node.getMidChild() != null) {
				drawLeafNode(node.getMidChild());
			}
		}
	}

	public void draw(final WB_Point3d point, final double d) {
		_home.pushMatrix();
		_home.translate(point.xf(), point.yf(), point.zf());
		_home.box((float) d);
		_home.popMatrix();
	}

	public void draw(final Collection<? extends WB_Point3d> points,
			final double d) {
		for (final WB_Point3d point : points) {
			_home.pushMatrix();
			_home.translate(point.xf(), point.yf(), point.zf());
			_home.box((float) d);
			_home.popMatrix();
		}
	}
}
