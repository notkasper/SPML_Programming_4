package nl.ru.ai.vroon.mdp;

/**
 * Enumerates the actions possible within a MDP
 * @author Jered Vroon
 */
public enum Action {
    UP(0, 1),    // i.e. go to the field above
    DOWN(0, -1), // i.e. go to the field below
    LEFT(-1, 0), // i.e. go to the field to the left
    RIGHT(1, 0); // i.e. go to the field to the right

    private final int dx, dy;

    private Action(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int GetDX() {
        return dx;
    }

    public int GetDY() {
        return dy;
    }

    public static Action nextAction(Action in) {
        switch (in) {
            case UP:
                return RIGHT;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
            case RIGHT:
                return DOWN;
        }
        return null;
    }

    public static Action previousAction(Action in) {
        switch (in) {
            case UP:
                return LEFT;
            case DOWN:
                return RIGHT;
            case LEFT:
                return DOWN;
            case RIGHT:
                return UP;
        }
        return null;
    }

    public static Action backAction(Action in) {
        switch (in) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return null;
    }
}
