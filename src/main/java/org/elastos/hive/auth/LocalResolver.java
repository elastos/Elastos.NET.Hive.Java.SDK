package org.elastos.hive.auth;

import org.elastos.hive.AuthToken;

public class LocalResolver implements TokenResolver {

	@Override
	public AuthToken getToken() {
		return null;
	}

	@Override
	public void saveToken() {
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {

	}
}