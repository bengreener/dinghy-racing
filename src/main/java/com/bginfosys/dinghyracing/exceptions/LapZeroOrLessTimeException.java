package com.bginfosys.dinghyracing.exceptions;

public class LapZeroOrLessTimeException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	/**
     * Constructs an {@code LapZeroOrLessTimeException} with no
     * detail message.
     */
	public LapZeroOrLessTimeException() {
		super();
	}
	
	/**
     * Constructs an {@code LapZeroOrLessTimeException} with the
     * specified detail message.
     *
     * @param	s	the detail message.
     */
	public LapZeroOrLessTimeException(String s) {
		super(s);
	}

}
