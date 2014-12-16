/**
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_SphereInversion extends HEM_Modifier {
	private WB_Point3d	center;
	private double		r;
	private double		r2;
	private double		icutoff;
	private boolean		linear;

	public HEM_SphereInversion() {
		super();
		icutoff = 0.0001;
		linear = false;
	}

	public HEM_SphereInversion setCenter(final WB_Point3d c) {
		center = c;
		return this;
	}

	public HEM_SphereInversion setCenter(final double x, final double y,
			final double z) {
		center = new WB_Point3d(x, y, z);
		return this;
	}

	public HEM_SphereInversion setRadius(final double r) {
		this.r = r;
		r2 = r * r;
		return this;
	}

	public HEM_SphereInversion setCutoff(final double cutoff) {
		icutoff = 1.0 / cutoff;
		return this;
	}

	public HEM_SphereInversion setLinear(final boolean b) {
		linear = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEM_Modifier#modify(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (center == null) {
			return mesh;
		}
		if (r == 0) {
			return mesh;
		}
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		WB_Vector3d d;
		WB_Point3d surf;
		double ri, rf;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (linear) {
				d = v.subToVector(center);
				d.normalize();
				surf = new WB_Point3d(center).add(d, r);
				d = surf.subToVector(v).mult(2);
				v.add(d);
			} else {
				d = v.subToVector(center);
				ri = d.mag();
				d.normalize();
				rf = r2 * Math.max(icutoff, 1.0 / ri);
				v.set(center);
				v.add(d, rf);
			}

		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEM_Modifier#modifySelected(wblut.hemesh.core.
	 * HE_Mesh, wblut.hemesh.core.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if (center == null) {
			return selection.parent;
		}
		if (r == 0) {
			return selection.parent;
		}
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		WB_Vector3d d;
		WB_Point3d surf;
		double ri, rf;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (linear) {
				d = v.subToVector(center);
				d.normalize();
				surf = new WB_Point3d(center).add(d, r);
				d = v.subToVector(surf);
				v.add(d);
			} else {
				d = v.subToVector(center);
				ri = d.mag();
				d.normalize();
				rf = r2 * Math.max(icutoff, 1.0 / ri);
				v.set(center);
				v.add(d, rf);
			}

		}
		return selection.parent;
	}

}
