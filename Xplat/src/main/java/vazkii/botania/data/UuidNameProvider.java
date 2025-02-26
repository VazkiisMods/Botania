package vazkii.botania.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Nullable;

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
	private static final Map<String, FailureEntry> failureTimes = new ConcurrentHashMap<>();
	private static final Duration CACHE_DURATION = Duration.ofMinutes(15);
	private static final int MAX_FAILURES = 5;

	public static String getPlayerNameFromUUID(String uuid) {
		CacheEntry entry = cache.get(uuid);
		boolean isCacheExpired = entry == null || entry.isExpired();

		if (!isCacheExpired) {
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
			if (jsonObject.has("name")) {
				String name = jsonObject.get("name").getAsString();

				cache.put(uuid, new CacheEntry(name));
				failureTimes.remove(uuid); // Reset failure count on success

				return name;
			} else {
				return SaveFailureData(uuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return SaveFailureData(uuid);
		}
	}

	@Nullable
	private static String SaveFailureData(String uuid) {
		recordFailureTime(uuid);
		FailureEntry failureEntry = failureTimes.getOrDefault(uuid, new FailureEntry());
		if (failureEntry.failures >= MAX_FAILURES) {
			cache.put(uuid, new CacheEntry(uuid)); // Store UUID as Name
			failureTimes.remove(uuid); // Reset failure count after storing UUID as Name
			return uuid;
		}
		return null;
	}

	private static void recordFailureTime(String uuid) {
		FailureEntry failureEntry = failureTimes.computeIfAbsent(uuid, k -> new FailureEntry());
		failureEntry.failures++;
		failureEntry.lastFailureTime = Instant.now();
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

	private static class FailureEntry {
		int failures = 0;
		Instant lastFailureTime = Instant.now();
	}
}
