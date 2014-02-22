package org.home.blackjack.infrastructure.persistence.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GsonProvider {
	
	protected GsonBuilder gsonBuilder = new GsonBuilder();
	
	public Gson gson() {
		return gsonBuilder.create();
	}

}
