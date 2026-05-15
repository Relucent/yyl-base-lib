package com.github.relucent.base.plugin.jackson.databind;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.plugin.jackson.JacksonConvertUtil;

public class MapxDeserializer extends JsonDeserializer<Mapx> {

	/**
	 * Singleton instance to use.
	 */
	public static final MapxDeserializer INSTANCE = new MapxDeserializer();

	@Override
	public Mapx deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = parser.getCodec().readTree(parser);
		if (node == null || node.isNull() || node.isMissingNode()) {
			return null;
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("Expected ObjectNode but got: " + node.getNodeType());
		}
		return JacksonConvertUtil.convertObject((ObjectNode) node);
	}

}
