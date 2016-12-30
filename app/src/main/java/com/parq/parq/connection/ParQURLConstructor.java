package com.parq.parq.connection;

import android.content.Context;
import android.net.Uri.Builder;

import com.parq.parq.R;

/**
 * Created by piotr on 27.12.16.
 */

public class ParQURLConstructor {
    private String authority;
    private Context context;

    public ParQURLConstructor(String authority, Context context) {
        this.authority = authority;
        this.context = context;
    }

    private Builder getBase() {
        return new Builder()
                .scheme("http")
                .encodedAuthority(authority);
    }

    public String getLoginURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_login));
        return builder.build().toString();
    }

    public String getTicketListURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_tickets));
        return builder.build().toString();
    }

    public String getTicketByBadgeURL(String badge) {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_tickets))
                .appendQueryParameter("badge", badge);
        return builder.build().toString();
    }

    public String getCurrentURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_current));
        return builder.build().toString();
    }

    public String getVehiclesURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_vehicles));
        return builder.build().toString();
    }
}
