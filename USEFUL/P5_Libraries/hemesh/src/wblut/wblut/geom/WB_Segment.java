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
 * 3D line segment.
 */
public interface WB_Segment {

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @return point
	 */

	public WB_Point3d getPoint(final double t);

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @param result WB_Point to store result
	 */

	public void getPointInto(final double t, final WB_Point3d result);

	/**
	 * Get origin.
	 *
	 * @return origin
	 */
	public WB_Point3d getOrigin();

	/**
	 * Get end.
	 *
	 * @return end
	 */
	public WB_Point3d getEnd();

	/**
	 * Get center.
	 *
	 * @return center
	 */
	public WB_Point3d getCenter();

	/**
	 * Get direction.
	 *
	 * @return end
	 */
	public WB_Vector3d getDirection();

	/**
	 * Get length.
	 *
	 * @return length
	 */
	public double getLength();

}