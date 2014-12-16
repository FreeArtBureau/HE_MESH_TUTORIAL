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
import wblut.geom.WB_Vector3d;



/**
 * Cylinder.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_Cylinder extends HEC_Creator {

	/** Base radius. */
	private double	Ri;

	/** Top Radius. */
	private double	Ro;

	/** Height. */
	private double	H;

	/** Facets. */
	private int		facets;

	/** Height steps. */
	private int		steps;

	private boolean	topcap;
	private boolean	bottomcap;
	private double	taper;

	/**
	 * Instantiates a new cylinder.
	 *
	 */
	public HEC_Cylinder() {
		super();
		Ri = 0;
		Ro = 0;
		H = 0;
		facets = 6;
		steps = 1;
		Z = WB_Vector3d.Y();
		topcap = true;
		bottomcap = true;
		taper = 1.0;
	}

	/**
	 * Instantiates a new cylinder. 
	 * 
	 * @param Ri bottom radius
	 * @param Ro top radius
	 * @param H height
	 * @param facets number of facets
	 * @param steps number of height divisions
	 */
	public HEC_Cylinder(final double Ri, final double Ro, final double H,
			final int facets, final int steps) {
		this();
		this.Ri = Ri;
		this.Ro = Ro;
		this.H = H;
		this.facets = facets;
		this.steps = steps;
		taper = 1.0;
	}

	/**
	 * Set fixed radius.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Cylinder setRadius(final double R) {
		Ri = R;
		Ro = R;
		return this;
	}

	/**
	 * Set lower and upper radius.
	 *
	 * @param Ri lower radius
	 * @param Ro upper radius
	 * @return self
	 */
	public HEC_Cylinder setRadius(final double Ri, final double Ro) {
		this.Ri = Ri;
		this.Ro = Ro;
		return this;
	}

	/**
	 * set height.
	 *
	 * @param H height
	 * @return self
	 */
	public HEC_Cylinder setHeight(final double H) {
		this.H = H;
		return this;
	}

	/**
	 * Set vertical divisions.
	 *
	 * @param steps vertical divisions
	 * @return self
	 */
	public HEC_Cylinder setSteps(final int steps) {
		this.steps = steps;
		return this;
	}

	/**
	 * Set number of sides.
	 *
	 * @param facets number of sides
	 * @return self
	 */
	public HEC_Cylinder setFacets(final int facets) {
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
	public HEC_Cylinder setCap(final boolean topcap, final boolean bottomcap) {
		this.topcap = topcap;
		this.bottomcap = bottomcap;
		return this;
	}

	public HEC_Cylinder setTaper(final double t) {
		taper = t;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (WB_Epsilon.isZero(Ro)) {
			final HEC_Cone cone = new HEC_Cone(Ri, H, facets, steps);
			cone.setCap(bottomcap).setTaper(taper);
			return cone.createBase();

		}
		if (WB_Epsilon.isZero(Ri)) {
			final HEC_Cone cone = new HEC_Cone(Ro, H, facets, steps);
			cone.setCap(topcap).setTaper(taper);
			cone.setReverse(true);
			return cone.createBase();

		}
		final double[][] vertices = new double[(steps + 1) * facets][3];
		final double invs = 1.0 / steps;
		for (int i = 0; i < steps + 1; i++) {
			final double R = Ri + Math.pow(i * invs, taper) * (Ro - Ri);
			final double Hj = i * H * invs;
			for (int j = 0; j < facets; j++) {
				vertices[j + i * facets][0] = R
						* Math.cos(2 * Math.PI / facets * j);
				vertices[j + i * facets][2] = R
						* Math.sin(2 * Math.PI / facets * j);
				vertices[j + i * facets][1] = Hj;
			}
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
				faces[bc][j] = j;
			}
			if (topcap) {
				faces[tc][facets - 1 - j] = steps * facets + j;
			}
			for (int i = 0; i < steps; i++) {
				faces[j + i * facets] = new int[4];
				faces[j + i * facets][0] = j + i * facets;
				faces[j + i * facets][1] = j + i * facets + facets;
				faces[j + i * facets][2] = ((j + 1) % facets) + facets + i
						* facets;
				faces[j + i * facets][3] = (j + 1) % facets + i * facets;
			}
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();

	}

}
