/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import commands.ICommandGUI;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Deserializer for ICommandGUI instances.
 */
public class ICommandDeserializer extends StdDeserializer<ICommandGUI> {

    protected ICommandDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Constructor for Jackson,
     */
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

            // Class implements ICommandGUI?
            if (ICommandGUI.class.isAssignableFrom(cl)) {
                return (ICommandGUI) cl.getConstructor().newInstance();
            } else {
                throw new IOException("Could not create instance of " + classPath);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IOException(e);
        }
    }
}
