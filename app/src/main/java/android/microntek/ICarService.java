package android.microntek;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICarService extends IInterface {

    public static abstract class Stub extends Binder implements ICarService {
        private static final String DESCRIPTOR = "android.microntek.ICarService";
        static final int TRANSACTION_getBooleanState = 8;
        static final int TRANSACTION_getByteArrayState = 12;
        static final int TRANSACTION_getByteState = 9;
        static final int TRANSACTION_getIntArrayState = 13;
        static final int TRANSACTION_getIntState = 10;
        static final int TRANSACTION_getParameters = 18;
        static final int TRANSACTION_getStringArrayState = 14;
        static final int TRANSACTION_getStringState = 11;
        static final int TRANSACTION_putBooleanState = 1;
        static final int TRANSACTION_putByteArraryState = 5;
        static final int TRANSACTION_putByteState = 2;
        static final int TRANSACTION_putDataChanage = 19;
        static final int TRANSACTION_putIntArraryState = 6;
        static final int TRANSACTION_putIntState = 3;
        static final int TRANSACTION_putStringArraryState = 7;
        static final int TRANSACTION_putStringState = 4;
        static final int TRANSACTION_registerCallback = 15;
        static final int TRANSACTION_setParameters = 17;
        static final int TRANSACTION_unregisterCallback = 16;

        private static class Proxy implements ICarService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void putBooleanState(String key, boolean value) throws RemoteException {
                int i = Stub.TRANSACTION_putBooleanState;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    if (!value) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_putBooleanState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putByteState(String key, byte value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeByte(value);
                    this.mRemote.transact(Stub.TRANSACTION_putByteState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putIntState(String key, int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeInt(value);
                    this.mRemote.transact(Stub.TRANSACTION_putIntState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putStringState(String key, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    this.mRemote.transact(Stub.TRANSACTION_putStringState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putByteArraryState(String key, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeByteArray(value);
                    this.mRemote.transact(Stub.TRANSACTION_putByteArraryState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putIntArraryState(String key, int[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeIntArray(value);
                    this.mRemote.transact(Stub.TRANSACTION_putIntArraryState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putStringArraryState(String key, String[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeStringArray(value);
                    this.mRemote.transact(Stub.TRANSACTION_putStringArraryState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getBooleanState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getBooleanState, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }

                return false;
            }

            public byte getByteState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getByteState, _data, _reply, 0);
                    _reply.readException();
                    byte _result = _reply.readByte();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getIntState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getIntState, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getStringState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getStringState, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getByteArrayState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getByteArrayState, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int[] getIntArrayState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getIntArrayState, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getStringArrayState(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_getStringArrayState, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerCallback(ICarManageCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterCallback(ICarManageCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int setParameters(String par) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(par);
                    this.mRemote.transact(Stub.TRANSACTION_setParameters, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getParameters(String par) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(par);
                    this.mRemote.transact(Stub.TRANSACTION_getParameters, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putDataChanage(String type, String state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeString(state);
                    this.mRemote.transact(Stub.TRANSACTION_putDataChanage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICarService)) {
                return new Proxy(obj);
            }
            return (ICarService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int _result;
            String _result2;
            switch (code) {
                case TRANSACTION_putBooleanState /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    putBooleanState(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putByteState /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    putByteState(data.readString(), data.readByte());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putIntState /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    putIntState(data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putStringState /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    putStringState(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putByteArraryState /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    putByteArraryState(data.readString(), data.createByteArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putIntArraryState /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    putIntArraryState(data.readString(), data.createIntArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_putStringArraryState /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    putStringArraryState(data.readString(), data.createStringArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getBooleanState /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = getBooleanState(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result3 ? TRANSACTION_putBooleanState : 0);
                    return true;
                case TRANSACTION_getByteState /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result4 = getByteState(data.readString());
                    reply.writeNoException();
                    reply.writeByte(_result4);
                    return true;
                case TRANSACTION_getIntState /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getIntState(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case TRANSACTION_getStringState /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getStringState(data.readString());
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case TRANSACTION_getByteArrayState /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _result5 = getByteArrayState(data.readString());
                    reply.writeNoException();
                    reply.writeByteArray(_result5);
                    return true;
                case TRANSACTION_getIntArrayState /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result6 = getIntArrayState(data.readString());
                    reply.writeNoException();
                    reply.writeIntArray(_result6);
                    return true;
                case TRANSACTION_getStringArrayState /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result7 = getStringArrayState(data.readString());
                    reply.writeNoException();
                    reply.writeStringArray(_result7);
                    return true;
                case TRANSACTION_registerCallback /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    registerCallback(android.microntek.ICarManageCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_unregisterCallback /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterCallback(android.microntek.ICarManageCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setParameters /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = setParameters(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case TRANSACTION_getParameters /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getParameters(data.readString());
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case TRANSACTION_putDataChanage /*19*/:
                    data.enforceInterface(DESCRIPTOR);
                    putDataChanage(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    boolean getBooleanState(String str) throws RemoteException;

    byte[] getByteArrayState(String str) throws RemoteException;

    byte getByteState(String str) throws RemoteException;

    int[] getIntArrayState(String str) throws RemoteException;

    int getIntState(String str) throws RemoteException;

    String getParameters(String str) throws RemoteException;

    String[] getStringArrayState(String str) throws RemoteException;

    String getStringState(String str) throws RemoteException;

    void putBooleanState(String str, boolean z) throws RemoteException;

    void putByteArraryState(String str, byte[] bArr) throws RemoteException;

    void putByteState(String str, byte b) throws RemoteException;

    void putDataChanage(String str, String str2) throws RemoteException;

    void putIntArraryState(String str, int[] iArr) throws RemoteException;

    void putIntState(String str, int i) throws RemoteException;

    void putStringArraryState(String str, String[] strArr) throws RemoteException;

    void putStringState(String str, String str2) throws RemoteException;

    void registerCallback(ICarManageCallback iCarManageCallback) throws RemoteException;

    int setParameters(String str) throws RemoteException;

    void unregisterCallback(ICarManageCallback iCarManageCallback) throws RemoteException;
}