package com.backend.immilog.user.infrastructure.gateway;

import com.backend.immilog.shared.config.properties.GeocodeProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodeGateway {
    private final GeocodeProperties geocodeProperties;
    private final RestClient restClient;

    public GeocodeGateway(
            GeocodeProperties geocodeProperties,
            RestClient restClient
    ) {
        this.geocodeProperties = geocodeProperties;
        this.restClient = restClient;
    }

    public String fetchGeocode(
            double latitude,
            double longitude
    ) {
        var url = String.format(geocodeProperties.url(), latitude, longitude, geocodeProperties.key());
        return restClient.get().uri(url).retrieve().body(String.class);
    }
}
