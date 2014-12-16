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

import java.util.List;
import java.util.Map;

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance;
import wblut.geom.WB_ExplicitPolygon;
import wblut.geom.WB_ExplicitSegment;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_PolygonType2D;
import wblut.geom.WB_Segment;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;


import javolution.util.FastList;
import javolution.util.FastMap;

// TODO: Auto-generated Javadoc
/**
 * Extrudes and scales a face along its face normal.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Extrude extends HEM_Modifier {

	/** Extrusion distance. */
	private WB_Parameter<Double>		d;

	/** Threshold angle for hard edges. */
	private double						thresholdAngle;

	/** Chamfer factor or distance. */
	private double						chamfer;

	/** Hard edge chamfer distance. */
	private double						hardEdgeChamfer;

	/** Extrusion mode. */
	private boolean						relative;

	/** Fuse coplanar faces. */
	private boolean						fuse;

	/** Turn non-extrudable faces into spiked faces? */
	private boolean						peak;

	/** Limit angle for face fusion. */
	private double						fuseAngle;

	/** sin(fuseAngle). */
	private double						sin2FA;

	/** Vertex normals. */
	private Map<Integer, WB_Normal3d>	_faceNormals;

	/** Halfedge normals. */
	private Map<Integer, WB_Normal3d>	_halfedgeNormals;

	/** Extrusion widths */
	private Map<Integer, Double>		_halfedgeEWs;

	/** Face centers. */
	private Map<Integer, WB_Point3d>	_faceCenters;

	public HE_Selection					walls;
	public HE_Selection					extruded;

	private List<HE_Face>				failedFaces;

	private boolean						flat;

	/**
	 * Instantiates a new HEM_Extrude
	 */
	public HEM_Extrude() {
		super();
		d = new WB_ConstantParameter(0);
		flat = true;
		thresholdAngle = -1;
		chamfer = 0;
		hardEdgeChamfer = 0;
		relative = true;
		fuseAngle = Math.PI / 36;
		sin2FA = Math.sin(fuseAngle);
		sin2FA *= sin2FA;
	}

	/**
	 * Set extrusion distance.
	 *
	 * @param d extrusion distance
	 * @return self
	 */
	public HEM_Extrude setDistance(final double d) {
		this.d = new WB_ConstantParameter(d);
		flat = (WB_Epsilon.isZero(d));
		return this;
	}

	public HEM_Extrude setDistance(final WB_Parameter<Double> d) {
		this.d = d;
		flat = false;
		return this;
	}

	/**
	 * Set chamfer factor.
	 *
	 * @param c chamfer factor
	 * @return self
	 */
	public HEM_Extrude setChamfer(final double c) {
		chamfer = c;
		return this;
	}

	/**
	 * Set hard edge chamfer distance
	 * 
	 * Set extrusion distance for hard edge.
	 *
	 * @param c extrusion distance
	 * @return self
	 */
	public HEM_Extrude setHardEdgeChamfer(final double c) {
		hardEdgeChamfer = c;
		return this;
	}

	/**
	 * Set chamfer mode.
	 *
	 * @param relative true/false
	 * @return self
	 */
	public HEM_Extrude setRelative(final boolean relative) {
		this.relative = relative;
		return this;
	}

	/**
	 * Set fuse option: merges coplanar faces.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Extrude setFuse(final boolean b) {
		fuse = b;
		return this;
	}

	/**
	 * Set peak option.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Extrude setPeak(final boolean b) {
		peak = b;
		return this;
	}

	/**
	 * Set threshold angle for hard edge.
	 *
	 * @param a threshold angle
	 * @return self
	 */
	public HEM_Extrude setThresholdAngle(final double a) {
		thresholdAngle = a;
		return this;
	}

	/**
	 * Set threshold angle for fuse.
	 *
	 * @param a threshold angle
	 * @return self
	 */
	public HEM_Extrude setFuseAngle(final double a) {
		fuseAngle = a;
		sin2FA = Math.sin(fuseAngle);
		sin2FA *= sin2FA;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.resetFaceLabels();
		walls = new HE_Selection(mesh);
		extruded = new HE_Selection(mesh);
		_halfedgeNormals = new FastMap<Integer, WB_Normal3d>();
		_halfedgeEWs = new FastMap<Integer, Double>();

		if ((chamfer == 0) && (d == null)) {
			return mesh;
		}

		HE_Face f;
		HE_Halfedge he;
		final List<HE_Face> faces = mesh.getFacesAsList();
		_faceNormals = mesh.getKeyedFaceNormals();
		_faceCenters = mesh.getKeyedFaceCenters();
		final int nf = faces.size();

		for (int i = 0; i < nf; i++) {
			f = faces.get(i);
			he = f.getHalfedge();
			do {
				_halfedgeNormals.put(he.key(), he.getHalfedgeNormal());
				_halfedgeEWs
						.put(he.key(),
								(he.getHalfedgeDihedralAngle() < thresholdAngle) ? hardEdgeChamfer
										: chamfer);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

		}

		if (chamfer == 0) {
			return applyStraight(mesh, mesh.getFacesAsList());
		}

		if ((relative == true) && (chamfer == 1)) {
			return applyPeaked(mesh, mesh.getFacesAsList());
		}
		failedFaces = new FastList<HE_Face>();
		applyFlat(mesh, faces, flat && fuse);
		if (peak) {
			applyPeaked(mesh, failedFaces);
		}

		WB_Normal3d n;
		if (!flat) {
			for (int i = 0; i < faces.size(); i++) {
				f = faces.get(i);
				if (!failedFaces.contains(f)) {
					n = _faceNormals.get(f.key());

					he = f.getHalfedge();
					do {
						final HE_Vertex v = he.getVertex();
						he.getVertex().add(n, d.value(v.x, v.y, v.z));
						he = he.getNextInFace();
					} while (he != f.getHalfedge());
				}

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
		selection.parent.resetFaceLabels();
		walls = new HE_Selection(selection.parent);
		extruded = new HE_Selection(selection.parent);
		if (selection.numberOfFaces() == 0) {
			return selection.parent;
		}

		_halfedgeNormals = new FastMap<Integer, WB_Normal3d>();
		_halfedgeEWs = new FastMap<Integer, Double>();
		if ((chamfer == 0) && flat) {
			return selection.parent;
		}

		HE_Face f;
		HE_Halfedge he;
		final List<HE_Face> selFaces = selection.getFacesAsList();
		_faceNormals = selection.parent.getKeyedFaceNormals();
		_faceCenters = selection.parent.getKeyedFaceCenters();
		final int nf = selFaces.size();
		for (int i = 0; i < nf; i++) {
			f = selFaces.get(i);
			he = f.getHalfedge();
			do {
				_halfedgeNormals.put(he.key(), he.getHalfedgeNormal());
				_halfedgeEWs
						.put(he.key(),
								(he.getHalfedgeDihedralAngle() < thresholdAngle) ? hardEdgeChamfer
										: chamfer);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

		}

		if (chamfer == 0) {
			return applyStraight(selection.parent, selFaces);
		}

		if ((relative == true) && (chamfer == 1)) {
			return applyPeaked(selection.parent, selFaces);
		}

		applyFlat(selection.parent, selFaces, flat && fuse);

		WB_Normal3d n;
		if (!flat) {
			for (int i = 0; i < selFaces.size(); i++) {
				f = selFaces.get(i);
				n = _faceNormals.get(f.key());

				he = f.getHalfedge();
				do {
					final HE_Vertex v = he.getVertex();
					he.getVertex().add(n, d.value(v.x, v.y, v.z));
					he = he.getNextInFace();
				} while (he != f.getHalfedge());

			}
		}
		return selection.parent;

	}

	/**
	 * Apply straight extrusion.
	 *
	 * @param mesh
	 * @param faces
	 * @return mesh
	 */
	private HE_Mesh applyStraight(final HE_Mesh mesh, final List<HE_Face> faces) {
		final int nf = faces.size();
		final boolean[] visited = new boolean[nf];
		for (int i = 0; i < nf; i++) {
			applyStraightToOneFace(i, faces, mesh, visited);
		}
		return mesh;
	}

	/**
	 * Apply straight extrusion to one face.
	 *
	 * @param id 
	 * @param selfaces
	 * @param mesh 
	 * @param visited
	 * @return true, if successful
	 */
	private boolean applyStraightToOneFace(final int id,
			final List<HE_Face> selfaces, final HE_Mesh mesh,
			final boolean[] visited) {

		if (visited[id]) {
			return false;
		}
		final HE_Face f = selfaces.get(id);
		final WB_Normal3d n = _faceNormals.get(f.key());

		final List<HE_Face> neighborhood = new FastList<HE_Face>();
		neighborhood.add(f);
		f.setLabel(1);
		visited[id] = true;
		int no = 0;
		int nn = 1;
		do {
			nn = neighborhood.size();
			for (int i = no; i < nn; i++) {
				final HE_Face fi = neighborhood.get(i);
				final List<HE_Face> faces = fi.getNeighborFaces();
				for (int j = 0; j < faces.size(); j++) {
					final HE_Face fj = faces.get(j);
					if ((_faceNormals.get(fi.key()) != null)
							&& (_faceNormals.get(fj.key()) != null)) {

						if (_faceNormals.get(fi.key()).isParallel(
								_faceNormals.get(fj.key()))) {
							final int ij = selfaces.indexOf(fj);
							if (ij >= 0) {
								if (!neighborhood.contains(fj)) {
									neighborhood.add(fj);
									fj.setLabel(1);
								}
								visited[ij] = true;
							}
						}
					}
				}
			}
			no = nn;

		} while (neighborhood.size() > nn);

		extruded.addFaces(neighborhood);

		final List<HE_Halfedge> outerHalfedges = new FastList<HE_Halfedge>();
		final List<HE_Halfedge> halfedges = new FastList<HE_Halfedge>();
		final List<HE_Vertex> vertices = new FastList<HE_Vertex>();
		final List<HE_Halfedge> pairHalfedges = new FastList<HE_Halfedge>();
		final List<HE_Vertex> outerVertices = new FastList<HE_Vertex>();
		final List<HE_Vertex> extOuterVertices = new FastList<HE_Vertex>();
		final List<HE_Edge> newEdges = new FastList<HE_Edge>();

		for (int i = 0; i < neighborhood.size(); i++) {
			HE_Halfedge he = neighborhood.get(i).getHalfedge();
			do {
				final HE_Face fp = he.getPair().getFace();
				if ((fp == null) || (!neighborhood.contains(fp))) {
					outerHalfedges.add(he);
				}
				halfedges.add(he);
				if (!vertices.contains(he.getVertex())) {
					vertices.add(he.getVertex());
				}
				he = he.getNextInFace();
			} while (he != neighborhood.get(i).getHalfedge());
		}

		for (int i = 0; i < outerHalfedges.size(); i++) {
			pairHalfedges.add(outerHalfedges.get(i).getPair());
			outerVertices.add(outerHalfedges.get(i).getVertex());
			final HE_Vertex eov = new HE_Vertex(outerHalfedges.get(i)
					.getVertex());

			eov.add(n, d.value(eov.x, eov.y, eov.z));
			extOuterVertices.add(eov);
			newEdges.add(new HE_Edge());

		}
		mesh.addEdges(newEdges);
		mesh.addVertices(extOuterVertices);
		for (int i = 0; i < vertices.size(); i++) {
			final HE_Vertex v = vertices.get(i);
			if (!outerVertices.contains(v)) {
				v.add(n, d.value(v.x, v.y, v.z));
			}

		}

		for (int i = 0; i < halfedges.size(); i++) {
			final HE_Halfedge he = halfedges.get(i);
			final int ovi = outerVertices.indexOf(he.getVertex());
			if (ovi >= 0) {
				he.setVertex(extOuterVertices.get(ovi));
				extOuterVertices.get(ovi).setHalfedge(he);
			}

		}
		for (int c = 0; c < outerHalfedges.size(); c++) {
			final HE_Face fNew = new HE_Face();

			walls.add(fNew);
			fNew.setLabel(2);
			final HE_Halfedge heOrig1 = outerHalfedges.get(c);
			final HE_Halfedge heOrig2 = pairHalfedges.get(c);
			final HE_Halfedge heNew1 = new HE_Halfedge();
			final HE_Halfedge heNew2 = new HE_Halfedge();
			final HE_Halfedge heNew3 = new HE_Halfedge();
			final HE_Halfedge heNew4 = new HE_Halfedge();
			final HE_Edge eNew = new HE_Edge();

			HE_Halfedge hen = heOrig1.getNextInFace();
			int cp = -1;
			do {
				cp = outerHalfedges.indexOf(hen);
				hen = hen.getPair().getNextInFace();
			} while ((hen != heOrig1.getNextInFace()) && (cp == -1));

			final HE_Vertex v1 = outerVertices.get(c);
			final HE_Vertex v2 = outerVertices.get(cp);
			final HE_Vertex v4 = extOuterVertices.get(c);
			final HE_Vertex v3 = extOuterVertices.get(cp);
			heNew1.setVertex(v1);
			v1.setHalfedge(heNew1);
			heNew1.setFace(fNew);
			fNew.setHalfedge(heNew1);
			heNew1.setEdge(heOrig2.getEdge());
			heNew1.getEdge().setHalfedge(heNew1);
			heNew1.setPair(heOrig2);

			heNew1.setNext(heNew2);
			heNew2.setVertex(v2);
			heNew2.setEdge(newEdges.get(cp));
			if (heNew2.getEdge().getHalfedge() == null) {
				heNew2.getEdge().setHalfedge(heNew2);
			} else {
				heNew2.setPair(heNew2.getEdge().getHalfedge());
				heNew2.getEdge().getHalfedge().setPair(heNew2);
			}
			v2.setHalfedge(heNew2);
			heNew2.setFace(fNew);
			heNew2.setNext(heNew3);
			heNew3.setVertex(v3);
			v3.setHalfedge(heNew3);
			heNew3.setFace(fNew);
			heNew3.setEdge(eNew);
			heOrig1.setEdge(eNew);
			eNew.setHalfedge(heNew3);
			heNew3.setPair(heOrig1);
			heOrig1.setPair(heNew3);
			heNew3.setNext(heNew4);
			heNew4.setVertex(v4);
			v4.setHalfedge(heNew4);
			heNew4.setFace(fNew);
			heNew4.setNext(heNew1);
			heNew4.setEdge(newEdges.get(c));
			if (heNew4.getEdge().getHalfedge() == null) {
				heNew4.getEdge().setHalfedge(heNew4);
			} else {
				heNew4.setPair(heNew4.getEdge().getHalfedge());
				heNew4.getEdge().getHalfedge().setPair(heNew4);
			}
			heOrig1.setVertex(v4);
			mesh.add(fNew);
			mesh.add(eNew);
			mesh.add(heNew1);
			mesh.add(heNew2);
			mesh.add(heNew3);
			mesh.add(heNew4);
		}

		return true;

	}

	/**
	 * Apply peaked extrusion.
	 *
	 * @param mesh
	 * @param faces
	 * @return mesh
	 */
	private HE_Mesh applyPeaked(final HE_Mesh mesh, final List<HE_Face> faces) {
		final int nf = faces.size();
		HE_Face f;
		for (int i = 0; i < nf; i++) {
			f = faces.get(i);
			_faceCenters.put(f.key(), f.getFaceCenter());

		}

		for (int i = 0; i < nf; i++) {
			applyPeakToOneFace(i, faces, mesh);
		}

		return mesh;
	}

	/**
	 * Apply peaked extrusion to one face.
	 *
	 * @param id
	 * @param selFaces
	 * @param mesh
	 * @param pd
	 */
	private void applyPeakToOneFace(final int id, final List<HE_Face> selFaces,
			final HE_Mesh mesh) {
		final HE_Face f = selFaces.get(id);
		final WB_Normal3d n = _faceNormals.get(f.key());
		final WB_Point3d fc = _faceCenters.get(f.key());

		walls.add(f);
		f.setLabel(4);
		final HE_Face[] newFaces = mesh.triSplitFace(f,
				fc.add(n.mult(d.value(fc.x, fc.y, fc.z)))).getFacesAsArray();
		for (final HE_Face newFace : newFaces) {
			newFace.setLabel(4);
		}
		walls.addFaces(newFaces);

	}

	/**
	 * Apply flat extrusion.
	 *
	 * @param mesh
	 * @param faces
	 * @return mesh
	 */
	private HE_Mesh applyFlat(final HE_Mesh mesh, final List<HE_Face> faces,
			final boolean fuse) {

		final HE_Selection sel = new HE_Selection(mesh);
		sel.addFaces(faces);
		sel.collectEdges();

		final List<HE_Edge> originalEdges = sel.getEdgesAsList();
		final int nf = faces.size();
		for (int i = 0; i < nf; i++) {
			// applyFlatToOneFace(i, faces, mesh);

			if (!applyFlatToOneFace(i, faces, mesh)) {
				failedFaces.add(faces.get(i));
			}

		}

		if (fuse) {
			for (int i = 0; i < originalEdges.size(); i++) {
				final HE_Edge e = originalEdges.get(i);

				final HE_Face f1 = e.getHalfedge().getFace();
				final HE_Face f2 = e.getHalfedge().getPair().getFace();
				if ((f1 != null) && (f2 != null)) {
					if ((f1.getLabel() == 2) && (f2.getLabel() == 2)) {
						if ((f1.getFaceNormal().cross((f2.getFaceNormal()))
								.mag2()) < sin2FA) {
							final HE_Face f = mesh.deleteEdge(e);
							if (f != null) {
								f.setLabel(3);
							}
						}
					}
				}

			}
		}

		return mesh;
	}

	/**
	 * Apply flat extrusion to one face.
	 *
	 * @param id
	 * @param selFaces
	 * @param mesh
	 * @return true, if successful
	 */
	private boolean applyFlatToOneFace(final int id,
			final List<HE_Face> selFaces, final HE_Mesh mesh) {
		final HE_Face f = selFaces.get(id);
		final WB_Point3d fc = _faceCenters.get(f.key());
		final List<HE_Vertex> faceVertices = new FastList<HE_Vertex>();
		final List<HE_Halfedge> faceHalfedges = new FastList<HE_Halfedge>();
		final List<WB_Normal3d> faceHalfedgeNormals = new FastList<WB_Normal3d>();
		final List<WB_Point3d> faceEdgeCenters = new FastList<WB_Point3d>();
		final List<HE_Vertex> extFaceVertices = new FastList<HE_Vertex>();
		HE_Halfedge he = f.getHalfedge();
		do {
			faceVertices.add(he.getVertex());
			faceHalfedges.add(he);
			faceHalfedgeNormals.add(_halfedgeNormals.get(he.key()));
			faceEdgeCenters.add(he.getHalfedgeCenter());
			extFaceVertices.add(he.getVertex().get());
			he = he.getNextInFace();
		} while (he != f.getHalfedge());

		boolean isPossible = true;
		final int n = faceVertices.size();
		if (relative == true) {
			double ch;
			for (int i = 0; i < n; i++) {
				final HE_Vertex v = faceVertices.get(i);
				final WB_Point3d diff = fc.subAndCopy(v);
				he = faceHalfedges.get(i);
				ch = Math.max(_halfedgeEWs.get(he.key()),
						_halfedgeEWs.get(he.getPrevInFace().key()));
				diff.mult(ch);
				diff.add(v);
				extFaceVertices.get(i).set(diff);
			}
		} else {
			final double[] d = new double[n];
			for (int i = 0; i < n; i++) {
				d[i] = _halfedgeEWs.get(faceHalfedges.get(i).key());
			}
			if ((chamfer > 0) && (f.getFaceType() == WB_PolygonType2D.CONVEX)) {
				final WB_Point3d[] vPos = new WB_Point3d[n];
				for (int i = 0; i < n; i++) {
					final HE_Vertex v = faceVertices.get(i);
					vPos[i] = new WB_Point3d(v);
				}
				final WB_ExplicitPolygon poly = new WB_ExplicitPolygon(vPos, n);
				poly.trimConvexPolygon(d);
				if (poly.n == n) {
					final int inew = poly.closestIndex(faceVertices.get(0));

					for (int i = 0; i < n; i++) {
						extFaceVertices.get(i).set(
								poly.getPoint((inew + i) % n));
					}
				} else if (poly.n > 2) {
					for (int i = 0; i < n; i++) {
						extFaceVertices.get(i).set(
								poly.closestPoint(faceVertices.get(i)));
					}
				} else {
					isPossible = false;
				}
			} else {
				WB_Point3d v1 = new WB_Point3d(faceVertices.get(n - 1));
				WB_Point3d v2 = new WB_Point3d(faceVertices.get(0));
				for (int i = 0, j = n - 1; i < n; j = i, i++) {
					final WB_Normal3d n1 = faceHalfedgeNormals.get(j);
					final WB_Normal3d n2 = faceHalfedgeNormals.get(i);
					final WB_Point3d v3 = faceVertices.get((i + 1) % n);
					final WB_ExplicitSegment S1 = new WB_ExplicitSegment(
							v1.addAndCopy(n1, d[j]), v2.addAndCopy(n1, d[j]));
					final WB_ExplicitSegment S2 = new WB_ExplicitSegment(
							v2.addAndCopy(n2, d[i]), v3.addAndCopy(n2, d[i]));

					final WB_IntersectionResult ir = WB_Intersection
							.getIntersection(S1, S2);
					final WB_Point3d p = (ir.dimension == 0) ? (WB_Point3d) ir.object
							: ((WB_Segment) ir.object).getCenter();
					extFaceVertices.get(i).set(p);
					v1 = v2;
					v2 = v3;

				}
			}
		}
		if (isPossible) {
			extruded.add(f);
			f.setLabel(1);
			final List<HE_Edge> newEdges = new FastList<HE_Edge>();
			final List<HE_Edge> newEdges2 = new FastList<HE_Edge>();
			he = f.getHalfedge();
			do {
				he = he.getNextInFace();
				newEdges.add(new HE_Edge());
			} while (he != f.getHalfedge());
			mesh.addEdges(newEdges);
			int c = 0;
			he = f.getHalfedge();
			do {
				final HE_Face fNew = new HE_Face();
				walls.add(fNew);
				fNew.setLabel(2);
				final HE_Halfedge heOrig1 = he;
				final HE_Halfedge heOrig2 = he.getPair();
				final HE_Halfedge heNew1 = new HE_Halfedge();
				final HE_Halfedge heNew2 = new HE_Halfedge();
				final HE_Halfedge heNew3 = new HE_Halfedge();
				final HE_Halfedge heNew4 = new HE_Halfedge();
				final HE_Edge eNew = new HE_Edge();
				newEdges2.add(eNew);
				final int cp = (c + 1) % faceVertices.size();
				final HE_Vertex v1 = faceVertices.get(c);
				final HE_Vertex v2 = faceVertices.get(cp);
				final HE_Vertex v4 = extFaceVertices.get(c);
				final HE_Vertex v3 = extFaceVertices.get(cp);
				heNew1.setVertex(v1);
				v1.setHalfedge(heNew1);
				heNew1.setFace(fNew);
				fNew.setHalfedge(heNew1);
				heNew1.setEdge(heOrig2.getEdge());
				heNew1.getEdge().setHalfedge(heNew1);
				heNew1.setPair(heOrig2);

				heNew1.setNext(heNew2);
				heNew2.setVertex(v2);
				heNew2.setEdge(newEdges.get(cp));
				if (heNew2.getEdge().getHalfedge() == null) {
					heNew2.getEdge().setHalfedge(heNew2);
				} else {
					heNew2.setPair(heNew2.getEdge().getHalfedge());
					heNew2.getEdge().getHalfedge().setPair(heNew2);
				}
				v2.setHalfedge(heNew2);
				heNew2.setFace(fNew);
				heNew2.setNext(heNew3);
				heNew3.setVertex(v3);
				v3.setHalfedge(heNew3);
				heNew3.setFace(fNew);
				heNew3.setEdge(eNew);
				heOrig1.setEdge(eNew);
				eNew.setHalfedge(heNew3);
				heNew3.setPair(heOrig1);
				heOrig1.setPair(heNew3);
				heNew3.setNext(heNew4);
				heNew4.setVertex(v4);
				v4.setHalfedge(heNew4);
				heNew4.setFace(fNew);
				heNew4.setNext(heNew1);
				heNew4.setEdge(newEdges.get(c));
				if (heNew4.getEdge().getHalfedge() == null) {
					heNew4.getEdge().setHalfedge(heNew4);
				} else {
					heNew4.setPair(heNew4.getEdge().getHalfedge());
					heNew4.getEdge().getHalfedge().setPair(heNew4);
				}
				heOrig1.setVertex(v4);
				mesh.add(fNew);
				mesh.add(eNew);
				mesh.add(v3);
				mesh.add(heNew1);
				mesh.add(heNew2);
				mesh.add(heNew3);
				mesh.add(heNew4);
				he = he.getNextInFace();
				c++;
			} while (he != f.getHalfedge());
			final List<HE_Edge> edgesToRemove = new FastList<HE_Edge>();
			for (int i = 0; i < newEdges2.size(); i++) {
				final HE_Edge e = newEdges2.get(i);
				if (WB_Epsilon.isZeroSq(WB_Distance.sqDistance(
						e.getStartVertex(), e.getEndVertex()))) {
					edgesToRemove.add(e);
				}
			}
			for (int i = 0; i < edgesToRemove.size(); i++) {
				mesh.collapseEdge(edgesToRemove.get(i));
			}

		}
		return isPossible;
	}
}
