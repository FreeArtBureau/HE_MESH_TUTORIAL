/**
 * 
 */
package wblut.geom;

import java.util.Map;


/**
 * 
 *
 * WB_KDNeighbor stores entries from a nearest-neighbor search. It contains the
 * point, value and squared distance from the query point. 
 *
 * @author Frederik Vanhoutte, W:Blut
 */
public class WB_KDNeighbor2D<V> implements Comparable<WB_KDNeighbor2D<V>> {
	private final double	sqDistance;
	private final WB_Point2d		neighbor;
	private final V			value;

	WB_KDNeighbor2D(final double d2, final Map.Entry<WB_Point2d, V> neighbor) {
		sqDistance = d2;
		this.neighbor = neighbor.getKey();
		this.value = neighbor.getValue();
	}

	/**
	 * Squared distance to the query point
	 * @return squared distance
	 */
	public double sqDistance() {
		return sqDistance;
	}

	/**
	 * Value of neighbor
	 * @return value
	 */
	public V value() {
		return value;
	}

	/**
	 * Position of neighbor
	 * @return WB_Point
	 */
	public WB_Point2d point() {
		return neighbor;
	}

	public int compareTo(final WB_KDNeighbor2D<V> obj) {
		final double d = obj.sqDistance();
		if (sqDistance < d) {
			return -1;
		} else if (sqDistance > d) {
			return 1;
		}

		return 0;
	}
}