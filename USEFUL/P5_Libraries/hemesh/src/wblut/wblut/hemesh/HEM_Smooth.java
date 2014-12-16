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
import java.util.List;

import wblut.geom.WB_AABB;
import wblut.geom.WB_Point3d;



/**
 * Simple Laplacian smooth modifier. Does not add new vertices.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_Smooth extends HEM_Modifier {
	private boolean	autoRescale;
	private int		iter;

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */

	public HEM_Smooth setAutoRescale(final boolean b) {
		autoRescale = b;
		return this;

	}

	public HEM_Smooth setIterations(final int r) {
		iter = r;
		return this;

	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		WB_AABB box = new WB_AABB();
		if (autoRescale) {
			box = mesh.getAABB();
		}

		final WB_Point3d[] newPositions = new WB_Point3d[mesh
				.numberOfVertices()];
		if (iter < 1) {
			iter = 1;
		}
		for (int r = 0; r < iter; r++) {
			Iterator<HE_Vertex> vItr = mesh.vItr();
			HE_Vertex v;
			List<HE_Vertex> neighbors;
			int id = 0;
			WB_Point3d p;
			while (vItr.hasNext()) {
				v = vItr.next();
				p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				p.mult(neighbors.size());
		
				for (int i = 0; i < neighbors.size(); i++) {
					p.add(neighbors.get(i));
				}

				newPositions[id] = p.scale(0.5 / neighbors.size());
				
				id++;
				
			}
			vItr = mesh.vItr();
			id = 0;
			while (vItr.hasNext()) {
				vItr.next().set(newPositions[id]);
				id++;
			}
			
		}
		mesh.resetCenter();
		if (autoRescale) {
			mesh.fitInAABB(box);
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		selection.collectVertices();

		WB_AABB box = new WB_AABB();
		if (autoRescale) {
			box = selection.parent.getAABB();
		}
		final WB_Point3d[] newPositions = new WB_Point3d[selection
				.numberOfVertices()];
		if (iter < 1) {
			iter = 1;
		}
		for (int r = 0; r < iter; r++) {
			Iterator<HE_Vertex> vItr = selection.vItr();
			HE_Vertex v;
			HE_Vertex n;
			List<HE_Vertex> neighbors;
			int id = 0;

			while (vItr.hasNext()) {
				v = vItr.next();
				final WB_Point3d p = new WB_Point3d(v);

				neighbors = v.getNeighborVertices();
				final Iterator<HE_Vertex> nItr = neighbors.iterator();
				while (nItr.hasNext()) {
					n = nItr.next();
					if (!selection.contains(n)) {
						nItr.remove();
					}
				}
				p.mult(neighbors.size());
				for (int i = 0; i < neighbors.size(); i++) {
					p.add(neighbors.get(i));
				}

				newPositions[id] = p.scale(0.5 / neighbors.size());
				id++;
			}
			vItr = selection.vItr();
			id = 0;
			while (vItr.hasNext()) {
				vItr.next().set(newPositions[id]);
				id++;
			}
		}
		selection.parent.resetCenter();
		if (autoRescale) {
			selection.parent.fitInAABB(box);
		}
		return selection.parent;
	}

}
