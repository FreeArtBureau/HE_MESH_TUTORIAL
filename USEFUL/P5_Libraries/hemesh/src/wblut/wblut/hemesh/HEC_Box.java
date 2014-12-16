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

import wblut.geom.WB_AABB;

/**
 * Axis Aligned Box.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_Box extends HEC_Creator {

	/**width */
	private double	W;

	/** height */
	private double	H;

	/** depth */
	private double	D;

	/** width segments */
	private int		L;

	/** height segments */
	private int		M;

	/** depth segments */
	private int		N;

	/**
	 * Create a placeholder box.
	 *
	 */
	public HEC_Box() {
		super();
		W = H = D = 0;
		L = M = N = 1;

	}

	/**
	 *  Create a box at (0,0,0).
	 * @param W width (X)
	 * @param H height (Y)
	 * @param D depth (Z)
	 * @param L number of width divisions
	 * @param M number of height divisions
	 * @param N number of depth divisions
	 */
	public HEC_Box(final double W, final double H, final double D, final int L,
			final int M, final int N) {
		this();
		this.W = W;
		this.H = H;
		this.D = D;
		this.L = Math.max(1, L);
		this.M = Math.max(1, M);
		this.N = Math.max(1, N);
	}

	/**
	 * Set box from AABB.
	 * 
	 * @param AABB
	 *           WB_AABB
	 * @return self
	 */
	public HEC_Box setFromAABB(final WB_AABB AABB, final double padding) {
		W = AABB.getWidth() + 2 * padding;
		H = AABB.getHeight() + 2 * padding;
		D = AABB.getDepth() + 2 * padding;
		setCenter(AABB.getCenter());
		return this;
	}

	/**
	 * Set box width.
	 * 
	 * @param W
	 *            width of box (x-axis)
	 * @return self
	 */
	public HEC_Box setWidth(final double W) {
		this.W = W;
		return this;
	}

	/**
	 * Set box height.
	 * 
	 * @param H
	 *            height of box (y-axis)
	 * @return self
	 */
	public HEC_Box setHeight(final double H) {
		this.H = H;
		return this;
	}

	/**
	 * Set box depth.
	 * 
	 * @param D
	 *            depth of box (z-axis)
	 * @return self
	 */
	public HEC_Box setDepth(final double D) {
		this.D = D;
		return this;
	}

	/**
	 * Set box width segments.
	 * 
	 * @param L
	 *            number of width segments (x-axis)
	 * @return self
	 */
	public HEC_Box setWidthSegments(final int L) {
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
	public HEC_Box setHeightSegments(final int M) {
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
	public HEC_Box setDepthSegments(final int N) {
		this.N = Math.max(1, N);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		final double oW = -0.5 * W;
		final double oH = -0.5 * H;
		final double oD = -0.5 * D;
		final double dW = W * 1.0 / L;
		final double dH = H * 1.0 / M;
		final double dD = D * 1.0 / N;
		final double[][] vertices = new double[(N + 1) * (L + 1) * (M + 1)][3];
		final int[][] faces = new int[2 * N * L + 2 * M * N + 2 * M * L][4];

		int idv = 0;
		for (int k = 0; k < M + 1; k++) {
			for (int j = 0; j < N + 1; j++) {
				for (int i = 0; i < L + 1; i++) {
					vertices[idv][0] = oW + i * dW;
					vertices[idv][1] = oH + k * dH;
					vertices[idv][2] = oD + j * dD;
					idv++;
				}
			}
		}

		int idf = 0;
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < L; i++) {
				faces[idf][0] = index(i, j, 0);
				faces[idf][1] = index(i + 1, j, 0);
				faces[idf][2] = index(i + 1, j + 1, 0);
				faces[idf][3] = index(i, j + 1, 0);
				idf++;
			}
		}

		for (int j = 0; j < N; j++) {
			for (int i = 0; i < L; i++) {
				faces[idf][3] = index(i, j, M);
				faces[idf][2] = index(i + 1, j, M);
				faces[idf][1] = index(i + 1, j + 1, M);
				faces[idf][0] = index(i, j + 1, M);
				idf++;
			}
		}

		for (int k = 0; k < M; k++) {
			for (int i = 0; i < L; i++) {
				faces[idf][3] = index(i, 0, k);
				faces[idf][2] = index(i + 1, 0, k);
				faces[idf][1] = index(i + 1, 0, k + 1);
				faces[idf][0] = index(i, 0, k + 1);
				idf++;
			}
		}

		for (int k = 0; k < M; k++) {
			for (int i = 0; i < L; i++) {
				faces[idf][0] = index(i, N, k);
				faces[idf][1] = index(i + 1, N, k);
				faces[idf][2] = index(i + 1, N, k + 1);
				faces[idf][3] = index(i, N, k + 1);
				idf++;
			}
		}

		for (int k = 0; k < M; k++) {
			for (int j = 0; j < N; j++) {
				faces[idf][0] = index(0, j, k);
				faces[idf][1] = index(0, j + 1, k);
				faces[idf][2] = index(0, j + 1, k + 1);
				faces[idf][3] = index(0, j, k + 1);
				idf++;
			}
		}
		for (int k = 0; k < M; k++) {
			for (int j = 0; j < N; j++) {
				faces[idf][3] = index(L, j, k);
				faces[idf][2] = index(L, j + 1, k);
				faces[idf][1] = index(L, j + 1, k + 1);
				faces[idf][0] = index(L, j, k + 1);
				idf++;
			}
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces).setDuplicate(false);
		return fl.createBase().cleanUnusedElementsByFace();
	}

	private int index(final int i, final int j, final int k) {
		return i + (L + 1) * j + (L + 1) * (N + 1) * k;

	}
}
