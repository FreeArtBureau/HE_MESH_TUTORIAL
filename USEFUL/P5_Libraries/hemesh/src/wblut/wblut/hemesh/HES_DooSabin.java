/**
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance;
import wblut.geom.WB_Point3d;


import javolution.util.FastMap;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HES_DooSabin extends HES_Subdividor {

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.subdividors.HES_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
	 */
	private double		faceFactor;
	private double		edgeFactor;
	private boolean		absolute;
	private double		d;
	public HE_Selection	faceFaces;
	public HE_Selection	edgeFaces;
	public HE_Selection	vertexFaces;

	public HES_DooSabin() {
		faceFactor = 1.0;
		edgeFactor = 1.0;

	}

	public HES_DooSabin setFactors(final double ff, final double ef) {
		faceFactor = ff;
		edgeFactor = ef;
		return this;
	}

	public HES_DooSabin setAbsolute(final boolean b) {
		absolute = b;
		return this;

	}

	public HES_DooSabin setDistance(final double d) {
		this.d = d;
		return this;

	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (mesh.selectAllBoundaryEdges().numberOfEdges() > 0) {
			throw new IllegalArgumentException(
					"HES_DooSabin only supports closed meshes at this time.");
		}
		Iterator<HE_Face> fItr = mesh.fItr();
		final FastMap<Integer, Integer> halfedgeCorrelation = new FastMap<Integer, Integer>();
		final ArrayList<WB_Point3d> newVertices = new ArrayList<WB_Point3d>();
		HE_Face f;
		HE_Halfedge he;
		WB_Point3d fc;
		int vertexCount = 0;
		double div = 1.0 + 2.0 * edgeFactor + faceFactor;

		if (WB_Epsilon.isZero(div)) {
			div = 1.0;
		}
		if (absolute) {
			div = 4.0;
		}
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			fc = f.getFaceCenter();
			do {
				final WB_Point3d p = fc.multAndCopy(faceFactor);
				p.add(he.getVertex());
				p.add(he.getHalfedgeCenter().multAndCopy(edgeFactor));
				p.add(he.getPrevInFace().getHalfedgeCenter()
						.multAndCopy(edgeFactor));
				p.div(div);
				if (absolute) {
					final double dcurrent = WB_Distance.distance(p,
							he.getVertex());
					p.sub(he.getVertex());
					p.mult(d / dcurrent);
					p.add(he.getVertex());

				}

				halfedgeCorrelation.put(he.key(), vertexCount);
				vertexCount++;

				newVertices.add(p);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		final int[][] faces = new int[mesh.numberOfFaces()
				+ mesh.numberOfEdges() + mesh.numberOfVertices()][];
		final int[] labels = new int[mesh.numberOfFaces()
				+ mesh.numberOfEdges() + mesh.numberOfVertices()];
		final int[] noe = { mesh.numberOfFaces(), mesh.numberOfEdges(),
				mesh.numberOfVertices() };
		int currentFace = 0;
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faces[currentFace] = new int[f.getFaceOrder()];
			he = f.getHalfedge();
			int i = 0;
			labels[currentFace] = currentFace;
			do {
				faces[currentFace][i] = halfedgeCorrelation.get(he.key());
				he = he.getNextInFace();
				i++;
			} while (he != f.getHalfedge());
			currentFace++;
		}
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		int currentEdge = 0;
		while (eItr.hasNext()) {

			e = eItr.next();
			faces[currentFace] = new int[4];
			faces[currentFace][3] = halfedgeCorrelation.get(e.getHalfedge()
					.key());
			faces[currentFace][2] = halfedgeCorrelation.get(e.getHalfedge()
					.getNextInFace().key());
			faces[currentFace][1] = halfedgeCorrelation.get(e.getHalfedge()
					.getPair().key());
			faces[currentFace][0] = halfedgeCorrelation.get(e.getHalfedge()
					.getPair().getNextInFace().key());
			labels[currentFace] = currentEdge;
			currentEdge++;
			currentFace++;
		}
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		int currentVertex = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			faces[currentFace] = new int[v.getVertexOrder()];
			he = v.getHalfedge();
			int i = v.getVertexOrder() - 1;
			do {
				faces[currentFace][i] = halfedgeCorrelation.get(he.key());
				he = he.getNextInVertex();
				i--;
			} while (he != v.getHalfedge());
			labels[currentFace] = currentVertex;
			currentVertex++;
			currentFace++;
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist().setFaces(faces)
				.setVertices(newVertices).setDuplicate(false);
		mesh.set(fl.create());
		fItr = mesh.fItr();
		currentFace = 0;
		faceFaces = new HE_Selection(mesh);
		edgeFaces = new HE_Selection(mesh);
		vertexFaces = new HE_Selection(mesh);
		while (fItr.hasNext()) {
			f = fItr.next();
			f.setLabel(labels[currentFace]);
			if (currentFace < noe[0]) {
				faceFaces.add(f);
			} else if (currentFace < noe[0] + noe[1]) {
				edgeFaces.add(f);
			} else {
				vertexFaces.add(f);
			}
			currentFace++;
		}

		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.subdividors.HES_Subdividor#subdivideSelected(wblut.hemesh
	 * .HE_Mesh, wblut.hemesh.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return selection.parent;
	}

}
