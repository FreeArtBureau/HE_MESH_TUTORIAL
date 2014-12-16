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


/**
 * A halfedge is a directed segment that represents one side of an edge in
 * the triangulation. Each halfedge has pointers to an origin point,
 * the next halfedge, and its "sibling" halfedge, which points in the opposite
 * direction but shares the same edge of the triangulation.
 *
 * A valid triangulation will contain a 3-cycle of halfedges for each face,
 * every interior edge will correspond to a sibling pair of halfeldges, and
 * every boundary edge will correspond to a single halfedge with a null
 * sibling pointer.
 *
 * @author Mark Howison
 */
public class Tri_HalfEdge {
	public final static int	AUXILARY		= 0;
	public final static int	BOUNDARY		= 1;
	public final static int	CONSTRAINT		= 2;

	public final static int	FLAGS			= 3;
	public final static int	FLAG_ALGORITHM	= 0;
	public final static int	FLAG_DRAW		= 1;
	public final static int	FLAG_READ		= 2;

	public Tri_Point		origin			= null;
	public Tri_HalfEdge		next			= null;
	public Tri_HalfEdge		sibling			= null;

	int						type			= AUXILARY;

	private boolean			flagged[]		= new boolean[FLAGS];

	/**
	 * Constructs a halfedge with null pointers.
	 */
	public Tri_HalfEdge() {
	}

	/**
	 * Constructs a halfedge with origin <tt>p</tt>, and null next and sibling
	 * pointers.
	 *
	 * @param p
	 */
	public Tri_HalfEdge(final Tri_Point p) {
		origin = p;
	}

	/**
	 * Constructs a halfedge with origin <tt>p</tt>, type <tt>t</tt>, and
	 * null next and sibling pointers.
	 *
	 * @param p
	 * @param t
	 */
	public Tri_HalfEdge(final Tri_Point p, final int t) {
		this(p);
		type = t;
	}

	/**
	 * Shallow copies <tt>he</tt>. All pointers are copied, but not the
	 * underlying objects.
	 *
	 * @param he
	 */
	public Tri_HalfEdge(final Tri_HalfEdge he) {
		origin = he.origin;
		next = he.next;
		sibling = he.sibling;
		type = he.type;
		flagged = he.flagged;
	}

	/**
	 * Gets the next halfedge.
	 *
	 * @return
	 */
	public Tri_HalfEdge getNext() {
		return next;
	}

	/**
	 * Gets the previous halfedge whose next pointer points to this halfedge.
	 *
	 * NOTE: In a valid triangulation, this could be accomplished by following
	 * three next pointers. However, this method is robust for non-triangular
	 * regions, and instead uses a potentially infinite while loop.
	 *
	 * @return
	 */
	public Tri_HalfEdge getPrev() {
		Tri_HalfEdge hePrev = next;
		while (true) {
			if (hePrev.next == this) {
				break;
			}
			hePrev = hePrev.next;
		}
		return hePrev;
	}

	/**
	 * Gets this halfedge's type (interior, boundary, constrained etc.). Use
	 * the static int flags to specify type.
	 *
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the next halfedge pointer to <tt>he</tt>.
	 *
	 * @param he
	 */
	public void setNext(final Tri_HalfEdge he) {
		next = he;
	}

	/**
	 * Sets the sibling halfedge pointer to <tt>he</tt>.
	 *
	 * @param he
	 */
	public void setSibling(final Tri_HalfEdge he) {
		sibling = he;
	}

	/**
	 * Sets this halfedge's type (interior, boundary, constrained etc.). Use
	 * the static int flags to specify type.
	 *
	 * @param type
	 */
	public void setType(final int type) {
		this.type = type;
		if (sibling != null) {
			sibling.type = type;
		}
	}

	/**
	 * Tests if this halfedges is of <tt>type</tt>. Use the static int flags
	 * to specify type.
	 *
	 * @param type
	 * @return
	 */
	public boolean isType(final int type) {
		if (this.type == type) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests whether this halfedge's <tt>flag</tt> is set to true.
	 *
	 * Flags can be used to mark which edges or faces of a triangulation
	 * have been already processed by a procedure or algorithm.
	 *
	 * @param flag
	 * @return
	 */
	public boolean isFlagged(final int flag) {
		return flagged[flag];
	}

	/**
	 * Sets this halfedge's type to CONSTRAINT.
	 */
	public void constrain() {
		type = CONSTRAINT;
	}

	/**
	 * Sets this halfedge's <tt>flag</tt> to true. Does not change the
	 * sibling's flag.
	 *
	 * @param flag
	 */
	public void flag(final int flag) {
		flagged[flag] = true;
	}

	/**
	 * Sets this halfedge and its sibling's <tt>flag</tt> to true.
	 *
	 * @param flag
	 */
	public void flagEdge(final int flag) {
		flagged[flag] = true;
		if (sibling != null) {
			sibling.flagged[flag] = true;
		}
	}

	/**
	 * Sets this halfedge's <tt>flag</tt> to false. Does not change the
	 * sibling's flag.
	 *
	 * @param flag
	 */
	public void unflag(final int flag) {
		flagged[flag] = false;
	}
}
