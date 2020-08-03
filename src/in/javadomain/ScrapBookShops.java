package in.javadomain;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ScrapBookShops {
	public JSONObject delegateToShop(String shop, String isbn) {
		if (shop.equalsIgnoreCase("uread")) {
			return getUreadPrice(isbn);
		} else if (shop.equalsIgnoreCase("amazon")) {
			return getAmazonPrice(isbn);
		} else if (shop.equalsIgnoreCase("crossword")) {
			return getCrossWordPrice(isbn);
		}
		return null;
	}

	public JSONObject getUreadPrice(String isbn) {
		String baseUrl = "http://www.uread.com/search-books/" + isbn + "";
		String uReadPrice = "";
		Document doc;
		JSONObject uReadJSON = new JSONObject();
		try {
			doc = Jsoup.connect(baseUrl).timeout(0).get();
			String url = doc.baseUri();
			String outOfStock = doc.select("div.stock-info")
					.select("div.out-of-stock").text();
			uReadPrice = doc.select("div.sale").text();
			uReadJSON = formJSON("Uread", formatPrice(uReadPrice), url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uReadJSON;
	}

	public JSONObject getCrossWordPrice(String isbn) {
		String crossWordURL = "http://www.crossword.in/books/search?q=" + isbn
				+ "";
		String crossWordPrice = "";
		Document doc;
		JSONObject crossWordJSON = new JSONObject();
		try {
			doc = Jsoup.connect(crossWordURL).timeout(0).get();
			crossWordPrice = doc.select("span.variant-final-price").text();
			String url = doc.baseUri();
			crossWordJSON = formJSON("CrossWord", formatPrice(crossWordPrice),
					url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return crossWordJSON;
	}

	public JSONObject getAmazonPrice(String isbn) {
		String amazonURL = "http://www.amazon.in/s/field-keywords=" + isbn + "";
		String amazonPrice = "";
		Document doc;
		JSONObject amazonJSON = new JSONObject();
		try {
			doc = Jsoup.connect(amazonURL).timeout(0).get();
			amazonPrice = doc.select("span.a-size-base")
					.select("span.a-color-price").select("span.s-price")
					.select("span.a-text-bold").text();
			String url = doc.select("div.a-row").select("div.a-spacing-none")
					.select("a").attr("href");
			amazonJSON = formJSON("amazon", formatPrice(amazonPrice), url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return amazonJSON;
	}

	private JSONObject formJSON(String site, String price, String url) {
		JSONObject formedJSONObj = new JSONObject();
		if (!price.isEmpty()) {
			formedJSONObj.put("source", site);
			formedJSONObj.put("url", url);
			formedJSONObj.put("price", price);
		}
		return formedJSONObj;
	}

	private String formatPrice(String price) {
		return price.replaceAll("\\D+", "");
	}

}