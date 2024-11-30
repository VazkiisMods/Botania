package vazkii.botania.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UuidNameProvider {

	private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
	private static final Duration CACHE_DURATION = Duration.ofMinutes(15);

	public static String getPlayerNameFromUUID(String uuid) {
		CacheEntry entry = cache.get(uuid);
		if (entry != null && !entry.isExpired()) {
			return entry.name;
		}

		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
			String name = jsonObject.get("name").getAsString();

			cache.put(uuid, new CacheEntry(name));

			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static class CacheEntry {
		final String name;
		final Instant creationTime;

		CacheEntry(String name) {
			this.name = name;
			this.creationTime = Instant.now();
		}

		boolean isExpired() {
			return Instant.now().isAfter(creationTime.plus(CACHE_DURATION));
		}
	}
}
