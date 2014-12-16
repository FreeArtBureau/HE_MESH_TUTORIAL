/**
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import wblut.WB_Epsilon;
import wblut.core.Heap;
import wblut.geom.WB_ExplicitTriangle;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Vector3d;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HES_TriDec extends HES_Simplifier {
	private double				_lambda;
	private Heap<HE_Halfedge>	heap;
	private HE_Mesh				_mesh;
	FastMap<Integer, Double>	vertexCost;
	private int					goal;

	public HES_TriDec() {
		_lambda = 10;

	}

	public HES_TriDec setLambda(final double f) {
		_lambda = f;
		return this;
	}

	public HES_TriDec setGoal(final int g) {
		goal = g;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.simplifiers.HES_Simplifier#apply(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.triangulate();
		if (mesh.numberOfVertices() < 4) {
			return mesh;
		}
		if (goal < 4) {
			goal = (int)(0.9*mesh.numberOfVertices());
		}
		if (goal < 4) {
			return mesh;
		}

		_mesh = mesh;

		buildHeap();
		int cn = -1;
		while ((mesh.numberOfVertices() > goal)
				&& (cn != mesh.numberOfVertices())) {
			final HE_Halfedge he = heap.pop();
			final List<HE_Vertex> vertices = he.getVertex()
					.getNeighborVertices();
			cn = mesh.numberOfVertices();
			collapseHalfedge(he);
			vertexCost.remove(he.getVertex().key());
			updateHeap(vertices);

		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * wblut.hemesh.simplifiers.HES_Simplifier#apply(wblut.hemesh.core.HE_Selection
	 * )
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		// TODO Auto-generated method stub
		return apply(selection.parent);
	}

	private void buildHeap() {
		heap = new Heap<HE_Halfedge>();
		vertexCost = new FastMap<Integer, Double>(_mesh.numberOfVertices());
		final Iterator<HE_Vertex> vItr = _mesh.vItr();
		while (vItr.hasNext()) {
			final HE_Vertex v = vItr.next();
			final double vvi = visualImportance(v);
			vertexCost.put(v.key(), vvi);
			final List<HE_Halfedge> vstar = v.getHalfedgeStar();
			HE_Halfedge minhe = vstar.get(0);
			double min = halfedgeCollapseCost(vstar.get(0));
			for (int i = 1; i < vstar.size(); i++) {
				final double c = halfedgeCollapseCost(vstar.get(i));
				if (c < min) {
					min = c;
					minhe = vstar.get(i);
				}
			}
			heap.push(min * vvi, minhe);
		}
	}

	private void updateHeap(final List<HE_Vertex> vertices) {
		final Iterator<HE_Vertex> vItr = vertices.iterator();
		while (vItr.hasNext()) {
			final HE_Vertex v = vItr.next();
			final double vvi = visualImportance(v);
			vertexCost.put(v.key(), vvi);
			final List<HE_Halfedge> vstar = v.getHalfedgeStar();
			HE_Halfedge minhe = vstar.get(0);
			heap.removeNoRebuild(vstar.get(0));
			double min = halfedgeCollapseCost(vstar.get(0));
			for (int i = 1; i < vstar.size(); i++) {
				final double c = halfedgeCollapseCost(vstar.get(i));
				heap.removeNoRebuild(vstar.get(i));
				if (c < min) {
					min = c;
					minhe = vstar.get(i);

				}
			}
			heap.push(min * vvi, minhe);
		}
		heap.rebuild();
	}

	private double visualImportance(final HE_Vertex v) {
		final List<HE_Face> faces = v.getFaceStar();
		final WB_Normal3d nom = new WB_Normal3d();
		double denom = 0.0;
		double A;
		for (final HE_Face f : faces) {
			A = f.getFaceArea();
			nom.add(f.getFaceNormal(), A);
			denom += A;
		}
		if (WB_Epsilon.isZero(denom)) {
			throw new IllegalArgumentException(
					"HES_TriDec: can't simplify meshes with degenerate faces.");
		}
		nom.div(denom);
		return 1.0 - nom.mag();
	}

	private double halfedgeCollapseCost(final HE_Halfedge he) {
		final List<HE_Face> faces = new FastList<HE_Face>();
		final HE_Face f = he.getFace();
		final HE_Face fp = he.getPair().getFace();
		if ((f == null) || (fp == null)) {
			return Double.POSITIVE_INFINITY;
		}
		double cost = 0.0;
		HE_Halfedge helooper = he.getNextInVertex();
		do {
			final HE_Face fl = helooper.getFace();
			if (fl != null) {
				if ((fl != f) && (fl != fp)) {
					final WB_ExplicitTriangle T = new WB_ExplicitTriangle(
							he.getEndVertex(), helooper.getNextInFace()
									.getVertex(), helooper.getNextInFace()
									.getNextInFace().getVertex());
					final WB_Plane P = T.getPlane();
					if (P == null) {
						cost += 0.5 * (T.getArea() + fl.getFaceArea());
					} else {
						cost += 0.5
								* (T.getArea() + fl.getFaceArea())
								* (1.0 - fl.getFaceNormal().dot(
										T.getPlane().getNormal()));
					}

				}
			} else {
				return Double.POSITIVE_INFINITY;
			}
			helooper = helooper.getNextInVertex();

		} while (helooper != he);
		if ((f == null) || (fp == null)) {
			HE_Halfedge boundary = he.getNextInVertex();
			while ((he.getFace() != null) && (he.getPair().getFace() != null)) {
				boundary = boundary.getNextInVertex();
			}
			final WB_Vector3d v1 = he.getEndVertex()
					.subToVector(he.getVertex());
			v1.normalize();
			final WB_Vector3d v2 = boundary.getEndVertex().subToVector(
					boundary.getVertex());
			v2.normalize();
			cost += he.getEdge().getLength() * (1.0 - v1.dot(v2)) * _lambda;
		}
		return cost;

	}

	private void collapseHalfedge(final HE_Halfedge he) {

		_mesh.remove(he.getVertex());
		final HE_Vertex ve = he.getEndVertex();
		ve.setHalfedge(he.getNextInVertex());
		HE_Halfedge loophe = he;
		do {
			loophe.setVertex(ve);
			loophe = loophe.getNextInVertex();
		} while (loophe != he);

		if ((he.getFace() != null) && (he.getPair().getFace() != null)) {
			final HE_Halfedge heps = he.getPrevInVertex();
			final HE_Halfedge hens = he.getNextInVertex().getPair();
			hens.getVertex().setHalfedge(hens);

			final HE_Halfedge hep = he.getPair();
			final HE_Halfedge hepps = hep.getPrevInVertex();
			hep.getVertex().setHalfedge(hepps);
			final HE_Halfedge hepns = hep.getNextInVertex().getPair();
			hepns.getVertex().setHalfedge(hepns);

			heps.setPair(hepns);
			HE_Edge e = heps.getEdge();
			heps.setEdge(hepns.getEdge());
			hepns.getEdge().setHalfedge(hepns);
			_mesh.remove(e);

			hens.setPair(hepps);
			e = hens.getEdge();
			hens.setEdge(hepps.getEdge());
			hepps.getEdge().setHalfedge(hepps);
			_mesh.remove(e);

			_mesh.remove(he);
			_mesh.remove(he.getNextInFace());
			_mesh.remove(he.getPrevInFace());
			_mesh.remove(hep);
			_mesh.remove(hep.getNextInFace());
			_mesh.remove(hep.getPrevInFace());
			heap.removeNoRebuild(he);
			heap.removeNoRebuild(he.getNextInFace());
			heap.removeNoRebuild(he.getPrevInFace());
			heap.removeNoRebuild(hep);
			heap.removeNoRebuild(hep.getNextInFace());
			heap.removeNoRebuild(hep.getPrevInFace());

			_mesh.remove(he.getEdge());

			_mesh.remove(he.getFace());
			_mesh.remove(hep.getFace());
		} else if (he.getFace() == null) {
			_mesh.remove(he.getVertex());
			final HE_Halfedge henb = he.getNextInFace();
			final HE_Halfedge hepb = he.getPrevInFace();
			final HE_Halfedge hep = he.getPair();
			final HE_Halfedge hens = he.getNextInVertex().getPair();
			final HE_Halfedge heps = hep.getPrevInVertex();

			hepb.setNext(henb);

			hens.setPair(heps);
			final HE_Edge e = hens.getEdge();
			hens.setEdge(heps.getEdge());
			heps.getEdge().setHalfedge(heps);
			_mesh.remove(e);
			_mesh.remove(he);
			_mesh.remove(hep);
			_mesh.remove(he.getEdge());
			_mesh.remove(hep.getFace());

		} else {
			_mesh.remove(he.getVertex());
			final HE_Halfedge hep = he.getPair();
			final HE_Halfedge hepn = hep.getNextInFace();
			final HE_Halfedge hepp = hep.getPrevInFace();
			final HE_Halfedge heps = he.getPrevInVertex();
			final HE_Halfedge hens = hep.getNextInVertex().getPair();

			hepp.setNext(hepn);

			hens.setPair(heps);
			final HE_Edge e = hens.getEdge();
			hens.setEdge(heps.getEdge());
			heps.getEdge().setHalfedge(heps);
			_mesh.remove(e);
			_mesh.remove(he);
			_mesh.remove(hep);
			_mesh.remove(he.getEdge());
			_mesh.remove(he.getFace());

		}
	}
}
