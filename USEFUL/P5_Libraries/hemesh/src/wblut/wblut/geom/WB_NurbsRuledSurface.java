/**
 * 
 */
package wblut.geom;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_NurbsRuledSurface {
	public static WB_BSplineSurface getRuledSurface(WB_BSpline CA, WB_BSpline CB) {
		if ((CA.loweru() != CB.loweru()) || (CA.upperu() != CB.upperu())) {
			throw new IllegalArgumentException(
					"Curves not defined on same parameter range.");
		}
		int degree;
		final int degreeA = CA.p();
		final int degreeB = CB.p();
		if (degreeA < degreeB) {
			degree = degreeB;
			CA = CA.elevateDegree(degreeB - degreeA);
		} else if (degreeB < degreeA) {
			degree = degreeA;
			CB = CB.elevateDegree(degreeA - degreeB);
		} else {
			degree = degreeA;
		}
		final WB_NurbsKnot mergedKnot = WB_NurbsKnot.merge(CA.knot(), CB.knot());
		CA = CA.refineKnot(mergedKnot);
		CB = CB.refineKnot(mergedKnot);
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final int nocp = mergedKnot.n() + 1;
		final WB_Point3d[][] controlPoints = new WB_Point3d[nocp][2];
		for (int i = 0; i < nocp; i++) {
			controlPoints[i][0] = CA.points()[i];
			controlPoints[i][1] = CB.points()[i];
		}

		return new WB_BSplineSurface(controlPoints, mergedKnot, VKnot);
	}

	public static WB_RBSplineSurface getRuledSurface(WB_RBSpline CA,
			WB_RBSpline CB) {
		if ((CA.loweru() != CB.loweru()) || (CA.upperu() != CB.upperu())) {
			throw new IllegalArgumentException(
					"Curves not defined on same parameter range.");
		}
		int degree;
		final int degreeA = CA.p();
		final int degreeB = CB.p();
		if (degreeA < degreeB) {
			degree = degreeB;
			CA = CA.elevateDegree(degreeB - degreeA);
		} else if (degreeB < degreeA) {
			degree = degreeA;
			CB = CB.elevateDegree(degreeA - degreeB);
		} else {
			degree = degreeA;
		}
		final WB_NurbsKnot mergedKnot = WB_NurbsKnot.merge(CA.knot(), CB.knot());
		CA = CA.refineKnot(mergedKnot);
		CB = CB.refineKnot(mergedKnot);
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final int nocp = mergedKnot.n() + 1;
		final WB_Homogeneous[][] controlPoints = new WB_Homogeneous[nocp][2];
		for (int i = 0; i < nocp; i++) {
			controlPoints[i][0] = CA.wpoints[i];
			controlPoints[i][1] = CB.wpoints[i];
		}

		return new WB_RBSplineSurface(controlPoints, mergedKnot, VKnot);
	}
}
