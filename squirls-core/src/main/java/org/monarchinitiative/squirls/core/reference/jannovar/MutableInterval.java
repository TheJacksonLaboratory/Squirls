package org.monarchinitiative.squirls.core.reference.jannovar;

/**
 * Mutable half-open interval, for incremental building of {@link Interval} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
class MutableInterval<T> implements Comparable<MutableInterval<T>> {

	/**
	 * start point of the interval (inclusive)
	 */
	private int begin;

	/**
	 * end point of the interval (exclusive)
	 */
	private int end;

	/**
	 * the value stored for the Interval
	 */
	private T value;

	/**
	 * the maximum of this nodes {@link #end} and both of its children's {@link #end}
	 */
	private int maxEnd;

	MutableInterval(int begin, int end, T value, int maxEnd) {
		this.begin = begin;
		this.end = end;
		this.value = value;
		this.maxEnd = maxEnd;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getMaxEnd() {
		return maxEnd;
	}

	public void setMaxEnd(int maxEnd) {
		this.maxEnd = maxEnd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + maxEnd;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof Interval<?>) {
			Interval<?> other = (Interval<?>) obj;
			if (begin != other.getBegin())
				return false;
			if (end != other.getEnd())
				return false;
			if (maxEnd != other.getMaxEnd())
				return false;
			if (value == null) {
				return other.getValue() == null;
			} else
				return value.equals(other.getValue());
		}
		return true;
	}

	public int compareTo(MutableInterval<T> o) {
		final int result = (begin - o.begin);
		if (result == 0)
			return (end - o.end);
		return result;
	}

}
