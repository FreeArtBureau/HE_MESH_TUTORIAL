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
 * 3D line.
 */
public class WB_Line extends WB_Linear {
	public static final WB_Line	X	= new WB_Line(0, 0, 0, 1, 0, 0);
	public static final WB_Line	Y	= new WB_Line(0, 0, 0, 0, 1, 0);
	public static final WB_Line	Z	= new WB_Line(0, 0, 0, 0, 0, 1);

	/**
	 * Instantiates a new WB_Line.
	 */
	public WB_Line() {
		super();

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * @param o origin
	 * @param d direction
	 */
	public WB_Line(final WB_Point3d o, final WB_Vector3d d) {
		super(o, d);

	}

	/**
	 * Sets new line parameters.
	 *
	 * @param o origin
	 * @param d direction
	 */
	@Override
	public void set(final WB_Point3d o, final WB_Vector3d d) {
		super.set(o, d);

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * @param o origin
	 * @param d direction
	 */
	public WB_Line(final WB_Point3d o, final WB_Normal3d d) {
		super(o, d);

	}

	/**
	 * Sets new line parameters.
	 *
	 * @param o origin
	 * @param d direction
	 */
	public void set(final WB_Point3d o, final WB_Normal3d d) {
		super.set(o, d);

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * @param p1x first point on line
	 * @param p1y first point on line
	 * @param p1z first point on line
	 * @param p2x second point on line
	 * @param p2y second point on line
	 * @param p2z second point on line
	 */
	public WB_Line(final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		super(new WB_Point3d(p1x, p1y, p1z), new WB_Point3d(p2x, p2y, p2z));

	}

	/**
	 * Instantiates a new WB_Line.
	 *
	 * @param p1 first point on line
	 * @param p2 second point on line
	 */
	public WB_Line(final WB_Point3d p1, final WB_Point3d p2) {
		super(p1, p2);

	}

	/**
	 * Sets new line parameters.
	 *
	 * @param p1 first point on line
	 * @param p2 second point on line
	 */
	@Override
	public void set(final WB_Point3d p1, final WB_Point3d p2) {
		super.set(p1, p2);
	}

	@Override
	public String toString() {
		return "Line: " + origin.toString() + " " + direction.toString();
	}
}
