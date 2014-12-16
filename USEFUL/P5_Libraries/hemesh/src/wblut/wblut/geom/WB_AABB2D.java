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

import java.util.Collection;

import wblut.WB_Epsilon;



/**
 *
 */
public class WB_AABB2D {

	public WB_Point2d	min;

	public WB_Point2d	max;

	public WB_AABB2D() {
		min = new WB_Point2d();
		max = new WB_Point2d();
	}

	public void set(final WB_AABB2D src) {
		min = src.min.get();
		max = src.max.get();
	}

	public WB_AABB2D get() {
		return new WB_AABB2D(min, max);
	}

	/**
	 *
	 * @param points point cloud
	 * @param n number of points
	 */
	public WB_AABB2D(final WB_Point2d[] points, final int n) {
		min = new WB_Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		max = new WB_Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (int i = 0; i < n; i++) {
			if (min.x > points[i].x) {
				min.x = points[i].x;
			}
			if (min.y > points[i].y) {
				min.y = points[i].y;
			}
			if (max.x <= points[i].x) {
				max.x = points[i].x;
			}
			if (max.y <= points[i].y) {
				max.y = points[i].y;
			}

		}
	}

	public WB_AABB2D(final Collection<? extends WB_Point2d> points) {
		min = new WB_Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		max = new WB_Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (final WB_Point2d point : points) {
			if (min.x > point.x) {
				min.x = point.x;
			}
			if (min.y > point.y) {
				min.y = point.y;
			}
			if (max.x <= point.x) {
				max.x = point.x;
			}
			if (max.y <= point.y) {
				max.y = point.y;
			}

		}
	}

	/**
	 *
	 * @param min minimum values as double[2]
	 * @param max maximum values as double[2]
	 */
	public WB_AABB2D(final double[] min, final double[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**
	 *
	 * @param min minimum values as float[2]
	 * @param max maximum values as float[2]
	 */
	public WB_AABB2D(final float[] min, final float[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**
	 *
	 * @param min minimum values as int[2]
	 * @param max maximum values as int[2]
	 */
	public WB_AABB2D(final int[] min, final int[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**
	 *
	 * @param min minimum values as WB_XY
	 * @param max maximum values as WB_XY
	 */
	public WB_AABB2D(final WB_Point2d min, final WB_Point2d max) {
		this.min = min.get();
		this.max = max.get();
	}

	public double getW() {
		return max.x - min.x;
	}

	public double getH() {
		return max.y - min.y;
	}

	public WB_Point2d getCenter() {
		return new WB_Point2d(0.5 * (max.x + min.x), 0.5 * (max.y + min.y));
	}

	public WB_Point2d getMin() {
		return min;
	}

	public WB_Point2d getMax() {
		return max;
	}

	public WB_AABB2D union(final WB_AABB2D aabb) {
		final WB_Point2d newmin = new WB_Point2d(Math.min(min.x, aabb.getMin().x),
				Math.min(min.y, aabb.getMin().y));
		final WB_Point2d newmax = new WB_Point2d(Math.max(max.x, aabb.getMax().x),
				Math.max(max.y, aabb.getMax().y));
		return new WB_AABB2D(newmin, newmax);
	}

	/**
	 * Instantiates a new WB_AABB2D.
	 */
	public WB_AABB2D(final double minx, final double miny, final double maxx,
			final double maxy) {
		min = new WB_Point2d(minx, miny);
		max = new WB_Point2d(maxx, maxy);
		check();
	}

	private void check() {
		double tmp;
		if (min.x > max.x) {
			tmp = min.x;
			min.x = max.x;
			max.x = tmp;
		}
		if (min.y > max.y) {
			tmp = min.y;
			min.y = max.y;
			max.y = tmp;
		}

	}

	public int getDimension() {
		int dim = 0;
		if (WB_Epsilon.isEqualAbs(min.x, max.x)) {
			dim++;
		}
		if (WB_Epsilon.isEqualAbs(min.y, max.y)) {
			dim++;
		}

		return dim;
	}

	public double getWidth() {
		return max.x - min.x;
	}

	public double getHeight() {
		return max.y - min.y;
	}

	/**
	 * @param points point cloud
	 * @param n number of points
	 */
	public void set(final WB_Point2d[] points, final int n) {
		min = new WB_Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		max = new WB_Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (int i = 0; i < n; i++) {
			if (min.x > points[i].x) {
				min.x = points[i].x;
			}
			if (min.y > points[i].y) {
				min.y = points[i].y;
			}
			if (max.x <= points[i].x) {
				max.x = points[i].x;
			}
			if (max.y <= points[i].y) {
				max.y = points[i].y;
			}

		}
	}

	/**
	 * @param min minimum values as double[2]
	 * @param max maximum values as double[2]
	 */
	public void set(final double[] min, final double[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**
	 * @param min minimum values as float[2]
	 * @param max maximum values as float[2]
	 */
	public void set(final float[] min, final float[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**

	 * @param min minimum values as int[2]
	 * @param max maximum values as int[2]
	 */
	public void set(final int[] min, final int[] max) {
		this.min = new WB_Point2d(min[0], min[1]);
		this.max = new WB_Point2d(max[0], max[1]);
	}

	/**
	 * @param min minimum values as WB_XY
	 * @param max maximum values as WB_XY
	 */
	public void set(final WB_Point2d min, final WB_Point2d max) {
		this.min = min.get();
		this.max = max.get();
	}

}
