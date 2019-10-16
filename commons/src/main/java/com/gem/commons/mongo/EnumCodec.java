package com.gem.commons.mongo;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class EnumCodec<T extends Enum<T>> implements Codec<T> {


	private final Class<T> clazz;


	public EnumCodec(Class<T> clazz){
		this.clazz = clazz;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		var name = reader.readString();
		if(name == null){
			return null;
		}
		return Enum.valueOf(clazz, name);
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		if(value == null){
			writer.writeNull();
		}else{
			writer.writeString(value.toString());
		}
	}

	@Override
	public Class<T> getEncoderClass() {
		return clazz;
	}
}
