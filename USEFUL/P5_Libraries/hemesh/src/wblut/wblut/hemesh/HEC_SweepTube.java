/*
 * Copyright (c) 2010, Frederik Vanhoutte This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * http://creativecommons.org/licenses/LGPL/2.1/ This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package wblut.hemesh;

import wblut.WB_Epsilon;
import wblut.geom.WB_BSpline;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;



/**
 * Circle swept along curve.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_SweepTube extends HEC_Creator {

	/** Base radius. */
	private double		R;

	/** Facets. */
	private int			facets;

	/** Steps along curve. */
	private int			steps;

	private WB_BSpline	curve;

	private boolean		topcap;
	private boolean		bottomcap;

	public HEC_SweepTube() {
		super();
		R = 0;
		facets = 6;
		steps = 1;
		topcap = true;
		bottomcap = true;
	}

	public HEC_SweepTube(final double R, final int facets, final int steps,
			final WB_BSpline curve) {
		this();
		this.R = R;
		this.facets = facets;
		this.steps = steps;
		this.curve = curve;
	}

	/**
	 * Set fixed radius.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_SweepTube setRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set vertical divisions.
	 *
	 * @param steps vertical divisions
	 * @return self
	 */
	public HEC_SweepTube setSteps(final int steps) {
		this.steps = steps;
		return this;
	}

	/**
	 * Set number of sides.
	 *
	 * @param facets number of sides
	 * @return self
	 */
	public HEC_SweepTube setFacets(final int facets) {
		this.facets = facets;
		return this;
	}

	/**
	 * Set capping options.
	 *
	 * @param topcap create top cap?
	 * @param bottomcap create bottom cap?
	 * @return self
	 */
	public HEC_SweepTube setCap(final boolean topcap, final boolean bottomcap) {
		this.topcap = topcap;
		this.bottomcap = bottomcap;
		return this;
	}

	public HEC_SweepTube setCurve(final WB_BSpline curve) {
		this.curve = curve;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {

		final WB_Point3d[] vertices = new WB_Point3d[(steps + 1) * facets];
		final WB_Point3d[] basevertices = new WB_Point3d[facets];

		final double da = 2 * Math.PI / facets;
		for (int i = 0; i < facets; i++) {
			// normal(0,0,1);
			basevertices[i] = new WB_Point3d(R * Math.cos(da * i), R
					* Math.sin(da * i), 0);

		}
		final double ds = 1.0 / steps;
		WB_Point3d onCurve;
		WB_Vector3d deriv;
		WB_Vector3d oldderiv = new WB_Vector3d(0, 0, 1);
		final WB_Vector3d Z = new WB_Vector3d(0, 0, 1);
		final WB_Point3d origin = new WB_Point3d(0, 0, 0);
		for (int i = 0; i < steps + 1; i++) {
			onCurve = curve.curvePoint(i * ds);
			if (curve.p() == 1) {
				deriv = new WB_Vector3d(0, 0, 0);
				if (i > 0) {
					deriv.add(onCurve.subToVector(curve
							.curvePoint((i - 1) * ds)));
				}
				if (i < steps) {
					deriv.add(curve.curvePoint((i + 1) * ds).subToVector(
							onCurve));
				}
				deriv.normalize();
			} else {
				deriv = curve.curveFirstDeriv(i * ds);
			}
			final WB_Vector3d axis = oldderiv.cross(deriv);
			final double angle = Math.acos(oldderiv.dot(deriv));
			for (int j = 0; j < facets; j++) {
				if (!WB_Epsilon.isZeroSq(axis.mag2())) {
					basevertices[j].rotateAboutAxis(angle, origin, axis);
				}
				vertices[j + i * facets] = new WB_Point3d(basevertices[j]);
				vertices[j + i * facets].add(onCurve);
			}
			oldderiv = deriv;
		}
		int nfaces = steps * facets;
		int bc = 0;
		int tc = 0;
		if (bottomcap) {
			bc = nfaces;
			nfaces++;
		}
		if (topcap) {
			tc = nfaces;
			nfaces++;
		}
		final int[][] faces = new int[nfaces][];

		if (bottomcap) {
			faces[bc] = new int[facets];
		}
		if (topcap) {
			faces[tc] = new int[facets];
		}
		for (int j = 0; j < facets; j++) {
			if (bottomcap) {
				faces[bc][facets - 1 - j] = j;
			}
			if (topcap) {
				faces[tc][j] = steps * facets + j;
			}
			for (int i = 0; i < steps; i++) {
				faces[j + i * facets] = new int[4];
				faces[j + i * facets][0] = j + i * facets;
				faces[j + i * facets][3] = j + i * facets + facets;
				faces[j + i * facets][2] = ((j + 1) % facets) + facets + i
						* facets;
				faces[j + i * facets][1] = (j + 1) % facets + i * facets;
			}
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();

	}

}
