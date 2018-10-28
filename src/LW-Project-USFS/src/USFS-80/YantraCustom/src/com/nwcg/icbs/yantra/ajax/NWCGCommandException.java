package com.nwcg.icbs.yantra.ajax;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;

public class NWCGCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NWCGCommandException() {
		super();
	}

	public NWCGCommandException(String messagecode) {
		super(ResourceUtil.get(messagecode));
	}

	public NWCGCommandException(Throwable t) {
		super(t);
	}

	public NWCGCommandException(String messagecode, Throwable t) {
		super(ResourceUtil.get(messagecode), t);
	}

}
