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
package wblut.geom;

// TODO: Auto-generated Javadoc
/**
 * Placeholder class for a 3D tetrahedron.
 */
public class WB_Tetrahedron {

	/** First point. */
	public WB_Point3d	p1;

	/** Second point. */
	public WB_Point3d	p2;

	/** Third point. */
	public WB_Point3d	p3;

	/** Fourth point. */
	public WB_Point3d	p4;

	/**
	 * Instantiates a new WB_Tetrahedron. No copies are made.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @param p4 fourth point
	 */
	public WB_Tetrahedron(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d p3, final WB_Point3d p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	/**
	 * Instantiates a new WB_Tetrahedron.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @param p4 fourth point
	 * @param copy copy points?
	 */
	public WB_Tetrahedron(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d p3, final WB_Point3d p4, final boolean copy) {
		if (!copy) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		} else {
			this.p1 = p1.get();
			this.p2 = p2.get();
			this.p3 = p3.get();
			this.p4 = p4.get();
		}
	}

}