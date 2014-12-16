/**
 * 
 */
package wblut.math;

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;

/**
 * 
 * Random generator for vectors uniformly distributed in the unit cube.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomBox {
	private final WB_MTRandom	randomGen;

	public WB_RandomBox() {
		randomGen = new WB_MTRandom();
	}

	/**
	 * Set random seed.
	 *
	 * @param seed seed
	 * @return self
	 */
	public WB_RandomBox setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/**
	 * 
	 * @return 
	 */
	public WB_Point3d nextPoint() {
		return new WB_Point3d(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

	/**
	 * 
	 * @return
	 */
	public WB_Vector3d nextVector() {
		return new WB_Vector3d(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

	/**
	 * 
	 * @return
	 */
	public WB_Normal3d nextNormal() {
		return new WB_Normal3d(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

}
