package settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import commands.ICommand;
import commands.ICommandGUI;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ICommandDeserializer extends StdDeserializer<ICommandGUI> {
    protected ICommandDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unused") /* Jackson uses it. */
    public ICommandDeserializer() {
        this(null);
    }

    @Override
    public ICommandGUI deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String classPath = node.get("classPath").asText();
        try {
            Class<?> cl = this.getClass().getClassLoader().loadClass(classPath);

            // Class implements ICommand?
            if (ICommand.class.isAssignableFrom(cl)) {
                return (ICommandGUI) cl.getConstructor().newInstance();
            } else {
                throw new IOException("Could not create instance of " + classPath);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IOException(e);
        }
    }
}
