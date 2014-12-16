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
 * Planar cut of a mesh. Both parts are returned as separate meshes.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_Panelizer extends HEMC_MultiCreator {

	/** Source mesh. */
	private HE_Mesh	mesh;

	private double	thickness;
	private double range;

	/**
	 * Set thickness.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEMC_Panelizer setThickness(final double d) {
		thickness = d;
		range=0;
		return this;
	}
	
	public HEMC_Panelizer setThickness(final double dmin, double dmax) {
		thickness = dmin;
		range=dmax-dmin;
		return this;
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh mesh to panelize
	 * @return self
	 */
	public HEMC_Panelizer setMesh(final HE_Mesh mesh) {
		this.mesh = mesh;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {

		if (mesh == null) {
			_numberOfMeshes = 0;
			return null;
		}

		final HE_Mesh[] result = new HE_Mesh[mesh.numberOfFaces()];
		int id = 0;
		final HEC_Polygon pc = new HEC_Polygon().setThickness(thickness);
		for (final HE_Face f : mesh.getFacesAsList()) {
			pc.setThickness(thickness+Math.random()*range);
			pc.setPolygon(f.toPolygon());
			result[id] = new HE_Mesh(pc);
			id++;
		}

		return result;
	}
}
