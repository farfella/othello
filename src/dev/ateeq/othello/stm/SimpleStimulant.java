package dev.ateeq.othello.stm;


import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.feature.QuadrantType;


/**
 * Represents a single stimulant
 */
public class SimpleStimulant implements Comparable<SimpleStimulant>, Cloneable {

    //defined centers of each l-quadrant
    protected static final BoardLocation L1CENTER = new BoardLocation(3, 3);
    protected static final BoardLocation L2CENTER = new BoardLocation(4, 3);
    protected static final BoardLocation L3CENTER = new BoardLocation(3, 4);
    protected static final BoardLocation L4CENTER = new BoardLocation(4, 4);

    //defined corrections for the l-quadrants
    protected static final int[] L1CORRECTION = {-1, -1};
    protected static final int[] L2CORRECTION = {1, -1};
    protected static final int[] L3CORRECTION = {-1, 1};
    protected static final int[] L4CORRECTION = {1, 1};
    public BoardLocation distFromCenter;
    public int numFlipped;
    public float calcProbability;

    public SimpleStimulant() {
        distFromCenter = new BoardLocation(0, 0);
        numFlipped = 0;
        calcProbability = 0.0f;
    }

    //this calculates the distance from the center piece of the quad
    //loc - being the piece that is considered for placement on board
    //returns distance relative to the specified l-quad correction
    public static BoardLocation calcDistanceFromCenter(BoardLocation loc, QuadrantType correctionType) {
        QuadrantType lval = calcQuadType(loc);

        BoardLocation ret = new BoardLocation(0, 0);
        if (lval == QuadrantType.L1) {
            ret.X = Math.abs(L1CENTER.X - loc.X);
            ret.Y = Math.abs(L1CENTER.Y - loc.Y);
        } else if (lval == QuadrantType.L2) {
            ret.X = Math.abs(L2CENTER.X - loc.X);
            ret.Y = Math.abs(L2CENTER.Y - loc.Y);
        } else if (lval == QuadrantType.L3) {
            ret.X = Math.abs(L3CENTER.X - loc.X);
            ret.Y = Math.abs(L3CENTER.Y - loc.Y);
        } else {
            ret.X = Math.abs(L4CENTER.X - loc.X);
            ret.Y = Math.abs(L4CENTER.Y - loc.Y);
        }
        ret = applyCorrection(ret, correctionType);

        return ret;
    }

    //this calculates l-value for given board location
    public static QuadrantType calcQuadType(BoardLocation loc) {
        if (loc.X < 4) {
            if (loc.Y < 4) {
                return QuadrantType.L1;
            }

            return QuadrantType.L3;
        }
        if (loc.Y < 4) {
            return QuadrantType.L2;
        }
        return QuadrantType.L4;
    }

    //this applies the requested correction to the result of calcFromDist call
    protected static BoardLocation applyCorrection(BoardLocation loc, QuadrantType lval) {
        BoardLocation ret = new BoardLocation(loc.X, loc.Y);

        if (lval == QuadrantType.L1) {
            //apply the l1 correction
            ret.X *= L1CORRECTION[0];
            ret.Y *= L1CORRECTION[1];
        } else if (lval == QuadrantType.L2) {
            //apply the l2 correction
            ret.X *= L2CORRECTION[0];
            ret.Y *= L2CORRECTION[1];
        } else if (lval == QuadrantType.L3) {
            //apply the l3 correction
            ret.X *= L3CORRECTION[0];
            ret.Y *= L3CORRECTION[1];
        } else {
            //apply the l4 correction
            ret.X *= L4CORRECTION[0];
            ret.Y *= L4CORRECTION[1];
        }
        return ret;
    }

    //returns 4 possible moves on the board that correspond to this stimulant
    public BoardLocation[] translateStimulantToBoardLocations() {
        BoardLocation locs[] = new BoardLocation[4];
        locs[0] = new BoardLocation(0, 0);
        locs[1] = new BoardLocation(0, 0);
        locs[2] = new BoardLocation(0, 0);
        locs[3] = new BoardLocation(0, 0);

        //assuming our stimulant is relative to L1
        locs[0].X = L1CENTER.X - Math.abs(this.distFromCenter.X);
        locs[0].Y = L1CENTER.Y - Math.abs(this.distFromCenter.Y);

        locs[1].X = L2CENTER.X + Math.abs(this.distFromCenter.X);
        locs[1].Y = L2CENTER.Y - Math.abs(this.distFromCenter.Y);

        locs[2].X = L3CENTER.X - Math.abs(this.distFromCenter.X);
        locs[2].Y = L3CENTER.Y + Math.abs(this.distFromCenter.Y);

        locs[3].X = L4CENTER.X + Math.abs(this.distFromCenter.X);
        locs[3].Y = L4CENTER.Y + Math.abs(this.distFromCenter.Y);

        return locs;
    }

    public int hashCode() {
        //max hashcode from BoardLocation <=64
        //and  1 <=numFlipped <=20
        return distFromCenter.hashCode() + numFlipped * 100;
    }

    public String toString() {
        return "stim dist= (" + distFromCenter.X + "," + distFromCenter.Y + ") #flipped=" + numFlipped;
    }

//	@Override
//	public int compareTo(Object arg0) 
//	{
//		if (arg0.getClass() == this.getClass())
//		{
//			SimpleStimulant rhs = (SimpleStimulant)(arg0);
//			
//			return new Integer(this.hashCode()).compareTo(rhs.hashCode());
//			
//		}
//		else
//			throw new ClassCastException("Cannot convert arg0 to SimpleStimulant");
//	
//	}

    @Override
    public int compareTo(SimpleStimulant o) {
        return new Integer(this.hashCode()).compareTo(o.hashCode());
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0.getClass() == this.getClass()) {
            SimpleStimulant rhs = (SimpleStimulant) (arg0);

            if (this.hashCode() == rhs.hashCode()) {
                return true;
            }
            return false;

        } else
            throw new ClassCastException("Cannot convert arg0 to SimpleStimulant");
    }


}