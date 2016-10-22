package com.mymobkit.model;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.mymobkit.common.EntityHelper;

/**
 * 
 * Base class for all models.
 */
@Slf4j
public abstract class ModelBase {

	/**
	 * GAE data store entity.
	 */
	protected Entity entity;

	protected String key;

	public ModelBase(String key) {
		if (StringUtils.isBlank(key)) {
			key = EntityHelper.generateGuid();
		}
		this.key = key;
	}
	
	public abstract void getValuesFromEntity(Entity entity);
	protected abstract String getEntityKind();
	protected abstract void setEntityValues();

	public long getKeyId() {
		if (entity == null) entity = new Entity(getEntityKind(), key);
		return this.entity.getKey().getId();
	}

	public String getKeyName() {
		if (entity == null) entity = new Entity(getEntityKind(), key);
		return this.entity.getKey().getName();
	}
	
	public Entity getEntity(){
		setEntityValues();
		return this.entity;
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}

	public static <T extends ModelBase> boolean find(final T model, final String key) {
		DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
		Key searchKey = KeyFactory.createKey(model.getEntityKind(), key);
		try {
			Entity entity = dataStore.get(searchKey);
			model.setEntity(entity);
			model.getValuesFromEntity(entity);
			return true;
		} catch (EntityNotFoundException e) {
			log.info("Entity not found for " + key);
		}
		return false;
	}
	
	public static <T extends ModelBase> T fromEntity(Entity entity, Class<T> clazz){
		try {
			T obj = clazz.newInstance();
			obj.setEntity(entity);
			obj.getValuesFromEntity(entity);
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			log.warn("Error instantiating", e.getCause());
		}
		return null;
	}
	
	public void save(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(getEntity());		
	}
	
	public void delete(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete(entity.getKey());
	}

}
