/**
 * 
 */
package wblut.geom;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Triangulation2D {
	/**
	 * Planar Delaunay triangulation.
	 *
	 * @param points 
	 * @param numPoints 
	 * @return 2D array of WB_Triangle2D
	 */
	public static WB_IndexedTriangle2D[] triangulate(final WB_Point2d[] points,
			final int numPoints) {

		final Triangulation tri = new Triangulation();
		tri.startWithBoundary(points);
		final WB_Point2d[] tripoints = tri.getPoints();
		return tri.getIndexedTriangles(tripoints);

	}

}
