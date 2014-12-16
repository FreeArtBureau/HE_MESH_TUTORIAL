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
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;



// TODO: Auto-generated Javadoc
/**
 * Bend a mesh. Determined by a ground plane, a bend axis and an angle factor.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Bend extends HEM_Modifier {

	/** Ground plane. */
	private WB_Plane	groundPlane;

	/** Bend axis. */
	private WB_Line		bendAxis;

	/** Angle factor. */
	private double		angleFactor;

	/** Positive side of plane only? */
	private boolean		posOnly;

	/**
	 * Instantiates a new HEM_Bend.
	 */
	public HEM_Bend() {
		super();
	}

	/**
	 * Set ground plane.
	 *
	 * @param P ground plane
	 * @return self
	 */
	public HEM_Bend setGroundPlane(final WB_Plane P) {
		groundPlane = P;
		return this;
	}

	public HEM_Bend setGroundPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set bend axis.
	 *
	 * @param a bend axis
	 * @return self
	 */
	public HEM_Bend setBendAxis(final WB_Line a) {
		bendAxis = a;
		return this;
	}

	public HEM_Bend setBendAxis(final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		bendAxis = new WB_Line(p1x, p1y, p1z, p2x, p2y, p2z);
		return this;
	}

	/**
	 * Set angle factor, ratio of bend angle in degrees to distance to ground plane.
	 *
	 * @param f direction
	 * @return self
	 */
	public HEM_Bend setAngleFactor(final double f) {
		angleFactor = f * (Math.PI / 180);
		return this;
	}

	/**
	 * Positive only? Only apply modifier to positive side of ground plane.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_Bend setPosOnly(final boolean b) {
		posOnly = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > 0)) {
					v.rotateAboutAxis(d * angleFactor, bendAxis.getOrigin(),
							bendAxis.getOrigin().addAndCopy(bendAxis.getDirection()));
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
		if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
			selection.collectVertices();
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = selection.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance.distance(v, groundPlane);
				if (!posOnly || (d > 0)) {
					v.rotateAboutAxis(d * angleFactor, bendAxis.getOrigin(),
							bendAxis.getOrigin().addAndCopy(bendAxis.getDirection()));
				}
			}
		}
		return selection.parent;
	}

}
