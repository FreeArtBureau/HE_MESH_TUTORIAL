/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.math.WB_Fast;


import javolution.util.FastList;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_BSPTree2D {
	private WB_BSPNode2D	root;

	public WB_BSPTree2D() {
		root = null;
	}

	private void build(final WB_BSPNode2D tree,
			final List<WB_ExplicitSegment2D> segs) {
		WB_ExplicitSegment2D cseg = null;
		final Iterator<WB_ExplicitSegment2D> S2DItr = segs.iterator();
		if (S2DItr.hasNext()) {
			cseg = S2DItr.next();
		}
		tree.partition = new WB_Line2D(cseg.getOrigin(), cseg.getDirection());
		final FastList<WB_ExplicitSegment2D> _segs = new FastList<WB_ExplicitSegment2D>();

		_segs.add(cseg);
		final FastList<WB_ExplicitSegment2D> pos_list = new FastList<WB_ExplicitSegment2D>();
		final FastList<WB_ExplicitSegment2D> neg_list = new FastList<WB_ExplicitSegment2D>();
		WB_ExplicitSegment2D seg = null;
		while (S2DItr.hasNext()) {
			seg = S2DItr.next();
			final WB_ClassifySegmentToLine2D result = tree.partition
					.classifySegmentToLine2D(seg);

			if (result == WB_ClassifySegmentToLine2D.SEGMENT_IN_FRONT_OF_LINE) {
				pos_list.add(seg);
			} else if (result == WB_ClassifySegmentToLine2D.SEGMENT_BEHIND_LINE) {
				neg_list.add(seg);
			} else if (result == WB_ClassifySegmentToLine2D.SEGMENT_SPANNING_LINE) { /* spanning */
				final WB_ExplicitSegment2D[] split_seg = WB_Intersection2D
						.splitSegment2D(seg, tree.partition);
				if (split_seg != null) {
					pos_list.add(split_seg[0]);
					neg_list.add(split_seg[1]);
				} else {

				}
			} else if (result == WB_ClassifySegmentToLine2D.SEGMENT_ON_LINE) {
				_segs.add(seg);
			}
		}
		if (!pos_list.isEmpty()) {
			tree.pos = new WB_BSPNode2D();
			build(tree.pos, pos_list);
		}
		if (!neg_list.isEmpty()) {
			tree.neg = new WB_BSPNode2D();
			build(tree.neg, neg_list);
		}
		if (tree.segments != null) {
			tree.segments.clear();
		}
		tree.segments.addAll(_segs);
	}

	public void build(final List<WB_ExplicitSegment2D> segments) {
		if (root == null) {
			root = new WB_BSPNode2D();
		}
		build(root, segments);
	}

	public void build(final WB_Polygon2D poly) {
		if (root == null) {
			root = new WB_BSPNode2D();
		}
		build(root, poly.toExplicitSegments());
	}

	public int pointLocation(final WB_Point2d p) {
		return pointLocation(root, p);

	}

	public int pointLocation(final double x, final double y) {
		return pointLocation(root, new WB_Point2d(x, y));

	}

	private int pointLocation(final WB_BSPNode2D node, final WB_Point2d p) {
		final WB_ClassifyPointToLine2D type = node.partition
				.classifyPointToLine2D(p);
		if (type == WB_ClassifyPointToLine2D.POINT_IN_FRONT_OF_LINE) {
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			} else {
				return 1;
			}
		} else if (type == WB_ClassifyPointToLine2D.POINT_BEHIND_LINE) {
			if (node.neg != null) {
				return pointLocation(node.neg, p);
			} else {
				return -1;
			}
		} else {
			for (int i = 0; i < node.segments.size(); i++) {
				if (WB_Epsilon.isZero(WB_Distance2D.distance(p,
						node.segments.get(i)))) {
					return 0;
				}
			}
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			} else if (node.neg != null) {
				return pointLocation(node.neg, p);
			} else {
				return 0;

			}
		}
	}

	public void partitionSegment(final WB_ExplicitSegment2D S,
			final List<WB_ExplicitSegment2D> pos,
			final List<WB_ExplicitSegment2D> neg,
			final List<WB_ExplicitSegment2D> coSame,
			final List<WB_ExplicitSegment2D> coDiff) {

		partitionSegment(root, S, pos, neg, coSame, coDiff);

	}

	private void partitionSegment(final WB_BSPNode2D node,
			final WB_ExplicitSegment2D S, final List<WB_ExplicitSegment2D> pos,
			final List<WB_ExplicitSegment2D> neg,
			final List<WB_ExplicitSegment2D> coSame,
			final List<WB_ExplicitSegment2D> coDiff) {

		final WB_ClassifySegmentToLine2D type = node.partition
				.classifySegmentToLine2D(S);

		if (type == WB_ClassifySegmentToLine2D.SEGMENT_SPANNING_LINE) {
			final WB_ExplicitSegment2D[] split = WB_Intersection2D
					.splitSegment2D(S, node.partition);
			if (split != null) {
				getSegmentPosPartition(node, split[0], pos, neg, coSame, coDiff);
				getSegmentNegPartition(node, split[1], pos, neg, coSame, coDiff);
			}
		} else if (type == WB_ClassifySegmentToLine2D.SEGMENT_IN_FRONT_OF_LINE) {
			getSegmentPosPartition(node, S, pos, neg, coSame, coDiff);

		} else if (type == WB_ClassifySegmentToLine2D.SEGMENT_BEHIND_LINE) {
			getSegmentNegPartition(node, S, pos, neg, coSame, coDiff);

		} else if (type == WB_ClassifySegmentToLine2D.SEGMENT_ON_LINE) {
			partitionCoincidentSegments(node, S, pos, neg, coSame, coDiff);
		}

	}

	private void partitionCoincidentSegments(final WB_BSPNode2D node,
			final WB_ExplicitSegment2D S, final List<WB_ExplicitSegment2D> pos,
			final List<WB_ExplicitSegment2D> neg,
			final List<WB_ExplicitSegment2D> coSame,
			final List<WB_ExplicitSegment2D> coDiff) {

		FastList<WB_ExplicitSegment2D> partSegments = new FastList<WB_ExplicitSegment2D>();
		partSegments.add(S);
		WB_ExplicitSegment2D thisS, otherS;
		final WB_Line2D L = node.partition;
		for (int i = 0; i < node.segments.size(); i++) {
			final FastList<WB_ExplicitSegment2D> newpartSegments = new FastList<WB_ExplicitSegment2D>();
			otherS = node.segments.get(i);
			final double v0 = L.getT(otherS.getOrigin());
			final double v1 = L.getT(otherS.getEnd());

			for (int j = 0; j < partSegments.size(); j++) {
				thisS = partSegments.get(j);
				final double u0 = L.getT(thisS.getOrigin());
				final double u1 = L.getT(thisS.getEnd());
				double[] intersection;
				if (u0 < u1) {
					intersection = WB_Intersection2D.intervalIntersection(u0,
							u1, WB_Fast.min(v0, v1), WB_Fast.max(v0, v1));

					if (intersection[0] == 2) {
						final WB_Point2d pi = L.getPoint(intersection[1]);
						final WB_Point2d pj = L.getPoint(intersection[2]);
						if (u0 < intersection[1]) {
							newpartSegments.add(new WB_ExplicitSegment2D(thisS
									.getOrigin(), pi));

						}
						coSame.add(new WB_ExplicitSegment2D(pi, pj));
						if (u1 > intersection[2]) {
							newpartSegments.add(new WB_ExplicitSegment2D(pj,
									thisS.getEnd()));
						}
					} else {// this segment doesn't coincide with an edge
						newpartSegments.add(thisS);
					}

				} else {
					intersection = WB_Intersection2D.intervalIntersection(u1,
							u0, WB_Fast.min(v0, v1), WB_Fast.max(v0, v1));

					if (intersection[0] == 2) {
						final WB_Point2d pi = L.getPoint(intersection[1]);
						final WB_Point2d pj = L.getPoint(intersection[2]);
						if (u1 < intersection[1]) {
							newpartSegments.add(new WB_ExplicitSegment2D(pi,
									thisS.getEnd()));
						}
						coDiff.add(new WB_ExplicitSegment2D(pj, pi));
						if (u0 > intersection[2]) {
							newpartSegments.add(new WB_ExplicitSegment2D(thisS
									.getOrigin(), pj));
						}
					} else {
						newpartSegments.add(thisS);
					}
				}

			}
			partSegments = newpartSegments;
		}

		for (int i = 0; i < partSegments.size(); i++) {
			getSegmentPosPartition(node, partSegments.get(i), pos, neg, coSame,
					coDiff);
			getSegmentNegPartition(node, partSegments.get(i), pos, neg, coSame,
					coDiff);

		}

	}

	private void getSegmentPosPartition(final WB_BSPNode2D node,
			final WB_ExplicitSegment2D S, final List<WB_ExplicitSegment2D> pos,
			final List<WB_ExplicitSegment2D> neg,
			final List<WB_ExplicitSegment2D> coSame,
			final List<WB_ExplicitSegment2D> coDiff) {
		if (node.pos != null) {
			partitionSegment(node.pos, S, pos, neg, coSame, coDiff);
		} else {
			pos.add(S);
		}

	}

	private void getSegmentNegPartition(final WB_BSPNode2D node,
			final WB_ExplicitSegment2D S, final List<WB_ExplicitSegment2D> pos,
			final List<WB_ExplicitSegment2D> neg,
			final List<WB_ExplicitSegment2D> coSame,
			final List<WB_ExplicitSegment2D> coDiff) {
		if (node.neg != null) {
			partitionSegment(node.neg, S, pos, neg, coSame, coDiff);
		} else {
			neg.add(S);
		}
	}

	public ArrayList<WB_ExplicitSegment2D> toSegments() {
		final ArrayList<WB_ExplicitSegment2D> segments = new ArrayList<WB_ExplicitSegment2D>();
		addSegments(root, segments);
		return segments;

	}

	private void addSegments(final WB_BSPNode2D node,
			final ArrayList<WB_ExplicitSegment2D> segments) {
		segments.addAll(node.segments);
		if (node.pos != null) {
			addSegments(node.pos, segments);
		}
		if (node.neg != null) {
			addSegments(node.neg, segments);
		}

	}

	public WB_BSPTree2D negate() {
		final WB_BSPTree2D negTree = new WB_BSPTree2D();
		negTree.root = negate(root);
		return negTree;
	}

	private WB_BSPNode2D negate(final WB_BSPNode2D node) {
		final WB_BSPNode2D negNode = new WB_BSPNode2D();
		negNode.partition = new WB_Line2D(node.partition.getOrigin(),
				node.partition.getDirection().multAndCopy(-1));
		for (int i = 0; i < node.segments.size(); i++) {
			final WB_ExplicitSegment2D seg = node.segments.get(i);
			negNode.segments.add(new WB_ExplicitSegment2D(seg.getEnd(), seg
					.getOrigin()));
		}
		if (node.pos != null) {
			negNode.neg = negate(node.pos);
		}
		if (node.neg != null) {
			negNode.pos = negate(node.neg);
		}
		return negNode;
	}

	public void partitionPolygon(final WB_Polygon2D P,
			final List<WB_Polygon2D> pos, final List<WB_Polygon2D> neg) {

		partitionPolygon(root, P, pos, neg);

	}

	private void partitionPolygon(final WB_BSPNode2D node,
			final WB_Polygon2D P, final List<WB_Polygon2D> pos,
			final List<WB_Polygon2D> neg) {

		if (P.n > 2) {
			final WB_ClassifyPolygonToLine2D type = node.partition
					.classifyPolygonToLine2D(P);

			if (type == WB_ClassifyPolygonToLine2D.POLYGON_SPANNING_LINE) {
				final WB_Polygon2D[] split = WB_Intersection2D.splitPolygon2D(
						P, node.partition);
				if (split[0].n > 2) {
					getPolygonPosPartition(node, split[0], pos, neg);
				}
				if (split[1].n > 2) {
					getPolygonNegPartition(node, split[1], pos, neg);
				}
			} else if (type == WB_ClassifyPolygonToLine2D.POLYGON_IN_FRONT_OF_LINE) {
				getPolygonPosPartition(node, P, pos, neg);

			} else if (type == WB_ClassifyPolygonToLine2D.POLYGON_BEHIND_LINE) {
				getPolygonNegPartition(node, P, pos, neg);

			}
		}

	}

	private void getPolygonPosPartition(final WB_BSPNode2D node,
			final WB_Polygon2D P, final List<WB_Polygon2D> pos,
			final List<WB_Polygon2D> neg) {
		if (node.pos != null) {
			partitionPolygon(node.pos, P, pos, neg);
		} else {
			pos.add(P);
		}

	}

	private void getPolygonNegPartition(final WB_BSPNode2D node,
			final WB_Polygon2D P, final List<WB_Polygon2D> pos,
			final List<WB_Polygon2D> neg) {
		if (node.neg != null) {
			partitionPolygon(node.neg, P, pos, neg);
		} else {
			neg.add(P);
		}
	}

}
