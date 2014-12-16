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

import wblut.WB_Epsilon;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point3d;



/**
 * Pseudo Dirichlet minimizer. Triangulates mesh. Does not add new vertices.
 * No claim whatsoever of scientif accuracy...
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_Soapfilm extends HEM_Modifier {
	private boolean	autoRescale;
	private int		iter;

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */

	public HEM_Soapfilm setAutoRescale(final boolean b) {
		autoRescale = b;
		return this;

	}

	public HEM_Soapfilm setIterations(final int r) {
		iter = r;
		return this;

	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.triangulate();
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
			int id = 0;
			final HE_Selection sel = mesh.selectAllFaces();
			final List<HE_Vertex> outer = sel.getOuterVertices();
			while (vItr.hasNext()) {
				v = vItr.next();
				if (outer.contains(v)) {
					newPositions[id] = v;
				} else {
					newPositions[id] = minDirichletEnergy(v);
				}
				id++;
			}
			vItr = mesh.vItr();
			id = 0;
			while (vItr.hasNext()) {
				v = vItr.next();
				v.set(newPositions[id]);
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
		selection.parent.triangulate();
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
			int id = 0;
			final HE_Selection sel = selection.parent.selectAllFaces();
			final List<HE_Vertex> outer = sel.getOuterVertices();

			while (vItr.hasNext()) {
				v = vItr.next();
				if (outer.contains(v)) {
					newPositions[id] = v;
				} else {
					newPositions[id] = minDirichletEnergy(v);
				}
				id++;
			}
			vItr = selection.vItr();
			id = 0;
			while (vItr.hasNext()) {
				v = vItr.next();
				v.set(newPositions[id]);
				id++;
			}
		}
		selection.parent.resetCenter();
		if (autoRescale) {
			selection.parent.fitInAABB(box);
		}
		return selection.parent;
	}

	private WB_Point3d minDirichletEnergy(final HE_Vertex v) {
		final WB_Point3d result = new WB_Point3d();
		final List<HE_Halfedge> hes = v.getHalfedgeStar();
		HE_Vertex neighbor;
		HE_Vertex corner;
		HE_Halfedge he;
		double cota;
		double cotb;
		double cotsum;
		double weight = 0;
		for (int i = 0; i < hes.size(); i++) {
			cotsum = 0;
			he = hes.get(i);
			neighbor = he.getEndVertex();
			{
				corner = he.getPrevInFace().getVertex();
				cota = WB_Point3d.cosAngleBetween(corner, neighbor, v);
				cotsum += cota / Math.sqrt(1 - cota * cota);
				corner = he.getPair().getPrevInFace().getVertex();
				cotb = WB_Point3d.cosAngleBetween(corner, neighbor, v);
				cotsum += cotb / Math.sqrt(1 - cotb * cotb);
			}
			result.add(neighbor, cotsum);
			weight += cotsum;
		}

		if (!WB_Epsilon.isZero(weight)) {
			result.div(weight);
		}
		return result;
	}

}
