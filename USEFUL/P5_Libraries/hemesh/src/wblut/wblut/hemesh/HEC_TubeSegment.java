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
 * TubeSegment.
 * 
 * @author Jan Vantomme
 * 
 */

public class HEC_TubeSegment extends HEC_Creator {
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

	/** Angle */
	private double	angle;

	/** Facet Angle */
	private double	facetAngle;

	/** Shift distance in X direction */
	public double	shiftX;

	/** Shift distance in Y direction */
	public double	shiftY;

	/**
	 * Instantiates a new HEC_TubeSegment.
	 *
	 */
	public HEC_TubeSegment() {
		super();

		innerRadius = 0;
		outerRadius = 0;

		H = 0;

		facets = 6;
		steps = 1;

		angle = 90;
		facetAngle = (angle / facets) / 180.0 * Math.PI;

		shiftX = 0;
		shiftY = 0;

	}

	/**
	 * Set the outer radius.
	 *
	 * @param R Outer radius
	 * @return self
	 */
	public HEC_TubeSegment setOuterRadius(final double R) {
		outerRadius = R;
		return this;
	}

	/**
	 * Set the inner radius.
	 *
	 * @param R Inner radius
	 * @return self
	 */
	public HEC_TubeSegment setInnerRadius(final double R) {
		innerRadius = R;
		return this;
	}

	/**
	 * Set the height
	 *
	 * @param H Height
	 * @return self
	 */
	public HEC_TubeSegment setHeight(final double H) {
		this.H = H;
		return this;
	}

	/**
	 * Set vertical divisions
	 *
	 * @param steps Vertical divisions
	 * @return self
	 */
	public HEC_TubeSegment setSteps(final int steps) {
		this.steps = steps;
		return this;
	}

	/**
	 * Set number of sides
	 *
	 * @param facets number of sides
	 * @return self
	 */
	public HEC_TubeSegment setFacets(final int facets) {
		this.facets = facets;
		return this;
	}

	/**
	 * Set angle in degrees
	 *
	 * @param angle Angle
	 * @return self
	 */
	public HEC_TubeSegment setAngle(final double angle) {
		this.angle = angle;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in X direction
	 *
	 * @param shiftX shift distance in X direction
	 * @return self
	 */
	public HEC_TubeSegment shiftX(final double shiftX) {
		this.shiftX = shiftX;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in Y direction
	 *
	 * @param shiftY shift distance in Y direction
	 * @return self
	 */
	public HEC_TubeSegment shiftY(final double shiftY) {
		this.shiftY = shiftY;
		return this;
	}

	/**
	 * Set the shift distance for the center of the inner radius in X and Y direction
	 *
	 * @param shift shift distance in X and Y direction
	 * @return self
	 */
	public HEC_TubeSegment shift(final double shift) {
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
	public HEC_TubeSegment shift(final double shiftX, final double shiftY) {
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
		facetAngle = (angle / facets) / 180.0 * Math.PI;

		final double[][] vertices = new double[(steps + 1) * (facets + 1) * 2][3];

		// outer vertices
		int index = 0;
		for (int i = 0; i < steps + 1; i++) {
			final double Hj = i * H / steps;
			for (int j = 0; j < facets + 1; j++) {

				vertices[index][0] = outerRadius * Math.cos(j * facetAngle);
				vertices[index][1] = outerRadius * Math.sin(j * facetAngle);
				vertices[index][2] = Hj;

				index++;
			}

		}

		// inner vertices
		for (int i = 0; i < steps + 1; i++) {
			final double Hj = i * H / steps;
			for (int j = 0; j < facets + 1; j++) {

				vertices[index][0] = shiftX + innerRadius
						* Math.cos(j * facetAngle);
				vertices[index][1] = shiftY + innerRadius
						* Math.sin(j * facetAngle);
				vertices[index][2] = Hj;

				index++;
			}
		}

		final int[][] faces = new int[(steps * facets * 2) + (facets * 2)
				+ (steps * 2)][3];

		// outer faces
		index = 0;
		for (int j = 0; j < steps; j++) {
			for (int i = 0; i < facets; i++) {

				faces[index] = new int[4];
				faces[index][0] = i + (facets + 1) * j;
				faces[index][1] = i + 1 + (facets + 1) * j;
				faces[index][2] = i + 1 + (facets + 1) * (j + 1);
				faces[index][3] = i + (facets + 1) * (j + 1);

				index++;

			}
		}

		// inner faces
		for (int j = 0; j < steps; j++) {
			for (int i = 0; i < facets; i++) {

				faces[index] = new int[4];
				faces[index][3] = (i + (facets + 1) * j)
						+ ((steps + 1) * (facets + 1));
				faces[index][2] = (i + 1 + (facets + 1) * j)
						+ ((steps + 1) * (facets + 1));
				faces[index][1] = (i + 1 + (facets + 1) * (j + 1))
						+ ((steps + 1) * (facets + 1));
				faces[index][0] = (i + (facets + 1) * (j + 1))
						+ ((steps + 1) * (facets + 1));

				index++;
			}
		}

		// top faces
		for (int i = 0; i < facets; i++) {

			faces[index] = new int[4];
			faces[index][3] = i;
			faces[index][2] = i + 1;
			faces[index][1] = i + 1 + ((steps + 1) * (facets + 1));
			faces[index][0] = i + ((steps + 1) * (facets + 1));

			index++;
		}

		// bottom faces
		final int offSet = ((steps + 1) * (facets + 1)) - facets - 1;

		for (int i = 0; i < facets; i++) {
			faces[index] = new int[4];
			faces[index][0] = i + offSet;
			faces[index][1] = i + 1 + offSet;
			faces[index][2] = i + 1 + ((steps + 1) * (facets + 1)) + offSet;
			faces[index][3] = i + ((steps + 1) * (facets + 1)) + offSet;

			index++;
		}

		// close side 1
		for (int i = 0; i < steps; i++) {

			faces[index] = new int[4];
			faces[index][0] = (i + 1) * (facets + 1);
			faces[index][1] = ((steps + 1) * (facets + 1)) + (i * (facets + 1))
					+ (facets + 1);
			faces[index][2] = ((steps + 1) * (facets + 1)) + (i * (facets + 1));
			faces[index][3] = i * (facets + 1);

			index++;
		}

		// close side 2
		for (int i = 0; i < steps; i++) {

			faces[index] = new int[4];
			faces[index][3] = ((steps + 1) * (facets + 1)) + (i * (facets + 1))
					+ (facets + 1) - 1;
			faces[index][2] = (i + 1) * (facets + 1) - 1;
			faces[index][1] = (i + 1) * (facets + 1) - 1 + (facets + 1);
			faces[index][0] = ((steps + 1) * (facets + 1)) + (i * (facets + 1))
					+ (facets + 1) + facets;

			index++;
		}

		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces).setDuplicate(false);

		return fl.createBase();
	}
}