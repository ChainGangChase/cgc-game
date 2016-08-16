package com.percipient24.cgc.art;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.ChaseApp;

/**
 * Created by percipient24 on 8/13/16.
 */
public class Characters {

	private static boolean initiated = false;

	public static final int NUM_CONS = 13;
	public static final int NUM_COPS = 13;

	public static CharacterArt defaultCon;
	public static CharacterArt defaultCop;

	public static Array<CharacterArt> cons;
	public static Array<CharacterArt> cops;


	public Characters(ChaseApp app) {
		if (!initiated) {
			initiated = true;
			String con = "con";
			String cop = "cop";

			defaultCon = new CharacterArt(app, con, 0);
			defaultCon.index = -1;
			defaultCop = new CharacterArt(app, cop, 0);
			defaultCop.index = -1;

			cons = new Array<CharacterArt>(NUM_CONS);
			for (int i = 0; i < NUM_CONS; i++) {
				cons.insert(i, new CharacterArt(app, con, i));
			}

			cops = new Array<CharacterArt>(NUM_COPS);
			for (int i = 0; i < NUM_COPS; i++) {
				cops.insert(i, new CharacterArt(app, cop, i));
			}
		}
	}

	private static CharacterArt getNextStartingAt(int startIndex, int direction, Array<CharacterArt> haystack, boolean sitting) {
		int index = startIndex;
		int size = haystack.size;
		CharacterArt potential;
		boolean freePrevious = true;

		if (sitting) {
			if (startIndex != -1) {
				return haystack.get(startIndex);
			} else {
				startIndex = index = 0;
				freePrevious = false;
			}
		}

		potential = haystack.get(index);

		while(potential.used == true) {
			index += direction;
			if (index < 0) {
				index += size;
			}
			index = index % size;
			potential = haystack.get(index);
		}
		if (freePrevious) {
			haystack.get(startIndex).used = false;
		}
		potential.used = true;
		return potential;
	}

	public static CharacterArt getNextConStartingAt(CharacterArt currentCon, boolean sitting) {
		return getNextStartingAt(currentCon.index, 1, cons, sitting);
	}

	public static CharacterArt getNextCopStartingAt(CharacterArt currentCop, boolean sitting) {
		return getNextStartingAt(currentCop.index, 1, cops, sitting);
	}

	public static CharacterArt getPrevConStartingAt(CharacterArt currentCon, boolean sitting) {
		return getNextStartingAt(currentCon.index, -1, cons, sitting);
	}

	public static CharacterArt getPrevCopStartingAt(CharacterArt currentCop, boolean sitting) {
		return getNextStartingAt(currentCop.index, -1, cops, sitting);
	}
}
