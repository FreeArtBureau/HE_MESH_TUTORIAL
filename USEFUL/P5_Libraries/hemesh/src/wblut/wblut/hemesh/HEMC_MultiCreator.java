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
 * Abstract base class for simultaneous creation of multiple meshes. Implementation should
 * return an array of valid meshes.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public abstract class HEMC_MultiCreator {

	/** Number of meshes. */
	protected int	_numberOfMeshes;

	/**
	 * Instantiates a new HEC_MultiCreator.
	 *
	 */
	public HEMC_MultiCreator() {
		super();
		_numberOfMeshes = 0;
	}

	/**
	 * Create an array of meshes.
	 * 
	 * @return array of meshes
	 */
	public HE_Mesh[] create() {
		final HE_Mesh[] result = new HE_Mesh[0];
		return result;
	}

	/**
	 * Number of meshes.
	 *
	 * @return number of meshes.
	 */
	public int numberOfMeshes() {
		return _numberOfMeshes;
	}

}
