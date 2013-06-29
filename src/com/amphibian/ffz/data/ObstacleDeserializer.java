package com.amphibian.ffz.data;

import java.lang.reflect.Type;

import com.amphibian.ffz.Obstacle;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ObstacleDeserializer implements JsonDeserializer<Obstacle> {

	@Override
	public Obstacle deserialize(JsonElement element, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		
		JsonObject o = element.getAsJsonObject();
		
		String otype = o.get("type").getAsString();
		float x = o.get("x").getAsFloat();
		float y = o.get("y").getAsFloat();
		
		Obstacle obstacle = new Obstacle(otype, x, y);
		
		return obstacle;
		
	}

}
