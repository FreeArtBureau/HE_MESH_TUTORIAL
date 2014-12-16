package wblut.geom;

/**
 *  3D Triangle.
 */
public interface WB_Triangle2D {

	public WB_Point2d getCenter();

	public WB_Point2d getCentroid();

	public WB_Point2d getCircumcenter();

	public WB_Point2d getOrthocenter();

	public WB_Point2d getIncenter();

	public WB_Circle getCircumcircle();

	public WB_Circle getIncircle();

	public WB_Point2d getPointFromTrilinear(final double x, final double y,
			final double z);

	public WB_Point2d getPointFromBarycentric(final double x, final double y,
			final double z);

	public WB_Point3d getBarycentric(final WB_Point2d p);

	public WB_Point2d p1();

	public WB_Point2d p2();

	public WB_Point2d p3();

}
