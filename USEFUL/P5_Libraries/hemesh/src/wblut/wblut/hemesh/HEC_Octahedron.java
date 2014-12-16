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


/**
 * Octahedron.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_Octahedron extends HEC_Creator {

	/** Outer radius. */
	private double	R;

	/**
	 * Instantiates a new HEC_Octahedron.
	 *
	 */
	public HEC_Octahedron() {
		super();
		R = 0f;
	}

	/**
	 * Set edge length.
	 *
	 * @param E edge length
	 * @return self
	 */
	public HEC_Octahedron setEdge(final double E) {
		R = 0.70711 * E;
		return this;
	}

	/**
	 * Set radius inscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Octahedron setInnerRadius(final double R) {
		this.R = R * 1.732051;
		return this;
	}

	/**
	 * Set radius circumscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Octahedron setOuterRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set radius tangential sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Octahedron setMidRadius(final double R) {
		this.R = R * 1.41422;
		return this;
	}

	/*
	 * Code adapted from http://www.cs.umbc.edu/~squire/ (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {

		final double[][] vertices = new double[6][3]; /*
													 * 6 vertices with x, y, z
													 * coordinate
													 */
		final double Pi = 3.141592653589793238462643383279502884197;

		final double phiaa = 0.0; /* the phi needed for generation */

		final double phia = Pi * phiaa / 180.0; /* 1 set of four points */
		final double the90 = Pi * 90.0 / 180;
		vertices[0][0] = 0;
		vertices[0][1] = 0;
		vertices[0][2] = R;

		vertices[5][0] = 0;
		vertices[5][1] = 0;
		vertices[5][2] = -R;

		double the = 0.0;
		for (int i = 1; i < 5; i++) {
			vertices[i][0] = R * Math.cos(the) * Math.cos(phia);
			vertices[i][1] = R * Math.sin(the) * Math.cos(phia);
			vertices[i][2] = R * Math.sin(phia);
			the = the + the90;
		}

		final int[][] faces = { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 4 },
				{ 0, 4, 1 }, { 5, 2, 1 }, { 5, 3, 2 }, { 5, 4, 3 }, { 5, 1, 4 }

		};

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();

	}
}
