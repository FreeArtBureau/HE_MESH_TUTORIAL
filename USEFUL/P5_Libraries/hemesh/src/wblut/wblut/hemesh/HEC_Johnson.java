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
 * Johnson polyhedra.
 * 
 * @author Implemented by Frederik Vanhoutte (W:Blut), painstakingly collected by David Marec.
 * Many thanks, without David this wouldn't be here.
 * 
 */

public class HEC_Johnson extends HEC_Creator {

	/** Edge. */
	private double	R;

	/** Type. */
	private int		type;

	private String	name;

	/**
	 * Instantiates a new dodecahedron.
	 *
	 */
	public HEC_Johnson() {
		super();
		R = 1;
		type = 1;
		name = "default";

	}

	/**
	 * Instantiates a new Johnson polyhedron.
	 *
	 * @param E edge length
	 */
	public HEC_Johnson(final int type, final double E) {
		super();
		R = E;
		this.type = type;
		if ((type < 1) || (type > 92)) {
			throw new IllegalArgumentException(
					"Type of Johnson polyhedron should be between 1 and 92.");
		}
		if (type < 24) {
			name = HE_JohnsonPolyhedraData01.names[type - 1];
		} else if (type < 47) {
			name = HE_JohnsonPolyhedraData02.names[type - 24];
		} else if (type < 71) {
			name = HE_JohnsonPolyhedraData03.names[type - 47];
		} else {
			name = HE_JohnsonPolyhedraData04.names[type - 71];
		}
		center = new WB_Point3d();
	}

	/**
	 * Set edge length.
	 *
	 * @param E edge length
	 * @return self
	 */
	public HEC_Johnson setEdge(final double E) {
		R = E;
		return this;
	}

	/**
	 * Set type.
	 *
	 * @param type
	 * @return self
	 */
	public HEC_Johnson setType(final int type) {
		if ((type < 1) || (type > 92)) {
			throw new IllegalArgumentException(
					"Type of Johnson polyhedron should be between 1 and 92.");
		}
		this.type = type;
		if (type < 24) {
			name = HE_JohnsonPolyhedraData01.names[type - 1];
		} else if (type < 47) {
			name = HE_JohnsonPolyhedraData02.names[type - 24];
		} else if (type < 71) {
			name = HE_JohnsonPolyhedraData03.names[type - 47];
		} else {
			name = HE_JohnsonPolyhedraData04.names[type - 71];
		}
		return this;
	}

	public String getName() {
		return (name);

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {

		final double[][] vertices;
		final int[][] faces;
		if (type < 24) {
			vertices = HE_JohnsonPolyhedraData01.vertices[type - 1];
			faces = HE_JohnsonPolyhedraData01.faces[type - 1];

		} else if (type < 47) {
			vertices = HE_JohnsonPolyhedraData02.vertices[type - 24];
			faces = HE_JohnsonPolyhedraData02.faces[type - 24];
		} else if (type < 71) {
			vertices = HE_JohnsonPolyhedraData03.vertices[type - 47];
			faces = HE_JohnsonPolyhedraData03.faces[type - 47];
		} else {
			vertices = HE_JohnsonPolyhedraData04.vertices[type - 71];
			faces = HE_JohnsonPolyhedraData04.faces[type - 71];
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		final HE_Mesh result = fl.create();
		result.scale(R);
		return result;

	}
}
