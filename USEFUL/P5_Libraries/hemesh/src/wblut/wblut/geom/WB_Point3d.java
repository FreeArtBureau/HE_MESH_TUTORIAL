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
import wblut.math.WB_MTRandom;

/**
 *
 * @author Frederik Vanhoutte (W:Blut) 2010
 */
public class WB_Point3d implements Comparable<WB_Point3d> {
	public static WB_Point3d ZERO() {
		return new WB_Point3d(0, 0, 0);
	}

	public static WB_Point3d X() {
		return new WB_Point3d(1, 0, 0);
	}

	public static WB_Point3d Y() {
		return new WB_Point3d(0, 1, 0);
	}

	public static WB_Point3d Z() {
		return new WB_Point3d(0, 0, 1);
	}

	/** Coordinates. */
	public double	x, y, z;

	/**
	 * Instantiates a new WB_XYZ.
	 */
	public WB_Point3d() {
		x = y = z = 0;
	}

	/**
	 * Instantiates a new WB_XYZ.
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public WB_Point3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Instantiates a new WB_XYZ.
	 *
	 * @param x
	 * @param y
	 */
	public WB_Point3d(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Instantiates a new WB_XYZ.
	 *
	 * @param v
	 */
	public WB_Point3d(final WB_Point3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	/**
	 * return copy.
	 *
	 * @return copy
	 */
	public WB_Point3d get() {
		return new WB_Point3d(x, y, z);
	}

	/**
	 * Set coordinates.
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Set coordinates.
	 *
	 * @param x
	 * @param y
	 */
	public void set(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Set coordinates.
	 *
	 * @param v
	 */
	public void set(final WB_Point3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	/**
	 * 
	 *

	 */
	public void invert() {
		x *= -1;
		y *= -1;
		z *= -1;
	}

	public double normalize() {
		final double d = mag();
		if (WB_Epsilon.isZero(d)) {
			set(0, 0, 0);
		} else {
			set(x / d, y / d, z / d);
		}
		return d;
	}

	public void trim(final double d) {
		if (mag2() > d * d) {
			normalize();
			mult(d);
		}
	}

	/**
	 * Scale.
	 *
	 * @param f scale factor
	 * @return self
	 */
	public WB_Point3d scale(final double f) {
		x *= f;
		y *= f;
		z *= f;
		return this;
	}

	/**
	 * Scale.
	 *
	 * @param fx scale factor
	 * @param fy scale factor
	 * @param fz scale factor
	 * @return self
	 */

	public WB_Point3d scale(final double fx, final double fy, final double fz) {
		x *= fx;
		y *= fy;
		z *= fz;
		return this;
	}

	/**
	 * Scale .
	 *
	 * @param f scale factor
	 * @param result WB_XYZ to store result
	 */
	public void scaleInto(final double f, final WB_Point3d result) {
		result.x = x * f;
		result.y = y * f;
		result.z = z * f;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return self
	 */
	public WB_Point3d add(final double x, final double y, final double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return self
	 */
	public WB_Point3d add(final WB_Point3d p) {
		x += p.x;
		y += p.y;
		z += p.z;
		return this;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param result 
	 */
	public void addInto(final double x, final double y, final double z,
			final WB_Point3d result) {
		result.x = (this.x + x);
		result.y = (this.y + y);
		result.z = (this.z + z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @param result 
	 */
	public void addInto(final WB_Point3d p, final WB_Point3d result) {
		result.x = x + p.x;
		result.y = y + p.y;
		result.z = z + p.z;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZW
	 */
	public WB_Point3d addAndCopy(final double x, final double y, final double z) {
		return new WB_Point3d(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return new WB_XYZ
	 */
	public WB_Point3d addAndCopy(final WB_Point3d p) {
		return new WB_Point3d(x + p.x, y + p.y, z + p.z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return self
	 */
	public WB_Point3d add(final WB_Point3d p, final double f) {
		x += f * p.x;
		y += f * p.y;
		z += f * p.z;
		return this;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return self
	 */
	public WB_Point3d add(final double x, final double y, final double z,
			final double f) {
		this.x += f * x;
		this.y += f * y;
		this.z += f * z;
		return this;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param result 
	 */
	public void addInto(final double x, final double y, final double z,
			final double f, final WB_Point3d result) {
		result.x = (this.x + f * x);
		result.y = (this.y + f * y);
		result.z = (this.z + f * z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @param result 
	 */
	public void addInto(final WB_Point3d p, final double f,
			final WB_Point3d result) {
		result.x = x + f * p.x;
		result.y = y + f * p.y;
		result.z = z + f * p.z;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZW
	 */
	public WB_Point3d addAndCopy(final double x, final double y,
			final double z, final double f) {
		return new WB_Point3d(this.x + f * x, this.y + f * y, this.z + f * z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return new WB_XYZ
	 */
	public WB_Point3d addAndCopy(final WB_Point3d p, final double f) {
		return new WB_Point3d(x + f * p.x, y + f * p.y, z + f * p.z);
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return self
	 */
	public WB_Point3d sub(final double x, final double y, final double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	/**
	 * 
	 *
	 * @param v 
	 * @return self
	 */
	public WB_Point3d sub(final WB_Point3d v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param result 
	 */
	public void subInto(final double x, final double y, final double z,
			final WB_Point3d result) {
		result.x = (this.x - x);
		result.y = (this.y - y);
		result.z = (this.z - z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @param result 
	 */
	public void subInto(final WB_Point3d p, final WB_Point3d result) {
		result.x = x - p.x;
		result.y = y - p.y;
		result.z = z - p.z;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZ
	 */
	public WB_Point3d subAndCopy(final double x, final double y, final double z) {
		return new WB_Point3d(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return new WB_XYZ
	 */
	public WB_Point3d subAndCopy(final WB_Point3d p) {
		return new WB_Point3d(x - p.x, y - p.y, z - p.z);
	}

	/**
	 * Subtract to vector.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return new WB_Vector
	 */
	public WB_Vector3d subToVector(final double x, final double y,
			final double z) {
		return new WB_Vector3d(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Subtract to vector.
	 *
	 * @param p point
	 * @return new WB_Vector
	 */
	public WB_Vector3d subToVector(final WB_Point3d p) {
		return new WB_Vector3d(x - p.x, y - p.y, z - p.z);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return self
	 */
	public WB_Point3d mult(final double f) {
		scale(f);
		return this;
	}

	/**
	 * 
	 *
	 * @param f 
	 * @param result 
	 */
	public void multInto(final double f, final WB_Point3d result) {
		scaleInto(f, result);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return new WB_XYZW
	 */
	public WB_Point3d multAndCopy(final double f) {
		return new WB_Point3d(x * f, y * f, z * f);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return self
	 */
	public WB_Point3d div(final double f) {
		return mult(1.0 / f);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @param result 
	 */
	public void divInto(final double f, final WB_Point3d result) {
		multInto(1.0 / f, result);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return new WB_XYZW
	 */
	public WB_Point3d divAndCopy(final double f) {
		return multAndCopy(1.0 / f);
	}

	/**
	 * Dot product.
	 *
	 * @param p
	 * @param q
	 * @return dot product
	 */
	public static double dot(final WB_Point3d p, final WB_Point3d q) {
		return (p.x * q.x + p.y * q.y + p.z * q.z);
	}

	/**
	 * Dot product.
	 *
	 * @param p
	 * @return dot product
	 */
	public double dot(final WB_Point3d p) {
		return (p.x * x + p.y * y + p.z * z);
	}

	/**
	 * Angle to vector. Normalized vectors are assumed.
	 *
	 * @param p normalized point, vector or normal
	 * @return angle
	 */
	public double angleNorm(final WB_Point3d p) {
		return Math.acos(p.x * x + p.y * y + p.z * z);
	}

	/**
	 * Absolute value of dot product.
	 *
	 * @param p
	 * @param q 
	 * @return absolute value of dot product
	 */
	public static double absDot(final WB_Point3d p, final WB_Point3d q) {
		return WB_Fast.abs(p.x * q.x + p.y * q.y + p.z * q.z);
	}

	/**
	 * Absolute value of dot product.
	 *
	 * @param p
	* @return absolute value of dot product
	 */
	public double absDot(final WB_Point3d p) {
		return WB_Fast.abs(p.x * x + p.y * y + p.z * z);
	}

	/**
	 * Cross product. Internal use only.
	 */
	public WB_Point3d cross(final WB_Point3d p) {
		return new WB_Point3d(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y
				* p.x);
	}

	/**
	 * Cross product. Internal use only.
	 */
	public static WB_Point3d cross(final WB_Point3d p, final WB_Point3d q) {
		return new WB_Point3d(p.y * q.z - p.z * q.y, p.z * q.x - p.x * q.z, p.x
				* q.y - p.y * q.x);
	}

	/**
	 * Cross product. Internal use only.
	 */
	public void crossInto(final WB_Point3d p, final WB_Point3d result) {
		result.x = y * p.z - z * p.y;
		result.y = z * p.x - x * p.z;
		result.z = x * p.y - y * p.x;
	}

	/**
	 * Scalar triple product.
	 *
	 * @param p
	 * @param q
	 * @param r
	 * @return scalar triple product
	 */
	public static double scalarTriple(final WB_Point3d p, final WB_Point3d q,
			final WB_Point3d r) {
		return (dot(p, cross(q, r)));
	}

	/**
	 * Scalar triple product.
	 *
	 * @param p
	 * @param q
	 * @return scalar triple product.
	 */
	public double scalarTriple(final WB_Point3d p, final WB_Point3d q) {
		return (dot(this, cross(p, q)));
	}

	public static WB_M33 tensor(final WB_Point3d p, final WB_Point3d q) {
		return new WB_M33(p.x * q.x, p.x * q.y, p.x * q.z, p.y * q.x,
				p.y * q.y, p.y * q.z, p.z * q.x, p.z * q.y, p.z * q.z);
	}

	public WB_M33 tensor(final WB_Point3d q) {
		return new WB_M33(x * q.x, x * q.y, x * q.z, y * q.x, y * q.y, y * q.z,
				z * q.x, z * q.y, z * q.z);
	}

	/**
	 * Get squared magnitude.
	 *
	 * @return squared magnitude
	 */
	public double mag2() {
		return x * x + y * y + z * z;
	}

	/**
	 * Get magnitude.
	 *
	 * @return magnitude
	 */
	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Checks if vector is zero-vector.
	 *
	 * @return true, if zero
	 */
	public boolean isZero() {
		return (mag2() < WB_Epsilon.SQEPSILON);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final WB_Point3d otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return _tmp;
	}

	public int compareToY1st(final WB_Point3d otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return _tmp;
	}

	/**
	 * Smaller than.
	 *
	 * @param otherXYZ point, vector or normal
	 * @return true, if successful
	 */
	public boolean smallerThan(final WB_Point3d otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return (_tmp < 0);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XYZ [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	/**
	 * Get coordinate from index value.
	 *
	 * @param i 0,1,2
	 * @return x-,y- or z-coordinate
	 */
	public double get(final int i) {
		if (i == 0) {
			return x;
		}
		if (i == 1) {
			return y;
		}
		if (i == 2) {
			return z;
		}
		return Double.NaN;
	}

	/**
	 * Set coordinate with index value.
	 *
	 * @param i  0,1,2
	 * @param v x-,y- or z-coordinate
	 */
	public void set(final int i, final double v) {
		if (i == 0) {
			x = v;
		} else if (i == 1) {
			y = v;
		} else if (i == 2) {
			z = v;
		}

	}

	/**
	 * Get x-coordinate as float.
	 *
	 * @return x
	 */
	public float xf() {
		return (float) x;
	}

	/**
	 * Get y-coordinate as float.
	 *
	 * @return y
	 */
	public float yf() {
		return (float) y;
	}

	/**
	 * Get z-coordinate as float.
	 *
	 * @return z
	 */
	public float zf() {
		return (float) z;
	}

	/**
	 * Is vector parallel to other vector
	 * 
	 * @param p
	 * @return true, if parallel
	 */
	public boolean isParallel(final WB_Point3d p) {
		return (cross(p).mag2() / (p.mag2() * mag2()) < WB_Epsilon.SQEPSILON);
	}

	/**
	 * Is vector parallel to other vector
	 * 
	 * @param p
	 * @param t threshold value = (sin(threshold angle))^2
	 * @return true, if parallel
	 */
	public boolean isParallel(final WB_Point3d p, final double t) {
		return (cross(p).mag2() / (p.mag2() * mag2()) < t
				+ WB_Epsilon.SQEPSILON);
	}

	/**
	 * Is normalized vector parallel to other normalized vector
	 * 
	 * @param p
	 * @return true, if parallel
	 */
	public boolean isParallelNorm(final WB_Point3d p) {
		return (cross(p).mag2() < WB_Epsilon.SQEPSILON);
	}

	/**
	 * Is normalized vector parallel to other normalized vector
	 * 
	 * @param p
	 * @param t threshold value = (sin(threshold angle))^2
	 * @return true, if parallel
	 */
	public boolean isParallelNorm(final WB_Point3d p, final double t) {
		return (cross(p).mag2() < t + WB_Epsilon.SQEPSILON);
	}

	protected static int calculateHashCode(final double x, final double y,
			final double z) {
		int result = 17;

		final long a = Double.doubleToLongBits(x);
		result += 31 * result + (int) (a ^ (a >>> 32));

		final long b = Double.doubleToLongBits(y);
		result += 31 * result + (int) (b ^ (b >>> 32));

		final long c = Double.doubleToLongBits(z);
		result += 31 * result + (int) (c ^ (c >>> 32));

		return result;

	}

	protected int calculateHashCode() {
		int result = 17;

		final long a = Double.doubleToLongBits(x);
		result += 31 * result + (int) (a ^ (a >>> 32));

		final long b = Double.doubleToLongBits(y);
		result += 31 * result + (int) (b ^ (b >>> 32));

		final long c = Double.doubleToLongBits(z);
		result += 31 * result + (int) (c ^ (c >>> 32));

		return result;

	}

	/**
	 * Move to position.
	 *
	 * @param x
	 * @param y 
	 * @param z
	 * @return self
	 */
	public WB_Point3d moveTo(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Move to position.
	 *
	 * @param x
	 * @param y 
	 * @return self
	 */
	public WB_Point3d moveTo(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
		return this;
	}

	/**
	 * Move to position.
	 *
	 * @param p point, vector or normal
	 * @return self
	 */
	public WB_Point3d moveTo(final WB_Point3d p) {
		x = p.x;
		y = p.y;
		z = p.z;
		return this;
	}

	/**
	 * Move by vector.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return self
	 */
	public WB_Point3d moveBy(final double x, final double y, final double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Move by vector.
	 *
	 * @param v point, vector or normal
	 * @return self
	 */
	public WB_Point3d moveBy(final WB_Point3d v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	/**
	 * Move by vector.
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @param result WB_XYZ to store result
	 */
	public void moveByInto(final double x, final double y, final double z,
			final WB_Point3d result) {
		result.x = this.x + x;
		result.y = this.y + y;
		result.z = this.z + z;
	}

	/**
	 * Move by vector.
	 *
	 * @param v point, vector or normal
	 * @param result WB_XYZ to store result
	 */
	public void moveByInto(final WB_Point3d v, final WB_Point3d result) {
		result.x = x + v.x;
		result.y = y + v.y;
		result.z = z + v.z;
	}

	/**
	 * Move by vector.
	 *
	 * @param x 
	 * @param y 
	 * @param z
	 * @return new WB_XYZ
	 */
	public WB_Point3d moveByAndCopy(final double x, final double y,
			final double z) {
		return new WB_Point3d(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Move by vector.
	 *
	 * @param v point, vector or normal
	 * @return new WB_XYZ
	 */
	public WB_Point3d moveByAndCopy(final WB_Point3d v) {
		return new WB_Point3d(x + v.x, y + v.y, z + v.z);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	/**
	 * Rotate vertex around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p1x x-coordinate of first point on axis
	 * @param p1y y-coordinate of first point on axis
	 * @param p1z z-coordinate of first point on axis
	 * @param p2x x-coordinate of second point on axis
	 * @param p2y y-coordinate of second point on axis
	 * @param p2z z-coordinate of second point on axis
	 */
	public void rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point3d(p1x, p1y, p1z),
				new WB_Vector3d(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelf(this);
	}

	/**
	 * Rotate vertex around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p1 first point on axis
	 * @param p2 second point on axis
	 */
	public void rotateAboutAxis(final double angle, final WB_Point3d p1,
			final WB_Point3d p2) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, p2.subToVector(p1));

		raa.applySelf(this);

	}

	/**
	 * Rotate vertex around an arbitrary axis.
	 *
	 * @param angle angle
	 * @param p rotation point
	 * @param a axis
	 */
	public void rotateAboutAxis(final double angle, final WB_Point3d p,
			final WB_Vector3d a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);

		raa.applySelf(this);

	}

	// Get n points in range (-x,x), (-y,y),(-z,z)
	public static WB_Point3d[] randomPoints(final int n, final double x,
			final double y, final double z) {
		final WB_MTRandom mtr = new WB_MTRandom();
		final WB_Point3d[] points = new WB_Point3d[n];
		for (int i = 0; i < n; i++) {
			points[i] = new WB_Point3d(-x + 2 * mtr.nextDouble() * x, -y + 2
					* mtr.nextDouble() * y, -z + 2 * mtr.nextDouble() * z);
		}

		return points;
	}

	// Get n points in range (lx,ux), (ly,uy),(lz,uz)
	public static WB_Point3d[] randomPoints(final int n, final double lx,
			final double ly, final double lz, final double ux, final double uy,
			final double uz) {
		final WB_MTRandom mtr = new WB_MTRandom();
		final WB_Point3d[] points = new WB_Point3d[n];
		final double dx = ux - lx;
		final double dy = uy - ly;
		final double dz = uz - lz;

		for (int i = 0; i < n; i++) {
			points[i] = new WB_Point3d(lx + mtr.nextDouble() * dx, ly
					+ mtr.nextDouble() * dy, lz + mtr.nextDouble() * dz);
		}

		return points;
	}

	public WB_Vector3d toVector() {
		return new WB_Vector3d(x, y, z);
	}

	public WB_Normal3d toNormal() {
		return new WB_Normal3d(x, y, z);
	}

	/**
	 * Interpolate.
	 *
	 * @param p0 the p0
	 * @param p1 the p1
	 * @param t the t
	 * @return the w b_ point
	 */
	public static WB_Point3d interpolate(final WB_Point3d p0,
			final WB_Point3d p1, final double t) {
		return new WB_Point3d(p0.x + t * (p1.x - p0.x), p0.y + t
				* (p1.y - p0.y), p0.z + t * (p1.z - p0.z));

	}

	public static double angleBetween(final WB_Point3d corner,
			final WB_Point3d p1, final WB_Point3d p2) {
		final WB_Point3d v0 = p1.subAndCopy(corner);
		final WB_Point3d v1 = p2.subAndCopy(corner);
		v0.normalize();
		v1.normalize();
		return Math.acos(v0.dot(v1));

	}

	public static double cosAngleBetween(final WB_Point3d corner,
			final WB_Point3d p1, final WB_Point3d p2) {
		final WB_Point3d v0 = p1.subAndCopy(corner);
		final WB_Point3d v1 = p2.subAndCopy(corner);
		v0.normalize();
		v1.normalize();
		return v0.dot(v1);

	}

}
