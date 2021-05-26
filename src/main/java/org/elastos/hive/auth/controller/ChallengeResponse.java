package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class ChallengeResponse {
    @SerializedName("jwt")
    private final String challengeResponse;

    public ChallengeResponse(String challengeResponse) {
        this.challengeResponse = challengeResponse;
    }
}