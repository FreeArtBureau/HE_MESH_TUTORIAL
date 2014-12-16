/**
 * 
 */
package wblut.hemesh;

// Straight port from Karsten Schmidt's code
/*
 * __ .__ .__ ._____. _/ |_ _______ __|__| ____ | | |__\_ |__ ______ \ __\/ _ \
 * \/ / |/ ___\| | | || __ \ / ___/ | | ( <_> > <| \ \___| |_| || \_\ \\___ \
 * |__| \____/__/\_ \__|\___ >____/__||___ /____ > \/ \/ \/ \/ Copyright (c)
 * 2006-2011 Karsten Schmidt This library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 * http://creativecommons.org/licenses/LGPL/2.1/ This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
/**
 * Extremely bare bones Wavefront OBJ 3D format exporter. Purely handles the
 * writing of data to the .obj file, but does not have any form of mesh
 * management. See {@link TriangleMesh} for details.
 * 
 * Needs to get some more TLC in future versions.
 * 
 * @see TriangleMesh#saveAsOBJ(OBJWriter)
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;



public class HET_OBJWriter {

	protected OutputStream	objStream;
	protected PrintWriter	objWriter;

	protected int			numVerticesWritten	= 0;
	protected int			numNormalsWritten	= 0;

	public void beginSave(final OutputStream stream) {
		try {
			objStream = stream;
			handleBeginSave();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void beginSave(final String fn) {
		try {
			objStream = new FileOutputStream(fn);
			handleBeginSave();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void endSave() {
		try {
			objWriter.flush();
			objWriter.close();
			objStream.flush();
			objStream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void face(final int a, final int b, final int c) {
		objWriter.println("f " + a + " " + b + " " + c);
	}

	public void faceList() {
		objWriter.println("s off");
	}

	public void faceWithNormals(final int a, final int b, final int c,
			final int na, final int nb, final int nc) {
		objWriter.println("f " + a + "//" + na + " " + b + "//" + nb + " " + c
				+ "//" + nc);
	}

	public int getCurrNormalOffset() {
		return numNormalsWritten;
	}

	public int getCurrVertexOffset() {
		return numVerticesWritten;
	}

	protected void handleBeginSave() {
		objWriter = new PrintWriter(objStream);
		objWriter.println("# generated by HE_OBJExport");
		numVerticesWritten = 0;
		numNormalsWritten = 0;
	}

	public void newObject(final String name) {
		objWriter.println("o " + name);
	}

	public void normal(final WB_Normal3d n) {
		objWriter.println("vn " + n.x + " " + n.y + " " + n.z);
		numNormalsWritten++;
	}

	public void vertex(final WB_Point3d v) {
		objWriter.println("v " + v.x + " " + v.y + " " + v.z);
		numVerticesWritten++;
	}
}