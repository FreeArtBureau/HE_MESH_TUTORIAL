/**
 * 
 */
package wblut.hemesh;

import wblut.WB_Epsilon;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Polygon2D;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 *	Turns a single WB_Polygon2D into a mesh. If a thickness is specified
 *  the result is a solid, otherwise it's a surface.
 *
 */
public class HEC_Polygon extends HEC_Creator {
	private WB_Polygon	polygon;
	private double		thickness;

	public HEC_Polygon() {
		super();
		override = true;
	}

	public HEC_Polygon(final WB_Polygon2D poly, final double d) {
		this();
		override = true;
		polygon = poly.toPolygon();
		thickness = d;
	}

	public HEC_Polygon setPolygon(final WB_Polygon2D poly) {
		polygon = poly.toPolygon();
		return this;
	}

	public HEC_Polygon(final WB_Polygon poly, final double d) {
		this();
		override = true;
		polygon = poly;
		thickness = d;
	}

	public HEC_Polygon setPolygon(final WB_Polygon poly) {
		polygon = poly;
		return this;
	}

	public HEC_Polygon setThickness(final double d) {
		thickness = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (polygon == null) {
			return null;
		}
		final WB_Normal3d norm = polygon.getPlane().getNormal();
		final int n = polygon.getN();
		final boolean surf = WB_Epsilon.isZero(thickness);
		final WB_Point3d[] points = new WB_Point3d[surf ? n : 2 * n];
		for (int i = 0; i < n; i++) {
			points[i] = polygon.getPoint(i);

		}
		if (!surf) {
			for (int i = 0; i < n; i++) {
				points[n + i] = points[i].addAndCopy(norm, thickness);
			}
		}
		int[][] faces;
		if (surf) {
			faces = new int[1][n];
			for (int i = 0; i < n; i++) {
				faces[0][i] = i;
			}

		} else {
			faces = new int[n + 2][];
			faces[n] = new int[n];
			faces[n + 1] = new int[n];
			for (int i = 0; i < n; i++) {

				faces[n][i] = i;
				faces[n + 1][i] = 2 * n - 1 - i;
				faces[i] = new int[4];
				faces[i][0] = i;
				faces[i][3] = (i + 1) % n;
				faces[i][2] = n + (i + 1) % n;
				faces[i][1] = n + i;

			}

		}
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(points).setFaces(faces).setDuplicate(false);
		return fl.createBase().flipAllFaces();

	}

}
