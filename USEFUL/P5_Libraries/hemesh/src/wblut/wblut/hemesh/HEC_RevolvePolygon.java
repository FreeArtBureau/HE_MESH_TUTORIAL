/**
 * 
 */
package wblut.hemesh;

import wblut.geom.WB_Line;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Polygon2D;
import wblut.geom.WB_Vector3d;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 *	Turns a single WB_Polygon2D into a mesh. If a thickness is specified
 *  the result is a solid, otherwise it's a surface.
 *
 */
public class HEC_RevolvePolygon extends HEC_Creator {
	private WB_Polygon	polygon;
	private double		thickness;
	private WB_Line		axis;
	private double		angle;
	private int			facets;

	public HEC_RevolvePolygon() {
		super();
		override = true;
	}

	public HEC_RevolvePolygon(final WB_Polygon2D poly, final double d) {
		this();
		override = true;
		polygon = poly.toPolygon();
		thickness = d;
	}

	public HEC_RevolvePolygon setPolygon(final WB_Polygon2D poly) {
		polygon = poly.toPolygon();
		return this;
	}

	public HEC_RevolvePolygon(final WB_Polygon poly, final double d) {
		this();
		override = true;
		polygon = poly;
		thickness = d;
	}

	public HEC_RevolvePolygon setPolygon(final WB_Polygon poly) {
		polygon = poly;
		return this;
	}

	public HEC_RevolvePolygon setThickness(final double d) {
		thickness = d;
		return this;
	}

	public HEC_RevolvePolygon setAxis(final WB_Point3d p, final WB_Vector3d v) {
		axis = new WB_Line(p, v);
		return this;
	}

	@Override
	public HEC_RevolvePolygon setAxis(final double ox, final double oy,
			final double oz, final double vx, final double vy, final double vz) {
		axis = new WB_Line(new WB_Point3d(ox, oy, oz), new WB_Vector3d(vx, vy,
				vz));
		return this;
	}

	public HEC_RevolvePolygon setAngle(final double angle) {
		this.angle = angle;
		return this;
	}

	public HEC_RevolvePolygon setFacets(final int n) {
		facets = n;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if ((polygon == null) || (axis == null)) {
			return null;
		}
		final WB_Normal3d norm = polygon.getPlane().getNormal();
		final int n = polygon.getN();

		final WB_Point3d[] points = new WB_Point3d[n * (facets + 1)];
		final double da = angle / facets;
		int id = 0;
		for (int a = 0; a < facets + 1; a++) {
			for (int i = 0; i < n; i++) {
				points[id] = polygon.getPoint(i).get();
				points[id].rotateAboutAxis(a * da, axis.getOrigin(),
						axis.getDirection());
				id++;
			}

		}

		int[][] faces;

		faces = new int[(n - 1) * facets][];
		id = 0;
		for (int a = 0; a < facets; a++) {
			for (int i = 0; i < n - 1; i++) {
				faces[id] = new int[4];
				faces[id][0] = a * n + i;
				faces[id][1] = a * n + (i + 1);
				faces[id][2] = (a + 1) * n + (i + 1);
				faces[id][3] = (a + 1) * n + i;
				id++;
			}
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(points).setFaces(faces).setDuplicate(false);
		return fl.createBase().flipAllFaces();

	}
}
