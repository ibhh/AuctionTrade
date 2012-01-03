/**
 * 
 */
package me.ibhh.AuctionTrade;

import java.sql.Date;

/**
 * @author Simon
 *
 */
public class TimeHandler {

	public Date getDate()
	{
		try 
		{
		long time = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(time);
		return date;
		}
		catch (Exception e) {
		      System.out.println("[AuctionTrade]: Error while getting Date! - " + e.getMessage());
		      e.printStackTrace();
		      return null;
		}
	}
}
