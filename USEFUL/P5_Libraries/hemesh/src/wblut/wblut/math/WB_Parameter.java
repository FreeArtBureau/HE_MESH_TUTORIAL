/**
 * 
 */
package wblut.math;


/**
 * Generic interface for parameter classes.
 * 
 * @author Frederik Vanhoutte, W:Blut
 */
public interface WB_Parameter<T> {
	/**
	 * 
	 * @return default value
	 */
	public T value();

	/**
	 * 
	 * @return value dependent on 1 variable
	 */
	public T value(double x);

	/**
	 * 
	 * @return value dependent on 2 variables
	 */
	public T value(double x, double y);

	/**
	 * 
	 * @return value dependent on 3 variables
	 */
	public T value(double x, double y, double z);

	/**
	 * 
	 * @return value dependent on 4 variables
	 */
	public T value(double x, double y, double z, double t);

}
