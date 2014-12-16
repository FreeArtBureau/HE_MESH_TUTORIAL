/**

 */
package wblut.geom;

import java.util.List;

import wblut.WB_Epsilon;


import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Stripped version of Zhenyu Pan's JAVA transcription of Shane O'Sullivan's C
 * implementation of Fortune's sweep algorithm. I added degenerate point
 * detection.
 * 
 * http://shaneosullivan.wordpress.com/2007/04/05/fortunes-sweep-line-voronoi-
 * algorithm-implemented-in-java/
 * 
 * @author Frederik Vanhoutte, W:Blut, extremely derivative
 *
 */
public class WB_Voronoi2D {

	private class VoronoiEdge2D {
		public double	a	= 0, b = 0, c = 0;
		WB_Point2d[]	endPoints;
		WB_Point2d[]	betweenSites;
		int[]			indices;

		VoronoiEdge2D() {
			endPoints = new WB_Point2d[2];
			betweenSites = new WB_Point2d[2];
			indices = new int[2];
		}
	}

	private class VoronoiHalfedge2D {
		VoronoiHalfedge2D	leftHalfedge, rightHalfedge;
		VoronoiEdge2D		voronoiEdge;
		boolean				deleted;
		int					ELpm;
		WB_Point2d			vertex;
		double				ystar;
		VoronoiHalfedge2D	nextHalfedgeInPointQueue;

		VoronoiHalfedge2D() {
			nextHalfedgeInPointQueue = null;
		}
	}

	private double							borderMinX, borderMaxX, borderMinY,
			borderMaxY;
	private int								currentSite;
	private double							xmin, xmax, ymin, ymax, deltax,
			deltay;

	private int								nsites;
	private WB_Point2d[]					sites;
	private WB_Point2d						bottomsite;
	private int								pointQueueSize, pointQueueMin,
			numberOfPointQueuMarkers;
	private VoronoiHalfedge2D				pointQueueMarkers[];
	private static int						leftEnd		= 0;
	private static int						rightEnd	= 1;
	private int								numberOfEdgeListMarkers;
	private VoronoiHalfedge2D				edgeListMarkers[];
	private VoronoiHalfedge2D				edgeListLeftEnd, edgeListRightEnd;
	private FastList<WB_IndexedBisector2D>	allEdges;
	private FastMap<WB_Point2d, Integer>	indices;

	public WB_Voronoi2D() {
	}

	public List<WB_IndexedBisector2D> generateVoronoi(final double minX,
			final double maxX, final double minY, final double maxY,
			final WB_Point2d[] points) {
		final FastList<WB_Point2d> ppoints = new FastList<WB_Point2d>();
		for (final WB_Point2d point : points) {
			ppoints.add(point);
		}
		return generateVoronoi(minX, maxX, minY, maxY, ppoints);
	}

	public List<WB_IndexedBisector2D> generateVoronoi(double minX, double maxX,
			double minY, double maxY, final List<WB_Point2d> points) {
		double temp = 0;
		if (minX > maxX) {
			temp = minX;
			minX = maxX;
			maxX = temp;
		}
		if (minY > maxY) {
			temp = minY;
			minY = maxY;
			maxY = temp;
		}
		borderMinX = minX;
		borderMinY = minY;
		borderMaxX = maxX;
		borderMaxY = maxY;
		currentSite = 0;

		collectSites(points);
		createVoronoi2D();

		final List<WB_IndexedBisector2D> result = new FastList<WB_IndexedBisector2D>(
				allEdges.size());
		result.addAll(allEdges);
		return result;
	}

	private void collectSites(final List<WB_Point2d> pointsIn) {
		allEdges = new FastList<WB_IndexedBisector2D>();

		final FastList<WB_Point2d> points = new FastList<WB_Point2d>();
		final WB_KDTree2Dold<Integer> kdtree = new WB_KDTree2Dold<Integer>();
		WB_KDNeighbor2D<Integer>[] neighbors;
		WB_Point2d p = new WB_Point2d(pointsIn.get(0));
		kdtree.put(p, 0);
		points.add(p);
		for (int i = 1; i < pointsIn.size(); i++) {
			p = new WB_Point2d(pointsIn.get(i));
			neighbors = kdtree.getNearestNeighbors(p, 1, false);
			if (neighbors[0].sqDistance() > WB_Epsilon.SQEPSILON) {
				kdtree.put(p, i);
				points.add(p);

			}
		}
		sortSites(points);
	}

	private void sortSites(final FastList<WB_Point2d> points) {
		sites = new WB_Point2d[nsites];
		xmin = points.get(0).x;
		ymin = points.get(0).y;
		xmax = xmin;
		ymax = ymin;
		indices = new FastMap<WB_Point2d, Integer>();
		for (int i = 0; i < nsites; i++) {
			sites[i] = new WB_Point2d(points.get(i).x, points.get(i).y);
			indices.put(sites[i], i);
			final double xi = points.get(i).x;
			if (xi < xmin) {
				xmin = xi;
			} else if (xi > xmax) {
				xmax = xi;
			}
			final double yi = points.get(i).y;
			if (yi < ymin) {
				ymin = yi;
			} else if (yi > ymax) {
				ymax = yi;
			}
		}
		WB_Point2d tmp;
		for (int i = 0; i < nsites; i++) {
			for (int j = i + 1; j < nsites; j++) {
				if (sites[i].compareToY1st(sites[j]) > 0) {
					tmp = sites[i];
					sites[i] = sites[j];
					sites[j] = tmp;
				}
			}
		}
		deltay = ymax - ymin;
		deltax = xmax - xmin;
	}

	private boolean createVoronoi2D() {
		WB_Point2d newsite, bot, top, temp, p;
		WB_Point2d v;
		WB_Point2d newintstar = null;
		int pm;
		VoronoiHalfedge2D lbnd, rbnd, llbnd, rrbnd, bisector;
		VoronoiEdge2D e;
		currentSite = 0;
		pointQueueInitialize();
		edgeListInitialize();

		bottomsite = getNextSite();

		newsite = getNextSite();
		while (true) {
			if (!isPointQueueEmpty()) {
				newintstar = pointQueueMin();
			}

			if (newsite != null
					&& (isPointQueueEmpty() || newsite.y < newintstar.y || (newsite.y == newintstar.y && newsite.x < newintstar.x))) {

				/* new site is smallest -this is a site event */
				// get the first HalfEdge to the LEFT of the new site
				lbnd = ELleftbnd((newsite));
				// get the first HalfEdge to the RIGHT of the new site
				rbnd = lbnd.rightHalfedge;
				// if this halfedge has no edge,bot =bottom site (whatever that
				// is)
				bot = rightreg(lbnd);
				// create a new edge that bisects
				e = bisect(bot, newsite);

				// create a new HalfEdge, setting its ELpm field to 0
				bisector = HEcreate(e, leftEnd);
				// insert this new bisector edge between the left and right
				// vectors in a linked list
				insertHalfedgeInEdgeList(lbnd, bisector);

				// if the new bisector intersects with the left edge,
				// remove the left edge's vertex, and put in the new one
				if ((p = intersect(lbnd, bisector)) != null) {
					removeHalfedgeFromPointqueue(lbnd);
					insertHalfedgeInPointqueue(lbnd, p,
							WB_Distance2D.distance(p, newsite));
				}
				lbnd = bisector;
				// create a new HalfEdge, setting its ELpm field to 1
				bisector = HEcreate(e, rightEnd);
				// insert the new HE to the right of the original bisector
				// earlier in the IF stmt
				insertHalfedgeInEdgeList(lbnd, bisector);

				// if this new bisector intersects with the new HalfEdge
				if ((p = intersect(bisector, rbnd)) != null) {
					// push the HE into the ordered linked list of vertices
					insertHalfedgeInPointqueue(bisector, p,
							WB_Distance2D.distance(p, newsite));
				}

				newsite = getNextSite();
			} else if (!isPointQueueEmpty())
			/* intersection is smallest - this is a vector event */
			{
				// pop the HalfEdge with the lowest vector off the ordered list
				// of vectors
				lbnd = popMinFromPointQueue();
				// get the HalfEdge to the left of the above HE
				llbnd = lbnd.leftHalfedge;
				// get the HalfEdge to the right of the above HE
				rbnd = lbnd.rightHalfedge;
				// get the HalfEdge to the right of the HE to the right of the
				// lowest HE
				rrbnd = rbnd.rightHalfedge;
				// get the Site to the left of the left HE which it bisects
				bot = leftreg(lbnd);
				// get the Site to the right of the right HE which it bisects
				top = rightreg(rbnd);

				v = lbnd.vertex; // get the vertex that caused this event

				endpoint(lbnd.voronoiEdge, lbnd.ELpm, v);
				// set the endpoint of
				// the left HalfEdge to be this vector
				endpoint(rbnd.voronoiEdge, rbnd.ELpm, v);
				// set the endpoint of the right HalfEdge to
				// be this vector
				deleteHalfedgeFromEdgeList(lbnd); // mark the lowest HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				removeHalfedgeFromPointqueue(rbnd);
				// remove all vertex events to do with the right HE
				deleteHalfedgeFromEdgeList(rbnd); // mark the right HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				pm = leftEnd; // set the pm variable to zero

				if (bot.y > top.y)
				// if the site to the left of the event is higher than the
				// Site
				{ // to the right of it, then swap them and set the 'pm'
					// variable to 1
					temp = bot;
					bot = top;
					top = temp;
					pm = rightEnd;
				}
				e = bisect(bot, top); // create an Edge (or line)
				// that is between the two Sites. This creates the formula of
				// the line, and assigns a line number to it
				bisector = HEcreate(e, pm); // create a HE from the Edge 'e',
				// and make it point to that edge
				// with its ELedge field
				insertHalfedgeInEdgeList(llbnd, bisector); // insert the new
				// bisector to the
				// right of the left HE
				endpoint(e, rightEnd - pm, v); // set one endpoint to the new
				// edge
				// to be the vector point 'v'.
				// If the site to the left of this bisector is higher than the
				// right Site, then this endpoint
				// is put in position 0; otherwise in pos 1

				// if left HE and the new bisector intersect, then delete
				// the left HE, and reinsert it
				if ((p = intersect(llbnd, bisector)) != null) {
					removeHalfedgeFromPointqueue(llbnd);
					insertHalfedgeInPointqueue(llbnd, p,
							WB_Distance2D.distance(p, bot));
				}

				// if right HE and the new bisector intersect, then
				// reinsert it
				if ((p = intersect(bisector, rrbnd)) != null) {
					insertHalfedgeInPointqueue(bisector, p,
							WB_Distance2D.distance(p, bot));
				}
			} else {
				break;
			}
		}
		for (lbnd = edgeListLeftEnd.rightHalfedge; lbnd != edgeListRightEnd; lbnd = lbnd.rightHalfedge) {
			e = lbnd.voronoiEdge;
			clipToBoundary(e);
		}
		return true;
	}

	private boolean pointQueueInitialize() {
		pointQueueSize = 0;
		pointQueueMin = 0;
		numberOfPointQueuMarkers = 4 * (int) Math.sqrt(nsites + 4);
		pointQueueMarkers = new VoronoiHalfedge2D[numberOfPointQueuMarkers];
		for (int i = 0; i < numberOfPointQueuMarkers; i += 1) {
			pointQueueMarkers[i] = new VoronoiHalfedge2D();
		}
		return true;
	}

	private int pointQueueMarker(final VoronoiHalfedge2D he) {
		int marker;

		marker = (int) ((he.ystar - ymin) / deltay * numberOfPointQueuMarkers);
		if (marker < 0) {
			marker = 0;
		}
		if (marker >= numberOfPointQueuMarkers) {
			marker = numberOfPointQueuMarkers - 1;
		}
		if (marker < pointQueueMin) {
			pointQueueMin = marker;
		}
		return (marker);
	}

	// push the HalfEdge into the ordered linked list of vertices
	private void insertHalfedgeInPointqueue(final VoronoiHalfedge2D he,
			final WB_Point2d v, final double offset) {
		VoronoiHalfedge2D last, next;

		he.vertex = v;
		he.ystar = (v.y + offset);
		last = pointQueueMarkers[pointQueueMarker(he)];
		while ((next = last.nextHalfedgeInPointQueue) != null
				&& (he.ystar > next.ystar || (he.ystar == next.ystar && v.x > next.vertex.x))) {
			last = next;
		}
		he.nextHalfedgeInPointQueue = last.nextHalfedgeInPointQueue;
		last.nextHalfedgeInPointQueue = he;
		pointQueueSize += 1;
	}

	// remove the HalfEdge from the list of vertices
	private void removeHalfedgeFromPointqueue(final VoronoiHalfedge2D he) {
		VoronoiHalfedge2D last;

		if (he.vertex != null) {
			last = pointQueueMarkers[pointQueueMarker(he)];
			while (last.nextHalfedgeInPointQueue != he) {
				last = last.nextHalfedgeInPointQueue;
			}

			last.nextHalfedgeInPointQueue = he.nextHalfedgeInPointQueue;
			pointQueueSize -= 1;
			he.vertex = null;
		}
	}

	private boolean isPointQueueEmpty() {
		return (pointQueueSize == 0);
	}

	private WB_Point2d pointQueueMin() {
		final WB_Point2d result = new WB_Point2d();

		while (pointQueueMarkers[pointQueueMin].nextHalfedgeInPointQueue == null) {
			pointQueueMin += 1;
		}
		result.x = pointQueueMarkers[pointQueueMin].nextHalfedgeInPointQueue.vertex.x;
		result.y = pointQueueMarkers[pointQueueMin].nextHalfedgeInPointQueue.ystar;
		return (result);
	}

	private VoronoiHalfedge2D popMinFromPointQueue() {
		VoronoiHalfedge2D curr;

		curr = pointQueueMarkers[pointQueueMin].nextHalfedgeInPointQueue;
		pointQueueMarkers[pointQueueMin].nextHalfedgeInPointQueue = curr.nextHalfedgeInPointQueue;
		pointQueueSize -= 1;
		return (curr);
	}

	private boolean edgeListInitialize() {
		int i;
		numberOfEdgeListMarkers = 2 * (int) Math.sqrt(nsites + 4);
		edgeListMarkers = new VoronoiHalfedge2D[numberOfEdgeListMarkers];

		for (i = 0; i < numberOfEdgeListMarkers; i += 1) {
			edgeListMarkers[i] = null;
		}
		edgeListLeftEnd = HEcreate(null, 0);
		edgeListRightEnd = HEcreate(null, 0);
		edgeListLeftEnd.leftHalfedge = null;
		edgeListLeftEnd.rightHalfedge = edgeListRightEnd;
		edgeListRightEnd.leftHalfedge = edgeListLeftEnd;
		edgeListRightEnd.rightHalfedge = null;
		edgeListMarkers[0] = edgeListLeftEnd;
		edgeListMarkers[numberOfEdgeListMarkers - 1] = edgeListRightEnd;

		return true;
	}

	private void insertHalfedgeInEdgeList(final VoronoiHalfedge2D lb,
			final VoronoiHalfedge2D newHe) {
		newHe.leftHalfedge = lb;
		newHe.rightHalfedge = lb.rightHalfedge;
		(lb.rightHalfedge).leftHalfedge = newHe;
		lb.rightHalfedge = newHe;
	}

	/*
	 * This delete routine can't reclaim node, since pointers from hash table
	 * may be present.
	 */
	private void deleteHalfedgeFromEdgeList(final VoronoiHalfedge2D he) {
		(he.leftHalfedge).rightHalfedge = he.rightHalfedge;
		(he.rightHalfedge).leftHalfedge = he.leftHalfedge;
		he.deleted = true;
	}

	/* Get entry from hash table, pruning any deleted nodes */
	private VoronoiHalfedge2D edgeListMarker(final int b) {
		VoronoiHalfedge2D he;

		if (b < 0 || b >= numberOfEdgeListMarkers) {
			return (null);
		}
		he = edgeListMarkers[b];
		if (he == null || !he.deleted) {
			return (he);
		}

		/* Hash table points to deleted half edge. Patch as necessary. */
		edgeListMarkers[b] = null;
		return (null);
	}

	private VoronoiHalfedge2D ELleftbnd(final WB_Point2d p) {
		int i, marker;
		VoronoiHalfedge2D he;

		/* Use hash table to get close to desired halfedge */
		// use the hash function to find the place in the hash map that this
		// HalfEdge should be
		marker = (int) ((p.x - xmin) / deltax * numberOfEdgeListMarkers);

		// make sure that the bucket position in within the range of the hash
		// array
		if (marker < 0) {
			marker = 0;
		}
		if (marker >= numberOfEdgeListMarkers) {
			marker = numberOfEdgeListMarkers - 1;
		}

		he = edgeListMarker(marker);
		if (he == null)
		// if the HE isn't found, search backwards and forwards in the hash map
		// for the first non-null entry
		{
			for (i = 1; i < numberOfEdgeListMarkers; i += 1) {
				if ((he = edgeListMarker(marker - i)) != null) {
					break;
				}
				if ((he = edgeListMarker(marker + i)) != null) {
					break;
				}
			}
		}
		/* Now search linear list of halfedges for the correct one */
		if (he == edgeListLeftEnd
				|| (he != edgeListRightEnd && isPointToRightOfEdge(he, p))) {
			// keep going right on the list until either the end is reached, or
			// you find the 1st edge which the point isn't to the right of
			do {
				he = he.rightHalfedge;
			} while (he != edgeListRightEnd && isPointToRightOfEdge(he, p));
			he = he.leftHalfedge;
		} else {
			// if the point is to the left of the HalfEdge, then search left for
			// the HE just to the left of the point
			do {
				he = he.leftHalfedge;
			} while (he != edgeListLeftEnd && !isPointToRightOfEdge(he, p));
		}

		/* Update hash table and reference counts */
		if (marker > 0 && marker < numberOfEdgeListMarkers - 1) {
			edgeListMarkers[marker] = he;
		}
		return (he);
	}

	private WB_Point2d getNextSite() {
		WB_Point2d s;
		if (currentSite < nsites) {
			s = sites[currentSite];
			currentSite += 1;
			return (s);
		} else {
			return (null);
		}
	}

	private VoronoiEdge2D bisect(final WB_Point2d s1, final WB_Point2d s2) {
		double dx, dy, adx, ady;
		VoronoiEdge2D newedge;
		newedge = new VoronoiEdge2D();
		newedge.betweenSites[0] = s1;
		newedge.betweenSites[1] = s2;
		newedge.endPoints[0] = null;
		newedge.endPoints[1] = null;

		// get the difference in x dist between the sites
		dx = s2.x - s1.x;
		dy = s2.y - s1.y;
		// make sure that the difference in positive
		adx = dx > 0 ? dx : -dx;
		ady = dy > 0 ? dy : -dy;
		newedge.c = s1.x * dx + s1.y * dy + (dx * dx + dy * dy) * 0.5;// get
		// the
		// slope
		// of
		// the
		// line

		if (adx > ady) {
			newedge.a = 1.0f;
			newedge.b = dy / dx;
			newedge.c /= dx;// set formula of line, with x fixed to 1
		} else {
			newedge.b = 1.0f;
			newedge.a = dx / dy;
			newedge.c /= dy;// set formula of line, with y fixed to 1
		}
		newedge.indices[0] = indices.get(s1);
		newedge.indices[1] = indices.get(s2);
		return (newedge);
	}

	private VoronoiHalfedge2D HEcreate(final VoronoiEdge2D e, final int pm) {
		VoronoiHalfedge2D result;
		result = new VoronoiHalfedge2D();
		result.voronoiEdge = e;
		result.ELpm = pm;
		result.nextHalfedgeInPointQueue = null;
		result.vertex = null;
		return (result);
	}

	private WB_Point2d leftreg(final VoronoiHalfedge2D he) {
		if (he.voronoiEdge == null) {
			return (bottomsite);
		}
		return (he.ELpm == leftEnd ? he.voronoiEdge.betweenSites[leftEnd]
				: he.voronoiEdge.betweenSites[rightEnd]);
	}

	private void clipToBoundary(final VoronoiEdge2D e) {
		double pxmin, pxmax, pymin, pymax;
		WB_Point2d s1, s2;
		double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

		x1 = e.betweenSites[0].x;
		x2 = e.betweenSites[1].x;
		y1 = e.betweenSites[0].y;
		y2 = e.betweenSites[1].y;

		pxmin = borderMinX;
		pxmax = borderMaxX;
		pymin = borderMinY;
		pymax = borderMaxY;

		if (e.a == 1.0 && e.b >= 0.0) {
			s1 = e.endPoints[1];
			s2 = e.endPoints[0];
		} else {
			s1 = e.endPoints[0];
			s2 = e.endPoints[1];
		}

		if (e.a == 1.0) {
			y1 = pymin;
			if (s1 != null && s1.y > pymin) {
				y1 = s1.y;
			}
			if (y1 > pymax) {
				y1 = pymax;
			}
			x1 = e.c - e.b * y1;
			y2 = pymax;
			if (s2 != null && s2.y < pymax) {
				y2 = s2.y;
			}

			if (y2 < pymin) {
				y2 = pymin;
			}
			x2 = (e.c) - (e.b) * y2;
			if (((x1 > pxmax) & (x2 > pxmax)) | ((x1 < pxmin) & (x2 < pxmin))) {
				return;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
				y1 = (e.c - x1) / e.b;
			}
			if (x1 < pxmin) {
				x1 = pxmin;
				y1 = (e.c - x1) / e.b;
			}
			if (x2 > pxmax) {
				x2 = pxmax;
				y2 = (e.c - x2) / e.b;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
				y2 = (e.c - x2) / e.b;
			}
		} else {
			x1 = pxmin;
			if (s1 != null && s1.x > pxmin) {
				x1 = s1.x;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
			}
			y1 = e.c - e.a * x1;
			x2 = pxmax;
			if (s2 != null && s2.x < pxmax) {
				x2 = s2.x;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
			}
			y2 = e.c - e.a * x2;
			if (((y1 > pymax) & (y2 > pymax)) | ((y1 < pymin) & (y2 < pymin))) {
				return;
			}
			if (y1 > pymax) {
				y1 = pymax;
				x1 = (e.c - y1) / e.a;
			}
			if (y1 < pymin) {
				y1 = pymin;
				x1 = (e.c - y1) / e.a;
			}
			if (y2 > pymax) {
				y2 = pymax;
				x2 = (e.c - y2) / e.a;
			}
			if (y2 < pymin) {
				y2 = pymin;
				x2 = (e.c - y2) / e.a;
			}
		}
		final WB_IndexedBisector2D ib = new WB_IndexedBisector2D();
		ib.start = new WB_Point2d(x1, y1);
		ib.end = new WB_Point2d(x2, y2);
		ib.i = e.indices[0];
		ib.j = e.indices[1];
		allEdges.add(ib);
	}

	private void endpoint(final VoronoiEdge2D e, final int lr,
			final WB_Point2d s) {
		e.endPoints[lr] = s;
		if (e.endPoints[rightEnd - lr] == null) {
			return;
		}
		clipToBoundary(e);
	}

	/* returns 1 if p is to right of halfedge e */
	private boolean isPointToRightOfEdge(final VoronoiHalfedge2D el,
			final WB_Point2d p) {
		VoronoiEdge2D e;
		WB_Point2d topsite;
		boolean toRightOfSite;
		boolean above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;

		e = el.voronoiEdge;
		topsite = e.betweenSites[1];
		if (p.x > topsite.x) {
			toRightOfSite = true;
		} else {
			toRightOfSite = false;
		}
		if (toRightOfSite && el.ELpm == leftEnd) {
			return (true);
		}
		if (!toRightOfSite && el.ELpm == rightEnd) {
			return (false);
		}

		if (e.a == 1.0) {
			dyp = p.y - topsite.y;
			dxp = p.x - topsite.x;
			fast = false;
			if ((!toRightOfSite & (e.b < 0.0)) | (toRightOfSite & (e.b >= 0.0))) {
				above = dyp >= e.b * dxp;
				fast = above;
			} else {
				above = p.x + p.y * e.b > e.c;
				if (e.b < 0.0) {
					above = !above;
				}
				if (!above) {
					fast = true;
				}
			}
			if (!fast) {
				dxs = topsite.x - (e.betweenSites[0]).x;
				above = e.b * (dxp * dxp - dyp * dyp) < dxs * dyp
						* (1.0 + 2.0 * dxp / dxs + e.b * e.b);
				if (e.b < 0.0) {
					above = !above;
				}
			}
		} else /* e.b==1.0 */
		{
			yl = e.c - e.a * p.x;
			t1 = p.y - yl;
			t2 = p.x - topsite.x;
			t3 = yl - topsite.y;
			above = t1 * t1 > t2 * t2 + t3 * t3;
		}
		return (el.ELpm == leftEnd ? above : !above);
	}

	private WB_Point2d rightreg(final VoronoiHalfedge2D he) {
		if (he.voronoiEdge == null) {
			// if this halfedge has no edge, return the bottom site (whatever
			// that is)
			return (bottomsite);
		}

		// if the ELpm field is zero, return the site 0 that this edge bisects,
		// otherwise return site number 1
		return (he.ELpm == leftEnd ? he.voronoiEdge.betweenSites[rightEnd]
				: he.voronoiEdge.betweenSites[leftEnd]);
	}

	private WB_Point2d intersect(final VoronoiHalfedge2D el1,
			final VoronoiHalfedge2D el2) {
		VoronoiEdge2D e1, e2, e;
		VoronoiHalfedge2D el;
		double d, xint, yint;
		boolean right_of_site;
		WB_Point2d v;

		e1 = el1.voronoiEdge;
		e2 = el2.voronoiEdge;
		if (e1 == null || e2 == null) {
			return null;
		}

		// if the two edges bisect the same parent, return null
		if (e1.betweenSites[1] == e2.betweenSites[1]) {
			return null;
		}

		d = e1.a * e2.b - e1.b * e2.a;
		if (-1.0e-10 < d && d < 1.0e-10) {
			return null;
		}

		xint = (e1.c * e2.b - e2.c * e1.b) / d;
		yint = (e2.c * e1.a - e1.c * e2.a) / d;

		if ((e1.betweenSites[1].y < e2.betweenSites[1].y)
				|| (e1.betweenSites[1].y == e2.betweenSites[1].y && e1.betweenSites[1].x < e2.betweenSites[1].x)) {
			el = el1;
			e = e1;
		} else {
			el = el2;
			e = e2;
		}

		right_of_site = xint >= e.betweenSites[1].x;
		if ((right_of_site && el.ELpm == leftEnd)
				|| (!right_of_site && el.ELpm == rightEnd)) {
			return null;
		}

		// create a new site at the point of intersection - this is a new vector
		// event waiting to happen
		v = new WB_Point2d();
		v.x = xint;
		v.y = yint;
		return (v);
	}
}
