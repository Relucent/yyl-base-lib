package com.github.relucent.base.common.logging;

import java.util.HashMap;
import java.util.Map;

public class LoggerMessageFormatter {

	private static final char DELIM_START = '{';
	private static final String DELIM_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';

	public static final Throwable getThrowableCandidate(Object[] args) {
		if (args == null || args.length == 0) {
			return null;
		}
		final Object lastEntry = args[args.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}

	public static LoggerMessageTuple arrayFormat(String messagePattern, Object[] args) {
		Throwable throwableCandidate = getThrowableCandidate(args);
		if (throwableCandidate != null) {
			args = trimmedCopy(args);
		}
		return arrayFormat(messagePattern, args, throwableCandidate);
	}

	public static final LoggerMessageTuple arrayFormat(final String messagePattern, final Object[] args, Throwable throwable) {
		if (messagePattern == null) {
			return new LoggerMessageTuple("", throwable);
		}
		if (args == null) {
			return new LoggerMessageTuple(messagePattern, null);
		}
		int i = 0;
		int j;
		StringBuilder buffer = new StringBuilder(messagePattern.length() + 50);
		int L;
		for (L = 0; L < args.length; L++) {
			j = messagePattern.indexOf(DELIM_STR, i);
			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return new LoggerMessageTuple(messagePattern, throwable);
				} else { // add the tail string which contains no variables and return
					// the result.
					buffer.append(messagePattern, i, messagePattern.length());
					return new LoggerMessageTuple(buffer.toString(), throwable);
				}
			} else {
				if (isEscapedDelimeter(messagePattern, j)) {
					if (!isDoubleEscaped(messagePattern, j)) {
						L--; // DELIM_START was escaped, thus should not be incremented
						buffer.append(messagePattern, i, j - 1);
						buffer.append(DELIM_START);
						i = j + 1;
					} else {
						// The escape character preceding the delimiter start is
						// itself escaped: "abc x:\\{}"
						// we have to consume one backward slash
						buffer.append(messagePattern, i, j - 1);
						deeplyAppendParameter(buffer, args[L], new HashMap<Object[], Object>());
						i = j + 2;
					}
				} else {
					// normal case
					buffer.append(messagePattern, i, j);
					deeplyAppendParameter(buffer, args[L], new HashMap<Object[], Object>());
					i = j + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		buffer.append(messagePattern, i, messagePattern.length());
		return new LoggerMessageTuple(buffer.toString(), throwable);
	}

	private static Object[] trimmedCopy(Object[] args) {
		if (args == null || args.length == 0) {
			throw new IllegalStateException("non-sensical empty or null argument array");
		}
		final int trimemdLen = args.length - 1;
		Object[] trimmed = new Object[trimemdLen];
		System.arraycopy(args, 0, trimmed, 0, trimemdLen);
		return trimmed;
	}

	private static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
		if (delimeterStartIndex == 0) {
			return false;
		}
		char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
		if (potentialEscape == ESCAPE_CHAR) {
			return true;
		} else {
			return false;
		}
	}

	private static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
		if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR) {
			return true;
		} else {
			return false;
		}
	}

	// special treatment of array values was suggested by 'lizongbo'
	private static void deeplyAppendParameter(StringBuilder buffer, Object o, Map<Object[], Object> seenMap) {
		if (o == null) {
			buffer.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			safeObjectAppend(buffer, o);
		} else {
			// check for primitive array types because they
			// unfortunately cannot be cast to Object[]
			if (o instanceof boolean[]) {
				booleanArrayAppend(buffer, (boolean[]) o);
			} else if (o instanceof byte[]) {
				byteArrayAppend(buffer, (byte[]) o);
			} else if (o instanceof char[]) {
				charArrayAppend(buffer, (char[]) o);
			} else if (o instanceof short[]) {
				shortArrayAppend(buffer, (short[]) o);
			} else if (o instanceof int[]) {
				intArrayAppend(buffer, (int[]) o);
			} else if (o instanceof long[]) {
				longArrayAppend(buffer, (long[]) o);
			} else if (o instanceof float[]) {
				floatArrayAppend(buffer, (float[]) o);
			} else if (o instanceof double[]) {
				doubleArrayAppend(buffer, (double[]) o);
			} else {
				objectArrayAppend(buffer, (Object[]) o, seenMap);
			}
		}
	}

	private static void safeObjectAppend(StringBuilder buffer, Object o) {
		try {
			String oAsString = o.toString();
			buffer.append(oAsString);
		} catch (Throwable t) {
			buffer.append("[FAILED " + o.getClass().getName() + "]toString()]");
		}

	}

	private static void objectArrayAppend(StringBuilder buffer, Object[] a, Map<Object[], Object> seenMap) {
		buffer.append('[');
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				deeplyAppendParameter(buffer, a[i], seenMap);
				if (i != len - 1) {
					buffer.append(", ");
				}
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			buffer.append("...");
		}
		buffer.append(']');
	}

	private static void booleanArrayAppend(StringBuilder buffer, boolean[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void byteArrayAppend(StringBuilder buffer, byte[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void charArrayAppend(StringBuilder buffer, char[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void shortArrayAppend(StringBuilder buffer, short[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void intArrayAppend(StringBuilder buffer, int[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void longArrayAppend(StringBuilder buffer, long[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void floatArrayAppend(StringBuilder buffer, float[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}

	private static void doubleArrayAppend(StringBuilder buffer, double[] a) {
		buffer.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			buffer.append(a[i]);
			if (i != len - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
	}
}
