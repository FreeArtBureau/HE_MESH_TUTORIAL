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
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Planar polygon class.
 */
public class WB_Polyline {

	/** Ordered array of WB_Point. */
	public WB_Point3d[]	points;

	/** Number of points. */
	public int			n;

	/**
	 * Instantiates a new WB_Polyline.
	 */
	public WB_Polyline() {
		points = new WB_Point3d[0];
		n = 0;
	}

	/**
	 * Instantiates a new WB_Polyline.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public WB_Polyline(final WB_Point3d[] points, final int n) {
		this.points = points;
		this.n = n;
	}

	/**
	 * Instantiates a new WB_Polyline.
	 *
	 * @param points array of WB_Point
	 * @param n number of points
	 * @param copy copy points?
	 */
	public WB_Polyline(final WB_Point3d[] points, final int n,
			final boolean copy) {
		if (copy == false) {
			this.points = points;
		} else {
			this.points = new WB_Point3d[n];
			for (int i = 0; i < n; i++) {
				this.points[i] = points[i].get();
			}

		}
		this.n = n;
	}

	/**
	 * Instantiates a new WB_Polyline.
	 *
	 * @param points arrayList of WB_Point
	 */
	public WB_Polyline(final List<WB_Point3d> points) {
		n = points.size();
		this.points = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
	}

	/**
	 * Set polyline.
	 *
	 * @param points array of WB_Point, no copies are made
	 * @param n number of points
	 */
	public void set(final WB_Point3d[] points, final int n) {
		this.points = points;
		this.n = n;

	}

	/**
	 * Set polyline.
	 *
	 * @param poly source polygon, no copies are made
	 */
	public void set(final WB_Polyline poly) {
		points = poly.points;
		n = poly.n;
	}

	/**
	 * Set polyline.
	 *
	 * @param points arrayList of WB_Point, no copies are made
	 * @param n number of points
	 */
	public void set(final ArrayList<WB_Point3d> points, final int n) {
		this.points = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
		this.n = n;
	}

	/**
	 * Get deep copy.
	 *
	 * @return copy
	 */
	public WB_Polyline get() {
		final WB_Point3d[] newPoints = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			newPoints[i] = points[i].get();
		}
		return new WB_Polyline(newPoints, n);

	}

	/**
	 * Get shallow copy.
	 *
	 * @return copy
	 */
	public WB_Polyline getNoCopy() {
		return new WB_Polyline(points, n);

	}

	/**
	 * Closest point on polyline to given point.
	 *
	 * @param p point
	 * @return closest point of polyline
	 */
	public WB_Point3d closestPoint(final WB_Point3d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return points[id];

	}

	/**
	 * Index of closest point on polyline to given point.
	 *
	 * @param p point
	 * @return index of closest point of polyline
	 */
	public int closestIndex(final WB_Point3d p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return id;
	}

	/**
	 * Removes point.
	 *
	 * @param i index of point to remove
	 * @return new WB_Polyline with point removed
	 */
	public WB_Polyline removePoint(final int i) {
		final WB_Point3d[] newPoints = new WB_Point3d[n - 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		for (int j = i; j < n - 1; j++) {
			newPoints[j] = points[j + 1];
		}
		return new WB_Polyline(newPoints, n - 1);

	}

	/**
	 * Adds point.
	 *
	 * @param i index to put point
	 * @param p point
	 * @return new WB_Polyline with point addedd
	 */
	public WB_Polyline addPoint(final int i, final WB_Point3d p) {
		final WB_Point3d[] newPoints = new WB_Point3d[n + 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		newPoints[i] = p;
		for (int j = i + 1; j < n + 1; j++) {
			newPoints[j] = points[j - 1];
		}
		return new WB_Polyline(newPoints, n + 1);

	}

	/**
	 * Refine polygon and smooth with simple Laplacian filter.
	 *
	 * @return new refined WB_Polyline
	 */
	public WB_Polyline smooth() {
		final WB_Point3d[] newPoints = new WB_Point3d[2 * n - 1];

		newPoints[0] = points[0].get();
		for (int i = 1; i < n; i++) {
			newPoints[2 * i - 1] = points[i].addAndCopy(points[i - 1]);
			newPoints[2 * i - 1].mult(0.5);
			newPoints[2 * i] = points[i].get();
		}

		final WB_Point3d[] sPoints = new WB_Point3d[2 * n - 1];

		sPoints[0] = newPoints[0];
		for (int i = 1; i < 2 * n - 2; i++) {
			sPoints[i] = newPoints[i - 1].addAndCopy(newPoints[i + 1]);
			sPoints[i].mult(0.5);
		}
		sPoints[2 * n - 2] = newPoints[2 * n - 2];
		return new WB_Polyline(sPoints, 2 * n - 1);

	}

}