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

import wblut.geom.WB_Vector3d;

/**
 * Sphere.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_Sphere extends HEC_Creator {

	/** Radius. */
	private double	R;

	/** U facets. */
	private int		uFacets;

	/** V facets. */
	private int		vFacets;

	/**
	 * Instantiates a new HEC_Sphere.
	 *
	 */
	public HEC_Sphere() {
		super();
		R = 0;
		uFacets = 12;
		vFacets = 6;
		Z = WB_Vector3d.Y();
	}

	/**
	 * Set fixed radius.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Sphere setRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set number of faces along equator.
	 *
	 * @param facets number of faces
	 * @return self
	 */
	public HEC_Sphere setUFacets(final int facets) {
		uFacets = facets;
		return this;
	}

	/**
	 * Set number of facets along meridian.
	 *
	 * @param facets number of faces
	 * @return self
	 */
	public HEC_Sphere setVFacets(final int facets) {
		vFacets = facets;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {

		final double[][] vertices = new double[2 + uFacets * (vFacets - 1)][3];
		vertices[0][0] = 0;
		vertices[0][1] = R;
		vertices[0][2] = 0;
		vertices[1][0] = 0;
		vertices[1][1] = -R;
		vertices[1][2] = 0;
		int id = 2;
		for (int v = 1; v < vFacets; v++) {
			final double Rs = R * Math.sin(v * Math.PI / vFacets);
			final double Rc = R * Math.cos(v * Math.PI / vFacets);
			for (int u = 0; u < uFacets; u++) {
				vertices[id][0] = Rs * Math.cos(2 * u * Math.PI / uFacets);
				vertices[id][1] = Rc;
				vertices[id][2] = Rs * Math.sin(2 * u * Math.PI / uFacets);
				id++;
			}
		}

		final int[][] faces = new int[uFacets * vFacets][];

		for (int u = 0; u < uFacets; u++) {
			faces[u] = new int[3];
			faces[u][0] = index(u, 0);
			faces[u][1] = index(u + 1, 1);
			faces[u][2] = index(u, 1);
		}
		for (int v = 1; v < vFacets - 1; v++) {
			for (int u = 0; u < uFacets; u++) {
				faces[u + uFacets * v] = new int[4];
				faces[u + uFacets * v][0] = index(u, v);
				faces[u + uFacets * v][1] = index(u + 1, v);
				faces[u + uFacets * v][2] = index(u + 1, v + 1);
				faces[u + uFacets * v][3] = index(u, v + 1);
			}
		}
		for (int u = 0; u < uFacets; u++) {
			faces[u + uFacets * (vFacets - 1)] = new int[3];
			faces[u + uFacets * (vFacets - 1)][0] = index(u, vFacets - 1);
			faces[u + uFacets * (vFacets - 1)][1] = index(u + 1, vFacets - 1);
			faces[u + uFacets * (vFacets - 1)][2] = index(u + 1, vFacets);
		}

		/*
		 * for(int j=0;j<facets;j++){ int jp=(j==facets-1)?0:j+1;
		 * faces[facets-1+facets*j]=new int[3];
		 * faces[facets-1+facets*j][0]=facets-1+(facets+1)*j;
		 * faces[facets-1+facets*j][1]=(facets+1)*facets-1;
		 * faces[facets-1+facets*j][2]=facets-1+(facets+1)*jp; }
		 */

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();

	}

	/**
	 * Index.
	 *
	 * @param u the u
	 * @param v the v
	 * @return the int
	 */
	private int index(final int u, final int v) {
		if (v == 0) {
			return 0;
		}
		if (v == vFacets) {
			return 1;
		}
		if (u == uFacets) {
			return index(0, v);
		}
		return 2 + u + uFacets * (v - 1);

	}

}
