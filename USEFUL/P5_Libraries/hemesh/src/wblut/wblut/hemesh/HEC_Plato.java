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
 * Platonic polyhedra.
 * 
 * @author Implemented by Frederik Vanhoutte (W:Blut), painstakingly collected by David Marec.
 * Many thanks, without David this wouldn't be here.
 * 
 */

public class HEC_Plato extends HEC_Creator {

	final static String[]		names		= { "cube", "dodecahedron",
			"icosahedron", "octahedron", "tetrahedron" };
	final static double[][][]	vertices	= {
			{ { -0.5, -0.5, -0.5 }, { -0.5, -0.5, 0.5 }, { -0.5, 0.5, -0.5 },
			{ -0.5, 0.5, 0.5 }, { 0.5, -0.5, -0.5 }, { 0.5, -0.5, 0.5 },
			{ 0.5, 0.5, -0.5 }, { 0.5, 0.5, 0.5 } },
			{ { 0., 0., 1.40126 }, { 0., 0., -1.40126 },
			{ 0.178411, -1.30902, 0.467086 }, { 0.178411, 1.30902, 0.467086 },
			{ 0.467086, -0.809017, -1.04444 },
			{ 0.467086, 0.809017, -1.04444 }, { 1.04444, -0.809017, 0.467086 },
			{ 1.04444, 0.809017, 0.467086 }, { -1.22285, -0.5, 0.467086 },
			{ -1.22285, 0.5, 0.467086 }, { 1.22285, -0.5, -0.467086 },
			{ 1.22285, 0.5, -0.467086 }, { -0.934172, 0., -1.04444 },
			{ -0.467086, -0.809017, 1.04444 },
			{ -0.467086, 0.809017, 1.04444 }, { 0.934172, 0., 1.04444 },
			{ -1.04444, -0.809017, -0.467086 },
			{ -1.04444, 0.809017, -0.467086 },
			{ -0.178411, -1.30902, -0.467086 },
			{ -0.178411, 1.30902, -0.467086 } },
			{ { 0., 0., -0.951057 }, { 0., 0., 0.951057 },
			{ -0.850651, 0., -0.425325 }, { 0.850651, 0., 0.425325 },
			{ 0.688191, -0.5, -0.425325 }, { 0.688191, 0.5, -0.425325 },
			{ -0.688191, -0.5, 0.425325 }, { -0.688191, 0.5, 0.425325 },
			{ -0.262866, -0.809017, -0.425325 },
			{ -0.262866, 0.809017, -0.425325 },
			{ 0.262866, -0.809017, 0.425325 }, { 0.262866, 0.809017, 0.425325 } },
			{ { -0.5, -0.5, 0. }, { -0.5, 0.5, 0. }, { 0., 0., -0.707107 },
			{ 0., 0., 0.707107 }, { 0.5, -0.5, 0. }, { 0.5, 0.5, 0. } },
			{ { 0., 0., 0.612372 }, { -0.288675, -0.5, -0.204124 },
			{ -0.288675, 0.5, -0.204124 }, { 0.57735, 0., -0.204124 } } };
	final static int[][][]		faces		= {
			{ { 7, 3, 1, 5 }, { 7, 5, 4, 6 }, { 7, 6, 2, 3 }, { 3, 2, 0, 1 },
			{ 0, 2, 6, 4 }, { 1, 0, 4, 5 } },
			{ { 14, 9, 8, 13, 0 }, { 1, 5, 11, 10, 4 }, { 4, 10, 6, 2, 18 },
			{ 10, 11, 7, 15, 6 }, { 11, 5, 19, 3, 7 }, { 5, 1, 12, 17, 19 },
			{ 1, 4, 18, 16, 12 }, { 3, 19, 17, 9, 14 }, { 17, 12, 16, 8, 9 },
			{ 16, 18, 2, 13, 8 }, { 2, 6, 15, 0, 13 }, { 15, 7, 3, 14, 0 } },
			{ { 1, 11, 7 }, { 1, 7, 6 }, { 1, 6, 10 }, { 1, 10, 3 },
			{ 1, 3, 11 }, { 4, 8, 0 }, { 5, 4, 0 }, { 9, 5, 0 }, { 2, 9, 0 },
			{ 8, 2, 0 }, { 11, 9, 7 }, { 7, 2, 6 }, { 6, 8, 10 }, { 10, 4, 3 },
			{ 3, 5, 11 }, { 4, 10, 8 }, { 5, 3, 4 }, { 9, 11, 5 }, { 2, 7, 9 },
			{ 8, 6, 2 } },
			{ { 3, 4, 5 }, { 3, 5, 1 }, { 3, 1, 0 }, { 3, 0, 4 }, { 4, 0, 2 },
			{ 4, 2, 5 }, { 2, 0, 1 }, { 5, 2, 1 } },
			{ { 1, 2, 3 }, { 2, 1, 0 }, { 3, 0, 1 }, { 0, 3, 2 } } };
	/** Edge. */
	private double				R;

	/** Type. */
	private int					type;

	private String				name;

	/**
	 * Instantiates a new Archimedean polyhedron.
	 *
	 */
	public HEC_Plato() {
		super();
		R = 1;
		type = 1;
		name = "default";

	}

	/**
	 * Instantiates a new Archimedean polyhedron.
	 *
	 * @param E edge length
	 */
	public HEC_Plato(final int type, final double E) {
		super();
		R = E;
		this.type = type;
		if ((type < 1) || (type > 5)) {
			throw new IllegalArgumentException(
					"Type of Platonic polyhedron should be between 1 and 5.");
		}

		name = names[type - 1];

		center = new WB_Point3d();
	}

	/**
	 * Set edge length.
	 *
	 * @param E edge length
	 * @return self
	 */
	public HEC_Plato setEdge(final double E) {
		R = E;
		return this;
	}

	/**
	 * Set type.
	 *
	 * @param type
	 * @return self
	 */
	public HEC_Plato setType(final int type) {
		if ((type < 1) || (type > 5)) {
			throw new IllegalArgumentException(
					"Type of Platonic polyhedron should be between 1 and 5.");
		}
		this.type = type;
		name = names[type - 1];
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

		final double[][] verts = vertices[type - 1];
		final int[][] facs = faces[type - 1];

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(verts).setFaces(facs);
		final HE_Mesh result = fl.create();
		result.scale(R);
		return result;

	}
}
