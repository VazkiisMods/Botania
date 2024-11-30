package vazkii.botania.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UuidNameProvider {

	public static String getPlayerNameFromUUID(String uuid) {
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
			return jsonObject.get("name").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
