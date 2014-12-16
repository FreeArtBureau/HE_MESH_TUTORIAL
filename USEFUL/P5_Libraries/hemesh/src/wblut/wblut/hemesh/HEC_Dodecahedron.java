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

import wblut.geom.WB_Point3d;

/**
 * Dodecahedron.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_Dodecahedron extends HEC_Creator {

	/** Outer Radius. */
	private double	R;

	/**
	 * Instantiates a new dodecahedron.
	 *
	 */
	public HEC_Dodecahedron() {
		super();
		R = 0f;

	}

	/**
	 * Instantiates a new dodecahedron.
	 *
	 * @param R outer radius
	 */
	public HEC_Dodecahedron(final double R) {
		super();
		this.R = R;
		center = new WB_Point3d();

	}

	/**
	 * Set edge length.
	 *
	 * @param E edge length
	 * @return self
	 */
	public HEC_Dodecahedron setEdge(final double E) {
		R = 1.40126 * E;
		return this;
	}

	/**
	 * Set radius inscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Dodecahedron setInnerRadius(final double R) {
		this.R = R * 1.258406;
		return this;
	}

	/**
	 * Set radius circumscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Dodecahedron setOuterRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set radius circumscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Dodecahedron setRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set radius tangential sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Dodecahedron setMidRadius(final double R) {
		this.R = R * 1.070465;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {

		final double[][] vertices = new double[20][3]; /*
														 * 20 vertices with x,
														 * y, z coordinate
														 */
		final double Pi = 3.141592653589793238462643383279502884197;

		final double phiaa = 52.62263590; /*
										 * the two phi angles needed for
										 * generation
										 */
		final double phibb = 10.81231754;

		final double phia = Pi * phiaa / 180.0; /* 4 sets of five points each */
		final double phib = Pi * phibb / 180.0;
		final double phic = Pi * (-phibb) / 180.0;
		final double phid = Pi * (-phiaa) / 180.0;
		final double the72 = Pi * 72.0 / 180;
		final double theb = the72 / 2.0; /* pairs of layers offset 36 degrees */
		double the = 0.0;
		for (int i = 0; i < 5; i++) {
			vertices[i][0] = R * Math.cos(the) * Math.cos(phia);
			vertices[i][1] = R * Math.sin(the) * Math.cos(phia);
			vertices[i][2] = R * Math.sin(phia);
			the = the + the72;
		}
		the = 0.0;
		for (int i = 5; i < 10; i++) {
			vertices[i][0] = R * Math.cos(the) * Math.cos(phib);
			vertices[i][1] = R * Math.sin(the) * Math.cos(phib);
			vertices[i][2] = R * Math.sin(phib);
			the = the + the72;
		}
		the = theb;
		for (int i = 10; i < 15; i++) {
			vertices[i][0] = R * Math.cos(the) * Math.cos(phic);
			vertices[i][1] = R * Math.sin(the) * Math.cos(phic);
			vertices[i][2] = R * Math.sin(phic);
			the = the + the72;
		}
		the = theb;
		for (int i = 15; i < 20; i++) {
			vertices[i][0] = R * Math.cos(the) * Math.cos(phid);
			vertices[i][1] = R * Math.sin(the) * Math.cos(phid);
			vertices[i][2] = R * Math.sin(phid);
			the = the + the72;
		}

		final int[][] faces = { { 0, 1, 2, 3, 4 }, { 5, 10, 6, 1, 0 },
				{ 6, 11, 7, 2, 1 }, { 7, 12, 8, 3, 2 }, { 8, 13, 9, 4, 3 },
				{ 9, 14, 5, 0, 4 }, { 15, 16, 11, 6, 10 },
				{ 16, 17, 12, 7, 11 }, { 17, 18, 13, 8, 12 },
				{ 18, 19, 14, 9, 13 }, { 19, 15, 10, 5, 14 },
				{ 19, 18, 17, 16, 15 }

		};

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.create();

	}
}
