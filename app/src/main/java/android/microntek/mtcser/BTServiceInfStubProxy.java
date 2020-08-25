package android.microntek.mtcser;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

class BTServiceInfStubProxy implements BTServiceInf {
    private IBinder mRemote;

    BTServiceInfStubProxy(IBinder paramIBinder) {
        this.mRemote = paramIBinder;
    }

    public boolean IsConferenceCalling() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            IBinder iBinder = this.mRemote;
            boolean bool = false;
            iBinder.transact(8, parcel1, parcel2, 0);
            parcel2.readException();
            int i = parcel2.readInt();
            if (i != 0)
                bool = true;
            return bool;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public boolean IsThreeWayCalling() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            IBinder iBinder = this.mRemote;
            boolean bool = false;
            iBinder.transact(7, parcel1, parcel2, 0);
            parcel2.readException();
            int i = parcel2.readInt();
            if (i != 0)
                bool = true;
            return bool;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void addCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(22, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void answerCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(19, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public IBinder asBinder() {
        return this.mRemote;
    }

    public void avPlay() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(14, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void avPlayNext() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(18, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void avPlayPause() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(15, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void avPlayPrev() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(17, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void avPlayStop() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(16, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void connectBT(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(36, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void connectOBD(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(38, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void deleteBT(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(41, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void deleteHistory(int paramInt) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeInt(paramInt);
            this.mRemote.transact(48, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void deleteHistoryAll() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(49, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void deleteOBD(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(40, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void dialOut(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(54, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void dialOutSub(char paramChar) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeInt(paramChar);
            this.mRemote.transact(55, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void disconnectBT(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(37, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void disconnectOBD(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(39, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public byte getAVState() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(3, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readByte();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public boolean getAutoAnswer() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            IBinder iBinder = this.mRemote;
            boolean bool = false;
            iBinder.transact(35, parcel1, parcel2, 0);
            parcel2.readException();
            int i = parcel2.readInt();
            if (i != 0)
                bool = true;
            return bool;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public boolean getAutoConnect() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            IBinder iBinder = this.mRemote;
            boolean bool = false;
            iBinder.transact(33, parcel1, parcel2, 0);
            parcel2.readException();
            int i = parcel2.readInt();
            if (i != 0)
                bool = true;
            return bool;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public byte getBTState() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(2, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readByte();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getCallInNum() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(5, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public List<String> getCallingNumberList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(10, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public List<String> getDeviceList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(44, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getDialOutNum() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(4, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public List<String> getHistoryList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(45, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getInterfaceDescriptor() {
        return "android.microntek.mtcser.BTServiceInf";
    }

    public List<String> getMatchList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(43, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getModuleName() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(28, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getModulePassword() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(29, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getMusicInfo() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(57, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public long getNowDevAddr() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(11, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readLong();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getNowDevName() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(12, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public List<String> getNowDevUuids() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(13, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public int getOBDstate() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(58, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readInt();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public List<String> getPhoneBookList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(46, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.createStringArrayList();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getPhoneNum() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(6, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public String getThreeWayCallNum() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(9, parcel1, parcel2, 0);
            parcel2.readException();
            return parcel2.readString();
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void hangupCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(20, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void init() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(1, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void mergeCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(24, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void musicMute() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(50, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void musicUnmute() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(51, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void reDial() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(56, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void rejectCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(21, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void requestBtInfo() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(59, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void scanStart() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(52, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void scanStop() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(53, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void setAutoAnswer(boolean paramBoolean) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeInt(paramBoolean ? 1 : 0);
            this.mRemote.transact(34, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void setAutoConnect(boolean paramBoolean) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeInt(paramBoolean ? 1 : 0);
            this.mRemote.transact(32, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void setModuleName(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(30, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void setModulePassword(String paramString) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeString(paramString);
            this.mRemote.transact(31, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void setPhoneBookList(List<String> paramList) throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            parcel1.writeStringList(paramList);
            this.mRemote.transact(47, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void swichCall() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(23, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void switchVoice() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(26, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void syncMatchList() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(42, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void syncPhonebook() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(27, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }

    public void voiceControl() throws RemoteException {
        Parcel parcel1 = Parcel.obtain();
        Parcel parcel2 = Parcel.obtain();
        try {
            parcel1.writeInterfaceToken("android.microntek.mtcser.BTServiceInf");
            this.mRemote.transact(25, parcel1, parcel2, 0);
            parcel2.readException();
            return;
        } finally {
            parcel2.recycle();
            parcel1.recycle();
        }
    }
}
