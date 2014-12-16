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
import java.util.Collection;
import java.util.Iterator;

import wblut.geom.WB_Plane;



// TODO: Auto-generated Javadoc
/**
 * Multiple planar cuts of a mesh. No faces are removed.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_MultiSliceSurface extends HEM_Modifier {

	/** Cut planes. */
	private ArrayList<WB_Plane>	planes;

	/** Store cut faces. */
	public HE_Selection			cut;

	public HE_Selection			newEdges;

	private double				offset;

	/**
	 * Set offset.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEM_MultiSliceSurface setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_MultiSlice surface.
	 */
	public HEM_MultiSliceSurface() {
		super();
	}

	/**
	 * Set cut planes from an arrayList of WB_Plane.
	 *
	 * @param planes arrayList of WB_Plane
	 * @return self
	 */
	public HEM_MultiSliceSurface setPlanes(final Collection<WB_Plane> planes) {
		this.planes = new ArrayList<WB_Plane>();
		this.planes.addAll(planes);
		return this;
	}

	/**
	 * Set cut planes from an array of WB_Plane.
	 *
	 * @param planes array of WB_Plane
	 * @return self
	 */
	public HEM_MultiSliceSurface setPlanes(final WB_Plane[] planes) {
		this.planes = new ArrayList<WB_Plane>();
		for (final WB_Plane plane : planes) {
			this.planes.add(plane);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		cut = new HE_Selection(mesh);
		newEdges = new HE_Selection(mesh);

		mesh.resetFaceLabels();
		mesh.resetEdgeLabels();

		if (planes == null) {
			return mesh;
		}
		final HEM_SliceSurface slice = new HEM_SliceSurface();
		for (int k = 0; k < planes.size(); k++) {
			final WB_Plane P = planes.get(k);
			slice.setPlane(P).setOffset(offset);
			slice.apply(mesh);
			cut.union(slice.cut);
			newEdges.union(slice.newEdges);
		}
		cut.cleanSelection();
		newEdges.cleanSelection();
		final Iterator<HE_Edge> eItr = newEdges.eItr();
		while (eItr.hasNext()) {
			eItr.next().setLabel(1);
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		selection.parent.resetFaceLabels();
		selection.parent.resetEdgeLabels();
		cut = new HE_Selection(selection.parent);
		newEdges = new HE_Selection(selection.parent);
		if (planes == null) {
			return selection.parent;
		}
		final HEM_SliceSurface slice = new HEM_SliceSurface();
		for (int k = 0; k < planes.size(); k++) {
			final WB_Plane P = planes.get(k);
			slice.setPlane(P).setOffset(offset);
			slice.apply(selection);

			cut.union(slice.cut);
			newEdges.union(slice.newEdges);
		}
		cut.cleanSelection();
		newEdges.cleanSelection();
		final Iterator<HE_Edge> eItr = newEdges.eItr();
		while (eItr.hasNext()) {
			eItr.next().setLabel(1);
		}
		return selection.parent;
	}

}
