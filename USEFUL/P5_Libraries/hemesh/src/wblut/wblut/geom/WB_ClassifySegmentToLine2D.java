/**
 * 
 */
package wblut.geom;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public enum WB_ClassifySegmentToLine2D {
	/** Segment on line. */
	SEGMENT_ON_LINE,
	/** Segment on positive side of line. */
	SEGMENT_IN_FRONT_OF_LINE,
	/** Segment on negative side of line. */
	SEGMENT_BEHIND_LINE,
	/** Segment spanning line. */
	SEGMENT_SPANNING_LINE
}