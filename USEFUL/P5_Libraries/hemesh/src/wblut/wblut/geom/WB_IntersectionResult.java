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
 * WB_Intersection is a placeholder for 3D intersection test results.
 */
public class WB_IntersectionResult {

	/** First intersection value. */
	public double	t1				= Float.NEGATIVE_INFINITY;

	/** Section intersection value. */
	public double	t2				= Float.NEGATIVE_INFINITY;	;

	/** Intersection result. */
	public boolean	intersection	= false;

	/** Squared distance. */
	public double	sqDist			= Float.POSITIVE_INFINITY;

	public Object	object;

	public int		dimension		= -1;

}
