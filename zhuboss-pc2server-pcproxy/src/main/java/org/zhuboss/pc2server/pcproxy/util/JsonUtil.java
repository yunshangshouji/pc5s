package org.zhuboss.pc2server.pcproxy.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.map.DeserializationConfig;

public class JsonUtil {
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class) ;
	private static ObjectMapper mapper = new ObjectMapper(); //must reuse!!
	static{
//		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		mapper.getDeserializationConfig().withDateFormat(df);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String serializeToJson(Object obj, String... ignoreProperties) {
//		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		FilterProvider filterProvider = new SimpleFilterProvider().addFilter(
				"ignorePropertiesFilter",
				SimpleBeanPropertyFilter.serializeAllExcept(ignoreProperties));
		String json;
		try {
			json = mapper.writer(filterProvider).writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		return json;
	}

	public static <T> T unserializeFromJson(String json, Class<T> clazz)
			 {
		
		try {
			T object=null;
			if(json!=null && json.length()>0){
				object = mapper.readValue(json, clazz);
			}
			
			return object;
		} catch (Exception e) {
			logger.error("can not serialize:"+json);
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}

	public static <T> List<T> unserializeFromJsonAsList(String json,
			Class<T> cls)  {
		try {
			List<T> object = mapper.readValue(json, mapper.getTypeFactory()
					.constructCollectionType(ArrayList.class, cls));
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}

	public static <T>  T unserializeFromJsonByTypeReference(String json,
			TypeReference<T> typeReference) throws JsonParseException, JsonMappingException,
			IOException {
		T object = mapper.readValue(json, typeReference);
		return object;
	}
	public static <T> T convertValue(Object fromValue, Class<T> toValueType){
		return mapper.convertValue(fromValue, toValueType);
	}
}
