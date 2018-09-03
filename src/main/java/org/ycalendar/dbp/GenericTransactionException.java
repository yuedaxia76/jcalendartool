package org.ycalendar.dbp;

public class GenericTransactionException extends RuntimeException{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GenericTransactionException() {
        super();
    }

    public GenericTransactionException(String str) {
        super(str);
    }

    public GenericTransactionException(String str, Throwable nested) {
        super(str, nested);
    }

}
