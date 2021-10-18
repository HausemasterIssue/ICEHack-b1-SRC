package me.fluffycq.icehack.util;

public class ColorUtil {

    public static String getColor(String bColor) {
        String uColor = "";
        byte b0 = -1;

        switch (bColor.hashCode()) {
        case 1226:
            if (bColor.equals("&0")) {
                b0 = 0;
            }
            break;

        case 1227:
            if (bColor.equals("&1")) {
                b0 = 1;
            }
            break;

        case 1228:
            if (bColor.equals("&2")) {
                b0 = 2;
            }
            break;

        case 1229:
            if (bColor.equals("&3")) {
                b0 = 3;
            }
            break;

        case 1230:
            if (bColor.equals("&4")) {
                b0 = 4;
            }
            break;

        case 1231:
            if (bColor.equals("&5")) {
                b0 = 5;
            }
            break;

        case 1232:
            if (bColor.equals("&6")) {
                b0 = 6;
            }
            break;

        case 1233:
            if (bColor.equals("&7")) {
                b0 = 7;
            }
            break;

        case 1234:
            if (bColor.equals("&8")) {
                b0 = 8;
            }
            break;

        case 1235:
            if (bColor.equals("&9")) {
                b0 = 9;
            }

        case 1236:
        case 1237:
        case 1238:
        case 1239:
        case 1240:
        case 1241:
        case 1242:
        case 1243:
        case 1244:
        case 1245:
        case 1246:
        case 1247:
        case 1248:
        case 1249:
        case 1250:
        case 1251:
        case 1252:
        case 1253:
        case 1254:
        case 1255:
        case 1256:
        case 1257:
        case 1258:
        case 1259:
        case 1260:
        case 1261:
        case 1262:
        case 1263:
        case 1264:
        case 1265:
        case 1266:
        case 1267:
        case 1268:
        case 1269:
        case 1270:
        case 1271:
        case 1272:
        case 1273:
        case 1274:
        default:
            break;

        case 1275:
            if (bColor.equals("&a")) {
                b0 = 10;
            }
            break;

        case 1276:
            if (bColor.equals("&b")) {
                b0 = 11;
            }
            break;

        case 1277:
            if (bColor.equals("&c")) {
                b0 = 12;
            }
            break;

        case 1278:
            if (bColor.equals("&d")) {
                b0 = 13;
            }
            break;

        case 1279:
            if (bColor.equals("&e")) {
                b0 = 14;
            }
            break;

        case 1280:
            if (bColor.equals("&f")) {
                b0 = 15;
            }
        }

        switch (b0) {
        case 0:
            uColor = "§0";
            break;

        case 1:
            uColor = "§1";
            break;

        case 2:
            uColor = "§2";
            break;

        case 3:
            uColor = "§3";
            break;

        case 4:
            uColor = "§4";
            break;

        case 5:
            uColor = "§5";
            break;

        case 6:
            uColor = "§6";
            break;

        case 7:
            uColor = "§7";
            break;

        case 8:
            uColor = "§8";
            break;

        case 9:
            uColor = "§9";
            break;

        case 10:
            uColor = "§a";
            break;

        case 11:
            uColor = "§b";
            break;

        case 12:
            uColor = "§c";
            break;

        case 13:
            uColor = "§d";
            break;

        case 14:
            uColor = "§e";
            break;

        case 15:
            uColor = "§f";
        }

        return uColor;
    }
}
