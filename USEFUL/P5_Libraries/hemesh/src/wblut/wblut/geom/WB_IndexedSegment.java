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

import wblut.math.WB_Fast;

// TODO: Auto-generated Javadoc
/**
 * A subclass of WB_Segment referencing points as array indices.
 */
public class WB_IndexedSegment extends WB_Linear implements WB_Segment {

	/** index of start point. */
	private int			i1;
	/** index of end point. */
	private int			i2;
	private double		length;
	private WB_Point3d[]	points;

	/**
	 * Instantiates a new WB_IndexedSegment.
	 *
	 * @param i1 index of start point
	 * @param i2 
	 * @param points points as WB_Point[]
	 */
	public WB_IndexedSegment(final int i1, final int i2, final WB_Point3d[] points) {

		this.i1 = i1;
		this.i2 = i2;
		this.points = points;
		length = WB_Distance.distance(points[i1], points[i2]);
	}

	public void set(final int i1, final int i2, final WB_Point3d[] points) {
		this.i1 = i1;
		this.i2 = i2;
		this.points = points;
		length = WB_Distance.distance(points[i1], points[i2]);
	}

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @return point
	 */
	@Override
	public WB_Point3d getPoint(final double t) {
		final WB_Point3d result = new WB_Point3d(direction);
		result.scale(WB_Fast.clamp(t, 0, 1) * length);
		result.moveBy(origin);
		return result;
	}

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @param result WB_Point to store result
	 */
	@Override
	public void getPointInto(final double t, final WB_Point3d result) {
		result.moveTo(direction);
		if (WB_Fast.clamp(t, 0, 1) == t) {
			result.scale(t * length);
		}
		result.moveBy(origin);

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Segment#center()
	 */
	public WB_Point3d getCenter() {
		return WB_Point3d.interpolate(points[i1], points[i2], 0.5);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Segment#end()
	 */
	public WB_Point3d getEnd() {
		return points[i2];
	}

	@Override
	public WB_Point3d getOrigin() {
		return points[i1];
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.geom.WB_Segment#length()
	 */
	public double getLength() {
		// TODO Auto-generated method stub
		return length;
	}

	public int i1() {
		return i1;
	}

	public int i2() {
		return i2;
	}

	public WB_Point3d[] points() {
		return points;
	}

}
