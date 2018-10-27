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

package com.nwcg.icbs.yantra.api.issue.util;

import java.util.Hashtable;
import java.util.Vector;

public class NWCGUpdateNFESOrderLine {

	String orderType;
	String orderNo;
	String orderHdrKey;
	String reqNo;
	String baseReqNo;
	String status;
	Vector <String> serialNos;
	String itemID;
	String itemDesc;
	String shippedQty;
	String utfQty;
	String backOrderQty;
	String fwdQty;
	String incidentNo;
	String incidentYr;
	String notes;
	String shipmentEstimatedDepartDate;
	String shipmentEstimatedArrivalDate;
	Hashtable<String, String> htAddr;
	
	public void printOrderLine(){
	}
	
	public String getBaseReqNo() {
		return baseReqNo;
	}

	public void setBaseReqNo(String baseReqNo) {
		this.baseReqNo = baseReqNo;
	}

	public String getIncidentNo() {
		return incidentNo;
	}

	public void setIncidentNo(String incidentNo) {
		this.incidentNo = incidentNo;
	}

	public String getIncidentYr() {
		return incidentYr;
	}

	public void setIncidentYr(String incidentYr) {
		this.incidentYr = incidentYr;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public Vector <String> getSerialNos() {
		return serialNos;
	}

	public void setSerialNos(Vector <String> serialNos) {
		this.serialNos = serialNos;
	}

	public String getShippedQty() {
		return shippedQty;
	}

	public void setShippedQty(String shippedQty) {
		this.shippedQty = shippedQty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUtfQty() {
		return utfQty;
	}

	public void setUtfQty(String utfQty) {
		this.utfQty = utfQty;
	}

	public Hashtable<String, String> getHtAddr() {
		return htAddr;
	}

	public void setHtAddr(Hashtable<String, String> htAddr) {
		this.htAddr = htAddr;
	}

	public String getOrderHdrKey() {
		return orderHdrKey;
	}

	public void setOrderHdrKey(String orderHdrKey) {
		this.orderHdrKey = orderHdrKey;
	}

	public String getBackOrderQty() {
		return backOrderQty;
	}

	public void setBackOrderQty(String backOrderQty) {
		this.backOrderQty = backOrderQty;
	}

	public String getFwdQty() {
		return fwdQty;
	}

	public void setFwdQty(String fwdQty) {
		this.fwdQty = fwdQty;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getShipmentEstimatedArrivalDate() {
		return shipmentEstimatedArrivalDate;
	}

	public void setShipmentEstimatedArrivalDate(String shipmentEstimatedArrivalDate) {
		this.shipmentEstimatedArrivalDate = shipmentEstimatedArrivalDate;
	}

	public String getShipmentEstimatedDepartDate() {
		return shipmentEstimatedDepartDate;
	}

	public void setShipmentEstimatedDepartDate(String shipmentEstimatedDepartDate) {
		this.shipmentEstimatedDepartDate = shipmentEstimatedDepartDate;
	}
}