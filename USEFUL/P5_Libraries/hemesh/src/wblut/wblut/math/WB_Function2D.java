/**
 * 
 */
package wblut.math;

/**
 * Interface for a function of 2 variables
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface WB_Function2D<T> {
	/**
	 * 
	 * @param x
	 * @param y
	 * @return result
	 */
	public T f(double x, double y);

}
