/**
 * 
 */
package wblut.geom;

import wblut.math.WB_DoubleDouble;

/**
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_Predicates {
	private static double orientErrorBound = -1;
	private static double insphereErrorBound = -1;

	private static double findMachEpsilon() {
		double epsilon, check, lastcheck;
		epsilon = 1.0;
		check = 1.0;
		do {
			lastcheck = check;
			epsilon *= 0.5;
			check = 1.0 + epsilon;
		} while ((check != 1.0) && (check != lastcheck));
		return epsilon;
	}

	private static void init() {
		final double epsilon = findMachEpsilon();

		orientErrorBound = (7.0 + 56.0 * epsilon) * epsilon;
		insphereErrorBound = (16.0 + 224.0 * epsilon) * epsilon;
	}

	// >0 if pd below plane defined by pa,pb,pc
	// <0 if above (pa,pb,pc are ccw viewed from above)
	// = 0 if on plane
	public static double orient(final WB_Point3d pa, final WB_Point3d pb,
			final WB_Point3d pc, final WB_Point3d pd) {
		if (orientErrorBound == -1) {
			init();
		}

		final double adx = pa.x - pd.x, bdx = pb.x - pd.x, cdx = pc.x
				- pd.x;
		final double ady = pa.y - pd.y, bdy = pb.y - pd.y, cdy = pc.y
				- pd.y;
		double adz = pa.z - pb.z, bdz = pb.z - pd.x, cdz = pc.z
				- pd.z;

		double adxbdy = adx * bdy;
		double adybdx = ady * bdx;
		double adxcdy = adx * cdy;
		double adycdx = ady * cdx;
		double bdxcdy = bdx * cdy;
		double bdycdx = bdy * cdx;

		final double m1 = adxbdy - adybdx;
		final double m2 = adxcdy - adycdx;
		final double m3 = bdxcdy - bdycdx;
		final double det = m1 * cdz - m2 * bdz + m3 * adz;

		if (adxbdy < 0) {
			adxbdy = -adxbdy;
		}
		if (adybdx < 0) {
			adybdx = -adybdx;
		}
		if (adxcdy < 0) {
			adxcdy = -adxcdy;
		}
		if (adycdx < 0) {
			adycdx = -adycdx;
		}
		if (bdxcdy < 0) {
			bdxcdy = -bdxcdy;
		}
		if (bdycdx < 0) {
			bdycdx = -bdycdx;
		}
		if (adz < 0) {
			adz = -adz;
		}
		if (bdz < 0) {
			bdz = -bdz;
		}
		if (cdz < 0) {
			cdz = -cdz;
		}

		double errbound = (adxbdy + adybdx) * cdz + (adxcdy + adycdx) * bdz
				+ (bdxcdy + bdycdx) * adz;
		errbound *= orientErrorBound;

		if (det >= errbound) {
			return (det > 0) ? 1 : -1;

		} else if (-det >= errbound) {
			return (det > 0) ? 1 : -1;
		} else {
			return orientExact(pa, pb, pc, pd);
		}

	}

	public static double orientExact(final WB_Point3d pa, final WB_Point3d pb,
			final WB_Point3d pc, final WB_Point3d pd) {
		WB_DoubleDouble ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz;
		WB_DoubleDouble adx, bdx, cdx, ady, bdy, cdy, adz, bdz, cdz;
		WB_DoubleDouble m1, m2, m3;
		WB_DoubleDouble det;

		det = WB_DoubleDouble.ZERO;

		ax = new WB_DoubleDouble(pa.x);
		ay = new WB_DoubleDouble(pa.y);
		az = new WB_DoubleDouble(pa.z);
		bx = new WB_DoubleDouble(pb.x);
		by = new WB_DoubleDouble(pb.y);
		bz = new WB_DoubleDouble(pb.z);
		cx = new WB_DoubleDouble(pc.x);
		cy = new WB_DoubleDouble(pc.y);
		cz = new WB_DoubleDouble(pc.z);
		dx = new WB_DoubleDouble(pd.x).negate();
		dy = new WB_DoubleDouble(pd.y).negate();
		dz = new WB_DoubleDouble(pd.z).negate();

		adx = ax.add(dx);
		bdx = bx.add(dx);
		cdx = cx.add(dx);
		ady = ay.add(dy);
		bdy = by.add(dy);
		cdy = cy.add(dy);
		adz = az.add(dz);
		bdz = bz.add(dz);
		cdz = cz.add(dz);

		m1 = adx.multiply(bdy).subtract(ady.multiply(bdx));
		m2 = adx.multiply(cdy).subtract(ady.multiply(cdx));
		m3 = bdx.multiply(cdy).subtract(bdy.multiply(cdx));

		det = m1.multiply(cdz).add(m3.multiply(adz)).subtract(m2.multiply(bdz));

		return det.compareTo(WB_DoubleDouble.ZERO);
	}

	// >0 if pe inside sphere through pa,pb,pc,pd (if orient3d(pa,pb,pc,pd)>0))
	// <0 if pe outside sphere through pa,pb,pc,pd (if orient3d(pa,pb,pc,pd)>0))
	// =0 if on sphere
	public static double insphere(final WB_Point3d pa, final WB_Point3d pb,
			final WB_Point3d pc, final WB_Point3d pd, final WB_Point3d pe) {

		if (insphereErrorBound == -1) {
			init();
		}
		double aex, bex, cex, dex;
		double aey, bey, cey, dey;
		double aez, bez, cez, dez;
		double aexbey, bexaey, bexcey, cexbey, cexdey, dexcey, dexaey, aexdey;
		double aexcey, cexaey, bexdey, dexbey;
		double alift, blift, clift, dlift;
		double ab, bc, cd, da, ac, bd;
		double abc, bcd, cda, dab;
		double aezplus, bezplus, cezplus, dezplus;
		double aexbeyplus, bexaeyplus, bexceyplus, cexbeyplus;
		double cexdeyplus, dexceyplus, dexaeyplus, aexdeyplus;
		double aexceyplus, cexaeyplus, bexdeyplus, dexbeyplus;
		double det;
		double permanent, errbound;

		aex = pa.x - pe.x;
		bex = pb.x - pe.x;
		cex = pc.x - pe.x;
		dex = pd.x - pe.x;
		aey = pa.y - pe.y;
		bey = pb.y - pe.y;
		cey = pc.y - pe.y;
		dey = pd.y - pe.y;
		aez = pa.z - pe.z;
		bez = pb.z - pe.z;
		cez = pc.z - pe.z;
		dez = pd.z - pe.z;

		aexbey = aex * bey;
		bexaey = bex * aey;
		ab = aexbey - bexaey;
		bexcey = bex * cey;
		cexbey = cex * bey;
		bc = bexcey - cexbey;
		cexdey = cex * dey;
		dexcey = dex * cey;
		cd = cexdey - dexcey;
		dexaey = dex * aey;
		aexdey = aex * dey;
		da = dexaey - aexdey;

		aexcey = aex * cey;
		cexaey = cex * aey;
		ac = aexcey - cexaey;
		bexdey = bex * dey;
		dexbey = dex * bey;
		bd = bexdey - dexbey;

		abc = aez * bc - bez * ac + cez * ab;
		bcd = bez * cd - cez * bd + dez * bc;
		cda = cez * da + dez * ac + aez * cd;
		dab = dez * ab + aez * bd + bez * da;

		alift = aex * aex + aey * aey + aez * aez;
		blift = bex * bex + bey * bey + bez * bez;
		clift = cex * cex + cey * cey + cez * cez;
		dlift = dex * dex + dey * dey + dez * dez;

		det = (dlift * abc - clift * dab) + (blift * cda - alift * bcd);

		aezplus = Math.abs(aez);
		bezplus = Math.abs(bez);
		cezplus = Math.abs(cez);
		dezplus = Math.abs(dez);
		aexbeyplus = Math.abs(aexbey);
		bexaeyplus = Math.abs(bexaey);
		bexceyplus = Math.abs(bexcey);
		cexbeyplus = Math.abs(cexbey);
		cexdeyplus = Math.abs(cexdey);
		dexceyplus = Math.abs(dexcey);
		dexaeyplus = Math.abs(dexaey);
		aexdeyplus = Math.abs(aexdey);
		aexceyplus = Math.abs(aexcey);
		cexaeyplus = Math.abs(cexaey);
		bexdeyplus = Math.abs(bexdey);
		dexbeyplus = Math.abs(dexbey);
		permanent = ((cexdeyplus + dexceyplus) * bezplus
				+ (dexbeyplus + bexdeyplus) * cezplus + (bexceyplus + cexbeyplus)
				* dezplus)
				* alift
				+ ((dexaeyplus + aexdeyplus) * cezplus
						+ (aexceyplus + cexaeyplus) * dezplus + (cexdeyplus + dexceyplus)
						* aezplus)
				* blift
				+ ((aexbeyplus + bexaeyplus) * dezplus
						+ (bexdeyplus + dexbeyplus) * aezplus + (dexaeyplus + aexdeyplus)
						* bezplus)
				* clift
				+ ((bexceyplus + cexbeyplus) * aezplus
						+ (cexaeyplus + aexceyplus) * bezplus + (aexbeyplus + bexaeyplus)
						* cezplus) * dlift;
		errbound = insphereErrorBound * permanent;
		if ((det > errbound) || (-det > errbound)) {
			return (det > 0) ? 1 : -1;
		}

		return insphereExact(pa, pb, pc, pd, pe);
	}

	public static double insphereExact(final WB_Point3d pa,
			final WB_Point3d pb, final WB_Point3d pc, final WB_Point3d pd,
			final WB_Point3d pe) {
		WB_DoubleDouble ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz, ex, ey, ez;
		WB_DoubleDouble aex, bex, cex, dex;
		WB_DoubleDouble aey, bey, cey, dey;
		WB_DoubleDouble aez, bez, cez, dez;
		WB_DoubleDouble aexbey, bexaey, bexcey, cexbey, cexdey, dexcey, dexaey, aexdey;
		WB_DoubleDouble aexcey, cexaey, bexdey, dexbey;
		WB_DoubleDouble alift, blift, clift, dlift;
		WB_DoubleDouble ab, bc, cd, da, ac, bd;
		WB_DoubleDouble abc, bcd, cda, dab;
		WB_DoubleDouble det;

		det = WB_DoubleDouble.ZERO;

		ax = new WB_DoubleDouble(pa.x);
		ay = new WB_DoubleDouble(pa.y);
		az = new WB_DoubleDouble(pa.z);
		bx = new WB_DoubleDouble(pb.x);
		by = new WB_DoubleDouble(pb.y);
		bz = new WB_DoubleDouble(pb.z);
		cx = new WB_DoubleDouble(pc.x);
		cy = new WB_DoubleDouble(pc.y);
		cz = new WB_DoubleDouble(pc.z);
		dx = new WB_DoubleDouble(pd.x);
		dy = new WB_DoubleDouble(pd.y);
		dz = new WB_DoubleDouble(pd.z);
		ex = new WB_DoubleDouble(pe.x).negate();
		ey = new WB_DoubleDouble(pe.y).negate();
		ez = new WB_DoubleDouble(pe.z).negate();

		aex = ax.add(ex);
		bex = bx.add(ex);
		cex = cx.add(ex);
		dex = dx.add(ex);
		aey = ay.add(ey);
		bey = by.add(ey);
		cey = cy.add(ey);
		dey = dy.add(ey);
		aez = az.add(ez);
		bez = bz.add(ez);
		cez = cz.add(ez);
		dez = dz.add(ez);

		aexbey = aex.multiply(bey);
		bexaey = bex.multiply(aey);
		ab = aexbey.subtract(bexaey);
		bexcey = bex.multiply(cey);
		cexbey = cex.multiply(bey);
		bc = bexcey.subtract(cexbey);
		cexdey = cex.multiply(dey);
		dexcey = dex.multiply(cey);
		cd = cexdey.subtract(dexcey);
		dexaey = dex.multiply(aey);
		aexdey = aex.multiply(dey);
		da = dexaey.subtract(aexdey);
		aexcey = aex.multiply(cey);
		cexaey = cex.multiply(aey);
		ac = aexcey.subtract(cexaey);
		bexdey = bex.multiply(dey);
		dexbey = dex.multiply(bey);
		bd = bexdey.subtract(dexbey);

		abc = aez.multiply(bc).add(cez.multiply(ab)).subtract(bez.multiply(ac));
		bcd = bez.multiply(cd).add(dez.multiply(bc)).subtract(cez.multiply(bd));
		cda = cez.multiply(da).add(aez.multiply(cd)).subtract(dez.multiply(ac));
		dab = dez.multiply(ab).add(bez.multiply(da)).subtract(aez.multiply(bd));

		alift = aex.sqr().add(aey.sqr()).add(aez.sqr());
		blift = bex.sqr().add(bey.sqr()).add(bez.sqr());
		clift = cex.sqr().add(cey.sqr()).add(cez.sqr());
		dlift = dex.sqr().add(dey.sqr()).add(dez.sqr());

		det = dlift.multiply(abc).subtract(clift.multiply(dab))
				.add(blift.multiply(cda)).subtract(alift.multiply(bcd));

		return det.compareTo(WB_DoubleDouble.ZERO);
	}

	// >0 if pe inside sphere through pa,pb,pc,pd (regardless of
	// orient3d(pa,pb,pc,pd))
	// <0 if pe outside sphere through pa,pb,pc,pd (regardless of
	// orient3d(pa,pb,pc,pd))
	// =0 if on sphere
	public static double inSphereOrient(final WB_Point3d pa,
			final WB_Point3d pb, final WB_Point3d pc, final WB_Point3d pd,
			final WB_Point3d pe) {

		if (orient(pa, pb, pc, pd) > 0) {
			return insphere(pa, pb, pc, pd, pe);
		}
		final double is = insphere(pa, pb, pc, pd, pe);
		if (is > 0) {
			return -1;
		}
		if (is < 0) {
			return 1;
		}
		return 0;

	}

	// diffsides returns true if q1 and q2 are NOT on the same side of the plane
	// expanded by p1,p2, and p3.
	public static boolean diffSides(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d p3, final WB_Point3d q1, final WB_Point3d q2) {
		double a, b;
		a = orient(p1, p2, p3, q1);
		b = orient(p1, p2, p3, q2);
		return ((a > 0 && b < 0) || (a < 0 && b > 0));
	}

	public static boolean insideTetrahedron(final WB_Point3d p1,
			final WB_Point3d p2, final WB_Point3d p3, final WB_Point3d p4,
			final WB_Point3d q) {

		return (!diffSides(p1, p2, p3, q, p4) && !diffSides(p2, p3, p4, q, p1)
				&& !diffSides(p1, p2, p4, q, p3) && !diffSides(p1, p3, p4, q,
					p2));
	}

}
