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
import wblut.math.WB_Fast;
import wblut.math.WB_M33;

// TODO: Auto-generated Javadoc
/**
 * 3D Sphere.
 */
public class WB_Sphere {

	/** Center. */
	private WB_Point3d	c;

	/** Radius. */
	private double		r;

	/**
	 * Instantiates a new WB_Sphere.
	 */
	public WB_Sphere() {
		c = new WB_Point3d();
		r = 0;
	}

	/**
	 * Instantiates a new WB_Sphere. No copies are made.
	 *
	 * @param c center
	 * @param r radius
	 */
	public WB_Sphere(final WB_Point3d c, final double r) {
		this.c = c;
		this.r = r;
	}

	/**
	 * Instantiates a new WB_Sphere.
	 *
	 * @param c center
	 * @param r radius
	 * @param copy copy center?
	 */
	public WB_Sphere(final WB_Point3d c, final double r, final boolean copy) {
		if (copy) {
			this.c = c.get();
			this.r = r;
		} else {
			this.c = c;
			this.r = r;
		}
	}

	/**
	 * Get copy.
	 *
	 * @return copy
	 */
	public WB_Sphere get() {
		return new WB_Sphere(c, r, true);
	}

	public WB_Point3d getCenter() {
		return c;
	}

	public void setCenter(final WB_Point3d c) {
		this.c = c;
	}

	public double getRadius() {
		return r;
	}

	public void setRadius(final double r) {
		this.r = r;
	}

	/**
	 * Approximate sphere enclosing points, calculated from distant points.
	 *
	 * @param points WB_Point[]
	 * @param numPoints number of points
	 * @return sphere
	 */
	public static WB_Sphere sphereFromDistantPoints(final WB_Point3d[] points,
			final int numPoints) {
		int minx = 0;
		int maxx = 0;
		int miny = 0;
		int maxy = 0;
		int minz = 0;
		int maxz = 0;
		for (int i = 1; i < numPoints; i++) {
			if (points[i].x < points[minx].x) {
				minx = i;
			}
			if (points[i].x > points[maxx].x) {
				maxx = i;
			}
			if (points[i].y < points[miny].y) {
				miny = i;
			}
			if (points[i].y > points[maxy].y) {
				maxy = i;
			}
			if (points[i].z < points[minz].z) {
				minz = i;
			}
			if (points[i].z > points[maxz].z) {
				maxz = i;
			}
		}
		final double dist2x = (points[maxx].subAndCopy(points[minx])).mag2();
		final double dist2y = (points[maxy].subAndCopy(points[miny])).mag2();
		final double dist2z = (points[maxz].subAndCopy(points[minz])).mag2();
		int min = minx;
		int max = maxx;
		if (dist2y > dist2x && dist2y > dist2z) {
			max = maxy;
			min = miny;
		}
		if (dist2z > dist2x && dist2z > dist2y) {
			max = maxz;
			min = minz;
		}
		final WB_Point3d c = (points[min].addAndCopy(points[max])).mult(0.5);
		final double r = (points[max].subAndCopy(c)).mag();
		return new WB_Sphere(c, r);

	}

	/**
	 * Get Ritter sphere enclosing points.
	 *
	 * @param points WB_Point[]
	 * @param numPoints number of points
	 * @return sphere
	 */
	public static WB_Sphere ritterSphere(final WB_Point3d[] points,
			final int numPoints) {
		final WB_Sphere s = sphereFromDistantPoints(points, numPoints);
		for (int i = 0; i < numPoints; i++) {
			s.growSpherebyPoint(points[i]);
		}
		return s;

	}

	/**
	 * Get iterative Ritter sphere enclosing points.
	 *
	 * @param points WB_Point[]
	 * @param numPoints number of points
	 * @param iter number of iterations (8 should be fine)
	 * @return sphere
	 */
	public static WB_Sphere ritterIterativeSphere(final WB_Point3d[] points,
			final int numPoints, final int iter) {
		WB_Sphere s = ritterSphere(points, numPoints);
		final WB_Sphere s2 = s.get();
		for (int k = 0; k < iter; k++) {
			s2.r = s2.r * 0.95;
			for (int i = 0; i < numPoints; i++) {
				int j = i + 1
						+ (int) (.999999 * Math.random() * (numPoints - i - 1));
				if (j > numPoints - 1) {
					j = numPoints - 1;
				}
				final WB_Point3d tmp = points[i];
				points[i] = points[j];
				points[j] = tmp;
				s2.growSpherebyPoint(points[i]);
			}
			if (s2.r < s.r) {
				s = s2;
			}
		}
		return s;
	}

	/**
	 * Get Eigensphere enclosing points.
	 *
	 * @param points WB_Point[]
	 * @param numPoints number of points
	 * @return sphere
	 */
	public static WB_Sphere eigenSphere(final WB_Point3d[] points,
			final int numPoints) {
		WB_M33 m;
		WB_M33 v;
		m = WB_M33.covarianceMatrix(points, numPoints);
		v = m.Jacobi();
		final WB_Vector3d e = new WB_Vector3d();
		if ((WB_Fast.abs(m.m11) >= WB_Fast.abs(m.m22))
				&& (WB_Fast.abs(m.m11) >= WB_Fast.abs(m.m33))) {
			e.set(v.m11, v.m21, v.m31);
		}
		if ((WB_Fast.abs(m.m22) >= WB_Fast.abs(m.m11))
				&& (WB_Fast.abs(m.m22) >= WB_Fast.abs(m.m33))) {
			e.set(v.m12, v.m22, v.m32);
		}
		if ((WB_Fast.abs(m.m33) >= WB_Fast.abs(m.m11))
				&& (WB_Fast.abs(m.m33) >= WB_Fast.abs(m.m11))) {
			e.set(v.m13, v.m23, v.m33);
		}
		final int[] iminmax = extremePointsAlongDirection(points, numPoints, e);
		final WB_Point3d minpt = points[iminmax[0]];
		final WB_Point3d maxpt = points[iminmax[1]];
		final double dist = Math.sqrt(WB_Distance.sqDistance(minpt, maxpt));
		return new WB_Sphere(minpt.add(maxpt).mult(0.5), 0.5 * dist);
	}

	/** Get Ritter Eigensphere enclosing points.
	 *
	 * @param points WB_Point[]
	 * @param numPoints number of points
	 * @return sphere
	 */
	public static WB_Sphere ritterEigenSphere(final WB_Point3d[] points,
			final int numPoints) {
		final WB_Sphere s = eigenSphere(points, numPoints);
		for (int i = 0; i < numPoints; i++) {
			s.growSpherebyPoint(points[i]);
		}
		return s;

	}

	/**
	 * Grow sphere to include point.
	 *
	 * @param p point to include
	 */
	public void growSpherebyPoint(final WB_Point3d p) {
		final WB_Vector3d d = p.subToVector(c);
		final double dist2 = d.mag2();
		if (dist2 > r * r) {
			final double dist = Math.sqrt(dist2);
			final double newRadius = (r + dist) * 0.5;
			final double k = (newRadius - r) / dist;
			r = newRadius;
			c.add(k * d.x, k * d.y, k * d.z);
		}
	}

	/**
	 * Extreme points along direction.
	 *
	 * @param points 
	 * @param numPoints 
	 * @param dir the dir
	 * @return the int[]
	 */
	private static int[] extremePointsAlongDirection(final WB_Point3d[] points,
			final int numPoints, final WB_Vector3d dir) {
		final int[] result = new int[] { -1, -1 };
		double minproj = Double.POSITIVE_INFINITY;
		double maxproj = Double.NEGATIVE_INFINITY;
		double proj;
		for (int i = 0; i < numPoints; i++) {
			proj = points[i].dot(dir);
			if (proj < minproj) {
				minproj = proj;
				result[0] = i;
			}
			if (proj > maxproj) {
				maxproj = proj;
				result[1] = i;
			}

		}
		return result;

	}

	/**
	 * Project point to sphere
	 * @param v
	 * @return point projected to sphere
	 */
	public WB_Point3d projectToSphere(final WB_Point3d v) {
		final WB_Point3d vc = v.subAndCopy(c);
		final double er = vc.normalize();
		if (WB_Epsilon.isZero(er)) {
			return null;
		}
		return c.addAndCopy(vc, r);
	}

}
