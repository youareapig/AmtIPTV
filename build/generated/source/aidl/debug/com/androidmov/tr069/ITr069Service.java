/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\demo\\AmtIPTV\\src\\main\\aidl\\com\\androidmov\\tr069\\ITr069Service.aidl
 */
package com.androidmov.tr069;
public interface ITr069Service extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.androidmov.tr069.ITr069Service
{
private static final java.lang.String DESCRIPTOR = "com.androidmov.tr069.ITr069Service";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.androidmov.tr069.ITr069Service interface,
 * generating a proxy if needed.
 */
public static com.androidmov.tr069.ITr069Service asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.androidmov.tr069.ITr069Service))) {
return ((com.androidmov.tr069.ITr069Service)iin);
}
return new com.androidmov.tr069.ITr069Service.Stub.Proxy(obj);
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
case TRANSACTION_setValueToTr069:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.setValueToTr069(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getValueFromTr069:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getValueFromTr069(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_notifyMessageTotr069:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
this.notifyMessageTotr069(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.androidmov.tr069.ITr069Service
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
@Override public boolean setValueToTr069(java.lang.String key, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setValueToTr069, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getValueFromTr069(java.lang.String key) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
mRemote.transact(Stub.TRANSACTION_getValueFromTr069, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void notifyMessageTotr069(java.lang.String msg, java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(msg);
_data.writeString(arg0);
_data.writeString(arg1);
_data.writeString(arg2);
mRemote.transact(Stub.TRANSACTION_notifyMessageTotr069, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setValueToTr069 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getValueFromTr069 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_notifyMessageTotr069 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public boolean setValueToTr069(java.lang.String key, java.lang.String value) throws android.os.RemoteException;
public java.lang.String getValueFromTr069(java.lang.String key) throws android.os.RemoteException;
public void notifyMessageTotr069(java.lang.String msg, java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws android.os.RemoteException;
}
