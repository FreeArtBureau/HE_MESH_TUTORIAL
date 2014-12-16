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

import java.util.Iterator;

import wblut.geom.WB_Normal3d;


import javolution.util.FastList;

// TODO: Auto-generated Javadoc
/**
 * Expands or contracts all vertices along the vertex normals.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEM_VertexExpand extends HEM_Modifier {

	/** Expansion distance. */
	private double	d;

	/**
	 * Instantiates a new HEM_VertexExpand.
	 */
	public HEM_VertexExpand() {

		super();
	}

	/**
	 * Set distance to move vertices.
	 *
	 * @param d distance
	 * @return this
	 */
	public HEM_VertexExpand setDistance(final double d) {
		this.d = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (d == 0) {
			return mesh;
		}
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = mesh.vItr();
		final FastList<WB_Normal3d> normals = new FastList<WB_Normal3d>(mesh
				.numberOfVertices());

		while (vItr.hasNext()) {
			v = vItr.next();
			normals.add(v.getVertexNormal());
		}
		final Iterator<WB_Normal3d> vnItr = normals.iterator();
		vItr = mesh.vItr();
		WB_Normal3d n;
		while (vItr.hasNext()) {

			v = vItr.next();
			n = vnItr.next();
			v.add(n.mult(d));
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		if (d == 0) {
			return selection.parent;
		}
		selection.collectVertices();
		final Iterator<HE_Vertex> vItr = selection.vItr();

		HE_Vertex v;

		while (vItr.hasNext()) {

			v = vItr.next();
			v.add(v.getVertexNormal().mult(d));
		}

		return selection.parent;
	}
}
