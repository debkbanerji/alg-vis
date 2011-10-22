package algvis.core;

import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JTextField;

import algvis.internationalization.ILabel;

/**
 * The Class InputField.
 * This is a smart version of JTextField with methods that parse the input.
 * The input field may be asked for an integer, a vector of integers, or
 * a non-empty vector of integers (if no input is given a random value is generated).
 */
public class InputField extends JTextField {
	private static final long serialVersionUID = -1263697952255226926L;
	public final static int MAX = 999;
	Random G;
	ILabel sb; // status bar

	public InputField(int cols, ILabel sb) {
		super(cols);
		G = new Random(System.currentTimeMillis());
		this.sb = sb;
	}

	/**
	 * Returns an integer in the range 1..MAX.
	 * If no input is given, a default value def is returned.
	 */
	public int getInt(int def) {
		return getInt(def, 1, MAX);
	}

	/**
	 * Returns an integer in the range min..max.
	 * If no input is given, a default value def is returned.
	 */
	public int getInt(int def, int min, int max) {
		int n = def;
		StringTokenizer st = new StringTokenizer(this.getText());
		try {
			n = Integer.parseInt(st.nextToken());
			sb.setText("");
		} catch (Exception e) {
			sb.setText("couldn't parse an integer; using the default value " + def);
		} finally {
			if (n < min) {
				n = min;
				sb.setText("value too small; using the minimum value " + min + " instead");
			}
			if (n > max) {
				n = max;
				sb.setText("value too high; using the maximum value " + max + " instead");
			}
		}
		return n;
	}

	/**
	 * Returns a vector of integers in the range 1..MAX.
	 * Numbers in the input may be delimited by whitespaces and/or commas. 
	 */
	public Vector<Integer> getVI() {
		return getVI(1, MAX);
	}

	/**
	 * Returns a vector of integers in the range min..max.
	 * Numbers in the input may be delimited by whitespaces and/or commas. 
	 */
	public Vector<Integer> getVI(int min, int max) {
		Vector<Integer> args = new Vector<Integer>();
		String[] tokens = this.getText().split("(\\s|,)+");
		for (String t : tokens) {
			int x = min;
			try {
				x = Integer.parseInt(t); // st.nextToken());
			} catch (Exception e) {
				sb.setText("couldn't parse an integer");
			} finally {
				if (x < min) {
					x = min;
					sb.setText("value too small; using the minimum value instead");
				}
				if (x > max) {
					x = max;
					sb.setText("value too high; using the maximum value instead");
				}
				args.add(x);
			}
		}
		return args;
	}
	/**
	 * Returns a non-empty vector of integers in the range 1..MAX.
	 * Numbers in the input may be delimited by whitespaces and/or commas. 
	 * If no input is given, a vector with 1 random value in the range 1..MAX is returned.
	 */
	public Vector<Integer> getNonEmptyVI() {
		return getNonEmptyVI(1, MAX);
	}

	/**
	 * Returns a non-empty vector of integers in the range min..max.
	 * Numbers in the input may be delimited by whitespaces and/or commas. 
	 * If no input is given, a vector with 1 random value in the range min..max is returned.
	 */
	public Vector<Integer> getNonEmptyVI(int min, int max) {
		Vector<Integer> args = getVI();
		if (args.size() == 0) {
			args.add(G.nextInt(max - min + 1) + min);
			sb.setText("no input; using random value");
		}
		return args;
	}
}