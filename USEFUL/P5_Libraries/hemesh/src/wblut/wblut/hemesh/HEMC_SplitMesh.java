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

import wblut.geom.WB_Plane;

/**
 * Planar cut of a mesh. Both parts are returned as separate meshes.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_SplitMesh extends HEMC_MultiCreator {

	/** Cutting plane. */
	private WB_Plane	P;

	/** Source mesh. */
	private HE_Mesh		mesh;

	/** Cap holes?. */
	private boolean		cap	= true;	;

	private double		offset;

	/**
	 * Set offset.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEMC_SplitMesh setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEMC_SplitMesh.
	 *
	 */
	public HEMC_SplitMesh() {
		super();
	}

	/**
	 * Set split plane.
	 *
	 * @param P plane
	 * @return self
	 */
	public HEMC_SplitMesh setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh mesh to split
	 * @return self
	 */
	public HEMC_SplitMesh setMesh(final HE_Mesh mesh) {
		this.mesh = mesh;
		return this;
	}

	/**
	 * Set option to cap holes.
	 *
	 * @param b true, false;
	 * @return self
	 */

	public HEMC_SplitMesh setCap(final Boolean b) {
		cap = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		final HE_Mesh[] result = new HE_Mesh[2];
		if (mesh == null) {
			_numberOfMeshes = 1;
			return result;
		}
		result[0] = mesh.get();
		if (P == null) {
			_numberOfMeshes = 1;
			return result;
		}
		final HEM_Slice sm = new HEM_Slice();
		sm.setPlane(P).setReverse(false).setCap(cap).setOffset(offset);
		sm.apply(result[0]);
		P.flipNormal();
		sm.setPlane(P).setReverse(false).setCap(cap).setOffset(offset);
		result[1] = mesh.get();
		sm.apply(result[1]);
		_numberOfMeshes = 2;
		return result;
	}
}
