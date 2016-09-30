package com.mymobkit.data.contact;

import java.util.List;

public class ResolvedContact {
    private String humanReadableName;
    private String canonicalPhoneNumber;
    
    private ResolvedContact[] possibleCandidates;
        
    ResolvedContact(String name, String number) {
        humanReadableName = name;
        canonicalPhoneNumber = number;        
    }
    
    ResolvedContact(List<ResolvedContact> candidates) {
        possibleCandidates = new ResolvedContact[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            possibleCandidates[i] = candidates.get(i);
        }
    }
    
    public String getName() {        
        return humanReadableName;        
    }
    
    public String getNumber() {
        return canonicalPhoneNumber;
    }
    
    public ResolvedContact[] getCandidates() {
        return possibleCandidates;        
    }
    
    public boolean isDistinct() {
        return (possibleCandidates == null);
    }
}
