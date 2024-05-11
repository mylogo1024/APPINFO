package com.getappinfo.app;


import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/* loaded from: classes.dex */
public class DriverTypeUtils {
    private static final String CPU_ARCHITECTURE_KEY_64 = "ro.product.cpu.abilist64";
    public static final String CPU_ARCHITECTURE_TYPE_32 = "32";
    public static final String CPU_ARCHITECTURE_TYPE_64 = "64";
    private static final int EI_CLASS = 4;
    private static final int ELFCLASS32 = 1;
    private static final int ELFCLASS64 = 2;
    private static boolean LOGENABLE = false;
    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";
    private static final String SYSTEM_LIB_C_PATH = "/system/lib/libc.so";
    private static final String SYSTEM_LIB_C_PATH_64 = "/system/lib64/libc.so";

    /**
     * Check if system libc.so is 32 bit or 64 bit
     */
    public static boolean isLibc64() {
        File libcFile = new File(SYSTEM_LIB_C_PATH);
        if (libcFile != null && libcFile.exists()) {
            byte[] header = readELFHeadrIndentArray(libcFile);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                if (LOGENABLE) {
                    Log.d("#####isLibc64()", SYSTEM_LIB_C_PATH + " is 64bit");
                }
                return true;
            }
        }

        File libcFile64 = new File(SYSTEM_LIB_C_PATH_64);
        if (libcFile64 != null && libcFile64.exists()) {
            byte[] header = readELFHeadrIndentArray(libcFile64);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                if (LOGENABLE) {
                    Log.d("####isLibc64()", SYSTEM_LIB_C_PATH_64 + " is 64bit");
                }
                return true;
            }
        }

        return false;
    }

    /**
     * ELF文件头格式是固定的:文件开始是一个16字节的byte数组e_indent[16]
     * e_indent[4]的值可以判断ELF是32位还是64位
     */
    private static byte[] readELFHeadrIndentArray(File libFile) {
        if (libFile != null && libFile.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(libFile);
                if (inputStream != null) {
                    byte[] tempBuffer = new byte[16];
                    int count = inputStream.read(tempBuffer, 0, 16);
                    if (count == 16) {
                        return tempBuffer;
                    } else {
                        if (LOGENABLE) {
                            Log.e("readELFHeadrIndentArray", "Error: e_indent lenght should be 16, but actual is " + count);
                        }
                    }
                }
            } catch (Throwable t) {
                if (LOGENABLE) {
                    Log.e("readELFHeadrIndentArray", "Error:" + t.toString());
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /*
     * 该方法的作用是判断手机使用哪种架构的 、Android获取CPU架构
     * */
    public static String CPUABI = "";

    public static String getCPUABI() {
        if (CPUABI == null) {
            try {
                String os_cpuabi = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream())).readLine();
                System.out.println("os_cpuabi==" + os_cpuabi);
                if (os_cpuabi.contains("x86_64")) {
                    CPUABI = "x86_64";
                    return "x86_64";
                } else if (os_cpuabi.contains("x86")) {
                    CPUABI = "x86";
                    return "x86";
                } else if (os_cpuabi.contains("armeabi-v7a")) {
                    CPUABI = "armeabi-v7a";
                    return "armeabi-v7a";
                } else if (os_cpuabi.contains("arm64-v8a")) {
                    CPUABI = "arm64-v8a";
                    return "arm64-v8a";
                } else if (os_cpuabi.contains("armeabi")) {
                    CPUABI = "armeabi";
                    return "armeabi";
                } else if (os_cpuabi.contains("mips64")) {
                    CPUABI = "mips64";
                    return "mips64";
                } else if (os_cpuabi.contains("mips")) {
                    CPUABI = "mips";
                    return "mips";
                } else {
                    CPUABI = "" + os_cpuabi;
                    return "unknow:" + os_cpuabi;
                }

            } catch (Exception e) {
                CPUABI = "armeabi";
                return "armeabi";
            }
        }

        if (CPUABI == null || TextUtils.isEmpty(CPUABI)) {
            Boolean isLib64CPU = isLibc64();
            if (isLib64CPU) {
                CPUABI = "armeabi";
            } else {
                CPUABI = "x86";
            }
        }

        return CPUABI;
    }
}