/**
 * 
 */
package wblut.math;


/**
 * Parameter class dependent on two variables. An implementation
 * of the WB_Function2D instance is passed upon instantiation.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_PlanarParameter2D<T> implements WB_Parameter<T> {
	/**
	 * 2D parameter function
	 */
	WB_Function2D<T>	value;

	public WB_PlanarParameter2D(final WB_Function2D<T> value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value()
	 */
	public T value() {
		return value.f(0, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double)
	 */
	public T value(final double x) {
		return value.f(x, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double, double)
	 */
	public T value(final double x, final double y) {
		return value.f(x, y);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double, double, double)
	 */
	public T value(final double x, final double y, final double z) {
		return value.f(x, y);
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.math.WB_Parameter#value(double, double, double, double)
	 */
	public T value(final double x, final double y, final double z,
			final double t) {
		return value.f(x, y);
	}

}
