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
import java.util.Collections;
import java.util.Iterator;

import wblut.geom.WB_Distance;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;



// TODO: Auto-generated Javadoc
/**
 * Multiple planar cuts of a mesh. Faces on positive side of cut plane are
 * removed.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_MultiSlice extends HEM_Modifier {

	/** Cut planes. */
	private ArrayList<WB_Plane>	planes;

	/** Labels of cut faces. */
	private int[]				labels;

	/** Reverse planar cuts. */
	private boolean				reverse		= false;

	/** Keep center of cut mesh. */
	private boolean				keepCenter	= false;

	/** Center used to sort cut planes. */
	private WB_Point3d			center;

	/** Cap holes? */
	private boolean				capHoles	= true;

	private boolean				simpleCap	= true;

	/** Original faces? */
	public HE_Selection			origFaces;

	/** New faces? */
	public HE_Selection			newFaces;

	private double				offset;

	/**
	 * Set offset.
	 *
	 * @param d offset
	 * @return self
	 */
	public HEM_MultiSlice setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_MultiSlice.
	 */
	public HEM_MultiSlice() {
		super();
	}

	/**
	 * Set cut planes from an arrayList of WB_Plane.
	 *
	 * @param planes arrayList of WB_Plane
	 * @return self
	 */
	public HEM_MultiSlice setPlanes(final Collection<WB_Plane> planes) {
		this.planes = new ArrayList<WB_Plane>();
		this.planes.addAll(planes);
		return this;
	}

	/**
	 * Set cut planes from an array of WB_Plane.
	 *
	 * @param planes arrayList of WB_Plane
	 * @return self
	 */
	public HEM_MultiSlice setPlanes(final WB_Plane[] planes) {
		this.planes = new ArrayList<WB_Plane>();
		for (final WB_Plane plane : planes) {
			this.planes.add(plane);
		}
		return this;
	}

	/**
	 * Set labels of cut planes. Cap faces will be labeled.
	 *
	 * @param labels array of int
	 * @return self
	 */
	public HEM_MultiSlice setLabels(final int[] labels) {
		this.labels = labels;
		return this;
	}

	/**
	 * Set reverse option.
	 *
	 * @param b true, false
	 * @return self
	 */
	public HEM_MultiSlice setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Set center for cut plane sorting.
	 *
	 * @param c center
	 * @return self
	 */
	public HEM_MultiSlice setCenter(final WB_Point3d c) {
		center = c.get();
		return this;
	}

	/**
	 * Set option to cap holes.
	 *
	 * @param b true, false;
	 * @return self
	 */

	public HEM_MultiSlice setCap(final Boolean b) {
		capHoles = b;
		return this;
	}

	public HEM_MultiSlice setSimpleCap(final Boolean b) {
		simpleCap = b;
		return this;
	}

	/**
	 * Set option to reset mesh center.
	 *
	 * @param b true, false;
	 * @return self
	 */

	public HEM_MultiSlice setKeepCenter(final Boolean b) {
		keepCenter = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		origFaces = new HE_Selection(mesh);
		newFaces = new HE_Selection(mesh);
		if (planes == null) {
			return mesh;
		}
		if (labels == null) {
			labels = new int[planes.size()];
			for (int i = 0; i < planes.size(); i++) {
				labels[i] = i;
			}

		}
		Iterator<HE_Face> fItr = mesh.fItr();
		mesh.resetFaceLabels();
		final HEM_Slice slice = new HEM_Slice();
		slice.setReverse(reverse).setCap(capHoles).setOffset(offset)
				.setSimpleCap(simpleCap);
		if (center != null) {
			final double[] r = new double[planes.size()];
			for (int i = 0; i < planes.size(); i++) {
				final WB_Plane P = planes.get(i);
				r[i] = WB_Distance.sqDistance(P.getOrigin(), center);
			}
			for (int i = planes.size(); --i >= 0;) {
				for (int m = 0; m < i; m++) {
					if (r[m] > r[m + 1]) {
						Collections.swap(planes, m, m + 1);
						final double tmp = r[m];
						r[m] = r[m + 1];
						r[m + 1] = tmp;
						final int tmpid = labels[m];
						labels[m] = labels[m + 1];
						labels[m + 1] = tmpid;
					}
				}
			}
		}

		boolean unique = false;
		WB_Plane Pi;
		WB_Plane Pj;
		final int stop = planes.size();
		for (int i = 0; i < stop; i++) {
			Pi = planes.get(i);
			unique = true;
			for (int j = 0; j < i; j++) {
				Pj = planes.get(j);
				if (WB_Plane.isEqual(Pi, Pj)) {
					unique = false;
					break;
				}
			}
			if (unique) {

				slice.setPlane(Pi);
				slice.setKeepCenter(true);
				slice.apply(mesh);
				fItr = slice.cap.fItr();
				while (fItr.hasNext()) {

					fItr.next().setLabel(labels[i]);

				}
			}

		}

		fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == -1) {
				origFaces.add(f);
			} else {
				newFaces.add(f);
			}
		}
		if (!keepCenter) {
			mesh.resetCenter();
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
		return apply(selection.parent);
	}

}
