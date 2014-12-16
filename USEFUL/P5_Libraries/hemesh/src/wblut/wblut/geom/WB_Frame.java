/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javolution.util.FastList;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_Frame {
	private FastList<WB_FrameStrut>	struts;
	private FastList<WB_FrameNode>	nodes;

	public WB_Frame() {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
	}

	public WB_Frame(final WB_Point3d[] points,
			final WB_IndexedSegment[] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public WB_Frame(final WB_Point3d[] points,
			final Collection<WB_IndexedSegment> connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public void add(final WB_Point3d[] points,
			final Collection<WB_IndexedSegment> connections) {

		if (struts == null) {
			struts = new FastList<WB_FrameStrut>();
		}
		if (nodes == null) {
			nodes = new FastList<WB_FrameNode>();
		}
		final int nodeoffset = nodes.size();

		for (final WB_Point3d point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1() + nodeoffset, connection.i2() + nodeoffset);
		}
	}

	public void add(final WB_Frame frame) {

		if (struts == null) {
			struts = new FastList<WB_FrameStrut>();
		}
		if (nodes == null) {
			nodes = new FastList<WB_FrameNode>();
		}
		final int nodeoffset = nodes.size();

		for (final WB_FrameNode node : frame.nodes) {
			addNode(node, node.getValue());
		}
		for (final WB_IndexedSegment connection : frame.getIndexedSegments()) {
			addStrut(connection.i1() + nodeoffset, connection.i2() + nodeoffset);
		}
	}

	public WB_Frame(final Collection<WB_Point3d> points,
			final Collection<WB_IndexedSegment> connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public WB_Frame(final WB_Point3d[] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final Collection<WB_Point3d> points,
			final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final double[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final double[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final float[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final float[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final int[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final int[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final WB_Point3d[] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
	}

	public WB_Frame(final Collection<WB_Point3d> points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point3d point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
	}

	public WB_Frame(final double[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final double[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
	}

	public WB_Frame(final float[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final float[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}

	}

	public WB_Frame(final int[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final int[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}

	}

	public int addNode(final double x, final double y, final double z,
			final double v) {
		final int n = nodes.size();
		nodes.add(new WB_FrameNode(new WB_Point3d(x, y, z), n, v));
		return n;
	}

	public int addNode(final WB_Point3d pos, final double v) {
		final int n = nodes.size();
		nodes.add(new WB_FrameNode(pos, n, v));
		return n;
	}

	public void removeNode(final WB_FrameNode node) {
		for (final WB_FrameStrut strut : node.getStruts()) {
			removeStrut(strut);
		}
		nodes.remove(node);
	}

	public int addNodes(final Collection<WB_Point3d> pos) {
		int n = nodes.size();
		final Iterator<WB_Point3d> pItr = pos.iterator();
		while (pItr.hasNext()) {
			nodes.add(new WB_FrameNode(pItr.next(), n, 1));
			n++;
		}
		return n;
	}

	public int addStrut(final int i, final int j) {
		if (i == j) {
			throw new IllegalArgumentException(
					"Strut can't connect a node to itself: " + i + " " + j
							+ ".");
		}
		final int nn = nodes.size();
		if ((i < 0) || (j < 0) || (i >= nn) || (j >= nn)) {
			throw new IllegalArgumentException(
					"Strut indices outside node range.");
		}
		final int n = struts.size();
		WB_FrameStrut strut;
		if (i <= j) {
			strut = new WB_FrameStrut(nodes.get(i), nodes.get(j), n);
		} else {
			strut = new WB_FrameStrut(nodes.get(j), nodes.get(i), n);
		}
		if (!nodes.get(i).addStrut(strut)) {
			System.out.println("WB_Frame : Strut " + i + "-" + j
					+ " already added.");
		} else if (!nodes.get(j).addStrut(strut)) {
			System.out.println("WB_Frame : Strut " + i + "-" + j
					+ " already added.");
		} else {

			struts.add(strut);
		}
		return n;
	}

	public void removeStrut(final WB_FrameStrut strut) {
		nodes.get(strut.getStartIndex()).removeStrut(strut);
		nodes.get(strut.getEndIndex()).removeStrut(strut);
		struts.remove(strut);
	}

	public ArrayList<WB_FrameStrut> getStruts() {
		final ArrayList<WB_FrameStrut> result = new ArrayList<WB_FrameStrut>();
		result.addAll(struts);
		return result;
	}

	public ArrayList<WB_ExplicitSegment> getSegments() {
		final ArrayList<WB_ExplicitSegment> result = new ArrayList<WB_ExplicitSegment>();
		for (final WB_FrameStrut strut : struts) {
			result.add(strut.toSegment());
		}
		return result;
	}

	public ArrayList<WB_IndexedSegment> getIndexedSegments() {
		final ArrayList<WB_Point3d> apoints = getPoints();
		WB_Point3d[] ipoints = new WB_Point3d[apoints.size()];
		ipoints = apoints.toArray(ipoints);
		final ArrayList<WB_IndexedSegment> result = new ArrayList<WB_IndexedSegment>();
		for (final WB_FrameStrut strut : struts) {
			result.add(new WB_IndexedSegment(strut.getStartIndex(), strut
					.getEndIndex(), ipoints));
		}
		return result;
	}

	public int getNumberOfStruts() {
		return struts.size();
	}

	public ArrayList<WB_FrameNode> getNodes() {
		final ArrayList<WB_FrameNode> result = new ArrayList<WB_FrameNode>();
		result.addAll(nodes);
		return result;
	}

	public ArrayList<WB_Point3d> getPoints() {
		final ArrayList<WB_Point3d> result = new ArrayList<WB_Point3d>();
		result.addAll(nodes);
		return result;
	}

	public WB_Point3d[] getPointsAsArray() {
		final ArrayList<WB_Point3d> result = new ArrayList<WB_Point3d>();
		result.addAll(nodes);
		final ArrayList<WB_Point3d> apoints = getPoints();
		final WB_Point3d[] ipoints = new WB_Point3d[apoints.size()];
		return apoints.toArray(ipoints);

	}

	public int getNumberOfNodes() {
		return nodes.size();
	}

	public WB_FrameNode getNode(final int i) {
		if ((i < 0) || (i >= nodes.size())) {
			throw new IllegalArgumentException("Index outside of node range.");
		}
		return nodes.get(i);

	}

	public WB_FrameStrut getStrut(final int i) {
		if ((i < 0) || (i >= struts.size())) {
			throw new IllegalArgumentException("Index outside of strut range.");
		}
		return struts.get(i);

	}

	public double getDistanceToFrame(final WB_Point3d p) {
		double d = Double.POSITIVE_INFINITY;
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_ExplicitSegment S = new WB_ExplicitSegment(strut.start(),
					strut.end());
			d = Math.min(d, WB_Distance.distance(p, S));
		}
		return d;
	}

	public int getClosestNodeOnFrame(final WB_Point3d p) {
		double mind = Double.POSITIVE_INFINITY;
		int q = -1;
		for (int i = 0; i < nodes.size(); i++) {

			final double d = WB_Distance.sqDistance(p, nodes.get(i));
			if (d < mind) {
				mind = d;
				q = i;
			}

		}
		return q;
	}

	public WB_Point3d getClosestPointOnFrame(final WB_Point3d p) {
		double mind = Double.POSITIVE_INFINITY;
		WB_Point3d q = new WB_Point3d(p);
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_ExplicitSegment S = new WB_ExplicitSegment(strut.start(),
					strut.end());

			final double d = WB_Distance.distance(p, S);
			if (d < mind) {
				mind = d;
				q = WB_Intersection.closestPoint(S, p);
			}

		}
		return q;
	}

	public double getDistanceToFrame(final double x, final double y,
			final double z) {
		double d = Double.POSITIVE_INFINITY;
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_ExplicitSegment S = new WB_ExplicitSegment(strut.start(),
					strut.end());
			d = Math.min(d, WB_Distance.distance(new WB_Point3d(x, y, z), S));
		}
		return d;
	}

	public WB_Point3d getClosestPointOnFrame(final double x, final double y,
			final double z) {
		double mind = Double.POSITIVE_INFINITY;
		WB_Point3d q = new WB_Point3d(x, y, z);
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_ExplicitSegment S = new WB_ExplicitSegment(strut.start(),
					strut.end());

			final double d = WB_Distance.distance(new WB_Point3d(x, y, z), S);
			if (d < mind) {
				mind = d;
				q = WB_Intersection.closestPoint(S, new WB_Point3d(x, y, z));
			}

		}
		return q;
	}

	public void smoothBiNodes() {
		final WB_Point3d[] newPos = new WB_Point3d[nodes.size()];
		int id = 0;
		for (final WB_FrameNode node : nodes) {
			if (node.getOrder() == 2) {
				newPos[id] = node.getNeighbor(0)
						.addAndCopy(node.getNeighbor(1));

				newPos[id].mult(0.5);
				newPos[id].add(node);
				newPos[id].mult(0.5);
			}
			id++;
		}
		id = 0;
		for (final WB_FrameNode node : nodes) {
			if (node.getOrder() == 2) {
				node.set(newPos[id]);
			}
			id++;
		}
	}

	public WB_Frame refine(final double threshold) {

		final FastList<WB_Point3d> npoints = new FastList<WB_Point3d>();
		for (final WB_FrameNode node : nodes) {
			npoints.add(node);
		}
		for (final WB_FrameStrut strut : struts) {
			if (strut.getLength() > threshold) {
				final WB_Point3d start = strut.start();
				final WB_Point3d end = strut.end();
				final WB_Point3d mid = WB_Point3d.interpolate(start, end, 0.5);
				npoints.add(mid);
			}
		}
		final int n = getNumberOfNodes();
		int id = 0;
		final WB_Frame result = new WB_Frame(npoints);
		for (final WB_FrameStrut strut : struts) {
			if (strut.getLength() > threshold) {
				final int start = strut.getStartIndex();
				final int end = strut.getEndIndex();
				result.addStrut(start, n + id);
				result.addStrut(n + id, end);
				id++;
			} else {
				final int start = strut.getStartIndex();
				final int end = strut.getEndIndex();
				result.addStrut(start, end);
			}
		}

		return result;

	}

}
