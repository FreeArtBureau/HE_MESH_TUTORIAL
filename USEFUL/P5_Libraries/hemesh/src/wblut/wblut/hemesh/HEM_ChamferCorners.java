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
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;



// TODO: Auto-generated Javadoc
/**
 * Chamfer all convex corners.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_ChamferCorners extends HEM_Modifier {

	/** Chamfer distance. */
	private WB_Parameter<Double>	distance;

	/**
	 * Instantiates a new HEM_ChamferCorners.
	 */
	public HEM_ChamferCorners() {
		super();
	}

	/**
	 * Set chamfer distance along vertex normals.
	 *
	 * @param d distance
	 * @return self
	 */
	public HEM_ChamferCorners setDistance(final double d) {
		distance = new WB_ConstantParameter<Double>(d);
		return this;
	}

	/**
	 * Set chamfer distance along vertex normals.
	 *
	 * @param d WB_Parameter
	 * @return self
	 */
	public HEM_ChamferCorners setDistance(final WB_Parameter<Double> d) {
		distance = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (distance == null) {
			return mesh;
		}

		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getVertexType() == WB_VertexType3D.CONVEX) {
				final WB_Normal3d N = new WB_Normal3d(v.getVertexNormal());
				final WB_Point3d O = new WB_Point3d(N).mult(-distance.value(v.x,
						v.y, v.z));
				N.mult(-1);
				O.add(v);
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
		if (distance == null) {
			return selection.parent;
		}

		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		selection.collectVertices();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = selection.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getVertexType() == WB_VertexType3D.CONVEX) {
				final WB_Normal3d N = new WB_Normal3d(v.getVertexNormal());
				final WB_Point3d O = new WB_Point3d(N).mult(-distance.value(v.x,
						v.y, v.z));
				N.mult(-1);
				O.add(v);
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
