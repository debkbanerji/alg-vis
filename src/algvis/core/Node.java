package algvis.core;

import java.awt.Color;

import algvis.scenario.ArrowCommand;
import algvis.scenario.ChangeColorCommand;
import algvis.scenario.ChangeStateCommand;
import algvis.scenario.MoveCommand;

/**
 * The Class Node.
 * This is a basic element of the visualization. Nodes can be drawn, they can move,
 * change color, become marked/unmarked, or point in some direction.
 * Nodes are by default drawn as circles with their key in the middle.
 */
public class Node {
	public DataStructure D;
	public int key;
	/**
	 * x, y - node position
	 * tox, toy - the position, where the node is heading
	 *          - never change it other way than goto(tox, toy), because it 
	 *            breakes MoveCommand
	 * steps - the number of steps to reach the destination 
	 */
	public int x, y, tox, toy, steps;
	/** the state of a node - either ALIVE or INVISIBLE. */
	public int state = -1;
	public Color fgcolor, bgcolor;
	public boolean marked = false;
	public Node dir = null;
	public int arrow = Node.NOARROW; // NOARROW or angle (0=E, 45=SE, 90=S, 135=SW, 180=W)
	boolean arc = false;

	/**
	 * the key values are generally integers from 1 to 999 (inclusive)
	 * special values of a key are: INF (infinity) or -INF (-infinity),
	 * these are drawn nicely as the numeral 8 sideways
	 * NOKEY (drawn as an empty circle), and NULL (not drawn at all)
	 */
	public static final int INF = 99999, NOKEY = -1, NULL = 100000;
	/**
	 * a node can be in several different states: INVISIBLE (not drawn),
	 * ALIVE (visible), UP (this is the default starting state; the node is
	 * not yet visible, but when it moves, it starts at the top of the screen
	 * and automatically changes state to ALIVE); DOWN, LEFT, and RIGHT
	 * (the node moves down, or diagonally left or right until it gets out
	 * of the screen, and then turns INVISIBLE)
	 */
	public static final int INVISIBLE = -1, ALIVE = 0;
	public static final int NOARROW = -10000, DIRARROW = -10001, TOARROW = -10002;
	
	public Node() {
	}

	public Node(DataStructure D, int key, int x, int y) {
		this.D = D;
		this.key = key;
		this.x = tox = x;
		this.y = toy = y;
		steps = 0;
		setColor(Color.black, Colors.NORMAL);
	}

	public Node(DataStructure D, int key, int[] pos) {
		this(D, key, pos[0], pos[1]);
	}
	
	public Node(DataStructure D, int key) {
		this(D, key, 0, 0);
	}

	public Node(Node v) {
		this(v.D, v.key, v.x, v.y);
	}

	public void setState(int s) {
		if (D != null && D.scenario != null) {
			D.scenario.add(new ChangeStateCommand(this, s));
		}
		state = s;
	}

	public void setColor(Color fg, Color bg) {
		fgcolor = fg;
		bgcolor = bg;
	}

	public void fgColor(Color fg) {
		fgcolor = fg;
	}

	public void bgColor(Color bg) {
		if (bg != bgcolor) {
			if (D.scenario != null) {
				D.scenario.add(new ChangeColorCommand(this, bg));
			}
			bgcolor = bg;
		}
	}

	/**
	 * Set background color depending on the key (the higher the key, the darker the color).
	 */
	public void bgKeyColor() {
		bgColor(new Color(255, 255 - key / 10, 0));
	}

	public void mark() {
		marked = true;
	}

	public void unmark() {
		marked = false;
	}

	
	/**
	 * Draw an arrow pointing above the node w.
	 * @param w
	 */
	public void pointAbove(Node w) {
		dir = w;
		arrow = Node.DIRARROW;
		if (D.scenario != null) {
			D.scenario.add(new ArrowCommand(this, true));
		}
	}

	/**
	 * Draw an arrow pointing to the node w.
	 * @param w
	 */
	public void pointTo(Node w) {
		dir = w;
		arrow = Node.TOARROW;
	}

	/**
	 * Point in direction angle.
	 * The angle in degrees should be a nonnegative integer
	 * 0 = RIGHT, then clockwise: 90 = DOWN, 180 = LEFT 
	 * @param angle 
	 */
	public void pointInDir(int angle) {
		dir = null;
		arrow = angle;
		D.scenario.add(new ArrowCommand(this, true));
	}

	/**
	 * Stop drawing an arrow.
	 */
	public void noArrow() {
		if (D.scenario != null) {
			D.scenario.add(new ArrowCommand(this, false));
		}
		dir = null;
		arrow = Node.NOARROW;
	}

	/**
	 * Draw an arc pointing to node w.
	 * Assumption: w is above this node.
	 * @param w
	 */
	public void setArc(Node w) {
		dir = w;
		arc = true;
	}

	/**
	 * Stop drawing an arc.
	 */
	public void noArc() {
		arc = false;
	}

	/**
	 * Draw bg.
	 *
	 * @param g where to draw
	 * @param v view
	 */
	protected void drawBg(View v) {
		v.setColor(bgcolor);
		v.fillCircle(x, y, D.radius);
		v.setColor(Color.BLACK); // fgcolor);
		v.drawCircle(x, y, D.radius);
		if (marked) {
			v.drawCircle(x, y, D.radius + 2);
		}
	}

	/**
	 * Convert the key into a string (INF is converted to "8" sideways).
	 */
	@Override
	public String toString() {
		if (key == INF) {
			return "\u221e";
		} else if (key == -INF) {
			return "-\u221e";
		} else {
			return "" + key;
		}
	}

	public void drawKey(View v) {
		v.setColor(fgcolor);
		if (key != NOKEY) {
			v.drawString(toString(), x, y, 9);
		}
	}

	public void drawArrow(View v) {
		if (arrow == Node.NOARROW || (arrow < 0 && dir == null)) {
			return;
		}
		double dx, dy;
		if (arrow < 0) {
			dx = dir.x - x;
			if (arrow == DIRARROW) {
				dy = dir.y - 2 * D.radius - D.yspan - y;
			} else if (arrow == TOARROW) {
				dy = dir.y - y;
			} else {
				// vypindaj
				return;
			}
			double d = Math.sqrt(dx * dx + dy * dy);
			dx /= d;
			dy /= d;
		} else {
			dx = Math.cos(arrow * Math.PI / 180);
			dy = Math.sin(arrow * Math.PI / 180);
		}
		double x1, y1, x2, y2;
		x1 = x + 1.5 * D.radius * dx;
		y1 = y + 1.5 * D.radius * dy;
		if (arrow == TOARROW) {
			x2 = dir.x - 1.5 * D.radius * dx;
			y2 = dir.y - 1.5 * D.radius * dy;
		} else {
			x2 = x1 + 2 * D.radius * dx;
			y2 = y1 + 2 * D.radius * dy;
		}
		v.drawArrow((int) x1, (int) y1, (int) x2, (int) y2);
	}

	// Assumption: dir (the node we are pointing to) is above this node
	public void drawArc(View v) {
		if (!arc || dir == null) {
			return;
		}
		int x = dir.x, y = this.y - D.radius - D.yspan, a = Math.abs(this.x
				- dir.x), b = Math.abs(this.y - dir.y);
		if (this.x > dir.x) {
			v.drawArcArrow(x - a, y - b, 2 * a, 2 * b, 0, 90);
		} else {
			v.drawArcArrow(x - a, y - b, 2 * a, 2 * b, 180, 90);
		}
	}

	public void draw(View v) {
		if (state == Node.INVISIBLE /*|| state == Node.UP*/ || key == NULL) {
			return;
		}
		drawBg(v);
		drawKey(v);
		drawArrow(v);
		drawArc(v);
	}

	/**
	 * Is the given point inside the node?
	 * (Used mainly to decide whether a user clicked at the node.)
	 */
	public boolean inside(int x, int y) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) <= D.radius
				* D.radius;
	}

	/**
	 * Set new coordinates, where the node should go.
	 */
	public void goTo(int tox, int toy) {
		goToS(tox, toy, D.M.STEPS);
	}
	
	/**
	 * Set steps ("speed") and new coordinates, where the node should go.
	 */
	public void goToS(int tox, int toy, int steps) {
		if (D.scenario != null) {
			D.scenario.add(new MoveCommand(this, tox, toy, steps));
		}
		this.tox = tox;
		this.toy = toy;
		this.steps = steps;
		if (state == Node.ALIVE) {
			if (!D.M.S.V.inside(tox, toy)) {
				while (D.M.S.V.inside(x, y)) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
				}
				setState(Node.INVISIBLE);
			} else if (!D.M.S.V.inside(x, y)) {
				while (x != tox || y != toy) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
	}

	/**
	 * Go to the same position where node v is going.
	 */
	public void goTo(Node v) {
		goTo(v.tox, v.toy);
	}

	// public void goAbove (int tox, int toy) { goTo (tox, toy - 2*D.radius - D.yspan); }
	/**
	 * Go above node v (or more precisely: above the position where v is going).
	 */
	public void goAbove(Node v) {
		goTo(v.tox, v.toy - 2 * D.radius - D.yspan);
	}

	// public void goNextTo (int tox, int toy) { goTo (tox + 2*D.radius + D.xspan, toy); }
	/**
	 * Go next to node v (precisely to the right of where v is going).
	 */
	public void goNextTo(Node v) {
		goTo(v.tox + 2 * D.radius + D.xspan, v.toy);
	}

	/**
	 * Go to the root position.
	 */
	public void goToRoot() {
		goToS(D.rootx, D.rooty, (D.rooty - y) / 20);
	}

	/**
	 * Go above the root position.
	 */
	public void goAboveRoot() {
		int toy = D.rooty - 2 * D.radius - D.yspan;
		goToS(D.rootx, toy, (toy - y) / 20);
	}

	/**
	 * Go downwards out of the screen.
	 */
	public void goDown() {
		int down = (int) (D.M.S.V.viewY + D.M.S.V.viewH) + D.radius;
		goToS(x, down, (down - y) / 20);
	}

	/**
	 * Go left downwards out of the screen.
	 */
	public void goLeft() {
		int down = (int) (D.M.S.V.viewY + D.M.S.V.viewH) + D.radius;
		goToS((int) (D.M.S.V.viewX - D.M.S.V.viewW) - D.radius, down,
				(down - y) / 20);
	}

	/**
	 * Go right downwards out of the screen.
	 */
	public void goRight() {
		int down = (int) (D.M.S.V.viewY + D.M.S.V.viewH) + D.radius;
		goToS((int) (D.M.S.V.viewX + D.M.S.V.viewW) + D.radius, down,
				(down - y) / 20);
	}
	
	/**
	 * Make one step towards the destination (tox, toy).
	 */
	public void move() {
		if (steps > 0) {
			x += (tox - x) / steps;
			y += (toy - y) / steps;
			--steps;
		}
	}
}
