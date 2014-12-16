/**
 * 
 */
package wblut.geom;

import wblut.math.WB_Fast;

/**
 * Linear blend of two surfaces.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_SurfaceBlend implements WB_Surface {
	private final WB_Surface	surfA;
	private final WB_Surface	surfB;

	/**
	 * Instantiate new surface blend
	 * 
	 * @param surfA first source surface
	 * @param surfB second source surface
	 */
	public WB_SurfaceBlend(final WB_Surface surfA, final WB_Surface surfB) {
		this.surfA = surfA;
		this.surfB = surfB;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#loweru()
	 */
	public double loweru() {
		return WB_Fast.max(surfA.loweru(), surfB.loweru());
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#lowerv()
	 */
	public double lowerv() {
		return WB_Fast.max(surfA.lowerv(), surfB.lowerv());

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#surfacePoint(double, double)
	 */
	public WB_Point3d surfacePoint(final double u, final double v) {
		return (surfA.surfacePoint(u, v).add(surfB.surfacePoint(u, v)))
				.mult(0.5);

	}

	/**
	 * return blended point with factor t. 0 gives first surfaces, 1 gives second surface.
	 * 
	 * @param u
	 * @param v
	 * @param t blend parameter, typically [0,1]
	 * @return blended point
	 */
	public WB_Point3d surfacePoint(final double u, final double v, final double t) {
		if (t == 0) {
			return surfA.surfacePoint(u, v);
		}
		if (t == 1) {
			return surfB.surfacePoint(u, v);
		}
		final WB_Point3d A = surfA.surfacePoint(u, v);
		return A.add(surfB.surfacePoint(u, v).sub(A), t);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#upperu()
	 */
	public double upperu() {
		return WB_Fast.min(surfA.upperu(), surfB.upperu());
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.nurbs.WB_Surface#upperv()
	 */
	public double upperv() {
		return WB_Fast.min(surfA.upperv(), surfB.upperv());
	}

}
