/*
 *  Copyright 2016 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.service;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.tfsw.accounting.model.AbstractBaseEntity;

/**
 * 
 * @author Thorsten Frank
 *
 */
class BusinessLogger {
	
	/** */
	static enum Operation {
		SAVE, DELETE;
	}
		
	/** */
	private static final Logger BUSINESS_LOG = LogManager.getLogger(BusinessLogger.class); //$NON-NLS-1$

	/** */
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
			.registerTypeAdapter(Instant.class, new InstantSerializer())
			.create();
	
	/**
	 * 
	 * @param operation
	 * @param entity
	 */
	static void log(Operation operation, final AbstractBaseEntity entity) {
		WriteOperation op = new WriteOperation();
		op.setEntity(entity);
		op.setOperation(operation);
		op.setTimestamp(Instant.now());
		BUSINESS_LOG.info(GSON.toJson(op));
	}
	
	private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
		
		@Override
		public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	
	private static class InstantSerializer implements JsonSerializer<Instant> {
		@Override
		public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	
	static class WriteOperation {
		private Operation operation;
		private Instant timestamp;
		private String type;
		private AbstractBaseEntity entity;
		
		public Operation getOperation() {
			return operation;
		}
		public void setOperation(Operation operation) {
			this.operation = operation;
		}
		public Instant getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Instant timestamp) {
			this.timestamp = timestamp;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public AbstractBaseEntity getEntity() {
			return entity;
		}
		public void setEntity(AbstractBaseEntity entity) {
			this.type = entity.getClass().getName();
			this.entity = entity;
		}
	}
}
