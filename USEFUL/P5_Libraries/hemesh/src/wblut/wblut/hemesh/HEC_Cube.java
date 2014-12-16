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
 * Axis Aligned Cube.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_Cube extends HEC_Creator {

	/** The W. */
	private double	W;

	/** The L. */
	private int		L;

	/** The M. */
	private int		M;

	/** The N. */
	private int		N;

	/**
	 * Instantiates a new cube.
	 *
	 */
	public HEC_Cube() {
		super();
		W = 0f;
	}

	/**
	 * Create a cube.
	 * @param W width
	 * @param L number of width divisions
	 * @param M number of height divisions
	 * @param N number of depth divisions
	 */
	public HEC_Cube(final double W, final int L, final int M, final int N) {
		this();
		this.W = W;
		this.L = Math.max(1, L);
		this.M = Math.max(1, M);
		this.N = Math.max(1, N);
	}

	/**
	 * Set edge length.
	 *
	 * @param E edge length
	 * @return self
	 */
	public HEC_Cube setEdge(final double E) {
		W = E;
		return this;
	}

	/**
	 * Set box width segments.
	 * 
	 * @param L
	 *            number of width segments (x-axis)
	 * @return self
	 */
	public HEC_Cube setWidthSegments(final int L) {
		this.L = Math.max(1, L);
		return this;
	}

	/**
	 * Set box height segments.
	 * 
	 * @param M
	 *            number of height segments (y-axis)
	 * @return self
	 */
	public HEC_Cube setHeightSegments(final int M) {
		this.M = Math.max(1, M);
		return this;
	}

	/**
	 * Set box depth segments.
	 * 
	 * @param N
	 *            number of depth segments (z-axis)
	 * @return self
	 */
	public HEC_Cube setDepthSegments(final int N) {
		this.N = Math.max(1, N);
		return this;
	}

	/**
	 * Set radius of inscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Cube setRadius(final double R) {
		W = 2 * R;
		return this;
	}

	/**
	 * Set radius of inscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Cube setInnerRadius(final double R) {
		W = 2 * R;
		return this;
	}

	/**
	 * Set radius of circumscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Cube setOuterRadius(final double R) {
		W = 1.1547005 * R;
		return this;
	}

	/**
	 * Set radius of tangential sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Cube setMidRadius(final double R) {
		W = 1.4142136 * R;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		final HEC_Box box = new HEC_Box(W, W, W, N, M, L);
		return box.createBase();

	}

}
