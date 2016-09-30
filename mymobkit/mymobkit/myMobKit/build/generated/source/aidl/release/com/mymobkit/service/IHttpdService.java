/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\myspace\\2016\\Development\\mymobkit\\mymobkit\\myMobKit\\src\\main\\aidl\\com\\mymobkit\\service\\IHttpdService.aidl
 */
package com.mymobkit.service;
public interface IHttpdService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mymobkit.service.IHttpdService
{
private static final java.lang.String DESCRIPTOR = "com.mymobkit.service.IHttpdService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mymobkit.service.IHttpdService interface,
 * generating a proxy if needed.
 */
public static com.mymobkit.service.IHttpdService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mymobkit.service.IHttpdService))) {
return ((com.mymobkit.service.IHttpdService)iin);
}
return new com.mymobkit.service.IHttpdService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isAlive:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isAlive();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isError:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isError();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getUri:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getUri();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getErrorMsg:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getErrorMsg();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mymobkit.service.IHttpdService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean isAlive() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isAlive, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isError() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isError, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getUri() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUri, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getErrorMsg() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getErrorMsg, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_isAlive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getUri = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getErrorMsg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public boolean isAlive() throws android.os.RemoteException;
public boolean isError() throws android.os.RemoteException;
public java.lang.String getUri() throws android.os.RemoteException;
public java.lang.String getErrorMsg() throws android.os.RemoteException;
}
