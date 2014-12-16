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
import java.util.Collection;
import java.util.Iterator;

import wblut.geom.WB_AABB;
import wblut.geom.WB_KDNeighbor;
import wblut.geom.WB_KDTree3Dold;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * Creates the Voronoi cells of a collection of points, constrained by a mesh.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_VoronoiCells extends HEMC_MultiCreator {

	/** Points. */
	private WB_Point3d[]	points;

	/** Number of points. */
	private int				numberOfPoints;

	/** Container. */
	private HE_Mesh			container;

	/** Treat container as surface? */
	private boolean			surface;

	private boolean			simpleCap;

	/** Offset. */
	private double			offset;

	public HE_Selection[]	inner;
	public HE_Selection[]	outer;

	/** Create divided skin of container. */
	private boolean			createSkin;

	/** Use dummy for neighbor resolution? */
	private boolean			useDummy;

	public int				limit;

	/**
	 * Instantiates a new HEMC_VoronoiCells.
	 *
	 */
	public HEMC_VoronoiCells() {
		super();
		useDummy = false;
	}

	/**
	 * Set mesh, defines both points and container.
	 *
	 * @param mesh HE_Mesh
	 * @param addCenter add mesh center as extra point?
	 * @return self
	 */
	public HEMC_VoronoiCells setMesh(final HE_Mesh mesh, final boolean addCenter) {
		if (addCenter) {
			points = new WB_Point3d[mesh.numberOfVertices() + 1];
			final WB_Point3d[] tmp = mesh.getVerticesAsPoint();
			for (int i = 0; i < mesh.numberOfVertices(); i++) {
				points[i] = tmp[i];
			}
			points[mesh.numberOfVertices()] = mesh.getCenter();
		} else {
			points = mesh.getVerticesAsPoint();
		}
		container = mesh;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points array of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final WB_Point3d[] points) {
		this.points = points;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points collection of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final Collection<WB_Point3d> points) {
		final int n = points.size();
		this.points = new WB_Point3d[n];
		int i = 0;

		for (final WB_Point3d point : points) {
			this.points[i] = point;
			i++;
		}

		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points 2D array of double of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final double[][] points) {
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
	public HEMC_VoronoiCells setPoints(final float[][] points) {
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
	public HEMC_VoronoiCells setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Set voronoi cell offset.
	 *
	 * @param o offset
	 * @return self
	 */
	public HEMC_VoronoiCells setOffset(final double o) {
		offset = o;
		return this;
	}

	/**
	 * Set enclosing mesh limiting cells.
	 *
	 * @param container enclosing mesh
	 * @return self
	 */
	public HEMC_VoronoiCells setContainer(final HE_Mesh container) {
		this.container = container;
		return this;
	}

	/**
	 * Set optional surface mesh mode.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEMC_VoronoiCells setSurface(final boolean b) {
		surface = b;
		return this;
	}

	public HEMC_VoronoiCells setSimpleCap(final boolean b) {
		simpleCap = b;
		return this;
	}

	/**
	 * Create skin mesh?
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEMC_VoronoiCells setCreateSkin(final boolean b) {
		createSkin = b;
		return this;
	}

	/**
	 * Use dummy mesh for fast neighbor resolution?
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEMC_VoronoiCells setUseDummy(final boolean b) {
		useDummy = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		HE_Mesh[] result;
		if (container == null) {
			result = new HE_Mesh[1];
			result[0] = new HE_Mesh();
			_numberOfMeshes = 0;
			return result;
		}
		if (points == null) {
			result = new HE_Mesh[1];
			result[0] = container;
			_numberOfMeshes = 1;
			return result;
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		if (useDummy) {
			return createWithDummy();
		}
		final ArrayList<HE_Mesh> lresult = new ArrayList<HE_Mesh>();
		final ArrayList<HE_Selection> linnersel = new ArrayList<HE_Selection>();
		final ArrayList<HE_Selection> loutersel = new ArrayList<HE_Selection>();
		final HEC_VoronoiCell cvc = new HEC_VoronoiCell();
		cvc.setPoints(points).setN(numberOfPoints).setContainer(container)
				.setSurface(surface).setOffset(offset).setSimpleCap(simpleCap);
		if (limit > 0) {
			final WB_KDTree3Dold<Integer> tree = new WB_KDTree3Dold<Integer>();
			for (int i = 0; i < numberOfPoints; i++) {
				tree.put(points[i], i);
			}

			for (int i = 0; i < numberOfPoints; i++) {
				cvc.setCellIndex(i);
				System.out.println("HEMC_VoronoiCells: creating cell "
						+ (i + 1) + " of " + numberOfPoints + ".");
				final WB_KDNeighbor<Integer>[] closest = tree
						.getNearestNeighbors(points[i], limit);
				final ArrayList<Integer> indicesToUse = new ArrayList<Integer>();
				for (int j = 0; j < limit; j++) {
					indicesToUse.add(closest[j].value());
				}

				cvc.setLimitPoints(true).setPointsToUse(indicesToUse);

				final HE_Mesh mesh = cvc.createBase();
				linnersel.add(cvc.inner);
				loutersel.add(cvc.outer);
				lresult.add(mesh);
			}

		} else {
			for (int i = 0; i < numberOfPoints; i++) {
				cvc.setCellIndex(i);
				System.out.println("HEMC_VoronoiCells: creating cell "
						+ (i + 1) + " of " + numberOfPoints + ".");
				final HE_Mesh mesh = cvc.createBase();
				linnersel.add(cvc.inner);
				loutersel.add(cvc.outer);
				lresult.add(mesh);
			}

		}

		result = new HE_Mesh[(createSkin) ? lresult.size() + 1 : lresult.size()];
		inner = new HE_Selection[lresult.size()];
		outer = new HE_Selection[lresult.size()];
		_numberOfMeshes = lresult.size();
		for (int i = 0; i < _numberOfMeshes; i++) {
			result[i] = lresult.get(i);
			inner[i] = linnersel.get(i);
			outer[i] = loutersel.get(i);
		}

		if (createSkin) {
			final boolean[] on = new boolean[_numberOfMeshes];
			for (int i = 0; i < _numberOfMeshes; i++) {
				on[i] = true;
			}

			result[_numberOfMeshes] = new HE_Mesh(new HEC_FromVoronoiCells()
					.setActive(on).setCells(result));
		}
		return result;
	}

	public HE_Mesh[] createWithDummy() {
		final WB_AABB AABB = new WB_AABB(points, numberOfPoints);
		final WB_AABB meshAABB = container.getAABB();
		AABB.getMin().x = Math.min(AABB.getMin().x, meshAABB.getMin().x);
		AABB.getMin().y = Math.min(AABB.getMin().y, meshAABB.getMin().y);
		AABB.getMin().z = Math.min(AABB.getMin().z, meshAABB.getMin().z);
		AABB.getMax().x = Math.max(AABB.getMax().x, meshAABB.getMax().x);
		AABB.getMax().y = Math.max(AABB.getMax().y, meshAABB.getMax().y);
		AABB.getMax().z = Math.max(AABB.getMax().z, meshAABB.getMax().z);

		final HE_Mesh dummy = new HE_Mesh(new HEC_Box().setFromAABB(AABB, 20));
		HE_Mesh[] result;

		final ArrayList<HE_Mesh> lresult = new ArrayList<HE_Mesh>();
		final ArrayList<HE_Selection> linnersel = new ArrayList<HE_Selection>();
		final ArrayList<HE_Selection> loutersel = new ArrayList<HE_Selection>();
		final HEC_VoronoiCell cvc = new HEC_VoronoiCell();
		cvc.setPoints(points).setN(numberOfPoints).setContainer(container)
				.setSurface(surface).setOffset(offset).setSimpleCap(simpleCap);

		final HEC_VoronoiCell dvc = new HEC_VoronoiCell();
		dvc.setPoints(points).setN(numberOfPoints).setContainer(dummy)
				.setSurface(surface).setOffset(offset).setSimpleCap(true);

		HE_Mesh mesh;
		for (int i = 0; i < numberOfPoints; i++) {
			cvc.setCellIndex(i);
			dvc.setCellIndex(i);
			System.out.println("HEMC_VoronoiCells: creating cell " + (i + 1)
					+ " of " + numberOfPoints + ".");

			mesh = dvc.createBase();
			final ArrayList<Integer> indicesToUse = new ArrayList<Integer>(
					mesh.numberOfFaces());
			final Iterator<HE_Face> fItr = mesh.fItr();
			while (fItr.hasNext()) {
				final int j = fItr.next().getLabel();
				if (j > -1) {
					indicesToUse.add(j);
				}
			}
			if (mesh.numberOfFaces() == 0) {
				mesh = new HE_Mesh();
				linnersel.add(new HE_Selection(mesh));
				loutersel.add(new HE_Selection(mesh));
			} else {
				cvc.setLimitPoints(true).setPointsToUse(indicesToUse);
				mesh = cvc.createBase();
				linnersel.add(cvc.inner);
				loutersel.add(cvc.outer);
			}
			lresult.add(mesh);
		}
		result = new HE_Mesh[lresult.size()];
		inner = new HE_Selection[lresult.size()];
		outer = new HE_Selection[lresult.size()];
		_numberOfMeshes = lresult.size();
		for (int i = 0; i < _numberOfMeshes; i++) {
			result[i] = lresult.get(i);
			inner[i] = linnersel.get(i);
			outer[i] = loutersel.get(i);
		}

		if (createSkin) {
			final boolean[] on = new boolean[_numberOfMeshes];
			for (int i = 0; i < _numberOfMeshes; i++) {
				on[i] = true;
			}

			final HE_Mesh[] temp = new HE_Mesh[result.length + 1];
			System.arraycopy(result, 0, temp, 0, result.length);
			temp[result.length] = new HE_Mesh(new HEC_FromVoronoiCells()
					.setActive(on).setCells(result));
			result = temp;
		}

		return result;
	}

}
