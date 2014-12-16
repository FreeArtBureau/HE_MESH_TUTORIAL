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
 * Tube.
 * 
 * @author Jan Vantomme
 * 
 */

public class HEC_Tube extends HEC_Creator {
	/** Outer Radius */
	private double	outerRadius;

	/** Inner Radius */
	private double	innerRadius;

	/** Height */
	private double	H;

	/** Facets */
	private int		facets;

	/** Height steps */
	private int		steps;

	/** Shift distance in X direction */
	private double	shiftX;

	/** Shift distance in Y direction */
	private double	shiftY;

	/**
	 * Instantiates a new HEC_Tube.
	 *
	 */
	public HEC_Tube() {
		super();

		innerRadius = 0;
		outerRadius = 0;

		H = 0;

		facets = 6;
		steps = 1;

		shiftX = 0;
		shiftY = 0;

		Z = WB_Vector3d.Y();

	}

	/**
	 * Set the outer radius.
	 *
	 * @param R Outer radius
	 * @return self
	 */
	public HEC_Tube setOuterRadius(final double R) {
		outerRadius = R;
		return this;
	}

	/**
	 * Set the inner radius.
	 *
	 * @param R Inner radius
	 * @return self
	 */
	public HEC_Tube setInnerRadius(final double R) {
		innerRadius = R;
		return this;
	}

	/**
	 * Set the height
	 *
	 * @param H Height
	 * @return self
	 */
	public HEC_Tube setHeight(final double H) {
		this.H = H;
		return this;
	}

	/**
	 * Set vertical divisions
	 *
	 * @param steps Vertical divisions
	 * @return self
	 */
	public HEC_Tube setSteps(final int steps) {
		this.steps = steps;
		return this;
	}

	/**
	 * Set number of sides
	 *
	 * @param facets number of sides
	 * @return self
	 */
	public HEC_Tube setFacets(final int facets) {
		this.facets = facets;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in X direction
	 *
	 * @param shiftX shift distance in X direction
	 * @return self
	 */
	public HEC_Tube shiftX(final double shiftX) {
		this.shiftX = shiftX;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in Y direction
	 *
	 * @param shiftY shift distance in Y direction
	 * @return self
	 */
	public HEC_Tube shiftY(final double shiftY) {
		this.shiftY = shiftY;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in X and Y direction
	 *
	 * @param shift shift distance in X and Y direction
	 * @return self
	 */
	public HEC_Tube shift(final double shift) {
		shiftX = shift;
		shiftY = shift;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in X and Y direction
	 *
	 * @param shiftX shift distance in X direction
	 * @param shiftY shift distance in Y direction
	 * @return self
	 */
	public HEC_Tube shift(final double shiftX, final double shiftY) {
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		final double[][] vertices = new double[((steps + 1) * facets) * 2][3];

		// outer vertices
		for (int i = 0; i < steps + 1; i++) {
			final double Hj = i * H / steps;
			for (int j = 0; j < facets; j++) {
				vertices[j + i * facets][0] = outerRadius
						* Math.cos(2 * Math.PI / facets * j);
				vertices[j + i * facets][1] = outerRadius
						* Math.sin(2 * Math.PI / facets * j);
				vertices[j + i * facets][2] = Hj;
			}
		}

		// inner vertices
		for (int i = steps; i >= 0; i--) {
			final double Hj = i * H / steps;
			for (int j = 0; j < facets; j++) {
				vertices[j + i * facets + ((steps + 1) * facets)][0] = shiftX
						+ (innerRadius * Math.cos(2 * Math.PI / facets * j));
				vertices[j + i * facets + ((steps + 1) * facets)][1] = shiftY
						+ innerRadius * Math.sin(2 * Math.PI / facets * j);
				vertices[j + i * facets + ((steps + 1) * facets)][2] = Hj;
			}
		}

		final int[][] faces = new int[(steps * facets * 2) + (facets * 2)][];

		for (int j = 0; j < facets; j++) {
			// outer faces
			for (int i = 0; i < steps; i++) {
				faces[j + i * facets] = new int[4];
				faces[j + i * facets][0] = (j + 1) % facets + i * facets;
				faces[j + i * facets][1] = ((j + 1) % facets) + facets + i
						* facets;
				faces[j + i * facets][2] = j + i * facets + facets;
				faces[j + i * facets][3] = j + i * facets;
			}

			// top faces
			if (j != facets - 1) {
				faces[j + (steps * facets)] = new int[4];
				faces[j + (steps * facets)][0] = j + (steps * facets) + facets;
				faces[j + (steps * facets)][1] = j + 1 + (steps * facets)
						+ facets;
				faces[j + (steps * facets)][2] = j + 1;
				faces[j + (steps * facets)][3] = j;
			} else {
				faces[j + (steps * facets)] = new int[4];
				faces[j + (steps * facets)][0] = j + (steps * facets) + facets;
				faces[j + (steps * facets)][1] = (steps * facets) + facets;
				faces[j + (steps * facets)][2] = 0;
				faces[j + (steps * facets)][3] = j;
			}

			// inner faces
			for (int i = 0; i < steps; i++) {
				faces[(j + i * facets) + (steps * facets) + facets] = new int[4];
				faces[(j + i * facets) + (steps * facets) + facets][0] = (j + i
						* facets)
						+ (steps * facets) + facets;
				faces[(j + i * facets) + (steps * facets) + facets][1] = (j + i
						* facets + facets)
						+ (steps * facets) + facets;
				faces[(j + i * facets) + (steps * facets) + facets][2] = (((j + 1) % facets)
						+ facets + i * facets)
						+ (steps * facets) + facets;
				faces[(j + i * facets) + (steps * facets) + facets][3] = ((j + 1)
						% facets + i * facets)
						+ (steps * facets) + facets;

			}

			// bottom faces
			if (j != facets - 1) {
				faces[j + 2 * (steps * facets) + facets] = new int[4];
				faces[j + 2 * (steps * facets) + facets][0] = j
						+ (steps * facets);
				faces[j + 2 * (steps * facets) + facets][1] = j + 1
						+ (steps * facets);
				faces[j + 2 * (steps * facets) + facets][2] = j + 1
						+ (steps * facets) + facets + (steps * facets);
				faces[j + 2 * (steps * facets) + facets][3] = j
						+ (steps * facets) + facets + (steps * facets);
			} else {
				faces[j + 2 * (steps * facets) + facets] = new int[4];
				faces[j + 2 * (steps * facets) + facets][0] = j
						+ (steps * facets);
				faces[j + 2 * (steps * facets) + facets][1] = (facets * steps);
				faces[j + 2 * (steps * facets) + facets][2] = vertices.length
						- 1 - j;
				faces[j + 2 * (steps * facets) + facets][3] = vertices.length - 1;
			}
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces).setDuplicate(false);

		return fl.createBase();

	}
}