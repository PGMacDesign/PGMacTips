package com.pgmacdesign.pgmacutilities.adaptersandlisteners;

/**
 * Listener for sending back data. The int custom tag is used for identifying
 * what is being sent back
 * Created by pmacdowell on 8/12/2016.
 */
public interface OnTaskCompleteListener {
	/**
	 * Called whenever the current task has been completed
	 * @param result The result to send back {@link Object}
	 * @param customTag The custom tag that is used to identify the return type and
	 *                  subsequently parse / cast the returned object
	 */
    public void onTaskComplete(Object result, int customTag);
}
