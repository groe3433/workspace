/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
                     LIMITATION OF LIABILITY
THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
*/

package com.nwcg.icbs.yantra.exception.common;

import java.util.Hashtable;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * Exception to be thrown by custom classes in the USFS ICBS Solution
 * This class will fetch the description of the error codes from the project
 * specific resource bundles.
 * 
 * @author gacharya
 */
public class NWCGException extends CustomException {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGException.class);
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructor taking an error code.
     * 
     * @param errorCode Error Code.
     */
    public NWCGException(String errorCode) {
        this(errorCode, null, null, null);
    }

    /**
     * Constructor taking ErrorCode & Error Args.
     * 
     * @param errorCode Error Code.
     * @param errorArgs Arguments to the description for the error code.
     */
    public NWCGException(String errorCode, Object[] errorArgs) {
        this(errorCode, errorArgs, null, null);
    }

    /**
     * Constructor taking a Nested exception.
     * 
     * @param nestedException Nested Exception.
     */
    public NWCGException(Exception nestedException) {
        this(null, (Object []) null, nestedException);
    }

    /**
     * Constructor taking ErrorCode and a Nested exception.
     * 
     * @param errorCode Error Code.
     * @param nestedException Nested Exception.
     */
    public NWCGException(String errorCode, Exception nestedException) {
        this(errorCode, null, null, nestedException);
    }

    /**
     * Constructor taking ErrorCode, Arguments and a Nested exception.
     *
     * @param errorCode Error Code.
     * @param errorArgs Arguments to the description for the error code.
     * @param nestedException Nested Exception.
     */
    public NWCGException(String errorCode, Object[] errorArgs, Exception nestedException) {
        this(errorCode, errorArgs, null, nestedException);
    }

    /**
     * Constructor taking ErrorCode, ErrorRelatedMoreInfo and a Nested exception.
     * 
     * @param errorCode Error Code.
     * @param errorArgs Arguments to the description for the error code.
     * @param errorRelatedInfo More context specific error information.
     * @param nestedException Nested Exception.
     */
    public NWCGException(String errorCode, Object[] errorArgs, String errorRelatedInfo,
                                            Exception nestedException) {
        super(errorCode, errorArgs, errorRelatedInfo, nestedException);
    }

    /**
     * Constructor taking ErrorCode, ErrorRelatedMoreInfo and a Nested exception.
     *
     * @param errorCode Error Code.
     * @param errorArgs Arguments to the description for the error code.
     * @param errorAttributes Error Attributes.
     * @param errorRelatedInfo More context specific error information.
     * @param nestedException Nested Exception.
     */
    public NWCGException(String errorCode, Object[] errorArgs, Hashtable errorAttributes,
                                String errorRelatedInfo, Exception nestedException) {
        super(errorCode, errorArgs, errorAttributes, errorRelatedInfo, nestedException);
    }
}