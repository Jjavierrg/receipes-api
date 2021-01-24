package converters;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import controllers.dto.BaseDto;
import converters.json.JsonConverter;
import converters.xml.IXmlConverter;


public class ConverterFactory {
    @Inject
    private Injector injector;

    public <TDto extends BaseDto> IConverter<TDto> getConverter(Class<TDto> dtoClass, String requestType) {
        if (requestType.equals("application/xml")) {
            var type = TypeLiteral.get(Types.newParameterizedType(IXmlConverter.class, dtoClass));
            return (IConverter<TDto>) this.injector.getInstance(Key.get(type));
        }

        return new JsonConverter<>(dtoClass);
    }
}
