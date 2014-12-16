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

import java.util.ArrayList;

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * Creates the Voronoi cell of one point in a collection of points, constrained
 * by a mesh.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_VoronoiCell extends HEC_Creator {

	/** Points. */
	private WB_Point3d[]	points;

	/** Number of points. */
	private int				numberOfPoints;

	/** Use specific subselection of points. */
	private int[]			pointsToUse;

	/** Cell index. */
	private int				cellIndex;

	/** Container. */
	private HE_Mesh			container;

	/** Treat container as surface? */
	private boolean			surface;

	private boolean			simpleCap;

	/** Offset. */
	private double			offset;

	/** Faces fully interior to cell. */
	public HE_Selection		inner;

	/** Faces part of container. */
	public HE_Selection		outer;

	private boolean			limitPoints;

	/**
	 * Instantiates a new HEC_VoronoiCell.
	 *
	 */
	public HEC_VoronoiCell() {
		super();
		override = true;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points array of vertex positions
	 * @return self
	 */
	public HEC_VoronoiCell setPoints(final WB_Point3d[] points) {
		this.points = points;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points 2D array of double of vertex positions
	 * @return self
	 */
	public HEC_VoronoiCell setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points 2D array of float of vertex positions
	 * @return self
	 */
	public HEC_VoronoiCell setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	public HEC_VoronoiCell setPointsToUse(final int[] pointsToUse) {
		this.pointsToUse = pointsToUse;
		return this;
	}

	public HEC_VoronoiCell setPointsToUse(final ArrayList<Integer> pointsToUse) {
		final int n = pointsToUse.size();
		this.pointsToUse = new int[n];
		for (int i = 0; i < n; i++) {
			this.pointsToUse[i] = pointsToUse.get(i);
		}
		return this;
	}

	/**
	 * Set number of points.
	 *
	 * @param N number of points
	 * @return self
	 */
	public HEC_VoronoiCell setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Set index of cell to create.
	 *
	 * @param i index
	 * @return self
	 */
	public HEC_VoronoiCell setCellIndex(final int i) {
		cellIndex = i;
		return this;
	}

	/**
	 * Set voronoi cell offset.
	 *
	 * @param o offset
	 * @return self
	 */
	public HEC_VoronoiCell setOffset(final double o) {
		offset = o;
		return this;
	}

	/**
	 * Set enclosing mesh limiting cells.
	 *
	 * @param container enclosing mesh
	 * @return self
	 */
	public HEC_VoronoiCell setContainer(final HE_Mesh container) {
		this.container = container;
		return this;
	}

	/**
	 * Limit the points considered to those indices specified in the pointsToUseArray.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEC_VoronoiCell setLimitPoints(final boolean b) {
		limitPoints = b;
		return this;
	}

	/**
	 * Set optional surface mesh mode.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEC_VoronoiCell setSurface(final boolean b) {
		surface = b;
		return this;
	}

	public HEC_VoronoiCell setSimpleCap(final Boolean b) {
		simpleCap = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		if (container == null) {
			return new HE_Mesh();
		}
		if (points == null) {
			return container;
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		if ((cellIndex < 0) || (cellIndex >= numberOfPoints)) {
			return container;
		}
		final HE_Mesh result = container.get();

		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();

		int id = 0;
		final WB_Point3d O = new WB_Point3d();
		WB_Plane P;
		final int[] labels;
		if (limitPoints) {
			labels = new int[pointsToUse.length];
			for (final int element : pointsToUse) {
				if (cellIndex != element) {
					final WB_Normal3d N = new WB_Normal3d(points[cellIndex]);
					N.sub(points[element]);
					N.normalize();
					O.set(points[cellIndex]); // plane origin=point halfway
					// between point i and point j
					O.add(points[element]);
					O.mult(0.5);
					if (offset != 0) {
						O.add(N.multAndCopy(offset));
					}
					P = new WB_Plane(O, N);
					cutPlanes.add(P);
					labels[id] = element;
					id++;
				}
			}
		} else {
			labels = new int[numberOfPoints - 1];
			for (int j = 0; j < numberOfPoints; j++) {
				if (cellIndex != j) {
					final WB_Normal3d N = new WB_Normal3d(points[cellIndex]);
					N.sub(points[j]);
					N.normalize();
					O.set(points[cellIndex]); // plane origin=point halfway
					// between point i and point j
					O.add(points[j]);
					O.mult(0.5);
					if (offset != 0) {
						O.add(N.multAndCopy(offset));
					}
					P = new WB_Plane(O, N);
					cutPlanes.add(P);
					labels[id] = j;
					id++;
				}
			}
		}
		final HEM_MultiSlice msm = new HEM_MultiSlice();
		msm.setPlanes(cutPlanes).setCenter(new WB_Point3d(points[cellIndex]))
				.setCap(!surface).setKeepCenter(true).setLabels(labels)
				.setSimpleCap(simpleCap);
		result.modify(msm);
		inner = msm.newFaces;
		outer = msm.origFaces;
		return result;
	}
}
