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

import java.util.ArrayList;

import wblut.math.WB_Fast;



// TODO: Auto-generated Javadoc
/**
 * A subclass of WB_Segment referencing points as array indices.
 */
public class WB_IndexedSegment2D extends WB_Linear2D implements WB_Segment2D {

	/** Direction. */
	private WB_Point2d			direction;

	/** Length. */
	protected double		length;

	private final WB_Point2d[]	points;

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @return point
	 */
	@Override
	public WB_Point2d getPoint(final double t) {
		final WB_Point2d result = new WB_Point2d(direction);
		result.scale(WB_Fast.clamp(t, 0, 1) * length);
		result.moveBy(points[i1]);
		return result;
	}

	/**
	 * Get point along segment.
	 *
	 * @param t 0..1: origin to end
	 * @param result WB_XY to store result
	 */
	@Override
	public void getPointInto(final double t, final WB_Point2d result) {
		result.moveTo(direction);
		if (WB_Fast.clamp(t, 0, 1) == t) {
			result.scale(t * length);
		}
		result.moveBy(points[i1]);

	}

	/**
	 * Get end.
	 *
	 * @return end
	 */
	public WB_Point2d getEnd() {
		return points[i2];
	}

	/**
	 * Get center.
	 *
	 * @return center
	 */
	public WB_Point2d getCenter() {
		return points[i2].addAndCopy(points[i1]).mult(0.5);
	}

	/**
	 * Get length.
	 *
	 * @return length
	 */
	public double getLength() {
		return length;
	}

	public void reverse() {
		set(i2, i1);
	}

	public WB_IndexedSegment2D negate() {
		return new WB_IndexedSegment2D(i2, i1, points);

	}

	public static ArrayList<WB_IndexedSegment2D> negate(
			final ArrayList<WB_IndexedSegment2D> segs) {
		final ArrayList<WB_IndexedSegment2D> neg = new ArrayList<WB_IndexedSegment2D>();
		for (int i = 0; i < segs.size(); i++) {
			neg.add(segs.get(i).negate());
		}

		return neg;

	}

	/**
	 * @return copy
	 */
	public WB_IndexedSegment2D get() {

		return new WB_IndexedSegment2D(i1, i2, points);
	}

	/** index of start point. */
	private int	i1;
	/** index of end point. */
	private int	i2;

	/**
	 * Instantiates a new WB_IndexedSegment.
	 *
	 * @param i1 index of start point
	 * @param i2 
	 * @param points points as WB_Point[]
	 */
	public WB_IndexedSegment2D(final int i1, final int i2, final WB_Point2d[] points) {
		super(points[i1], points[i2]);
		this.points = points;
		length = Math.sqrt(WB_Distance2D.sqDistance(points[i1], points[i2]));
		this.i1 = i1;
		this.i2 = i2;
	}

	public void set(final int i1, final int i2) {
		super.set(points[i1], points[i2]);
		length = Math.sqrt(WB_Distance2D.sqDistance(points[i1], points[i2]));
		this.i1 = i1;
		this.i2 = i2;
	}

	public int i1() {
		return i1;
	}

	public int i2() {
		return i2;
	}
}
