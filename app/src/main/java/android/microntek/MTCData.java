package android.microntek;

public class MTCData {
    public static final int MAX_BALANCE = 28;
    public static final int MAX_EQ = 20;
    public static final int af = 22;
    public static final int atvmode = 11;
    public static final int avinAudioOnly = 26;
    public static final int backgroundvideo = 17;
    public static final int backscreenMode = 29;
    public static final int backviewvol = 8;
    public static final int barbacklight = 16;
    public static final int bareject = 15;
    public static final int barvolume = 14;
    public static final int carLogoDisable = 27;
    public static final int carrecloc = 12;
    public static final int drvingsafe = 2;
    public static final int dvrphotodisable = 23;
    public static final int gpsbackvolume = 25;
    public static final int gpsmix = 5;
    public static final int gpsphonevol = 24;
    public static final int gpssmonitor = 3;
    public static final int gpsswitch = 4;
    public static final int gpstime = 13;
    public static final int instruction = 10;
    public static final int kernelConfig = 28;
    public static final int[][] music_stytle_data;
    public static final int[][] music_stytle_data9;
    public static final int musicautoplay = 6;
    public static final int muticamera = 1;
    public static final int nozhja = 9;
    public static final int otherui = 0;
    public static final int screenclock = 18;
    public static final int screenshootloc = 21;
    public static final int screentimeout = 19;
    public static final int time_24 = 7;
    public static final int videogesture = 20;

    static {
        int[][] iArr = new int[musicautoplay][];
        iArr[otherui] = new int[]{screenclock, backviewvol, screenclock};
        iArr[muticamera] = new int[]{atvmode, screenclock, nozhja};
        iArr[drvingsafe] = new int[]{barbacklight, musicautoplay, backgroundvideo};
        iArr[gpssmonitor] = new int[]{screenclock, musicautoplay, screenclock};
        iArr[gpsswitch] = new int[]{instruction, instruction, instruction};
        iArr[gpsmix] = new int[]{backviewvol, barbacklight, backviewvol};
        music_stytle_data = iArr;
        iArr = new int[musicautoplay][];
        iArr[otherui] = new int[]{videogesture, screenclock, barbacklight, nozhja, musicautoplay, nozhja, barbacklight, screenclock, videogesture};
        iArr[muticamera] = new int[]{nozhja, atvmode, gpstime, backgroundvideo, videogesture, backgroundvideo, atvmode, nozhja, time_24};
        iArr[drvingsafe] = new int[]{videogesture, barbacklight, carrecloc, instruction, musicautoplay, drvingsafe, barvolume, backgroundvideo, videogesture};
        iArr[gpssmonitor] = new int[]{videogesture, screenclock, barbacklight, backviewvol, drvingsafe, backviewvol, barbacklight, screenclock, videogesture};
        iArr[gpsswitch] = new int[]{instruction, instruction, instruction, instruction, instruction, instruction, instruction, instruction, instruction};
        iArr[gpsmix] = new int[]{gpsmix, backviewvol, atvmode, barvolume, backgroundvideo, barvolume, atvmode, backviewvol, gpsmix};
        music_stytle_data9 = iArr;
    }
}