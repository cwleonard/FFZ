package com.amphibian.ffz.engine;

import java.util.Comparator;

import com.amphibian.ffz.engine.sprite.Sprite;

public class SpriteSorter implements Comparator<Sprite> {

	@Override
	public int compare(Sprite lhs, Sprite rhs) {
		
		if (lhs == null && rhs == null) {
			return 0;
		} else if (lhs == null && rhs != null) {
			return 1;
		} else if (lhs != null && rhs == null) {
			return -1;
		}
		
		float lhsBottom = lhs.getBottom();
		float rhsBottom = rhs.getBottom();
		if (rhsBottom - lhsBottom < 0f) {
			return -1;
		} else if (rhsBottom - lhsBottom > 0f) {
			return 1;
		} else {
			return 0;
		}
		
	}

}
