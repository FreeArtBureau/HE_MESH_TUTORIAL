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

import wblut.geom.WB_Distance;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * 2^n order geodesic sphere.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_Geodesic extends HEC_Creator {

	/** Outer radius. */
	private double	R;

	/** Level of recursive division. */
	private int		level;

	private int		type;

	/**
	 * Instantiates a new HEC_Geodesic.
	 *
	 */
	public HEC_Geodesic() {
		super();
		R = 0f;
	}

	/**
	 * Instantiates a new HEC_Geodesic.
	 *
	 * @param R radius
	 * @param L number of recursive subdivisions
	 */
	public HEC_Geodesic(final double R, final int L) {
		this();
		this.R = R;
		level = L;
	}

	/**
	 * Set radius of circumscribed sphere.
	 *
	 * @param R radius
	 * @return self
	 */
	public HEC_Geodesic setRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set number of recursive subdivisions.
	 *
	 * @param L number of recursive subdivisions
	 * @return self
	 */
	public HEC_Geodesic setLevel(final int L) {
		level = L;
		return this;
	}

	public HEC_Geodesic setType(final int t) {
		type = t;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {

		HE_Mesh result;
		HEC_Creator ic = new HEC_Icosahedron().setOuterRadius(R);
		if (type == 1) {
			ic = new HEC_Tetrahedron().setOuterRadius(R);
		} else if (type == 2) {
			ic = new HEC_Octahedron().setOuterRadius(R);
		}
		result = ic.createBase();
		final HES_PlanarMidEdge pmes = new HES_PlanarMidEdge();
		result.subdivide(pmes, level);
		final WB_Point3d bc = new WB_Point3d(0, 0, 0);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = result.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			final double d = Math.sqrt(WB_Distance.sqDistance(v, bc));
			v.mult(R / d);
		}

		return result;
	}
}
