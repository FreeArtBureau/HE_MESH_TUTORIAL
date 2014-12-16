/**
 * 
 */
package wblut.math;

/**
 * Interface for a function of 3 variables
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface WB_Function3D<T> {
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return result
	 */
	public T f(double x, double y, double z);
}
