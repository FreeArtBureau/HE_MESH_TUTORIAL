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
public interface WB_Segment2D {

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @return point
	 */
	public WB_Point2d getPoint(final double t);

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @param result WB_XY to store result
	 */
	public void getPointInto(final double t, final WB_Point2d result);

	/**
	 * Get origin.
	 *
	 * @return origin
	 */
	public WB_Point2d getOrigin();

	/**
	 * Get direction.
	 *
	 * @return direction
	 */
	public WB_Point2d getDirection();

	/**
	 * Get end.
	 *
	 * @return end
	 */
	public WB_Point2d getEnd();

	/**
	 * Get center.
	 *
	 * @return center
	 */
	public WB_Point2d getCenter();

	/**
	 * Get length.
	 *
	 * @return length
	 */
	public double getLength();

	public void reverse();

}