package settings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import commands.ICommandGUI;

import java.io.IOException;

public class ICommandSerializer extends StdSerializer<ICommandGUI> {
    protected ICommandSerializer(Class<ICommandGUI> t) {
        super(t);
    }

    // Do not remove! Needed by Jackson.
    public ICommandSerializer() {
        this(null);
    }

    @Override
    public void serialize(ICommandGUI iCommand, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("classPath", iCommand.getClass().getCanonicalName());
        jsonGenerator.writeEndObject();
    }
}
