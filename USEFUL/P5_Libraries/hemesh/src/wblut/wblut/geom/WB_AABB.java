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

import wblut.WB_Epsilon;

/**
 * Axis-aligned bounding box.
 *
 */
public class WB_AABB {

	/** Minimum x, y and z values. */
	protected WB_Point3d	min;

	/** Maximum x, y and z values. */
	protected WB_Point3d	max;

	/**
	 * Instantiates a new WB_AABB.
	 */
	public WB_AABB() {
		min = new WB_Point3d();
		max = new WB_Point3d();
	}

	/**
	 * Instantiates a new WB_AABB.
	 */
	public void set(final WB_AABB src) {
		min = src.min.get();
		max = src.max.get();
	}

	/**
	 * Instantiates a new WB_AABB.
	 */
	public WB_AABB get() {
		return new WB_AABB(min, max);
	}

	/**
	 * Instantiates a new WB_AABB.
	 *
	 * @param points point cloud
	 * @param n number of points
	 */
	public WB_AABB(final WB_Point3d[] points, final int n) {
		min = new WB_Point3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		max = new WB_Point3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY);
		for (int i = 0; i < n; i++) {
			if (min.x > points[i].x) {
				min.x = points[i].x;
			}
			if (min.y > points[i].y) {
				min.y = points[i].y;
			}
			if (min.z > points[i].z) {
				min.z = points[i].z;
			}
			if (max.x <= points[i].x) {
				max.x = points[i].x;
			}
			if (max.y <= points[i].y) {
				max.y = points[i].y;
			}
			if (max.z <= points[i].z) {
				max.z = points[i].z;
			}

		}
	}

	/**
	 * Instantiates a new WB_AABB.
	 */
	public WB_AABB(final double minx, final double miny, final double minz,
			final double maxx, final double maxy, final double maxz) {
		min = new WB_Point3d(minx, miny, minz);
		max = new WB_Point3d(maxx, maxy, maxz);
		check();
	}

	/**
	 * Instantiates a new WB_AABB.
	 *
	 * @param min minimum values as double[3]
	 * @param max maximum values as double[3]
	 */
	public WB_AABB(final double[] min, final double[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
		check();
	}

	/**
	 * Instantiates a new WB_AABB.
	 *
	 * @param min minimum values as float[3]
	 * @param max maximum values as float[3]
	 */
	public WB_AABB(final float[] min, final float[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
		check();
	}

	/**
	 * Instantiates a new WB_AABB.
	 *
	 * @param min minimum values as int[3]
	 * @param max maximum values as int[3]
	 */
	public WB_AABB(final int[] min, final int[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
		check();
	}

	/**
	 * Instantiates a new WB_AABB.
	 *
	 * @param min minimum values as WB_Point
	 * @param max maximum values as WB_Point
	 */
	public WB_AABB(final WB_Point3d min, final WB_Point3d max) {
		this.min = min.get();
		this.max = max.get();
		check();
	}

	/**
	 * @param min minimum values as WB_Point
	 * @param max maximum values as WB_Point
	 */
	public void set(final WB_Point3d min, final WB_Point3d max) {
		this.min = min.get();
		this.max = max.get();
		check();
	}

	/**
	 * @param points point cloud
	 * @param n number of points
	 */
	public void set(final WB_Point3d[] points, final int n) {
		min = new WB_Point3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		max = new WB_Point3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY);
		for (int i = 0; i < n; i++) {
			if (min.x > points[i].x) {
				min.x = points[i].x;
			}
			if (min.y > points[i].y) {
				min.y = points[i].y;
			}
			if (min.z > points[i].z) {
				min.z = points[i].z;
			}
			if (max.x <= points[i].x) {
				max.x = points[i].x;
			}
			if (max.y <= points[i].y) {
				max.y = points[i].y;
			}
			if (max.z <= points[i].z) {
				max.z = points[i].z;
			}

		}
	}

	/**
	 */
	public void set(final double minx, final double miny, final double minz,
			final double maxx, final double maxy, final double maxz) {
		min = new WB_Point3d(minx, miny, minz);
		max = new WB_Point3d(maxx, maxy, maxz);
		check();
	}

	/**
	 * @param min minimum values as double[3]
	 * @param max maximum values as double[3]
	 */
	public void set(final double[] min, final double[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
		check();
	}

	/**
	 * @param min minimum values as float[3]
	 * @param max maximum values as float[3]
	 */
	public void set(final float[] min, final float[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
		check();
	}

	/**
	 * @param min minimum values as int[3]
	 * @param max maximum values as int[3]
	 */
	public void set(final int[] min, final int[] max) {
		this.min = new WB_Point3d(min[0], min[1], min[2]);
		this.max = new WB_Point3d(max[0], max[1], max[2]);
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
		if (min.z > max.z) {
			tmp = min.z;
			min.z = max.z;
			max.z = tmp;
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
		if (WB_Epsilon.isEqualAbs(min.z, max.z)) {
			dim++;
		}
		return dim;
	}

	public WB_Point3d getMin() {
		return min;

	}

	public WB_Point3d getMax() {
		return max;

	}

	public double getWidth() {
		return max.x - min.x;
	}

	public double getHeight() {
		return max.y - min.y;
	}

	public double getDepth() {
		return max.z - min.z;
	}

	public WB_Point3d getCenter() {
		return new WB_Point3d(0.5 * (max.x + min.x), 0.5 * (max.y + min.y),
				0.5 * (max.z + min.z));

	}

	public WB_AABB union(final WB_AABB aabb) {
		final WB_Point3d newmin = new WB_Point3d(Math.min(min.x, aabb.getMin().x),
				Math.min(min.y, aabb.getMin().y), Math.min(min.z,
						aabb.getMin().z));
		final WB_Point3d newmax = new WB_Point3d(Math.max(max.x, aabb.getMax().x),
				Math.max(max.y, aabb.getMax().y), Math.max(max.z,
						aabb.getMax().z));
		return new WB_AABB(newmin, newmax);
	}

	/**
	 * Squared distance between point and axis-aligned box.
	 *
	 * @param p point
	 * @return squared distance
	 */
	public double sqDistance(final WB_Point3d p) {
		return WB_Distance.sqDistance(p, this);
	}

	/**
	 * Distance between point and axis-aligned box.
	 *
	 * @param p point
	 * @return distance
	 */
	public double distance(final WB_Point3d p) {
		return WB_Distance.sqDistance(p, this);
	}

	public WB_IntersectionResult getIntersection(final WB_Ray R) {
		return WB_Intersection.getIntersection(R, this);
	}

	public boolean checkIntersection(final WB_Triangle T) {
		return WB_Intersection.checkIntersection(T, this);
	}

	public boolean checkIntersection(final WB_Segment S) {
		return WB_Intersection.checkIntersection(S, this);
	}

	public boolean checkIntersection(final WB_Ray R) {
		return WB_Intersection.checkIntersection(R, this);
	}

	public boolean checkIntersection(final WB_Line L) {
		return WB_Intersection.checkIntersection(L, this);
	}

	public boolean checkIntersection(final WB_AABB other) {
		return WB_Intersection.checkIntersection(this, other);
	}

	public boolean checkIntersection(final WB_Plane P) {
		return WB_Intersection.checkIntersection(this, P);
	}

	public boolean checkIntersection(final WB_Sphere S) {
		return WB_Intersection.checkIntersection(this, S);
	}

	public boolean contains(final WB_Point3d p) {
		return WB_Containment.contains(p, this);

	}

}
