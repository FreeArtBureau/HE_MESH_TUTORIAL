/**
 * 
 */
package wblut.geom;

import wblut.WB_Epsilon;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Point4d implements Comparable<WB_Point4d> {
	public static final WB_Point4d	ZERO	= new WB_Point4d(0, 0, 0, 1);
	/** Coordinates. */
	public double					x, y, z, w;

	/**
	 * Instantiates a new WB_XYZW.
	 */
	public WB_Point4d() {
		x = y = z = w = 0;
	}

	/**
	 * Instantiates a new WB_XYZW.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public WB_Point4d(final double x, final double y, final double z,
			final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Instantiates a new WB_XYZW.
	 *
	 * @param v
	 */
	public WB_Point4d(final WB_Point4d v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}

	/**
	 * Instantiates a new WB_XYZW.
	 *
	 * @param v
	 */
	public WB_Point4d(final WB_Point3d v, final double w) {
		x = v.x;
		y = v.y;
		z = v.z;
		this.w = w;
	}

	/**
	 * Set coordinates.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void set(final double x, final double y, final double z,
			final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Set coordinates.
	 *
	 * @param v
	 */
	public void set(final WB_Point3d v, final double w) {
		x = v.x;
		y = v.y;
		z = v.z;
		this.w = w;
	}

	/**
	 * Get squared magnitude.
	 *
	 * @return squared magnitude
	 */
	public double mag2() {
		return x * x + y * y + z * z + w * w;
	}

	/**
	 * Get magnitude.
	 *
	 * @return magnitude
	 */
	public double mag() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
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
	public int compareTo(final WB_Point4d otherXYZW) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZW.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZW.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZW.z);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(w, otherXYZW.w);
		return _tmp;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XYZW [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
	}

	/**
	 * Get coordinate from index value.
	 *
	 * @param i 0,1,2,3
	 * @return x-, y-, z- or w-coordinate
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
		if (i == 3) {
			return w;
		}
		return Double.NaN;
	}

	/**
	 * Set coordinate with index value.
	 *
	 * @param i  0,1,2,3
	 * @param v x-, y-, z- or w-coordinate
	 */
	public void set(final int i, final double v) {
		if (i == 0) {
			x = v;
		} else if (i == 1) {
			y = v;
		} else if (i == 2) {
			z = v;
		} else if (i == 3) {
			w = v;
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
	 * Get w-coordinate as float.
	 *
	 * @return w
	 */
	public float wf() {
		return (float) w;
	}

	/**
	 * return copy.
	 *
	 * @return copy
	 */
	public WB_Point4d get() {
		return new WB_Point4d(x, y, z, w);
	}

	/**
	 * Move to position.
	 *
	 * @param x
	 * @param y 
	 * @param z
	 * @return self
	 */
	public WB_Point4d moveTo(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Move to position.
	 *
	 * @param p point, vector or normal
	 * @return self
	 */
	public WB_Point4d moveTo(final WB_Point3d p) {
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
	public WB_Point4d moveBy(final double x, final double y, final double z) {
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
	public WB_Point4d moveBy(final WB_Point3d v) {
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
	 * @param result WB_XYZW to store result
	 */
	public void moveByInto(final double x, final double y, final double z,
			final WB_Point4d result) {
		result.x = this.x + x;
		result.y = this.y + y;
		result.z = this.z + z;
		result.w = w;
	}

	/**
	 * Move by vector.
	 *
	 * @param v point, vector or normal
	 * @param result WB_XYZW to store result
	 */
	public void moveByInto(final WB_Point3d v, final WB_Point4d result) {
		result.x = x + v.x;
		result.y = y + v.y;
		result.z = z + v.z;
		result.w = w;
	}

	/**
	 * Move by vector.
	 *
	 * @param x 
	 * @param y 
	 * @param z
	 * @return new WB_XYZW
	 */
	public WB_Point4d moveByAndCopy(final double x, final double y,
			final double z) {
		return new WB_Point4d(this.x + x, this.y + y, this.z + z, w);
	}

	/**
	 * Move by vector.
	 *
	 * @param v point, vector or normal
	 * @return new WB_XYZW
	 */
	public WB_Point4d moveByAndCopy(final WB_Point3d v) {
		return new WB_Point4d(x + v.x, y + v.y, z + v.z, w);
	}

	/**
	 * 
	 *
	 * @return self
	 */
	public WB_Point4d invert() {
		x *= -1;
		y *= -1;
		z *= -1;
		w *= -1;
		return this;
	}

	public double normalize() {
		final double d = mag();
		if (WB_Epsilon.isZero(d)) {
			set(0, 0, 0, 0);
		} else {
			set(x / d, y / d, z / d, w / d);
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
	public WB_Point4d scale(final double f) {
		x *= f;
		y *= f;
		z *= f;
		w *= f;
		return this;
	}

	/**
	 * Scale .
	 *
	 * @param f scale factor
	 * @param result WB_XYZW to store result
	 */
	public void scaleInto(final double f, final WB_Point4d result) {
		result.x = x * f;
		result.y = y * f;
		result.z = z * f;
		result.w = w * f;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return self
	 */
	public WB_Point4d add(final double x, final double y, final double z,
			final double w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
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
	public WB_Point4d add(final double x, final double y, final double z,
			final double w, final double f) {
		this.x += f * x;
		this.y += f * y;
		this.z += f * z;
		this.w += f * w;
		return this;
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return self
	 */
	public WB_Point4d add(final WB_Point4d p) {
		x += p.x;
		y += p.y;
		z += p.z;
		w += p.w;
		return this;
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return self
	 */
	public WB_Point4d add(final WB_Point4d p, final double f) {
		x += f * p.x;
		y += f * p.y;
		z += f * p.z;
		w += f * p.w;
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
			final double w, final WB_Point4d result) {
		result.x = (this.x + x);
		result.y = (this.y + y);
		result.z = (this.z + z);
		result.w = this.w + w;
	}

	/**
	 * 
	 *
	 * @param p 
	 * @param result 
	 */
	public void addInto(final WB_Point4d p, final WB_Point4d result) {
		result.x = x + p.x;
		result.y = y + p.y;
		result.z = z + p.z;
		result.w = w + p.w;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZW
	 */
	public WB_Point4d addAndCopy(final double x, final double y,
			final double z, final double w) {
		return new WB_Point4d(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZW
	 */
	public WB_Point4d addAndCopy(final double x, final double y,
			final double z, final double w, final double f) {
		return new WB_Point4d(this.x + f * x, this.y + f * y, this.z + f * z,
				this.w + f * w);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return new WB_XYZW
	 */
	public WB_Point4d addAndCopy(final WB_Point4d p) {
		return new WB_Point4d(x + p.x, y + p.y, z + p.z, w + p.w);
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return self
	 */
	public WB_Point4d sub(final double x, final double y, final double z,
			final double w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
		return this;
	}

	/**
	 * 
	 *
	 * @param v 
	 * @return self
	 */
	public WB_Point4d sub(final WB_Point4d v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
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
			final double w, final WB_Point4d result) {
		result.x = (this.x - x);
		result.y = (this.y - y);
		result.z = (this.z - z);
		result.w = this.w - w;
	}

	/**
	 * 
	 *
	 * @param p 
	 * @param result 
	 */
	public void subInto(final WB_Point4d p, final WB_Point4d result) {
		result.x = x - p.x;
		result.y = y - p.y;
		result.z = z - p.z;
		result.w = w - p.w;
	}

	/**
	 * 
	 *
	 * @param x 
	 * @param y 
	 * @param z 
	 * @return new WB_XYZW
	 */
	public WB_Point4d subAndCopy(final double x, final double y,
			final double z, final double w) {
		return new WB_Point4d(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	/**
	 * 
	 *
	 * @param p 
	 * @return new WB_XYZW
	 */
	public WB_Point4d subAndCopy(final WB_Point4d p) {
		return new WB_Point4d(x - p.x, y - p.y, z - p.z, w - p.w);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return self
	 */
	public WB_Point4d mult(final double f) {
		scale(f);
		return this;
	}

	/**
	 * 
	 *
	 * @param f 
	 * @param result 
	 */
	public void multInto(final double f, final WB_Point4d result) {
		scaleInto(f, result);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return new WB_XYZW
	 */
	public WB_Point4d multAndCopy(final double f) {
		return new WB_Point4d(x * f, y * f, z * f, w * f);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return self
	 */
	public WB_Point4d div(final double f) {
		return mult(1.0 / f);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @param result 
	 */
	public void divInto(final double f, final WB_Point4d result) {
		multInto(1.0 / f, result);
	}

	/**
	 * 
	 *
	 * @param f 
	 * @return new WB_XYZW
	 */
	public WB_Point4d divAndCopy(final double f) {
		return multAndCopy(1.0 / f);
	}

	/**
	 * Interpolate.
	 *
	 * @param p0 the p0
	 * @param p1 the p1
	 * @param t the t
	 * @return the w b_ point
	 */
	public static WB_Point4d interpolate(final WB_Point4d p0,
			final WB_Point4d p1, final double t) {
		return new WB_Point4d(p0.x + t * (p1.x - p0.x), p0.y + t
				* (p1.y - p0.y), p0.z + t * (p1.z - p0.z), p0.w + t
				* (p1.w - p0.w));

	}

}
