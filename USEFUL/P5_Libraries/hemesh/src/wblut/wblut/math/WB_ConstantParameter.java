/**
 * 
 */
package wblut.math;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 * A parameter which is constant, i.e. a single unchanging value.
 * As a generic class the value needs to be an object. For example,
 * Double, Float or Integer. Primitives such as double, float or int
 * cannot be passed as objects.
 *
 */
public class WB_ConstantParameter<T> implements WB_Parameter<T> {

	T	value;

	/**
	 * 
	 * 
	 * @param value constant value. 
	 */
	public WB_ConstantParameter(final T value) {
		this.value = value;
	}

	public T value(final double x, final double y, final double z) {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value()
	 */
	public T value() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double)
	 */
	public T value(final double x) {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double, double)
	 */
	public T value(final double x, final double y) {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double, double, double, double)
	 */
	public T value(final double x, final double y, final double z,
			final double t) {
		return value;
	}

}
