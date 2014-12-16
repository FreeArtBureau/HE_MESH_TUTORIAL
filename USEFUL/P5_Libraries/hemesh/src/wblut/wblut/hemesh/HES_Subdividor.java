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


/**
 * Abstract base class for mesh subdivision. Implementation should preserve mesh validity.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
abstract public class HES_Subdividor implements HE_Machine {

	/**
	 * Instantiates a new HES_Subdividor.
	 */
	public HES_Subdividor() {

	}

	public abstract HE_Mesh apply(final HE_Mesh mesh);

	public abstract HE_Mesh apply(final HE_Selection selection);

}
