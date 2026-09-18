package com.mapsindoorsrn.core;

import android.graphics.Bitmap;

import com.facebook.react.bridge.Promise;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapsindoors.core.errors.MIError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.Reference;
import java.nio.Buffer;

/**
 * The Gson instance every React module uses to put SDK model objects on the bridge.
 *
 * A bare {@code new Gson()} serialises by reflection, and reflection does not stop at the SDK's
 * public API. {@code MPLocation} holds a non-transient {@code MPIcon} whose {@code IconLayer}
 * holds an {@code android.graphics.Bitmap}; a Bitmap is native-backed, and the buffer behind it
 * carries a {@code sun.misc.Cleaner}. Every Cleaner alive in the process is a node in one
 * doubly-linked list joined by {@code next}/{@code prev}, so reflecting into one walks all of
 * them. The serialised depth then tracks how many native buffers the *host app* has alive rather
 * than anything about the map data - which is why this reproduces on a physical device with a
 * real app around it and not on an emulator (MS-3983).
 *
 * Excluding these types is safe for the wire contract: the JavaScript models never read a
 * serialised bitmap. {@code MPLocation.imageUrl} comes from {@code properties.imageUrl}, a string.
 */
final class RnGson {

    private RnGson() {
    }

    static Gson create() {
        return new GsonBuilder()
                .addSerializationExclusionStrategy(new RuntimeInternalsExclusion())
                .create();
    }

    /**
     * Rejects {@code promise} with a payload built without Gson.
     *
     * The ordinary reject paths serialise an {@code MPError}, whose {@code tag} is a bare
     * {@code Object} carrying whatever the SDK put in {@code MIError.tag}. Recovering from a
     * serialisation failure by reflecting into another untyped field risks a second throw, which
     * would escape the catch and take the host app down - the outcome the guards exist to prevent.
     * {@code JSONObject} does no reflection, so this path cannot fail the same way.
     *
     * <p>The shape matches {@code MPError}'s own {@code @SerializedName} values, so {@code
     * MPError.parse} on the TypeScript side reads it like any other error.</p>
     *
     * @param code the module's own reject code, so a serialisation failure is reported under the
     *             same code as that module's other rejections
     */
    static void rejectSerialisationFailure(Promise promise, String code, String message) {
        JSONObject error = new JSONObject();
        try {
            error.put("code", MIError.UNKNOWN_ERROR);
            error.put("message", message);
        } catch (JSONException ignored) {
            // Only thrown for a NaN or infinite value, neither of which can occur here.
        }
        promise.reject(code, error.toString());
    }

    /**
     * Cuts the paths out of the SDK's models and into the runtime.
     *
     * {@link Reference} is what actually stops MS-3983: {@code sun.misc.Cleaner} extends
     * {@code PhantomReference}, so this clause is what breaks the cleaner chain. The rest close
     * the neighbouring doors - a Bitmap or Buffer field is the usual way in, and a Thread or
     * ClassLoader reference reaches most of the runtime in a few hops.
     */
    private static final class RuntimeInternalsExclusion implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return isRuntimeInternal(f.getDeclaredClass());
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return isRuntimeInternal(clazz);
        }

        private static boolean isRuntimeInternal(Class<?> raw) {
            if (Reference.class.isAssignableFrom(raw)
                    || Bitmap.class.isAssignableFrom(raw)
                    || Buffer.class.isAssignableFrom(raw)
                    || Thread.class.isAssignableFrom(raw)
                    || ClassLoader.class.isAssignableFrom(raw)
                    || Class.class == raw) {
                return true;
            }

            final String name = raw.getName();

            return name.startsWith("sun.")
                    || name.startsWith("jdk.")
                    || name.startsWith("dalvik.")
                    || name.startsWith("libcore.")
                    || name.startsWith("java.lang.invoke.")
                    || name.startsWith("java.lang.ref.");
        }
    }
}
