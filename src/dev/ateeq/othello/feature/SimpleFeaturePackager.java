package dev.ateeq.othello.feature;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.stm.SimpleStimulant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


/**
 * This class takes extractable features provided by the Environment and packages them up into a stimulus to feed to stm
 */
public class SimpleFeaturePackager {

    public HashSet<SimpleStimulant> processFeatures(Hashtable<BoardLocation, ArrayList<BoardLocation>> features) {
        /* For every valid move, calculate the dist relative to l1 and if unique, add to stimulus */
        HashSet<SimpleStimulant> stimulus = new HashSet<>(5);

        BoardLocation[] keys = features.keySet().toArray(new BoardLocation[0]);
		for (BoardLocation key : keys) {
			/* setup stimulant: */
			SimpleStimulant simpleStimulant = new SimpleStimulant();
			simpleStimulant.distFromCenter = SimpleStimulant.calcDistanceFromCenter(key, QuadrantType.L1);
			simpleStimulant.numFlipped = features.get(key).size();

			boolean isVal = stimulus.add(simpleStimulant);
		}

        return stimulus;
    }
}
