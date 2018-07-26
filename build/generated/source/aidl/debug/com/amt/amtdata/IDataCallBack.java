/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\demo\\AmtIPTV\\src\\main\\aidl\\com\\amt\\amtdata\\IDataCallBack.aidl
 */
package com.amt.amtdata;
public interface IDataCallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.amt.amtdata.IDataCallBack
{
private static final java.lang.String DESCRIPTOR = "com.amt.amtdata.IDataCallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.amt.amtdata.IDataCallBack interface,
 * generating a proxy if needed.
 */
public static com.amt.amtdata.IDataCallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.amt.amtdata.IDataCallBack))) {
return ((com.amt.amtdata.IDataCallBack)iin);
}
return new com.amt.amtdata.IDataCallBack.Stub.Proxy(obj);
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
case TRANSACTION_dataChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.dataChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.amt.amtdata.IDataCallBack
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
@Override public void dataChanged(java.lang.String key, java.lang.String oldValue, java.lang.String newValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(oldValue);
_data.writeString(newValue);
mRemote.transact(Stub.TRANSACTION_dataChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_dataChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void dataChanged(java.lang.String key, java.lang.String oldValue, java.lang.String newValue) throws android.os.RemoteException;
}
