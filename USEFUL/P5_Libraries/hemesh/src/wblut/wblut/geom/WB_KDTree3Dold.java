package wblut.geom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import wblut.WB_Epsilon;



/**
 * @author NOT Frederik Vanhoutte
 * 
 * I barely understand this code, just sufficient to tweak to my needs
 * 
 * This is a tweaked copy of code provided on
 * http://www.savarese.com/software/libssrckdtree-j/ Copyright 2010 Savarese
 * Software Research Corporation Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.savarese.com/software/ApacheLicense-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * 
 * Original comments:
 * 
 * All the view classes are inefficient for anything other than iteration.
 *
 * A KD-tree divides a k-dimensional space relative to the points it
 * contains by storing them in a binary tree, discriminating by a
 * different dimension at each level of the tree.  It allows efficient
 * point data retrieval (<em>O(lg(n))</em>) and range searching.</p>
 *
 * WB_KDTree conforms to the java.util.Map interface except that
 * Iterator.remove is not supported by the returned views.</p>
 */
public class WB_KDTree3Dold<V> {
	private final int	dim	= 3;
	private int			treeSize, hashCode;
	private WB_KDNode	treeRoot;

	private final class WB_KDNode implements Map.Entry<WB_Point3d, V> {
		int			nodeCoordinate;
		WB_Point3d	nodePoint;
		V			nodeValue;
		WB_KDNode	lowNode, highNode;

		WB_KDNode(final int coord, final WB_Point3d point, final V value) {
			nodePoint = point;
			nodeValue = value;
			lowNode = highNode = null;
			nodeCoordinate = coord;
		}

		@Override
		public boolean equals(final Object o) {
			final WB_KDNode node = (WB_KDNode) o;

			if (node == this) {
				return true;
			}

			return ((getKey() == null ? node.getKey() == null : getKey()
					.equals(node.getKey())) && (getValue() == null ? node
					.getValue() == null : getValue().equals(node.getValue())));
		}

		public WB_Point3d getKey() {
			return nodePoint;
		}

		public V getValue() {
			return nodeValue;
		}

		// Only call if the node is in the tree.
		public V setValue(final V value) {
			final V old = nodeValue;
			hashCode -= hashCode();
			nodeValue = value;
			hashCode += hashCode();
			return old;
		}

		@Override
		public int hashCode() {
			return ((getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0
					: getValue().hashCode()));
		}
	}

	private final class MapEntryIterator implements
			Iterator<Map.Entry<WB_Point3d, V>> {
		LinkedList<WB_KDNode>	nodeStack;
		WB_KDNode				nextNode;
		WB_Point3d				lowerPoint, upperPoint;

		MapEntryIterator(final WB_Point3d lower, final WB_Point3d upper) {
			nodeStack = new LinkedList<WB_KDNode>();
			lowerPoint = lower;
			upperPoint = upper;
			nextNode = null;

			if (treeRoot != null) {
				nodeStack.addLast(treeRoot);
			}
			next();
		}

		MapEntryIterator(final WB_AABB AABB) {
			nodeStack = new LinkedList<WB_KDNode>();
			lowerPoint = AABB.getMin();
			upperPoint = AABB.getMax();
			nextNode = null;

			if (treeRoot != null) {
				nodeStack.addLast(treeRoot);
			}
			next();
		}

		MapEntryIterator() {
			this(null, null);
		}

		public boolean hasNext() {
			return (nextNode != null);
		}

		public Map.Entry<WB_Point3d, V> next() {
			final WB_KDNode old = nextNode;

			while (!nodeStack.isEmpty()) {
				final WB_KDNode node = nodeStack.removeLast();
				final int coord = node.nodeCoordinate;

				if ((upperPoint == null || node.nodePoint.get(coord) <= upperPoint
						.get(coord)) && node.highNode != null) {
					nodeStack.addLast(node.highNode);
				}

				if ((lowerPoint == null || node.nodePoint.get(coord) > lowerPoint
						.get(coord)) && node.lowNode != null) {
					nodeStack.addLast(node.lowNode);
				}

				if (isInRange(node.nodePoint, lowerPoint, upperPoint)) {
					nextNode = node;
					return old;
				}
			}

			nextNode = null;

			return old;
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
	}

	private final class KeyIterator implements Iterator<WB_Point3d> {
		MapEntryIterator	iterator;

		KeyIterator(final MapEntryIterator it) {
			iterator = it;
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public WB_Point3d next() {
			final Map.Entry<WB_Point3d, V> next = iterator.next();
			return (next == null ? null : next.getKey());
		}

		public void remove() throws UnsupportedOperationException {
			iterator.remove();
		}
	}

	private final class ValueIterator implements Iterator<V> {
		MapEntryIterator	iterator;

		ValueIterator(final MapEntryIterator it) {
			iterator = it;
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public V next() {
			final Map.Entry<WB_Point3d, V> next = iterator.next();
			return (next == null ? null : next.getValue());
		}

		public void remove() throws UnsupportedOperationException {
			iterator.remove();
		}
	}

	private abstract class CollectionView<E> implements Collection<E> {

		public boolean add(final E o) throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(final Collection<? extends E> c)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			WB_KDTree3Dold.this.clear();
		}

		public boolean containsAll(final Collection<?> c) {
			for (final Object o : c) {
				if (!contains(o)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode() {
			return WB_KDTree3Dold.this.hashCode();
		}

		public boolean isEmpty() {
			return WB_KDTree3Dold.this.isEmpty();
		}

		public int size() {
			return WB_KDTree3Dold.this.size();
		}

		public Object[] toArray() {
			final Object[] obja = new Object[size()];
			int i = 0;

			for (final E e : this) {
				obja[i] = e;
				++i;
			}

			return obja;
		}

		public <T> T[] toArray(T[] a) {
			Object[] array = a;

			if (array.length < size()) {
				array = a = (T[]) Array.newInstance(a.getClass()
						.getComponentType(), size());
			}

			if (array.length > size()) {
				array[size()] = null;
			}

			int i = 0;
			for (final E e : this) {
				array[i] = e;
				++i;
			}

			return a;
		}
	}

	private abstract class SetView<E> extends CollectionView<E> implements
			Set<E> {
		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Set)) {
				return false;
			}

			if (o == this) {
				return true;
			}

			final Set<?> set = (Set<?>) o;

			if (set.size() != size()) {
				return false;
			}

			try {
				return containsAll(set);
			} catch (final ClassCastException cce) {
				return false;
			}
		}
	}

	private final class MapEntrySet extends SetView<Map.Entry<WB_Point3d, V>> {
		public boolean contains(final Object o) throws ClassCastException,
				NullPointerException {
			final Map.Entry<WB_Point3d, V> e = (Map.Entry<WB_Point3d, V>) o;
			final WB_KDNode node = getNode(e.getKey());

			if (node == null) {
				return false;
			}

			return e.getValue().equals(node.getValue());
		}

		public Iterator<Map.Entry<WB_Point3d, V>> iterator() {
			return new MapEntryIterator();
		}

		public boolean remove(final Object o) throws ClassCastException {
			final int size = size();
			final Map.Entry<WB_Point3d, V> e = (Map.Entry<WB_Point3d, V>) o;

			WB_KDTree3Dold.this.remove(e.getKey());

			return (size != size());
		}

		public boolean removeAll(final Collection<?> c)
				throws ClassCastException {
			final int size = size();

			for (final Object o : c) {
				final Map.Entry<WB_Point3d, V> e = (Map.Entry<WB_Point3d, V>) o;
				WB_KDTree3Dold.this.remove(e.getKey());
			}

			return (size != size());
		}

		public boolean retainAll(final Collection<?> c)
				throws ClassCastException {
			for (final Object o : c) {
				if (contains(o)) {
					final Collection<Map.Entry<WB_Point3d, V>> col = (Collection<Map.Entry<WB_Point3d, V>>) c;
					clear();
					for (final Map.Entry<WB_Point3d, V> e : col) {
						put(e.getKey(), e.getValue());
					}
					return true;
				}
			}
			return false;
		}
	}

	private final class KeySet extends SetView<WB_Point3d> {

		public boolean contains(final Object o) throws ClassCastException,
				NullPointerException {
			return WB_KDTree3Dold.this.containsKey(o);
		}

		public Iterator<WB_Point3d> iterator() {
			return new KeyIterator(new MapEntryIterator());
		}

		public boolean remove(final Object o) throws ClassCastException {
			final int size = size();
			WB_KDTree3Dold.this.remove(o);
			return (size != size());
		}

		public boolean removeAll(final Collection<?> c)
				throws ClassCastException {
			final int size = size();

			for (final Object o : c) {
				WB_KDTree3Dold.this.remove(o);
			}

			return (size != size());
		}

		public boolean retainAll(final Collection<?> c)
				throws ClassCastException {
			final HashMap<WB_Point3d, V> map = new HashMap<WB_Point3d, V>();
			final int size = size();

			for (final Object o : c) {
				final V val = get(o);

				if (val != null || contains(o)) {
					map.put((WB_Point3d) o, val);
				}
			}

			clear();
			putAll(map);

			return (size != size());
		}
	}

	private final class ValueCollection extends CollectionView<V> {

		public boolean contains(final Object o) throws ClassCastException,
				NullPointerException {
			return WB_KDTree3Dold.this.containsValue(o);
		}

		public Iterator<V> iterator() {
			return new ValueIterator(new MapEntryIterator());
		}

		public boolean remove(final Object o) throws ClassCastException {
			final WB_KDNode node = findValue(treeRoot, o);

			if (node != null) {
				WB_KDTree3Dold.this.remove(node.getKey());
				return true;
			}

			return false;
		}

		public boolean removeAll(final Collection<?> c)
				throws ClassCastException {
			final int size = size();

			for (final Object o : c) {
				WB_KDNode node = findValue(treeRoot, o);

				while (node != null) {
					WB_KDTree3Dold.this.remove(o);
					node = findValue(treeRoot, o);
				}
			}

			return (size != size());
		}

		public boolean retainAll(final Collection<?> c)
				throws ClassCastException {
			final HashMap<WB_Point3d, V> map = new HashMap<WB_Point3d, V>();
			final int size = size();

			for (final Object o : c) {
				WB_KDNode node = findValue(treeRoot, o);

				while (node != null) {
					map.put(node.getKey(), node.getValue());
					node = findValue(treeRoot, o);
				}
			}

			clear();
			putAll(map);

			return (size != size());
		}
	}

	private WB_KDNode getNode(final WB_Point3d point, final WB_KDNode[] parent) {
		int coord;
		WB_KDNode node = treeRoot, current, last = null;
		double c1, c2;

		while (node != null) {
			coord = node.nodeCoordinate;
			c1 = point.get(coord);
			c2 = node.nodePoint.get(coord);
			current = node;

			if (c1 > c2) {
				node = node.highNode;
			} else if (c1 < c2) {
				node = node.lowNode;
			} else if (node.nodePoint.compareTo(point) == 0) {
				if (parent != null) {
					parent[0] = last;
				}
				return node;
			} else {
				node = node.highNode;
			}

			last = current;
		}

		if (parent != null) {
			parent[0] = last;
		}

		return null;
	}

	private WB_KDNode getNode(final WB_Point3d point) {
		return getNode(point, null);
	}

	private WB_KDNode getMinimumNode(final WB_KDNode node, final WB_KDNode p,
			final int coord, final WB_KDNode[] parent) {
		WB_KDNode result;

		if (coord == node.nodeCoordinate) {
			if (node.lowNode != null) {
				return getMinimumNode(node.lowNode, node, coord, parent);
			} else {
				result = node;
			}
		} else {
			WB_KDNode nlow = null, nhigh = null;
			final WB_KDNode[] plow = new WB_KDTree3Dold.WB_KDNode[1], phigh = new WB_KDTree3Dold.WB_KDNode[1];

			if (node.lowNode != null) {
				nlow = getMinimumNode(node.lowNode, node, coord, plow);
			}

			if (node.highNode != null) {
				nhigh = getMinimumNode(node.highNode, node, coord, phigh);
			}

			if (nlow != null && nhigh != null) {
				if (nlow.nodePoint.get(coord) < nhigh.nodePoint.get(coord)) {
					result = nlow;
					parent[0] = plow[0];
				} else {
					result = nhigh;
					parent[0] = phigh[0];
				}
			} else if (nlow != null) {
				result = nlow;
				parent[0] = plow[0];
			} else if (nhigh != null) {
				result = nhigh;
				parent[0] = phigh[0];
			} else {
				result = node;
			}
		}

		if (result == node) {
			parent[0] = p;
		} else if (node.nodePoint.get(coord) < result.nodePoint.get(coord)) {
			result = node;
			parent[0] = p;
		}

		return result;
	}

	private WB_KDNode recursiveRemoveNode(final WB_KDNode node) {
		int coord;

		if (node.lowNode == null && node.highNode == null) {
			return null;
		} else {
			coord = node.nodeCoordinate;
		}

		if (node.highNode == null) {
			node.highNode = node.lowNode;
			node.lowNode = null;
		}

		final WB_KDNode[] parent = new WB_KDTree3Dold.WB_KDNode[1];
		final WB_KDNode newRoot = getMinimumNode(node.highNode, node, coord,
				parent);
		final WB_KDNode child = recursiveRemoveNode(newRoot);

		if (parent[0].lowNode == newRoot) {
			parent[0].lowNode = child;
		} else {
			parent[0].highNode = child;
		}

		newRoot.lowNode = node.lowNode;
		newRoot.highNode = node.highNode;
		newRoot.nodeCoordinate = node.nodeCoordinate;

		return newRoot;
	}

	private WB_KDNode findValue(final WB_KDNode node, final Object value) {
		if (node == null
				|| (value == null ? node.getValue() == null : value.equals(node
						.getValue()))) {
			return node;
		}

		WB_KDNode result;

		if ((result = findValue(node.lowNode, value)) == null) {
			result = findValue(node.highNode, value);
		}

		return result;
	}

	private boolean isInRange(final WB_Point3d point, final WB_Point3d lower,
			final WB_Point3d upper) {
		Double coordinate1, coordinate2 = null, coordinate3 = null;

		if (lower != null || upper != null) {

			for (int i = 0; i < dim; ++i) {
				coordinate1 = new Double(point.get(i));
				if (lower != null) {
					coordinate2 = new Double(lower.get(i));
				}
				if (upper != null) {
					coordinate3 = new Double(upper.get(i));
				}
				if ((coordinate2 != null && coordinate1.compareTo(coordinate2) < 0)
						|| (coordinate3 != null && coordinate1
								.compareTo(coordinate3) > 0)) {
					return false;
				}
			}
		}

		return true;
	}

	public WB_KDTree3Dold() {
		clear();
	}

	// Begin Map interface methods

	/**
	 * Removes all elements from the container, leaving it empty.
	 */
	public void clear() {
		treeRoot = null;
		treeSize = hashCode = 0;
	}

	/**
	 * Returns true if the container contains a mapping for the specified key.
	 *
	 * @param key The point key to search for.
	 * @return true if the container contains a mapping for the specified key.
	 * @exception ClassCastException if the key is not an instance of WB_Point.
	 */
	public boolean containsKey(final Object key) throws ClassCastException {
		return (getNode((WB_Point3d) key) != null);
	}

	/**
	 * Returns true if the container contains a mapping with the specified value.
	 * Note: this is very inefficient for KDTrees because it requires searching
	 * the entire tree.
	 *
	 * @param value The value to search for.
	 * @return true If the container contains a mapping with the specified value.
	 */
	public boolean containsValue(final Object value) {
		return (findValue(treeRoot, value) != null);
	}

	/**
	 * Returns a Set view of the point to value mappings in the WB_KDTree.
	 * Modifications to the resulting set will be reflected in the WB_KDTree
	 * and vice versa, except that {@code Iterator.remove} is not supported.
	 *
	 * @return A Set view of the point to value mappings in the WB_KDTree.
	 */
	public Set<Map.Entry<WB_Point3d, V>> entrySet() {
		return new MapEntrySet();
	}

	/**
	 * Returns true if the object contains the same mappings, false if not.
	 *
	 * @param o The object to test for equality.
	 * @return true if the object contains the same mappings, false if not.
	 */
	@Override
	public boolean equals(final Object o) throws ClassCastException {
		if (!(o instanceof Map)) {
			return false;
		}

		if (o == this) {
			return true;
		}

		final Map map = (Map) o;

		return (entrySet().equals(map.entrySet()));
	}

	/**
	 * Retrieves the value at the given location.
	 *
	 * @param point The location from which to retrieve the value.
	 * @return The value at the given location, or null if no value is present.
	 * @exception ClassCastException If the given point is not of the
	 * expected type.
	 */
	public V get(final Object point) throws ClassCastException {
		final WB_KDNode node = getNode((WB_Point3d) point);

		return (node == null ? null : node.getValue());
	}

	/**
	 * Returns the hash code value for this map.
	 *
	 * @return The sum of the hash codes of all of the map entries.
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Returns true if the container has no elements, false if it
	 * contains one or more elements.
	 *
	 * @return true if the container has no elements, false if it
	 * contains one or more elements.
	 */
	public boolean isEmpty() {
		return (treeRoot == null);
	}

	/**
	 * Returns a Set view of the point keys for the mappings in the
	 * WB_KDTree.  Changes to the Set are reflected in the WB_KDTree and vice
	 * versa, except that {@code Iterator.remove} is not supported.
	 *
	 * @return A Set view of the point keys for the mappings in the WB_KDTree.
	 */
	public Set<WB_Point3d> keySet() {
		return new KeySet();
	}

	/**
	 * Inserts a point value pair into the tree, preserving the
	 * spatial ordering.
	 *
	 * @param point The point serving as a key.
	 * @param value The value to insert at the point.
	 * @return The old value if an existing value is replaced by the
	 * inserted value.
	 */
	public V put(final WB_Point3d point, final V value) {
		final WB_KDNode[] parent = new WB_KDTree3Dold.WB_KDNode[1];
		WB_KDNode node = getNode(point, parent);
		V old = null;

		if (node != null) {
			old = node.getValue();
			hashCode -= node.hashCode();
			node.nodeValue = value;
		} else {
			if (parent[0] == null) {
				node = treeRoot = new WB_KDNode(0, point, value);
			} else {
				final int coord = parent[0].nodeCoordinate;

				if (point.get(coord) >= parent[0].nodePoint.get(coord)) {
					node = parent[0].highNode = new WB_KDNode(
							(coord + 1) % dim, point, value);
				} else {
					node = parent[0].lowNode = new WB_KDNode((coord + 1) % dim,
							point, value);
				}
			}

			++treeSize;
		}

		hashCode += node.hashCode();

		return old;
	}

	/**
	 * Copies all of the point-value mappings from the given Map into the WB_KDTree.
	 *
	 * @param map The Map from which to copy the mappings.
	 */
	public void putAll(final Map<? extends WB_Point3d, ? extends V> map) {
		for (final Map.Entry<? extends WB_Point3d, ? extends V> pair : map
				.entrySet()) {
			put(pair.getKey(), pair.getValue());
		}
	}

	/**
	 * Removes the point-value mapping corresponding to the given point key.
	 *
	 * @param key The point key of the mapping to remove.
	 * @return The value part of the mapping, if a mapping existed and
	 * was removed.  Null if not.
	 * @exception ClassCastException If the key is not an instance of WB_Point.
	 */
	public V remove(final Object key) throws ClassCastException {
		final WB_KDNode[] parent = new WB_KDTree3Dold.WB_KDNode[1];
		WB_KDNode node = getNode((WB_Point3d) key, parent);
		V old = null;

		if (node != null) {
			final WB_KDNode child = node;

			node = recursiveRemoveNode(child);

			if (parent[0] == null) {
				treeRoot = node;
			} else if (child == parent[0].lowNode) {
				parent[0].lowNode = node;
			} else if (child == parent[0].highNode) {
				parent[0].highNode = node;
			}

			--treeSize;
			hashCode -= child.hashCode();
			old = child.getValue();
		}

		return old;
	}

	/**
	 * Returns the number of point-value mappings in the WB_KDTree.
	 *
	 * @return The number of point-value mappings in the WB_KDTree.
	 */
	public int size() {
		return treeSize;
	}

	/**
	 * Returns a Collection view of the values contained in the WB_KDTree.
	 * Changes to the Collection are reflected in the WB_KDTree and vice versa.
	 * Note: the resulting Collection is very inefficient.
	 *
	 * @return A Collection view of the values contained in the WB_KDTree.
	 */
	public Collection<V> values() {
		return new ValueCollection();
	}

	// End Map interface methods

	public Iterator<Map.Entry<WB_Point3d, V>> iterator(final WB_Point3d lower,
			final WB_Point3d upper) {
		return new MapEntryIterator(lower, upper);
	}

	public Iterator<Map.Entry<WB_Point3d, V>> iterator(final WB_AABB AABB) {
		return new MapEntryIterator(AABB);
	}

	int fillArray(final WB_KDNode[] a, int index, final WB_KDNode node) {
		if (node == null) {
			return index;
		}
		a[index] = node;
		index = fillArray(a, index + 1, node.lowNode);
		return fillArray(a, index, node.highNode);
	}

	final class NodeComparator implements Comparator<WB_KDNode> {
		int	nodeCoordinate	= 0;

		void setDiscriminator(final int val) {
			nodeCoordinate = val;
		}

		int getDiscriminator() {
			return nodeCoordinate;
		}

		public int compare(final WB_KDNode n1, final WB_KDNode n2) {
			return (n1.nodePoint.get(nodeCoordinate) < n2.nodePoint
					.get(nodeCoordinate)) ? -1
					: (n1.nodePoint.get(nodeCoordinate) > n2.nodePoint
							.get(nodeCoordinate)) ? 1 : 0;
		}
	}

	WB_KDNode optimize(final WB_KDNode[] nodes, final int begin, final int end,
			final NodeComparator comp) {
		WB_KDNode midpoint = null;
		final int size = end - begin;

		if (size > 1) {
			int nth = begin + (size >> 1);
			int nthprev = nth - 1;
			int d = comp.getDiscriminator();

			Arrays.sort(nodes, begin, end, comp);

			while (nth > begin
					&& nodes[nth].nodePoint.get(d) == nodes[nthprev].nodePoint
							.get(d)) {
				--nth;
				--nthprev;
			}

			midpoint = nodes[nth];
			midpoint.nodeCoordinate = d;

			if (++d >= dim) {
				d = 0;
			}

			comp.setDiscriminator(d);

			midpoint.lowNode = optimize(nodes, begin, nth, comp);

			comp.setDiscriminator(d);

			midpoint.highNode = optimize(nodes, nth + 1, end, comp);
		} else if (size == 1) {
			midpoint = nodes[begin];
			midpoint.nodeCoordinate = comp.getDiscriminator();
			midpoint.lowNode = midpoint.highNode = null;
		}

		return midpoint;
	}

	/**
	 * Optimizes the performance of future search operations by balancing the
	 * WB_KDTree.  The balancing operation is relatively expensive, but can
	 * significantly improve the performance of searches.  Usually, you
	 * don't have to optimize a tree which contains random key values
	 * inserted in a random order.
	 */
	public void optimize() {
		if (isEmpty()) {
			return;
		}

		final WB_KDNode[] nodes = (WB_KDNode[]) Array.newInstance(
				WB_KDNode.class, size());
		fillArray(nodes, 0, treeRoot);

		treeRoot = optimize(nodes, 0, nodes.length, new NodeComparator());
	}

	public ArrayList<Entry<WB_Point3d, V>> getAllNodesInAABB(final WB_AABB AABB) {
		final ArrayList<Entry<WB_Point3d, V>> nodes = new ArrayList<Entry<WB_Point3d, V>>();
		final Iterator<Entry<WB_Point3d, V>> range = iterator(AABB);
		while (range.hasNext()) {
			nodes.add(range.next());

		}
		return nodes;
	}

	// stuff for nearest neighbor searches
	private final class SqDistComparator implements
			Comparator<WB_KDNeighbor<V>> {
		// Invert relationship so priority queue keeps highest on top.
		public int compare(final WB_KDNeighbor<V> n1, final WB_KDNeighbor<V> n2) {
			final double d1 = n1.sqDistance();
			final double d2 = n2.sqDistance();

			if (d1 < d2) {
				return 1;
			} else if (d1 > d2) {
				return -1;
			}

			return 0;
		}

		@Override
		public boolean equals(final Object obj) {
			return (obj != null && obj == this);
		}

	}

	private boolean							__omitQueryPoint;
	private int								__numNeighbors;
	private double							__minDistance;
	private PriorityQueue<WB_KDNeighbor<V>>	__pq;
	private WB_Point3d						__query;

	private void find(final WB_KDTree3Dold<V>.WB_KDNode node) {
		if (node == null) {
			return;
		}

		final int discriminator = node.nodeCoordinate;
		final WB_Point3d point = node.getKey();
		double d2 = WB_Distance.sqDistance(__query, point);

		if (d2 < __minDistance
				&& (!WB_Epsilon.isZeroSq(d2) || !__omitQueryPoint)) {
			if (__pq.size() == __numNeighbors) {
				__pq.poll();
				__pq.add(new WB_KDNeighbor<V>(d2, node));
				__minDistance = __pq.peek().sqDistance();
			} else {
				__pq.add(new WB_KDNeighbor<V>(d2, node));
				if (__pq.size() == __numNeighbors) {
					__minDistance = __pq.peek().sqDistance();
				}
			}
		}

		final double dp = __query.get(discriminator) - point.get(discriminator);

		d2 = dp * dp;

		if (dp < 0) {
			find(node.lowNode);
			if (d2 < __minDistance) {
				find(node.highNode);
			}
		} else {
			find(node.highNode);
			if (d2 < __minDistance) {
				find(node.lowNode);
			}
		}
	}

	/**
	* Finds the k-nearest neighbors to a query point within a KDTree instance.
	* The neighbors are returned as an array of {@link WB_KDNeighbor} instances, sorted
	* from nearest to farthest.
	*
	* @param queryPoint The query point.
	* @param numNeighbors The number of nearest neighbors to find.  This should
	*        be a positive value.  Non-positive values result in no neighbors
	*        being found.
	* @param omitQueryPoint If true, point-value mappings at a distance of
	*        zero are omitted from the result.  If false, mappings at a
	*        distance of zero are included.
	* @return An array containing the nearest neighbors and their distances
	*         sorted by least distance to greatest distance.  If no neighbors
	*         are found, the array will have a length of zero.
	*/
	public WB_KDNeighbor<V>[] getNearestNeighbors(final WB_Point3d queryPoint,
			final int numNeighbors, final boolean omitQueryPoint) {
		__omitQueryPoint = omitQueryPoint;
		__numNeighbors = numNeighbors;
		__query = queryPoint;
		__minDistance = Double.POSITIVE_INFINITY;

		__pq = new PriorityQueue<WB_KDNeighbor<V>>(numNeighbors,
				new SqDistComparator());

		if (numNeighbors > 0) {
			find(treeRoot);
		}

		final WB_KDNeighbor<V>[] neighbors = new WB_KDNeighbor[__pq.size()];

		__pq.toArray(neighbors);
		Arrays.sort(neighbors);

		__pq = null;
		__query = null;

		return neighbors;
	}

	/**
	* Same as {@link #get get(queryPoint, numNeighbors, true)}.
	*/
	public WB_KDNeighbor<V>[] getNearestNeighbors(final WB_Point3d queryPoint,
			final int numNeighbors) {
		return getNearestNeighbors(queryPoint, numNeighbors, true);
	}

}
