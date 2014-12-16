/**
 * 
 */
package wblut.math;

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;

/**
 * 
 * Random generator for vectors uniformly distributed inside the unit disk.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomDisc {
	private final WB_MTRandom	randomGen;

	public WB_RandomDisc() {
		randomGen = new WB_MTRandom();
	}

	/**
	 * Set random seed.
	 *
	 * @param seed seed
	 * @return self
	 */
	public WB_RandomDisc setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/**
	 * 
	 * @return next random WB_Point inside unit disk
	 */
	public WB_Point3d nextPoint() {
		final double r = Math.sqrt(randomGen.nextDouble());
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Point3d(r * Math.cos(t), r * Math.sin(t), 0);
	}

	/**
	 * 
	 * @return next random WB_Vector inside unit disk
	 */
	public WB_Vector3d nextVector() {
		final double r = Math.sqrt(randomGen.nextDouble());
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Vector3d(r * Math.cos(t), r * Math.sin(t), 0);
	}

	/**
	 * 
	 * @return next random WB_Normal inside unit disk
	 */
	public WB_Normal3d nextNormal() {
		final double r = Math.sqrt(randomGen.nextDouble());
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Normal3d(r * Math.cos(t), r * Math.sin(t), 0);
	}

}