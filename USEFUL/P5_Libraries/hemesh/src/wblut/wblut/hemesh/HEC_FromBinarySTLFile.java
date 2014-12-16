/**
 * 
 */
package wblut.hemesh;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import wblut.geom.WB_ExplicitTriangle;
import wblut.geom.WB_Point3d;



/**
 * @author Frederik Vanhoutte, W:Blut
 * 
 * based on
 *   __               .__       .__  ._____.           
 * _/  |_  _______  __|__| ____ |  | |__\_ |__   ______
 * \   __\/  _ \  \/  /  |/ ___\|  | |  || __ \ /  ___/
 *  |  | (  <_> >    <|  \  \___|  |_|  || \_\ \\___ \ 
 *  |__|  \____/__/\_ \__|\___  >____/__||___  /____  >
 *                   \/       \/             \/     \/ 
 *
 * Copyright (c) 2006-2011 Karsten Schmidt
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 */

public class HEC_FromBinarySTLFile extends HEC_Creator {
	private final byte[]	buf	= new byte[12];
	private String			path;
	private double			scale;

	public HEC_FromBinarySTLFile() {
		super();
		scale = 1;
		path = null;
		override = true;
	}

	public HEC_FromBinarySTLFile(final String path) {
		super();
		this.path = path;
		scale = 1;
		override = true;
	}

	public HEC_FromBinarySTLFile setPath(final String path) {
		this.path = path;
		return this;
	}

	public HEC_FromBinarySTLFile setScale(final double f) {
		scale = f;
		return this;
	}

	private final double bufferToDouble() {
		return Float.intBitsToFloat(bufferToInt());
	}

	private final int bufferToInt() {
		return byteToInt(buf[0]) | (byteToInt(buf[1]) << 8)
				| (byteToInt(buf[2]) << 16) | (byteToInt(buf[3]) << 24);
	}

	private final int byteToInt(final byte b) {
		return (b < 0 ? 256 + b : b);
	}

	private InputStream createInputStream(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("file can't be null");
		}
		try {
			InputStream stream = new FileInputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				stream = new GZIPInputStream(stream);
			}
			return stream;
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private WB_Point3d readVector(final DataInputStream ds, final WB_Point3d result)
			throws IOException {
		ds.read(buf, 0, 4);
		result.x = scale * bufferToDouble();
		ds.read(buf, 0, 4);
		result.y = scale * bufferToDouble();
		ds.read(buf, 0, 4);
		result.z = scale * bufferToDouble();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		final File file = new File(path);
		final InputStream stream = createInputStream(file);
		final ArrayList<WB_ExplicitTriangle> triangles = new ArrayList<WB_ExplicitTriangle>();
		try {
			final DataInputStream ds = new DataInputStream(
					new BufferedInputStream(stream, 0x8000));
			// read header, ignore color model
			for (int i = 0; i < 80; i++) {
				ds.read();
			}
			// read num faces
			ds.read(buf, 0, 4);
			final int numFaces = bufferToInt();
			final WB_Point3d a = new WB_Point3d();
			final WB_Point3d b = new WB_Point3d();
			final WB_Point3d c = new WB_Point3d();
			for (int i = 0; i < numFaces; i++) {
				// ignore face normal
				ds.read(buf, 0, 12);
				// face vertices
				readVector(ds, a);
				readVector(ds, b);
				readVector(ds, c);
				triangles
						.add(new WB_ExplicitTriangle(a.get(), b.get(), c.get()));
				// ignore colour
				ds.read(buf, 0, 2);
			}
			final HEC_FromTriangles<WB_ExplicitTriangle> ft = new HEC_FromTriangles<WB_ExplicitTriangle>();
			ft.setTriangles(triangles);
			return new HE_Mesh(ft);

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
