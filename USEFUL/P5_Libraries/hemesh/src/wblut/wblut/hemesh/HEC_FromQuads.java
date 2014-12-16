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
import wblut.geom.WB_Quad;



// TODO: Auto-generated Javadoc
/**
 * Creates a new mesh from a list of quads. Duplicate vertices are fused.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_FromQuads extends HEC_Creator {

	/** Quads. */
	private WB_Quad[]	quads;

	/**
	 * Instantiates a new HEC_FromQuads.
	 *
	 */
	public HEC_FromQuads() {
		super();
		override = true;
	}

	/**
	 * Instantiates a new HEC_FromQuads.
	 *
	 */
	public HEC_FromQuads(final WB_Quad[] qs) {
		this();
		quads = qs;
	}

	public HEC_FromQuads(final Collection<WB_Quad> qs) {
		this();
		setQuads(qs);
	}

	/**
	 * Sets the source quads.
	 *
	 * @param qs source quads
	 * @return self
	 */
	public HEC_FromQuads setQuads(final WB_Quad[] qs) {
		quads = qs;
		return this;
	}

	/**
	 * Sets the source quads.
	 *
	 * @param qs source quads
	 * @return self
	 */
	public HEC_FromQuads setQuads(final Collection<WB_Quad> qs) {
		final int n = qs.size();
		quads = new WB_Quad[n];
		int i = 0;
		for (final WB_Quad quad : qs) {
			quads[i] = quad;
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
		if (quads != null) {
			final int nq = quads.length;
			final WB_Point3d[] vertices = new WB_Point3d[nq * 4];
			final int[][] faces = new int[nq][4];
			for (int i = 0; i < nq; i++) {
				vertices[4 * i] = quads[i].p1;
				vertices[4 * i + 1] = quads[i].p2;
				vertices[4 * i + 2] = quads[i].p3;
				vertices[4 * i + 3] = quads[i].p4;
				faces[i][0] = 4 * i;
				faces[i][1] = 4 * i + 1;
				faces[i][2] = 4 * i + 2;
				faces[i][3] = 4 * i + 3;
			}
			final HEC_FromFacelist ffl = new HEC_FromFacelist().setVertices(
					vertices).setFaces(faces).setDuplicate(true);
			return ffl.createBase();
		}
		return null;
	}
}
