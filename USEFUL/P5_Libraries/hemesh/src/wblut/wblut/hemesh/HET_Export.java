/**
 * 
 */
package wblut.hemesh;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;


import javolution.util.FastMap;

/**
 * 
 * Collection of export functions.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_Export {

	/*
	 * Copyright (c) 2006-2011 Karsten Schmidt This library is free software;
	 * you can redistribute it and/or modify it under the terms of the GNU
	 * Lesser General Public License as published by the Free Software
	 * Foundation; either version 2.1 of the License, or (at your option) any
	 * later version. http://creativecommons.org/licenses/LGPL/2.1/ This library
	 * is distributed in the hope that it will be useful, but WITHOUT ANY
	 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
	 * more details. You should have received a copy of the GNU Lesser General
	 * Public License along with this library; if not, write to the Free
	 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
	 * 02110-1301, USA
	 */
	/**
	* Saves the mesh as OBJ format by appending it to the given mesh
	* {@link HET_OBJWriter} instance. Karsten Schmidt, 2011
	* 
	* @param obj
	*/
	public static void saveToOBJ(final HE_Mesh mesh, final HET_OBJWriter obj) {
		final HE_Mesh triMesh = mesh.get();
		triMesh.triangulate();
		final int vOffset = obj.getCurrVertexOffset() + 1;
		final int nOffset = obj.getCurrNormalOffset() + 1;
		obj.newObject(mesh.key().toString());
		// vertices
		final FastMap<Integer, Integer> keyToIndex = new FastMap<Integer, Integer>(
				mesh.numberOfVertices());
		Iterator<HE_Vertex> vItr = triMesh.vItr();
		HE_Vertex v;
		int i = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			keyToIndex.put(v.key(), i);
			obj.vertex(v);
			i++;
		}

		vItr = mesh.vItr();
		while (vItr.hasNext()) {
			obj.normal(vItr.next().getVertexNormal());
		}

		// faces
		final Iterator<HE_Face> fItr = triMesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			obj.faceWithNormals(
					keyToIndex.get(f.getHalfedge().getVertex().key()) + vOffset,
					keyToIndex.get(f.getHalfedge().getNextInFace().getVertex()
							.key())
							+ vOffset,
					keyToIndex.get(f.getHalfedge().getPrevInFace().getVertex()
							.key())
							+ vOffset,
					keyToIndex.get(f.getHalfedge().getVertex().key()) + nOffset,
					keyToIndex.get(f.getHalfedge().getNextInFace().getVertex()
							.key())
							+ nOffset,
					keyToIndex.get(f.getHalfedge().getPrevInFace().getVertex()
							.key())
							+ nOffset);
		}
	}

	/**
	* Saves the mesh as OBJ format to the given {@link OutputStream}. Currently
	* no texture coordinates are supported or written. Karsten Schmidt, 2011
	* 
	* @param stream
	*/
	public static void saveToOBJ(final HE_Mesh mesh, final OutputStream stream) {
		final HET_OBJWriter obj = new HET_OBJWriter();
		obj.beginSave(stream);
		saveToOBJ(mesh, obj);
		obj.endSave();
	}

	/**
	 * Saves the mesh as OBJ format to the given file path. Existing files will
	 * be overwritten. Karsten Schmidt, 2011
	 * 
	 * @param path
	 */
	public static void saveToOBJ(final HE_Mesh mesh, final String path) {
		final HET_OBJWriter obj = new HET_OBJWriter();
		obj.beginSave(path);
		saveToOBJ(mesh, obj);
		obj.endSave();
	}

	/**
	 * Export mesh to binary STL file.
	 * Heav(i/en)ly inspired by Marius Watz
	 * 
	 * @param path
	 *            file path
	 * @param scale
	 *            scaling factor
	 */
	public static void saveToSTL(final HE_Mesh mesh, final String path,
			final double scale) {
		System.out.println("HET_Export.saveToSTL: copying mesh");
		final HE_Mesh save = mesh.get();
		System.out.println("HET_Export.saveToSTL: triangulating copy");
		save.triangulate();
		System.out.println("HET_Export.saveToSTL: opening stream");
		FileOutputStream out;
		try {
			out = new FileOutputStream(path);

			byte[] header = new byte[80];

			final ByteBuffer buf = ByteBuffer.allocate(200);

			header = new byte[80];
			buf.get(header, 0, 80);
			out.write(header);
			buf.rewind();

			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(save.numberOfFaces());
			buf.rewind();
			buf.get(header, 0, 4);
			out.write(header, 0, 4);
			buf.rewind();
			buf.clear();
			header = new byte[50];
			HE_Face f;
			int i = 1;
			final Iterator<HE_Face> fItr = save.fItr();
			while (fItr.hasNext()) {

				f = fItr.next();

				final WB_Normal3d n = f.getFaceNormal();
				final WB_Point3d v1 = f.getHalfedge().getVertex();
				final WB_Point3d v2 = f.getHalfedge().getNextInFace()
						.getVertex();
				final WB_Point3d v3 = f.getHalfedge().getNextInFace()
						.getNextInFace().getVertex();
				buf.rewind();
				buf.putFloat((float) n.x);
				buf.putFloat((float) n.y);
				buf.putFloat((float) n.z);
				buf.putFloat((float) (scale * v1.x));
				buf.putFloat((float) (scale * v1.y));
				buf.putFloat((float) (scale * v1.z));
				buf.putFloat((float) (scale * v2.x));
				buf.putFloat((float) (scale * v2.y));
				buf.putFloat((float) (scale * v2.z));
				buf.putFloat((float) (scale * v3.x));
				buf.putFloat((float) (scale * v3.y));
				buf.putFloat((float) (scale * v3.z));

				buf.rewind();
				buf.get(header);
				out.write(header);
				i++;

			}

			out.flush();
			out.close();
			System.out.println("HET_Export.saveToSTL:done saving");

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Saves the mesh as simpleMesh format to the given file path. Existing files will
	 * be overwritten. The file gives the vertex coordinates and an indexed facelist.
	 * 
	 * @param path
	 */
	public static void saveToSimpleMesh(final HE_Mesh mesh, final String path) {
		final HET_SimpleMeshWriter hem = new HET_SimpleMeshWriter();
		hem.beginSave(path);
		final WB_Point3d[] points = mesh.getVerticesAsPoint();
		hem.intValue(mesh.numberOfVertices());
		hem.vertices(points);
		final int[][] faces = mesh.getFacesAsInt();
		hem.intValue(mesh.numberOfFaces());
		hem.faces(faces);
		hem.endSave();
	}

	/**
	 * Saves the mesh as hemesh format to the given file path. Existing files will
	 * be overwritten. The file contains the vertex coordinates and all half-edge
	 * interconnection information. Larger than a simpleMesh but much quicker to rebuild.
	 * 
	 * @param path
	 */
	public static void saveToHemesh(final HE_Mesh mesh, final String path) {
		final HET_HemeshWriter hem = new HET_HemeshWriter();
		hem.beginSave(path);
		final FastMap<Integer, Integer> vertexKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> halfedgeKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Halfedge> heItr = mesh.heItr();
		i = 0;
		while (heItr.hasNext()) {
			halfedgeKeys.put(heItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> edgeKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Edge> eItr = mesh.eItr();
		i = 0;
		while (eItr.hasNext()) {
			edgeKeys.put(eItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> faceKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Face> fItr = mesh.fItr();
		i = 0;
		while (fItr.hasNext()) {
			faceKeys.put(fItr.next().key(), i);
			i++;
		}
		hem.sizes(mesh.numberOfVertices(), mesh.numberOfHalfedges(),
				mesh.numberOfEdges(), mesh.numberOfFaces());

		vItr = mesh.vItr();
		HE_Vertex v;
		Integer heid;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(v.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.vertex(v, heid);
		}

		heItr = mesh.heItr();
		HE_Halfedge he;
		Integer vid, henextid, hepairid, eid, fid;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getVertex() == null) {
				vid = -1;
			} else {
				vid = vertexKeys.get(he.getVertex().key());
				if (vid == null) {
					vid = -1;
				}
			}
			if (he.getNextInFace() == null) {
				henextid = -1;
			} else {
				henextid = halfedgeKeys.get(he.getNextInFace().key());
				if (henextid == null) {
					henextid = -1;
				}
			}
			if (he.getPair() == null) {
				hepairid = -1;
			} else {
				hepairid = halfedgeKeys.get(he.getPair().key());
				if (hepairid == null) {
					hepairid = -1;
				}
			}
			if (he.getEdge() == null) {
				eid = -1;
			} else {
				eid = edgeKeys.get(he.getEdge().key());
				if (eid == null) {
					eid = -1;
				}
			}
			if (he.getFace() == null) {
				fid = -1;
			} else {
				fid = faceKeys.get(he.getFace().key());
				if (fid == null) {
					fid = -1;
				}
			}
			hem.halfedge(vid, henextid, hepairid, eid, fid);
		}

		eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(e.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.edge(heid);
		}
		fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(f.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.face(heid);
		}
		hem.endSave();
	}

	/**
	 * Saves the mesh as binary hemesh format to the given file path. Existing files will
	 * be overwritten.  The file contains the vertex coordinates and all half-edge
	 * interconnection information. About the same size of a simpleMesh but a lot quicker
	 * to rebuild. Due to compression about half as fast as an ordinary hemesh file but only
	 * a third in size.
	 * 
	 * @param path
	 */
	public static void saveToBinaryHemesh(final HE_Mesh mesh, final String path) {
		final HET_BinaryHemeshWriter hem = new HET_BinaryHemeshWriter();
		hem.beginSave(path);
		final FastMap<Integer, Integer> vertexKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> halfedgeKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Halfedge> heItr = mesh.heItr();
		i = 0;
		while (heItr.hasNext()) {
			halfedgeKeys.put(heItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> edgeKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Edge> eItr = mesh.eItr();
		i = 0;
		while (eItr.hasNext()) {
			edgeKeys.put(eItr.next().key(), i);
			i++;
		}
		final FastMap<Integer, Integer> faceKeys = new FastMap<Integer, Integer>();
		Iterator<HE_Face> fItr = mesh.fItr();
		i = 0;
		while (fItr.hasNext()) {
			faceKeys.put(fItr.next().key(), i);
			i++;
		}
		hem.sizes(mesh.numberOfVertices(), mesh.numberOfHalfedges(),
				mesh.numberOfEdges(), mesh.numberOfFaces());

		vItr = mesh.vItr();
		HE_Vertex v;
		Integer heid;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(v.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.vertex(v, heid);
		}

		heItr = mesh.heItr();
		HE_Halfedge he;
		Integer vid, henextid, hepairid, eid, fid;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getVertex() == null) {
				vid = -1;
			} else {
				vid = vertexKeys.get(he.getVertex().key());
				if (vid == null) {
					vid = -1;
				}
			}
			if (he.getNextInFace() == null) {
				henextid = -1;
			} else {
				henextid = halfedgeKeys.get(he.getNextInFace().key());
				if (henextid == null) {
					henextid = -1;
				}
			}
			if (he.getPair() == null) {
				hepairid = -1;
			} else {
				hepairid = halfedgeKeys.get(he.getPair().key());
				if (hepairid == null) {
					hepairid = -1;
				}
			}
			if (he.getEdge() == null) {
				eid = -1;
			} else {
				eid = edgeKeys.get(he.getEdge().key());
				if (eid == null) {
					eid = -1;
				}
			}
			if (he.getFace() == null) {
				fid = -1;
			} else {
				fid = faceKeys.get(he.getFace().key());
				if (fid == null) {
					fid = -1;
				}
			}
			hem.halfedge(vid, henextid, hepairid, eid, fid);
		}

		eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(e.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.edge(heid);
		}
		fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getHalfedge() == null) {
				heid = -1;
			} else {
				heid = halfedgeKeys.get(f.getHalfedge().key());
				if (heid == null) {
					heid = -1;
				}
			}
			hem.face(heid);
		}
		hem.endSave();
	}

	/**
	* Saves the mesh as PovRAY mesh2 format by appending it to the given mesh
	* {@link HET_POVWriter} instance.
	*
	* @param obj
	* @param pov instance of HET_POVWriter
	* @param normals smooth faces
	*/
	public static void saveToPOV(final HE_Mesh mesh, final HET_POVWriter pov,
			final boolean normals) {
		final HE_Mesh triMesh = mesh.get();
		triMesh.triangulate();
		final int vOffset = pov.getCurrVertexOffset();
		pov.beginMesh2(String.format("obj%d", mesh.key()));
		final FastMap<Integer, Integer> keyToIndex = new FastMap<Integer, Integer>(
				mesh.numberOfVertices());
		Iterator<HE_Vertex> vItr = triMesh.vItr();
		final int vcount = mesh.numberOfVertices();
		pov.total(vcount);
		HE_Vertex v;
		int fcount = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			keyToIndex.put(v.key(), fcount);
			pov.vertex(v);
			fcount++;
		}
		pov.endSection();
		if (normals) {
			pov.beginNormals(vcount);
			vItr = triMesh.vItr();
			while (vItr.hasNext()) {
				pov.vertex(vItr.next().getVertexNormal());
			}
			pov.endSection();
		}
		final Iterator<HE_Face> fItr = triMesh.fItr();
		pov.beginIndices(triMesh.numberOfFaces());
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			pov.face(
					keyToIndex.get(f.getHalfedge().getVertex().key()) + vOffset,
					keyToIndex.get(f.getHalfedge().getNextInFace().getVertex()
							.key())
							+ vOffset,
					keyToIndex.get(f.getHalfedge().getPrevInFace().getVertex()
							.key())
							+ vOffset);
		}
		pov.endSection();
	}

	/**
	* Saves the mesh as PovRAY format to the given {@link PrintWriter}.
	*
	* @param mesh HE_Mesh
	* @param pw PrintWriter
	*/
	public static void saveToPOV(final HE_Mesh mesh, final PrintWriter pw) {
		saveToPOV(mesh, pw, true);
	}

	/**
	* Saves the mesh as PovRAY format to the given {@link PrintWriter}.
	*
	* @param mesh HE_Mesh
	* @param pw PrintWriter
	* @param saveNormals boolean (Smooth face or otherwise)
	*/
	public static void saveToPOV(final HE_Mesh mesh, final PrintWriter pw,
			final boolean saveNormals) {
		final HET_POVWriter obj = new HET_POVWriter();
		obj.beginSave(pw);
		saveToPOV(mesh, obj, saveNormals);
		obj.endSave();
	}

}
