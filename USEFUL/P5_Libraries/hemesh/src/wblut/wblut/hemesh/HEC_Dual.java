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
import java.util.HashMap;
import java.util.Iterator;


/**
 * Creates the dual of a mesh. Vertices are replace with faces connecting
 * all face centers surrounding original vertex. The faces are replaced by vertices
 * at their center.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_Dual extends HEC_Creator {

	/** Source mesh. */
	private HE_Mesh	source;

	/**
	 * Instantiates a new HEC_Dual.
	 *
	 */
	public HEC_Dual() {
		super();
		override = true;
		toModelview = false;
	}

	/**
	 * Instantiates a new HEC_Dual.
	 *
	 * @param mesh source mesh
	 */
	public HEC_Dual(final HE_Mesh mesh) {
		this();
		source = mesh;
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh source mesh
	 * @return self
	 */
	public HEC_Dual setSource(final HE_Mesh mesh) {
		source = mesh;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		final HE_Mesh result = new HE_Mesh();
		if (source == null) {
			return result;
		}

		final HashMap<Integer, Integer> faceVertexCorrelation = new HashMap<Integer, Integer>();
		final Iterator<HE_Face> fItr = source.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			final HE_Vertex cv = new HE_Vertex(f.getFaceCenter());
			faceVertexCorrelation.put(f.key(), cv.key());
			result.add(cv);
		}
		final Iterator<HE_Vertex> vItr = source.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			HE_Halfedge he = v.getHalfedge();
			final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
			final HE_Face nf = new HE_Face();
			result.add(nf);
			do {
				final HE_Halfedge hen = new HE_Halfedge();
				faceHalfedges.add(hen);
				hen.setFace(nf);
				final Integer key = faceVertexCorrelation.get(he.getFace()
						.key());
				hen.setVertex(result.getVertexByKey(key));
				if (hen.getVertex().getHalfedge() == null) {
					hen.getVertex().setHalfedge(hen);
				}
				if (nf.getHalfedge() == null) {
					nf.setHalfedge(hen);
				}
				he = he.getNextInVertex();
			} while (he != v.getHalfedge());
			HE_Mesh.cycleHalfedges(faceHalfedges);
			result.addHalfedges(faceHalfedges);
		}
		result.pairHalfedges();
		result.moveTo(source.getCenter());
		return result;
	}
}
