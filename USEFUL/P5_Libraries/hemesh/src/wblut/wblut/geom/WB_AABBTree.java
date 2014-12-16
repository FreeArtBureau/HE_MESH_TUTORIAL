/**
 * 
 */
package wblut.geom;

import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_AABBTree {
	private WB_AABBNode	root;
	private final int	maxLevel;
	private final int	maxNumberOfFaces;

	public WB_AABBTree(final HE_Mesh mesh, final int mnof) {
		maxLevel = 2 * (int) Math.ceil(Math.log(mesh.numberOfFaces())
				/ Math.log(3.0));
		maxNumberOfFaces = Math.max(1, mnof);
		buildTree(mesh);

	}

	private void buildTree(final HE_Mesh mesh) {
		root = new WB_AABBNode();
		final HE_Selection faces = new HE_Selection(mesh);
		faces.addFaces(mesh.getFacesAsList());
		buildNode(root, faces, mesh, 0);
	}

	private void buildNode(final WB_AABBNode node, final HE_Selection faces,
			final HE_Mesh mesh, final int level) {
		node.level = level;
		faces.collectVertices();
		node.aabb = faces.getAABB();
		if ((level == maxLevel) || (faces.numberOfFaces() <= maxNumberOfFaces)) {
			node.faces.addAll(faces.getFacesAsList());
			node.isLeaf = true;
		} else {
			final HE_Selection pos = new HE_Selection(mesh);
			final HE_Selection neg = new HE_Selection(mesh);
			final HE_Selection mid = new HE_Selection(mesh);
			final WB_Vector3d dir = new WB_Vector3d();
			if (level % 3 == 0) {
				dir.set(0, 0, 1);

			} else if (level % 3 == 1) {
				dir.set(0, 1, 0);
			} else {
				dir.set(1, 0, 0);
			}

			node.separator = new WB_Plane(node.aabb.getCenter(), dir);
			for (final HE_Face face : faces.getFacesAsList()) {
				final WB_ClassifyPolygonToPlane cptp = node.separator
						.classifyPolygonToPlane(face.toPolygon());
				if (cptp == WB_ClassifyPolygonToPlane.POLYGON_STRADDLING_PLANE) {
					mid.add(face);
				} else if (cptp == WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE) {
					neg.add(face);
				} else {
					pos.add(face);
				}
			}
			node.isLeaf = true;
			if (mid.numberOfFaces() > 0) {
				node.mid = new WB_AABBNode();
				buildNode(node.mid, mid, mesh, level + 1);
				node.isLeaf = false;

			}
			if (neg.numberOfFaces() > 0) {
				node.negative = new WB_AABBNode();
				buildNode(node.negative, neg, mesh, level + 1);
				node.isLeaf = false;

			}
			if (pos.numberOfFaces() > 0) {
				node.positive = new WB_AABBNode();
				buildNode(node.positive, pos, mesh, level + 1);
				node.isLeaf = false;
			}

		}
	}

	public WB_AABBNode getRoot() {
		return root;
	}
}
