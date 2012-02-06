/**
 * 
 */
package me.ibhh.AuctionTrade;

/**
 * @author Simon
 *
 */
public class TimeHandler {

	public Long getTime()
	{
		try 
		{
		long time = System.currentTimeMillis();
		return time;
		}
		catch (Exception e) {
		      System.out.println("[AuctionTrade]: Error while getting Date! - " + e.getMessage());
		      e.printStackTrace();
		      return null;
		}
	}
}
