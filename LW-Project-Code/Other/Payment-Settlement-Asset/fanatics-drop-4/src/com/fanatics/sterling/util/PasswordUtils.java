package com.fanatics.sterling.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * <p>
 * This class contains util metods for generation of SHA-256 hash and comparing the hash values
 * 
 * </p>.
 * 
 * @(#) PasswordUtils.java
 * Created on   Apr 15, 2008
 * 3:17:30 PM
 * 
 * Package Declaration:
 * File Name:       PasswordUtils.java
 * Package Name:    com.fanatics.sterling.util;
 * Project name:    Fanatics
 * Type Declaration:
 * Class Name:      PasswordUtils
 * @author gacharya,skumar
 * @version 1.0
 * @history Apr 15, 2008
 * (C) Copyright 2006-2007 by owner.
 * All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorization from the owner.
 */

public class PasswordUtils {
/*
	 /**
 	 * Gets the hash as byte array using the SHA-256 algo for a given string and salt
 	 * @param password the password
 	 * @param salt the salt
 	 * 
 	 * @return the hash
 	 * 
 	 * @throws NoSuchAlgorithmException the no such algorithm exception
 	 * @throws UnsupportedEncodingException the unsupported encoding exception
 	 */
 	public static byte[] getHash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	       MessageDigest digest = MessageDigest.getInstance(ResourceUtil.get("dsw.default.hash.scheme","SHA-256"));
	       digest.reset();
	       //digest.update(salt);
	       return digest.digest(password.getBytes(ResourceUtil.get("dsw.default.encoding.scheme","UTF-8")));
	 }
	 

	/**
 	 * Compare existing hashString with the enterted string    
 	 * @param existing the existing
 	 * @param entered the entered
 	 * 
 	 * @return true, if successful
 	 */
 	public static boolean compare(String existing,String entered){
		boolean retFlag = false;
		try{
			
			
			byte oldPass[] = Base64.decode(existing);
			byte newPass[] = PasswordUtils.getHash(entered, null);
			return Arrays.equals(oldPass, newPass);
		
		}catch(NoSuchAlgorithmException ne){
			return retFlag;
		}catch(UnsupportedEncodingException ue){
			return retFlag;
		}

	 }
	
	/**
	 * Gets the hash as Base-64 encoded string using the SHA-256 algo for a given string
	 * 
	 * @param password the password
	 * 
	 * @return the hash
	 * 
	 * @throws Exception the exception
	 */
	public static final String getHash(String password)throws Exception{
		return Base64.encode(getHash(password,null));
	}
	
	
	/**
	 * The main method
	 * 
	 * @param args the arguments
	 * 
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception{
		
		String testStr= "testPass" ;
		String test1Str= "test1Pass" ;
		String hashStr = getHash(testStr);
//		System.out.println("testStr:"+testStr+"="+hashStr);
		//String testHash ="rwQm5x3VfA/fk/I/bxkaSqBXitfXGJfpNnRgKLj/0x0=";
//		System.out.println("Comparing Eqality:"+compare(hashStr,testStr));
//		System.out.println("Comparing Unequal:"+compare(hashStr,test1Str));
		
	}
}
