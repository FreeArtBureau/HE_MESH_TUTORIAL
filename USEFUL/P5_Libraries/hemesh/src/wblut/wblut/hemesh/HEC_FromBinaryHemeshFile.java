/**
 * 
 */
package wblut.hemesh;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

import javolution.util.FastTable;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEC_FromBinaryHemeshFile extends HEC_Creator {

	/** File path. */
	private String	path;

	/**
	 * Instantiates a new HEC_FromHemeshFile.
	 *
	 */
	public HEC_FromBinaryHemeshFile() {
		super();
		override = true;
	}

	/**
	 * Instantiates a new HEC_FromHemeshFile.
	 *
	 * @param path
	 */
	public HEC_FromBinaryHemeshFile(final String path) {
		this();
		this.path = path;
	}

	/**
	 * Sets the source path.
	 *
	 * @param path
	 * @return self
	 */
	public HEC_FromBinaryHemeshFile setPath(final String path) {
		this.path = path;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (path == null) {
			return null;
		}
		final HE_Mesh mesh = new HE_Mesh();
		try {
			final FileInputStream fis = new FileInputStream(path);
			final DataInputStream dis = new DataInputStream(
					new InflaterInputStream(fis));
			final int numVertices = dis.readInt();
			final int numHalfedges = dis.readInt();
			final int numEdges = dis.readInt();
			final int numFaces = dis.readInt();

			final FastTable<HE_Vertex> vertices = new FastTable<HE_Vertex>(
					numVertices);
			for (int i = 0; i < numVertices; i++) {
				vertices.add(new HE_Vertex());
			}
			final FastTable<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>(
					numHalfedges);
			for (int i = 0; i < numHalfedges; i++) {
				halfedges.add(new HE_Halfedge());
			}
			final FastTable<HE_Edge> edges = new FastTable<HE_Edge>(numEdges);
			for (int i = 0; i < numEdges; i++) {
				edges.add(new HE_Edge());
			}
			final FastTable<HE_Face> faces = new FastTable<HE_Face>(numFaces);
			for (int i = 0; i < numFaces; i++) {
				faces.add(new HE_Face());
			}

			double x, y, z;
			int heid, vid, henextid, hepairid, eid, fid;
			HE_Vertex v;
			for (int i = 0; i < numVertices; i++) {
				v = vertices.get(i);
				x = dis.readDouble();
				y = dis.readDouble();
				z = dis.readDouble();
				heid = dis.readInt();
				v.set(x, y, z);
				if (heid > -1) {
					v.setHalfedge(halfedges.get(heid));
				}
			}
			HE_Halfedge he;
			for (int i = 0; i < numHalfedges; i++) {
				he = halfedges.get(i);
				vid = dis.readInt();
				henextid = dis.readInt();
				hepairid = dis.readInt();
				eid = dis.readInt();
				fid = dis.readInt();
				if (vid > -1) {
					he.setVertex(vertices.get(vid));
				}
				if (henextid > -1) {
					he.setNext(halfedges.get(henextid));
				}
				if (hepairid > -1) {
					he.setPair(halfedges.get(hepairid));
				}
				if (eid > -1) {
					he.setEdge(edges.get(eid));
				}
				if (fid > -1) {
					he.setFace(faces.get(fid));
				}
			}
			HE_Edge e;
			for (int i = 0; i < numEdges; i++) {
				e = edges.get(i);
				heid = dis.readInt();
				if (heid > -1) {
					e.setHalfedge(halfedges.get(heid));
				}
			}
			HE_Face f;
			for (int i = 0; i < numFaces; i++) {
				f = faces.get(i);
				heid = dis.readInt();
				if (heid > -1) {
					f.setHalfedge(halfedges.get(heid));
				}
			}
			mesh.addVertices(vertices);
			mesh.addHalfedges(halfedges);
			mesh.addEdges(edges);
			mesh.addFaces(faces);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}

		return mesh;

	}
}