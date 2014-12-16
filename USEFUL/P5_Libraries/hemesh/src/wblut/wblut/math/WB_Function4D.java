/**
 * 
 */
package wblut.math;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface WB_Function4D<T> {
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return result
	 */
	public T f(double x, double y, double z, double w);
}
