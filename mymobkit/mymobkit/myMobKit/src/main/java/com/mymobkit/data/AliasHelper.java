package com.mymobkit.data;

import java.util.ArrayList;

import com.mymobkit.data.contact.ContactsManager;
import com.mymobkit.data.phone.Phone;

import android.content.Context;

/**
 * Middle-end Helper, if we ever want to integrate the Alias function also in
 * the App itself: This is the way to go.
 * At one place in the code, the constructor needs to be called, to make sure
 * that the Database is setup correctly.
 * 
 *
 */
public final class AliasHelper {
    private final Context ctx;
    private static AliasHelper aliasHelper;
    
    /**
     * This constructor ensures that the database is setup correctly
     * @param ctx
     */
    private AliasHelper(Context ctx) {
        new AliasTable(ctx);
        this.ctx = ctx;
    }
    
    public static AliasHelper getAliasHelper(Context ctx) {
        if (aliasHelper == null) {
            aliasHelper = new AliasHelper(ctx);
        }
        return aliasHelper;
    }
    
    /**
     * Adds an alias by a phone number
     * if the alias contains an invalid character, false will be returned
     * 
     * @param aliasName
     * @param number
     * @return
     */
    public boolean addAliasByNumber(String aliasName, String number) {
        if (aliasName.contains("'"))
            return false;
        
        String contactName = ContactsManager.getContactNameOrNull(ctx, number);
        addOrUpdate(aliasName, number, contactName);
        return true;
    }
    
    /**
     * Tries to add or update a alias
     * Returns null if the aliasName contains invalid characters
     * Returns an ArrayList of Phones with matching names
     * If the list has a size of 1 the name was distinct enough to
     * and the alias was added. Otherwise the user should specify more details
     * with help from the list
     * 
     * @param aliasName
     * @param name
     * @return
     */
    public ArrayList<Phone> addAliasByName(String aliasName, String name) {
        if (aliasName.contains("'"))
            return null;
        
        ArrayList<Phone> res;
        res = ContactsManager.getMobilePhones(ctx, name);
        if (res.size() == 1) {
            Phone p = res.get(0);
            addOrUpdate(aliasName, p.getCleanNumber(), p.getContactName());
        }
        return res;
    }
    
    /**
     * Deletes an Alias
     * 
     * @param aliasName
     * @return true if successful, otherwise false
     */
    public boolean deleteAlias(String aliasName) {
        if(!aliasName.contains("'") && AliasTable.containsAlias(aliasName)) {
            return AliasTable.deleteAlias(aliasName);
        } else {
            return false;
        }
    }
    
    /**
     * Converts an given alias to a unique phone number
     * 
     * @param aliasName
     * @return the phone number, or the given alias if there is no number
     */
    public String convertAliasToNumber(String aliasName) {
        if(!aliasName.contains("'") && AliasTable.containsAlias(aliasName)) {
            String[] res = AliasTable.getAlias(aliasName); 
            return res[1];
        } 
        return aliasName;
    }
    
    public String[] getAliasOrNull(String aliasName) {
        if(!aliasName.contains("'") && AliasTable.containsAlias(aliasName)) {
            return AliasTable.getAlias(aliasName);
        } 
        return null;
    }
    
    /**
     * Returns the all known aliases
     * or null if there are none
     * 
     * @return
     */
    public String[][] getAllAliases() {
        String[][] res = AliasTable.getFullDatabase();
        if (res.length == 0)
            res = null;
        return res;
    }
    
    private static void addOrUpdate(String aliasName, String number, String contactName) {
       if(AliasTable.containsAlias(aliasName)) {
           AliasTable.updateAlias(aliasName, number, contactName);
       } else {
           AliasTable.addAlias(aliasName, number, contactName);
       }
    }
}
