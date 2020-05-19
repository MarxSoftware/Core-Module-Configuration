/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.core.modules.configuration.store;

import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.cluster.ClusterMessageAdapter;
import com.thorstenmarx.webtools.api.cluster.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ClusterDB implements DB, ClusterMessageAdapter<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterDB.class);

	private final DB wrapped_db;
	private final ClusterService cluster;

	final Gson gson = new Gson();

	private static final String REGISTRY_TYPE = "registry_type";
	
	public enum Command {
		ADD,
		DELETE,
		CLEAR
	}
	private static final String REGISTRY_ADD = "registry_add";
	private static final String REGISTRY_DELETE = "registry_delete";
	private static final String REGISTRY_CLEAR = "registry_clear";

	public ClusterDB(final DB wrapped_db, final ClusterService cluster) {
		this.wrapped_db = wrapped_db;
		this.cluster = cluster;
		this.cluster.registerAdpater(this);
	}

	@Override
	public boolean add(String namespace, String key, String content) {
		Payload payload = new Payload();
		payload.namespace = namespace;
		payload.command = Command.ADD;
		payload.key = key;
		payload.content = content;

		wrapped_db.add(namespace, key, content);
		cluster.replicate(REGISTRY_TYPE, gson.toJson(payload));
		
		return true;
	}

	@Override
	public void clear(String namespace) {
		Payload payload = new Payload();
		payload.namespace = namespace;
		payload.command = Command.CLEAR;

		wrapped_db.delete(namespace);
		cluster.replicate(REGISTRY_TYPE, gson.toJson(payload));
	}

	@Override
	public int count(String namespace) {
		return wrapped_db.count(namespace);
	}

	@Override
	public void delete(String namespace) {

		Payload payload = new Payload();
		payload.namespace = namespace;
		payload.command = Command.CLEAR;

		wrapped_db.delete(namespace);
		cluster.replicate(REGISTRY_TYPE, gson.toJson(payload));
	}

	@Override
	public String get(String namespace, String key) {
		return wrapped_db.get(namespace, key);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public String getType() {
		return REGISTRY_TYPE;
	}

	@Override
	public void reset() {
		this.clearAll();
	}

	@Override
	public void apply(final String value) {
		final Payload payload = gson.fromJson(value, Payload.class);
		if (Command.ADD.equals(payload.command)) {
			wrapped_db.add(payload.namespace, payload.key, payload.content);
		} else if (Command.CLEAR.equals(payload.command)) {
			wrapped_db.clear(payload.namespace);
		} else if (Command.DELETE.equals(payload.command)) {
			wrapped_db.delete(payload.namespace);
		}
	}

	@Override
	public void clearAll() {
		wrapped_db.clearAll();
	}

	public static class Payload {

		public String namespace;
		public String key;
		public String content;
		public Command command;
	}
}
