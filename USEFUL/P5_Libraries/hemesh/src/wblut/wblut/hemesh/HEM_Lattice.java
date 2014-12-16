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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wblut.geom.WB_ExplicitPolygon;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * Turns a solid into a rudimentary ribbed structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Lattice extends HEM_Modifier {

	/** Depth. */
	private double	d;

	/** Width. */
	private double	sew;
	private double	hew;
	/** Hard edge threshold angle. */
	private double	thresholdAngle;

	/** Fuse coplanar faces? */
	private boolean	fuse;

	/** Threshold angle for fusion. */
	private double	fuseAngle;

	/**
	 * Instantiates a new HEM_Lattice.
	 */
	public HEM_Lattice() {
		super();
		d = 0;
		sew = 0;
		thresholdAngle = -1;
		fuseAngle = Math.PI / 36;
		fuse = false;
	}

	/**
	 * Set depth of beams.
	 *
	 * @param d depth of beams
	 * @return self
	 */
	public HEM_Lattice setDepth(final double d) {
		this.d = d;
		return this;
	}

	/**
	 * Set width of beams.
	 *
	 * @param w width of beam
	 * @return slef
	 */
	public HEM_Lattice setWidth(final double w) {
		sew = 0.5 * w;
		hew = w;
		return this;
	}

	public HEM_Lattice setWidth(final double w, final double hew) {
		sew = 0.5 * w;
		this.hew = hew;
		return this;
	}

	/**
	 * Set fuse option.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Lattice setFuse(final boolean b) {
		fuse = b;
		return this;
	}

	/**
	 * Set threshold angle for hard edges.
	 *
	 * @param a angle
	 * @return self
	 */
	public HEM_Lattice setThresholdAngle(final double a) {
		thresholdAngle = a;
		return this;
	}

	/**
	 * Set threshold angle for face fusion.
	 *
	 * @param a angle
	 * @return self
	 */
	public HEM_Lattice setFuseAngle(final double a) {
		fuseAngle = a;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (d == 0) {
			return mesh;
		}
		if (sew == 0) {
			return mesh;
		}
		final HEM_Extrude extm = new HEM_Extrude().setDistance(0)
				.setRelative(false).setChamfer(sew).setFuse(fuse)
				.setHardEdgeChamfer(hew).setFuseAngle(fuseAngle)
				.setThresholdAngle(thresholdAngle);
		mesh.modify(extm);
		final HE_Mesh innerMesh = mesh.get();

		final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(-d);
		innerMesh.modify(expm);
		final HashMap<Integer, Integer> faceCorrelation = new HashMap<Integer, Integer>();
		final HashMap<Integer, Integer> heCorrelation = new HashMap<Integer, Integer>();
		final Iterator<HE_Face> fItr1 = mesh.fItr();
		final Iterator<HE_Face> fItr2 = innerMesh.fItr();
		HE_Face f1;
		HE_Face f2;
		while (fItr1.hasNext()) {
			f1 = fItr1.next();
			f2 = fItr2.next();
			faceCorrelation.put(f1.key(), f2.key());
		}
		final Iterator<HE_Halfedge> heItr1 = mesh.heItr();
		final Iterator<HE_Halfedge> heItr2 = innerMesh.heItr();
		HE_Halfedge he1;
		HE_Halfedge he2;
		while (heItr1.hasNext()) {
			he1 = heItr1.next();
			he2 = heItr2.next();
			if (he1.getFace() == null) {
				heCorrelation.put(he1.key(), he2.key());
			}
		}
		innerMesh.flipAllFaces();
		final int nf = mesh.numberOfFaces();
		final HE_Face[] origFaces = mesh.getFacesAsArray();
		mesh.addVertices(innerMesh.getVerticesAsArray());
		mesh.addFaces(innerMesh.getFacesAsArray());
		mesh.addEdges(innerMesh.getEdgesAsArray());
		mesh.addHalfedges(innerMesh.getHalfedgesAsArray());
		HE_Face fo;
		HE_Face fi;
		List<HE_Halfedge> hei;
		List<HE_Halfedge> heo;
		WB_Point3d[] viPos;
		WB_ExplicitPolygon poly;
		HE_Halfedge heoc, heic, heon, hein, heio, heoi;
		HE_Face fNew;
		for (int i = 0; i < nf; i++) {
			fo = origFaces[i];

			final Integer innerKey = faceCorrelation.get(fo.key());
			if (extm.extruded.contains(fo)) {
				fi = mesh.getFaceByKey(innerKey);
				final int nvo = fo.getFaceOrder();
				final int nvi = fi.getFaceOrder();

				hei = fi.getFaceHalfedges();
				viPos = new WB_Point3d[nvi];
				for (int j = 0; j < nvi; j++) {
					viPos[j] = new WB_Point3d(hei.get(j).getVertex());
				}
				poly = new WB_ExplicitPolygon(viPos, nvi);

				heo = fo.getFaceHalfedges();

				for (int j = 0; j < nvo; j++) {
					heoc = heo.get(j);
					heon = heo.get((j + 1) % nvo);

					final int cic = poly.closestIndex(heoc.getVertex());
					final int cin = poly.closestIndex(heon.getVertex());
					heic = hei.get(cin);

					hein = hei.get(cic);
					heio = new HE_Halfedge();
					heoi = new HE_Halfedge();
					fNew = new HE_Face();
					heoi.setVertex(heon.getVertex());
					heio.setVertex(hein.getVertex());
					heoc.setNext(heoi);
					heoc.setFace(fNew);
					if (cic == cin) {
						heoi.setNext(heio);
						heoi.setFace(fNew);
					} else {
						heoi.setNext(heic);
						heoi.setFace(fNew);
						heic.setNext(heio);
						heic.setFace(fNew);
					}

					heio.setNext(heoc);
					heio.setFace(fNew);
					fNew.setHalfedge(heoc);
					mesh.add(heio);
					mesh.add(heoi);
					mesh.add(fNew);
					mesh.remove(fo);
					mesh.remove(fi);
				}

			}
		}

		final Iterator<Map.Entry<Integer, Integer>> it = heCorrelation
				.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<Integer, Integer> pairs = it.next();
			he1 = mesh.getHalfedgeByKey(pairs.getKey());
			he2 = mesh.getHalfedgeByKey(pairs.getValue());
			heio = new HE_Halfedge();
			heoi = new HE_Halfedge();
			mesh.add(heio);
			mesh.add(heoi);
			heio.setVertex(he1.getPair().getVertex());
			heoi.setVertex(he2.getPair().getVertex());
			he1.setNext(heio);
			heio.setNext(he2);
			he2.setNext(heoi);
			heoi.setNext(he1);
			fNew = new HE_Face();
			mesh.add(fNew);
			fNew.setHalfedge(he1);
			he1.setFace(fNew);
			he2.setFace(fNew);
			heio.setFace(fNew);
			heoi.setFace(fNew);

		}

		mesh.pairHalfedges();

		return mesh;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if (d == 0) {
			return selection.parent;
		}
		if (sew == 0) {
			return selection.parent;
		}
		final HEM_Extrude extm = new HEM_Extrude().setDistance(0)
				.setRelative(false).setChamfer(sew).setFuse(fuse)
				.setHardEdgeChamfer(hew).setFuseAngle(fuseAngle)
				.setThresholdAngle(thresholdAngle);
		selection.parent.modifySelected(extm, selection);
		final HE_Mesh innerMesh = selection.parent.get();

		final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(-d);
		innerMesh.modify(expm);
		final HashMap<Integer, Integer> faceCorrelation = new HashMap<Integer, Integer>();
		final Iterator<HE_Face> fItr1 = selection.parent.fItr();
		final Iterator<HE_Face> fItr2 = innerMesh.fItr();
		HE_Face f1;
		HE_Face f2;
		while (fItr1.hasNext()) {
			f1 = fItr1.next();
			f2 = fItr2.next();
			faceCorrelation.put(f1.key(), f2.key());

		}

		innerMesh.flipAllFaces();
		final int nf = selection.parent.numberOfFaces();
		final HE_Face[] origFaces = selection.parent.getFacesAsArray();
		selection.parent.addVertices(innerMesh.getVerticesAsArray());
		selection.parent.addFaces(innerMesh.getFacesAsArray());
		selection.parent.addEdges(innerMesh.getEdgesAsArray());
		selection.parent.addHalfedges(innerMesh.getHalfedgesAsArray());
		HE_Face fo;
		HE_Face fi;
		List<HE_Halfedge> hei;
		List<HE_Halfedge> heo;
		WB_Point3d[] viPos;
		WB_ExplicitPolygon poly;
		HE_Halfedge heoc, heic, heon, hein, heio, heoi;
		HE_Face fNew;
		for (int i = 0; i < nf; i++) {
			fo = origFaces[i];

			final Integer innerKey = faceCorrelation.get(fo.key());
			if (extm.extruded.contains(fo)) {
				fi = selection.parent.getFaceByKey(innerKey);
				final int nvo = fo.getFaceOrder();
				final int nvi = fi.getFaceOrder();

				hei = fi.getFaceHalfedges();
				viPos = new WB_Point3d[nvi];
				for (int j = 0; j < nvi; j++) {
					viPos[j] = new WB_Point3d(hei.get(j).getVertex());
				}
				poly = new WB_ExplicitPolygon(viPos, nvi);

				heo = fo.getFaceHalfedges();

				for (int j = 0; j < nvo; j++) {
					heoc = heo.get(j);
					heon = heo.get((j + 1) % nvo);

					final int cic = poly.closestIndex(heoc.getVertex());
					final int cin = poly.closestIndex(heon.getVertex());
					heic = hei.get(cin);

					hein = hei.get(cic);
					heio = new HE_Halfedge();
					heoi = new HE_Halfedge();
					fNew = new HE_Face();
					heoi.setVertex(heon.getVertex());
					heio.setVertex(hein.getVertex());
					heoc.setNext(heoi);
					heoc.setFace(fNew);
					if (cic == cin) {
						heoi.setNext(heio);
						heoi.setFace(fNew);
					} else {
						heoi.setNext(heic);
						heoi.setFace(fNew);
						heic.setNext(heio);
						heic.setFace(fNew);
					}

					heio.setNext(heoc);
					heio.setFace(fNew);
					fNew.setHalfedge(heoc);
					selection.parent.add(heio);
					selection.parent.add(heoi);
					selection.parent.add(fNew);
					selection.parent.remove(fo);
					selection.parent.remove(fi);
				}

			}
		}
		selection.parent.pairHalfedges();

		return selection.parent;
	}

}
