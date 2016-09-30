/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\myspace\\2016\\Development\\mymobkit\\mymobkit\\myMobKit\\src\\main\\aidl\\com\\mymobkit\\service\\ICameraService.aidl
 */
package com.mymobkit.service;
public interface ICameraService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mymobkit.service.ICameraService
{
private static final java.lang.String DESCRIPTOR = "com.mymobkit.service.ICameraService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mymobkit.service.ICameraService interface,
 * generating a proxy if needed.
 */
public static com.mymobkit.service.ICameraService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mymobkit.service.ICameraService))) {
return ((com.mymobkit.service.ICameraService)iin);
}
return new com.mymobkit.service.ICameraService.Stub.Proxy(obj);
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
case TRANSACTION_hide:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.hide();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_show:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.show();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mymobkit.service.ICameraService
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
@Override public boolean hide() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hide, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean show() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_show, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
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
}
static final int TRANSACTION_hide = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_show = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isAlive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public boolean hide() throws android.os.RemoteException;
public boolean show() throws android.os.RemoteException;
public boolean isAlive() throws android.os.RemoteException;
}
