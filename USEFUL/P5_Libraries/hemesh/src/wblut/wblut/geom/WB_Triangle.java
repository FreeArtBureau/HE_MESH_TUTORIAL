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

/**
 *  3D Triangle.
 */
public interface WB_Triangle {

	public WB_Plane getPlane();

	public WB_Point3d getCenter();

	public WB_Point3d getCentroid();

	public WB_Point3d getCircumcenter();

	public WB_Point3d getOrthocenter();

	public WB_Point3d getPointFromTrilinear(final double x, final double y,
			final double z);

	public WB_Point3d getPointFromBarycentric(final double x, final double y,
			final double z);

	public WB_Point3d getBarycentric(final WB_Point3d p);

	public WB_Point3d p1();

	public WB_Point3d p2();

	public WB_Point3d p3();

}
