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

import wblut.geom.WB_Plane;

/**
 * Planar cut of a mesh. Both parts are returned as separate meshes.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_Explode extends HEMC_MultiCreator {

	
	/** Source mesh. */
	private HE_Mesh		mesh;


	/**
	 * Instantiates a new HEMC_SplitMesh.
	 *
	 */
	public HEMC_Explode() {
		super();
	}


	/**
	 * Set source mesh.
	 *
	 * @param mesh mesh to split
	 * @return self
	 */
	public HEMC_Explode setMesh(final HE_Mesh mesh) {
		this.mesh = mesh;
		return this;
	}



	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		final ArrayList<HE_Mesh> result = new ArrayList<HE_Mesh>();
		if (mesh == null) {
			_numberOfMeshes = 0;
			HE_Mesh[] resarray=new HE_Mesh[result.size()];
			return (HE_Mesh[])result.toArray(resarray);
		}
		
		
		
		HE_Mesh[] resarray=new HE_Mesh[result.size()];
		return (HE_Mesh[])result.toArray(resarray);
		
		
	}
}
