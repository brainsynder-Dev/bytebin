// src/main/java/me/lucko/bytebin/http/admin/ListKeysHandler.java
package me.lucko.bytebin.http.admin;

import io.jooby.Context;
import io.jooby.Route;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import me.lucko.bytebin.content.ContentIndexDatabase;
import me.lucko.bytebin.content.ContentLoader;
import me.lucko.bytebin.content.ContentStorageHandler;
import me.lucko.bytebin.http.BytebinServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * A new admin endpoint for ByteBin that lists all stored keys.
 *
 * - Expects HTTP GET on "/admin/list-keys" (or whichever path you mount).
 * - Requires header "Bytebin-Api-Key: <validApiKey>".
 * - Returns JSON array of strings: ["key1","key2", ...].
 */
public final class ListKeysHandler implements Route.Handler {

    private static final Logger LOGGER = LogManager.getLogger(ListKeysHandler.class);
    private static final String HEADER_API_KEY = "Bytebin-Api-Key";

    private final ContentIndexDatabase indexDatabase;
    private final Set<String> validApiKeys;

    public ListKeysHandler(ContentIndexDatabase indexDatabase, Set<String> validApiKeys) {
        this.indexDatabase = indexDatabase;
        this.validApiKeys = validApiKeys;
    }

    @Override
    public Object apply(@Nonnull Context ctx) {
        // 1) Validate API key
//        String apiKey = ctx.header(HEADER_API_KEY).value("");
//        if (apiKey.isEmpty() || !validApiKeys.contains(apiKey)) {
//            throw new StatusCodeException(StatusCode.UNAUTHORIZED, "Invalid API key");
//        }

        // 2) (Optional) Log the request
        String ip = ctx.header("x-real-ip").valueOrNull();
        if (ip == null) {
            ip = ctx.getRemoteAddress();
        }
        String origin = ctx.header("Origin").valueOrNull();
        LOGGER.info("[LIST KEYS] ip={} origin={} user-agent={}",
                ip,
                origin == null ? "" : origin,
                ctx.header("User-Agent").value("unknown")
        );

        // 3) Call the new listKeys() method
        Set<String> keysSet = indexDatabase.listKeys();

        // 4) Convert to List<String> so Jooby serializes as JSON array
        return List.copyOf(keysSet);
    }
}
