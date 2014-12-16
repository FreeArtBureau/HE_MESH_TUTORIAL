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
package wblut.geom;

// TODO: Auto-generated Javadoc
/**
 *  3D Ray.
 */
public class WB_Ray extends WB_Linear {
	public static final WB_Ray	X	= new WB_Ray(0, 0, 0, 1, 0, 0);
	public static final WB_Ray	Y	= new WB_Ray(0, 0, 0, 0, 1, 0);
	public static final WB_Ray	Z	= new WB_Ray(0, 0, 0, 0, 0, 1);

	/**
	 * Instantiates a new WB_Ray.
	 */
	public WB_Ray() {
		origin = new WB_Point3d();
		direction = new WB_Vector3d(1, 0, 0);

	}

	/**
	 * Instantiates a new WB_Ray.
	 *
	 * @param o origin
	 * @param d direction
	 */
	public WB_Ray(final WB_Point3d o, final WB_Vector3d d) {
		origin = o.get();
		direction = d.get();
		direction.normalize();

	}

	/**
	 * Set ray.
	 *
	 * @param o origin
	 * @param d direction
	 */
	@Override
	public void set(final WB_Point3d o, final WB_Vector3d d) {
		origin = o.get();
		direction = d.get();
		direction.normalize();

	}

	/**
	 * Instantiates a new WB_Ray.
	 *
	 * @param p1 origin
	 * @param p2 point on ray
	 */
	public WB_Ray(final WB_Point3d p1, final WB_Point3d p2) {
		origin = p1.get();
		direction = p2.subToVector(p1);
		direction.normalize();

	}

	/**
	 * Instantiates a new WB_Ray.
	 *
	 * @param p1x first point on line
	 * @param p1y first point on line
	 * @param p1z first point on line
	 * @param p2x second point on line
	 * @param p2y second point on line
	 * @param p2z second point on line
	 */
	public WB_Ray(final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		origin = new WB_Point3d(p1x, p1y, p1z);
		direction = new WB_Vector3d(p2x - p1x, p2y - p1y, p2z - p1z);
		direction.normalize();

	}

	/**
	 * Set ray.
	 *
	 * @param p1 origin
	 * @param p2 point on ray
	 */
	@Override
	public void set(final WB_Point3d p1, final WB_Point3d p2) {
		origin = p1.get();
		direction = p2.subToVector(p1);
		direction.normalize();

	}

}