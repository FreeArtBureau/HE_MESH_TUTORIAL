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

import java.util.Collection;

import wblut.geom.WB_Point3d;
import wblut.geom.WB_Triangle;


import javolution.util.FastList;

// TODO: Auto-generated Javadoc
/**
 * Creates a new mesh from a list of triangles. Duplicate vertices are fused.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_FromTriangles<T extends WB_Triangle> extends HEC_Creator {

	/** Source triangles. */
	FastList<T>	triangles;

	/**
	 * Instantiates a new HEC_FromTriangles.
	 *
	 */
	public HEC_FromTriangles() {
		super();
		override = true;
	}

	/**
	 * Sets the source triangles.
	 *
	 * @param ts source triangles
	 * @return self
	 */
	public HEC_FromTriangles setTriangles(final T[] ts) {
		triangles = new FastList<T>();
		for (final T tri : ts) {
			triangles.add(tri);
		}
		return this;
	}

	/**
	 * Sets the source triangles.
	 *
	 * @param ts source triangles
	 * @return self
	 */
	public HEC_FromTriangles setTriangles(final Collection<T> ts) {

		triangles = new FastList<T>();
		triangles.addAll(ts);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (triangles != null) {

			final WB_Point3d[] vertices = new WB_Point3d[triangles.size() * 3];
			final int[][] faces = new int[triangles.size()][3];
			for (int i = 0; i < triangles.size(); i++) {
				vertices[3 * i] = triangles.get(i).p1();
				vertices[3 * i + 1] = triangles.get(i).p2();
				vertices[3 * i + 2] = triangles.get(i).p3();

				faces[i][0] = 3 * i;
				faces[i][1] = 3 * i + 1;
				faces[i][2] = 3 * i + 2;
			}
			System.out.println("HEC_FromTriangles: passing " + triangles.size()
					+ " triangles as faces.");
			final HEC_FromFacelist ffl = new HEC_FromFacelist()
					.setVertices(vertices).setFaces(faces).setDuplicate(true);
			return ffl.createBase();
		}
		return null;
	}

}
