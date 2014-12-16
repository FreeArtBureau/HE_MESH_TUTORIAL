/**
 * 
 */
package wblut;

import wblut.math.WB_Fast;

/**
 * WB_Epsilon contains functions for tolerance based floating point
 * calculations.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Epsilon {

	/** General precision. */
	static public double	EPSILON			= 1e-20;

	/** General precision when square is used > EPSILON*EPSILON. */
	static public double	SQEPSILON		= EPSILON * EPSILON;

	/** Plane thickness. */
	static public double	PLANE_EPSILON	= 1e-10;

	/** Random point jitter. */
	static public double	JITTER_EPSILON	= 1e-3;

	/**
	 * Clamp value to range with error margin.
	 *
	 * @param x
	 * @param min
	 * @param max
	 * @return clamped value
	 */
	public static double clampEpsilon(final double x, final double min,
			final double max) {
		if (x < min + EPSILON) {
			return min;
		}
		if (x > max - EPSILON) {
			return max;
		}
		return x;
	}

	/**
	 * Equality with error range. Absolute for small values, relative for large values
	 *
	 * @param x
	 * @param y
	 * @return true, if equal
	 */
	public static boolean isEqualHybrid(final double x, final double y) {
		return WB_Fast.abs(x - y) <= WB_Epsilon.EPSILON
				* WB_Fast.max(WB_Fast.abs(x), WB_Fast.abs(y), 1.0);
	}

	/**
	 * Equality with absolute error range.
	 *
	 * @param x
	 * @param y
	 * @return true, if equal
	 */
	public static boolean isEqualAbs(final double x, final double y) {
		return WB_Fast.abs(x - y) <= WB_Epsilon.EPSILON;
	}

	/**
	 * Equality with relative error range.
	 *
	 * @param x
	 * @param y
	 * @return true, if equal
	 */
	public static boolean isEqualRel(final double x, final double y) {
		return WB_Fast.abs(x - y) <= WB_Epsilon.EPSILON
				* WB_Fast.max(WB_Fast.abs(x), WB_Fast.abs(y));
	}

	/**
	 * Check if value is zero with error range.
	 *
	 * @param x
	 * @return true, if zero
	 */
	public static boolean isZero(final double x) {
		return WB_Fast.abs(x) <= WB_Epsilon.EPSILON;
	}

	/**
	 * Check if squared value is zero with error range.
	 *
	 * @param x
	 * @return true, if zero
	 */
	public static boolean isZeroSq(final double x) {
		return WB_Fast.abs(x) <= WB_Epsilon.SQEPSILON;
	}

	/**
	 * Compare with error margin using isEqualHybrid.
	 *
	 * @param x
	 * @param y
	 * @return 0 if equal, -1 if x<y, +1 if x>1
	 */
	public static int compareHybrid(final double x, final double y) {
		if (isEqualHybrid(x, y)) {// x and y in range -epsilon, epsilon
			return 0;
		}
		if (x > y) {
			return 1;
		}
		return -1;
	}

	/**
	 * Compare with error margin using isEqualAbs.
	 *
	 * @param x
	 * @param y
	 * @return 0 if equal, -1 if x<y, +1 if x>1
	 */
	public static int compareAbs(final double x, final double y) {
		if (isEqualAbs(x, y)) {// x and y in range -epsilon, epsilon
			return 0;
		}
		if (x > y) {
			return 1;
		}
		return -1;
	}

	/**
	 * Compare with error margin using isEqualRel.
	 *
	 * @param x
	 * @param y
	 * @return 0 if equal, -1 if x<y, +1 if x>1
	 */
	public static int compareRel(final double x, final double y) {
		if (isEqualRel(x, y)) {// x and y in range -epsilon, epsilon
			return 0;
		}
		if (x > y) {
			return 1;
		}
		return -1;
	}

}
