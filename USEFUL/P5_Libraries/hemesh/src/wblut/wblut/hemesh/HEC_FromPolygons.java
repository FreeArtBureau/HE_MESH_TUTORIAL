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
import wblut.geom.WB_Polygon;


import javolution.util.FastList;

// TODO: Auto-generated Javadoc
/**
 * Creates a new mesh from a list of polygons. Duplicate vertices are fused.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_FromPolygons extends HEC_Creator {

	/** Quads. */
	private WB_Polygon[]	polygons;

	/**
	 * Instantiates a new HEC_FromPolygons.
	 *
	 */
	public HEC_FromPolygons() {
		super();
		override = true;
	}

	/**
	 * Instantiates a new HEC_FromPolygons.
	 *
	 */
	public HEC_FromPolygons(final WB_Polygon[] qs) {
		this();
		polygons = qs;
	}

	public HEC_FromPolygons(final Collection<? extends WB_Polygon> qs) {
		this();
		setPolygons(qs);
	}

	/**
	 * Sets the source polygons.
	 *
	 * @param qs source polygons
	 * @return self
	 */
	public HEC_FromPolygons setPolygons(final WB_Polygon[] qs) {
		polygons = qs;
		return this;
	}

	/**
	 * Sets the source polygons.
	 *
	 * @param qs source polygons
	 * @return self
	 */
	public HEC_FromPolygons setPolygons(
			final Collection<? extends WB_Polygon> qs) {
		final int n = qs.size();
		polygons = new WB_Polygon[n];
		int i = 0;
		for (final WB_Polygon poly : qs) {
			polygons[i] = poly;
			i++;
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (polygons != null) {
			if (polygons.length > 0) {
				final int nq = polygons.length;
				final FastList<WB_Point3d> vertices = new FastList<WB_Point3d>(
						nq * 3);
				final int[][] faces = new int[nq][];
				int id = 0;
				for (int i = 0; i < nq; i++) {
					faces[i] = new int[polygons[i].getN()];
					for (int j = 0; j < polygons[i].getN(); j++) {
						vertices.add(polygons[i].getPoint(j));
						faces[i][j] = id;
						id++;
					}
				}
				final HEC_FromFacelist ffl = new HEC_FromFacelist()
						.setVertices(vertices).setFaces(faces)
						.setDuplicate(true);
				return ffl.createBase();
			}
		}
		return new HE_Mesh();
	}
}
