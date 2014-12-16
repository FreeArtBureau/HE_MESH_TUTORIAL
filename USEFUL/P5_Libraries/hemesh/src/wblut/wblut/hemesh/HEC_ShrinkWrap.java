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

import wblut.WB_Epsilon;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Distance;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Vector3d;



/**
 * Creates the dual of a mesh. Vertices are replace with faces connecting
 * all face centers surrounding original vertex. The faces are replaced by vertices
 * at their center.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_ShrinkWrap extends HEC_Creator {

	/** Source mesh. */
	private HE_Mesh		source;
	private int			level;
	private WB_Point3d	wcenter;
	private WB_AABBTree	tree;

	/**
	 * Instantiates a new HEC_ShrinkWrap.
	 *
	 */
	public HEC_ShrinkWrap() {
		super();
		override = true;
		toModelview = false;
		level = 2;
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh source mesh
	 * @return self
	 */
	public HEC_ShrinkWrap setSource(final HE_Mesh mesh) {
		source = mesh;
		return this;
	}

	public HEC_ShrinkWrap setSource(final HE_Mesh mesh, final WB_AABBTree tree) {
		source = mesh;
		this.tree = tree;
		return this;
	}

	public HEC_ShrinkWrap setLevel(final int level) {
		this.level = level;
		return this;
	}

	public HEC_ShrinkWrap setWrapCenter(final WB_Point3d c) {
		wcenter = c;
		return this;

	}

	public HEC_ShrinkWrap setWrapCenter(final double x, final double y,
			final double z) {
		wcenter = new WB_Point3d(x, y, z);
		return this;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		HE_Mesh result = new HE_Mesh();
		if (source == null) {
			return result;
		}
		final WB_AABB aabb = source.getAABB();
		if (wcenter == null) {
			wcenter = aabb.getCenter();
		}

		final double radius = WB_Distance.distance(center, aabb.getMax())
				+ WB_Epsilon.EPSILON;
		final HE_Mesh sphere = new HE_Mesh(new HEC_Geodesic().setLevel(level)
				.setRadius(radius).setCenter(wcenter));

		result = sphere.get();
		final Iterator<HE_Vertex> vItr = sphere.vItr();
		final Iterator<HE_Vertex> vmodItr = result.vItr();
		HE_Vertex v, vmod;
		WB_Ray R;
		if (tree == null) {
			tree = new WB_AABBTree(source, 4);
		}
		ArrayList<HE_Vertex> undecided = new ArrayList<HE_Vertex>();
		while (vItr.hasNext()) {
			v = vItr.next();
			vmod = vmodItr.next();
			R = new WB_Ray(v, v.getVertexNormal().mult(-1));
			final WB_Point3d p = HE_Intersection.getClosestIntersection(tree, R);
			if (p != null) {
				if (WB_Distance.distance(v, p) < radius) {
					vmod.set(p);
				} else {
					undecided.add(vmod);
				}

			} else {
				undecided.add(vmod);
			}

		}
		ArrayList<HE_Vertex> newundecided;
		while (undecided.size() > 0) {
			newundecided = new ArrayList<HE_Vertex>();
			for (int i = 0; i < undecided.size(); i++) {
				v = undecided.get(i);
				boolean lost = true;
				int decNeighbors = 0;
				double dist = 0;
				for (final HE_Vertex n : v.getNeighborVertices()) {
					if (!undecided.contains(n)) {
						lost = false;
						dist += WB_Distance.distance(wcenter, n);
						decNeighbors++;
					}
				}
				if (lost) {
					newundecided.add(v);
				} else {
					dist /= decNeighbors;
					final WB_Vector3d dv = v.subToVector(wcenter);
					dv.normalize();
					v.set(wcenter.addAndCopy(dv, dist));
				}

			}
			if (undecided.size() == newundecided.size()) {
				break;
			}
			undecided = newundecided;

		}

		return result;
	}
}
