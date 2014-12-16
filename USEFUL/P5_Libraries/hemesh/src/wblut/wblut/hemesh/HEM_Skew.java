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

import wblut.geom.WB_Distance;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * Skew a mesh. Determined by a ground plane, a skew direction and a skew factor.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Skew extends HEM_Modifier {

	/** Ground plane. */
	private WB_Plane	groundPlane;

	/** Skew direction. */
	private WB_Point3d		skewDirection;

	/** Skew factor. */
	private double		skewFactor;

	/** Only modify positive side of ground plane. */
	private boolean		posOnly;

	/**
	 * Instantiates a new HEM_Skew.
	 */
	public HEM_Skew() {
		super();
	}

	/**
	 * Set ground plane.
	 *
	 * @param P ground plane
	 * @return self
	 */
	public HEM_Skew setGroundPlane(final WB_Plane P) {
		groundPlane = P;
		return this;
	}

	public HEM_Skew setGroundPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set skew direction.
	 *
	 * @param p direction
	 * @return self
	 */
	public HEM_Skew setSkewDirection(final WB_Point3d p) {
		skewDirection = p.get();
		skewDirection.normalize();
		return this;
	}

	public HEM_Skew setSkewDirection(final double vx, final double vy,
			final double vz) {
		skewDirection = new WB_Point3d(vx, vy, vz);
		skewDirection.normalize();
		return this;
	}

	/**
	 * Set skew factor, ratio of skew distance to distance to ground plane.
	 *
	 * @param f direction
	 * @return self
	 */
	public HEM_Skew setSkewFactor(final double f) {
		skewFactor = f;
		return this;
	}

	/**
	 * Positive only? Only apply modifier to positive side of ground plane.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Skew setPosOnly(final boolean b) {
		posOnly = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if ((groundPlane != null) && (skewDirection != null)
				&& (skewFactor != 0)) {
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > 0)) {
					v.add(skewDirection.multAndCopy(d * skewFactor));
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
		if ((groundPlane != null) && (skewDirection != null)
				&& (skewFactor != 0)) {
			selection.collectVertices();
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = selection.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > 0)) {
					v.add(skewDirection.multAndCopy(d * skewFactor));
				}
			}
		}
		return selection.parent;
	}
}
