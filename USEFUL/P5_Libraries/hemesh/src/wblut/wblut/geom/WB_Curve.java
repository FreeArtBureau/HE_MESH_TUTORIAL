/**
 * 
 */
package wblut.geom;


/**
 * Interface for parameterized curves
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface WB_Curve {
	/**
	 * Retrieve the point at value u
	 * @param u
	 * @return WB_Point
	 */
	public WB_Point3d curvePoint(double u);

	/**
	 * Get the lower end of the parameter range
	 * @return u
	 */
	public double loweru();

	/**
	 * Get the upper end of the parameter range
	 * @return u
	 */
	public double upperu();
}
