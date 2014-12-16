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
import java.util.Iterator;
import java.util.List;

import wblut.geom.WB_ClassifyPolygonToPlane;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;




// TODO: Auto-generated Javadoc
/**
 * Planar cut of a mesh. Faces on positive side of cut plane are removed.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Mirror extends HEM_Modifier {

	/** Mirror plane. */
	private WB_Plane P;

	/** Keep center of cut mesh? */
	private boolean keepCenter = false;

	/** Reverse mirror plane. */
	private boolean reverse = false;

	/** Store cut faces */
	public HE_Selection cut;

	private double offset;

	/**
	 * Set offset.
	 * 
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEM_Mirror setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_Slice.
	 */
	public HEM_Mirror() {
		super();
	}

	/**
	 * Set mirror plane.
	 * 
	 * @param P
	 *            mirror plane
	 * @return self
	 */
	public HEM_Mirror setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	public HEM_Mirror setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set reverse option.
	 * 
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Mirror setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Set option to reset mesh center.
	 * 
	 * @param b
	 *            true, false;
	 * @return self
	 */

	public HEM_Mirror setKeepCenter(final Boolean b) {
		keepCenter = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		cut = new HE_Selection(mesh);
		// no plane defined
		if (P == null) {
			return mesh;
		}

		// empty mesh
		if (mesh.numberOfVertices() == 0) {
			return mesh;
		}

		final WB_Plane lP = P.get();
		if (reverse) {
			lP.flipNormal();
		}
		lP.set(lP.getNormal(), lP.d() + offset);
		HEM_SliceSurface ss;
		ss = new HEM_SliceSurface().setPlane(lP);
		mesh.modify(ss);

		cut = ss.cut;
		final HE_Selection newFaces = new HE_Selection(mesh);
		HE_Face face;
		Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();

			final WB_ClassifyPolygonToPlane cptp = WB_Plane
					.classifyPolygonToPlane(face.toPolygon(), lP);
			if ((cptp == WB_ClassifyPolygonToPlane.POLYGON_IN_FRONT_OF_PLANE)
					|| (cptp == WB_ClassifyPolygonToPlane.POLYGON_ON_PLANE)) {
				newFaces.add(face);
			} else {
				if (cut.contains(face)) {
					cut.remove(face);
				}
			}
		}

		mesh.replaceFaces(newFaces.getFacesAsArray());
		cut.cleanSelection();
		mesh.cleanUnusedElementsByFace();
		final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getFaceOrder() < 3) {
				facesToRemove.add(face);
			}
		}
		mesh.removeFaces(facesToRemove);
		mesh.cleanUnusedElementsByFace();
		mesh.capHalfedges();
		HE_Mesh mirrormesh = mesh.get();
		List<HE_Vertex> vertices = mirrormesh.getVerticesAsList();
		for (HE_Vertex v : vertices) {
			WB_Point3d p = WB_Intersection.closestPoint(v, lP);
			WB_Vector3d dv = v.subToVector(p);
			v.add(dv, -2);
		}
		mirrormesh.flipAllFaces();

		mesh.add(mirrormesh);
		
mesh.pairHalfedges();
		if (!keepCenter) {
			mesh.resetCenter();
		}
		return mesh;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return apply(selection.parent);
	}

}
