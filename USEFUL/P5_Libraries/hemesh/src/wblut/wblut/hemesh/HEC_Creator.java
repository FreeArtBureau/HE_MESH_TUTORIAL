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

import java.io.IOException;
import java.util.Iterator;


import processing.core.PApplet;
import processing.core.PGraphics3D;
import wblut.WB_Epsilon;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;

/**
 * Abstract base class for mesh creation. Implementation should return a valid HE_Mesh.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public abstract class HEC_Creator {

	/** Calling applet. */
	public PApplet			home;

	/** Center. */
	protected WB_Point3d	center;

	/** Rotation angle about Z-axis. */
	protected double		zangle;

	/** Z-axis. */
	protected WB_Vector3d	zaxis;

	/** Override. */
	protected boolean		override;

	/** Use applet model coordinates. */
	protected boolean		toModelview;

	/** Base Z-axis. */
	protected WB_Vector3d	Z;

	/**
	 * Constructor.
	 */
	public HEC_Creator() {
		center = new WB_Point3d();
		zaxis = WB_Vector3d.Z();
		Z = WB_Vector3d.Z();
		toModelview = false;
	}

	/**
	 * Set center of mesh.
	 * @param x x-coordinate of center
	 * @param y y-coordinate of center
	 * @param z z-coordinate of center
	 * @return self
	 */
	public HEC_Creator setCenter(final double x, final double y, final double z) {
		center.set(x, y, z);
		return this;
	}

	/**
	 * Set center of mesh.
	 * @param c center
	 * @return self
	 */
	public HEC_Creator setCenter(final WB_Point3d c) {
		center.set(c);
		return this;
	}

	/**
	 * Rotation of mesh about local Z-axis.
	 * @param a angle 
	 * @return self
	 */
	public HEC_Creator setZAngle(final double a) {
		zangle = a;
		return this;
	}

	/**
	 * Orientation of local Z-axis of mesh.
	 * @param x x-coordinate of axis vector
	 * @param y y-coordinate of axis vector
	 * @param z z-coordinate of axis vector
	 * @return self
	 */
	public HEC_Creator setZAxis(final double x, final double y, final double z) {
		zaxis.set(x, y, z);
		zaxis.normalize();
		return this;
	}

	/**
	 * Local Z-axis of mesh.
	 * @param p0x x-coordinate of first point on axis
	 * @param p0y y-coordinate of first point on axis
	 * @param p0z z-coordinate of first point on axis
	 * @param p1x x-coordinate of second point on axis
	 * @param p1y y-coordinate of second point on axis
	 * @param p1z z-coordinate of second point on axis
	 * @return self
	 */
	public HEC_Creator setAxis(final double p0x, final double p0y,
			final double p0z, final double p1x, final double p1y,
			final double p1z) {
		zaxis.set(p1x - p0x, p1y - p0y, p1z - p0z);
		zaxis.normalize();
		return this;
	}

	/**
	 * Orientation of local Z-axis of mesh.
	 * @param p axis vector
	 * @return self
	 */
	public HEC_Creator setAxis(final WB_Point3d p) {
		zaxis.set(p);
		zaxis.normalize();
		return this;
	}

	/**
	 * Local Z-axis of mesh.
	 * @param p0 first point on axis
	 * @param p1 second point on axis
	 * @return self
	 */
	public HEC_Creator setAxis(final WB_Point3d p0, final WB_Point3d p1) {
		zaxis.set(p1.subAndCopy(p0));
		zaxis.normalize();
		return this;
	}

	/**
	 * Use the applet's modelview coordinates.
	 * @param home calling applet, typically "this"
	 * @return self
	 */
	public HEC_Creator setToModelview(final PApplet home) {
		this.home = home;
		toModelview = true;
		return this;
	}

	/**
	 * Use absolute coordinates.
	 * @return self
	 */
	public HEC_Creator setToWorldview() {
		home = null;
		toModelview = false;
		return this;
	}

	/**
	 * Creates the base.
	 *
	 * @return HE_Mesh
	 * @throws IOException 
	 */
	protected abstract HE_Mesh createBase();

	/**
	 * Generate a mesh, move to center and orient along axis.
	 * 
	 * @return HE_Mesh 
	 * @throws IOException 
	 */
	public final HE_Mesh create() {
		final HE_Mesh base = createBase();
		if (!override) {

			if (zangle != 0) {
				base.rotateAboutAxis(zangle, center.x, center.y, center.z,
						center.x, center.y, center.z + 1);
			}

			final WB_Vector3d tmp = zaxis.cross(Z);
			if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
				base.rotateAboutAxis(-Math.acos(zaxis.dot(Z)), center.x,
						center.y, center.z, center.x + tmp.x, center.y + tmp.y,
						center.z + tmp.z);
			} else if (zaxis.dot(Z) < -1 + WB_Epsilon.EPSILON) {
				base.scale(1, 1, -1);
			}
			base.moveTo(center);
		}
		float cx, cy, cz;
		HE_Vertex v;
		if (toModelview) {
			if (home.g instanceof PGraphics3D) {
				final Iterator<HE_Vertex> vItr = base.vItr();
				while (vItr.hasNext()) {
					v = vItr.next();
					cx = (float) v.x;
					cy = (float) v.y;
					cz = (float) v.z;
					v.set(home.modelX(cx, cy, cz), home.modelY(cx, cy, cz),
							home.modelZ(cx, cy, cz));
				}
			}
		}
		return base;
	}

}
