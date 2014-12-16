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

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance;
import wblut.geom.WB_KDNeighbor;
import wblut.geom.WB_KDTree3Dold;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.math.WB_RandomSphere;



// TODO: Auto-generated Javadoc
/**
 * Creates the Voronoi cell of one point in a collection of points, constrained
 * by a maximum radius.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_VoronoiSphere extends HEC_Creator {

	/** points. */
	private WB_Point3d[]			points;

	/** Number of points. */
	private int						numberOfPoints;

	/** Cell index. */
	private int						cellIndex;

	/** Level of geodesic sphere in exact mode. */
	private int						level;

	/** Maximum radius. */
	private double					cutoff;

	/** Point directions. */
	private WB_Normal3d[]			dir;

	/** Approximate mode? */
	private boolean					approx;

	/** Number of tracer points to use in approximate mode? */
	private int						numTracers;

	/** Starting trace step in approximate mode? */
	private double					traceStep;

	private final WB_RandomSphere	randomGen;

	/**
	 * Instantiates a new HEC_VoronoiSphere.
	 *
	 */
	public HEC_VoronoiSphere() {
		super();
		level = 1;
		traceStep = 10;
		numTracers = 100;
		override = true;
		randomGen = new WB_RandomSphere();
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points array of vertex positions
	 * @return self
	 */
	public HEC_VoronoiSphere setPoints(final WB_Point3d[] points) {
		this.points = points;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points 2D array of double of vertex positions
	 * @return self
	 */
	public HEC_VoronoiSphere setPoints(final double[][] points) {
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
	public HEC_VoronoiSphere setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point3d[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point3d(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set number of points.
	 *
	 * @param N number of points
	 * @return self
	 */
	public HEC_VoronoiSphere setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Set index of cell to create.
	 *
	 * @param i index
	 * @return self
	 */
	public HEC_VoronoiSphere setCellIndex(final int i) {
		cellIndex = i;
		return this;
	}

	/**
	 * Set level of geodesic sphere in each cell.
	 *
	 * @param l recursive level
	 * @return self
	 */
	public HEC_VoronoiSphere setLevel(final int l) {
		level = l;
		return this;
	}

	/**
	 * Set number of tracer points to use in approximate model.
	 *
	 * @param n number of tracer points
	 * @return self
	 */
	public HEC_VoronoiSphere setNumTracers(final int n) {
		numTracers = n;
		return this;
	}

	/**
	 * Set initial trace step size.
	 *
	 * @param d trace step
	 * @return self
	 */
	public HEC_VoronoiSphere setTraceStep(final double d) {
		traceStep = d;
		return this;
	}

	/**
	 * Set maximum radius of cell.
	 *
	 * @param c cutoff radius
	 * @return self
	 */
	public HEC_VoronoiSphere setCutoff(final double c) {
		cutoff = Math.abs(c);
		return this;
	}

	/**
	 * Set approximate mode.
	 *
	 * @param a true, false
	 * @return self
	 */
	public HEC_VoronoiSphere setApprox(final boolean a) {
		approx = a;
		return this;
	}

	/**
	 * Set seed of random generator.
	 *
	 * @param seed seed
	 * @return self
	 */
	public HEC_VoronoiSphere setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		if (cutoff == 0) {
			return new HE_Mesh();
		}
		if (points == null) {
			return new HE_Mesh();
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		if ((cellIndex < 0) || (cellIndex >= numberOfPoints)) {
			return new HE_Mesh();
		}

		HE_Mesh result;

		if (approx) {
			final WB_Point3d[] tracers = new WB_Point3d[numTracers];
			for (int i = 0; i < numTracers; i++) {
				tracers[i] = randomGen.nextPoint();

			}
			dir = new WB_Normal3d[numTracers];
			for (int i = 0; i < numTracers; i++) {
				dir[i] = new WB_Normal3d(tracers[i]);
				dir[i].normalize();
				tracers[i].add(points[cellIndex]);
			}
			grow(tracers, cellIndex);
			final HEC_ConvexHull ch = new HEC_ConvexHull().setPoints(tracers)
					.setN(numTracers);
			result = new HE_Mesh(ch);
		} else {
			final HEC_Geodesic gc = new HEC_Geodesic().setLevel(level);
			gc.setCenter(points[cellIndex]);
			gc.setRadius(cutoff);
			result = new HE_Mesh(gc);
			final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
			for (int j = 0; j < numberOfPoints; j++) {
				if (cellIndex != j) {

					final WB_Normal3d N = new WB_Normal3d(points[cellIndex]);
					N.sub(points[j]);

					N.normalize();
					final WB_Point3d O = new WB_Point3d(points[cellIndex]); // plane
					// origin=point
					// halfway
					// between point i and point j
					O.add(points[j]);
					O.mult(0.5);
					final WB_Plane P = new WB_Plane(O, N);
					cutPlanes.add(P);
				}
			}
			final HEM_MultiSlice msm = new HEM_MultiSlice();
			msm.setPlanes(cutPlanes).setSimpleCap(true)
					.setCenter(new WB_Point3d(points[cellIndex]));
			result.modify(msm);

		}
		return result;
	}

	/**
	 * Grow the tracers.
	 *
	 * @param tracers
	 * @param index
	 */
	private void grow(final WB_Point3d[] tracers, final int index) {
		final WB_KDTree3Dold<Integer> kdtree = new WB_KDTree3Dold<Integer>();
		for (int i = 0; i < numberOfPoints; i++) {
			kdtree.put(points[i], i);
		}

		int steps;
		final WB_Point3d c = new WB_Point3d(points[index]);
		WB_Point3d p;
		WB_Normal3d r;
		double d2self = 0;
		for (int i = 0; i < numTracers; i++) {
			p = tracers[i];
			r = dir[i];
			d2self = 0;
			double stepSize = traceStep;
			int j = index;
			while (stepSize > WB_Epsilon.EPSILON) {
				steps = 0;
				while ((j == index) && (d2self < cutoff * cutoff)) {
					steps++;
					p.add(stepSize * r.x, stepSize * r.y, stepSize * r.z);
					d2self = WB_Distance.sqDistance(p, c);
					final WB_KDNeighbor<Integer>[] closest = kdtree
							.getNearestNeighbors(p, 1, true);
					j = closest[0].value();
				}
				if (j != index) {
					p.sub(stepSize * r.x, stepSize * r.y, stepSize * r.z);
					d2self = 0;
					stepSize /= 2;
				} else {
					p.set(c.x + cutoff * r.x, c.y + cutoff * r.y, c.z + cutoff
							* r.z);
					stepSize = -1;

				}
			}
		}
	}

}
