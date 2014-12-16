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

import wblut.math.WB_Function2D;

/**
 * Creates a flat rectangular grid.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_Grid extends HEC_Creator {

	/** U resolution. */
	private int			U;

	/** V resolution. */
	private int			V;

	/** U size. */
	private double		uSize;

	/** V size. */
	private double		vSize;

	/** W values. */
	private double[][]	values;

	private double		WScale;

	/**
	 * Instantiates a new HEC_Grid.
	 *
	 */
	public HEC_Grid() {
		super();
		uSize = 0;
		vSize = 0;
		U = 1;
		V = 1;
		WScale = 1;

	}

	/**
	 * Instantiates a new HEC_Grid.
	 *
	 * @param U number of U divisions
	 * @param V number of V divisions
	 * @param uSize size in U direction
	 * @param vSize size in V direction
	 */
	public HEC_Grid(final int U, final int V, final double uSize,
			final double vSize) {
		this();
		this.uSize = uSize;
		this.vSize = vSize;
		this.U = U;
		this.V = V;

	}

	/**
	 * set divisions in U direction.
	 *
	 * @param U number of U divisions
	 * @return self
	 */
	public HEC_Grid setU(final int U) {
		this.U = Math.max(1, U);
		return this;
	}

	/**
	 * set divisions in V direction.
	 *
	 * @param V number of V divisions
	 * @return self
	 */
	public HEC_Grid setV(final int V) {
		this.V = Math.max(1, V);
		return this;
	}

	/**
	 * Set size in U direction.
	 *
	 * @param uSize size in U direction
	 * @return self
	 */
	public HEC_Grid setUSize(final double uSize) {
		this.uSize = uSize;
		return this;
	}

	/**
	 * Set size in V direction.
	 *
	 * @param vSize size in V direction
	 * @return self
	 */
	public HEC_Grid setVSize(final double vSize) {
		this.vSize = vSize;
		return this;
	}

	/**
	 * Optional: set W values from 2D array of double of size [resU+1][resV+1].
	 *
	 * @param values 2D array of double
	 * @return self
	 */
	public HEC_Grid setWValues(final double[][] values) {
		this.values = values;
		return this;
	}

	/**
	 * Optional: set W values from 2D scalar function.
	 *
	 * @param height 2D scalar function
	 * @param ui starting u value for function
	 * @param vi starting v value for function
	 * @param du step size u for function
	 * @param dv step size v for function
	 * 
	 * @return self

	 */
	public HEC_Grid setWValues(final WB_Function2D<Double> height,
			final double ui, final double vi, final double du, final double dv) {
		values = new double[U + 1][V + 1];
		for (int i = 0; i < U + 1; i++) {
			for (int j = 0; j < V + 1; j++) {
				values[i][j] = height.f(ui + i * du, vi + j * dv);
			}
		}
		return this;
	}

	/**
	 * Optional: set W values from 2D array of float of size [resU+1][resV+1].
	 *
	 * @param values 2D array of float
	 * @return self
	 */
	public HEC_Grid setWValues(final float[][] values) {

		this.values = new double[U + 1][V + 1];
		for (int i = 0; i < U + 1; i++) {
			for (int j = 0; j < V + 1; j++) {
				this.values[i][j] = values[i][j];
			}
		}

		return this;
	}

	public HEC_Grid setWValues(final float[] values) {
		int id = 0;
		this.values = new double[U + 1][V + 1];
		for (int j = 0; j < V + 1; j++) {
			for (int i = 0; i < U + 1; i++) {
				this.values[i][j] = values[id];
				id++;
			}
		}

		return this;
	}

	public HEC_Grid setWValues(final double[] values) {
		int id = 0;
		this.values = new double[U + 1][V + 1];
		for (int j = 0; j < V + 1; j++) {
			for (int i = 0; i < U + 1; i++) {
				this.values[i][j] = values[id];
				id++;
			}
		}

		return this;
	}

	public HEC_Grid setWScale(final double value) {
		WScale = value;

		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEB_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if ((uSize == 0) || (vSize == 0)) {
			return new HE_Mesh();
		}
		final double dU = uSize / U;
		final double dV = vSize / V;
		final double[][] points = new double[(U + 1) * (V + 1)][3];
		final int[][] faces = new int[U * V][4];
		int index = 0;
		if (values == null) {
			for (int j = 0; j < V + 1; j++) {
				for (int i = 0; i < U + 1; i++) {
					points[index][0] = i * dU;
					points[index][1] = j * dV;
					points[index][2] = 0;
					index++;
				}
			}
		} else {
			for (int j = 0; j < V + 1; j++) {
				for (int i = 0; i < U + 1; i++) {
					points[index][0] = i * dU;
					points[index][1] = j * dV;
					points[index][2] = WScale * values[i][j];
					index++;
				}
			}
		}
		index = 0;
		for (int j = 0; j < V; j++) {
			for (int i = 0; i < U; i++) {
				faces[index][0] = i + (U + 1) * j;
				faces[index][1] = i + 1 + (U + 1) * j;
				faces[index][2] = i + 1 + (U + 1) * (j + 1);
				faces[index][3] = i + (U + 1) * (j + 1);
				index++;
			}
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(points).setFaces(faces);
		return fl.createBase();
	}
}
