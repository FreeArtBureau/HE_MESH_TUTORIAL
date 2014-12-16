package wblut.hemesh;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;


/**
 * Helper class for HE_Export.saveToBinaryHemesh.
 * 
 * @author Frederik Vanhoutte, W:Blut
 *
 */

public class HET_BinaryHemeshWriter {

	protected FileOutputStream	hemeshStream;
	protected DataOutputStream	hemeshWriter;

	public void beginSave(final FileOutputStream stream) {
		try {
			hemeshStream = stream;
			handleBeginSave();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void beginSave(final String fn) {
		try {
			hemeshStream = new FileOutputStream(fn);
			handleBeginSave();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void endSave() {
		try {
			hemeshWriter.flush();
			hemeshWriter.close();
			hemeshStream.flush();
			hemeshStream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	protected void handleBeginSave() {
		hemeshWriter = new DataOutputStream(new DeflaterOutputStream(
				hemeshStream));
	}

	public void vertex(final HE_Vertex v, final int heid) {
		try {
			hemeshWriter.writeDouble(v.x);
			hemeshWriter.writeDouble(v.y);
			hemeshWriter.writeDouble(v.z);
			hemeshWriter.writeInt(heid);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void halfedge(final int vid, final int henextid, final int hepairid,
			final int edgeid, final int faceid) {
		try {
			hemeshWriter.writeInt(vid);
			hemeshWriter.writeInt(henextid);
			hemeshWriter.writeInt(hepairid);
			hemeshWriter.writeInt(edgeid);
			hemeshWriter.writeInt(faceid);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void edge(final int heid) {
		try {
			hemeshWriter.writeInt(heid);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void face(final int heid) {
		try {
			hemeshWriter.writeInt(heid);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void sizes(final int v1, final int v2, final int v3, final int v4) {
		try {
			hemeshWriter.writeInt(v1);
			hemeshWriter.writeInt(v2);
			hemeshWriter.writeInt(v3);
			hemeshWriter.writeInt(v4);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
