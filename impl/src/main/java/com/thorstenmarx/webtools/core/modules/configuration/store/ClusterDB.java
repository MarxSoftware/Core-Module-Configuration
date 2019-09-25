/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.core.modules.configuration.store;

import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.cluster.Message;
import com.thorstenmarx.webtools.api.cluster.services.MessageService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ClusterDB implements DB, MessageService.MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterDB.class);
	
	private final DB wrapped_db;
	private final Cluster cluster;

	final Gson gson = new Gson();

	private static final String REGISTRY_ADD = "entities_add";
	private static final String REGISTRY_DELETE = "entities_delete";
	private static final String REGISTRY_CLEAR = "entities_clear";

	public ClusterDB(final DB wrapped_db, final Cluster cluster) {
		this.wrapped_db = wrapped_db;
		this.cluster = cluster;
		this.cluster.getMessageService().registerMessageListener(this);
	}
	

	@Override
	public boolean add(String namespace, String key, String content) {
		PayloadAdd payload = new PayloadAdd();
		payload.namespace = namespace;
		payload.key = key;
		payload.content = content;
		
		Message message = new Message();
		message.setType(REGISTRY_ADD);
		message.setPayload(gson.toJson(payload));
		try {	
			cluster.getMessageService().publish(message);
			
			return true;
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
		return false;
	}

	@Override
	public void clear(String namespace) {
		PayloadClear payload = new PayloadClear();
		payload.namespace = namespace;
		
		Message message = new Message();
		message.setType(REGISTRY_CLEAR);
		message.setPayload(gson.toJson(payload));
		try {	
			cluster.getMessageService().publish(message);
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
	}

	@Override
	public int count(String namespace) {
		return wrapped_db.count(namespace);
	}

	@Override
	public void delete(String namespace) {
		PayloadDelete payload = new PayloadDelete();
		payload.namespace = namespace;
		
		Message message = new Message();
		message.setType(REGISTRY_DELETE);
		message.setPayload(gson.toJson(payload));
		try {	
			cluster.getMessageService().publish(message);
		} catch (IOException ex) {
			LOGGER.error("", ex);
		}
	}

	@Override
	public String get(String namespace, String key) {
		return wrapped_db.get(namespace, key);
	}

	@Override
	public void handle(Message message) {
		if (REGISTRY_ADD.equals(message.getType())) {
			PayloadAdd payload = gson.fromJson(message.getPayload(), PayloadAdd.class);
			wrapped_db.add(payload.namespace, payload.key, payload.content);
		} else if (REGISTRY_CLEAR.equals(message.getType())) {
			PayloadClear payload = gson.fromJson(message.getPayload(), PayloadClear.class);
			wrapped_db.clear(payload.namespace);
		} else if (REGISTRY_DELETE.equals(message.getType())) {
			PayloadDelete payload = gson.fromJson(message.getPayload(), PayloadDelete.class);
			wrapped_db.delete(payload.namespace);
		}
	}

	public void close() {
		this.cluster.getMessageService().unregisterMessageListener(this);
	}

	public static class PayloadAdd {

		public String namespace;
		public String key;
		public String content;
	}

	public static class PayloadClear {

		public String namespace;
	}

	public static class PayloadDelete {

		public String namespace;
	}
}
