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
 * 3D line segment.
 */
public class WB_ExplicitSegment extends WB_Linear implements WB_Segment {

	/** Length. */
	protected double	length;

	/** End. */
	private WB_Point3d	end;

	/**
	 * Instantiates a new WB_Segment.
	 */
	public WB_ExplicitSegment() {
		super();
		end = new WB_Point3d();
		length = 0;

	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param o start point
	 * @param d direction
	 * @param l length
	 */
	public WB_ExplicitSegment(final WB_Point3d o, final WB_Vector3d d,
			final double l) {
		super(o, d);
		length = l;
		end = new WB_Point3d(direction);
		end.mult(l).add(origin);
	}

	/**
	 * Set segment.
	 *
	 * @param o start point
	 * @param d direction
	 * @param l length
	 */
	public void set(final WB_Point3d o, final WB_Vector3d d, final double l) {
		super.set(o, d);
		length = l;
		end = new WB_Point3d(direction);
		end.mult(l).add(origin);
	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param p1 start point
	 * @param p2 end point
	 */
	public WB_ExplicitSegment(final WB_Point3d p1, final WB_Point3d p2) {
		super.set(p1, p2);
		end = p2.get();
		length = Math.sqrt(WB_Distance.sqDistance(p1, p2));
	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param p1x first point on line
	 * @param p1y first point on line
	 * @param p1z first point on line
	 * @param p2x second point on line
	 * @param p2y second point on line
	 * @param p2z second point on line
	 */
	public WB_ExplicitSegment(final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		super(new WB_Point3d(p1x, p1y, p1z), new WB_Point3d(p2x, p2y, p2z));
		end = new WB_Point3d(p2x, p2y, p2z);
		length = Math.sqrt(WB_Distance.sqDistance(origin, end));

	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param p1 start point
	 * @param p2 end point
	 */
	public WB_ExplicitSegment(final WB_Point3d p1, final WB_Point3d p2,
			final boolean copy) {
		super(p1, p2, copy);
		if (copy) {
			end = p2.get();
			length = Math.sqrt(WB_Distance.sqDistance(p1, p2));
		} else {
			end = p2;
			length = Math.sqrt(WB_Distance.sqDistance(p1, p2));
		}

	}

	/**
	 * Instantiates a new WB_Segment.
	 *
	 * @param o origin
	 * @param d direction
	 * @param l length
	 * @param copy copy input points?
	 */
	public WB_ExplicitSegment(final WB_Point3d o, final WB_Vector3d d,
			final double l, final boolean copy) {
		super(o, d, copy);
		end = new WB_Point3d(direction);
		end.mult(l).add(origin);
		length = l;

	}

	/**
	 * Set segment.
	 *
	 * @param p1 start point
	 * @param p2 end point
	 */
	@Override
	public void set(final WB_Point3d p1, final WB_Point3d p2) {
		super.set(p1, p2);
		end = p2.get();
		length = Math.sqrt(WB_Distance.sqDistance(p1, p2));

	}

	@Override
	public void setNoCopy(final WB_Point3d p1, final WB_Point3d p2) {
		super.setNoCopy(p1, p2);

		end = p2.get();

		length = Math.sqrt(WB_Distance.sqDistance(p1, p2));

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

	/**
	 * Get end.
	 *
	 * @return end
	 */
	public WB_Point3d getEnd() {
		return end;
	}

	/**
	 * Get center.
	 *
	 * @return center
	 */
	public WB_Point3d getCenter() {
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
}