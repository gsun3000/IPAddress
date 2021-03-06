/*
 * Copyright 2017 Sean C Foley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     or at
 *     https://github.com/seancfoley/IPAddress/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package inet.ipaddr.format;


/**
 * A combination of two or more IP address segments.
 * 
 * @author sfoley
 *
 */
public abstract class IPAddressJoinedSegments extends IPAddressDivision {
	
	private static final long serialVersionUID = 3L;

	protected final int joinedCount;
	protected final long value; //the lower value
	protected final long upperValue; //the upper value of a CIDR or other type of range, if not a range it is the same as value
	
	public IPAddressJoinedSegments(int joinedCount, int value) {
		if(value < 0) {
			throw new IllegalArgumentException();
		}
		this.value = this.upperValue = value;
		this.joinedCount = joinedCount;
		if(joinedCount <= 0) {
			throw new IllegalArgumentException();
		}
	}

	public IPAddressJoinedSegments(int joinedCount, long value, Integer segmentPrefixLength) {
		this(joinedCount, value, value, segmentPrefixLength);
	}

	public IPAddressJoinedSegments(int joinedCount, long lower, long upper, Integer segmentPrefixLength) {
		super(segmentPrefixLength);
		if(lower < 0 || upper < 0) {
			throw new IllegalArgumentException();
		}
		if(lower > upper) {
			long tmp = lower;
			lower = upper;
			upper = tmp;
		}
		this.value = lower;
		this.upperValue = upper;
		this.joinedCount = joinedCount;
		if(joinedCount <= 0) {
			throw new IllegalArgumentException();
		}
	}
	
	public int getJoinedCount() {
		return joinedCount;
	}

	@Override
	public long getLowerValue() {
		return value;
	}

	@Override
	public long getUpperValue() {
		return upperValue;
	}
	
	protected abstract int getBitsPerSegment();

	@Override
	public int getBitCount() {
		return (joinedCount + 1) * getBitsPerSegment();
	}

	@Override
	public int getMaxDigitCount() {
		return getDigitCount(getMaxValue(), getDefaultTextualRadix());
	}

	@Override
	protected boolean isSameValues(AddressDivision other) {
		return (other instanceof IPAddressJoinedSegments) && 
				isSameValues((IPAddressJoinedSegments) other);
	}
	
	protected boolean isSameValues(IPAddressJoinedSegments otherSegment) {
		//note that it is the range of values that matters, the prefix bits do not
		return  otherSegment.joinedCount == joinedCount && value == otherSegment.value && upperValue == otherSegment.upperValue;
	}
	
	@Override
	public boolean equals(Object other) {
		return other == this || 
				(other instanceof IPAddressJoinedSegments && isSameValues((IPAddressJoinedSegments) other));
	}
	
	@Override
	public int hashCode() {
		return (int) (value | (upperValue << getBitCount()));
	}
}
