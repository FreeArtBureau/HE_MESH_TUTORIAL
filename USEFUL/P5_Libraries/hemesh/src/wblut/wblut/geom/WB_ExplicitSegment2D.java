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

import java.util.List;

import wblut.math.WB_Fast;


import javolution.util.FastList;

// TODO: Auto-generated Javadoc
/**
 * 3D line segment.
 */
public class WB_ExplicitSegment2D extends WB_Linear2D implements WB_Segment2D {

	/** Length. */
	protected double	length;

	/** End. */
	private WB_Point2d	end;

	/**
	 * Instantiates a new WB_Segment.
	 */
	public WB_ExplicitSegment2D() {
		super();
		end = new WB_Point2d();
		length = 0;

	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param p1 start point
	 * @param p2 end point
	 */
	public WB_ExplicitSegment2D(final WB_Point2d p1, final WB_Point2d p2) {
		super(p1, p2);
		end = p2.get();
		length = Math.sqrt(WB_Distance2D.sqDistance(p1, p2));

	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param o start point
	 * @param d direction
	 * @param l length
	 */
	public WB_ExplicitSegment2D(final WB_Point2d o, final WB_Point2d d,
			final double l) {
		super(o, d, true);
		length = l;
		end = new WB_Point2d(direction);
		end.mult(l).add(origin);
	}

	/**
	 * Set segment.
	 *
	 * @param p1 start point
	 * @param p2 end point
	 */
	@Override
	public void set(final WB_Point2d p1, final WB_Point2d p2) {
		super.set(p1, p2);
		end = p2.get();
		length = Math.sqrt(WB_Distance2D.sqDistance(p1, p2));

	}

	/**
	 * Set segment.
	 *
	 * @param o start point
	 * @param d direction
	 * @param l length
	 */
	public void set(final WB_Point2d o, final WB_Point2d d, final double l) {
		super.set(o, d, true);
		length = l;
		end = new WB_Point2d(direction);
		end.mult(l).add(origin);
	}

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
		result.moveBy(origin);
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
		result.moveBy(origin);

	}

	/**
	 * Get end.
	 *
	 * @return end
	 */
	public WB_Point2d getEnd() {
		return end;
	}

	/**
	 * Get center.
	 *
	 * @return center
	 */
	public WB_Point2d getCenter() {
		return end.addAndCopy(origin).mult(0.5);
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
		set(end, origin);
	}

	public WB_ExplicitSegment2D negate() {
		return new WB_ExplicitSegment2D(end, origin);

	}

	public static List<WB_ExplicitSegment2D> negate(
			final List<WB_ExplicitSegment2D> segs) {
		final List<WB_ExplicitSegment2D> neg = new FastList<WB_ExplicitSegment2D>();
		for (int i = 0; i < segs.size(); i++) {
			neg.add(segs.get(i).negate());
		}

		return neg;

	}

	public WB_ExplicitSegment2D get() {
		return new WB_ExplicitSegment2D(origin, end);
	}

}