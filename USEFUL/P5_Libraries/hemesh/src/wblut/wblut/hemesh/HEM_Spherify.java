/**
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_Point3d;
import wblut.geom.WB_Sphere;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_Spherify extends HEM_Modifier {
	private final WB_Sphere	sphere;

	/**
	 * Instantiates a new HEM_Spherify
	 */
	public HEM_Spherify() {
		super();
		sphere = new WB_Sphere();
	}

	/**
	 * Set radius of sphere
	 * 
	 * @param r radius
	 * @return self
	 */
	public HEM_Spherify setRadius(final double r) {
		sphere.setRadius(r);
		return this;
	}

	/**
	 * Set center of sphere
	 * 
	 * @param x center
	 * @param y center
	 * @param z center
	 * @return self
	 */
	public HEM_Spherify setCenter(final double x, final double y, final double z) {
		sphere.getCenter().set(x, y, z);
		return this;
	}

	/**
	 * Set center of sphere
	 * 
	 * @param c center
	 * @return self
	 */
	public HEM_Spherify setCenter(final WB_Point3d c) {
		sphere.setCenter(c);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEM_Modifier#apply(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(sphere.projectToSphere(v));
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @seewblut.hemesh.modifiers.HEM_Modifier#applySelected(wblut.hemesh.core.
	 * HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(sphere.projectToSphere(v));
		}
		return selection.parent;
	}

}
