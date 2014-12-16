/**
 * 
 */
package wblut.geom;

import java.util.LinkedList;

import wblut.WB_Epsilon;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Containment {
	public static boolean contains(final WB_Point3d p, final WB_AABBTree tree) {
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (contains(p, current.getAABB())) {
				if (current.isLeaf()) {
					return true;
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return false;
	}

	public static boolean contains(final WB_Point3d p, final WB_AABB AABB) {
		return (p.x >= AABB.getMin().x) && (p.y >= AABB.getMin().y)
				&& (p.z >= AABB.getMin().z) && (p.x < AABB.getMax().x)
				&& (p.y < AABB.getMax().y) && (p.z < AABB.getMax().z);

	}

	/**
	 * Check if points p1 and p2 lie on same side of line A-B.
	 *
	 * @param p1 the p1
	 * @param p2 the p2
	 * @param A the a
	 * @param B the b
	 * @return true, false
	 */
	public static boolean sameSide(final WB_Point3d p1, final WB_Point3d p2,
			final WB_Point3d A, final WB_Point3d B) {
		final WB_Point3d t1 = B.get().sub(A);
		WB_Point3d t2 = p1.get().sub(A);
		WB_Point3d t3 = p2.get().sub(A);
		t2 = t1.cross(t2);
		t3 = t1.cross(t3);
		final double t = t2.dot(t3);
		if (t >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}

	/**
	 * Check if point p lies in triangle A-B-C.
	 *
	 * @param p the p
	 * @param A the a
	 * @param B the b
	 * @param C the c
	 * @return true, false
	 */
	public static boolean contains(final WB_Point3d p, final WB_Point3d A,
			final WB_Point3d B, final WB_Point3d C) {
		if (WB_Epsilon.isZeroSq(WB_Distance.sqDistanceToLine(A, B, C))) {
			return false;
		}
		if (sameSide(p, A, B, C) && sameSide(p, B, A, C)
				&& sameSide(p, C, A, B)) {
			return true;
		}
		return false;
	}

	public static boolean contains(final WB_Point3d p, final WB_Triangle T) {
		return contains(p, T.p1(), T.p2(), T.p3());
	}

}
