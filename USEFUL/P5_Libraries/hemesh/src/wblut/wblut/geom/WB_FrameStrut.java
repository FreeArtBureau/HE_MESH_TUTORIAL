/**
 * 
 */
package wblut.geom;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_FrameStrut {
	private final WB_FrameNode	start;
	private final WB_FrameNode	end;
	private final int		index;
	private double			radiuss;
	private double			radiuse;
	private double			offsets;
	private double			offsete;

	public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e, final int id) {
		start = s;
		end = e;
		index = id;
	}

	public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e, final int id,
			final double r) {
		start = s;
		end = e;
		index = id;
		radiuss = radiuse = r;
	}

	public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e, final int id,
			final double rs, final double re) {
		start = s;
		end = e;
		index = id;
		radiuss = rs;
		radiuse = re;
	}

	public WB_FrameNode start() {
		return start;
	}

	public WB_FrameNode end() {
		return end;
	}

	public int getStartIndex() {
		return start.getIndex();
	}

	public int getEndIndex() {
		return end.getIndex();
	}

	public int getIndex() {
		return index;
	}

	public WB_Vector3d toVector() {
		return end().subToVector(start());
	}

	public WB_Vector3d toNormVector() {
		final WB_Vector3d v = end().subToVector(start());
		v.normalize();
		return v;
	}

	public double getSqLength() {
		return WB_Distance.sqDistance(end(), start());
	}

	public double getLength() {
		return WB_Distance.distance(end(), start());
	}

	public double getRadiusStart() {
		return radiuss;
	}

	public double getRadiusEnd() {
		return radiuse;
	}

	public void setRadiusStart(final double r) {
		radiuss = r;
	}

	public void setRadiusEnd(final double r) {
		radiuse = r;
	}

	public double getOffsetStart() {
		return offsets;
	}

	public double getOffsetEnd() {
		return offsete;
	}

	public void setOffsetStart(final double o) {
		offsets = o;
	}

	public void setOffsetEnd(final double o) {
		offsete = o;
	}

	public WB_Point3d getCenter() {
		return end().addAndCopy(start()).mult(0.5);
	}

	public WB_ExplicitSegment toSegment() {
		return new WB_ExplicitSegment(start, end);
	}

	public WB_Plane toPlane() {
		return new WB_Plane(start().toPoint(), toVector());
	}

}
