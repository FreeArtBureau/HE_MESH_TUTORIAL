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

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_VertexType3D;



// TODO: Auto-generated Javadoc
/**
 * Chamfer all convex edges.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_ChamferEdges extends HEM_Modifier {

	/** Chamfer distance. */
	private double	distance;

	/**
	 * Instantiates a new HEM_ChamferEdges.
	 */
	public HEM_ChamferEdges() {
		super();
		distance = 0;
	}

	/**
	 * Set chamfer distance along edge normals.
	 *
	 * @param d distance
	 * @return self
	 */
	public HEM_ChamferEdges setDistance(final double d) {
		distance = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (distance == 0) {
			return mesh;
		}
		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if ((e.getStartVertex().getVertexType() == WB_VertexType3D.CONVEX)
					|| (e.getEndVertex().getVertexType() == WB_VertexType3D.CONVEX)) {
				final WB_Normal3d N = new WB_Normal3d(e.getEdgeNormal());
				final WB_Point3d O = new WB_Point3d(N).mult(-distance);
				N.mult(-1);
				O.add(e.getEdgeCenter());
				final WB_Plane P = new WB_Plane(O, N);
				cutPlanes.add(P);
			}

		}
		final HEM_MultiSlice msm = new HEM_MultiSlice();
		msm.setPlanes(cutPlanes);
		mesh.modify(msm);
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if (distance == 0) {
			return selection.parent;
		}
		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		selection.collectEdges();
		final Iterator<HE_Edge> eItr = selection.parent.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if ((e.getStartVertex().getVertexType() == WB_VertexType3D.CONVEX)
					|| (e.getEndVertex().getVertexType() == WB_VertexType3D.CONVEX)) {
				final WB_Normal3d N = new WB_Normal3d(e.getEdgeNormal());
				final WB_Point3d O = new WB_Point3d(N).mult(-distance);
				N.mult(-1);
				O.add(e.getEdgeCenter());
				final WB_Plane P = new WB_Plane(O, N);
				cutPlanes.add(P);
			}

		}
		final HEM_MultiSlice msm = new HEM_MultiSlice();
		msm.setPlanes(cutPlanes);
		selection.parent.modify(msm);

		return selection.parent;
	}
}