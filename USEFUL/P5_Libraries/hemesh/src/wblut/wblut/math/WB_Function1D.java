/**
 * 
 */
package wblut.math;

/**
 * Interface for a function of 1 variable
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface WB_Function1D<T> {
	/**
	 * 
	 * @param x
	 * @return result
	 */
	public T f(double x);
}
