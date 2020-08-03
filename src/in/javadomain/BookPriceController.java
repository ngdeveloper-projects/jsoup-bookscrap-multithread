package in.javadomain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BookPriceController {
	private static final int MYTHREADS = 30;
	private static List<JSONObject> allOnlineShoppJSON = new ArrayList<JSONObject>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Multithreading started! */
		ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		String[] hostList = { "uread", "amazon", "crossword" };
		String isbn = "8173711461";
		for (int i = 0; i < hostList.length; i++) {
			String url = hostList[i];
			Runnable worker = new MyRunnable(url, isbn);
			executor.execute(worker);
		}
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
		}
		BookPriceController bpcBookPriceController = new BookPriceController();
		bpcBookPriceController.WriteJson(allOnlineShoppJSON,
				allOnlineShoppJSON.size());
		System.out.println("\nFinished all threads");
		/* Multithreading Ends! */
	}

	private void WriteJson(List<JSONObject> allJsonObj, int count) {
		System.out.print("Writting json");
		JSONObject obj = new JSONObject();
		obj.put("status_code", "200");
		obj.put("status_text", "Success");
		obj.put("count", count);

		JSONArray list = new JSONArray();
		for (JSONObject onlineShopJSON : allJsonObj) {
			list.add(onlineShopJSON);
		}
		obj.put("data", list);

		System.out.print("Great:::" + obj);
	}

	public static class MyRunnable implements Runnable {
		private final String shop;
		private final String isbn;

		MyRunnable(String shop, String isbn) {
			this.shop = shop;
			this.isbn = isbn;
		}

		public void run() {
			String result = "";
			try {
				ScrapBookShops allShopsScraps = new ScrapBookShops();
				JSONObject shopJSONObj = allShopsScraps.delegateToShop(shop,
						isbn);
				if (!shopJSONObj.isEmpty()) {
					allOnlineShoppJSON.add(shopJSONObj);
				}
			} catch (Exception e) {
				result = "->Red<-\t";
			}

			System.out.println(allOnlineShoppJSON.size());
			System.out.println(shop + "\t\tStatus:" + result);
		}

	}
}
