package com.mymobkit.service;


interface IHttpdService
{
   	boolean isAlive();
    boolean isError();
   	String getUri();
   	String getErrorMsg();
}