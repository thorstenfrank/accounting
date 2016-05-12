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
import java.time.LocalDate;

import org.apache.log4j.Logger;

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
	
	private static final String SEPARATOR = " :|: ";
	
	/** */
	private static final Logger BUSINESS_LOG = Logger.getLogger(BusinessLogger.class); //$NON-NLS-1$

	/** */
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
			.create();
	
	/**
	 * 
	 * @param operation
	 * @param entity
	 */
	static void log(Operation operation, final AbstractBaseEntity entity) {
		final StringBuilder sb = new StringBuilder(entity.getClass().getName());
		sb.append(SEPARATOR).append(operation).append(SEPARATOR);
		sb.append(GSON.toJson(entity));
		BUSINESS_LOG.info(sb.toString());
	}
	
	/**
	 * 
	 * @author Thorsten Frank
	 *
	 */
	private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
		
		@Override
		public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
}
