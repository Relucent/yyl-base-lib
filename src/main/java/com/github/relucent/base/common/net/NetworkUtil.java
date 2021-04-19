package com.github.relucent.base.common.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ServerSocketFactory;

import com.github.relucent.base.common.logging.Logger;

/**
 * 网络信息工具类
 */
public class NetworkUtil {

    private static final Logger LOGGER = Logger.getLogger(NetworkUtil.class);

    /** 回送地址(本机地址 ) */
    public static final String LOCALHOST_IPV4 = "127.0.0.1";

    /** 通配地址(所有主机) */
    public static final String ANYHOST_IPV4 = "0.0.0.0";

    /** 限制广播地址(所有主机) */
    public static final String BROADCAST_IPV4 = "255.255.255.255";

    /** 最小端口号 */
    public static final int MIN_PORT = 0;
    /** 最大端口号 */
    public static final int MAX_PORT = 65535;

    /** IPv4 正则 */
    private static final Pattern IPV4_PATTERN = Pattern.compile(//
            "^(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}$"//
    );

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected NetworkUtil() {
    }

    /**
     * 获得本机网卡物理地址
     * @return 网卡物理地址(数组)
     */
    public static final String[] getMacAddress() {
        Set<String> macSet = new LinkedHashSet<String>();
        try {
            for (Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces(); el.hasMoreElements();) {
                NetworkInterface networkInterface = el.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac == null || mac.length == 0) {
                    continue;
                }
                macSet.add(toMacString(mac));
            }
        } catch (SocketException e) {
            LOGGER.error("?", e);
        }
        return macSet.toArray(new String[macSet.size()]);
    }

    /**
     * 获得本机IP(v4)地址
     * @return 本机IP(v4)地址
     */
    public static final String getHostAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements();) {
                    String ip = addrs.nextElement().getHostAddress();
                    if (isValidIPv4(ip)) {
                        return ip;
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            LOGGER.error("?", e);
            return "";
        }
    }

    /**
     * 获得本机 MAC地址
     * @return 本机MAC地址
     */
    public static final byte[] getHardwareAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements();) {
                    String ip = addrs.nextElement().getHostAddress();
                    if (isValidIPv4(ip)) {
                        return networkInterface.getHardwareAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error("?", e);
        }
        return new byte[0];
    }

    /**
     * 地址转换为字符串
     * @param mac 地址
     * @return MAC地址字符串
     */
    public static String toMacString(byte[] mac) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = mac.length; i < length; i++) {
            if (i != 0) {
                builder.append("-");
            }
            String hex = Integer.toHexString(mac[i]);
            switch (hex.length()) {
            case 0:
                builder.append("00");// 00
                break;
            case 1:
                builder.append("0");// 0+
                break;
            default:
                builder.append(hex.substring(Math.max(hex.length() - 2, 0)));// ++
            }
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 检测本地端口可用性<br>
     * @param port 被检测的端口
     * @return 是否可用
     */
    public static boolean isPortAvailable(int port) {
        // 给定的IP未在指定端口范围中
        if (!isValidPort(port)) {
            return false;
        }
        // TCP
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
        } catch (Exception ex) {
            return false;
        }
        // UDP
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
            socket.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是有效IPv4地址
     * @param ip IP地址
     * @return 如果是有效IPv4地址返回true,否则返回false
     */
    public static boolean isValidIPv4(String ip) {
        return (ip != null //
                && !ANYHOST_IPV4.equals(ip) //
                && !LOCALHOST_IPV4.equals(ip) //
                && !BROADCAST_IPV4.equals(ip) //
                && IPV4_PATTERN.matcher(ip).matches());
    }

    /**
     * 判断是否是有效端口号(1～65535)
     * @param port 端口号
     * @return 如果是有效端口号返回true,否则返回false
     */
    public static boolean isValidPort(int port) {
        return MIN_PORT < port && port <= MAX_PORT;
    }
}
