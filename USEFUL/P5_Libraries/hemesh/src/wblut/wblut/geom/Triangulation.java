/*
 * Copyright (c) 2008-2009 Mark L. Howison All rights reserved. Redistribution
 * and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: (1) Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. (2) Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution. (3) The name of the copyright holder may not be used
 * to endorse or promote products derived from this software without specific
 * prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package wblut.geom;

import java.awt.geom.GeneralPath;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import wblut.WB_Epsilon;


import javolution.util.FastList;

/**
 * Represents a 2D triangulation using points (vertices) and halfedges.
 * Triangulation is performed by an interactive constrained Delaunay
 * algorithm. Point insertion uses Lawson's algorithm. Constraint insertion
 * and removal, as well as vertex removal and relocation are supported.
 *
 * For full details, see the "Interactive Constrained Delaunay Triangulation"
 * section of:
 *
 * Howison, M. CAD Tools for Creating Space-filling 3D Escher Tiles.
 * Master’s thesis, U.C. Berkeley, Berkeley, CA, May 2009.
 * (also Tech Report EECS-2009-56,
 *     http://www.eecs.berkeley.edu/Pubs/TechRpts/2009/EECS-2009-56.html)
 *
 * Or contact the author: mark.howison <at> gmail <dot> com
 *
 * @author Mark Howison
 */
public class Triangulation {

	/* typical size of polygons, for initializing FastLists */
	private final static int			TYPICAL_POLYGON_SIZE	= 16;

	/* errors */
	protected final static String		E_EXHAUSTED				= "Exhausted halfedges or points!";
	protected final static String		E_MISSING				= "Missing halfedge or point!";
	protected final static String		E_IDENTICAL				= "Identical halfedges or points!";
	protected final static String		E_COINCIDENT			= "Coincident points!";
	protected final static String		E_TYPE					= "Incorrect type!";
	protected final static String		E_POLYGON				= "Illegal polygonal region!";
	protected final static String		E_HALFEDGE				= "Mismatched halfedge!";
	protected final static int			NULL_VALUE				= -100000;

	/* stdout/debug/test flags */
	public final static boolean			MESSAGES				= false;

	/* mesh data */
	final protected List<Tri_Point>		points					= Collections
																		.synchronizedList(new LinkedList<Tri_Point>());
	final protected List<Tri_HalfEdge>	halfEdges				= Collections
																		.synchronizedList(new LinkedList<Tri_HalfEdge>());
	protected int						nBoundary;

	/* queues */
	protected LinkedList<Tri_HalfEdge>	delaunayQueue			= new LinkedList<Tri_HalfEdge>();
	protected LinkedList<Tri_Point>		removedConstraints		= new LinkedList<Tri_Point>();
	protected LinkedList<Tri_Point>		deleteQueue				= new LinkedList<Tri_Point>();
	private Tri_Point					removeConstraintPeg		= null;

	/******************************************************************************/
	/*
	 * Constructors etc.
	 * /*******************************************************
	 * **********************
	 */

	public Triangulation() {

	}

	/******************************************************************************/
	/*
	 * Accessors & Mutators
	 * /****************************************************
	 * *************************
	 */

	public int size() {
		return points.size();
	}

	public int boundarySize() {
		return nBoundary;
	}

	public void clear() {
		points.clear();
		halfEdges.clear();
		delaunayQueue.clear();
	}

	public void clearFlags(final int flag) {
		for (final Tri_HalfEdge he : halfEdges) {
			he.unflag(flag);
		}
	}

	public boolean contains(final Tri_Point p) {
		return points.contains(p);
	}

	public int indexOf(final Tri_Point p) {
		return points.indexOf(p);
	}

	public Tri_Point getPoint(final int i) {
		return points.get(i);
	}

	public WB_Point2d[] getFaceCoordinates() {
		final FastList<WB_Point2d> facePoints = new FastList<WB_Point2d>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;
			// the face is oriented CCW
			facePoints.add(new WB_Point2d(he0.origin));
			facePoints.add(new WB_Point2d(he1.origin));
			facePoints.add(new WB_Point2d(he2.origin));
			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return facePoints.toArray(new WB_Point2d[0]);
	}

	public WB_ExplicitTriangle2D[] getExplicitTriangles() {
		final FastList<WB_ExplicitTriangle2D> triangles = new FastList<WB_ExplicitTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;
			triangles.add(new WB_ExplicitTriangle2D(he0.origin, he1.origin,
					he2.origin));
			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles.toArray(new WB_ExplicitTriangle2D[0]);
	}

	public List<WB_ExplicitTriangle2D> getExplicitTrianglesAsList() {
		final List<WB_ExplicitTriangle2D> triangles = new FastList<WB_ExplicitTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;
			triangles.add(new WB_ExplicitTriangle2D(he0.origin, he1.origin,
					he2.origin));
			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles;
	}

	public WB_IndexedTriangle2D[] getIndexedTriangles(final WB_Point2d[] points) {
		final FastList<WB_IndexedTriangle2D> triangles = new FastList<WB_IndexedTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;
			triangles.add(new WB_IndexedTriangle2D(indexOf(he0.origin),
					indexOf(he1.origin), indexOf(he2.origin), points));
			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles.toArray(new WB_IndexedTriangle2D[0]);
	}

	public WB_IndexedTriangle2D[] getIndexedTrianglesInsideConstraints(
			final WB_Point2d[] points) {
		final WB_AABB2D AABB = new WB_AABB2D(this.points);
		final double range = WB_Distance2D.distance(AABB.min, AABB.max);
		final FastList<WB_IndexedTriangle2D> triangles = new FastList<WB_IndexedTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;
			if (insideClosedConstraints(he0.origin, he1.origin, he2.origin,
					range)) {
				triangles.add(new WB_IndexedTriangle2D(indexOf(he0.origin),
						indexOf(he1.origin), indexOf(he2.origin), points));
			}
			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles.toArray(new WB_IndexedTriangle2D[0]);
	}

	public List<WB_IndexedTriangle2D> getIndexedTrianglesAsList(
			final WB_Point2d[] points) {
		final List<WB_IndexedTriangle2D> triangles = new FastList<WB_IndexedTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;

			triangles.add(new WB_IndexedTriangle2D(indexOf(he0.origin),

			indexOf(he1.origin), indexOf(he2.origin), points));

			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles;
	}

	public List<WB_IndexedTriangle2D> getIndexedTrianglesAsList(
			final WB_Point2d[] points, final boolean reverse) {
		final List<WB_IndexedTriangle2D> triangles = new FastList<WB_IndexedTriangle2D>();
		// reset 'used' flags
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		final int n = points.length;
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final Tri_HalfEdge he1 = he0.next;
			final Tri_HalfEdge he2 = he1.next;

			triangles
					.add(new WB_IndexedTriangle2D(reverse ? n - 1
							- indexOf(he0.origin) : indexOf(he0.origin),

							reverse ? n - 1 - indexOf(he1.origin)
									: indexOf(he1.origin),
							reverse ? n - 1 - indexOf(he2.origin)
									: indexOf(he2.origin), points));

			// mark these half edges as used
			he0.flag(Tri_HalfEdge.FLAG_READ);
			he1.flag(Tri_HalfEdge.FLAG_READ);
			he2.flag(Tri_HalfEdge.FLAG_READ);
		}
		return triangles;
	}

	public WB_ExplicitSegment2D[] getExplicitEdges() {
		final FastList<WB_ExplicitSegment2D> edges = new FastList<WB_ExplicitSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final WB_ExplicitSegment2D e = new WB_ExplicitSegment2D(
					new Tri_Point(he.origin), new Tri_Point(he.next.origin));
			edges.add(e);
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_ExplicitSegment2D[0]);
	}

	public WB_ExplicitSegment2D[] getExplicitConstrainedEdges() {
		final FastList<WB_ExplicitSegment2D> edges = new FastList<WB_ExplicitSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if (he.isType(Tri_HalfEdge.CONSTRAINT)) {
				final WB_ExplicitSegment2D e = new WB_ExplicitSegment2D(
						new Tri_Point(he.origin), new Tri_Point(he.next.origin));
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_ExplicitSegment2D[0]);
	}

	public WB_ExplicitSegment2D[] getExplicitBoundaryEdges() {
		final FastList<WB_ExplicitSegment2D> edges = new FastList<WB_ExplicitSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if (he.isType(Tri_HalfEdge.BOUNDARY)) {
				final WB_ExplicitSegment2D e = new WB_ExplicitSegment2D(
						new Tri_Point(he.origin), new Tri_Point(he.next.origin));
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_ExplicitSegment2D[0]);
	}

	public WB_ExplicitSegment2D[] getExplicitInteriorEdges() {
		final FastList<WB_ExplicitSegment2D> edges = new FastList<WB_ExplicitSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if ((!he.isType(Tri_HalfEdge.BOUNDARY))
					&& (!he.isType(Tri_HalfEdge.CONSTRAINT))) {
				final WB_ExplicitSegment2D e = new WB_ExplicitSegment2D(
						new Tri_Point(he.origin), new Tri_Point(he.next.origin));
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_ExplicitSegment2D[0]);
	}

	public WB_IndexedSegment2D[] getIndexedEdges(final WB_Point2d[] points) {
		final FastList<WB_IndexedSegment2D> edges = new FastList<WB_IndexedSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			final WB_IndexedSegment2D e = new WB_IndexedSegment2D(
					indexOf(he.origin), indexOf(he.next.origin), points);
			edges.add(e);
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_IndexedSegment2D[0]);
	}

	public WB_IndexedSegment2D[] getIndexedConstrainedEdges(
			final WB_Point2d[] points) {
		final FastList<WB_IndexedSegment2D> edges = new FastList<WB_IndexedSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if (he.getType() == Tri_HalfEdge.CONSTRAINT) {
				final WB_IndexedSegment2D e = new WB_IndexedSegment2D(
						indexOf(he.origin), indexOf(he.next.origin), points);
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_IndexedSegment2D[0]);
	}

	public WB_IndexedSegment2D[] getIndexedBoundaryEdges(
			final WB_Point2d[] points) {
		final FastList<WB_IndexedSegment2D> edges = new FastList<WB_IndexedSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if (he.getType() == Tri_HalfEdge.BOUNDARY) {
				final WB_IndexedSegment2D e = new WB_IndexedSegment2D(
						indexOf(he.origin), indexOf(he.next.origin), points);
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_IndexedSegment2D[0]);
	}

	public WB_IndexedSegment2D[] getIndexedInternalEdges(
			final WB_Point2d[] points) {
		final FastList<WB_IndexedSegment2D> edges = new FastList<WB_IndexedSegment2D>();
		clearFlags(Tri_HalfEdge.FLAG_READ);
		// find the faces
		for (final Tri_HalfEdge he : halfEdges) {
			if (he.isFlagged(Tri_HalfEdge.FLAG_READ)) {
				continue;
			}
			if ((he.getType() != Tri_HalfEdge.BOUNDARY)
					&& (he.getType() != Tri_HalfEdge.CONSTRAINT)) {
				final WB_IndexedSegment2D e = new WB_IndexedSegment2D(
						indexOf(he.origin), indexOf(he.next.origin), points);
				edges.add(e);
			}
			he.flag(Tri_HalfEdge.FLAG_READ);
		}
		return edges.toArray(new WB_IndexedSegment2D[0]);
	}

	public WB_Point2d[] getPoints() {
		final WB_Point2d[] lpoints = new WB_Point2d[points.size()];
		for (int i = 0; i < points.size(); i++) {
			lpoints[i] = points.get(i);
		}
		return lpoints;

	}

	public void translate(final double x, final double y) {
		for (final Tri_Point p : points) {
			p.x += x;
			p.y += y;
		}
	}

	/******************************************************************************/
	/*
	 * Insertion methods
	 * /*******************************************************
	 * **********************
	 */

	public static void linkBoundary(final Tri_BPoint[] pts) {
		final int s = pts.length;
		for (int i = 0; i < s; i++) {
			pts[i].next = pts[(i + 1) % s];
			pts[i].prev = pts[(i - 1 + s) % s];
		}
	}

	// expects a list of points in *clockwise* order for specifiying
	// an initial boundary for the mesh
	public void startWithBoundary(final WB_Point2d[] pts) {
		final int s = pts.length;
		assert s >= 3 : error("Initialization requires at least 3 points!");
		final FastList<Tri_HalfEdge> polygon = new FastList<Tri_HalfEdge>(s);
		clear();
		for (final WB_Point2d point : pts) {
			final Tri_Point p = new Tri_Point(point);
			points.add(p);
			p.setType(Tri_Point.BOUNDARY);
			p.he = new Tri_HalfEdge(p, Tri_HalfEdge.BOUNDARY);
			halfEdges.add(p.he);
		}
		for (int i = 0; i < s; i++) {
			halfEdges.get(i).next = halfEdges.get((s + i - 1) % s);
		}
		nBoundary = s;
		for (int i = 0; i < s; i++) {
			polygon.add(halfEdges.get(s - 1 - i));
		}
		fillGeneralPolygon(polygon);
		delaunayQueue.addAll(polygon);
		updateDelaunay();
	}

	public void startWithBoundary(final WB_Point2d[] pts, final boolean reverse) {
		final int s = pts.length;
		assert s >= 3 : error("Initialization requires at least 3 points!");
		final FastList<Tri_HalfEdge> polygon = new FastList<Tri_HalfEdge>(s);
		clear();
		for (final WB_Point2d point : pts) {
			final Tri_Point p = new Tri_Point(point);
			points.add(p);
			p.setType(Tri_Point.BOUNDARY);
			p.he = new Tri_HalfEdge(p, Tri_HalfEdge.BOUNDARY);
			halfEdges.add(p.he);
		}
		if (reverse) {
			Collections.reverse(points);
		}
		if (reverse) {
			Collections.reverse(halfEdges);
		}
		for (int i = 0; i < s; i++) {
			halfEdges.get(i).next = halfEdges.get((s + i - 1) % s);
		}
		nBoundary = s;
		for (int i = 0; i < s; i++) {
			polygon.add(halfEdges.get(s - 1 - i));
		}
		fillGeneralPolygon(polygon);
		delaunayQueue.addAll(polygon);
		updateDelaunay();
	}

	private Tri_HalfEdge addHalfEdge(final Tri_Point origin,
			final Tri_Point destination) {
		final Tri_HalfEdge he1 = new Tri_HalfEdge(origin);
		final Tri_HalfEdge he2 = new Tri_HalfEdge(destination);
		he1.sibling = he2;
		he2.sibling = he1;
		halfEdges.add(he1);
		halfEdges.add(he2);
		if (MESSAGES) {
			message("Added halfedges %d and %d.", halfEdges.indexOf(he1),
					halfEdges.indexOf(he2));
		}
		return he1;
	}

	/*
	 * Adds an edge connecting the origins of he1 and he2.
	 * @return the halfedge from he1.origin to he2.origin
	 */
	private Tri_HalfEdge addEdge(final Tri_HalfEdge he1,
			final Tri_HalfEdge he2, final Tri_HalfEdge he1prev,
			final Tri_HalfEdge he2prev) {
		assert he1prev.next == he1 : error(E_HALFEDGE, he1, he1prev);
		assert he2prev.next == he2 : error(E_HALFEDGE, he2, he2prev);
		assert he1 != he2 : error(E_COINCIDENT);
		assert he1.origin != he2.origin : error(E_COINCIDENT);

		final Tri_HalfEdge heAdd = addHalfEdge(he1.origin, he2.origin);
		delaunayQueue.add(heAdd);
		heAdd.next = he2;
		he1prev.next = heAdd;
		heAdd.sibling.next = he1;
		he2prev.next = heAdd.sibling;
		return heAdd;
	}

	public Tri_Point addBoundaryPoint(final Tri_Point p, final Tri_HalfEdge he0) {
		assert halfEdges.contains(he0) : error(E_MISSING);
		assert between(he0.origin, he0.next.origin, p) : error("Adding boundary point that doesn't lie on boundary!");

		Tri_HalfEdge he1, he2;

		/* check for coincidence with the endpoints of he0 */
		if (coincident(p, he0.origin)) {
			if (MESSAGES) {
				message("Boundary point is within epsilon of %d.",
						points.indexOf(he0.origin));
			}
			return he0.origin;
		}
		if (coincident(p, he0.next.origin)) {
			if (MESSAGES) {
				message("Boundary point is within epsilon of %d.",
						points.indexOf(he0.next.origin));
			}
			return he0.next.origin;
		}
		points.add(p);
		if (MESSAGES) {
			message("Adding boundary point %d.", points.indexOf(p));
		}
		he2 = he0.next;
		/* split the existing boundary */
		he1 = new Tri_HalfEdge(p, Tri_HalfEdge.BOUNDARY);
		halfEdges.add(he1);
		/* link halfedges */
		p.he = he1;
		he0.next = he1;
		he1.next = he2;
		fillQuadrilateral(he0);
		updateDelaunay();
		nBoundary++;
		return p;
	}

	public Tri_Point addInteriorPoint(final WB_Point2d point) {
		double dist, min;
		Tri_Point pNearest = null;
		Tri_FaceWalk walk;
		final Tri_Point p = new Tri_Point(point);
		/* find the closest point to p */
		min = Double.MAX_VALUE;
		for (final Tri_Point pTest : points) {
			dist = WB_Distance2D.sqDistance(p, pTest);
			/* abort if the point is within epsilon of an existing point */
			if (dist < WB_Epsilon.SQEPSILON) {
				if (MESSAGES) {
					message("Tri_Point is within epsilon of %d.",
							points.indexOf(pTest));
				}
				return pTest;
			} else if (dist < min) {
				min = dist;
				pNearest = pTest;
			}
		}
		if (MESSAGES) {
			message("Closest point is ", points.indexOf(pNearest));
		}

		/* find face containing p, starting at pNearest */
		walk = findFace(pNearest.he, p);

		if (MESSAGES) {
			message("Tri_Point is within face with halfedge %d.",
					halfEdges.indexOf(walk.he));
		}
		points.add(p);
		if (walk.status == Tri_FaceWalk.COINCIDENT) {
			splitEdge(p, walk.he);
		} else {
			splitFace(p, walk.he);
		}
		updateDelaunay();
		return p;
	}

	public boolean addConstraint(final int start, final int end) {
		final Tri_Point pStart = getPoint(start);
		final Tri_Point pEnd = getPoint(end);
		return addConstraint(pStart, pEnd);
	}

	public boolean addConstraint(Tri_Point pStart, final Tri_Point pEnd) {
		assert points.contains(pStart) : error(E_MISSING);
		assert points.contains(pEnd) : error(E_MISSING);
		assert pStart != pEnd : error("Identical points!");
		assert !coincident(pStart, pEnd) : error("Coincident points!");

		int i;
		Tri_Point pSearch0, pSearch1;
		Tri_HalfEdge heSearch, heStart, heStartPrev;
		Tri_FaceWalk walk;

		if (MESSAGES) {
			message("Constraining %d -> %d.", points.indexOf(pStart),
					points.indexOf(pEnd));
		}
		/* find the halfedge at pStart that lies on or below the constraint */
		walk = startFaceWalk(pStart, pEnd);
		/* check for trivial condition where the edge already exists */
		if (walk.status == Tri_FaceWalk.COINCIDENT) {
			return constrainEdge(walk.he);
		}
		/* clear edges that intersect the constraint */
		heStart = walk.he;
		heStartPrev = findPrevious(heStart);
		heSearch = heStart.next;
		for (i = 0; i <= halfEdges.size(); i++) {
			pSearch0 = heSearch.origin;
			pSearch1 = heSearch.next.origin;
			/* check for termination */
			if (pSearch1 == pEnd) {
				if (MESSAGES) {
					message("Found constraint end at halfedge %d.",
							halfEdges.indexOf(heSearch));
				}
				break;
			}
			assert !coincident(pSearch1, pStart) : error(E_COINCIDENT,
					pSearch1, pStart);
			assert !coincident(pSearch1, pEnd) : error(E_COINCIDENT, pSearch1,
					pEnd);
			/* check for collinearity */
			// if (between(pStart,pEnd,pSearch1)) {
			/* split the constraint in two with pSearch1 as the midpoint */
			/*
			 * addConstraintEdge(heStart,heSearch.next,heStartPrev,heSearch);
			 * return addConstraint(pSearch1,pEnd); }
			 */
			/* check for intersection */
			if (intersect(pStart, pEnd, pSearch0, pSearch1)) {
				assert !heSearch.isType(Tri_HalfEdge.BOUNDARY) : error("Constraint crosses boundary edge!");
				if (heSearch.isType(Tri_HalfEdge.AUXILARY)) {
					removeEdge(heSearch);
					heSearch = heSearch.sibling;
				} else if (heSearch.isType(Tri_HalfEdge.CONSTRAINT)) {
					if (MESSAGES) {
						message("Constraint-constraint intersection found.");
					}
					final WB_Point2d p = intersection(pStart, pEnd, pSearch0,
							pSearch1);
					splitConstraint(heSearch, p);
					addConstraintEdge(heStart, heSearch.next, heStartPrev,
							heSearch);
					/* reset the starting point */
					heSearch = heSearch.sibling;
					heStart = heSearch;
					heStartPrev = findPrevious(heStart);
					pStart = heSearch.origin;
				}
			}
			heSearch = heSearch.next;
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED, pStart, pEnd);
		addConstraintEdge(heStart, heSearch.next, heStartPrev, heSearch);
		updateDelaunay();
		return true;
	}

	private void addConstraintEdge(final Tri_HalfEdge he1,
			final Tri_HalfEdge he2, final Tri_HalfEdge he1prev,
			final Tri_HalfEdge he2prev) {
		assert he1prev.next == he1 : error(E_HALFEDGE);
		assert he2prev.next == he2 : error(E_HALFEDGE);

		final Tri_HalfEdge heAdd = addEdge(he1, he2, he1prev, he2prev);
		constrainEdge(heAdd);
		fillEdgeVisiblePolygon(heAdd);
		fillEdgeVisiblePolygon(heAdd.sibling);
	}

	/******************************************************************************/
	/*
	 * Polygon filling methods
	 * /*************************************************
	 * ****************************
	 */

	protected void fillQuadrilateral(final Tri_HalfEdge he1) {
		Tri_HalfEdge he2, he3, he4;

		he2 = he1.next;
		he3 = he2.next;
		he4 = he3.next;
		assert he4.next == he1;

		if (WB_Predicates2D.orient2d(he1.origin, he3.origin, he2.origin)
				* WB_Predicates2D.orient2d(he1.origin, he3.origin, he4.origin) < 0) {
			addEdge(he1, he3, he4, he2);
		} else {
			addEdge(he2, he4, he1, he3);
		}
	}

	protected FastList<Tri_HalfEdge> constructPolygon(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING);

		int i;
		Tri_HalfEdge heSearch;
		final FastList<Tri_HalfEdge> polygon = new FastList<Tri_HalfEdge>(
				TYPICAL_POLYGON_SIZE);

		polygon.add(he);
		heSearch = he.next;
		for (i = 0; i <= halfEdges.size(); i++) {
			if (heSearch == he) {
				break;
			}
			polygon.add(heSearch);
			heSearch = heSearch.next;
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED, he, he.origin);

		return polygon;
	}

	protected void fillGeneralPolygon(final Tri_HalfEdge he) {
		fillGeneralPolygon(constructPolygon(he));
	}

	protected void fillGeneralPolygon(final FastList<Tri_HalfEdge> polygon) {
		fillGeneralPolygonRecurse(polygon);
		delaunayQueue.addAll(polygon);
	}

	private void fillGeneralPolygonRecurse(final FastList<Tri_HalfEdge> polygon) {
		assert polygon.size() >= 3 : error("Illegal size!");

		int n, s;
		Tri_Point p0, p1, p2;
		Tri_HalfEdge heTest0, heTest1, heAdd;

		/*
		 * Assumes a Jordan (simple) polygon with n>3 sides! (i.e. no sides
		 * intersect)
		 */
		if (MESSAGES) {
			message("Filling polygon with %d sides.", polygon.size());
		}
		s = polygon.size();
		if (s > 3) {
			/*
			 * A Jordan polygon always has two non-overlapping ears. We iterate
			 * over all possible ear edges, i.e. those between vertices i and
			 * i+2 in the polygon.
			 */
			p0 = p1 = p2 = null;
			heTest0 = null;
			n = 0;
			edgeWalk: for (int i = 0; i < s; i++) {
				n = i;
				heTest0 = polygon.get(i);
				p0 = heTest0.origin;
				p1 = heTest0.next.origin;
				p2 = polygon.get((i + 2) % s).origin;
				// check that the ear edge p0->p2 lies strictly
				// inside the polygon, i.e. to the left of p0->p1
				if (WB_Predicates2D.orient2d(p0, p1, p2) > 0
						&& (!between(p0, p2, p1))) {
					// check for intersections or points that lie too
					// close to the ear edge
					heTest1 = heTest0.next.next;
					for (int j = 0; j < (s - 3); j++) {
						if (intersectProper(p0, p2, heTest1.origin,
								heTest1.next.origin)) {
							continue edgeWalk;
						}
						heTest1 = heTest1.next;
					}
					break;
				}
			}
			heAdd = addHalfEdge(p0, p2);
			delaunayQueue.add(heAdd);
			// link halfedges in the ear
			heAdd.sibling.next = heTest0;
			polygon.get((n + 1) % s).next = heAdd.sibling;
			// link halfedges in the remaining polygon of size s-1
			heAdd.next = polygon.get((n + 2) % s);
			polygon.get((n + s - 1) % s).next = heAdd;
			if (s > 4) {
				final FastList<Tri_HalfEdge> polygon0 = new FastList<Tri_HalfEdge>();
				for (int j = 0; j < (s - 1); j++) {
					polygon0.add(heAdd);
					heAdd = heAdd.next;
				}
				fillGeneralPolygonRecurse(polygon0);
			}
		}
	}

	protected void fillEdgeVisiblePolygon(final Tri_HalfEdge he) {
		final FastList<Tri_HalfEdge> polygon = constructPolygon(he);
		fillEdgeVisiblePolygonRecurse(polygon);
		delaunayQueue.addAll(polygon);
	}

	private void fillEdgeVisiblePolygonRecurse(
			final FastList<Tri_HalfEdge> polygon) {
		assert polygon.size() >= 3 : error("Illegal size!");

		int i, c, s;
		Tri_Point pa, pb, pc;
		Tri_HalfEdge heAdd;

		if (MESSAGES) {
			message("Filling polygon with %d sides.", polygon.size());
		}
		s = polygon.size();
		if (s > 3) {
			pa = polygon.get(0).origin;
			pb = polygon.get(1).origin;
			pc = polygon.get(2).origin;
			c = 2;
			for (i = 3; i < s; i++) {
				final Tri_Point p = polygon.get(i).origin;
				if (WB_Predicates2D.incircle2d(pa, pb, pc, p) > 0) {
					pc = p;
					c = i;
				}
			}
			/* add edge pa -> pc */
			if (c < (s - 1)) {
				heAdd = addEdge(polygon.get(0), polygon.get(c),
						polygon.get(s - 1), polygon.get(c - 1));
				fillEdgeVisiblePolygonRecurse(constructPolygon(heAdd));
			}
			/* add edge pb -> pc */
			if (c > 2) {
				heAdd = addEdge(polygon.get(1), polygon.get(c - 1).next,
						polygon.get(0), polygon.get(c - 1));
				fillEdgeVisiblePolygonRecurse(constructPolygon(heAdd.sibling));
			}
		}
	}

	/******************************************************************************/
	/*
	 * Splitting methods
	 * /*******************************************************
	 * **********************
	 */

	/*
	 * Splits the two faces sharing edge he into four faces by inserting point
	 * p.
	 */
	private void splitEdge(final Tri_Point p, final Tri_HalfEdge he) {
		Tri_HalfEdge he1, he2, he3;
		Tri_HalfEdge heAdd1, heAdd2, heAdd3;

		if (MESSAGES) {
			message("Splitting edge %d.", halfEdges.indexOf(he));
		}
		assert !he.isType(Tri_HalfEdge.BOUNDARY) : error("Attempting to split boundary edge!");
		// he1 = he.next.next;
		he1 = findPrevious(he);
		he2 = he.sibling.next;
		// he3 = he2.next;
		he3 = findPrevious(he.sibling);
		// split the halfedge
		he.origin = p;
		p.he = he;
		// add halfedges
		heAdd1 = addHalfEdge(p, he1.origin);
		heAdd2 = addHalfEdge(p, he2.origin);
		heAdd3 = addHalfEdge(p, he3.origin);
		// link halfedges
		heAdd1.next = he1;
		heAdd2.next = he2;
		heAdd3.next = he3;
		he.next.next = heAdd1.sibling;
		he1.next = heAdd2.sibling;
		he2.next = heAdd3.sibling;
		heAdd1.sibling.next = he;
		heAdd2.sibling.next = heAdd1;
		heAdd3.sibling.next = heAdd2;
		he.sibling.next = heAdd3;
		// update the point->halfedge pointers
		updateHalfEdge(he2);
		// add halfedges to delaunay test
		delaunayQueue.add(he);
		delaunayQueue.add(he1);
		delaunayQueue.add(he2);
		delaunayQueue.add(he3);
	}

	/*
	 * Insert point p into the face with halfedge he1, splitting it into three
	 * faces.
	 */
	private void splitFace(final Tri_Point p, final Tri_HalfEdge he1) {
		assert halfEdges.contains(he1) : error(E_MISSING);

		Tri_HalfEdge he2, he3;
		Tri_HalfEdge heAdd1, heAdd2, heAdd3;

		if (MESSAGES) {
			message("Adding interior point inside face.");
		}
		he2 = he1.next;
		he3 = he2.next;
		/* add new halfedges */
		heAdd1 = addHalfEdge(p, he1.origin);
		heAdd2 = addHalfEdge(p, he2.origin);
		heAdd3 = addHalfEdge(p, he3.origin);
		/* link half edges */
		p.he = heAdd1;
		heAdd1.next = he1;
		heAdd2.next = he2;
		heAdd3.next = he3;
		he1.next = heAdd2.sibling;
		he2.next = heAdd3.sibling;
		he3.next = heAdd1.sibling;
		heAdd1.sibling.next = heAdd3;
		heAdd3.sibling.next = heAdd2;
		heAdd2.sibling.next = heAdd1;
		/* add halfedges to delaunay test */
		delaunayQueue.add(heAdd1);
		delaunayQueue.add(heAdd2);
		delaunayQueue.add(heAdd3);
		delaunayQueue.add(he1);
		delaunayQueue.add(he2);
		delaunayQueue.add(he3);
	}

	private void splitConstraint(final Tri_HalfEdge he, final WB_Point2d p) {
		int i;
		Tri_Point p0;
		Tri_HalfEdge he0, heTest;

		if (MESSAGES) {
			message("Splitting constraint edge %d.", halfEdges.indexOf(he));
		}
		assert !he.isType(Tri_HalfEdge.BOUNDARY) : error("Attempting to split a boundary edge!");
		// add point
		p0 = new Tri_Point(p);
		points.add(p0);
		if (MESSAGES) {
			message("Adding constraint intersection point %d.",
					points.indexOf(p0));
		}
		// add halfedge
		he0 = addHalfEdge(p0, he.sibling.origin);
		he0.constrain();
		he0.sibling.constrain();
		he.sibling.origin = p0;
		// update point->halfedge pointers
		p0.he = he0;
		updateHalfEdge(he0.sibling);
		// link halfedges
		he0.next = he.next;
		he.next = he0;
		he0.sibling.next = he.sibling;
		// find halfedge pointing to he.sibling
		heTest = he.sibling;
		for (i = 0; i <= halfEdges.size(); i++) {
			if (heTest.next == he.sibling) {
				heTest.next = he0.sibling;
				break;
			}
			heTest = heTest.next;
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED);
	}

	/******************************************************************************/
	/*
	 * Removal methods
	 * /*********************************************************
	 * ********************
	 */

	public void removeBoundaryPoint(final Tri_BPoint bp) {
		Tri_BPoint bp1, bp2;
		/*
		 * The boundary is oriented clockwise, opposite of the halfedge
		 * orientation (counter-clockwise). Therefore, the "previous" point to
		 * bp is actually the next point along the boundary.
		 */
		removeBoundaryPoint(bp, bp.next);
		/* relink boundary */
		bp1 = bp.prev;
		bp2 = bp.next;
		bp1.next = bp2;
		bp2.prev = bp1;
	}

	private void removeBoundaryPoint(final Tri_Point p, final Tri_Point pPrev) {
		int i;
		Tri_Point pNext;
		Tri_HalfEdge he;

		if (MESSAGES) {
			message("Removing boundary point %d.", points.indexOf(p));
		}
		pNext = p.he.next.origin;
		he = p.he.getPrev();
		deleteQueue.clear();
		for (i = 0; i <= halfEdges.size(); i++) {
			if (he.isType(Tri_HalfEdge.BOUNDARY)) {
				break;
			}
			if (WB_Predicates2D.orient2d(pPrev, pNext, he.origin) <= 0) {
				deleteQueue.add(he.origin);
			}
			removeEdge(he);
			he = p.he.getPrev();
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED);
		if (MESSAGES) {
			message("Performing flood delete to remove interior points.");
		}
		final Tri_Point[] bounds = new Tri_Point[3];
		bounds[0] = pPrev;
		bounds[1] = pNext;
		bounds[2] = p;
		floodDelete(bounds);
		pPrev.he.next = p.he.next;
		halfEdges.remove(p.he);
		p.he = null;
		points.remove(p);
		p.setType(Tri_Point.DELETED);
		/* stitch the polygon back together */
		fillEdgeVisiblePolygon(pPrev.he);
		updateDelaunay();
		nBoundary--;
	}

	public void removeInteriorPoint(final Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);

		int i;
		Tri_Point p1, p2, p3;
		Tri_HalfEdge heSearch, heFlip;
		final LinkedList<Tri_HalfEdge> star = new LinkedList<Tri_HalfEdge>();

		if (MESSAGES) {
			message("Removing interior point.");
		}
		/* construct the star of halfedges around p */
		heSearch = p.he;
		for (i = 0; i <= halfEdges.size(); i++) {
			star.add(heSearch);
			heSearch = heSearch.next.next.sibling;
			if (heSearch == p.he) {
				break;
			}
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED);
		assert star.size() >= 3;
		if (star.size() == 3) {
			for (final Tri_HalfEdge he : star) {
				removeEdge(he);
			}
		} else {
			while (star.size() > 4) {
				heFlip = star.pop();
				p1 = heFlip.sibling.origin;
				p2 = heFlip.next.next.origin;
				p3 = heFlip.sibling.next.next.origin;
				if (WB_Predicates2D.orient2d(p2, p3, p)
						* WB_Predicates2D.orient2d(p2, p3, p1) < 0) {
					flipEdge(heFlip);
				} else {
					star.add(heFlip);
				}
			}
			assert star.size() == 4;
			heSearch = star.get(0).next;
			for (final Tri_HalfEdge he : star) {
				removeEdge(he);
			}
			fillQuadrilateral(heSearch);
		}
		p.he = null;
		points.remove(p);
		p.setType(Tri_Point.DELETED);
		updateDelaunay();
	}

	/*
	 * Removes an interior point without refilling.
	 * @params p
	 */
	private void removePoint(final Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);
		assert !p.isType(Tri_Point.DELETED) : error("Re-removing point!");

		int i;
		Tri_HalfEdge he = p.he;

		if (MESSAGES) {
			message("Removing point %d.", points.indexOf(p));
		}

		for (i = 0; i <= halfEdges.size(); i++) {
			assert he.origin == p : error("Mismatched halfedge!");
			removeEdge(he);
			if (p.he == null) {
				break;
			}
			he = he.sibling.next;
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED);

		points.remove(p);
		p.setType(Tri_Point.DELETED);
		p.he = null;
	}

	private void removeEdge(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING);
		assert !he.isType(Tri_HalfEdge.BOUNDARY) : error(E_TYPE);

		Tri_HalfEdge hePrev, heSibPrev;

		if (MESSAGES) {
			message("Removing edge %d.", halfEdges.indexOf(he));
		}

		hePrev = findPrevious(he);
		heSibPrev = findPrevious(he.sibling);
		/* remove halfedges */
		halfEdges.remove(he);
		halfEdges.remove(he.sibling);
		delaunayQueue.remove(he);
		delaunayQueue.remove(he.sibling);
		/* cache the constraints */
		if (he.isType(Tri_HalfEdge.CONSTRAINT)) {
			removedConstraints.add(he.next.origin);
		}
		/* update point->halfedge pointers */
		if (he.sibling == hePrev) {
			/* this was the last halfedge eminating from he.origin */
			he.origin.he = null;
			updateHalfEdge(he.next);
		} else if (he.next == he.sibling) {
			/* this was the last halfedge eminating from he.sibling.origin */
			he.next.origin.he = null;
			updateHalfEdge(he.sibling.next);
		} else {
			updateHalfEdge(he.next);
			updateHalfEdge(he.sibling.next);
		}
		/* relink halfedges */
		hePrev.next = he.sibling.next;
		heSibPrev.next = he.next;
	}

	private void floodDelete(final Tri_Point[] bounds) {
		assert bounds.length >= 3 : error("Illegal bounds!");

		int i;
		int[] types;
		Boolean inside;
		Tri_Point p1, p2;
		Tri_HalfEdge heTest;
		final GeneralPath boundsPath = new GeneralPath();

		types = new int[bounds.length];
		for (i = 0; i < bounds.length; i++) {
			types[i] = bounds[i].type;
		}
		for (final Tri_Point p : bounds) {
			p.type = Tri_Point.BOUNDS;
		}
		boundsPath.moveTo(bounds[0].x, bounds[0].y);
		for (i = 1; i < bounds.length; i++) {
			boundsPath.lineTo(bounds[i].x, bounds[i].y);
		}
		boundsPath.closePath();
		while (deleteQueue.size() > 0) {
			p1 = deleteQueue.pop();
			if (boundsPath.contains(p1.x, p1.y) && !p1.isType(Tri_Point.BOUNDS)) {
				heTest = p1.he;
				for (i = 0; i <= halfEdges.size(); i++) {
					if (p1.he == null) {
						break;
					}
					p2 = heTest.next.origin;
					if (!p2.isType(Tri_Point.BOUNDS)) {
						inside = true;
						for (int j = 1; j < bounds.length; j++) {
							if (intersect(p1, p2, bounds[j - 1], bounds[j])) {
								/*
								 * check if p2 lies strictly outside the bounds
								 * (i.e. is not colinear with the bounds)
								 */
								if (WB_Predicates2D.orient2d(bounds[j - 1],
										bounds[j], p2) != 0) {
									inside = false;
								}
								break;
							}
						}
						if (inside) {
							deleteQueue.add(p2);
						}
					}
					removeEdge(heTest);
					heTest = heTest.sibling.next;
				}
				assert i < halfEdges.size() : error(E_EXHAUSTED);
				points.remove(p1);
				p1.type = Tri_Point.DELETED;
				p1.he = null;
			}
		}
		for (i = 0; i < bounds.length; i++) {
			bounds[i].type = types[i];
		}
	}

	private void clearNewBoundaryEdge(final Tri_Point pStart,
			final Tri_Point pEnd) {
		assert points.contains(pStart) : error(E_MISSING);
		assert points.contains(pEnd) : error(E_MISSING);
		assert pStart != pEnd : error(E_IDENTICAL);

		Tri_FaceWalk walk;

		if (MESSAGES) {
			message("Clearing new boundary edge.");
		}
		walk = startFaceWalk(pStart, pEnd);
		// check for trivial case
		if (walk.status == Tri_FaceWalk.COINCIDENT) {
			removeEdge(walk.he);
		} else {
			int i;
			Tri_Point pSearch0, pSearch1;
			Tri_HalfEdge heSearch;

			heSearch = walk.he;
			for (i = 0; i <= halfEdges.size(); i++) {
				pSearch0 = heSearch.origin;
				pSearch1 = heSearch.next.origin;
				// check for termination
				if (pSearch1 == pEnd) {
					break;
				}
				// check for collinearity
				if (between(pStart, pEnd, pSearch1)) {
					if (pSearch1.isType(Tri_Point.BOUNDARY)) {
						heSearch = heSearch.next;
					} else {
						deleteQueue.add(pSearch1);
						heSearch = heSearch.sibling.next;
					}
				}
				// check for intersection
				else if (intersectProper(pStart, pEnd, pSearch0, pSearch1)) {
					removeEdge(heSearch);
					deleteQueue.add(pSearch0);
					heSearch = heSearch.sibling.next;
				} else {
					heSearch = heSearch.next;
				}
			}
			assert i < halfEdges.size() : error(E_EXHAUSTED);
		}
	}

	/******************************************************************************/
	/*
	 * Update methods
	 * /**********************************************************
	 * *******************
	 */

	protected final void updateHalfEdge(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING);
		if (he.origin.isType(Tri_Point.INTERIOR)) {
			he.origin.he = he;
		}
	}

	public void updateDelaunay() {
		WB_Point2d p1, p2, p3, p4;

		if (MESSAGES) {
			message("Testing Delaunay queue with %d halfedges.",
					delaunayQueue.size());
		}
		while (delaunayQueue.size() > 0) {
			final Tri_HalfEdge he = delaunayQueue.pop();
			if (he.isType(Tri_HalfEdge.AUXILARY)) {
				p1 = he.next.origin;
				p2 = he.next.next.origin;
				p3 = he.origin;
				p4 = he.sibling.next.next.origin;
				if (WB_Predicates2D.incircle2d(p1, p2, p3, p4) > 0
						|| WB_Predicates2D.incircle2d(p3, p4, p1, p2) > 0) {
					flipEdge(he);
				}
			}
		}
	}

	public void updateDelaunayAll() {
		delaunayQueue.addAll(halfEdges);
		updateDelaunay();
	}

	public void updateInteriorPoint(Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);
		initRemoveConstraints(p);
		removeInteriorPoint(p);
		p = addInteriorPoint(p);
		p.setType(Tri_Point.INTERIOR);
		restoreConstraints(p);
		updateDelaunay();
	}

	public void updateBoundaryPointOutside(final Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);

		int i;
		Tri_HalfEdge he;

		if (MESSAGES) {
			message("Updating boundary point outside existing boundary.");
		}

		initRemoveConstraints(p);
		he = p.he.next.next;
		assert he.next == p.he : error(E_POLYGON);

		for (i = 0; i <= halfEdges.size(); i++) {
			if (he.isType(Tri_HalfEdge.BOUNDARY)) {
				break;
			}
			removeEdge(he.sibling);
			/* walk around p counter-clockwise */
			he = he.sibling.next.next;
		}
		assert i < halfEdges.size() : E_EXHAUSTED;

		fillGeneralPolygon(p.he);
		restoreConstraints(p);
		updateDelaunay();
	}

	public boolean updateBoundaryPointInside(final Tri_Point p,
			final Tri_Point pPrev, Tri_Point pTemp) {
		assert points.contains(p) : error(E_MISSING);
		assert points.contains(pPrev) : error(E_MISSING);

		int i;
		Tri_Point pNext;
		Tri_HalfEdge heTest;

		if (MESSAGES) {
			message("Updating boundary point inside existing boundary.");
		}

		// locate the previous and next boundary points
		pNext = p.he.next.origin;
		// check for coincidence
		/*
		 * if (coincident(p,pTemp)) { p.x = pTemp.x; p.y = pTemp.y; return
		 * updateBoundaryPointAlong(p,pPrev); }
		 */
		// relocate p to ensure that the quadrilateral (p,pPrev,pTemp,pNext)
		// is a simple polygon
		if (intersectProper(pPrev, p, pNext, pTemp)) {
			if (MESSAGES) {
				message("Moving point to ensure simple quadrilateral.");
			}
			final WB_Point2d pp = intersection(pPrev, p, pNext, pTemp);
			p.x = pp.x + 0.5 * (pPrev.x - pp.x);
			p.y = pp.y + 0.5 * (pPrev.y - pp.y);
			updateBoundaryPointOutside(p);
		} else if (intersectProper(pNext, p, pPrev, pTemp)) {
			if (MESSAGES) {
				message("Moving point to ensure simple quadrilateral.");
			}
			final WB_Point2d pp = intersection(pNext, p, pPrev, pTemp);
			p.x = pp.x + 0.5 * (pNext.x - pp.x);
			p.y = pp.y + 0.5 * (pNext.y - pp.y);
			updateBoundaryPointOutside(p);
		}
		// insert an interior point where the new boundary point will
		// eventually be
		pTemp = addInteriorPoint(pTemp);
		if (MESSAGES) {
			message("Added temporary interior point.");
		}
		// flip edge pPrev->pNext if it exists
		heTest = pPrev.he;
		for (i = 0; i <= halfEdges.size(); i++) {
			if (heTest.next.origin == pNext) {
				if (MESSAGES) {
					message("Flipping edge pPrev->pNext.");
				}
				flipEdge(heTest);
				break;
			}
			heTest = heTest.next.next;
			if (heTest.isType(Tri_HalfEdge.BOUNDARY)) {
				break;
			}
			heTest = heTest.sibling;
			if (heTest == pPrev.he) {
				break;
			}
		}
		assert i < halfEdges.size() : E_EXHAUSTED;
		// sweep along the new boundary edges to remove intersecting edges
		deleteQueue.clear();
		clearNewBoundaryEdge(pPrev, pTemp);
		clearNewBoundaryEdge(pTemp, pNext);
		/* flood fill to remove points and edges inside the quad */
		if (MESSAGES) {
			message("Performing flood delete to remove outside points.");
		}
		final Tri_Point[] bounds = new Tri_Point[4];
		bounds[0] = pPrev;
		bounds[1] = pTemp;
		bounds[2] = pNext;
		bounds[3] = p;
		floodDelete(bounds);
		if (MESSAGES) {
			message("Removing placeholder interior point.");
		}
		removePoint(pTemp);
		if (MESSAGES) {
			message("Relocating boundary point and boundary edges.");
		}
		p.x = pTemp.x;
		p.y = pTemp.y;
		fillGeneralPolygon(p.he);
		updateDelaunay();
		return true;
	}

	private boolean constrainEdge(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING);
		if (MESSAGES) {
			message("Constraining edge %d.", halfEdges.indexOf(he));
		}
		if (he.isType(Tri_HalfEdge.BOUNDARY)) {
			if (MESSAGES) {
				message("Ignoring boundary edge constraint.");
			}
			return false;
		}
		he.constrain();
		he.sibling.constrain();
		return true;
	}

	public void constrainAllEdges() {
		for (final Tri_HalfEdge he : halfEdges) {
			if (!he.isType(Tri_HalfEdge.BOUNDARY)) {
				he.constrain();
			}
		}
	}

	public void initRemoveConstraints(final Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);
		removeConstraintPeg = p;
		removedConstraints.clear();
	}

	public void restoreConstraints(final Tri_Point p) {
		assert points.contains(p) : error(E_MISSING);
		assert p == removeConstraintPeg : error("Race condition!");
		removedConstraints.remove(p);
		for (final Tri_Point p0 : removedConstraints) {
			if (p0.isType(Tri_Point.DELETED)) {
				continue;
			}
			addConstraint(p, p0);
		}
		removeConstraintPeg = null;
	}

	private boolean flipEdge(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING);
		assert !he.isType(Tri_HalfEdge.BOUNDARY) : error(E_TYPE);

		Tri_HalfEdge he1, he2, he3, he4;

		if (MESSAGES) {
			message("Flipping edge %d.", halfEdges.indexOf(he));
		}
		// locate halfedges
		he1 = he.next;
		he2 = he1.next;
		he3 = he.sibling.next;
		he4 = he3.next;
		// flip the origins
		he.origin = he2.origin;
		he.sibling.origin = he4.origin;
		// update point->halfedge pointers
		updateHalfEdge(he3);
		updateHalfEdge(he1);
		// link halfedges
		he1.next = he;
		he.next = he4;
		he4.next = he1;
		he3.next = he.sibling;
		he.sibling.next = he2;
		he2.next = he3;
		// add halfedges to the delaunay test
		delaunayQueue.add(he1);
		delaunayQueue.add(he2);
		delaunayQueue.add(he3);
		delaunayQueue.add(he4);
		return true;
	}

	/******************************************************************************/
	/*
	 * Locator methods
	 * /*********************************************************
	 * ********************
	 */

	/**
	 * This brute force approach works for *any* non-intersecting boundary,
	 * concave or convex. If the boundary is guaranteed to be a convex, a
	 * smarter face-walking algorithm could be used.
	 */
	public Tri_FaceWalk findFaceBruteForce(final Tri_HalfEdge heStart,
			final Tri_Point p) {
		final double[] ccw = new double[3];
		Tri_HalfEdge he1, he2;

		clearFlags(Tri_HalfEdge.FLAG_ALGORITHM);
		for (final Tri_HalfEdge he0 : halfEdges) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_ALGORITHM)) {
				continue;
			}
			he1 = he0.next;
			he2 = he1.next;
			assert he2.next == he0 : error("Found non-face!");
			he0.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			he1.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			he2.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			ccw[0] = WB_Predicates2D.orient2d(he0.origin, he1.origin, p);
			if (ccw[0] < 0) {
				continue;
			}
			ccw[1] = WB_Predicates2D.orient2d(he1.origin, he2.origin, p);
			if (ccw[1] < 0) {
				continue;
			}
			ccw[2] = WB_Predicates2D.orient2d(he2.origin, he0.origin, p);
			if (ccw[2] < 0) {
				continue;
			}
			if (ccw[0] == 0) {
				return new Tri_FaceWalk(he0, Tri_FaceWalk.COINCIDENT);
			}
			if (ccw[1] == 0) {
				return new Tri_FaceWalk(he1, Tri_FaceWalk.COINCIDENT);
			}
			if (ccw[2] == 0) {
				return new Tri_FaceWalk(he2, Tri_FaceWalk.COINCIDENT);
			}
			return new Tri_FaceWalk(he0, Tri_FaceWalk.CLOCKWISE);
		}
		return null;
	}

	/**
	 * A slightly smarter face walk routine that resorts to brute force
	 * only when it gets confused by an concave boundary.
	 */
	public Tri_FaceWalk findFace(final Tri_HalfEdge heStart, final Tri_Point p) {
		int i;
		final double[] ccw = new double[3];
		Tri_HalfEdge he0, he1, he2;
		final LinkedList<Tri_HalfEdge> queue = new LinkedList<Tri_HalfEdge>();

		clearFlags(Tri_HalfEdge.FLAG_ALGORITHM);
		queue.add(heStart);
		queue.addAll(halfEdges);
		he0 = queue.pop();
		for (i = 0; i <= halfEdges.size(); i++) {
			if (he0.isFlagged(Tri_HalfEdge.FLAG_ALGORITHM)) {
				he0 = queue.pop();
				continue;
			}
			he1 = he0.next;
			he2 = he1.next;
			assert he2.next == he0 : error("Found non-face!");
			he0.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			he1.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			he2.flag(Tri_HalfEdge.FLAG_ALGORITHM);
			ccw[0] = WB_Predicates2D.orient2d(he0.origin, he1.origin, p);
			if (ccw[0] < 0) {
				he0 = he0.sibling;
				continue;
			}
			ccw[1] = WB_Predicates2D.orient2d(he1.origin, he2.origin, p);
			if (ccw[1] < 0) {
				he0 = he1.sibling;
				continue;
			}
			ccw[2] = WB_Predicates2D.orient2d(he2.origin, he0.origin, p);
			if (ccw[2] < 0) {
				he0 = he2.sibling;
				continue;
			}
			if (ccw[0] == 0) {
				return new Tri_FaceWalk(he0, Tri_FaceWalk.COINCIDENT);
			}
			if (ccw[1] == 0) {
				return new Tri_FaceWalk(he1, Tri_FaceWalk.COINCIDENT);
			}
			if (ccw[2] == 0) {
				return new Tri_FaceWalk(he2, Tri_FaceWalk.COINCIDENT);
			}
			return new Tri_FaceWalk(he0, Tri_FaceWalk.CLOCKWISE);
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED);
		return null;
	}

	protected final Tri_HalfEdge findPrevious(final Tri_HalfEdge he) {
		assert halfEdges.contains(he) : error(E_MISSING, he);

		int i;
		Tri_HalfEdge heSearch;

		heSearch = he.next;
		for (i = 0; i <= halfEdges.size(); i++) {

			if (heSearch.next == he) {
				break;
			}
			heSearch = heSearch.next;
		}
		assert i < halfEdges.size() : error(E_EXHAUSTED, he);

		assert halfEdges.contains(heSearch) : error(E_MISSING, heSearch);

		return heSearch;
	}

	// robust with non-triangular regions
	Tri_FaceWalk startFaceWalk(final Tri_Point pStart, final Tri_Point pEnd) {
		assert points.contains(pStart) : error(E_MISSING);
		assert points.contains(pEnd) : error(E_MISSING);
		assert pStart != pEnd : error(E_IDENTICAL, pStart, pEnd);
		assert !coincident(pStart, pEnd) : error(E_COINCIDENT, pStart, pEnd);

		int i;
		double ccwTrailing, ccwLeading;
		Tri_Point pTrailing, pLeading;
		Tri_HalfEdge he;
		Tri_HalfEdge hePrev = null;

		he = pStart.he;
		pTrailing = he.next.origin;
		ccwTrailing = WB_Predicates2D.orient2d(pStart, pEnd, pTrailing);
		// special case for boundary starting points
		if (pStart.isType(Tri_Point.BOUNDARY)) {
			// check for coincidence with boundary
			if (pTrailing == pEnd) {
				return new Tri_FaceWalk(he, Tri_FaceWalk.COINCIDENT);
			}
			// check whether pStart->pEnd is within epsilon of the boundary
			assert ccwTrailing <= 0 : error("Tri_Point lies outside boundary!",
					pEnd);
			// it is safe to assume that he is clockwise because of the above
			// assertion
			if (ccwTrailing == 0) {
				if (betweenProper(pStart, pEnd, pTrailing)
						|| betweenProper(pStart, pTrailing, pEnd)) {
					return new Tri_FaceWalk(he, Tri_FaceWalk.CLOCKWISE);
				}
			}
		}
		for (i = 0; i <= halfEdges.size(); i++) {
			// this face may be a polygon, so search forward to find
			// the second test edge
			hePrev = findPrevious(he);
			pTrailing = he.next.origin;
			pLeading = hePrev.origin;
			// check for coincidence of either star edge
			if (pTrailing == pEnd) {
				return new Tri_FaceWalk(he, Tri_FaceWalk.COINCIDENT);
			} else if (pLeading == pEnd) {
				return new Tri_FaceWalk(hePrev, Tri_FaceWalk.COINCIDENT);
			}
			assert !coincident(pTrailing, pEnd) : error(E_COINCIDENT,
					pTrailing, pEnd);
			assert !coincident(pLeading, pEnd) : error(E_COINCIDENT, pLeading,
					pEnd);
			// check if the leading point is counter-clockwise/collinear and the
			// trailing point clockwise of pStart->pEnd
			ccwLeading = WB_Predicates2D.orient2d(pStart, pEnd, pLeading);
			if (ccwLeading >= 0 && ccwTrailing < 0) {
				return new Tri_FaceWalk(he, Tri_FaceWalk.CLOCKWISE);
			}
			ccwTrailing = ccwLeading;
			he = hePrev.sibling;
		}
		assert i < halfEdges.size() : E_EXHAUSTED;

		// return a failed walk at this point
		return new Tri_FaceWalk(null, Tri_FaceWalk.FAILED);
	}

	/******************************************************************************/
	/*
	 * Computational geometry methods and predicates
	 * /***************************
	 * **************************************************
	 */

	// compute the normalized projection of ac onto ab
	private final static double projNorm(final WB_Point2d a,
			final WB_Point2d b, final WB_Point2d c) {
		double x1, x2, y1, y2;
		x1 = b.x - a.x;
		x2 = c.x - a.x;
		y1 = b.y - a.y;
		y2 = c.y - a.y;
		return (x1 * x2 + y1 * y2) / (x1 * x1 + y1 * y1);
	}

	// compute the magnitude of the cross product of ab and ac
	private final static double cross(final WB_Point2d a, final WB_Point2d b,
			final WB_Point2d c) {
		double x1, x2, y1, y2;
		x1 = b.x - a.x;
		x2 = c.x - a.x;
		y1 = b.y - a.y;
		y2 = c.y - a.y;
		return x1 * y2 - y1 * x2;
	}

	// compute the squared perpendicular distance of c onto ab
	private final static double perpDistSq(final WB_Point2d a,
			final WB_Point2d b, final WB_Point2d c) {
		double x1, x2, y1, y2, cross, lenSq;
		x1 = b.x - a.x;
		x2 = c.x - a.x;
		y1 = b.y - a.y;
		y2 = c.y - a.y;
		cross = x1 * y2 - y1 * x2;
		lenSq = cross * cross;
		lenSq /= x1 * x1 + y1 * y1;
		return lenSq;
	}

	public final boolean coincident(final WB_Point2d a, final WB_Point2d b) {
		if (WB_Distance2D.sqDistance(a, b) < WB_Epsilon.SQEPSILON) {
			return true;
		}
		return false;
	}

	// project the point p orthogonally onto the segment p1->p2
	// and return the scaled, parameterized position in [0,1]
	public final static double projection(final WB_Point2d p1,
			final WB_Point2d p2, final WB_Point2d p) {
		double ax, ay, bx, by;
		ax = p.x - p1.x;
		ay = p.y - p1.y;
		bx = p2.x - p1.x;
		by = p2.y - p1.y;
		return (ax * bx + ay * by) / (bx * bx + by * by);
	}

	// returns the intersection point of segments ab and cd
	// as a point on ab
	public final static WB_Point2d intersection(final WB_Point2d a,
			final WB_Point2d b, final WB_Point2d c, final WB_Point2d d) {
		double t, l1, l2;
		double cdx, cdy;
		WB_Point2d p;

		cdx = c.x - d.x;
		cdy = c.y - d.y;
		// distance from a to cd
		l1 = Math.abs((a.x - d.x) * cdy - (a.y - d.y) * cdx);
		// distance from b to cd
		l2 = Math.abs((b.x - d.x) * cdy - (b.y - d.y) * cdx);
		// need to handle case where l1+l2 = 0
		// if this method could be called on parallel segments
		// that overlap
		if (l1 + l2 == 0) {
			System.err
					.println("Intersection called on parallel overlapping segments!");
		}
		t = l1 / (l1 + l2);
		p = new WB_Point2d((1 - t) * a.x + t * b.x, (1 - t) * a.y + t * b.y);
		return p;
	}

	// from O'Rourke's Computational Geometry in C
	public final boolean intersect(final WB_Point2d a, final WB_Point2d b,
			final WB_Point2d c, final WB_Point2d d) {
		if (intersectProper(a, b, c, d)) {
			return true;
		} else if (between(a, b, c) || between(a, b, d) || between(c, d, a)
				|| between(c, d, b)) {
			return true;
		} else {
			return false;
		}
	}

	// from O'Rourke's Computational Geometry in C
	public final boolean intersectProper(final WB_Point2d a,
			final WB_Point2d b, final WB_Point2d c, final WB_Point2d d) {
		/* Eliminate improper cases. */
		if (WB_Predicates2D.orient2d(a, b, c) == 0
				|| WB_Predicates2D.orient2d(a, b, d) == 0
				|| WB_Predicates2D.orient2d(c, d, a) == 0
				|| WB_Predicates2D.orient2d(c, d, b) == 0) {
			return false;
		} else if (WB_Predicates2D.orient2d(a, b, c)
				* WB_Predicates2D.orient2d(a, b, d) > 0
				|| WB_Predicates2D.orient2d(c, d, a)
						* WB_Predicates2D.orient2d(c, d, b) > 0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Tests whether c is within the epsilon tubular neighborhood around segment
	 * ab.
	 */
	public final boolean between(final WB_Point2d a, final WB_Point2d b,
			final WB_Point2d c) {
		/* check the epsilon neighborhood at the endpoints */
		if (coincident(a, c)) {
			return true;
		} else if (coincident(b, c)) {
			return true;
		} else {
			/* check the epsilon neighborhood along the segment */
			if (perpDistSq(a, b, c) < WB_Epsilon.SQEPSILON) {
				final double d = projNorm(a, b, c);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Tests whether c is within the epsilon tubular neighborhood around segment
	 * ab, but excludes the epsilon neighborhoods around a and b.
	 */
	public final boolean betweenProper(final WB_Point2d a, final WB_Point2d b,
			final WB_Point2d c) {
		/* reject the epsilon neighborhood at the endpoints */
		if (coincident(a, c)) {
			return false;
		} else if (coincident(b, c)) {
			return false;
		} else {
			/* check the epsilon neighborhood along the segment */
			if (perpDistSq(a, b, c) < WB_Epsilon.SQEPSILON) {
				final double d = projNorm(a, b, c);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	// adapted from java.awt.geom.Line2d.ptSegDistSq()
	public final static double edgeDistanceSq(final WB_Point2d p1,
			final WB_Point2d p2, final WB_Point2d p) {
		double x2, y2, px, py;
		// Adjust vectors relative to x1,y1
		// x2,y2 becomes relative vector from x1,y1 to end of segment
		x2 = p2.x - p1.x;
		y2 = p2.y - p1.y;
		// px,py becomes relative vector from x1,y1 to test point
		px = p.x - p1.x;
		py = p.y - p1.y;
		double dotprod = px * x2 + py * y2;
		double projlenSq;
		if (dotprod <= 0.0) {
			// px,py is on the side of x1,y1 away from x2,y2
			// distance to segment is length of px,py vector
			// "length of its (clipped) projection" is now 0.0
			projlenSq = 0.0;
		} else {
			// switch to backwards vectors relative to x2,y2
			// x2,y2 are already the negative of x1,y1=>x2,y2
			// to get px,py to be the negative of px,py=>x2,y2
			// the dot product of two negated vectors is the same
			// as the dot product of the two normal vectors
			px = x2 - px;
			py = y2 - py;
			dotprod = px * x2 + py * y2;
			if (dotprod <= 0.0) {
				// px,py is on the side of x2,y2 away from x1,y1
				// distance to segment is length of (backwards) px,py vector
				// "length of its (clipped) projection" is now 0.0
				projlenSq = 0.0;
			} else {
				// px,py is between x1,y1 and x2,y2
				// dotprod is the length of the px,py vector
				// projected on the x2,y2=>x1,y1 vector times the
				// length of the x2,y2=>x1,y1 vector
				projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			}
		}
		// Distance to line is now the length of the relative point
		// vector minus the length of its projection onto the line
		// (which is zero if the projection falls outside the range
		// of the line segment).
		double lenSq = px * px + py * py - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	public void listPoints() {
		message("### POINT LIST ###");
		for (int i = 0; i < points.size(); i++) {
			final Tri_Point p = points.get(i);
			if (i % 20 == 0) {
				message("     ID | Halfedge |    Pair | Type");
			}
			int ih, ip;
			try {
				ih = halfEdges.indexOf(p.he);
			} catch (final NullPointerException e) {
				ih = NULL_VALUE;
			}
			try {
				ip = halfEdges.indexOf(p.he);
			} catch (final NullPointerException e) {
				ip = NULL_VALUE;
			}
			message("%7d |  %7d | %7d |    %1d", i, ih, ip, p.type);
		}
	}

	public final void listHalfEdges() {
		message("### HALFEDGE LIST ###");
		for (int i = 0; i < halfEdges.size(); i++) {
			final Tri_HalfEdge he = halfEdges.get(i);
			if (i % 20 == 0) {
				message("     ID ->    Next |  Origin ->    "
						+ "Next | Sibling | Type");
			}
			int in, io, ino, is;
			try {
				in = halfEdges.indexOf(he.next);
			} catch (final NullPointerException e) {
				in = NULL_VALUE;
			}
			try {
				io = points.indexOf(he.origin);
			} catch (final NullPointerException e) {
				io = NULL_VALUE;
			}
			try {
				ino = points.indexOf(he.next.origin);
			} catch (final NullPointerException e) {
				ino = NULL_VALUE;
			}
			try {
				is = halfEdges.indexOf(he.sibling);
			} catch (final NullPointerException e) {
				is = NULL_VALUE;
			}
			message("%7d -> %7d | %7d -> %7d | %7d | %1d", i, in, io, ino, is,
					he.type);
		}
	}

	public final void message(final String s) {
		System.out.print("Triangulation: ");
		System.out.print(s);
		System.out.print("\n");
	}

	public final void message(final String s, final Object... args) {
		message(String.format(s, args));
	}

	// used in assert statements to dump halfedge and point lists
	protected final String error(final String s) {
		listHalfEdges();
		listPoints();
		return s;
	}

	protected final String error(final String s, final Object... args) {
		return error(s);
	}

	// Check if triangle lies inside closed constraints (used when triangulating
	// irregular polygons).
	private boolean insideClosedConstraints(final WB_Point2d p1,
			final WB_Point2d p2, final WB_Point2d p3, final double range) {
		final WB_ExplicitTriangle2D tri = new WB_ExplicitTriangle2D(p1, p2, p3);
		final WB_Point2d s1 = tri.getIncenter();
		final WB_Point2d dir = new WB_Point2d(Math.random() - 0.5,
				Math.random() - 0.5);
		final WB_ExplicitSegment2D Sray = new WB_ExplicitSegment2D(s1, dir,
				range);
		int count = 0;
		for (int i = 0; i < halfEdges.size(); i++) {
			final Tri_HalfEdge he = halfEdges.get(i);
			if (he.isType(Tri_HalfEdge.CONSTRAINT)) {
				final WB_ExplicitSegment2D She = new WB_ExplicitSegment2D(
						he.origin, he.getNext().origin);

				final WB_IntersectionResult ir = WB_Intersection2D.intersect2D(
						Sray, She);
				if (ir.intersection) {
					if ((ir.t2 > 0) && (ir.t2 < 1)) {
						count++;
					} else {
						return insideClosedConstraints(p1, p2, p3, range);
					}
				}
			}

		}
		return ((count / 2) % 2 == 1);

	}
}
