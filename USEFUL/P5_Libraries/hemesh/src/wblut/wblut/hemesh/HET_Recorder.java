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

import processing.core.PApplet;

/**
 * HET_Recorder is used to record meshes drawn in Processing.
 */
public class HET_Recorder {

	/** Mesh buffer. */
	private HET_MeshBuffer		meshBuffer;

	/** Calling applet. */
	private final PApplet		home;

	/** Recorded meshes. */
	public ArrayList<HE_Mesh>	meshes;

	/** Number of meshes. */
	public int					numberOfMeshes;

	/**
	 * Instantiates a new HET_Recorder.
	 *
	 * @param home calling applet, typically "this"
	 */
	public HET_Recorder(final PApplet home) {
		this.home = home;
	}

	/**
	 * Start recorder.
	 */
	public void start() {
		meshes = new ArrayList<HE_Mesh>();
		meshBuffer = (HET_MeshBuffer) home.createGraphics(home.width,
				home.height, "wblut.hemesh.tools.HET_MeshBuffer");
		meshBuffer.home = home;
		home.beginRecord(meshBuffer);

	}

	/**
	 * Start next mesh.
	 */
	public void nextMesh() {
		meshBuffer.nextMesh();
	}

	/**
	 * Stop recorder.
	 */
	public void stop() {
		meshBuffer.nextMesh();

		meshes = meshBuffer.meshes;
		home.endRecord();
		meshBuffer = null;
	}

}
