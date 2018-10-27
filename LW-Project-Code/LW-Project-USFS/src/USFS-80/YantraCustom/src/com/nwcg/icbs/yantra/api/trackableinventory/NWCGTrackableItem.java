package com.nwcg.icbs.yantra.api.trackableinventory;

public class NWCGTrackableItem 
{
	private String itemId;
	private String serialNo;
	private String nodeLevel;
	private NWCGTrackableItem parentTrackableItem;
	
	public static final String PARENT = "1";
	public static final String CHILD = "2";
	public static final String GRANDCHILD = "3";

	public NWCGTrackableItem(String itemId, String serialNo, NWCGTrackableItem parentTrackableItem)
	{
		this.itemId = itemId;
		this.serialNo = serialNo;
		this.parentTrackableItem = parentTrackableItem;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public NWCGTrackableItem getParentTrackableItem() {
		return parentTrackableItem;
	}

	public void setParentTrackableItem(NWCGTrackableItem parentTrackableItem) {
		this.parentTrackableItem = parentTrackableItem;
	}
	
	public String getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(String nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public static String getParentItemId(NWCGTrackableItem trackItem)
	{
		String parentItemId = null;
		if(trackItem != null)
		{
			NWCGTrackableItem parent = trackItem.getParentTrackableItem();
			if(parent != null)
			{
				parentItemId = parent.getItemId();
			}
		}
		return parentItemId;
	}

	public static String getParentSerialNo(NWCGTrackableItem trackItem)
	{
		String parentSerialNo = null;
		if(trackItem != null)
		{
			NWCGTrackableItem parent = trackItem.getParentTrackableItem();
			if(parent != null)
			{
				parentSerialNo = parent.getSerialNo();
			}
		}
		return parentSerialNo;
	}

	public static String getGrandParentItemId(NWCGTrackableItem trackItem)
	{
		String grandParentItemId = null;
		if(trackItem != null)
		{
			NWCGTrackableItem parent = trackItem.getParentTrackableItem();
			if(parent != null)
			{
				NWCGTrackableItem grandParent = parent.getParentTrackableItem();
				if(grandParent != null)
				{
					grandParentItemId = grandParent.getItemId();
				}
			}
		}
		return grandParentItemId;
	}

	public static String getGrandParentSerialNo(NWCGTrackableItem trackItem)
	{
		String grandParentSerialNo = null;
		if(trackItem != null)
		{
			NWCGTrackableItem parent = trackItem.getParentTrackableItem();
			if(parent != null)
			{
				NWCGTrackableItem grandParent = parent.getParentTrackableItem();
				if(grandParent != null)
				{
					grandParentSerialNo = grandParent.getSerialNo();
				}
			}
		}
		return grandParentSerialNo;
	}
	
	public static String getNodeLevel(NWCGTrackableItem trackItem)
	{
		String nodeLevel = PARENT;
		if(trackItem != null)
		{
			nodeLevel = CHILD;
			if(trackItem.getParentTrackableItem() != null)
			{
				nodeLevel = GRANDCHILD;
				if(trackItem.getParentTrackableItem().getParentTrackableItem() != null)
				{
					nodeLevel = "4";
					if(trackItem.getParentTrackableItem().getParentTrackableItem().getParentTrackableItem() != null)
					{
						nodeLevel = "5";
					}
				}
			}
		}
		return nodeLevel;
	}

	public String toString()
	{
		return "itemID = " + itemId + ", serialNo = " + serialNo;
	}
}
