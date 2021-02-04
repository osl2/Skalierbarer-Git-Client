package settings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import commands.ICommand;

import java.io.IOException;

public class ICommandSerializer extends StdSerializer<ICommand> {
    protected ICommandSerializer(Class<ICommand> t) {
        super(t);
    }

    public ICommandSerializer() {
        this(null);
    }

    @Override
    public void serialize(ICommand iCommand, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("classPath", iCommand.getClass().getCanonicalName());
        jsonGenerator.writeEndObject();
    }
}
