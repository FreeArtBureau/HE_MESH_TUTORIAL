/**
 * 
 */
package wblut.geom;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public enum WB_ClassifyPolygonToLine2D {
	/** Polygon on positive side of line. */
	POLYGON_IN_FRONT_OF_LINE,
	/** Polygon on negative side of line. */
	POLYGON_BEHIND_LINE,
	/** Polygon spanning line. */
	POLYGON_SPANNING_LINE
}