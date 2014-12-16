/**
 * 
 */
package wblut.hemesh;

import wblut.WB_Epsilon;
import wblut.geom.WB_ExplicitPolygon;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 *	Creates a rectangular prism. If a thickness is specified
 *  the result is a solid, otherwise it's a surface, a regular polygon.
 *
 */
public class HEC_Prism extends HEC_Creator {
	private int		facets;
	private double	thickness;
	private double	radius;

	public HEC_Prism() {
		super();
	}

	public HEC_Prism(final int n, final double r, final double d) {
		this();
		facets = n;
		thickness = d;
		radius = r;
	}

	public HEC_Prism setFacets(final int n) {
		facets = n;
		return this;
	}

	public HEC_Prism setHeight(final double d) {
		thickness = d;
		return this;
	}

	public HEC_Prism setRadius(final double r) {
		radius = r;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if ((facets < 3) || (WB_Epsilon.isZero(radius))) {
			return null;
		}

		final WB_Point3d[] ppoints = new WB_Point3d[facets];
		for (int i = 0; i < facets; i++) {
			final double x = radius * Math.cos(Math.PI * 2.0 / facets * i);
			final double y = radius * Math.sin(Math.PI * 2.0 / facets * i);
			ppoints[i] = new WB_Point3d(x, y, 0.0);

		}
		final WB_ExplicitPolygon polygon = new WB_ExplicitPolygon(ppoints,
				facets);
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
