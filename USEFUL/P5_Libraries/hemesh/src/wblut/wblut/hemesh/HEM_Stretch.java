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

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;



/**
 * Stretch and compress a mesh. Determined by a ground plane, a stretch factor and a compression factor.
 * Most commonly, the ground plane normal is the stretch direction.
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Stretch extends HEM_Modifier {

	/** Ground plane. */
	private WB_Plane	groundPlane;

	/** Stretch direction. */
	private WB_Vector3d	stretchDirection;

	/** Stretch factor. */
	private double		stretchFactor;

	/** Compression factor. */
	private double		compressionFactor;

	/** Modify only positive side of ground plane? */
	private boolean		posOnly;

	/**
	 * Instantiates a new HEM_Stretch.
	 */
	public HEM_Stretch() {
		super();
	}

	/**
	 * Set ground plane.
	 *
	 * @param P ground plane
	 * @return self
	 */
	public HEM_Stretch setGroundPlane(final WB_Plane P) {
		groundPlane = P;
		return this;
	}

	public HEM_Stretch setGroundPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set stretch factor along stretch direction.
	 *
	 * @param f the f
	 * @return self
	 */
	public HEM_Stretch setStretchFactor(final double f) {
		stretchFactor = f;
		compressionFactor = Math.sqrt(f);
		return this;
	}

	/**
	 * Set compression factor perpendicular to stretch direction.
	 *
	 * @param f the f
	 * @return self
	 */
	public HEM_Stretch setCompressionFactor(final double f) {
		if (f != 0) {
			compressionFactor = f;
		}
		return this;
	}

	/**
	 * Positive only? Only apply modifier to positive side of ground plane.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Stretch setPosOnly(final boolean b) {
		posOnly = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if ((groundPlane != null) && (stretchDirection == null)) {
			stretchDirection = new WB_Vector3d(groundPlane.getNormal());
		}

		if ((groundPlane != null) && (stretchFactor != 0)
				&& (compressionFactor != 0)) {
			final WB_Line L = new WB_Line(groundPlane.getOrigin(),
					stretchDirection);
			WB_Point3d p;
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			HE_Vertex v;
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > WB_Epsilon.EPSILON)) {
					p = WB_Intersection.closestPoint(v, groundPlane);
					v.sub(p);
					v.mult(stretchFactor);
					v.add(p);
					p = WB_Intersection.closestPoint(v, L);
					v.sub(p);
					v.mult(1 / compressionFactor);
					v.add(p);
				}
			}
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if ((groundPlane != null) && (stretchDirection == null)) {
			stretchDirection = new WB_Vector3d(groundPlane.getNormal());
		}

		if ((groundPlane != null)
				&& ((stretchFactor != 0) || compressionFactor != 0)) {
			final WB_Line L = new WB_Line(groundPlane.getOrigin(),
					stretchDirection);
			WB_Point3d p;
			final Iterator<HE_Vertex> vItr = selection.vItr();
			HE_Vertex v;
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > WB_Epsilon.EPSILON)) {
					p = WB_Intersection.closestPoint(v, groundPlane);
					v.sub(p);
					v.mult(stretchFactor);
					v.add(p);
					p = WB_Intersection.closestPoint(v, L);
					v.sub(p);
					v.mult(1 / compressionFactor);
					v.add(p);
				}
			}
		}
		return selection.parent;
	}

}
