package android.microntek.mtcser;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

public abstract class BTServiceInfStub extends Binder implements BTServiceInf {
    private static final String DESCRIPTOR = "android.microntek.mtcser.BTServiceInf";

    static final int TRANSACTION_IsConferenceCalling = 8;

    static final int TRANSACTION_IsThreeWayCalling = 7;

    static final int TRANSACTION_addCall = 22;

    static final int TRANSACTION_answerCall = 19;

    static final int TRANSACTION_avPlay = 14;

    static final int TRANSACTION_avPlayNext = 18;

    static final int TRANSACTION_avPlayPause = 15;

    static final int TRANSACTION_avPlayPrev = 17;

    static final int TRANSACTION_avPlayStop = 16;

    static final int TRANSACTION_connectBT = 36;

    static final int TRANSACTION_connectOBD = 38;

    static final int TRANSACTION_deleteBT = 41;

    static final int TRANSACTION_deleteHistory = 48;

    static final int TRANSACTION_deleteHistoryAll = 49;

    static final int TRANSACTION_deleteOBD = 40;

    static final int TRANSACTION_dialOut = 54;

    static final int TRANSACTION_dialOutSub = 55;

    static final int TRANSACTION_disconnectBT = 37;

    static final int TRANSACTION_disconnectOBD = 39;

    static final int TRANSACTION_getAVState = 3;

    static final int TRANSACTION_getAutoAnswer = 35;

    static final int TRANSACTION_getAutoConnect = 33;

    static final int TRANSACTION_getBTState = 2;

    static final int TRANSACTION_getCallInNum = 5;

    static final int TRANSACTION_getCallingNumberList = 10;

    static final int TRANSACTION_getDeviceList = 44;

    static final int TRANSACTION_getDialOutNum = 4;

    static final int TRANSACTION_getHistoryList = 45;

    static final int TRANSACTION_getMatchList = 43;

    static final int TRANSACTION_getModuleName = 28;

    static final int TRANSACTION_getModulePassword = 29;

    static final int TRANSACTION_getMusicInfo = 57;

    static final int TRANSACTION_getNowDevAddr = 11;

    static final int TRANSACTION_getNowDevName = 12;

    static final int TRANSACTION_getNowDevUuids = 13;

    static final int TRANSACTION_getOBDstate = 58;

    static final int TRANSACTION_getPhoneBookList = 46;

    static final int TRANSACTION_getPhoneNum = 6;

    static final int TRANSACTION_getThreeWayCallNum = 9;

    static final int TRANSACTION_hangupCall = 20;

    static final int TRANSACTION_init = 1;

    static final int TRANSACTION_mergeCall = 24;

    static final int TRANSACTION_musicMute = 50;

    static final int TRANSACTION_musicUnmute = 51;

    static final int TRANSACTION_reDial = 56;

    static final int TRANSACTION_rejectCall = 21;

    static final int TRANSACTION_requestBtInfo = 59;

    static final int TRANSACTION_scanStart = 52;

    static final int TRANSACTION_scanStop = 53;

    static final int TRANSACTION_setAutoAnswer = 34;

    static final int TRANSACTION_setAutoConnect = 32;

    static final int TRANSACTION_setModuleName = 30;

    static final int TRANSACTION_setModulePassword = 31;

    static final int TRANSACTION_setPhoneBookList = 47;

    static final int TRANSACTION_swichCall = 23;

    static final int TRANSACTION_switchVoice = 26;

    static final int TRANSACTION_syncMatchList = 42;

    static final int TRANSACTION_syncPhonebook = 27;

    static final int TRANSACTION_voiceControl = 25;

    public BTServiceInfStub() {
        attachInterface(this, "android.microntek.mtcser.BTServiceInf");
    }

    public static BTServiceInf asInterface(IBinder paramIBinder) {
        if (paramIBinder == null)
            return null;
        IInterface iInterface = paramIBinder.queryLocalInterface("android.microntek.mtcser.BTServiceInf");
        return (iInterface != null && iInterface instanceof BTServiceInf) ? (BTServiceInf)iInterface : new BTServiceInfStubProxy(paramIBinder);
    }

    public IBinder asBinder() {
        return (IBinder)this;
    }

    public boolean onTransact(int code, Parcel data, Parcel response, int flags) throws RemoteException {
        if (code != 1598968902) {
            boolean bool;
            String str1;
            String str4;
            List<String> list3;
            String str3;
            List<String> list2;
            String str2;
            List<String> list1;
            long l;
            byte b;
            boolean bool1 = false;
            boolean bool2 = false;
            switch (code) {
                default:
                    return super.onTransact(code, data, response, flags);
                case TRANSACTION_requestBtInfo:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    requestBtInfo();
                    response.writeNoException();
                    return true;
                case 58:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    code = getOBDstate();
                    response.writeNoException();
                    response.writeInt(code);
                    return true;
                case 57:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str4 = getMusicInfo();
                    response.writeNoException();
                    response.writeString(str4);
                    return true;
                case 56:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    reDial();
                    response.writeNoException();
                    return true;
                case 55:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    dialOutSub((char)data.readInt());
                    response.writeNoException();
                    return true;
                case 54:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    dialOut(data.readString());
                    response.writeNoException();
                    return true;
                case 53:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    scanStop();
                    response.writeNoException();
                    return true;
                case 52:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    scanStart();
                    response.writeNoException();
                    return true;
                case 51:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    musicUnmute();
                    response.writeNoException();
                    return true;
                case 50:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    musicMute();
                    response.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    deleteHistoryAll();
                    response.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    deleteHistory(data.readInt());
                    response.writeNoException();
                    return true;
                case 47:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    setPhoneBookList(data.createStringArrayList());
                    response.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list3 = getPhoneBookList();
                    response.writeNoException();
                    response.writeStringList(list3);
                    return true;
                case 45:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list3 = getHistoryList();
                    response.writeNoException();
                    response.writeStringList(list3);
                    return true;
                case 44:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list3 = getDeviceList();
                    response.writeNoException();
                    response.writeStringList(list3);
                    return true;
                case 43:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list3 = getMatchList();
                    response.writeNoException();
                    response.writeStringList(list3);
                    return true;
                case 42:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    syncMatchList();
                    response.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    deleteBT(data.readString());
                    response.writeNoException();
                    return true;
                case 40:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    deleteOBD(data.readString());
                    response.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    disconnectOBD(data.readString());
                    response.writeNoException();
                    return true;
                case 38:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    connectOBD(data.readString());
                    response.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    disconnectBT(data.readString());
                    response.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    connectBT(data.readString());
                    response.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    bool = getAutoAnswer();
                    response.writeNoException();
                    response.writeInt(bool ? 1 : 0);
                    return true;
                case 34:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    if (data.readInt() != 0)
                        bool2 = true;
                    setAutoAnswer(bool2);
                    response.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    bool = getAutoConnect();
                    response.writeNoException();
                    response.writeInt(bool ? 1 : 0);
                    return true;
                case 32:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    if (data.readInt() != 0) {
                        bool2 = true;
                    } else {
                        bool2 = bool1;
                    }
                    setAutoConnect(bool2);
                    response.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    setModulePassword(data.readString());
                    response.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    setModuleName(data.readString());
                    response.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str3 = getModulePassword();
                    response.writeNoException();
                    response.writeString(str3);
                    return true;
                case 28:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str3 = getModuleName();
                    response.writeNoException();
                    response.writeString(str3);
                    return true;
                case 27:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    syncPhonebook();
                    response.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    switchVoice();
                    response.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    voiceControl();
                    response.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    mergeCall();
                    response.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    swichCall();
                    response.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    addCall();
                    response.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    rejectCall();
                    response.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    hangupCall();
                    response.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    answerCall();
                    response.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    avPlayNext();
                    response.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    avPlayPrev();
                    response.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    avPlayStop();
                    response.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    avPlayPause();
                    response.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    avPlay();
                    response.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list2 = getNowDevUuids();
                    response.writeNoException();
                    response.writeStringList(list2);
                    return true;
                case 12:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str2 = getNowDevName();
                    response.writeNoException();
                    response.writeString(str2);
                    return true;
                case 11:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    l = getNowDevAddr();
                    response.writeNoException();
                    response.writeLong(l);
                    return true;
                case 10:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    list1 = getCallingNumberList();
                    response.writeNoException();
                    response.writeStringList(list1);
                    return true;
                case 9:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str1 = getThreeWayCallNum();
                    response.writeNoException();
                    response.writeString(str1);
                    return true;
                case 8:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    bool = IsConferenceCalling();
                    response.writeNoException();
                    response.writeInt(bool ? 1 : 0);
                    return true;
                case 7:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    bool = IsThreeWayCalling();
                    response.writeNoException();
                    response.writeInt(bool ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str1 = getPhoneNum();
                    response.writeNoException();
                    response.writeString(str1);
                    return true;
                case 5:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str1 = getCallInNum();
                    response.writeNoException();
                    response.writeString(str1);
                    return true;
                case 4:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    str1 = getDialOutNum();
                    response.writeNoException();
                    response.writeString(str1);
                    return true;
                case 3:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    b = getAVState();
                    response.writeNoException();
                    response.writeByte(b);
                    return true;
                case 2:
                    data.enforceInterface("android.microntek.mtcser.BTServiceInf");
                    b = getBTState();
                    response.writeNoException();
                    response.writeByte(b);
                    return true;
                case 1:
                    break;
            }
            data.enforceInterface("android.microntek.mtcser.BTServiceInf");
            init();
            response.writeNoException();
            return true;
        }
        response.writeString("android.microntek.mtcser.BTServiceInf");
        return true;
    }
}