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
import java.util.Iterator;
import java.util.List;

import wblut.geom.WB_Point3d;
import wblut.geom.WB_VertexType3D;



// TODO: Auto-generated Javadoc
/**
 * Catmull-Clark subdivision of a mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */

public class HES_Smooth extends HES_Subdividor {

	/** Keep edges? */
	private boolean	keepEdges		= true;

	/** Keep boundary? */
	private boolean	keepBoundary	= false;

	/** Weight of original vertex */
	private double	origWeight;

	/**Weight of neighbor vertex */
	private double	neigWeight;

	public HES_Smooth() {
		super();
		origWeight = 1.0;
		neigWeight = 1.0;

	}

	/**
	 * Keep edges of selection fixed when subdividing selection?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HES_Smooth setKeepEdges(final boolean b) {
		keepEdges = b;
		return this;

	}

	/**
	 * Keep boundary edges fixed?
	 *
	 * @param b true/false
	 * @return self
	 */
	public HES_Smooth setKeepBoundary(final boolean b) {
		keepBoundary = b;
		return this;

	}

	/**
	 * Set vertex weights?
	 *
	 * @param origWeight weight of original vertex
	 * @param neigWeight weight of neighbors
	 * @return self
	 */

	public HES_Smooth setWeight(final double origWeight, final double neigWeight) {
		this.origWeight = origWeight;
		this.neigWeight = neigWeight;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.quadSplitFaces();
		final WB_Point3d[] newPositions = new WB_Point3d[mesh
				.numberOfVertices()];
		final HE_Selection all = mesh.selectAllFaces();
		final List<HE_Vertex> boundary = all.getOuterVertices();
		final List<HE_Vertex> inner = all.getInnerVertices();

		HE_Vertex v;
		HE_Vertex n;
		List<HE_Vertex> neighbors;
		int id = 0;
		Iterator<HE_Vertex> vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			final WB_Point3d p = new WB_Point3d(v);
			neighbors = v.getNeighborVertices();
			p.mult(origWeight);
			double c = origWeight;
			for (int i = 0; i < neighbors.size(); i++) {
				n = neighbors.get(i);
				p.add(neigWeight * n.x, neigWeight * n.y, neigWeight * n.z);
				c += neigWeight;
			}
			newPositions[id] = p.scale(1.0 / c);

			id++;
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (keepBoundary) {
				newPositions[id] = v;
			} else {
				final WB_Point3d p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				p.mult(origWeight);
				double c = origWeight;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if (boundary.contains(n)) {
						p.add(neigWeight * n.x, neigWeight * n.y, neigWeight
								* n.z);
						c += neigWeight;
						nc++;
					}
				}
				newPositions[id] = (nc > 1) ? p.scale(1.0 / c) : v;
			}
			id++;
		}

		vItr = inner.iterator();
		id = 0;
		while (vItr.hasNext()) {
			vItr.next().set(newPositions[id]);
			id++;
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			vItr.next().set(newPositions[id]);
			id++;
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.subdividors.HEB_Subdividor#subdivideSelected(wblut.hemesh
	 * .HE_Mesh, wblut.hemesh.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		selection.parent.quadSplitFaces(selection);
		final List<WB_Point3d> newPositions = new ArrayList<WB_Point3d>();
		final List<HE_Vertex> boundary = selection.getBoundaryVertices();
		final List<HE_Vertex> outer = selection.getOuterVertices();
		final List<HE_Vertex> inner = selection.getInnerVertices();
		List<HE_Face> sharedFaces;
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = outer.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (boundary.contains(v)) {
				vItr.remove();
			}
		}

		HE_Vertex n;
		List<HE_Vertex> neighbors;
		int id = 0;
		vItr = inner.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			final WB_Point3d p = new WB_Point3d(v);
			neighbors = v.getNeighborVertices();
			p.mult(origWeight);
			double c = origWeight;
			for (int i = 0; i < neighbors.size(); i++) {
				n = neighbors.get(i);
				p.add(neigWeight * n.x, neigWeight * n.y, neigWeight * n.z);
				c += neigWeight;
			}
			newPositions.add(p.scale(1.0 / c));

			id++;
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (keepBoundary) {
				newPositions.add(v);
			} else {
				final WB_Point3d p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				p.mult(origWeight);
				double c = origWeight;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if (boundary.contains(n)) {
						p.add(neigWeight * n.x, neigWeight * n.y, neigWeight
								* n.z);
						c += neigWeight;
						nc++;
					}
				}
				newPositions.add((nc > 1) ? p.scale(1.0 / c) : v);
			}
			id++;
		}
		vItr = outer.iterator();
		while (vItr.hasNext()) {
			v = vItr.next();
			if ((keepEdges) || (v.getVertexType() != WB_VertexType3D.FLAT)) {
				newPositions.add(v);
			} else {
				final WB_Point3d p = new WB_Point3d(v);
				neighbors = v.getNeighborVertices();
				p.mult(origWeight);
				double c = origWeight;
				int nc = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					n = neighbors.get(i);
					if (outer.contains(n)) {
						sharedFaces = selection.parent.getSharedFaces(v, n);
						boolean singleFaceGap = true;
						for (int j = 0; j < sharedFaces.size(); j++) {
							if (selection.contains(sharedFaces.get(j))) {
								singleFaceGap = false;
								break;
							}
						}

						if (!singleFaceGap) {
							p.add(neigWeight * n.x, neigWeight * n.y,
									neigWeight * n.z);
							c += neigWeight;
							nc++;
						}
					}
				}
				newPositions.add((nc > 1) ? p.scale(1.0 / c) : v);
			}
			id++;
		}
		vItr = inner.iterator();
		id = 0;
		while (vItr.hasNext()) {
			vItr.next().set(newPositions.get(id));
			id++;
		}
		vItr = boundary.iterator();
		while (vItr.hasNext()) {
			vItr.next().set(newPositions.get(id));
			id++;
		}
		vItr = outer.iterator();
		while (vItr.hasNext()) {
			vItr.next().set(newPositions.get(id));
			id++;
		}
		return selection.parent;
	}

}
