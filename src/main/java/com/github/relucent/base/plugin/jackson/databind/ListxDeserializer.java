package com.github.relucent.base.plugin.jackson.databind;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.plugin.jackson.JacksonConvertUtil;

public class ListxDeserializer extends JsonDeserializer<Listx> {

	/**
	 * Singleton instance to use.
	 */
	public static final ListxDeserializer INSTANCE = new ListxDeserializer();

	@Override
	public Listx deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = parser.getCodec().readTree(parser);
		if (node == null || node.isNull() || node.isMissingNode()) {
			return null;
		}
		if (!node.isArray()) {
			throw new IllegalArgumentException("Expected ArrayNode but got: " + node.getNodeType());
		}
		return JacksonConvertUtil.convertArray((ArrayNode) node);
	}

}
