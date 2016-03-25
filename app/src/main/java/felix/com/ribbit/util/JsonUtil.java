package felix.com.ribbit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by fsoewito on 3/25/2016.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
    }

    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }
}
