/**
 * 
 */
package wblut.geom;

import wblut.WB_Epsilon;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Homogeneous extends WB_Point4d {

	private boolean	pointAtInfinity;

	/**
	 * Instantiates a new WB_Homogeneous.
	 */
	public WB_Homogeneous() {
		x = y = z = 0;
		w = 0;
		pointAtInfinity = false;
	}

	/**
	 * Instantiates a new WB_Homogeneous.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public WB_Homogeneous(final double x, final double y, final double z,
			final double w) {
		this.x = w * x;
		this.y = w * y;
		this.z = w * z;
		this.w = w;
		pointAtInfinity = false;
	}

	/**
	 * Instantiates a new WB_Homogeneous.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public WB_Homogeneous(final double x, final double y, final double z,
			final double w, final boolean atInfinity) {
		if (atInfinity) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = 0;
		} else {
			this.x = w * x;
			this.y = w * y;
			this.z = w * z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	/**
	 * Instantiates a new WB_Homogeneous.
	 *
	 * @param v
	 */
	public WB_Homogeneous(final WB_Homogeneous v) {
		w = v.w;
		x = v.x;
		y = v.y;
		z = v.z;

		pointAtInfinity = v.pointAtInfinity;
	}

	@Override
	public WB_Homogeneous get() {
		return new WB_Homogeneous(this);

	}

	/**
	 * Instantiates a new WB_Homogeneous.
	 *
	 * @param v
	 */
	public WB_Homogeneous(final WB_Point3d v, final double w) {
		x = w * v.x;
		y = w * v.y;
		z = w * v.z;
		this.w = w;
		pointAtInfinity = false;
	}

	/**
	 * Instantiates a new WB_Homogeneous.
	 *
	 * @param v
	 */
	public WB_Homogeneous(final WB_Point3d v, final double w,
			final boolean atInfinity) {

		if (atInfinity) {
			x = v.x;
			y = v.y;
			z = v.z;
			this.w = 0;
		} else {
			x = w * v.x;
			y = w * v.y;
			z = w * v.z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	/**
	 * Set coordinates.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	@Override
	public void set(final double x, final double y, final double z,
			final double w) {
		this.x = x * w;
		this.y = y * w;
		this.z = z * w;
		this.w = w;
		pointAtInfinity = false;
	}

	public void set(final double x, final double y, final double z,
			final double w, final boolean atInfinity) {
		if (atInfinity) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = 0;

		} else {
			this.x = w * x;
			this.y = w * y;
			this.z = w * z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	/**
	 * Set coordinates.
	 *
	 * @param v
	 */
	@Override
	public void set(final WB_Point3d v, final double w) {
		x = v.x * w;
		y = v.y * w;
		z = v.z * w;
		this.w = w;
		pointAtInfinity = false;
	}

	public void set(final WB_Point3d v, final double w, final boolean atInfinity) {

		if (atInfinity) {
			x = v.x;
			y = v.y;
			z = v.z;
			this.w = 0;

		} else {
			x = w * v.x;
			y = w * v.y;
			z = w * v.z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	/**
	 * Set coordinates.
	 *
	 * @param p
	 */
	public void set(final WB_Homogeneous p) {
		x = p.x;
		y = p.y;
		z = p.z;
		w = p.w;
		pointAtInfinity = p.pointAtInfinity;
	}

	/**
	 * Project to WB_XYZ.
	 *
	 * @return new WB_XYZ
	 */
	public WB_Point3d project() {
		if (pointAtInfinity) {
			return new WB_Point3d(x, y, z);
		} else if (WB_Epsilon.isZero(w)) {
			System.out.println("zero weight");
			return new WB_Point3d(0, 0, 0);
		}
		final double iw = 1.0 / w;
		return new WB_Point3d(x * iw, y * iw, z * iw);
	}

	public void setWeight(final double w) {
		final WB_Point3d p = project();
		set(p, w, pointAtInfinity);

	}

	/**
	 * Interpolate.
	 *
	 * @param p0 first homogeneous coord
	 * @param p1 second homogeneous coord
	 * @param t interpolation factor
	 * @return interpolated WB_Homogeneous
	 */
	public static WB_Homogeneous interpolate(final WB_Homogeneous p0,
			final WB_Homogeneous p1, final double t) {
		return new WB_Homogeneous(p0.x + t * (p1.x - p0.x), p0.y + t
				* (p1.y - p0.y), p0.z + t * (p1.z - p0.z), p0.w + t
				* (p1.w - p0.w));

	}

	public boolean isInfinite() {
		return pointAtInfinity;
	}

}
