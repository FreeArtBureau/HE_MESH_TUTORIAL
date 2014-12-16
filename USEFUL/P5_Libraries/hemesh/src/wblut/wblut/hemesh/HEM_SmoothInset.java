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


/**
 * Creates smoothed inset faces.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_SmoothInset extends HEM_Modifier {

	private int			rep;
	private double		offset;
	public HE_Selection	walls;
	public HE_Selection	inset;

	public HEM_SmoothInset() {
		rep = 1;
		offset = 0.1;
	}

	public HEM_SmoothInset setLevel(final int level) {
		rep = level;
		return this;
	}

	public HEM_SmoothInset setOffset(final double offset) {
		this.offset = offset;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(offset)
				.setRelative(false);
		mesh.modify(ext);
		for (int i = 0; i < rep; i++) {

			ext.extruded.collectEdges();
			final Iterator<HE_Edge> eItr = ext.extruded.eItr();
			while (eItr.hasNext()) {
				mesh.divideEdge(eItr.next(), 2);
			}
			ext.extruded.collectVertices();
			mesh.modifySelected(new HEM_Smooth(), ext.extruded);
		}
		inset = ext.extruded;
		walls = ext.walls;
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(offset)
				.setRelative(false);
		selection.parent.modifySelected(ext, selection);
		for (int i = 0; i < rep; i++) {

			ext.extruded.collectEdges();
			final Iterator<HE_Edge> eItr = ext.extruded.eItr();
			while (eItr.hasNext()) {
				selection.parent.divideEdge(eItr.next(), 2);
			}
			ext.extruded.collectVertices();
			selection.parent.modifySelected(new HEM_Smooth(), ext.extruded);
		}
		inset = ext.extruded;
		walls = ext.walls;
		return selection.parent;
	}

}