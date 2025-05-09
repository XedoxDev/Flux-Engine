package org.xedox.engine;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.io.Input;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import org.xedox.engine.objects.BaseObject;
import org.xedox.engine.objects.ObjectScript;
import org.xedox.engine.objects.TextureObject;

public class KryoEnvironment {
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        configureKryo(kryo);
        return kryo;
    });

    private static void configureKryo(Kryo kryo) {
        kryo.setRegistrationRequired(true);
        kryo.setReferences(true);
        
        kryo.register(ArrayList.class);
        kryo.register(String.class);
        kryo.register(Float.class);
        kryo.register(Integer.class);
        kryo.register(Boolean.class);

        kryo.register(Scene.class, new Scene.Serializer());
        kryo.register(TextureObject.class, new TextureObject.Serializer());
        kryo.register(Vector2.class, new Vector2Ser());
    }

    public static byte[] serialize(Object object) {
        if (object == null) throw new IllegalArgumentException("Cannot serialize null object");
        
        Kryo kryo = kryoThreadLocal.get();
        try (Output output = new Output(4096, -1)) {
            kryo.writeObject(output, object);
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) {
        if (bytes == null) throw new IllegalArgumentException("Cannot deserialize null bytes");
        
        Kryo kryo = kryoThreadLocal.get();
        try (Input input = new Input(bytes)) {
            return kryo.readObject(input, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    public static class Vector2Ser extends Serializer<Vector2> {
        @Override
        public void write(Kryo kryo, Output output, Vector2 vector) {
            output.writeBoolean(vector != null);
            if (vector != null) {
                output.writeFloat(vector.x);
                output.writeFloat(vector.y);
            }
        }

        @Override
        public Vector2 read(Kryo kryo, Input input, Class<? extends Vector2> type) {
            return input.readBoolean() ? new Vector2(input.readFloat(), input.readFloat()) : null;
        }
    }
}