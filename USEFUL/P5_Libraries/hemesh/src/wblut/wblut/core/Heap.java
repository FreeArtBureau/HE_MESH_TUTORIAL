/**
 * 
 */
package wblut.core;

import java.util.List;

import javolution.util.FastList;

public class Heap<E> {

	private final List<E>		heap;
	private final List<Double>	keys;

	public Heap() {
		heap = new FastList<E>();
		keys = new FastList<Double>();
	}

	public void push(final Double key, final E obj) {

		heap.add(obj);
		keys.add(key);
		pushUp(heap.size() - 1);
	}

	public E pop() {
		if (heap.size() > 0) {
			swap(0, heap.size() - 1);
			final E result = heap.remove(heap.size() - 1);
			keys.remove(heap.size());
			pushDown(0);
			return result;
		} else {
			return null;
		}
	}

	public E getFirst() {
		return heap.get(0);
	}

	public E get(final int index) {
		return heap.get(index);
	}

	public int size() {
		return heap.size();
	}

	protected int parent(final int i) {
		return (i - 1) / 2;
	}

	protected int left(final int i) {
		return 2 * i + 1;
	}

	protected int right(final int i) {
		return 2 * i + 2;
	}

	protected boolean hasPriority(final int i, final int j) {
		return keys.get(i) <= keys.get(j);
	}

	protected void swap(final int i, final int j) {
		final E tmp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, tmp);
		final Double tmpv = keys.get(i);
		keys.set(i, keys.get(j));
		keys.set(j, tmpv);
	}

	public void pushDown(final int i) {
		final int left = left(i);
		final int right = right(i);
		int highest = i;

		if (left < heap.size() && !hasPriority(highest, left)) {
			highest = left;
		}
		if (right < heap.size() && !hasPriority(highest, right)) {
			highest = right;
		}

		if (highest != i) {
			swap(highest, i);
			pushDown(highest);
		}
	}

	public void pushUp(int i) {
		while (i > 0 && !hasPriority(parent(i), i)) {
			swap(parent(i), i);
			i = parent(i);
		}
	}

	public void remove(final E obj) {
		final int i = heap.indexOf(obj);
		if (i > -1) {
			heap.remove(i);
			keys.remove(i);
		}
		rebuild();

	}

	public void removeNoRebuild(final E obj) {
		final int i = heap.indexOf(obj);
		if (i > -1) {
			heap.remove(i);
			keys.remove(i);
		}

	}

	public void rebuild() {
		final List<E> cheap = new FastList<E>(heap.size());
		final List<Double> ckeys = new FastList<Double>(keys.size());
		cheap.addAll(heap);
		ckeys.addAll(keys);
		heap.clear();
		keys.clear();
		for (int i = 0; i < cheap.size(); i++) {
			push(ckeys.get(i), cheap.get(i));
		}

	}

	@Override
	public String toString() {
		final StringBuffer s = new StringBuffer("Heap:\n");
		int rowStart = 0;
		int rowSize = 1;
		for (int i = 0; i < heap.size(); i++) {
			if (i == rowStart + rowSize) {
				s.append('\n');
				rowStart = i;
				rowSize *= 2;
			}
			s.append(get(i));
			s.append(" ");
		}
		return s.toString();
	}

}
