/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_GeomGridCell {
	protected int					index;
	protected WB_AABB				aabb;
	protected ArrayList<WB_Point3d>	points;
	protected ArrayList<WB_Segment>	segments;

	public WB_GeomGridCell(final int index, final WB_Point3d min,
			final WB_Point3d max) {
		this.index = index;
		points = new ArrayList<WB_Point3d>();
		segments = new ArrayList<WB_Segment>();
		aabb = new WB_AABB(min, max);

	}

	public void addPoint(final WB_Point3d p) {
		points.add(p);
	}

	public void removePoint(final WB_Point3d p) {
		points.remove(p);
	}

	public void addSegment(final WB_Segment seg) {
		segments.add(seg);
	}

	public void removeSegment(final WB_Segment seg) {
		segments.remove(seg);
	}

	public ArrayList<WB_Point3d> getPoints() {
		return points;
	}

	public ArrayList<WB_Segment> getSegments() {
		return segments;
	}

	public int getIndex() {
		return index;
	}

	public WB_AABB getAABB() {
		return aabb;
	}

	public boolean isEmpty() {
		return points.isEmpty() && segments.isEmpty();

	}
}
